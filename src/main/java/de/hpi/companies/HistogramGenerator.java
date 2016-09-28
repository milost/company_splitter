package de.hpi.companies;

import java.io.IOException;
import java.util.Arrays;

import de.hpi.companies.algo.Parser;
import de.hpi.companies.algo.Tokenizer;

public class HistogramGenerator {
	public static void main(String[] args) throws IOException {
		int[] histo=new int[0];
		int[] histo2=new int[0];
		try(Parser p = new Parser("../data/companies.json")) {
			while(p.hasNext()) {
				String v = p.next();
				int tokens=Tokenizer.tokenize(v).length;
				int l=v.length();
				if(tokens>=histo.length)
					histo=Arrays.copyOf(histo, tokens+1);
				histo[tokens]++;
				if(l>=histo2.length)
					histo2=Arrays.copyOf(histo2, l+1);
				histo2[l]++;
			}
		}
		for(int i=0;i<Math.max(histo.length,histo2.length);i++)
			System.out.println(i+"\t"+(histo2.length>i?histo2[i]:0)+"\t"+(histo.length>i?histo[i]:0));
	}
}
