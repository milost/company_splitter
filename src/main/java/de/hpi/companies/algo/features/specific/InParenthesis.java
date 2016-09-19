package de.hpi.companies.algo.features.specific;

import java.util.HashSet;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.StringFeature;

public class InParenthesis extends StringFeature {

	private static final Map<String,String> PARANTHESIS = ImmutableMap.<String,String>builder()
			.put("(",")")
			.put("{","}")
			.put("'","'")
			.put("\"","\"")
			.put("[","]")
			.put(">>","<<")
			.put(">","<")
			.build();
	
	@Override
	public void calculateFeatures(Token[] tokens) {
		
		HashSet<String> expectedClosing = new HashSet<>();
		
		
		for(Token t:tokens) {
			String feature=null;
			
			if(!expectedClosing.remove(t.getRawForm())) {
				if(PARANTHESIS.containsKey(t.getRawForm()))
					expectedClosing.add(PARANTHESIS.get(t.getRawForm()));
				else if(!expectedClosing.isEmpty())
					feature = Joiner.on("").join(expectedClosing);
			}
			
			t.setFeature(this, feature!=null?feature:"NONE");
		}
	}

}
