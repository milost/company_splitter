package de.hpi.companies.algo.features;

import java.io.Serializable;
import java.util.Collection;

public interface IFeature extends Serializable {
	
	public int getFeatureSize();
	
	Collection<String> getPossibleSimplifiedValues();
	
	public default String getName() {
		return this.getClass().getSimpleName();
	}
	
}
