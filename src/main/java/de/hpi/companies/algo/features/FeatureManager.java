package de.hpi.companies.algo.features;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.specific.AbsRevWordPosition;
import de.hpi.companies.algo.features.specific.AbsWordPosition;
import de.hpi.companies.algo.features.specific.CleanedSurface;
import de.hpi.companies.algo.features.specific.FamilyNameMatch;
import de.hpi.companies.algo.features.specific.FirstNameMatch;
import de.hpi.companies.algo.features.specific.Frequency;
import de.hpi.companies.algo.features.specific.FullWord;
import de.hpi.companies.algo.features.specific.GeneralFrequency;
import de.hpi.companies.algo.features.specific.InParenthesis;
import de.hpi.companies.algo.features.specific.IsNumber;
import de.hpi.companies.algo.features.specific.IsSpecial;
import de.hpi.companies.algo.features.specific.LegalRegex;
import de.hpi.companies.algo.features.specific.Length;
import de.hpi.companies.algo.features.specific.LocationMatch;
import de.hpi.companies.algo.features.specific.LongShape;
import de.hpi.companies.algo.features.specific.NameShape;
import de.hpi.companies.algo.features.specific.Prefix;
import de.hpi.companies.algo.features.specific.Sector;
import de.hpi.companies.algo.features.specific.SectorMatch;
import de.hpi.companies.algo.features.specific.SectorMatch2;
import de.hpi.companies.algo.features.specific.Shape;
import de.hpi.companies.algo.features.specific.ShortForm;
import de.hpi.companies.algo.features.specific.Suffix;
import de.hpi.companies.algo.features.specific.Surface;
import de.hpi.companies.algo.features.specific.UpperCase;
import de.hpi.companies.algo.features.specific.WordConnectionFeature;
import de.hpi.companies.algo.features.specific.WordCut;
import de.hpi.companies.algo.features.specific.WordPosition;
import de.hpi.companies.algo.features.specific.YellowPages;
import edu.stanford.nlp.sequences.Clique;
import weka.core.pmml.jaxbbindings.Output;

public class FeatureManager implements Serializable {
	
	public static final int DEFAULT_WINDOW_SIZE = 0;
	
	public static final FeatureManager ALL = new FeatureManager()
			.add(new Surface())						//doc
			.add(new CleanedSurface())				//doc
			.add(new FullWord())					//doc
			
			.add(new Prefix(1))
			.add(new Prefix(2))
			.add(new Prefix(3))
			.add(new Prefix(4))
			.add(new Prefix(5))
			.add(new Suffix(1))
			.add(new Suffix(2))
			.add(new Suffix(3))
			.add(new Suffix(4))
			.add(new Suffix(5))
			.add(new WordCut(1))
			.add(new WordCut(2))
			.add(new WordCut(3))
			.add(new WordCut(4))
			.add(new WordCut(5))
			
			.add(new WordPosition())
			.add(new AbsWordPosition())
			.add(new AbsRevWordPosition())
			
			.add(new Length())
			.add(new Shape())
			.add(new LongShape())
			.add(new IsNumber())
			.add(new IsSpecial())
			.add(new UpperCase())
			
			.add(new ShortForm())
			.add(new InParenthesis())
			.add(new NameShape())
			.add(new WordConnectionFeature())
			
			.add(new LegalRegex())
			.add(new FirstNameMatch())
			.add(new FamilyNameMatch())
			.add(new LocationMatch())
			.add(new SectorMatch())
			.add(new SectorMatch2())
			.add(new YellowPages())
			.add(new Sector())
			
			.add(new Frequency())
			.add(new GeneralFrequency())
			
			//.add(new ConnectedFeature())
			//.add(new StrictFamilyNameMatch())
			//.add(new W2VFeature())
			//.add(new YellowPages2())
			//.add(new TfIdf())
	;
	
	private List<SimpleFeature> simpleFeatures;
	private List<IComplexFeature> complexFeatures;
	private List<IFeature> features;
	
	public FeatureManager() {
		simpleFeatures = new ArrayList<>();
		complexFeatures = new ArrayList<>();
		features = new ArrayList<>();
	}
	public FeatureManager(Collection<IFeature> features) {
		this();
		for(IFeature f:features)
			this.add(f);
	}
	
	public FeatureManager add(SimpleFeature feature) {
		simpleFeatures.add(feature);
		features.add(feature);
		return this;
	}
	
	public FeatureManager add(IComplexFeature feature) {
		complexFeatures.add(feature);
		features.add(feature);
		return this;
	}
	
	public void calculateFeatures(Token[] tokens) {
		for(SimpleFeature f:simpleFeatures)
			f.calculateFeatures(tokens);
	}
	
	public void createSimplifications(Token[] tokens) {
		for(SimpleFeature f:simpleFeatures)
			f.createSimplifications(tokens);
	}

	private int getSimpleFeaturesSize() {
		return simpleFeatures.stream().mapToInt(cf -> cf.getFeatureSize()).sum();
	}
	
	private int getAllFeaturesSize() {
		return getSimpleFeaturesSize() + 
				complexFeatures.stream().mapToInt(cf -> cf.getFeatureSize()).sum();
	}
	
	public List<SimpleFeature> getSimpleFeatures() {
		return simpleFeatures;
	}

	public void finishSimplifications() {
		for(SimpleFeature f:simpleFeatures)
			f.finishSimplifications();
	}

	public CalculatedFeature[] getFeatureArray(Token t) {
		CalculatedFeature[] features=new CalculatedFeature[getAllFeaturesSize()];
		CalculatedFeature[] simpleFeatures = getSimpleFeatureArray(t);
		System.arraycopy(simpleFeatures, 0, features, 0, simpleFeatures.length);
		int id=simpleFeatures.length;
		for(IComplexFeature cf:complexFeatures) {
			String[] cFeatures=cf.calculateFeatures(t);
			for(int i=0;i<cFeatures.length;i++)
				features[id++]=new CalculatedFeature(cf,cf.getName()+"["+i+"]",cFeatures[i]);
		}
		return features;
	}
	
	public CalculatedFeature[] getComplexFeatureArray(Token t) {
		CalculatedFeature[] features=new CalculatedFeature[getAllFeaturesSize()];
		CalculatedFeature[] simpleFeatures = getSimpleFeatureArray(t);
		System.arraycopy(simpleFeatures, 0, features, 0, simpleFeatures.length);
		int id=simpleFeatures.length;
		for(IComplexFeature cf:complexFeatures) {
			String[] cFeatures=cf.calculateFeatures(t);
			for(int i=0;i<cFeatures.length;i++)
				features[id++]=new CalculatedFeature(cf,cf.getName()+"["+i+"]",cFeatures[i]);
		}
		return features;
	}
	public CalculatedFeature[] getComplexFeatureArray(Token[] tokens, int position, Clique clique) {
		List<CalculatedFeature> res = new ArrayList<>();
		for(IComplexFeature cf:complexFeatures) {
			List<String> cFeatures=cf.calculateFeatures(tokens, position, clique);
			for(int i=0;i<cFeatures.size();i++)
				res.add(new CalculatedFeature(cf,cf.getName()+"["+i+"]",cFeatures.get(i)));
		}
		return res.toArray(new CalculatedFeature[res.size()]);
	}
	
	public CalculatedFeature[] getSimpleFeatureArray(Token t) {
		CalculatedFeature[] features=new CalculatedFeature[getSimpleFeaturesSize()];
		int id=0;
		for(SimpleFeature feature:simpleFeatures) {
			for(int pos=-feature.getWindowSize();pos<=feature.getWindowSize();pos++) {
				String fName = feature.getName()+"["+pos+"]";
				int target=t.getTokenId()+pos;
				if(target<0 || target>=t.getName().length) {
					features[id++]=new CalculatedFeature(feature,fName);
				}
				else {
					features[id++]=new CalculatedFeature(feature,fName,t.getName()[target].getFeature(feature));
				}
			}
		}
		return features;
	}

	public List<IComplexFeature> getComplexFeatures() {
		return complexFeatures;
	}

	public <T extends IFeature> T getFeature(Class<T> featureClass) {
		return featureClass.cast(
				features.stream().filter(f -> f.getClass().equals(featureClass)).findFirst().get()
		);
	}
	
	public List<IFeature> getFeatures() {
		return features;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((complexFeatures == null) ? 0 : complexFeatures.hashCode());
		result = prime * result + ((features == null) ? 0 : features.hashCode());
		result = prime * result + ((simpleFeatures == null) ? 0 : simpleFeatures.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeatureManager other = (FeatureManager) obj;
		if (complexFeatures == null) {
			if (other.complexFeatures != null)
				return false;
		} else if (!complexFeatures.equals(other.complexFeatures))
			return false;
		if (features == null) {
			if (other.features != null)
				return false;
		} else if (!features.equals(other.features))
			return false;
		if (simpleFeatures == null) {
			if (other.simpleFeatures != null)
				return false;
		} else if (!simpleFeatures.equals(other.simpleFeatures))
			return false;
		return true;
	}
	public FeatureManager add(IFeature f) {
		if(f instanceof SimpleFeature)
			return this.add((SimpleFeature)f);
		if(f instanceof IComplexFeature)
			return this.add((IComplexFeature)f);
		else
			throw new IllegalArgumentException(f.getName());
	}
	
	@Override
	public String toString() {
		return features.toString();
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(features);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		simpleFeatures = new ArrayList<>();
		complexFeatures = new ArrayList<>();
		features = new ArrayList<>();
		List<IFeature> l = (List<IFeature>)in.readObject();
		for(IFeature f : l)
			add(f);
	}
}
