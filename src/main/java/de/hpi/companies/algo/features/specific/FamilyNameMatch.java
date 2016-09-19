package de.hpi.companies.algo.features.specific;

import java.io.BufferedWriter;
import java.io.IOException;

import com.github.powerlibraries.io.In;
import com.github.powerlibraries.io.Out;

import de.hpi.companies.algo.features.ListMatch;

public class FamilyNameMatch extends ListMatch {

	public FamilyNameMatch() {
		super("familyNames.txt");
	}

	public static void main(String[] args) {
		try(BufferedWriter out = Out.file("src/main/resources/de/hpi/companies/algo/features/specific/familyNames.txt").withUTF8().asWriter()) {
			In.file("../Data/lastnames.json").withUTF8().streamLines()
				.map(l -> l.substring(61, l.length()-3))
				.sorted()
				.distinct()
				.forEach(l -> {
					try {
						out.write(l+"\n");
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
