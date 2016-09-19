package de.hpi.companies.algo.features.specific;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.StringFeature;

public class ShortForm extends StringFeature {

	@Override
	public void calculateFeatures(Token[] tokens) {
		for(Token t:tokens) {
			if(Character.isAlphabetic(t.getRawForm().charAt(0))) {
				
				t.setFeature(this, Boolean.toString(isShortForm(t, tokens)));
			}
			else
				t.setFeature(this, Boolean.FALSE.toString());
		}
	}

	private boolean isShortForm(Token t, Token[] tokens) {
		String[] sfParts=t.getRawForm().split("(?<!^)(?=[A-Z])");//l.toArray(new String[l.size()]);
		
		//String[] sfParts=StringUtils.splitByCharacterTypeCamelCase(t.getRawForm());
		int sfId=0;
		for(int tokenId=0;tokenId<tokens.length;tokenId++) {
			if(tokenId!=t.getTokenId() && tokens[tokenId].getRawForm().toLowerCase().startsWith(sfParts[sfId].toLowerCase())) {
				sfId++;
				if(sfId==sfParts.length)
					return true;
			}
		}
		return false;
	}

}
