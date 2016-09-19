package de.hpi.companies.util;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;

import com.github.powerlibraries.io.In;

public abstract class DBPediaExtractor {

	private String query;
	private boolean foundSomething;
	private int maxOffset;
	
	public DBPediaExtractor(String query, int maxOffset) {
		this.query=query;
		this.maxOffset=maxOffset;
	}
	
	public DBPediaExtractor(String query) {
		this(query, Integer.MAX_VALUE);
	}
	
	protected abstract void processValue(String[] cols);
	
	public void startQuery() throws IOException, InterruptedException {
		
		foundSomething=true;
		for(int i=0;foundSomething && i<maxOffset;i++) {
			URL url = new URL("http://dbpedia.org/sparql?default-graph-uri=http%3A%2F%2Fdbpedia.org&query="+URLEncoder.encode(query)+"+OFFSET+"+(10000*i)+"+LIMIT+10000&format=text%2Ftab-separated-values&timeout=30000&debug=on");
			System.out.println(i);
	        URLConnection yc = url.openConnection();
	        yc.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
	        yc.setRequestProperty("Referrer","http://dbpedia.org/sparql");
	   		yc.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64; rv:36.0) Gecko/20100101 Firefox/36.0");
	   		int error=0;
	   		while(error>=0 && error <8) {
	   			foundSomething=false;
		   		try {
			   		In.stream(yc.getInputStream()).withUTF8().streamLines().skip(1).forEach( l -> {
				        	processValue(StringUtils.split(l, '\t'));
				        	foundSomething=true;
			        });
			   		error=-1;
		   		}
			   	catch(IOException e) {
			   		System.err.println(url);
			   		e.printStackTrace();
			   		error++;
		   		}
		   		Thread.sleep(1000);
	   		}
		}
	}

	protected String cleanString(String str) {
		return str.substring(1, str.length()-1).replace("\\\"", "\"");
	}

}