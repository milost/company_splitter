package de.hpi.companies.util.counting;

public class CCounter {
	private int correct;
	private int wrong;
	
	public int getCorrect() {
		return correct;
	}

	public int getWrong() {
		return wrong;
	}
	
	public void addCorrect() {
		correct++;
	}
	
	public void addWrong() {
		wrong++;
	}
	
	public float getCorrectness() {
		return (float)(correct+1)/(1+correct+wrong);
	}
}
