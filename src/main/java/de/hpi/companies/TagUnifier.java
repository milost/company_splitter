package de.hpi.companies;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.gson.Gson;

import de.hpi.companies.algo.STag;
import de.hpi.companies.algo.Tag;
import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.Tokenizer;

public class TagUnifier {
	public static void main(String[] args) throws IOException {

		SetGenerator set = new SetGenerator();
		
		for(File f:new File("../data/tagged/").listFiles()) {
			if(f.getName().startsWith("training-")) {
				int part=Integer.parseInt(f.getName().substring(9, f.getName().length()-4));
				unify(f,set.training.subList(part*50, (part+1)*50));
			}
		}
	}
	
	private static void unify(File file, List<String> names) throws FileNotFoundException, IOException {
		System.out.println("Unifying "+file);
		List<Token[]> lines = new ArrayList<>();
		RangeMap<Integer, Token> map = TreeRangeMap.create();
		int pos=0;
		for(String name:names) {
			Token[] tokens=Tokenizer.tokenize(name);
			lines.add(tokens);
			for(int t=0;t<tokens.length;t++) {
				int start=pos;
				pos+=tokens[t].getRawForm().length();
				tokens[t].setName(tokens);
				map.put(Range.closedOpen(start, pos), tokens[t]);
				pos++; //\n or space
			}
		}
		
		Gson gson = new Gson();
		
		try(BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("../data/unified/unified."+file.getName()), StandardCharsets.UTF_8))) {
			String line;
			while((line=in.readLine())!=null) {
				String[] parts = StringUtils.split(line, '\t');
				String[] res = StringUtils.split(parts[1],' ');
				
				Consumer<Token> action;
				STag sTag = STag.getBratMap().get(res[0]);
				if(sTag!=null)
					action =(Token t)-> {
							if(t.getExpectedSTag()!=STag.NONE)
								System.err.println("Token '"+t+"' in line '"+Arrays.toString(t.getName())+"' has two expected sTags");
							t.setExpectedSTag(sTag);
						};
				else { 
					Tag tag = Tag.getBratMap().get(res[0]);
					if(tag==null)
						throw new NullPointerException("Unknown TAG for '"+line+"'");
					action =((Token t) -> {
						if(t.getExpectedTag()!=null)
							System.err.println("Token '"+t+"' in line '"+Arrays.toString(t.getName())+"' has two expected tags");
						t.setExpectedTag(tag);
					});
				}
				int start=Integer.parseInt(res[1]);
				int end=Integer.parseInt(res[2]);
				
				RangeMap<Integer, Token> subRange = map.subRangeMap(Range.closedOpen(start, end));
				if(subRange.getEntry(start).getKey().lowerEndpoint()!=start)
					throw new IllegalStateException("'"+line+"' does not start on tokens");
				if(subRange.getEntry(end-1).getKey().upperEndpoint()!=end)
					throw new IllegalStateException("'"+line+"' does not end on tokens");
				
				for(Token t:subRange.asMapOfRanges().values()) {
					action.accept(t);
				}
			}
			for(Token[] l:lines) {
				if(Arrays.stream(l).anyMatch(t -> t.getExpectedTag()==null))
					System.err.println("Line '"+Arrays.toString(l)+"' has missing tags");
				if(Arrays.stream(l).noneMatch(t -> t.getExpectedSTag()==STag.COLLOQUIAL_NAME))
					System.err.println("Line '"+Arrays.toString(l)+"' has no colloquial name");
				gson.toJson(l, Token[].class, out);
				out.newLine();
			}
			
		}
	}
}
