package de.hpi.companies.algo.features.specific;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.github.powerlibraries.io.In;
import com.github.powerlibraries.io.Out;

import de.hpi.companies.algo.features.ListMatch;

public class StrictFamilyNameMatch extends ListMatch {

	public StrictFamilyNameMatch() {
		super("strictFamilyNames.txt");
	}

	public static void main(String[] args) throws IOException {
		Set<String> names=new HashSet<>(In.file("src/main/resources/de/hpi/companies/algo/features/specific/familyNames.txt").withUTF8().readLines());
		
		names.removeAll(In.file("src/main/resources/de/hpi/companies/algo/features/specific/firstNames.txt").withUTF8().readLines());
		names.removeAll(In.file("src/main/resources/de/hpi/companies/algo/features/specific/locations.txt").withUTF8().readLines());
		
		Out.file("src/main/resources/de/hpi/companies/algo/features/specific/strictFamilyNames.txt").withUTF8().writeLines(
				names.stream().sorted().iterator());
	}
}
