package de.hpi.companies.algo.features.specific;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.FloatFeature;

public class WordPosition extends FloatFeature {

	@Override
	public void calculateFeatures(Token[] tokens) {
		for(Token t:tokens)
			t.setFeature(this, (float)t.getTokenId()/t.getName().length);
	}

	@Override
	public String transformToString(Float value) {
		return Integer.toString((int)(value*10));
	}

}
