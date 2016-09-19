package de.hpi.companies.algo.features.specific;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.IComplexFeature;
import de.hpi.companies.algo.features.SimpleFeature;
import edu.stanford.nlp.sequences.Clique;

public class WordConnectionFeature implements IComplexFeature {



	@Override
	public String[] calculateFeatures(Token token) {
		int ws=2;
		String[] result=new String[getFeatureSize()];
		int pos=0;
		for(int i=-ws;i<0;i++) {
			int id=token.getTokenId()+i;
			if(id>=0)
				result[pos++]=countWordsLeft(Arrays.asList(token.getName()).subList(id, token.getTokenId()));
			else
				result[pos++]=SimpleFeature.NO_TOKEN;
		}
		result[pos++]="0";
		for(int i=1;i<=ws;i++) {
			int id=token.getTokenId()+i;
			if(id<token.getName().length)
				result[pos++]=countWordsRight(Arrays.asList(token.getName()).subList(token.getTokenId(),id));
			else
				result[pos++]=SimpleFeature.NO_TOKEN;
		}
		return result;
	}
	
	@Override
	public List<String> calculateFeatures(Token[] tokens, int position, Clique clique) {
		List<String> res=new ArrayList<>();
		for(int i=0;i<clique.size();i++) {
			int pos=position+clique.relativeIndex(i);
			if(pos<0 || pos>=tokens.length)
				res.add("NO_TOKEN");
			else if(pos==position)
				res.add("0");
			else if(clique.relativeIndex(i)<0)
				res.add(countWordsLeft(Arrays.asList(tokens).subList(pos, position)));
			else
				res.add(countWordsRight(Arrays.asList(tokens).subList(position,pos)));
		}
		return res;
	}

	private String countWordsRight(List<Token> list) {
		int w=0;
		for(Token t:list)
			if(!t.isCompoundAfter())
				w++;
		return Integer.toString(w);
	}

	private String countWordsLeft(List<Token> list) {
		int w=0;
		for(Token t:list)
			if(!t.isCompoundAfter())
				w++;
		return Integer.toString(-w);
	}

	@Override
	public int getFeatureSize() {
		return 5;
	}

	@Override
	public List<String> getPossibleSimplifiedValues() {
		List<String> l=new ArrayList<>();
		l.add(SimpleFeature.NO_TOKEN);
		l.add("0");
		for(int i=1;i<=getFeatureSize()/2;i++) {
			l.add(Integer.toString(i));
			l.add(Integer.toString(-i));
		}
		return l;
	}

	@Override
	public int getIntValue(String value) {
		if(StringUtils.isNumeric(value))
			return Integer.parseInt(value);
		else
			return 9999;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
