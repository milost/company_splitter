package de.hpi.companies.pretagging;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.collect.Range;

import de.hpi.companies.SetGenerator;
import de.hpi.companies.algo.Tag;
import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.Tokenizer;
import de.hpi.companies.util.trie.Trie;

public class PreTagging {
	
	private static final List<Entry<Tag, Trie>> PRE = Arrays.asList(
					ImmutablePair.of(Tag.LEGAL_FORM, new Trie()
									.addValue("GmbH")
									.addValue("Gesellschaft","mit","beschr�nkter","Haftung")
									.addValue("gesellschaft","mbH")
									.addValue("AG","&"+"."+"Co",".")
									.addValue("KG")
									.addValue("GmbH","&","Co",".","KG")
									.addValue("e",".","K",".")
									.addValue("Gesellschaft","mbH")
									.addValue("gesellschaft","UG")
									.addValue("UG")
									.addValue("haftungsbeschr�nkt")
									.addValue("B",".","V",".")
									.addValue("AG")
									.addValue("mbH")
									.addValue("GMBH")
									.addValue("OHG")
					),
					ImmutablePair.of(Tag.BUSINESS_DETAILS, new Trie()
							.addValue("Zweig","niederlassung")
					),
					ImmutablePair.of(Tag.OTHER, new Trie()
							.addValue("und")
							.addValue("f�r")
							.addValue("@")
							.addValue("$")
							.addValue("�")
							.addValue("�")
							.addValue("@")
					),
					ImmutablePair.of(Tag.SECTOR, new Trie()
							.addValue("verwaltung")
							.addValue("verwaltungs")
							.addValue("Verwaltung")
							.addValue("Verwaltungs")
					),
					ImmutablePair.of(Tag.LOCATION, new Trie()
							.addValue("Berlin")
							.addValue("M�nchen")
							.addValue("Deutschland")
							.addValue("Germany")
					),
					ImmutablePair.of(Tag.PERSON_ROLE, new Trie()
							.addValue("Inhaber")
							.addValue("Inh",".")
					),
					ImmutablePair.of(Tag.PERSON_TITLE, new Trie()
							.addValue("Dr",".")
							.addValue("Dr",".","med",".")
							.addValue("Prof",".")
					),
					ImmutablePair.of(Tag.PUNCTUATION, new Trie()
							.addValue(".")
							.addValue("(")
							.addValue(")")
							.addValue("...")
							.addValue("\"")
							.addValue("'")
							.addValue("�")
							.addValue("`")
							.addValue("[")
							.addValue("]")
							.addValue("{")
							.addValue("}")
							.addValue("-")
							.addValue(",")
							.addValue("&")
							.addValue("+")
					)
			);
	
	public static void main(String[] args) throws IOException {
		SetGenerator set = new SetGenerator();
		
		for(int i=0;i<set.training.size()/50;i++)
			preTag("training-"+i+".ann",set.training.subList(i*50, (i+1)*50));
		for(int i=0;i<set.testing.size()/50;i++)
			preTag("testing-"+i+".ann",set.testing.subList(i*50, (i+1)*50));
	}
	
	private static void preTag(String fileName, List<String> names) throws FileNotFoundException, IOException {
		List<Token[]> lines = new ArrayList<>();
		List<Range<Integer>> map = new ArrayList<>();
		int pos=0;
		for(String name:names) {
			Token[] tokens=Tokenizer.tokenize(name);
			lines.add(tokens);
			for(int t=0;t<tokens.length;t++) {
				int start=pos;
				pos+=tokens[t].getRawForm().length();
				map.add(Range.closedOpen(start, pos));
				pos++; //\n or space
			}
		}
		
		int count=1;
		int token=0;
		try(BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("../data/pretagged/"+fileName), StandardCharsets.UTF_8))) {
			for(Token[] line:lines) {
				Tag[] results = new Tag[line.length];
				String[] tokenStrings = Arrays.stream(line).map(t -> t.getRawForm()).toArray(String[]::new);
				for(int i=0;i<line.length;i++) {
					if(results[i]==null) {
						for(Entry<Tag, Trie> e:PRE) {
							int matchLength=e.getValue().match(tokenStrings, i);
							if(matchLength>0) {
								for(int j=0;j<matchLength;j++)
									results[i+j]=e.getKey();
								break;
							}
						}
					}
				}
				
				for(int i=0;i<results.length;i++) {
					if(results[i]!=null && (i==0 || results[i]!=results[i-1])) {
						int start=i;
						int end;
						StringBuilder raw=new StringBuilder();
						for(end=i;end<results.length && results[end]==results[i];end++) {
							raw.append(line[end].getRawForm());
							raw.append(' ');
						}
						
						out.write("T"+(count++));
						out.write('\t');
						out.write(results[i].getBratName());
						out.write(' ');
						out.write(Integer.toString(map.get(token+start).lowerEndpoint()));
						out.write(' ');
						out.write(Integer.toString(map.get(token+end-1).upperEndpoint()));
						out.write('\t');
						out.write(raw.substring(0,raw.length()-1));
						out.write('\n');
					}
				}
				token+=results.length;
			}
		}
	}
}
