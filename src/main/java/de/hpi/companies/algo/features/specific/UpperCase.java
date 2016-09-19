package de.hpi.companies.algo.features.specific;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.StringFeature;

public class UpperCase extends StringFeature {

	@Override
	public void calculateFeatures(Token[] tokens) {
		for(Token t:tokens) {
			int chars=t.getRawForm().length();
			int upper=(int) Arrays.stream(ArrayUtils.toObject(t.getRawForm().toCharArray())).filter(c -> Character.isUpperCase(c)).count();
			
			String result;
			if(upper==0)
				result="NONE";
			else if(upper==chars)
				result="ALL";
			else if(upper==1)
				result="1";
			else
				result="SOME";
			t.setFeature(this, result);
		}
	}

}
