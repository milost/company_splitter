package de.hpi.companies.algo.features.specific;

import org.apache.commons.lang3.StringUtils;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.StringFeature;

public class IsSpecial extends StringFeature {

	@Override
	public void calculateFeatures(Token[] tokens) {
		for(Token t:tokens)
			t.setFeature(this, Boolean.toString(!StringUtils.isAlphanumeric(t.getRawForm())));
	}

}
