package de.hpi.companies.algo.features.specific;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.StringFeature;

public class Prefix extends StringFeature {
	private int length;
	
	/** only for serialization **/
	public Prefix() {this(-1);}
	public Prefix(int length) {
		this.length=length;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + length;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Prefix other = (Prefix) obj;
		if (length != other.length)
			return false;
		return true;
	}
	@Override
	public void calculateFeatures(Token[] tokens) {
		for(Token t:tokens) {
			t.setFeature(this, t.getRawForm().substring(0, Math.min(length, t.getRawForm().length())));
		}
	}
	
	@Override
	public String getName() {
		return super.getName()+"-"+length;
	}
}
