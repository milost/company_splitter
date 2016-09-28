package de.hpi.companies.algo.classifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.CalculatedFeature;
import de.hpi.companies.algo.features.FloatFeature;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesMultinomialText;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Stacking;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.DenseInstance;
import weka.core.Instance;

public class StackClassifier<T> extends AWekaClassifier<Stacking, T> {

	/** only for serialization **/
	public StackClassifier() {this(null);};
	public StackClassifier(TagExtractor<T> tagExtractor) {
		super(tagExtractor);
	}
	
	@Override
	public void train(Collection<Token[]> names) throws IOException {
		
		classifier= new Stacking();
		classifier.setClassifiers(new Classifier[] {
				new RandomForest(),
				new J48(),
				new AdaBoostM1(),
				new NaiveBayesMultinomialText()
		});
		
		columnInformation.addAll(names.stream()
				.flatMap(name -> Arrays.stream(name))
				.map(t -> {
					Instance i=createInstance(t);
					i.setValue(attributes.get(0), getTag(t).toString());
					return i;
				})
				.collect(Collectors.toList())
		);
		try {
			classifier.buildClassifier(columnInformation);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private Instance createInstance(Token t) {
		CalculatedFeature[] features = getFeatureManager().getFeatureArray(t);
		DenseInstance i = new DenseInstance(features.length+1);
		for(int id=0;id<features.length;id++) {
			try {
				if(features[id].getFeature() instanceof FloatFeature)
					i.setValue(attributes.get(id+1), (Float)features[id].getValue());
				else
					i.setValue(attributes.get(id+1), features[id].getSimplified());
			} catch(IllegalArgumentException e) {
				throw new IllegalArgumentException("Could not create Instance for "+t+" and value '"+features[id]+"' of attribute "+attributes.get(id+1).toString(),e);
			}
		}
		return i;
	}

	@Override
	public List<T> getTags(Token[] tokens) {
		try {
			List<T> tags=new ArrayList<>(tokens.length);
			for(Token t:tokens) {
				Instance instance = createInstance(t);
				instance.setDataset(columnInformation);
				tags.add(this.valueOf(attributes.get(0).value((int) classifier.classifyInstance(instance))));
			}
			return tags;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public <NT> AClassifier<NT> createClassifier(TagExtractor<NT> ex) {
		return new StackClassifier<>(ex);
	}
}
