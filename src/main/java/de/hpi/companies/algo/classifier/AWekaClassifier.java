package de.hpi.companies.algo.classifier;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.github.powerlibraries.io.In;
import com.github.powerlibraries.io.Out;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.FloatFeature;
import de.hpi.companies.algo.features.IComplexFeature;
import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Instances;

public abstract class AWekaClassifier<CLASSIFIER extends AbstractClassifier, T> extends AClassifier<T> {

	protected ArrayList<Attribute>  attributes;
	protected CLASSIFIER classifier;
	protected Instances columnInformation;
	
	public AWekaClassifier(TagExtractor<T> tagExtractor) {
		super(tagExtractor);
	}
	
	@Override
	public void train(Collection<Token[]> names) throws IOException {
		generateAttributes();
	}

	private void generateAttributes() {
		attributes = new ArrayList<>();
		attributes.add(new Attribute("tag",new ArrayList<>(Arrays.stream(possibleTags()).map(tag -> tag.toString()).collect(Collectors.toList()))));
		getFeatureManager().getSimpleFeatures().stream()
			.map(f-> {
				if(f instanceof FloatFeature)
					return new Attribute(f.getName());
				else
					return new Attribute(f.getName(),new ArrayList<>(f.getPossibleSimplifiedValues()));
			})
			.forEach(attributes::add);
		for(IComplexFeature cf:getFeatureManager().getComplexFeatures()) {
			for(int i=0;i<cf.getFeatureSize();i++) {
				Attribute a=new Attribute(cf.getName()+"["+i+"]",new ArrayList<>(cf.getPossibleSimplifiedValues()));
				attributes.add(a);
			}
		}
		columnInformation = new Instances("index setter",attributes,0);
		columnInformation.setClass(attributes.get(0));
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(classifier);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		generateAttributes();
		classifier = (CLASSIFIER) in.readObject();
	}
}
