package de.hpi.companies.algo.features.specific;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.github.powerlibraries.io.In;
import com.github.powerlibraries.io.Out;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import de.hpi.companies.algo.Tokenizer;
import de.hpi.companies.algo.features.ListMatch;
import de.hpi.companies.util.DBPediaExtractor;

public class YellowPages extends ListMatch {
	
	public YellowPages() {
		super("yellowPages.txt");
	}
		
	public static void main(String[] args) throws JsonSyntaxException, IOException, InterruptedException {
		
		Multiset<String> general=HashMultiset.create();
		HashMap<String, Multiset<String>> sectors = new HashMap<>();
		
		final Gson gson = new Gson();
		for(String f:new String[]{"../Data/other/gelbe_seiten.json"}) {
			In.file(f).withUTF8().streamLines()
				.map(l ->gson.fromJson(l, JsonObject.class))
				.forEach(c -> {
					for(JsonElement name:c.get("aliases").getAsJsonArray()) {
						List<String> toks=Arrays.stream(Tokenizer.tokenize(name.getAsString())).map(t -> t.getRawForm().toLowerCase()).distinct().collect(Collectors.toList());
						for(JsonElement sector:c.get("branches").getAsJsonArray()) {
							if(!sector.getAsString().isEmpty()) {
								sectors
									.computeIfAbsent(sector(sector.getAsString()), k->HashMultiset.create())
									.addAll(toks);
							}
						}
					}
				});
		}
		
		new DBPediaExtractor("SELECT DISTINCT ?industry, STR(?label) WHERE { ?p dbo:industry ?industry. ?p rdfs:label ?label. FILTER ( lang(?label) = \"de\" ) }",Integer.MAX_VALUE) {
			
			@Override
			protected void processValue(String[] cols) {
				String industry = cols[0];
				String name = cleanString(cols[1]).trim();
				
				sectors
					.computeIfAbsent(industry, k->HashMultiset.create())
					.addAll(Arrays.stream(Tokenizer.tokenize(name)).map(t -> t.getRawForm().toLowerCase()).distinct().collect(Collectors.toList()));
			}
		}.startQuery();
		
		sectors.values().stream().map(s -> s.elementSet()).forEach(general::addAll);
		
		HashSet<String> results = new HashSet<>();
		double maxSector = general.entrySet().stream().mapToInt(e->e.getCount()).max().getAsInt();
		for(Entry<String, Multiset<String>> sector:sectors.entrySet()) {
			System.out.println(sector.getKey()+" -> "+sector.getValue().size());
			if(sector.getValue().size()>=10) {
				int maxFrequency=sector.getValue().entrySet().stream().mapToInt(e -> e.getCount()).max().getAsInt();
				for(com.google.common.collect.Multiset.Entry<String> e:sector.getValue().entrySet()) {
					double tf=0.5+0.5*(double)e.getCount()/maxFrequency;
					double idf=Math.log(maxSector/general.count(e.getElement()));
					if(tf*idf>5 && e.getCount()>1 && StringUtils.isAlpha(e.getElement())) {
						results.add(e.getElement());
						System.out.println("\t"+(tf*idf)+"\t"+e.getElement());
					}
				}
			}
			
		}
		
		//results.removeAll(In.file("src/main/resources/de/hpi/companies/algo/features/specific/firstNames.txt").withUTF8().readLines().stream().map(w->w.toLowerCase()).collect(Collectors.toList()));
		//results.removeAll(In.file("src/main/resources/de/hpi/companies/algo/features/specific/locations.txt").withUTF8().readLines().stream().map(w->w.toLowerCase()).collect(Collectors.toList()));
		//results.removeAll(In.file("src/main/resources/de/hpi/companies/algo/features/specific/familyNames.txt").withUTF8().readLines().stream().map(w->w.toLowerCase()).collect(Collectors.toList()));
		
		ArrayList<String> l=new ArrayList<>(results);
		Collections.sort(l);
		Out
			.file("src/main/resources/de/hpi/companies/algo/features/specific/yellowPages.txt")
			.withUTF8()
			.writeLines(l);
	}
	
	
	private static String sector(String sector) {
		return StringUtils.split(sector, ':')[0].trim();
	}
}
