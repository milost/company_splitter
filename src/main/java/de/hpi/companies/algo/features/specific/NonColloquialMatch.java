package de.hpi.companies.algo.features.specific;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.xml.sax.SAXException;

import com.github.powerlibraries.io.Out;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import de.hpi.companies.algo.Tokenizer;
import de.hpi.companies.algo.features.ListMatch;
import de.hpi.companies.util.DBPediaExtractor;
import info.bliki.wiki.dump.IArticleFilter;
import info.bliki.wiki.dump.Siteinfo;
import info.bliki.wiki.dump.WikiArticle;
import info.bliki.wiki.dump.WikiXMLParser;
import info.bliki.wiki.model.AbstractWikiModel;
import info.bliki.wiki.namespaces.INamespace;

public class NonColloquialMatch extends ListMatch {
	public static void main(String[] args) throws IOException, SAXException, InterruptedException {
		File path = new File("C:/Users/Virenerus/Downloads");
		Multimap<String, String> alternativeNames = HashMultimap.create();
		Visitor visitor =new Visitor(alternativeNames);
		new WikiXMLParser(new File(path, "dewiki-20160501-pages-articles-multistream.xml.bz2"), visitor).parse();
        visitor.done();
		
		Out.file("wiki_links.bin").writeObject(alternativeNames);
		System.out.println("stored links");
		
		HashSet<String> companies = new HashSet<>();
        new DBPediaExtractor("select DISTINCT STR(?l) where {?comp rdf:type dbo:Company. ?comp rdfs:label ?l.FILTER ( lang(?l) = \"de\" )}") {
        	@Override
        	protected void processValue(String[] cols) {
        		companies.add(cleanString(cols[0]));
        	}
        }.startQuery();
        
        Out.file("companies.bin").writeObject(companies);
        System.out.println("stored companies");
        
        Tokenizer tok = new Tokenizer();
        
        Multiset<String> counts = HashMultiset.create();
        for(String comp:companies) {
        	String[] compParts = Arrays.stream(tok.tokenize(comp)).map(t->t.getRawForm()).toArray(String[]::new);
        	for(String v:alternativeNames.get(comp)) {
        		String[] vParts = Arrays.stream(tok.tokenize(v)).map(t->t.getRawForm()).toArray(String[]::new);
        		counts.addAll(compare(compParts,vParts));
        		counts.addAll(compare(vParts,compParts));
        	}
        }
        
        Out.file("src/main/resources/de/hpi/companies/algo/features/specific/nonColloquial2.txt").withUTF8().writeObject(counts);
	}

	private static Set<String> compare(String[] a, String[] b) {
		HashSet<String> set=new HashSet<>();
		int j=0;
		for(int i=0;i<a.length;i++) {
			if(j==b.length || !a[i].equalsIgnoreCase(b[j]))
				set.add(a[i]);
			else
				j++;
		}
		if(j==b.length)
			return set;
		else
			return Collections.emptySet();
	}

	public static class Visitor implements IArticleFilter {
		
		private GenericObjectPool<AbstractWikiModel> model = new GenericObjectPool<>(new BasePooledObjectFactory<AbstractWikiModel>() {

			@Override
			public AbstractWikiModel create() throws Exception {
				return new AbstractWikiModel() {
					
					
					public void appendInternalLink(String topic, String hashSection, String topicDescription, String cssClass, boolean parseRecursive) {
						if(!topicDescription.equalsIgnoreCase(topic) && !topic.startsWith("Kategorie:") && !topic.startsWith("Datei:"))
							alternativeNames.put(clean(topic), clean(topicDescription));
					}
					
					private String clean(String topic) {
						return StringUtils.split(topic, '(')[0].trim();
					}


					@Override
					public Set<String> getLinks() {
						return Collections.emptySet();
					}

					@Override
					public INamespace getNamespace() {
						return fNamespace;
					}

					@Override
					public void parseInternalImageLink(String imageNamespace, String rawImageLink) {};
				};
			}

			@Override
			public PooledObject<AbstractWikiModel> wrap(AbstractWikiModel obj) {
				return new DefaultPooledObject<AbstractWikiModel>(obj);
			}
		});
		private Multimap<String, String> alternativeNames;
		private ThreadPoolExecutor pool;
		
		public Visitor(Multimap<String, String> alternativeNames) {
			this.alternativeNames=alternativeNames;
			pool = new ThreadPoolExecutor(8, 8,
                    0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(20),
                    new ThreadPoolExecutor.CallerRunsPolicy());
		}

		public void done() throws InterruptedException {
			pool.shutdown();
			pool.awaitTermination(2, TimeUnit.HOURS);
		}

		private int counter=0;
		
		
		@Override
		public void process(WikiArticle article, Siteinfo siteinfo) throws IOException {
			if(article.isMain()) {
				try {
					pool.execute(()-> {
						try {
							AbstractWikiModel m=model.borrowObject();
							m.render(article.getText());
							model.returnObject(m);
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
					counter++;
					if(counter%1000==0)
						System.out.println(counter);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
