package de.hpi.companies.algo.classifier;

import de.hpi.companies.algo.TagType;
import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.FeatureManager;

public interface TagExtractor<T> {
	public T getTag(Token t);
	
	public FeatureManager getFeatureManager();

	public T valueOf(String name);
	
	public T[] possibleValues();
	
	
	public static abstract class Simple<T extends Enum<T> & TagType<T>> implements TagExtractor<T> {
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((fm == null) ? 0 : fm.hashCode());
			result = prime * result + ((tagClass == null) ? 0 : tagClass.hashCode());
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
			Simple other = (Simple) obj;
			if (fm == null) {
				if (other.fm != null)
					return false;
			} else if (!fm.equals(other.fm))
				return false;
			if (tagClass == null) {
				if (other.tagClass != null)
					return false;
			} else if (!tagClass.equals(other.tagClass))
				return false;
			return true;
		}

		private FeatureManager fm;
		private Class<T> tagClass; 
		
		/** only for serialization **/
		public Simple() {this(null,null);}
		public Simple(Class<T> tagClass, FeatureManager fm) {
			this.fm = fm;
			this.tagClass=tagClass;
		}		
		
		@Override
		public FeatureManager getFeatureManager() {
			return fm;
		}

		@Override
		public T valueOf(String name) {
			try {
				return Enum.valueOf(tagClass, name);
			} catch(Exception e) {
				return possibleValues()[0].getUnknown();
			}
		}

		@Override
		public T[] possibleValues() {
			return tagClass.getEnumConstants();
		}
	}
	
	public static class STag extends Simple<de.hpi.companies.algo.STag> {
		/** only for serialization **/
		public STag() {this(null);}
		public STag(FeatureManager fm) {
			super(de.hpi.companies.algo.STag.class, fm);
		}

		@Override
		public de.hpi.companies.algo.STag getTag(Token t) {
			return t.getExpectedSTag();
		}
	}
	
	public static class SingularSTag extends STag {
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((value == null) ? 0 : value.hashCode());
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
			SingularSTag other = (SingularSTag) obj;
			if (value != other.value)
				return false;
			return true;
		}

		private de.hpi.companies.algo.STag value;

		public de.hpi.companies.algo.STag getValue() {
			return value;
		}

		/** only for serialization **/
		public SingularSTag() {this(null, null);}
		public SingularSTag(FeatureManager fm, de.hpi.companies.algo.STag value) {
			super(fm);
			this.value=value;
		}

		@Override
		public de.hpi.companies.algo.STag valueOf(String name) {
			de.hpi.companies.algo.STag res = super.valueOf(name);
			if(res == value)
				return res;
			else
				return res.getUnknown();
		}

		@Override
		public de.hpi.companies.algo.STag[] possibleValues() {
			return new de.hpi.companies.algo.STag[]{value, value.getUnknown()};
		}
		
		@Override
		public de.hpi.companies.algo.STag getTag(Token t) {
			de.hpi.companies.algo.STag res = super.getTag(t);
			if(res == value)
				return res;
			else
				return res.getUnknown();
		}
	}
	
	public static class Tag extends Simple<de.hpi.companies.algo.Tag> {
		/** only for serialization **/
		public Tag() {this(null);}
		public Tag(FeatureManager fm) {
			super(de.hpi.companies.algo.Tag.class, fm);
		}

		@Override
		public de.hpi.companies.algo.Tag getTag(Token t) {
			return t.getExpectedTag();
		}
	}
}
