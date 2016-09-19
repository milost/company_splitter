package de.hpi.companies.util.trie;

public class Trie extends TrieNode {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + size;
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
		Trie other = (Trie) obj;
		if (size != other.size)
			return false;
		return true;
	}

	private int size=0;
	
	public Trie addValue(String... value) {
		if(addValue(value,0))
			size++;
		return this;
	}
	
	public int size() {
		return size;
	}

	public int match(String[] tokenStrings, int pos) {
		return match(tokenStrings, pos, 0);
	}
}
