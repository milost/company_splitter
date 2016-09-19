package de.hpi.companies.algo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.danielnaber.jwordsplitter.GermanWordSplitter;

public class Tokenizer {
	private final GermanWordSplitter splitter;
	private final List<Token> parts=new ArrayList<>();
	private final List<Token> splitted=new ArrayList<>();
	
	public Tokenizer() throws IOException {
		splitter =  new GermanWordSplitter(true);
	}
	
	public Token[] tokenize(String in) {
		parts.clear();
		splitted.clear();
		
		int start=-1;
		int end=-1;
		boolean wordMode=true;
		for(int pos=0;pos<in.length();pos++) {
			char c=in.charAt(pos);
			
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
						parts.add(new Token(in.substring(start,end),start,end,parts.isEmpty()?false:parts.get(parts.size()-1).hasWhitespaceAfter(),false));
					start=pos;
					end=pos+1;
					wordMode=true;
				}
			}
			else if(Character.isWhitespace(c)) {
				if(start!=-1) {
					if(end>start)
						parts.add(new Token(in.substring(start,end),start,end,parts.isEmpty()?false:parts.get(parts.size()-1).hasWhitespaceAfter(),true));
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
						parts.add(new Token(in.substring(start,end),start,end,parts.isEmpty()?false:parts.get(parts.size()-1).hasWhitespaceAfter(),false));
					start=pos;
					end=pos+1;
					wordMode=false;
				}
				else {
					if(c==in.charAt(pos-1))
						end=pos+1;
					else {						
						if(end>start)
							parts.add(new Token(in.substring(start,end),start,end,parts.isEmpty()?false:parts.get(parts.size()-1).hasWhitespaceAfter(),false));
						end=pos+1;
						start=pos;
						
						wordMode=false;
					}
				}
			}
		}
		if(end>start)
			parts.add(new Token(in.substring(start,end),start,end,parts.isEmpty()?false:parts.get(parts.size()-1).hasWhitespaceAfter(),false));
		
		int tokenId=0;
		for(Token p:parts) {
			List<String> wordParts=splitter.splitWord(p.getRawForm());
			for(int i=0;i<wordParts.size();i++) {
				Token split = new Token(wordParts.get(i), tokenId++, 
						p.getRawForm().indexOf(wordParts.get(i)),i==0?p.hasWhitespaceBefore():false,i==wordParts.size()-1?p.hasWhitespaceAfter():false);
				splitted.add(split);
				if(i>0)
					split.setCompoundBefore(true);
				if(i<wordParts.size()-1)
					split.setCompoundAfter(true);
			}
		}
		
		return splitted.toArray(new Token[splitted.size()]);
	}
}
