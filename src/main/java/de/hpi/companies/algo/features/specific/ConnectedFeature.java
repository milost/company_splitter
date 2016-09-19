package de.hpi.companies.algo.features.specific;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.StringFeature;

public class ConnectedFeature extends StringFeature {

	@Override
	public void calculateFeatures(Token[] tokens) {
		for(Token t:tokens)
			t.setFeature(this, Boolean.toString(!t.isCompoundBefore() || ! t.isCompoundAfter()));
	}

}
