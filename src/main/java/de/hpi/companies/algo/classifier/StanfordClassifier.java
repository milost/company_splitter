package de.hpi.companies.algo.classifier;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.hpi.companies.algo.Token;
import edu.stanford.nlp.classify.Dataset;
import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.classify.LinearClassifierFactory;
import edu.stanford.nlp.ling.BasicDatum;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.stats.Counters;
import weka.core.pmml.jaxbbindings.Output;

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
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(classifier);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		classifier=(LinearClassifier<String, String>) in.readObject();
	}
}
