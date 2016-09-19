package de.hpi.companies.algo;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class Parser implements Iterable<String>, Closeable, Iterator<String> {
	private BufferedReader in;
	private String nextLine;
	private Gson gson = new Gson();
	private String attribute;
	
	public Parser(File file, String attribute) throws IOException {
		in=new BufferedReader(new InputStreamReader(new FileInputStream(file),StandardCharsets.UTF_8));
		nextLine=in.readLine();
		this.attribute=attribute;
	}

	public Parser(String file) throws IOException {
		this(new File(file), "name");
	}
	
	public Parser(String file, String attribute) throws IOException {
		this(new File(file), attribute);
	}

	public Iterator<String> iterator() {
		return this;
	}

	public void close() throws IOException {
		in.close();
	}

	public boolean hasNext() {
		return nextLine!=null;
	}

	public String next() {
		try {
			String l=nextLine;
			nextLine=in.readLine();
			
			JsonElement elem=gson.fromJson(l, JsonElement.class);
			return elem.getAsJsonObject().get(attribute).getAsString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
}
