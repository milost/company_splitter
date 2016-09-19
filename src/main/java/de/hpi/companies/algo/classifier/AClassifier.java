package de.hpi.companies.algo.classifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.FeatureManager;
import weka.core.Instances;

public abstract class AClassifier<T> implements KryoSerializable {
	
	private TagExtractor<T> tagExtractor;
	
	public AClassifier(TagExtractor<T> tagExtractor) {
		this.tagExtractor = tagExtractor;
	}
	
	public void train(Iterable<Token[]> names) throws IOException {
		
	}
	
	public abstract void train(Collection<Token[]> names) throws IOException;
	public abstract List<T> getTags(Token[] tokens);
	
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

	
	@Override
	public void write(Kryo kryo, Output output) {
		kryo.writeClassAndObject(output, tagExtractor);
	}

	@Override
	public void read(Kryo kryo, Input input) {
		tagExtractor=(TagExtractor<T>) kryo.readClassAndObject(input);
	}
	
	
	public void serialize(OutputStream out) {
		Kryo kryo = new Kryo();
		try (Output output = new Output(out)) {
			kryo.writeClassAndObject(output, this);
		}
	}
	
	public static <T extends AClassifier<?>> T deserialize(InputStream in) {
		Kryo kryo = new Kryo();
		try(Input input = new Input(in)) {
			return (T)kryo.readClassAndObject(input);
		}
	}
}
