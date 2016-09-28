package de.hpi.companies.algo.classifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.Pair;

import com.github.powerlibraries.io.In;
import com.github.powerlibraries.io.Out;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.Tokenizer;
import de.hpi.companies.algo.features.FeatureManager;

public abstract class AClassifier<T> implements Serializable {
	
	private TagExtractor<T> tagExtractor;
	
	public AClassifier(TagExtractor<T> tagExtractor) {
		this.tagExtractor = tagExtractor;
	}
	
	public abstract void train(Collection<Token[]> names) throws IOException;
	public abstract List<T> getTags(Token[] name);
	public List<Pair<Token,T>> getTags(String name) {
		Token[] tokens = Tokenizer.tokenize(name);
		for(Token t:tokens)
			t.setName(tokens);
		FeatureManager.ALL.calculateFeatures(tokens);
		List<T> tags = getTags(tokens);
		return IntStream
				.range(0, tokens.length)
				.mapToObj(index -> Pair.of(tokens[index], tags.get(index)))
				.collect(Collectors.toList());
	}
	
	public T getTag(Token t) {
		return tagExtractor.getTag(t);
	}
	
	public T valueOf(String name) {
		return tagExtractor.valueOf(name);
	}
	
	public T[] possibleTags() {
		return tagExtractor.possibleValues();
	}
	
	public FeatureManager getFeatureManager() {
		return tagExtractor.getFeatureManager();
	}
	
	public String getName() {
		return this.getClass().getSimpleName();
	}
	
	public abstract <NT> AClassifier<NT> createClassifier(TagExtractor<NT> ex);

	public void serialize(OutputStream out) throws IOException {
		Out.stream(out).writeObject(this);
	}
	
	public static <T extends AClassifier<?>> T deserialize(InputStream in) throws ClassNotFoundException, IOException {
		return In.stream(in).readObject();
	}
}
