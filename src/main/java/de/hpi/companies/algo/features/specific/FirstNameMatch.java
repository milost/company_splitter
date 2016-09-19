package de.hpi.companies.algo.features.specific;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.github.powerlibraries.io.Out;

import de.hpi.companies.algo.features.ListMatch;
import de.hpi.companies.util.DBPediaExtractor;

public class FirstNameMatch extends ListMatch {

	private static final Pattern NO_NAME = Pattern.compile("\\w\\..*");
	
	public FirstNameMatch() {
		super("firstNames.txt");
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		HashSet<String> firstNames = new HashSet<>();
		new DBPediaExtractor("SELECT DISTINCT STR(?label) WHERE { ?p foaf:givenName ?label }",Integer.MAX_VALUE) {
			
			@Override
			protected void processValue(String[] cols) {
				String clean = cleanString(cols[0]).trim();
				String[] p = StringUtils.split(clean, ' ');
				if(p.length>0) {
					clean = p[0];
					if(clean.length()>2 && Character.isLetter(clean.charAt(0)) && clean.charAt(1)!='.')
						firstNames.add(clean);
				}
			}
		}.startQuery();
		
		List<String> sorted = new ArrayList<>(firstNames);
		Collections.sort(sorted);
		
		Out
			.file("src/main/resources/de/hpi/companies/algo/features/specific/firstNames.txt")
			.withUTF8()
			.writeLines(sorted);
	}
}
