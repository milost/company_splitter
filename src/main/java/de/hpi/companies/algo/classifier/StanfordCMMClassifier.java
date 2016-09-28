package de.hpi.companies.algo.classifier;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.output.NullOutputStream;

import com.github.powerlibraries.io.In;
import com.github.powerlibraries.io.Out;
import com.github.powerlibraries.io.helper.byteout.BAObjectOutputStream;

import de.hpi.companies.algo.Token;
import de.hpi.companies.util.UnclosableObjectOutputStreamWrapper;
import edu.stanford.nlp.ie.ner.CMMClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.Clique;
import edu.stanford.nlp.sequences.FeatureFactory;
import edu.stanford.nlp.sequences.SeqClassifierFlags;
import edu.stanford.nlp.util.PaddedList;
import weka.core.pmml.jaxbbindings.Output;

public class StanfordCMMClassifier<T> extends AClassifier<T> {

	private CMMClassifier<Label> classifier;

	/** only for serialization **/
	public StanfordCMMClassifier() {this(null);};
	public StanfordCMMClassifier(TagExtractor<T> tagExtractor) {
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
		classifier=new CMMClassifier<>(flags);
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
	
	private static class FF<T> extends FeatureFactory<Label> {
		
		private StanfordCMMClassifier<T> classifier;

		public FF(StanfordCMMClassifier<T> classifier) {
			this.classifier=classifier;
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
			else {
				return Collections.emptyList();
				/*return Arrays.stream(FeatureManager.ALL.getComplexFeatureArray(info.stream().map(l->l.getToken()).toArray(Token[]::new),position, clique))
						.map(cf->cf.getName()+"|"+cf.getSimplified())
						.collect(Collectors.toList());*/
			}
		}
		
	}

	@Override
	public <NT> AClassifier<NT> createClassifier(TagExtractor<NT> ex) {
		return new StanfordCMMClassifier<>(ex);
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		//to fix the serialization of Stanford
		classifier.serializeClassifier(new UnclosableObjectOutputStreamWrapper(out));
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		classifier = (CMMClassifier<Label>) CMMClassifier.getClassifier(in);
	}
}
