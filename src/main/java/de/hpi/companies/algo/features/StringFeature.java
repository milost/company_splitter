package de.hpi.companies.algo.features;

public abstract class StringFeature extends SimpleFeature<String> {
	@Override
	public String transformToString(String object) {
		return object;
	}
}
