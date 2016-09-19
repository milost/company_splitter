package de.hpi.companies.algo.features.specific;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.github.powerlibraries.io.In;
import com.google.common.collect.Multiset;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.FloatFeature;

public class TfIdf extends FloatFeature {
	
	private static final String GENERAL_FILE_NAME = "generalFrequencies.bin";
	private static final String FILE_NAME = "frequencies.bin";
	private Multiset<String> frequencies;
	private Multiset<String> generalFrequencies;
	private int maxFrequency;
	private int generalSum;

	public TfIdf() {
		try(ObjectInputStream in = In.resource(TfIdf.class, FILE_NAME).asObjects()) {
			frequencies = (Multiset<String>) in.readObject();
			maxFrequency = frequencies.entrySet().stream().mapToInt(e -> e.getCount()).max().getAsInt();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		try(ObjectInputStream in = In.resource(Frequency.class, GENERAL_FILE_NAME).asObjects()) {
			generalFrequencies = (Multiset<String>) in.readObject();
			generalSum = generalFrequencies.size()+generalFrequencies.elementSet().size();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void calculateFeatures(Token[] tokens) {
		for(Token t:tokens) {
			
			float tf=((float)frequencies.count(t.getRawForm().toLowerCase())+1f)/(maxFrequency+1);
			float idf=(float) Math.log(generalSum/((float)generalFrequencies.count(t.getRawForm().toLowerCase())+1f));
			
			t.setFeature(this, tf*idf);
		}
	}

	@Override
	public String transformToString(Float value) {
		String res= Integer.toString(1+(int)Math.log10(value));
		return res;
	}
}
