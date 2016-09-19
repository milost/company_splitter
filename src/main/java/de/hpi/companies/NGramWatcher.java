package de.hpi.companies;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import com.github.powerlibraries.io.In;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.gson.Gson;

import de.hpi.companies.algo.Tag;
import de.hpi.companies.algo.Token;

public class NGramWatcher {
	
	private static final int SIZE=3;
	
	public static void main(String[] args) throws IOException {
		EnumMap<Tag,Multiset<String>> counts=new EnumMap<>(Tag.class);
		for(Tag t:Tag.values())
			counts.put(t, HashMultiset.create());
		
		File unifiedDir = new File("../Data/unified/");
		
		final Gson gson = new Gson();
		for(File f:unifiedDir.listFiles()) {
			System.out.println("Reading Unified: "+f.getName());
			In.file(f).withUTF8().streamLines()
				.map(l -> gson.fromJson(l, Token[].class))
				.forEach(name -> {
					for(Token t:name) {
						if(t.getRawForm().length()>=SIZE) {
							for(int i=0;i<t.getRawForm().length()-SIZE;i++) {
								counts.get(t.getExpectedTag()).add(t.getRawForm().toLowerCase().substring(0, SIZE));
							}
						}
					}
				});
		}
		
		for(Tag t:Tag.values()) {
			List<Entry<String>> entries=new ArrayList<>(counts.get(t).entrySet());
			Collections.sort(entries, (a,b)->-Integer.compare(a.getCount(), b.getCount()));
			System.out.println(t);
			for(int i=0;i<50 && i<entries.size();i++)
				System.out.println("\t"+entries.get(i).getCount()+"\t"+entries.get(i).getElement());
		}
	}
}
