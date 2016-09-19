package de.hpi.companies.algo.features.specific;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.StringFeature;

public class FullWord extends StringFeature {

	@Override
	public void calculateFeatures(Token[] tokens) {
		for(Token t:tokens) {
			t.setFeature(this, recreate(t));
		}
	}

	private String recreate(Token t) {
		String result=t.getRawForm();
		if(t.getTokenId()>0) {
			int id=t.getTokenId();
			while(id>0 && t.getName()[id].isCompoundBefore()) {
				id--;
				result=t.getName()[id].getRawForm()+result;
			}
		}
		
		if(t.getTokenId()<t.getName().length-1) {
			int id=t.getTokenId();
			while(id<t.getName().length-1 && t.getName()[id].isCompoundAfter()) {
				id++;
				result+=t.getName()[id].getRawForm();
			}
		}
		return result;
	}
	
}
