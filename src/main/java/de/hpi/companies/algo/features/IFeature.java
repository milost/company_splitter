package de.hpi.companies.algo.features;

import java.util.Collection;

public interface IFeature {
	
	public int getFeatureSize();
	
	Collection<String> getPossibleSimplifiedValues();
	
	public default String getName() {
		return this.getClass().getSimpleName();
	}
	
}
