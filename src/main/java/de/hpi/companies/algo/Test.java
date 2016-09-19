package de.hpi.companies.algo;

import java.io.BufferedWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.github.powerlibraries.io.Out;

import de.hpi.companies.util.DBPediaExtractor;

public class Test {
	public static void main(String[] args) throws IOException, InterruptedException {
		try(BufferedWriter out = Out.file("cities.txt").withUTF8().asWriter()) {
new DBPediaExtractor("select STR(?cityL), STR(?countyL) where {?city rdf:type dbo:Settlement.?city dbo:isPartOf ?county.?county rdf:type dbo:AdministrativeRegion.?city rdfs:label ?cityL. ?county rdfs:label ?countyL.FILTER ( lang(?countyL) = \"de\" ).FILTER ( lang(?cityL) = \"de\" )}") {
				
				@Override
				protected void processValue(String[] cols) {
					String city = StringUtils.split(StringUtils.split(cleanString(cols[0]),',')[0],'(')[0].trim();
					String county = cleanString(cols[1]);
					
					try {
						out.write(city+"\t"+county+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.startQuery();
			
			new DBPediaExtractor("select STR(?cityL), STR(?countyL) where {?city rdf:type dbo:Settlement.?city dbo:federalState ?county.?county rdf:type dbo:AdministrativeRegion. ?city rdfs:label ?cityL. ?county rdfs:label ?countyL.FILTER ( lang(?countyL) = \"de\" ).FILTER ( lang(?cityL) = \"de\" )}") {
				
				@Override
				protected void processValue(String[] cols) {
					String city = StringUtils.split(StringUtils.split(cleanString(cols[0]),',')[0],'(')[0].trim();
					String county = cleanString(cols[1]);
					
					try {
						out.write(city+"\t"+county+"\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}.startQuery();
		}
	}
}
