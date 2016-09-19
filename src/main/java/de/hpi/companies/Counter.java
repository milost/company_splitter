package de.hpi.companies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import de.danielnaber.jwordsplitter.GermanWordSplitter;
import de.hpi.companies.algo.Parser;
import de.hpi.companies.algo.Token;

public class Counter {
	public static void main(String[] args) throws IOException {
		Multiset<String> counter = HashMultiset.create();
		int i=0;
		try(Parser p = new Parser("../data/companies.json")) {
			for(String name:p) {
				counter.add("compound",countCompound(name));
				i++;
				if(i%10==0) {
					print(i,counter);
				}
			}
		}
		print(i,counter);
		
	}
	
	private static void print(int size, Multiset<String> counter) {
		for(Entry<String> e:counter.entrySet())
			System.out.println((e.getCount()*100/size)+"\t"+e.getCount()+"\t"+e.getElement());
	}

	private static int countCompound(String name) throws IOException {
		ArrayList<Token> parts = new ArrayList<>();
		
		int start=-1;
		int end=-1;
		boolean wordMode=true;
		for(int pos=0;pos<name.length();pos++) {
			char c=name.charAt(pos);
			
			if(Character.isAlphabetic(c)) {
				if(start==-1) {
					start=pos;
					end=pos+1;
					wordMode=true;
				}
				else if(wordMode) {
					end=pos+1;
				}
				else {
					if(end>start)
						parts.add(new Token(name.substring(start,end),start,end,parts.isEmpty()?false:parts.get(parts.size()-1).hasWhitespaceAfter(),false));
					start=pos;
					end=pos+1;
					wordMode=true;
				}
			}
			else if(Character.isWhitespace(c)) {
				if(start!=-1) {
					if(end>start)
						parts.add(new Token(name.substring(start,end),start,end,parts.isEmpty()?false:parts.get(parts.size()-1).hasWhitespaceAfter(),true));
					start=-1;
					end=-1;
				}
			}
			else {
				if(start==-1) {
					start=pos;
					end=pos+1;
					wordMode=false;
				}
				else if(wordMode) {
					if(end>start)
						parts.add(new Token(name.substring(start,end),start,end,parts.isEmpty()?false:parts.get(parts.size()-1).hasWhitespaceAfter(),false));
					start=pos;
					end=pos+1;
					wordMode=false;
				}
				else {
					if(c==name.charAt(pos-1))
						end=pos+1;
					else {						
						if(end>start)
							parts.add(new Token(name.substring(start,end),start,end,parts.isEmpty()?false:parts.get(parts.size()-1).hasWhitespaceAfter(),false));
						end=pos+1;
						start=pos;
						
						wordMode=false;
					}
				}
			}
		}
		if(end>start)
			parts.add(new Token(name.substring(start,end),start,end,parts.isEmpty()?false:parts.get(parts.size()-1).hasWhitespaceAfter(),false));
		
		int counter=0;
		GermanWordSplitter splitter =  new GermanWordSplitter(true);
		for(Token p:parts) {
			List<String> wordParts=splitter.splitWord(p.getRawForm());
			if(wordParts.size()>1)
				counter++;
		}
		return counter>0?1:0;
	}
}
