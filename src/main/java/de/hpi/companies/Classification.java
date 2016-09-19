package de.hpi.companies;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.powerlibraries.io.In;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.gson.Gson;

import de.hpi.companies.algo.STag;
import de.hpi.companies.algo.Tag;
import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.classifier.AClassifier;
import de.hpi.companies.algo.classifier.Classifiers;
import de.hpi.companies.algo.classifier.StanfordCMMClassifier;
import de.hpi.companies.algo.features.FeatureManager;
import de.hpi.companies.util.counting.KCounter;

public class Classification {
	
	private static List<String> errors=new ArrayList<>();
	
	public static void main(String[] args) throws IOException {
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
		
		File unifiedDir2 = new File("../Data/unified_eval/");
		List<String> in2 = new ArrayList<>();
		for(File f:unifiedDir2.listFiles()) {
			System.out.println("Reading Unified: "+f.getName());
			in2.addAll(In.file(f).withUTF8().readLines());
		}
		
		Collections.shuffle(names, new Random(777));
		
		Supplier<AClassifier<Tag>> tagClassifierSupplier= () -> new StanfordCMMClassifier<Tag>(Classifiers.getBestConfig(StanfordCMMClassifier.class, Tag.OTHER));//new StackingClassifier<Tag>(Tag.EXTRACTOR, new RandomForestClassifier<Tag>(Tag.EXTRACTOR), new StanfordCMMClassifier<Tag>(Tag.EXTRACTOR));///*CRFClassifier<Tag>(Tag.EXTRACTOR);*//*new StanfordClassifier(); OpenNLPClassifier(Algorithm.PERCEPTRON)*/RandomForestClassifier<Tag>(Tag.EXTRACTOR);
		Supplier<AClassifier<STag>> sTagClassifierSupplier= () -> new StanfordCMMClassifier<STag>(Classifiers.getBestConfig(StanfordCMMClassifier.class, STag.COLLOQUIAL_NAME));///*CRFClassifier<STag>(STag.EXTRACTOR);*//*new StanfordClassifier(); OpenNLPClassifier(Algorithm.PERCEPTRON)*/RandomForestClassifier<STag>(STag.EXTRACTOR);
		
		
		long time = -System.nanoTime();
		List<Token[]> names2 = new ArrayList<>();
		in2.stream()
			.map(l -> gson.fromJson(l, Token[].class))
			.peek(tokens -> {for(Token t:tokens) t.setName(tokens);})
			.peek(FeatureManager.ALL::calculateFeatures)
			.forEach(names2::add);
		
		
		KCounter counter = new KCounter();
		
		int blockSize=names.size()/5;
		//System.out.println("Blocksize: 5*"+blockSize);
		//train classifier on all blocks but valBlock
		AClassifier<Tag> tagClassifier = tagClassifierSupplier.get();
		AClassifier<STag> sTagClassifier = sTagClassifierSupplier.get();
		tagClassifier.train(names);
		sTagClassifier.train(names);

		//validate on valblock
		for(Token[] name : names2) {
			List<STag> cleanSTags = sTagClassifier.getTags(name);
			List<Tag> tags = tagClassifier.getTags(name);
			counter.count("clean STag", Arrays.stream(name).map(t->t.getExpectedSTag()).collect(Collectors.toList()), cleanSTags);
			
			if(counter.count("Tag", Arrays.stream(name).map(t->t.getExpectedTag()).collect(Collectors.toList()), tags))
				storeError(name,tags);
			else {
				System.out.println(Arrays.toString(name));
				System.out.println(tags);
				System.out.println(cleanSTags);
				System.out.println();
			}
			
			for(int t=0;t<tags.size();t++)
				name[t].setExpectedTag(tags.get(t));
			List<STag> dirtySTags = sTagClassifier.getTags(name);
			if(counter.count("dirty STag", Arrays.stream(name).map(t->t.getExpectedSTag()).collect(Collectors.toList()), dirtySTags))
				storeSError(name,dirtySTags);
			else {
			}
		}
		
		counter.finishBlock();
		
		System.out.println(System.nanoTime()+time);
		
		System.out.println("\n\n\n\n\n\n\n");
		
		for(String e:errors) {
			System.out.println();
			System.out.println(e);
		}
		
		System.out.println("\n\n\n\n\n\n\n");
		
		counter.print(System.out);
		
	}

	private static void storeError(Token[] name, List<Tag> tags) {
		StringBuilder sb=new StringBuilder();
		sb.append("Error "+Joiner.on(", ").join(IntStream.range(0, name.length).filter(i -> name[i].getExpectedTag()!=tags.get(i)).mapToObj(i->name[i].getExpectedTag()).distinct().toArray())+":\n");
		for(int i=0;i<name.length;i++) {
			sb.append("\t"+name[i].getExpectedTag()+"\t"+tags.get(i)+"\t"+name[i].getRawForm()+"\t{"+Joiner.on(", ").join(name[i].getFeatureSet().stream().map(e -> e.getKey().getName()+"="+e.getValue()).toArray())+"}\n");
		}
		errors.add(sb.toString());
	}
	
	private static void storeSError(Token[] name, List<STag> tags) {
		StringBuilder sb=new StringBuilder();
		sb.append("Error "+Joiner.on(", ").join(IntStream.range(0, name.length).filter(i -> name[i].getExpectedSTag()!=tags.get(i)).mapToObj(i->name[i].getExpectedSTag()).distinct().toArray())+":\n");
		for(int i=0;i<name.length;i++) {
			sb.append("\t"+name[i].getExpectedSTag()+"\t"+tags.get(i)+"\t"+name[i].getRawForm()+"\t{"+Joiner.on(", ").join(name[i].getFeatureSet().stream().map(e -> e.getKey().getName()+"="+e.getValue()).toArray())+"}\n");
		}
		errors.add(sb.toString());
	}
}
