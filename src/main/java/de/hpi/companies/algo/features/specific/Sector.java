package de.hpi.companies.algo.features.specific;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.github.powerlibraries.io.Out;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.JsonSyntaxException;

import de.hpi.companies.algo.Tokenizer;
import de.hpi.companies.algo.features.ListMatch;
import de.hpi.companies.util.DBPediaExtractor;

public class Sector extends ListMatch {
	
	public Sector() {
		super("sectorWords.txt");
	}
		
	public static void main(String[] args) throws JsonSyntaxException, IOException, InterruptedException {
		
		Multiset<String> general=HashMultiset.create();
		HashMap<String, Multiset<String>> sectors = new HashMap<>();
		
		new DBPediaExtractor("SELECT DISTINCT ?industry, COUNT(?p), STR(?text) WHERE { ?p dbo:industry ?industry. ?industry dbo:abstract ?text. FILTER ( lang(?text) = \"de\" ) }",Integer.MAX_VALUE) {
			
			@Override
			protected void processValue(String[] cols) {
				if(Integer.parseInt(cols[1])>2) {
					String text = cleanString(cols[2]);
					sectors
						.computeIfAbsent(cols[0], k->HashMultiset.create())
						.addAll(Arrays.stream(Tokenizer.tokenize(text)).map(t -> t.getRawForm().toLowerCase()).distinct().collect(Collectors.toList()));
				}
			}
		}.startQuery();
		
		sectors.values().stream().map(s -> s.elementSet()).forEach(general::addAll);
		
		HashSet<String> results = new HashSet<>();
		double maxSector = general.entrySet().stream().mapToInt(e->e.getCount()).max().getAsInt();
		for(Entry<String, Multiset<String>> sector:sectors.entrySet()) {
			System.out.println(sector.getKey()+" -> "+sector.getValue().size());
			if(sector.getValue().size()>=10) {
				int maxFrequency=sector.getValue().entrySet().stream().mapToInt(e -> e.getCount()).max().getAsInt();
				for(com.google.common.collect.Multiset.Entry<String> e:sector.getValue().entrySet()) {
					double tf=0.5+0.5*(double)e.getCount()/maxFrequency;
					double idf=Math.log(maxSector/general.count(e.getElement()));
					if(tf*idf>6) {// && e.getCount()>1 && StringUtils.isAlpha(e.getElement())) {
						results.add(e.getElement());
						System.out.println("\t"+(tf*idf)+"\t"+e.getElement());
					}
				}
			}
			
		}
		
		ArrayList<String> l=new ArrayList<>(results);
		Collections.sort(l);
		Out
			.file("src/main/resources/de/hpi/companies/algo/features/specific/sectorWords.txt")
			.withUTF8()
			.writeLines(l);
	}
	
	
	private static String sector(String sector) {
		return StringUtils.split(sector, ':')[0].trim();
	}
}
