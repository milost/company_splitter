package de.hpi.companies.algo.features.specific;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.StringFeature;

public class Shape extends StringFeature {

	@Override
	public void calculateFeatures(Token[] tokens) {
		for(Token t:tokens) {
			t.setFeature(this, shape(t.getRawForm()));
		}
	}

	private static String shape(String word) {
		StringBuilder sb = new StringBuilder();
		char lastChar=' ';
		for(char c:word.toCharArray()) {
			char s=shape(c);
			if(s!=lastChar) {
				lastChar=s;
				sb.append(s);
			}
		}
		return sb.toString();
	}

	public static char shape(char c) {
		if(Character.isUpperCase(c))
			return 'X';
		else if(Character.isLowerCase(c))
			return 'x';
		else if(Character.isDigit(c))
			return '#';
		else
			return '-';	
	}

}
