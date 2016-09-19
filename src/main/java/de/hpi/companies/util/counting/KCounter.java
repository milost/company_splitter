package de.hpi.companies.util.counting;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

public class KCounter {

	private HashSet<String> prKeys=new HashSet<>();
	private HashSet<String> cKeys=new HashSet<>();
	private HashMap<String, PRCounter> prs=new HashMap<>();
	private List<HashMap<String, PRCounter>> historicPrs=new ArrayList<>();
	private HashMap<String, CCounter> cs=new HashMap<>();
	private List<HashMap<String, CCounter>> historicCs=new ArrayList<>();
	
	public void finishBlock() {
		historicPrs.add(prs);
		prs=new HashMap<>();
		historicCs.add(cs);
		cs=new HashMap<>();
	}

	/*public <T extends Enum<T>> boolean count(String valueName, T[] expected, List<T> real) {
		return count(valueName, Arrays.asList(expected), real);
	}*/
	
	public <T extends Enum<T>> boolean count(String valueName, List<T> expected, List<T> real) {
		boolean wrong=false;
		CCounter cCounter = getCCounter("tokenwise "+valueName);
		
		for(int i=0;i<expected.size();i++) {
			if(expected.get(i).equals(real.get(i))) {
				cCounter.addCorrect();
				getPRCounter(valueName+" "+expected.get(i).name()).addTruePositive();
			}
			else {
				wrong=true;
				cCounter.addWrong();
				getPRCounter(valueName+" "+expected.get(i).name()).addFalseNegative();
				getPRCounter(valueName+" "+real.get(i).name()).addFalsePositive();
			}
		}
		if(wrong)
			getCCounter("namewise "+valueName).addWrong();
		else
			getCCounter("namewise "+valueName).addCorrect();
		return wrong;
	}

	private CCounter getCCounter(String name) {
		cKeys.add(name);
		return cs.computeIfAbsent(name, key->new CCounter());
	}
	
	private PRCounter getPRCounter(String name) {
		prKeys.add(name);
		return prs.computeIfAbsent(name, key->new PRCounter());
	}

	public HashSet<String> getPRKeys() {
		return prKeys;
	}
	
	public void print(PrintStream out) {
		List<String> prKeys = new ArrayList<>(this.prKeys);
		Collections.sort(prKeys);
		for(String k:prKeys) {
			out.println();
			out.println(k+":");
			out.println("\tPrecision: "+avgD(historicPrs,k,c -> c.getPrecision()));
			out.println("\tRecall: "+avgD(historicPrs,k,c -> c.getRecall()));
			out.println("\tF-Measure: "+avgD(historicPrs,k,c -> c.getFMeasure()));
			out.print("\tWrong: "+avgI(historicPrs,k,c -> c.getFalsePositive()+c.getFalseNegative()));
		}
		
		
		List<String> cKeys = new ArrayList<>(this.cKeys);
		Collections.sort(cKeys);
		for(String k:cKeys) {
			out.println();
			out.println(k+":");
			out.println("\tCorrectness: "+avgD(historicCs,k,c -> c.getCorrectness()));
		}
	}
	
	public double getCorrectness(String cLabel) {
		return avgD(historicCs,cLabel,c -> c.getCorrectness());
	}

	private <COUNTER_TYPE> double avgD(List<HashMap<String, COUNTER_TYPE>> history, String key, ToDoubleFunction<COUNTER_TYPE> getter) {
		return history.stream()
			.map(map -> map.get(key))
			.mapToDouble(counter -> counter==null?1d:getter.applyAsDouble(counter))
			.average().getAsDouble();
		//Arrays.sort(values);
		/*if(values.length%2==0)
			return (values[values.length/2]+values[values.length/2-1])/2;
		else
			return values[values.length/2];*/
			
	}
	
	private <COUNTER_TYPE> int avgI(List<HashMap<String, COUNTER_TYPE>> history, String key, ToIntFunction<COUNTER_TYPE> getter) {
		return (int)history.stream()
			.map(map -> map.get(key))
			.mapToInt(counter -> counter==null?1:getter.applyAsInt(counter))
			.average().getAsDouble();
		//Arrays.sort(values);
		/*if(values.length%2==0)
			return (values[values.length/2]+values[values.length/2-1])/2;
		else
			return values[values.length/2];*/
			
	}

	public double getPrecision(String prKey) {
		return avgD(historicPrs,prKey,c -> c.getPrecision());
	}
	
	public double getRecall(String prKey) {
		return avgD(historicPrs,prKey,c -> c.getRecall());
	}
	
	public double getFMeasure(String prKey) {
		return avgD(historicPrs,prKey,c -> c.getFMeasure());
	}
}
