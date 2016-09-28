package de.hpi.companies.algo.features;

import java.util.HashMap;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import de.hpi.companies.algo.Token;

public abstract class SimpleFeature<T> implements IFeature {
	
	public static final String RARE = "RARE";
	public static final String NO_TOKEN = "NO_TOKEN";
		
	
	private Multiset<String> simplificationCount = HashMultiset.create();
	private HashMap<String, Integer> simplifiedValues;
	private int simplifiedCounter=0;
	private int windowSize=FeatureManager.DEFAULT_WINDOW_SIZE;
	
	public abstract void calculateFeatures(Token[] tokens);

	public final String getValueAsString(Token t) {
		return transformToString(t.getFeature(this));
	}
	
	public abstract String transformToString(T object);
	
	public void createSimplifications(Token[] tokens) {
		for(Token t:tokens) {
			String v = getValueAsString(t);
			if(v==null)
				throw new IllegalStateException("Feature "+this.getName()+" returned String Value null for "+t);
			simplificationCount.add(v);
		}
	}
	
	public void finishSimplifications() {
		boolean small = simplificationCount.elementSet().size()<30;
		
		simplifiedValues = new HashMap<>();
		for(Entry<String> e:simplificationCount.entrySet())
			if(small || e.getCount()>1)
				simplifiedValues.put(e.getElement(), simplifiedCounter++);
		
		simplifiedValues.put(RARE,-1);
		simplifiedValues.put(NO_TOKEN,-2);
		simplificationCount=null;
	}
	
	public String getValueSimplified(T value) {
		String v = transformToString(value);
		if(simplifiedValues.containsKey(v))
			return v;
		else
			return RARE;
	}
	
	public int getValueSimplifiedInt(T value) {
		Integer res = simplifiedValues.get(value);
		if(res==null)
			return -1;
		else
			return res;
	}

	public Set<String> getPossibleSimplifiedValues() {
		return simplifiedValues.keySet();
	}

	public int getWindowSize() {
		return windowSize;
	}
	
	@Override
	public int getFeatureSize() {
		return windowSize*2+1;
	}
	
	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getClass().getName().hashCode();
		result = prime * result + windowSize;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleFeature other = (SimpleFeature) obj;
		if (windowSize != other.windowSize)
			return false;
		return true;
	}
	
	
}
