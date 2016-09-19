package de.hpi.companies.algo.classifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import de.hpi.companies.algo.Token;
import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.classify.LinearClassifierFactory;
import edu.stanford.nlp.ling.BasicDatum;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.stats.Counters;

public class StanfordClassifier<T> extends AClassifier<T> implements ProbabilityReturner {

	private LinearClassifier<String,String> classifier;
	private double[] lastProbs;

	/** only for serialization **/
	public StanfordClassifier() {this(null);};
	public StanfordClassifier(TagExtractor<T> tagExtractor) {
		super(tagExtractor);
	}
	
	@Override
	public void train(Collection<Token[]> names) {
		Dataset<String, String> dataset = new Dataset<>();
		for(Token[] n:names)
			for(Token t:n)
				if(t.getExpectedTag()!=null)
					dataset.add(getDatum(t), getTag(t).toString());
		classifier=new LinearClassifierFactory<String,String>().trainClassifier(dataset);
	}

	@Override
	public List<T> getTags(Token[] tokens) {
		lastProbs = new double[tokens.length];
		List<T> tags = new ArrayList<>(tokens.length);
		for(int i=0;i<tokens.length;i++) {
			Counter<String> scores = classifier.scoresOf(new BasicDatum<>(getDatum(tokens[i])));
			lastProbs[i]=Counters.max(scores);
			tags.add(valueOf(Counters.argmax(scores)));
		}
		return tags;
	}

	private List<String> getDatum(Token token) {
		return Arrays.stream(getFeatureManager().getFeatureArray(token))
						.map(cf->cf.getSimplified())
						.collect(Collectors.toList());
	}

	
	@Override
	public <NT> AClassifier<NT> createClassifier(TagExtractor<NT> ex) {
		return new StanfordClassifier<>(ex);
	}

	@Override
	public double[] getLastProbabilities() {
		return lastProbs;
	}
	
	@Override
	public void write(Kryo kryo, Output output) {
		super.write(kryo, output);
		kryo.writeClassAndObject(output, classifier);
	}

	@Override
	public void read(Kryo kryo, Input input) {
		super.read(kryo, input);
		classifier = (LinearClassifier<String, String>) kryo.readClassAndObject(input);
	}

}
