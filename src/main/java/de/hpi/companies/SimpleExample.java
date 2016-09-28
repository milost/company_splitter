package de.hpi.companies;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;

import com.github.powerlibraries.io.In;
import com.google.common.base.Joiner;

import de.hpi.companies.algo.Tag;
import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.classifier.AClassifier;

public class SimpleExample {
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		String name = Joiner.on(' ').join(args);
		System.out.println("Looking at '"+name+"'");
		if(name.length()<=0) return;
		AClassifier<Tag> classifier = In.file("StanfordCRFClassifier-Tag.bin").readObject();
		
		for(Pair<Token, Tag> p:classifier.getTags(name)) {
			System.out.println("\t"+p.getLeft().getRawForm()+"\t"+p.getRight());
		}
	}
}
