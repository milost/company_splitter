package de.hpi.companies;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.powerlibraries.io.In;
import com.google.gson.Gson;

import de.hpi.companies.algo.STag;
import de.hpi.companies.algo.Tag;
import de.hpi.companies.algo.TagType;
import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.classifier.AClassifier;
import de.hpi.companies.algo.classifier.OpenNLPClassifier;
import de.hpi.companies.algo.classifier.OpenNLPClassifier.Algorithm;
import de.hpi.companies.algo.classifier.TagExtractor;
import de.hpi.companies.algo.features.FeatureManager;
import de.hpi.companies.algo.features.IFeature;
import de.hpi.companies.algo.features.specific.Surface;
import de.hpi.companies.util.counting.KCounter;

public class BestConfigurationFinder<T extends Enum<T> & TagType<T>> {
	
	private static <T> List<AClassifier<T>> getClassifiers() {
		return Arrays.asList(
			new OpenNLPClassifier<T>(null)//,
			/*new BoostingClassifier<T>(null),
			new StanfordCMMClassifier<T>(null),
			new StanfordCRFClassifier<T>(null),
			new RandomForestClassifier<T>(null),
			new StanfordClassifier<T>(null)*/
		);
	}
	
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		List<Token[]> names = new ArrayList<>();
		File unifiedDir = new File("../Data/unified/");
		
		final Gson gson = new Gson();
		for(File f:unifiedDir.listFiles()) {
			System.out.println("Reading Unified: "+f.getName());
			In.file(f).withUTF8().streamLines()
				.map(l -> gson.fromJson(l, Token[].class))
				.peek(tokens -> {for(Token t:tokens) t.setName(tokens);})
				.peek(FeatureManager.ALL::calculateFeatures)
				.peek(FeatureManager.ALL::createSimplifications)
				.forEach(names::add);
		}
		FeatureManager.ALL.finishSimplifications();
		
		Collections.shuffle(names, new Random(777));
		
		
		for(AClassifier<Tag> cFactory:BestConfigurationFinder.<Tag>getClassifiers()) {
			try (PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream("C:\\Users\\Virenerus\\Google Drive\\Uni\\Masterarbeit\\bestConfigurations\\TAG_"+cFactory.getName()+".txt")), true, "UTF-8")) {
				System.setOut(out);
				new BestConfigurationFinder<Tag>().doIt(
						TagExtractor.Tag::new,
						ex -> cFactory.createClassifier(ex),
						Tag.OTHER,
						names);
			}
		}
		for(AClassifier<STag> cFactory:BestConfigurationFinder.<STag>getClassifiers()) {
			try (PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream("C:\\Users\\Virenerus\\Google Drive\\Uni\\Masterarbeit\\bestConfigurations\\COLLOQUIAL_NAME_"+cFactory.getName()+".txt")), true, "UTF-8")) {
				System.setOut(out);
				new BestConfigurationFinder<STag>().doIt(
						fm -> new TagExtractor.SingularSTag(fm, STag.COLLOQUIAL_NAME),
						ex -> cFactory.createClassifier(ex),
						STag.NONE,
						names);
			}
		}
		for(AClassifier<STag> cFactory:BestConfigurationFinder.<STag>getClassifiers()) {
			try (PrintStream out = new PrintStream(new BufferedOutputStream(new FileOutputStream("C:\\Users\\Virenerus\\Google Drive\\Uni\\Masterarbeit\\bestConfigurations\\PARENT_COMPANY_"+cFactory.getName()+".txt")), true, "UTF-8")) {
				System.setOut(out);
				new BestConfigurationFinder<STag>().doIt(
						fm -> new TagExtractor.SingularSTag(fm, STag.PARENT_COMPANY),
						ex -> cFactory.createClassifier(ex),
						STag.NONE,
						names);
			}
		}
		
	}
	
	private void doIt(Function<FeatureManager,TagExtractor<T>> tagExtractorSupplier, Function<TagExtractor<T>,AClassifier<T>> tagClassifierSupplier, TagType<T> tagType, List<Token[]> names) throws IOException, InterruptedException, ExecutionException {
		
		
		ExecutorService pool = Executors.newFixedThreadPool(1);//Runtime.getRuntime().availableProcessors());
		System.out.println("Pool of "+Runtime.getRuntime().availableProcessors()*2+" threads");
		
		List<IFeature> allFeatures = FeatureManager.ALL.getFeatures();
		Container current = new Container(FeatureManager.ALL.getFeature(Surface.class));
		workWith(current, tagExtractorSupplier.apply(new FeatureManager(current.features)), names, tagClassifierSupplier, tagType);
		while(true) {
			Container best=null;
			
			List<Future<Container>> futures = new ArrayList<>();
			
			for(IFeature f:allFeatures) {
				if(!current.features.contains(f)) {
					Container c = current.childWith(f);
					futures.add(pool.submit(()->workWith(c,tagExtractorSupplier.apply(new FeatureManager(c.features)), names, tagClassifierSupplier, tagType)));
				}
			}
			
			for(Future<Container> f:futures) {
				Container c = f.get();
				if(best == null || c.quality > best.quality)
					best=c;
			}
			
			if(best == null || best.quality <= current.quality) {
				System.out.println("WINNER: "+current.features);
				System.out.println(current.quality);
				pool.shutdown();
				return;
			}
			current=best;
		}
		
		
		/*Set<Container> openList = new HashSet<>();
			openList.add(new Container(FeatureManager.ALL.getFeature(Surface.class)));
		Set<Container> closedList = new HashSet<>();
		while(!openList.isEmpty()) {
			Iterator<Container> it = openList.iterator();
			Container current = it.next();
			it.remove();
			
			current.quality = workWith(new FeatureManager(current.features), names, tagClassifierSupplier);
			closedList.add(current);
			if(current.quality > current.parentQuality) {
				for(IFeature f:allFeatures) {
					if(!current.features.contains(f)) {
						Container next = current.childWith(f);
						if(!openList.contains(next) && !closedList.contains(next))
							openList.add(next);
					}
				}
			}
		}*/
	}
	
	private static class Container {
		private Set<IFeature> features;
		private double quality=0;
		private double parentQuality=0;
		
		public Container(IFeature f) {
			features=new HashSet<>();
			features.add(f);
		}
		public Container() {}
		public Container childWith(IFeature f) {
			Container r = new Container();
			r.features=new HashSet<>(features);
			r.features.add(f);
			r.parentQuality=quality;
			return r;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((features == null) ? 0 : features.hashCode());
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
			Container other = (Container) obj;
			if (features == null) {
				if (other.features != null)
					return false;
			} else if (!features.equals(other.features))
				return false;
			return true;
		}
	}
	

	private Container workWith(Container c, TagExtractor<T> ext, List<Token[]> names, Function<TagExtractor<T>, AClassifier<T>> tagClassifierSupplier, TagType<T> tagType) throws IOException {
		KCounter counter = new KCounter();
		
		int blockSize=names.size()/5;
		//System.out.println("Blocksize: 5*"+blockSize);
		for(int valBlock=0;valBlock<5;valBlock++) {
			//train classifier on all blocks but valBlock
			List<Token[]> training=new ArrayList<>(blockSize*4);
			AClassifier<T> tagClassifier = tagClassifierSupplier.apply(ext);
			for(int block=0;block<5;block++) {
				if(block!=valBlock)
					training.addAll(names.subList(block*blockSize, (block+1)*blockSize));
				
			}
			tagClassifier.train(training);

			//validate on valblock
			for(Token[] name : names.subList(valBlock*blockSize, (valBlock+1)*blockSize)) {
				List<T> tags = tagClassifier.getTags(name);
				counter.count("Tag", Arrays.stream(name).map(ext::getTag).collect(Collectors.toList()), tags);
			}
			
			counter.finishBlock();
		}
		System.out.println(ext.getFeatureManager());
		double result = counter.getCorrectness("tokenwise Tag");
		System.out.println("\t"+result);
		c.quality=result;
		return c;
	}
}
