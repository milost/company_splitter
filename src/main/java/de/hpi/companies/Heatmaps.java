package de.hpi.companies;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.github.powerlibraries.io.In;
import com.github.powerlibraries.io.Out;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import de.hpi.companies.algo.Parser;
import de.hpi.companies.algo.Tag;
import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.Tokenizer;
import de.hpi.companies.algo.classifier.AClassifier;
import de.hpi.companies.algo.classifier.StanfordCMMClassifier;
import de.hpi.companies.algo.features.FeatureManager;

public class Heatmaps {
	public static void main(String[] args) throws JsonSyntaxException, IOException {
		List<Token[]> names = new ArrayList<>();
		File unifiedDir = new File("../Data/unified/");
		
		final Gson gson = new Gson();
		for(File f:unifiedDir.listFiles()) {
			System.out.println("Reading Unified: "+f.getName());
			In.file(f).withUTF8().streamLines()
				.map(l -> gson.fromJson(l, Token[].class))
				.peek(tokens -> {for(Token t:tokens) t.setName(tokens);})
				.peek(FeatureManager.ALL::calculateFeatures)
				.peek(FeatureManager.ALL::createSimplifications)
				.forEach(names::add);
		}
		FeatureManager.ALL.finishSimplifications();
		
		Collections.shuffle(names, new Random(777));
		
		AClassifier<Tag> classifier = new StanfordCMMClassifier<Tag>(Tag.EXTRACTOR);
		
		classifier.train(names);
		
		int counter = 0;
		Table<String, String, Integer> map = HashBasedTable.create();
		try(Parser p = new Parser("../data/companies.json")) {
			while(p.hasNext()) {
				String v = p.next();
				Token[] name = Tokenizer.tokenize(v);
				for(Token t:name)
					t.setName(name);
				FeatureManager.ALL.calculateFeatures(name);
				List<Tag> tags = classifier.getTags(name);			
				List<String> sectors = find(Tag.SECTOR, tags, name);
				List<String> locations = find(Tag.LOCATION, tags, name);
				
				for(String s:sectors) {
					for(String l:locations) {
						Integer c = map.get(s, l);
						if(c==null)
							c=0;
						c++;
						map.put(s, l, c);
					}
				}
				
				
				counter++;
				if(counter % 1000 == 0) {
					System.out.println(counter);
					Out.file("heatmap/"+counter+".bin").writeObject(map);
				}
			}
		}
		Out.file("heatmap/all.bin").writeObject(map);
	}

	private static List<String> find(Tag target, List<Tag> tags, Token[] name) {
		Set<String> res = new HashSet<>();
		for(int i=0;i<tags.size();i++) {
			if(tags.get(i)==target) {
				int last = i+1;
				for(;last<tags.size() && tags.get(last)==target;last++);
				for(int l=i+1;l<=last;l++)
					res.add(Token.reconstruct(name,i,l));
			}
		}
		return new ArrayList<>(res);
	}

}
