package de.hpi.companies;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Joiner;

import de.hpi.companies.algo.Parser;
import de.hpi.companies.algo.Tokenizer;

public class SetGenerator {
	
	public ArrayList<String> training;
	public ArrayList<String> testing;
	private Tokenizer tokenizer=new Tokenizer();

	public static void main(String[] args) throws IOException {
		new SetGenerator().export();
	}
	
	public SetGenerator() throws IOException {
		Random r = new Random(7);
		ArrayList<String> all=new ArrayList<>();
		try(Parser p = new Parser("../data/companies.json")) {
			while(p.hasNext())
				all.add(p.next());
		}
		Collections.shuffle(all,r);
		
		
		int size = all.size();
		all.removeIf(s -> s.length()>=100);
		System.out.println(size+" - "+(size-all.size())+" -> "+all.size());
		size=all.size();
		
		training=new ArrayList<>();
		testing=new ArrayList<>();
		
		//iterate all characters
		Character[] chars=all.stream().flatMap(w -> Arrays.stream(ArrayUtils.toObject(w.toCharArray()))).distinct().toArray(Character[]::new);
		for(Character c:chars) {
			String name=all.stream().filter(n -> n.contains(c.toString())).findFirst().get();
			all.remove(name);
			training.add(name);
		}
		
		System.out.println("After adding one of every special character there are "+chars.length+" training names");
		training.addAll(all.subList(0, 2000-chars.length));
		testing.addAll(all.subList(2000-chars.length, 2400-chars.length));
		
		System.out.println("Created set with "+training.size()+"/"+testing.size());
		
	}
	
	public void export() throws IOException {
		for(int i=0;i<training.size()/50;i++)
			write("training-"+i+".txt",training.subList(i*50, (i+1)*50), tokenizer);
		for(int i=0;i<testing.size()/50;i++)
			write("testing-"+i+".txt",testing.subList(i*50, (i+1)*50), tokenizer);
	}

	private static void write(String name, List<String> subList, Tokenizer tokenizer) throws IOException {
		try(BufferedWriter out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("../Data/sets/",name)),StandardCharsets.UTF_8))) {
			for(String w:subList) {
				out.write(Joiner.on(' ').join(Arrays.stream(tokenizer.tokenize(w)).map(t -> t.getRawForm()).toArray(String[]::new))+"\n");
			}
		}
	}
}
