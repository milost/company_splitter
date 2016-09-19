package de.hpi.companies.algo.features;

public class CalculatedFeature {
	private IFeature feature;
	private Object value;
	private String name;
	public CalculatedFeature(IFeature feature, String name, Object value) {
		this.feature = feature;
		this.value = value;
		this.name = name;
		if(value==null)
			throw new IllegalStateException(name+" with value null");
	}
	
	//meaning no token here
	public CalculatedFeature(IFeature feature, String name) {
		this.feature = feature;
		this.value = SimpleFeature.NO_TOKEN;
		this.name = name;
	}
	public IFeature getFeature() {
		return feature;
	}
	public Object getValue() {
		return value;
	}
	public String getName() {
		return name;
	}

	public String getSimplified() {
		if(feature instanceof SimpleFeature)
			return ((SimpleFeature) feature).getValueSimplified(value);
		else
			return (String)value;
	}

	public int getSimplifiedInt() {
		if(feature instanceof SimpleFeature)
			return ((SimpleFeature) feature).getValueSimplifiedInt(value);
		else if(feature instanceof IComplexFeature)
			return ((IComplexFeature)feature).getIntValue((String)value);
		else
			throw new IllegalStateException();
	}
	
	
}
