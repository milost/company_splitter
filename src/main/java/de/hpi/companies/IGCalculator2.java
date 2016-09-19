package de.hpi.companies;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.BiFunction;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.github.powerlibraries.io.In;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.SetMultimap;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.FeatureManager;
import de.hpi.companies.algo.features.IComplexFeature;
import de.hpi.companies.algo.features.SimpleFeature;
import de.hpi.companies.algo.features.specific.Surface;

public class IGCalculator2 {
	
	private static final NumberFormat NUMBER = new DecimalFormat("0.###");
	
	
	private static final BiFunction<Token, Integer, String> TAG_FUNCTION = (t, pos)-> {
		int id=t.getTokenId()+pos;
		try {
			if(id>=0 && id<t.getName().length)
				return t.getName()[id].getExpectedTag().name();
			else
				return "NO_TOKEN";
		}
		catch(NullPointerException e) {
			throw new IllegalStateException(e);
		}
	};
	private static final BiFunction<Token, Integer, String> S_TAG_FUNCTION = (t, pos)->{
		int id=t.getTokenId()+pos;
		if(id>=0 && id<t.getName().length)
			return t.getName()[id].getExpectedSTag().name();
		else
			return "NO_TOKEN";
	};
	
	
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
		
		
		for(SimpleFeature f:FeatureManager.ALL.getSimpleFeatures()) {
			double best=0;
			System.out.println(f.getName()+":");
			for(int d=-2;d<=2;d++) {
				double generalEnt=entropy2(tokens, surface, TAG_FUNCTION, d);
				double entropy=entropy3(tokens, surface, f, TAG_FUNCTION, d);
				System.out.println("  IG "+num(d)+":\t "+NUMBER.format(generalEnt-entropy));
				best=Math.max(best, generalEnt-entropy);
			}
			for(int d=-2;d<=2;d++) {
				double generalEntS=entropy2(tokens, surface, S_TAG_FUNCTION, d);
				double entropyS=entropy3(tokens, surface, f, S_TAG_FUNCTION, d);
				System.out.println("S-IG "+num(d)+":\t"+NUMBER.format(generalEntS-entropyS));
				best=Math.max(best, generalEntS-entropyS);
			}
			
			System.out.println();
			
			value.add(ImmutablePair.of(best, f.getName()));
		}

		SetMultimap<String, String> dominates = HashMultimap.create();
		
		for(SimpleFeature f1:FeatureManager.ALL.getSimpleFeatures()) {
			for(SimpleFeature f2:FeatureManager.ALL.getSimpleFeatures()) {
				if(f1!=f2) {
					double best=0;
					//System.out.println(f1.getName()+" -> "+f2.getName()+":");
					for(int d=-2;d<=2;d++) {
						double generalEnt=entropy3(tokens, surface, f1, TAG_FUNCTION, d);
						double entropy=entropy4(tokens, surface, f1, f2, TAG_FUNCTION, d);
						//System.out.println("  IG "+num(d)+":\t "+NUMBER.format(generalEnt-entropy));
						best=Math.max(best, generalEnt-entropy);
					}
					for(int d=-2;d<=2;d++) {
						double generalEntS=entropy3(tokens, surface, f1, S_TAG_FUNCTION, d);
						double entropyS=entropy4(tokens, surface, f1, f2, S_TAG_FUNCTION, d);
						//System.out.println("S-IG "+num(d)+":\t"+NUMBER.format(generalEntS-entropyS));
						best=Math.max(best, generalEntS-entropyS);
					}
					
					//System.out.println();
					if(best<0.0001)
						dominates.put(f1.getName(), f2.getName());
					dualValue.add(ImmutablePair.of(best, f1.getName()+" -> "+f2.getName()));
				}
			}
		}
		
		for(IComplexFeature f:FeatureManager.ALL.getComplexFeatures()) {
			System.out.println(f.getClass().getSimpleName()+":");
			double best=0;
			for(int d=0;d<5;d++) {
				double generalEnt=entropy1(tokens, TAG_FUNCTION, d);
				double entropy=entropy2(tokens, f, TAG_FUNCTION, d);
				System.out.println("  IG "+num(d)+":\t "+NUMBER.format(generalEnt-entropy));
				best=Math.max(best, generalEnt-entropy);
			}
			for(int d=0;d<5;d++) {
				double generalEntS=entropy1(tokens, S_TAG_FUNCTION, d);
				double entropyS=entropy2(tokens, f, S_TAG_FUNCTION, d);
				System.out.println("S-IG "+num(d)+":\t"+NUMBER.format(generalEntS-entropyS));
				best=Math.max(best, generalEntS-entropyS);
			}
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
	}
	
	private static String num(int d) {
		if(d<0)
			return Integer.toString(d);
		else
			return "+"+d;
	}

	private static double entropy1(Collection<Token> tokens, BiFunction<Token, Integer, String> tagFunction, int pos) {
		Multiset<String> counter = HashMultiset.create();
		for(Token t:tokens) {
			counter.add(tagFunction.apply(t, pos));
		}
		
		int sum = counter.size();
		double entropy=0;
		for(Entry<String> e:counter.entrySet()) {
			double p=(double)e.getCount()/sum;
			entropy-=p*Math.log(p)/Math.log(2);
		}
		return entropy;
	}

	@SuppressWarnings("rawtypes")
	private static double entropy2(Collection<Token> tokens, SimpleFeature f, BiFunction<Token, Integer, String> tagFunction, int pos) {
		ListMultimap<String,Token> counter = ArrayListMultimap.create();
		for(Token t:tokens) {
			counter.put(f.getValueAsString(t),t);
		}
		
		int sum = counter.size();
		double entropy=0;
		for(java.util.Map.Entry<String, Collection<Token>> e:counter.asMap().entrySet()) {
			double p=(double)e.getValue().size()/sum;
			entropy+=p*entropy1(e.getValue(), tagFunction, pos);
		}
		return entropy;
	}
	
	@SuppressWarnings("rawtypes")
	private static double entropy3(Collection<Token> tokens, SimpleFeature f1, SimpleFeature f2, BiFunction<Token, Integer, String> tagFunction, int pos) {
		ListMultimap<String,Token> counter = ArrayListMultimap.create();
		for(Token t:tokens) {
			counter.put(f2.getValueAsString(t),t);
		}
		
		int sum = counter.size();
		double entropy=0;
		for(java.util.Map.Entry<String, Collection<Token>> e:counter.asMap().entrySet()) {
			double p=(double)e.getValue().size()/sum;
			entropy+=p*entropy2(e.getValue(), f1, tagFunction, pos);
		}
		return entropy;
	}
	
	@SuppressWarnings("rawtypes")
	private static double entropy4(Collection<Token> tokens, SimpleFeature f1, SimpleFeature f2, SimpleFeature f3, BiFunction<Token, Integer, String> tagFunction, int pos) {
		ListMultimap<String,Token> counter = ArrayListMultimap.create();
		for(Token t:tokens) {
			counter.put(f3.getValueAsString(t),t);
		}
		
		int sum = counter.size();
		double entropy=0;
		for(java.util.Map.Entry<String, Collection<Token>> e:counter.asMap().entrySet()) {
			double p=(double)e.getValue().size()/sum;
			entropy+=p*entropy3(e.getValue(), f1, f2, tagFunction, pos);
		}
		return entropy;
	}
	
	private static double entropy2(Collection<Token> tokens, IComplexFeature f, BiFunction<Token, Integer, String> tagFunction, int pos) {
		ListMultimap<String,Token> counter = ArrayListMultimap.create();
		for(Token t:tokens) {
			counter.put(f.calculateFeatures(t)[pos],t);
		}
		
		int sum = counter.size();
		double entropy=0;
		for(java.util.Map.Entry<String, Collection<Token>> e:counter.asMap().entrySet()) {
			double p=(double)e.getValue().size()/sum;
			entropy+=p*entropy1(e.getValue(), tagFunction, 0);
		}
		return entropy;
	}
}
