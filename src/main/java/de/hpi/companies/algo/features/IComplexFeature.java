package de.hpi.companies.algo.features;

import java.util.List;

import de.hpi.companies.algo.Token;
import edu.stanford.nlp.sequences.Clique;

public interface IComplexFeature extends IFeature {

	String[] calculateFeatures(Token token);

	int getIntValue(String value);

	List<String> calculateFeatures(Token[] tokens, int position, Clique clique);

	@Override
	default String getName() {
		return this.getClass().getSimpleName();
	}
}
