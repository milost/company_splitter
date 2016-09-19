package de.hpi.companies.algo.classifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import de.hpi.companies.algo.STag;
import de.hpi.companies.algo.Tag;
import de.hpi.companies.algo.TagType;
import de.hpi.companies.algo.features.FeatureManager;
import de.hpi.companies.algo.features.IFeature;

public class Classifiers {
	private static final Table<Class<? extends AClassifier>, TagType, TagExtractor> BEST_CONFIGS;
	
	static {
		BEST_CONFIGS=HashBasedTable.create();
		
		add(BoostingClassifier.class,		Tag.OTHER, "[Surface, Frequency]");
		add(OpenNLPClassifier.class,		Tag.OTHER, "[Suffix-2, FirstNameMatch, WordCut-5, Frequency, IsSpecial, WordConnectionFeature, Surface, LongShape]");
		add(RandomForestClassifier.class,	Tag.OTHER, "[ShortForm, Surface, Shape, WordCut-3, WordConnectionFeature, UpperCase, FirstNameMatch, LocationMatch, FamilyNameMatch, SectorMatch]");
		add(StanfordClassifier.class,		Tag.OTHER, "[AbsWordPosition, Surface, WordCut-1]");
		add(StanfordCRFClassifier.class,	Tag.OTHER, "[NameShape, Frequency, LegalRegex, Suffix-4, LocationMatch, Suffix-2, Prefix-3, FamilyNameMatch, ShortForm, AbsWordPosition, Surface, UpperCase, LongShape]");
		add(StanfordCMMClassifier.class,	Tag.OTHER, "[InParenthesis, NameShape, Frequency, Shape, Length, LegalRegex, LocationMatch, Suffix-2, Prefix-3, CleanedSurface, FamilyNameMatch, ShortForm, FullWord, Surface, LongShape, WordConnectionFeature]");
		add(CombinedClassifier.class,		Tag.OTHER, "[Surface]");
		
		add(BoostingClassifier.class,		STag.COLLOQUIAL_NAME, "[AbsWordPosition, WordConnectionFeature, LegalRegex, Surface, WordCut-1, WordCut-3]");
		add(OpenNLPClassifier.class,		STag.COLLOQUIAL_NAME, "[WordPosition, NameShape, CleanedSurface, AbsWordPosition, Surface]");
		add(RandomForestClassifier.class,	STag.COLLOQUIAL_NAME, "[AbsWordPosition, Prefix-3, LegalRegex, CleanedSurface, Surface, NameShape, InParenthesis]");
		add(StanfordClassifier.class,		STag.COLLOQUIAL_NAME, "[AbsWordPosition, Prefix-3, LegalRegex, Surface, NameShape]");
		add(StanfordCRFClassifier.class,	STag.COLLOQUIAL_NAME, "[AbsRevWordPosition, AbsWordPosition, Surface, LongShape]");
		add(StanfordCMMClassifier.class,	STag.COLLOQUIAL_NAME, "[AbsWordPosition, WordConnectionFeature, LegalRegex, Surface, LocationMatch, InParenthesis, LongShape]");
		add(CombinedClassifier.class,		STag.COLLOQUIAL_NAME, "[Surface]");
		
		add(BoostingClassifier.class,		STag.PARENT_COMPANY, "[AbsWordPosition, Surface]");
		add(OpenNLPClassifier.class,		STag.PARENT_COMPANY, "[Surface]");
		add(RandomForestClassifier.class,	STag.PARENT_COMPANY, "[FullWord, Surface]");
		add(StanfordClassifier.class,		STag.PARENT_COMPANY, "[Surface, Prefix-1]");
		add(StanfordCRFClassifier.class,	STag.PARENT_COMPANY, "[Prefix-1, Surface]");
		add(StanfordCMMClassifier.class,	STag.PARENT_COMPANY, "[Surface, Prefix-1]");
		add(CombinedClassifier.class,		STag.PARENT_COMPANY, "[Surface]");
	}
	
	public static TagExtractor getBestConfig(Class<? extends AClassifier> classifier, TagType tag) {
		TagExtractor res = BEST_CONFIGS.get(classifier, tag);
		if(res==null || res.getFeatureManager().getFeatures().isEmpty())
			throw new IllegalArgumentException("No config for "+tag+" and "+classifier);
		return res;
	}
	
	public static <T extends Enum<T>&TagType<T>> TagExtractor<T> getBestConfig(Class<? extends AClassifier> classifier, T tag) {
		TagExtractor res = BEST_CONFIGS.get(classifier, tag);
		if(res==null || res.getFeatureManager().getFeatures().isEmpty())
			throw new IllegalArgumentException("No config for "+tag+" and "+classifier);
		return res;
	}

	private static void add(Class<? extends AClassifier> classifier, TagType tt, String features) {
		HashMap<String, IFeature> nameMap = new HashMap<>();
		for(IFeature f:FeatureManager.ALL.getFeatures())
			nameMap.put(f.getName(), f);

		List<IFeature> l = new ArrayList<>();
		for(String n:StringUtils.split(features.substring(1, features.length()-1),',')) {
			if(!nameMap.containsKey(n.trim()))
				throw new IllegalStateException("Unknown Feature "+n.trim()+" for classifier "+classifier.getSimpleName());
			l.add(nameMap.get(n.trim()));
		}
		
		TagExtractor te;
		if(tt instanceof Tag)
			te=new TagExtractor.Tag(new FeatureManager(l));
		else if(tt instanceof STag) {
			if(tt == tt.getUnknown())
				te=new TagExtractor.STag(new FeatureManager(l));
			else
				te=new TagExtractor.SingularSTag(new FeatureManager(l), (STag)tt);
		}
		else
			throw new IllegalArgumentException();
		
		BEST_CONFIGS.put(classifier, tt, te);
	}
}
