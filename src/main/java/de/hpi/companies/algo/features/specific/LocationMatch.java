package de.hpi.companies.algo.features.specific;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import com.github.powerlibraries.io.In;
import com.github.powerlibraries.io.Out;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de.hpi.companies.algo.features.ListMatch;

public class LocationMatch extends ListMatch {

	public LocationMatch() {
		super("locations.txt");
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		HashSet<String> locations = new HashSet<>();
		
		for(String f:new String[]{"../Data/osm/germany-2016-03-22_highway.json", "../Data/osm/germany-2016-03-22_place.json"})
			In.file(f).withUTF8().streamLines()
				.map(l ->new Gson().fromJson(l, JsonObject.class).get("name").getAsString())
				.filter(l -> Character.isAlphabetic(l.charAt(0)))
				.forEach(locations::add);
		
		List<String> l=new ArrayList<>(locations);
		Collections.sort(l);
		Out
			.file("src/main/resources/de/hpi/companies/algo/features/specific/locations.txt")
			.withUTF8()
			.writeLines(l);
	}

	private static final Pattern LOCATION_CLEANER = Pattern.compile("[,\\(].*$");
	
	protected static String cleanLocation(String cleanString) {
		return LOCATION_CLEANER.matcher(cleanString).replaceFirst("");
	}
}
