package de.hpi.companies.algo.features.specific;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.tartarus.snowball.ext.German2Stemmer;

import com.github.powerlibraries.io.In;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.StringFeature;

public class CleanedSurface extends StringFeature {

	private transient Map<String, String> morph;
	private transient German2Stemmer stemmer=new German2Stemmer();

	public CleanedSurface() {
		try {
			morph=In.resource(CleanedSurface.class,"morphy.csv").withUTF8().streamLines()
				.filter(l-> !l.startsWith("#"))
				.filter(l -> !l.endsWith("\t?"))
				.collect(Collectors.toMap(
						l -> StringUtils.split(l,'\t')[0].trim(),
						l -> StringUtils.split(l,'\t')[1].trim(),
						(a,b) -> a
				));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void calculateFeatures(Token[] tokens) {
		for(Token t:tokens) {
			String raw=t.getRawForm();
			String m=morph.get(raw);
			if(m==null)
				m=raw;
			stemmer.setCurrent(m);
			stemmer.stem();
			String stemmed = stemmer.getCurrent();
			t.setFeature(this, stemmed.toLowerCase());
		}
	}

}
