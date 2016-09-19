package de.hpi.companies;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.github.powerlibraries.io.In;
import com.github.powerlibraries.io.Out;
import com.github.powerlibraries.io.helper.byteout.BAOutputStream;
import com.google.common.base.Supplier;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import de.hpi.companies.algo.STag;
import de.hpi.companies.algo.Tag;
import de.hpi.companies.algo.TagType;
import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.classifier.AClassifier;
import de.hpi.companies.algo.classifier.BoostingClassifier;
import de.hpi.companies.algo.classifier.Classifiers;
import de.hpi.companies.algo.classifier.CombinedClassifier;
import de.hpi.companies.algo.classifier.OpenNLPClassifier;
import de.hpi.companies.algo.classifier.RandomForestClassifier;
import de.hpi.companies.algo.classifier.StanfordCMMClassifier;
import de.hpi.companies.algo.classifier.StanfordCRFClassifier;
import de.hpi.companies.algo.classifier.StanfordClassifier;
import de.hpi.companies.algo.classifier.TagExtractor;
import de.hpi.companies.algo.classifier.OpenNLPClassifier.Algorithm;
import de.hpi.companies.algo.features.FeatureManager;
import de.hpi.companies.algo.features.IFeature;
import org.junit.Assert;

public class SerializationTest {
	
	
	private static List<Supplier<AClassifier<STag>>> CLASSIFIERS = Arrays.asList(
			()->new OpenNLPClassifier<>(null),
			()->new RandomForestClassifier<>(null),
			()->new StanfordClassifier<>(null),
			()->new StanfordCMMClassifier<>(null),
			()->new StanfordCRFClassifier<>(null),
			()->new BoostingClassifier<>(null),
			()->new CombinedClassifier<>(null)
	);
	
	@Test
	public void testSerialization() throws JsonSyntaxException, IOException {
		
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
		
		
		
		Kryo k = new Kryo();
		
		for(TagType tag:new TagType[]{Tag.OTHER, STag.COLLOQUIAL_NAME, STag.PARENT_COMPANY}) {
			System.out.println();
			System.out.println(tag);
			for(Supplier<AClassifier<STag>> cl:CLASSIFIERS) {
				AClassifier<STag> c = cl.get();
				TagExtractor best = Classifiers.getBestConfig(c.getClass(), tag);
				
				AClassifier<Object> bestC = c.createClassifier(best);
				bestC.train(trainingNames);
				
				BAOutputStream out = Out.bytes().asStream();
				System.out.println("Writing\t"+c.getName());
				bestC.serialize(out);
				out.close();
				byte[] bytes = out.toByteArray();
				System.out.println("Reading\t"+c.getName()+"\t"+bytes.length);
				
				AClassifier<Object> copy = AClassifier.deserialize(In.bytes(bytes).asStream());
				
				for(Token[] name:trainingNames)
					Assert.assertEquals("for name "+Arrays.toString(name),bestC.getTags(name), copy.getTags(name));
			}
		}
	}
}
