package de.hpi.companies;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.github.powerlibraries.io.In;
import com.github.powerlibraries.io.Out;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Heatmaps2 {
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		Gson gson = new Gson();
		HashMap<String, Triple<Double, Double, Integer>> locations = new HashMap<>();
		
		for(String f:new String[]{/*"../Data/osm/germany-2016-03-22_highway.json", */"../Data/osm/germany-2016-03-22_place.json"})
			In.file(f).withUTF8().streamLines()
				.map(l -> gson.fromJson(l, JsonObject.class))
				.filter(l -> l.has("latitude") && l.has("longitude") && l.has("population") && Integer.parseInt(l.get("population").getAsString().replaceAll("\\D", ""))>5000)
				.filter(l -> !"state".equals(l.get("place").getAsString()) && !"country".equals(l.get("place").getAsString()) && !"county".equals(l.get("place").getAsString()))
				.map(l -> Pair.of(l.get("name").getAsString(),Triple.of(l.get("latitude").getAsDouble(),l.get("longitude").getAsDouble(), Integer.parseInt(l.get("population").getAsString().replaceAll("\\D", "")))))
				.filter(l -> Character.isAlphabetic(l.getKey().charAt(0)))
				.forEach(e->locations.put(e.getKey(), e.getValue()));
		
		Table<String, String, Integer> map = In.file("heatmap/all.bin").readObject();
		
		String template = In.file("heatmap/template.html").readAll();
		
		map.rowMap().entrySet().stream()
			.map(e -> ImmutableTriple.of(normalize(e.getKey()), e.getKey(), e.getValue().values().stream().mapToInt(v->v).sum()))
			.collect(Collectors.groupingBy(ImmutableTriple::getLeft))
			.entrySet().stream()
			.map(e -> Triple.of(e.getKey(), e.getValue().stream().map(t->t.getMiddle()).collect(Collectors.toList()), e.getValue().stream().mapToInt(t->t.getRight()).sum()))
			.sorted((a,b)->-Integer.compare(a.getRight(), b.getRight()))
			.limit(150)
			.forEach(e -> {
				System.out.println(e.getLeft());
				StringBuilder sb = new StringBuilder();
				StringBuilder sb2 = new StringBuilder();
				
				List<Pair<String, Integer>> list = e.getMiddle().stream()
					.flatMap(k -> map.row(k).entrySet().stream())
					.collect(Collectors.groupingBy(Entry::getKey))
					.entrySet().stream()
					.map(en -> ImmutablePair.of(en.getKey(), en.getValue().stream().mapToInt(Entry::getValue).sum()))
					.filter(en -> !"europe".equalsIgnoreCase(en.getLeft()) && !"burg".equalsIgnoreCase(en.getLeft()) && !"berg".equalsIgnoreCase(en.getLeft()) && !"mitte".equalsIgnoreCase(en.getLeft()) && !"alpen".equalsIgnoreCase(en.getLeft()))
					.filter(en -> locations.containsKey(en.getLeft()))
					.collect(Collectors.toList());
				Collections.sort(list, (a,b)->-Integer.compare(a.getValue(),b.getValue()));
				
				List<Pair<String, Double>> list2 = list.stream()
					.map(p -> Pair.of(p.getKey(), 10000d*p.getValue()/locations.get(p.getKey()).getRight()))
					.sorted((a,b) -> -Double.compare(a.getRight(), b.getRight()))
					.collect(Collectors.toList());
				
				for(Entry<String, Integer> loc:list) {
					Triple<Double,Double, Integer> coords = locations.get(loc.getKey());
					if(coords != null && !loc.getKey().equals("Deutschland")) {
						sb.append("\t\t{location: new google.maps.LatLng("+coords.getLeft()+", "+coords.getMiddle()+"), weight: "+loc.getValue()+"},\t\t\t//"+loc.toString()+"\n");
					}
				}
				for(Entry<String, Double> loc:list2) {
					Triple<Double,Double, Integer> coords = locations.get(loc.getKey());
					if(coords != null && !loc.getKey().equals("Deutschland")) {
						sb2.append("\t\t{location: new google.maps.LatLng("+coords.getLeft()+", "+coords.getMiddle()+"), weight: "+loc.getValue()+"},\t\t\t//"+loc.toString()+"\n");
					}
				}
				if(sb.length()>0) {
					try {
						Out.file("heatmap/results/"+e.getLeft()+".html").withUTF8().write(template.replace("%%%points%%%",sb.toString()));
						Out.file("heatmap/results/"+e.getLeft()+"_normalized.html").withUTF8().write(template.replace("%%%points%%%",sb2.toString()));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
		
		
	}

	private static String normalize(String key) {
		return key.replaceAll("s+$", "").toLowerCase();
	}
}
