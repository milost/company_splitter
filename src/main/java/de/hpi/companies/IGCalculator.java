package de.hpi.companies;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.classifier.TagExtractor;
import de.hpi.companies.algo.features.IComplexFeature;
import de.hpi.companies.algo.features.SimpleFeature;

public class IGCalculator {
	
	private static final NumberFormat NUMBER = new DecimalFormat("0.###");
	private static final int[] WINDOW_SIZES = {0,2};
	
	private static String getValue(Token t, TagExtractor extactor, int pos) {
		int id=t.getTokenId()+pos;
		if(id>=0 && id<t.getName().length)
			return extactor.getTag(t.getName()[id]).toString();
		else
			return "NO_TOKEN";
	}
	/*
	public static void main(String[] args) throws JsonSyntaxException, IOException {
		List<Token> tokens = new ArrayList<>();
		File unifiedDir = new File("../Data/unified/");
		
		final Gson gson = new Gson();
		for(File f:unifiedDir.listFiles()) {
			System.out.println("Reading Unified: "+f.getName());
			In.file(f).withUTF8().streamLines()
				.map(l -> gson.fromJson(l, Token[].class))
				.peek(name -> {for(Token t:name) t.setName(name);})
				.peek(FeatureManager.ALL::calculateFeatures)
				.flatMap(Arrays::stream)
				.forEach(tokens::add);
		}
		

		PriorityQueue<Pair<Double,String>> value = new PriorityQueue<>();
		PriorityQueue<Pair<Double,String>> dualValue = new PriorityQueue<>();
		SimpleFeature<String> surface = FeatureManager.ALL.getFeature(Surface.class);
		
		
		
		System.out.println(surface.getName()+":");
		for(int window:WINDOW_SIZES) {
			double generalEnt=entropy1(tokens, TAG_FUNCTION);
			double entropy=entropy2(tokens, surface, TAG_FUNCTION, window);
			System.out.println("  IG "+num(window)+":\t "+NUMBER.format(generalEnt-entropy));
			value.add(ImmutablePair.of(generalEnt-entropy, surface.getName()+" "+num(window)));
		}
		for(int window:WINDOW_SIZES) {
			double generalEntS=entropy1(tokens, S_TAG_FUNCTION);
			double entropyS=entropy2(tokens, surface, S_TAG_FUNCTION, window);
			System.out.println("S-IG "+num(window)+":\t"+NUMBER.format(generalEntS-entropyS));
			value.add(ImmutablePair.of(generalEntS-entropyS, surface.getName()+" S "+num(window)));
		}
		
		System.out.println();
		
		
		
		
		
		for(SimpleFeature f:FeatureManager.ALL.getSimpleFeatures()) {
			System.out.println(f.getName()+":");
			for(int window:WINDOW_SIZES) {
				double generalEnt=entropy2(tokens, surface, TAG_FUNCTION, window);
				double entropy=entropy3(tokens, surface, f, TAG_FUNCTION, window);
				System.out.println("  IG "+num(window)+":\t "+NUMBER.format(generalEnt-entropy));
				value.add(ImmutablePair.of(generalEnt-entropy, f.getName()+" "+num(window)));
			}
			for(int window:WINDOW_SIZES) {
				double generalEntS=entropy2(tokens, surface, S_TAG_FUNCTION, window);
				double entropyS=entropy3(tokens, surface, f, S_TAG_FUNCTION, window);
				System.out.println("S-IG "+num(window)+":\t"+NUMBER.format(generalEntS-entropyS));
				value.add(ImmutablePair.of(generalEntS-entropyS, f.getName()+" S "+num(window)));
			}
			
			System.out.println();
			
		}

		SetMultimap<String, String> dominates = HashMultimap.create();
		
		for(SimpleFeature f1:FeatureManager.ALL.getSimpleFeatures()) {
			for(SimpleFeature f2:FeatureManager.ALL.getSimpleFeatures()) {
				if(f1!=f2) {
					//System.out.println(f1.getName()+" -> "+f2.getName()+":");
					for(int window:WINDOW_SIZES) {
						double generalEnt=entropy3(tokens, surface, f1, TAG_FUNCTION, window);
						double entropy=entropy4(tokens, surface, f1, f2, TAG_FUNCTION, window);
						//System.out.println("  IG "+num(d)+":\t "+NUMBER.format(generalEnt-entropy));
						//value.add(ImmutablePair.of(generalEnt-entropy, f1.getName()+" -> "+f2.getName()+" "+num(window)));
						if(generalEnt-entropy<0.0001)
							dominates.put(f1.getName(), f2.getName());
					}
					for(int window:WINDOW_SIZES) {
						double generalEntS=entropy3(tokens, surface, f1, S_TAG_FUNCTION, window);
						double entropyS=entropy4(tokens, surface, f1, f2, S_TAG_FUNCTION, window);
						//System.out.println("S-IG "+num(d)+":\t"+NUMBER.format(generalEntS-entropyS));
						//value.add(ImmutablePair.of(generalEntS-entropyS, f1.getName()+" -> "+f2.getName()+" S "+num(window)));
						if(generalEntS-entropyS<0.0001)
							dominates.put(f1.getName(), f2.getName());
					}
					
					//System.out.println();
					
				}
			}
		}
		
		for(IComplexFeature f:FeatureManager.ALL.getComplexFeatures()) {
			System.out.println(f.getClass().getSimpleName()+":");
			double best=0;
				double generalEnt=entropy2(tokens, surface, TAG_FUNCTION, 0);
				double entropy=entropy3(tokens, surface, f, TAG_FUNCTION);
				System.out.println("  IG :\t "+NUMBER.format(generalEnt-entropy));
				best=Math.max(best, generalEnt-entropy);
				double generalEntS=entropy2(tokens, surface, S_TAG_FUNCTION, 0);
				double entropyS=entropy3(tokens, surface, f, S_TAG_FUNCTION);
				System.out.println("S-IG :\t"+NUMBER.format(generalEntS-entropyS));
				best=Math.max(best, generalEntS-entropyS);
			System.out.println();
			
			value.add(ImmutablePair.of(best, f.getClass().getSimpleName()));
		}
		
		
		Pair<Double, String> e;
		System.out.println("Sorted Pairs:");
		while((e=dualValue.poll())!=null) {
			System.out.println("\t"+NUMBER.format(e.getLeft())+"\t"+e.getRight());
		}
		
		System.out.println("Topological Sorting:");
	
		int lastHash=0;
		List<String> keys = new ArrayList<>(dominates.keySet());
		Set<String> notRoot = new HashSet<>();
		while(dominates.hashCode()!=lastHash) {
			lastHash=dominates.hashCode();
			
			for(String k:keys) {
				if(dominates.containsKey(k)) {
					Set<String> dominated = new HashSet<>(dominates.get(k));
					for(String dom:dominated) {
						dominates.putAll(k, dominates.get(dom));
						notRoot.add(dom);
					}
				}
			}
		}
		System.out.println("Dominates:");
		for(String k:keys) {
			if(!notRoot.contains(k) && dominates.containsKey(k)) {
				System.out.println(k+":");
				for(String d:dominates.get(k))
					System.out.println("\t"+d);
			}
			
		}
		
		System.out.println("\n\nSorted:");
		while((e=value.poll())!=null) {
			System.out.println("\t"+NUMBER.format(e.getLeft())+"\t"+e.getRight());
		}
	}*/
	
	private static String num(int d) {
		return "["+Integer.toString(d)+"]";
	}

	private static double entropy1(Collection<Token> tokens, TagExtractor extractor) {
		Multiset<String> counter = HashMultiset.create();
		for(Token t:tokens) {
			counter.add(getCountable(tok->getValue(tok, extractor, tok.getTokenId()),t,0));
		}
		
		int sum = counter.size();
		double entropy=0;
		for(Entry<String> e:counter.entrySet()) {
			double p=(double)e.getCount()/sum;
			entropy-=p*Math.log(p)/Math.log(2);
		}
		return entropy;
	}

	private static String getCountable(Function<Token, String> printFunction, Token t, int window) {
		List<String> res = new ArrayList<>(window*2+1);
		for(int i=-window;i<=window;i++) {
			int id=t.getTokenId()+i;
			if(id>=0 && id<t.getName().length)
				res.add(printFunction.apply(t.getName()[id]));
			else
				res.add("NO_TOKEN");
		}
		return Joiner.on('@').join(res);
	}

	@SuppressWarnings("rawtypes")
	public static double entropy2(Collection<Token> tokens, SimpleFeature f, TagExtractor extractor, int window) {
		ListMultimap<String,Token> counter = ArrayListMultimap.create();
		for(Token t:tokens) {
			counter.put(getCountable(f::getValueAsString,t,window), t);
		}
		
		int sum = counter.size();
		double entropy=0;
		for(java.util.Map.Entry<String, Collection<Token>> e:counter.asMap().entrySet()) {
			double p=(double)e.getValue().size()/sum;
			entropy+=p*entropy1(e.getValue(), extractor);
		}
		return entropy;
	}
	
	@SuppressWarnings("rawtypes")
	public static double entropy3(Collection<Token> tokens, SimpleFeature f1, SimpleFeature f2, TagExtractor extractor, int window) {
		ListMultimap<String,Token> counter = ArrayListMultimap.create();
		for(Token t:tokens) {
			counter.put(getCountable(f2::getValueAsString,t,window),t);
		}
		
		int sum = counter.size();
		double entropy=0;
		for(java.util.Map.Entry<String, Collection<Token>> e:counter.asMap().entrySet()) {
			double p=(double)e.getValue().size()/sum;
			entropy+=p*entropy2(e.getValue(), f1, extractor, window);
		}
		return entropy;
	}
	
	@SuppressWarnings("rawtypes")
	private static double entropy4(Collection<Token> tokens, SimpleFeature f1, SimpleFeature f2, SimpleFeature f3, TagExtractor extractor, int window) {
		ListMultimap<String,Token> counter = ArrayListMultimap.create();
		for(Token t:tokens) {
			counter.put(getCountable(f3::getValueAsString,t,window),t);
		}
		
		int sum = counter.size();
		double entropy=0;
		for(java.util.Map.Entry<String, Collection<Token>> e:counter.asMap().entrySet()) {
			double p=(double)e.getValue().size()/sum;
			entropy+=p*entropy3(e.getValue(), f1, f2, extractor, window);
		}
		return entropy;
	}
	
	public static double entropy3(Collection<Token> tokens, SimpleFeature f1, IComplexFeature f2, TagExtractor extractor) {
		ListMultimap<String,Token> counter = ArrayListMultimap.create();
		for(Token t:tokens) {
			counter.put(getCountable(tok->Joiner.on('@').join(f2.calculateFeatures(tok)),t,0),t);
		}
		
		int sum = counter.size();
		double entropy=0;
		for(java.util.Map.Entry<String, Collection<Token>> e:counter.asMap().entrySet()) {
			double p=(double)e.getValue().size()/sum;
			entropy+=p*entropy2(e.getValue(), f1, extractor, 0);
		}
		return entropy;
	}
}
