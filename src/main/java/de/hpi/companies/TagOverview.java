package de.hpi.companies;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.ArrayUtils;

import com.github.powerlibraries.io.In;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import de.hpi.companies.algo.STag;
import de.hpi.companies.algo.Tag;
import de.hpi.companies.algo.Token;

public class TagOverview {
	public static void main(String[] args) throws JsonSyntaxException, IOException {
		
		File[] dirs = new File[] {new File("../Data/unified/"), new File("../Data/unified_eval/")};
		for(File dir:dirs) {
			System.out.println();
			System.out.println(dir);
			List<Token[]> names = new ArrayList<>();
			final Gson gson = new Gson();
			for(File f:dir.listFiles()) {
				System.out.println("Reading Unified: "+f.getName());
				In.file(f).withUTF8().streamLines()
					.map(l -> gson.fromJson(l, Token[].class))
					.peek(tokens -> {for(Token t:tokens) t.setName(tokens);})
					.forEach(names::add);
			}
			
			Multiset<String> tokenCounts = HashMultiset.create();
			Multiset<String> nameCounts = HashMultiset.create();
			for(Token[] n:names) {
				Multiset<String> counts = HashMultiset.create();
				for(Token t:n) {
					counts.add(t.getExpectedTag().toString());
					counts.add(t.getExpectedSTag().toString());
					if(t.getExpectedTag()!=Tag.OTHER)
						counts.add("tag");
					if(t.getExpectedSTag()!=STag.NONE)
						counts.add("stag");
					counts.add("token");
				}
				counts.add("names");
				tokenCounts.addAll(counts);
				nameCounts.addAll(counts.elementSet());
			}
			
			
			for(String e:tokenCounts.elementSet()) {
				int tokenCount = tokenCounts.count(e);
				int nameCount = nameCounts.count(e);
				System.out.println("\\spcl{"+e.replace('_', ' ')+"}\t& \\num{"+tokenCount+"}\t& \\SI{"+format((double)tokenCount/tokenCounts.count("token"))+"}{\\percent}\t& \\num{"+nameCount+"}\t& \\SI{"+format((double)nameCount/nameCounts.count("token"))+"}{\\percent}\\\\");
			}
		}
	}

	
	private static final NumberFormat FORMAT = new DecimalFormat("0.00",new DecimalFormatSymbols(Locale.US));
	private static String format(double v) {
		return FORMAT.format(100*v);
	}
}
