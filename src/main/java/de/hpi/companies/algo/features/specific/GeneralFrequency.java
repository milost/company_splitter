package de.hpi.companies.algo.features.specific;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.github.powerlibraries.io.In;
import com.github.powerlibraries.io.Out;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import de.hpi.companies.algo.Parser;
import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.Tokenizer;
import de.hpi.companies.algo.features.FloatFeature;

public class GeneralFrequency extends FloatFeature {
	
	private static final String FILE_NAME = "generalFrequencies.bin";
	private Multiset<String> frequencies;
	private int sum;

	public GeneralFrequency() {
		try(ObjectInputStream in = In.resource(GeneralFrequency.class, FILE_NAME).asObjects()) {
			frequencies = (Multiset<String>) in.readObject();
			sum = frequencies.size()+frequencies.elementSet().size();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void calculateFeatures(Token[] tokens) {
		for(Token t:tokens)
			t.setFeature(this, ((float)frequencies.count(t.getRawForm().toLowerCase())+1f)/sum);
	}

	@Override
	public String transformToString(Float value) {
		String res= Integer.toString(1+(int)Math.log10(value));
		return res;
	}
	
	public static void main(String[] args) throws IOException {
		Tokenizer tokenizer=new Tokenizer();
		
		Multiset<String> counter = HashMultiset.create();
		
		try(Parser p = new Parser("../data/other/articles.json","content")) {
			while(p.hasNext()) {
				String v = p.next();
				for(Token t:tokenizer.tokenize(v))
					counter.add(t.getRawForm().toLowerCase());
			}
		}
		try(ObjectOutputStream out = Out.file("src/main/resources/de/hpi/companies/algo/features/specific/"+FILE_NAME).asObjects()) {
			out.writeObject(counter);
		}
	}
}
