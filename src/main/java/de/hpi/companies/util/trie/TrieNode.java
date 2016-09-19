package de.hpi.companies.util.trie;

import java.util.HashMap;

public class TrieNode {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + (leaf ? 1231 : 1237);
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
		TrieNode other = (TrieNode) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (leaf != other.leaf)
			return false;
		return true;
	}

	private boolean leaf=false;
	private HashMap<String, TrieNode> children = new HashMap<>();
	
	boolean addValue(String[] value, int pos) {
		if(pos==value.length) {
			if(leaf)
				return false;
			else {
				leaf=true;
				return true;
			}
		}
		else {
			TrieNode c = children.get(value[pos]);
			if(c==null) {
				c=new TrieNode();
				children.put(value[pos], c);
			}
			return c.addValue(value, pos+1);
		}
	}
	
	int match(String[] tokenStrings, int pos, int matchLength) {		
		TrieNode c;
		if(pos<tokenStrings.length)
			c = children.get(tokenStrings[pos]);
		else
			c = null;
		int result = -1;
		if(c!=null)
			result=c.match(tokenStrings, pos+1, matchLength+1);
		if(leaf)
			result=Math.max(matchLength,result);
		return result;
	}
}
