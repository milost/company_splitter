package de.hpi.companies.algo.features.specific;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;

import com.github.powerlibraries.io.In;
import com.github.powerlibraries.io.Out;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de.hpi.companies.algo.features.ListMatch;

public class SectorMatch extends ListMatch {

	public SectorMatch() {
		super("sectors.txt");
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		HashSet<String> locations = new HashSet<>();
		final Gson gson = new Gson();
		for(String f:new String[]{"../Data/other/gelbe_seiten.json"})
			In.file(f).withUTF8().streamLines()
				.flatMap(l ->StreamSupport.stream(gson.fromJson(l, JsonObject.class).get("branches").getAsJsonArray().spliterator(),false))
				.map(e -> e.getAsString())
				.filter(l -> l!=null && !l.isEmpty())
				.filter(l -> Character.isAlphabetic(l.charAt(0)))
				.map(l-> StringUtils.split(l, ':')[0].trim())
				.forEach(locations::add);
		
		List<String> l=new ArrayList<>(locations);
		Collections.sort(l);
		Out
			.file("src/main/resources/de/hpi/companies/algo/features/specific/sectors.txt")
			.withUTF8()
			.writeLines(l);
	}
}
