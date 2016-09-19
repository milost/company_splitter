package de.hpi.companies.algo.features.specific;

import org.apache.commons.lang3.StringUtils;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.StringFeature;

public class NameShape extends StringFeature {

	@Override
	public void calculateFeatures(Token[] tokens) {
		for(Token t:tokens) {
			t.setFeature(this, getShape(tokens,t.getTokenId()-3)
								+connector(tokens,t.getTokenId()-3)
								+getShape(tokens,t.getTokenId()-2)
								+connector(tokens,t.getTokenId()-2)
								+getShape(tokens,t.getTokenId()-1)
								+connector(tokens,t.getTokenId()-1)
								+getShape(tokens,t.getTokenId()+0)
								+connector(tokens,t.getTokenId()+0)
								+getShape(tokens,t.getTokenId()+1)
								+connector(tokens,t.getTokenId()+1)
								+getShape(tokens,t.getTokenId()+2)
								+connector(tokens,t.getTokenId()+2)
								+getShape(tokens,t.getTokenId()+3)
			);
		}
	}

	private String connector(Token[] tokens, int id) {
		if(id<0 || id >=tokens.length)
			return "";
		else if(tokens[id].hasWhitespaceAfter())
			return " ";
		else if(tokens[id].isCompoundAfter())
			return "_";
		else
			return "";
	}

	private String getShape(Token[] tokens, int id) {
		if(id<0 || id >=tokens.length)
			return "N";
		
		String t=tokens[id].getRawForm();
		if(StringUtils.isNumeric(t))
			return "#";
		else if(StringUtils.isAlpha(t))
			return "w";
		else
			return t;
	}

}
