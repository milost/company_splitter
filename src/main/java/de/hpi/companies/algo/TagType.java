package de.hpi.companies.algo;

public interface TagType<T extends Enum<T> & TagType<T>> {
	public T getExpectedTag(Token t);
	public T getUnknown();
}
