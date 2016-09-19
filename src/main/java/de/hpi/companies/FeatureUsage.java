package de.hpi.companies;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.gson.JsonSyntaxException;

import de.hpi.companies.algo.STag;
import de.hpi.companies.algo.Tag;
import de.hpi.companies.algo.TagType;
import de.hpi.companies.algo.classifier.AClassifier;
import de.hpi.companies.algo.classifier.BoostingClassifier;
import de.hpi.companies.algo.classifier.Classifiers;
import de.hpi.companies.algo.classifier.CombinedClassifier;
import de.hpi.companies.algo.classifier.OpenNLPClassifier;
import de.hpi.companies.algo.classifier.OpenNLPClassifier.Algorithm;
import de.hpi.companies.algo.classifier.RandomForestClassifier;
import de.hpi.companies.algo.classifier.StanfordCMMClassifier;
import de.hpi.companies.algo.classifier.StanfordCRFClassifier;
import de.hpi.companies.algo.classifier.StanfordClassifier;
import de.hpi.companies.algo.classifier.TagExtractor;
import de.hpi.companies.algo.features.FeatureManager;
import de.hpi.companies.algo.features.IFeature;

public class FeatureUsage {

	private static List<Supplier<AClassifier<STag>>> CLASSIFIERS = Arrays.asList(
			()->new OpenNLPClassifier<>(null),
			()->new RandomForestClassifier<>(null),
			()->new StanfordClassifier<>(null),
			()->new StanfordCMMClassifier<>(null),
			()->new StanfordCRFClassifier<>(null),
			()->new BoostingClassifier<>(null),
			()->new CombinedClassifier<>(null)
	);
	
	public static void main(String[] args) throws JsonSyntaxException, IOException {
		
		for(TagType tag:new TagType[]{Tag.OTHER, STag.COLLOQUIAL_NAME, STag.PARENT_COMPANY}) {
			System.out.println();
			System.out.println(tag);
			for(Supplier<AClassifier<STag>> cl:CLASSIFIERS) {
				AClassifier<STag> c = cl.get();
				System.out.print(c.getName());
				TagExtractor best = Classifiers.getBestConfig(c.getClass(), tag);
				
				for(IFeature f:FeatureManager.ALL.getFeatures())
					System.out.print(" & "+(best.getFeatureManager().getFeatures().contains(f)?"\\checkmark":""));
				System.out.println("\\\\");
			}
		}
	}
}
