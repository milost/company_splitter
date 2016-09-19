package de.hpi.companies.util;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.github.powerlibraries.io.In;
import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.FeatureManager;
import de.hpi.companies.algo.features.IComplexFeature;
import de.hpi.companies.algo.features.IFeature;
import de.hpi.companies.algo.features.SimpleFeature;
import de.hpi.companies.algo.features.specific.SectorMatch2;
import de.hpi.companies.algo.features.specific.Surface;

public class SingleIGCalculator {
	
	private static final NumberFormat NUMBER = new DecimalFormat("0.###");
	private static final int[] WINDOW_SIZES = {0,2};
	private static final Class<? extends IFeature> FEATURE = SectorMatch2.class;
	
	
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
		
		SimpleFeature<String> surface = FeatureManager.ALL.getFeature(Surface.class);
		SimpleFeature<?> f = (SimpleFeature<?>) FeatureManager.ALL.getFeature(FEATURE);
		

		System.out.println(f.getName()+":");
		for(int window:WINDOW_SIZES) {
			double generalEnt=entropy2(tokens, surface, TAG_FUNCTION, window);
			double entropy=entropy3(tokens, surface, f, TAG_FUNCTION, window);
			System.out.println("  IG "+num(window)+":\t "+NUMBER.format(generalEnt-entropy));
		}
		for(int window:WINDOW_SIZES) {
			double generalEntS=entropy2(tokens, surface, S_TAG_FUNCTION, window);
			double entropyS=entropy3(tokens, surface, f, S_TAG_FUNCTION, window);
			System.out.println("S-IG "+num(window)+":\t"+NUMBER.format(generalEntS-entropyS));
		}
			
	}
	
	private static String num(int d) {
		return "["+Integer.toString(d)+"]";
	}

	private static double entropy1(Collection<Token> tokens, BiFunction<Token, Integer, String> tagFunction) {
		Multiset<String> counter = HashMultiset.create();
		for(Token t:tokens) {
			counter.add(getCountable(tok->tagFunction.apply(tok, tok.getTokenId()),t,0));
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
	private static double entropy2(Collection<Token> tokens, SimpleFeature f, BiFunction<Token, Integer, String> tagFunction, int window) {
		ListMultimap<String,Token> counter = ArrayListMultimap.create();
		for(Token t:tokens) {
			counter.put(getCountable(f::getValueAsString,t,window), t);
		}
		
		int sum = counter.size();
		double entropy=0;
		for(java.util.Map.Entry<String, Collection<Token>> e:counter.asMap().entrySet()) {
			double p=(double)e.getValue().size()/sum;
			entropy+=p*entropy1(e.getValue(), tagFunction);
		}
		return entropy;
	}
	
	@SuppressWarnings("rawtypes")
	private static double entropy3(Collection<Token> tokens, SimpleFeature f1, SimpleFeature f2, BiFunction<Token, Integer, String> tagFunction, int window) {
		ListMultimap<String,Token> counter = ArrayListMultimap.create();
		for(Token t:tokens) {
			counter.put(getCountable(f2::getValueAsString,t,window),t);
		}
		
		int sum = counter.size();
		double entropy=0;
		for(java.util.Map.Entry<String, Collection<Token>> e:counter.asMap().entrySet()) {
			double p=(double)e.getValue().size()/sum;
			entropy+=p*entropy2(e.getValue(), f1, tagFunction, window);
		}
		return entropy;
	}
	
	@SuppressWarnings("rawtypes")
	private static double entropy4(Collection<Token> tokens, SimpleFeature f1, SimpleFeature f2, SimpleFeature f3, BiFunction<Token, Integer, String> tagFunction, int window) {
		ListMultimap<String,Token> counter = ArrayListMultimap.create();
		for(Token t:tokens) {
			counter.put(getCountable(f3::getValueAsString,t,window),t);
		}
		
		int sum = counter.size();
		double entropy=0;
		for(java.util.Map.Entry<String, Collection<Token>> e:counter.asMap().entrySet()) {
			double p=(double)e.getValue().size()/sum;
			entropy+=p*entropy3(e.getValue(), f1, f2, tagFunction, window);
		}
		return entropy;
	}
	
	private static double entropy2(Collection<Token> tokens, IComplexFeature f, BiFunction<Token, Integer, String> tagFunction) {
		ListMultimap<String,Token> counter = ArrayListMultimap.create();
		for(Token t:tokens) {
			counter.put(getCountable(tok->Joiner.on('@').join(f.calculateFeatures(tok)),t,0),t);
		}
		
		int sum = counter.size();
		double entropy=0;
		for(java.util.Map.Entry<String, Collection<Token>> e:counter.asMap().entrySet()) {
			double p=(double)e.getValue().size()/sum;
			entropy+=p*entropy1(e.getValue(), tagFunction);
		}
		return entropy;
	}
}
