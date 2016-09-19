package de.hpi.companies.util.counting;

public class PRCounter {
	private int truePositive;
	private int falsePositive;
	private int falseNegative;
	
	public void addTruePositive() {
		truePositive++;
	}
	
	public void addFalsePositive() {
		falsePositive++;
	}
	
	public void addFalseNegative() {
		falseNegative++;
	}

	public int getTruePositive() {
		return truePositive;
	}

	public int getFalsePositive() {
		return falsePositive;
	}

	public int getFalseNegative() {
		return falseNegative;
	}
	
	public double getPrecision() {
		return (double)(truePositive+1)/(1+truePositive+falsePositive);
	}
	
	public double getRecall() {
		return (double)(truePositive+1)/(1+truePositive+falseNegative);
	}

	public double getFMeasure() {
		return 2d*getPrecision()*getRecall()/(getPrecision()+getRecall());
	}
}
