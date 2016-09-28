package de.hpi.companies.algo;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import de.hpi.companies.algo.features.SimpleFeature;

public class Token {
	private String rawForm;
	private boolean whitespaceAfter;
	private boolean whitespaceBefore;
	private boolean compoundBefore;
	private boolean compoundAfter;
	private int start;
	private int end;
	private Tag expectedTag;
	private STag expectedSTag = STag.NONE;
	private HashMap<SimpleFeature,Object> features;
	private transient Token[] name;
	private final int tokenId;
	
	public int getTokenId() {
		return tokenId;
	}

	public Token(String rawForm, int tokenId, int start, int end, boolean whitespaceBefore, boolean whitespaceAfter) {
		this.rawForm = rawForm;
		this.whitespaceAfter = whitespaceAfter;
		this.whitespaceBefore = whitespaceBefore;
		this.start=start;
		this.end=end;
		this.tokenId=tokenId;
	}
	
	public Token(String rawForm, int tokenId, int start, boolean whitespaceBefore, boolean whitespaceAfter) {
		this.rawForm = rawForm;
		this.whitespaceAfter = whitespaceAfter;
		this.whitespaceBefore = whitespaceBefore;
		this.start=start;
		this.end=start+rawForm.length();
		this.tokenId=tokenId;
	}

	@Override
	public String toString() {
		return "{"+rawForm+"|"+expectedTag+"}";
	}

	public String getRawForm() {
		return rawForm;
	}

	public boolean hasWhitespaceAfter() {
		return whitespaceAfter;
	}

	public boolean hasWhitespaceBefore() {
		return whitespaceBefore;
	}

	public void setExpectedTag(Tag tag) {
		this.expectedTag=tag;
	}

	public <T> void setFeature(SimpleFeature<T> feature, T value) {
		if(features==null)
			features=new HashMap<>();
		features.put(feature, value);
	}
	
	public Tag getExpectedTag() {
		return expectedTag;
	}

	public Token[] getName() {
		return name;
	}

	public void setName(Token[] name) {
		this.name = name;
	}

	public boolean isCompoundBefore() {
		return compoundBefore;
	}

	public void setCompoundBefore(boolean compoundBefore) {
		this.compoundBefore = compoundBefore;
	}

	public boolean isCompoundAfter() {
		return compoundAfter;
	}

	public void setCompoundAfter(boolean compoundAfter) {
		this.compoundAfter = compoundAfter;
	}

	public STag getExpectedSTag() {
		return expectedSTag;
	}

	public void setExpectedSTag(STag expectedSTag) {
		this.expectedSTag = expectedSTag;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getFeature(SimpleFeature<T> f) {
		if(null==features.get(f))
			System.out.println("ARRG");
		
		return (T) features.get(f);
	}

	public Set<Entry<SimpleFeature, Object>> getFeatureSet() {
		return features.entrySet();
	}
	
	public static String reconstruct(Token[] toks, int offset, int last) {
		StringBuilder result=new StringBuilder();
		for(int i=offset;i<last && i<toks.length;i++) {
			if(i!=offset && toks[i].hasWhitespaceBefore())
				result.append(' ');
			result.append(toks[i].getRawForm());
		}
		return result.toString();
	}
}
