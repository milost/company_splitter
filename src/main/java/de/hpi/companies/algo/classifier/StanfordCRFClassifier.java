package de.hpi.companies.algo.classifier;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.output.NullOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import de.hpi.companies.algo.Token;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.Clique;
import edu.stanford.nlp.sequences.FeatureFactory;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.PaddedList;

public class StanfordCRFClassifier<T> extends AClassifier<T> {

	private CRFClassifier<Label> classifier;

	/** only for serialization **/
	public StanfordCRFClassifier() {this(null);};
	public StanfordCRFClassifier(TagExtractor<T> tagExtractor) {
		super(tagExtractor);
	}
	
	@Override
	public void train(Collection<Token[]> names) {
		PrintStream old = System.err;
		System.setErr(new PrintStream(new NullOutputStream()));
		
		List<List<Label>> dataset = names.stream().map(n->toLabels(n, true)).collect(Collectors.toList());
		SeqClassifierFlags flags = new SeqClassifierFlags();
		flags.featureFactory=FF.class.getName();
		flags.featureFactoryArgs=new Object[]{this};
		flags.maxLeft=2;
		flags.maxRight=2;
		flags.maxQNItr = 25;
		classifier=new CRFClassifier<>(flags);
		classifier.train(dataset);
		
		System.setErr(old);
	}

	@Override
	public List<T> getTags(Token[] tokens) {
		return classifier
			.classify(toLabels(tokens, false))
			.stream()
			.map(l -> valueOf(l.getTag()))
			.collect(Collectors.toList());
	}
	
	public List<Label> toLabels(Token[] tokens, boolean includeAnswers) {
		return Arrays.stream(tokens).map(t-> new Label(t, includeAnswers?getTag(t).toString():null)).collect(Collectors.toList());
	}

	private static class Label extends CoreLabel {
		
		private Token t;
		
		public Label(Token t, String label) {
			this.t=t;
			this.set(CoreAnnotations.AnswerAnnotation.class, label);
			this.set(CoreAnnotations.IndexAnnotation.class, t.getTokenId()+1);
			this.set(CoreAnnotations.TextAnnotation.class, t.getRawForm());
			this.setValue(t.getRawForm());
		}
		
		public String getTag() {
			return this.get(CoreAnnotations.AnswerAnnotation.class);
		}

		public Token getToken() {
			return t;
		}
	}
	
	private static class FF<T extends Enum<T>> extends FeatureFactory<Label> {

		private StanfordCRFClassifier<T> classifier;
		
		public FF(StanfordCRFClassifier<T> classifier) {
			this.classifier = classifier;
		}

		@Override
		public Collection<String> getCliqueFeatures(PaddedList<Label> info, int position, Clique clique) {
			/*
			List<String> features = new ArrayList<>();
			for(int i=0;i<clique.size();i++) {
				int pos = position+clique.relativeIndex(i);
				if(pos < 0 || pos >= clique.size())
					features.addAll(Arrays.stream(FeatureManager.ALL.getFeatureArray(info.get(position).getToken()))
							.map(cf->"NO_TOKEN|"+cf.getName())
							.collect(Collectors.toList()));
				else
					features.addAll(Arrays.stream(FeatureManager.ALL.getFeatureArray(info.get(pos).getToken()))
						.map(cf->cf.getSimplified()+"|"+cf.getName())
						.collect(Collectors.toList()));
			}
			return features;
			*/
			if(clique.size()==1)
				return Arrays.stream(classifier.getFeatureManager().getFeatureArray(info.get(position).getToken()))
						.map(cf->cf.getName()+"|"+cf.getSimplified())
						.collect(Collectors.toList());
			else
				return Collections.emptyList();
		}
		
	}

	@Override
	public <NT> AClassifier<NT> createClassifier(TagExtractor<NT> ex) {
		return new StanfordCRFClassifier<NT>(ex);
	}
	
	@Override
	public void write(Kryo kryo, Output output) {
		super.write(kryo, output);
		kryo.writeClassAndObject(output, classifier);
	}

	@Override
	public void read(Kryo kryo, Input input) {
		super.read(kryo, input);
		classifier = (CRFClassifier<Label>) kryo.readClassAndObject(input);
	}
}
