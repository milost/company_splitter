package de.hpi.companies.algo.classifier;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.hpi.companies.algo.TagType;
import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.classifier.TagExtractor.SingularSTag;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.pmml.jaxbbindings.Output;

public class CombinedClassifier<T extends Enum<T>&TagType<T>> extends AClassifier<T> {

	private ArrayList<AClassifier<T>> children;
	private RandomForest tagClassifier;
	private ArrayList<Attribute> attributes;
	private Instances tagData;
	
	/** only for serialization **/
	public CombinedClassifier() {this(null);};
	public CombinedClassifier(TagExtractor<T> tagExtractor) {
		super(tagExtractor);
		if(tagExtractor!=null) {
			children = new ArrayList<>(Arrays.asList(
					new BoostingClassifier<>(extr(BoostingClassifier.class, tagExtractor)),
					new OpenNLPClassifier<>(extr(OpenNLPClassifier.class, tagExtractor)),
					new RandomForestClassifier<>(extr(RandomForestClassifier.class, tagExtractor)),
					new StanfordClassifier<>(extr(StanfordClassifier.class, tagExtractor)),
					new StanfordCRFClassifier<>(extr(StanfordCRFClassifier.class, tagExtractor)),
					new StanfordCMMClassifier<>(extr(StanfordCMMClassifier.class, tagExtractor))
			));
		}
	}

	private TagExtractor<T> extr(Class<? extends AClassifier> classifier, TagExtractor<T> tagExtractor) {
		return Classifiers.getBestConfig(classifier, (tagExtractor instanceof SingularSTag)?(T)((SingularSTag)tagExtractor).getValue():tagExtractor.possibleValues()[0].getUnknown());
	}

	@Override
	public void train(Collection<Token[]> nameCol) throws IOException {
		List<Token[]> names = new ArrayList<>(nameCol);
		for(AClassifier<T> c:children)
			c.train(names.subList(0, (int)(0.7*names.size())));
		
		
		tagClassifier= new RandomForest();
		attributes = new ArrayList<>();
		attributes.add(new Attribute("tag",new ArrayList<>(Arrays.stream(possibleTags()).map(tag -> tag.toString()).collect(Collectors.toList()))));
		attributes.addAll(
				children.stream()
				.flatMap(c-> {
					List<Attribute> res = new ArrayList<>();
					res.add(new Attribute(c.getName(),new ArrayList<>(Arrays.stream(possibleTags()).map(tag -> tag.toString()).collect(Collectors.toList()))));
					if(c instanceof ProbabilityReturner)
						res.add(new Attribute(c.getName()+"-Prob"));
					return res.stream();
				})
				.collect(Collectors.toList()));
		tagData = new Instances(
				"companyTags", 
				attributes,
				0);
		tagData.setClass(attributes.get(0));
		tagData.addAll(names.subList((int)(0.7*names.size()),names.size()).stream()
				.flatMap(name -> createInstances(name).stream())
				.collect(Collectors.toList())
		);
		
		try {
			tagClassifier.buildClassifier(tagData);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private List<Instance> createInstances(Token[] name) {
		List<Instance> res = new ArrayList<>();
		
		Map<AClassifier<T>,List<T>> childResults = new HashMap<>();
		Map<AClassifier<T>,double[]> probs = new HashMap<>();
		
		for(AClassifier<T> c:children) {
			childResults.put(c, c.getTags(name));
			if(c instanceof ProbabilityReturner)
				probs.put(c, ((ProbabilityReturner) c).getLastProbabilities());
		}
		
		for(Token t:name) {
			
			Instance i = new DenseInstance(attributes.size());
			int pos=0;
			try {
				i.setValue(attributes.get(pos++), getTag(t).toString());
			} catch(IllegalArgumentException e) {
				throw new IllegalStateException("Illegal value "+getTag(t).toString()+" in "+Arrays.toString(t.getName()));
			}
			
			for(AClassifier<T> c:children) {
				i.setValue(attributes.get(pos++), childResults.get(c).get(t.getTokenId()).toString());
				if(c instanceof ProbabilityReturner)
					i.setValue(attributes.get(pos++), probs.get(c)[t.getTokenId()]);
			}
			res.add(i);
		}
		return res;
	}

	@Override
	public List<T> getTags(Token[] tokens) {
		try {
			List<T> tags=new ArrayList<>(tokens.length);
			List<Instance> instances = createInstances(tokens);
			for(Instance i:instances) {
				i.setDataset(tagData);
				tags.add(this.valueOf(attributes.get(0).value((int) tagClassifier.classifyInstance(i))));
			}
			return tags;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <NT> AClassifier<NT> createClassifier(TagExtractor<NT> ex) {
		return new CombinedClassifier(ex);
	}
	
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(children);
		out.writeObject(tagClassifier);
		out.writeObject(attributes);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		children=(ArrayList<AClassifier<T>>) in.readObject();
		tagClassifier=(RandomForest) in.readObject();
		attributes=(ArrayList<Attribute>) in.readObject();
	}
}
