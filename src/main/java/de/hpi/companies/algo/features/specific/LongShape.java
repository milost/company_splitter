package de.hpi.companies.algo.features.specific;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.StringFeature;

public class LongShape extends StringFeature {

	@Override
	public void calculateFeatures(Token[] tokens) {
		for(Token t:tokens) {
			t.setFeature(this, shape(t.getRawForm()));
		}
	}

	private static String shape(String word) {
		StringBuilder sb = new StringBuilder();
		for(char c:word.toCharArray()) {
			char s=Shape.shape(c);
			sb.append(s);
		}
		return sb.toString();
	}
}
