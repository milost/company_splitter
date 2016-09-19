package de.hpi.companies;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.powerlibraries.io.In;
import com.google.common.base.Supplier;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import de.hpi.companies.algo.STag;
import de.hpi.companies.algo.TagType;
import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.classifier.AClassifier;
import de.hpi.companies.algo.classifier.RandomForestClassifier;
import de.hpi.companies.algo.classifier.StanfordCRFClassifier;
import de.hpi.companies.algo.classifier.TagExtractor;
import de.hpi.companies.algo.features.FeatureManager;
import de.hpi.companies.algo.features.IComplexFeature;
import de.hpi.companies.algo.features.IFeature;
import de.hpi.companies.algo.features.SimpleFeature;
import de.hpi.companies.algo.features.specific.Surface;
import de.hpi.companies.util.counting.KCounter;

public class FeatureTable {
	
	private static final List<Function<FeatureManager, TagExtractor>> EXTRACTORS = Arrays.asList(
			fm->new TagExtractor.Tag(fm),
			fm->new TagExtractor.SingularSTag(fm, STag.COLLOQUIAL_NAME),
			fm->new TagExtractor.SingularSTag(fm, STag.PARENT_COMPANY)
	);
	
	public static void main(String[] args) throws JsonSyntaxException, IOException {
		
		StringWriter sw = new StringWriter();
		PrintWriter out = new PrintWriter(sw);
		
		Surface surface = FeatureManager.ALL.getFeature(Surface.class);
		
		List<Token[]> names = new ArrayList<>();
		File unifiedDir = new File("../Data/unified/");
		
		final Gson gson = new Gson();
		for(File f:unifiedDir.listFiles()) {
			In.file(f).withUTF8().streamLines()
				.map(l -> gson.fromJson(l, Token[].class))
				.peek(name -> {for(Token t:name) t.setName(name);})
				.peek(FeatureManager.ALL::calculateFeatures)
				.peek(FeatureManager.ALL::createSimplifications)
				.forEach(names::add);
		}
		FeatureManager.ALL.finishSimplifications();
		Collections.shuffle(names, new Random(777));
		
		
		for(IFeature f:FeatureManager.ALL.getFeatures()) {
			out.print(f.getName()+"\t");
			for(Function<FeatureManager, TagExtractor> extr:EXTRACTORS) {
				out.print(igAbs(names,extr,f)+"\t");
				out.print(ig(names,extr,surface,f)+"\t");
				out.print(valueRF(names,extr,surface,f)+"\t");
				out.print(valueCRF(names,extr,surface,f)+"\t");
			}
			out.print(f.getName()+"\t");
			out.println();
			out.flush();
			System.out.println();
			System.out.println();
			System.out.println(sw.toString());
			System.out.println();
			System.out.println();
		}
		
		System.out.println(sw.toString());
	}

	private static <T extends Enum<T> & TagType<T>> double valueRF(List<Token[]> names, Function<FeatureManager, TagExtractor> extr, IFeature f1, IFeature f2) throws IOException {
		FeatureManager simpleManager = new FeatureManager().add(f1);
		if(f1!=f2)
			simpleManager.add(f2);
		Supplier<AClassifier<T>> tagClassifierSupplier= () -> new RandomForestClassifier<T>(extr.apply(simpleManager));
		return value(names, f1, f2, tagClassifierSupplier);
	}
	
	private static <T extends Enum<T> & TagType<T>> double valueCRF(List<Token[]> names, Function<FeatureManager, TagExtractor> extr, IFeature f1, IFeature f2) throws IOException {
		FeatureManager simpleManager = new FeatureManager().add(f1);
		if(f1!=f2)
			simpleManager.add(f2);
		Supplier<AClassifier<T>> tagClassifierSupplier= () -> new StanfordCRFClassifier<T>(extr.apply(simpleManager));
		return value(names, f1, f2, tagClassifierSupplier);
	}

	private static <T extends Enum<T> & TagType<T>> double value(List<Token[]> names, IFeature f1, IFeature f2, Supplier<AClassifier<T>> tagClassifierSupplier) throws IOException {
		KCounter counter = new KCounter();
		
		int blockSize=names.size()/5;
		//System.out.println("Blocksize: 5*"+blockSize);
		for(int valBlock=0;valBlock<5;valBlock++) {
			//train classifier on all blocks but valBlock
			List<Token[]> training=new ArrayList<>(blockSize*4);
			AClassifier<T> tagClassifier = tagClassifierSupplier.get();
			for(int block=0;block<5;block++) {
				if(block!=valBlock)
					training.addAll(names.subList(block*blockSize, (block+1)*blockSize));
			}
			tagClassifier.train(training);

			//validate on valblock
			for(Token[] name : names.subList(valBlock*blockSize, (valBlock+1)*blockSize)) {
				List<T> tags = tagClassifier.getTags(name);
				counter.count("Tag", Arrays.stream(name).map(t->tagClassifier.getTag(t)).collect(Collectors.toList()), tags);
			}
			counter.finishBlock();
		}
		return counter.getCorrectness("tokenwise Tag");
	}

	private static double ig(List<Token[]> names, Function<FeatureManager, TagExtractor> extr, Surface surface, IFeature f) {
		TagExtractor extractor = extr.apply(null);
		List<Token> tokens=names.stream().flatMap(Arrays::stream).collect(Collectors.toList());
		
		double generalEnt=IGCalculator.entropy2(tokens, surface, extractor, 0);
		double entropy;
		if(f instanceof SimpleFeature)
			entropy=IGCalculator.entropy3(tokens, surface, (SimpleFeature)f, extractor, 0);
		else
			entropy=IGCalculator.entropy3(tokens, surface, (IComplexFeature)f, extractor);
		return generalEnt-entropy;
	}
	
	private static double igAbs(List<Token[]> names, Function<FeatureManager, TagExtractor> extr, IFeature f) {
		TagExtractor extractor = extr.apply(null);
		List<Token> tokens=names.stream().flatMap(Arrays::stream).collect(Collectors.toList());
		
		double generalEnt=IGCalculatorAbs.entropy1(tokens, extractor);
		double entropy;
		if(f instanceof SimpleFeature)
			entropy=IGCalculatorAbs.entropy2(tokens, (SimpleFeature)f, extractor, 0);
		else
			entropy=IGCalculatorAbs.entropy2(tokens, (IComplexFeature)f, extractor);
		return generalEnt-entropy;
	}
}
