package de.hpi.companies.algo;

import java.util.HashMap;
import java.util.Map;

import de.hpi.companies.algo.classifier.TagExtractor;
import de.hpi.companies.algo.features.FeatureManager;

public enum STag implements TagType<STag> {
	COLLOQUIAL_NAME("COL_NA"),
	PARENT_COMPANY("PAR_COMP"),
	NONE(null);
	
	public static final TagExtractor<STag> EXTRACTOR = new TagExtractor.STag(FeatureManager.ALL);
	private static HashMap<String, STag> bratMap;
	
	public static Map<String, STag> getBratMap() {
		if(bratMap==null) {
			bratMap=new HashMap<>();
			for(STag t:STag.values())
				bratMap.put(t.getBratName(), t);
		}
		return bratMap;
	}
	
	
	private String bratName;

	private STag(String bratName) {
		this.bratName=bratName;
	}
	
	public String getBratName() {
		return bratName;
	}

	@Override
	public STag getExpectedTag(Token t) {
		return t.getExpectedSTag();
	}
	
	@Override
	public STag getUnknown() {
		return NONE;
	}
}
