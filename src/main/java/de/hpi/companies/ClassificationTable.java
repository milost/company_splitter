package de.hpi.companies;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.github.powerlibraries.io.In;
import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.gson.Gson;

import de.hpi.companies.algo.STag;
import de.hpi.companies.algo.Tag;
import de.hpi.companies.algo.TagType;
import de.hpi.companies.algo.Token;
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
import de.hpi.companies.algo.features.FeatureManager;
import de.hpi.companies.util.counting.KCounter;

public class ClassificationTable {
	
	private static List<Supplier<AClassifier<STag>>> CLASSIFIERS = Arrays.asList(
			()->new OpenNLPClassifier<>(null),
			()->new RandomForestClassifier<>(null),
			()->new StanfordClassifier<>(null),
			()->new StanfordCMMClassifier<>(null),
			()->new StanfordCRFClassifier<>(null),
			()->new BoostingClassifier<>(null),
			()->new CombinedClassifier<>(null)
	);
	
	public static void main(String[] args) throws IOException {
		List<Token[]> trainingNames = new ArrayList<>();
		File unifiedDir = new File("../Data/unified/");
		
		final Gson gson = new Gson();
		for(File f:unifiedDir.listFiles()) {
			System.out.println("Reading Unified: "+f.getName());
			In.file(f).withUTF8().streamLines()
				.map(l -> gson.fromJson(l, Token[].class))
				.peek(tokens -> {for(Token t:tokens) t.setName(tokens);})
				.peek(FeatureManager.ALL::calculateFeatures)
				.peek(FeatureManager.ALL::createSimplifications)
				.forEach(trainingNames::add);
		}
		FeatureManager.ALL.finishSimplifications();
		
		List<Token[]> evalNames = new ArrayList<>();
		for(File f:new File("../Data/unified_eval/").listFiles()) {
			System.out.println("Reading Unified: "+f.getName());
			In.file(f).withUTF8().streamLines()
				.map(l -> gson.fromJson(l, Token[].class))
				.peek(tokens -> {for(Token t:tokens) t.setName(tokens);})
				.peek(FeatureManager.ALL::calculateFeatures)
				.forEach(evalNames::add);
		}
		
		
		
		for(TagType tag:new TagType[]{Tag.OTHER, STag.COLLOQUIAL_NAME, STag.PARENT_COMPANY}) {
			Map<String, KCounter> measurements=new HashMap<>();
			count(trainingNames, evalNames, measurements, tag);
		
			List<Entry<String, KCounter>> m=new ArrayList<>(measurements.entrySet());
			
			System.out.println("PRECISION");
			System.out.println("Tag\t"+Joiner.on('\t').join(m.stream().map(e->e.getKey()+"\t\t").iterator()));
			for(TagType prKey:tag.getClass().getEnumConstants()) {
				System.out.println(prKey+"\t"+Joiner.on('\t').join(m.stream().map(e->e.getValue().getPrecision("Tag "+prKey)+"\t"+e.getValue().getRecall("Tag "+prKey)+"\t"+e.getValue().getFMeasure("Tag "+prKey)).iterator()));
			}
			
			/*System.out.println("\n\n");
			System.out.println("RECALL");
			System.out.println("Tag\t"+Joiner.on('\t').join(m.stream().map(e->e.getKey()).iterator()));
			for(TagType prKey:tag.getClass().getEnumConstants()) {
				System.out.println(prKey+"\t"+Joiner.on('\t').join(m.stream().mapToDouble(e->e.getValue().getRecall("Tag "+prKey)).iterator()));
			}
			System.out.println("\n\n");
			System.out.println("F-MEASURE");
			System.out.println("Tag\t"+Joiner.on('\t').join(m.stream().map(e->e.getKey()).iterator()));
			for(TagType prKey:tag.getClass().getEnumConstants()) {
				System.out.println(prKey+"\t"+Joiner.on('\t').join(m.stream().mapToDouble(e->e.getValue().getFMeasure("Tag "+prKey)).iterator()));
			}*/
			System.out.println("All\t"+Joiner.on('\t').join(m.stream().map(e->e.getValue().getCorrectness("tokenwise Tag")+"\t\t").iterator()));
		}
	}

	private static <T extends Enum<T>&TagType<T>> void count(List<Token[]> trainingNames, List<Token[]> evalNames, Map<String, KCounter> measurements, TagType tag) throws IOException {
		for(Supplier<AClassifier<STag>> tagClassifierSupplier:CLASSIFIERS) {
		
			KCounter counter = new KCounter();
			
			//train classifier on all blocks but valBlock
			AClassifier<T> tagClassifier = tagClassifierSupplier.get().createClassifier(Classifiers.<T>getBestConfig((Class<? extends AClassifier<T>>)tagClassifierSupplier.get().getClass(), (T)tag));
			tagClassifier.train(trainingNames);

			//validate on valblock
			for(Token[] name : evalNames) {
				List<T> tags = tagClassifier.getTags(name);
				counter.count("Tag", Arrays.stream(name).map(tagClassifier::getTag).collect(Collectors.toList()), tags);
			}
			
			counter.finishBlock();
			
			measurements.put(tagClassifierSupplier.get().getName(), counter);
		}
	}
}
