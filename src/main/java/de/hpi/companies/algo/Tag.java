package de.hpi.companies.algo;

import java.util.HashMap;
import java.util.Map;

import de.hpi.companies.algo.classifier.TagExtractor;
import de.hpi.companies.algo.features.FeatureManager;

public enum Tag implements TagType<Tag> {
	LEGAL_FORM("LEG"),
	BUSINESS_DETAILS("BUS_DE"),
	PERSON_FIRST_NAME("PER_FI"),
	PERSON_LAST_NAME("PER_LA"),
	PERSON_TITLE("PER_TI"),
	PERSON_ROLE("PER_RO"),
	SECTOR("SEC"),
	LOCATION("LOC"),
	PROPER_NOUN("PRO_NO"),
	PUNCTUATION("PUN"),
	ACRONYM("SRT_FR"),
	OTHER("OTH");
	
	public static final TagExtractor<Tag> EXTRACTOR = new TagExtractor.Tag(FeatureManager.ALL);
	private static HashMap<String, Tag> bratMap;
	
	public static Map<String, Tag> getBratMap() {
		if(bratMap==null) {
			bratMap=new HashMap<>();
			for(Tag t:Tag.values())
				bratMap.put(t.getBratName(), t);
		}
		return bratMap;
	}
	
	
	private String bratName;

	private Tag(String bratName) {
		this.bratName=bratName;
	}
	
	public String getBratName() {
		return bratName;
	}

	@Override
	public Tag getExpectedTag(Token t) {
		return t.getExpectedTag();
	}
	
	@Override
	public Tag getUnknown() {
		return OTHER;
	}
}
