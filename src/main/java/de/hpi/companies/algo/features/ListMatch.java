package de.hpi.companies.algo.features;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.UnaryOperator;

import com.github.powerlibraries.io.In;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.Tokenizer;
import de.hpi.companies.util.trie.Trie;

public abstract class ListMatch extends StringFeature {

	protected Trie trie;
	private String[] filenames;
	private UnaryOperator<String>[] extractors;

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((trie == null) ? 0 : trie.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ListMatch other = (ListMatch) obj;
		if (trie == null) {
			if (other.trie != null)
				return false;
		} else if (!trie.equals(other.trie))
			return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	public ListMatch(String... filenames) {
		this(filenames, new UnaryOperator[]{UnaryOperator.identity()});
	}
	
	@SuppressWarnings("unchecked")
	public ListMatch(String[] filenames, UnaryOperator<String>... extractors) {
		this.filenames=filenames;
		this.extractors=extractors;
	}
	
	@Override
	public void calculateFeatures(Token[] tokens) {
		init();
		boolean[] results = new boolean[tokens.length];
		String[] tokenStrings = Arrays.stream(tokens).map(t -> t.getRawForm().toLowerCase()).toArray(String[]::new);
		for(int i=0;i<tokens.length;i++) {
			if(!results[i]) {
				int matchLength=trie.match(tokenStrings, i);
				if(matchLength>0) {
					for(int j=0;j<matchLength;j++)
						results[i+j]=true;
				}
			}
		}
		
		for(int i=0;i<tokens.length;i++)
			tokens[i].setFeature(this, Boolean.toString(results[i]));
	}

	private void init() {
		if(trie == null) {
			try {
				trie = new Trie();
				Tokenizer tokenizer = new Tokenizer();
				int size=0;
				System.out.println("Reading "+this.getClass().getSimpleName());
				for(int f=0;f<filenames.length;f++) {
					UnaryOperator<String> extractor = extractors[f];
					In.resource(this.getClass(), filenames[f]).withUTF8().streamLines()
							.forEach( l -> {
								Token[] tokens = tokenizer.tokenize(extractor.apply(l));
								trie.addValue(Arrays.stream(tokens).map(t -> t.getRawForm().toLowerCase()).toArray(String[]::new));
							});
					if(filenames.length>1)
						System.out.println("\t"+(trie.size()-size)+" new from "+filenames[f]);
					size=trie.size();
				}
				extractors=null;
				filenames=null;
				System.out.println("\tIn total "+trie.size()+" values");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
