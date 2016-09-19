package de.hpi.companies.algo.features.specific;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.simmetrics.StringMetric;
import org.simmetrics.builders.StringMetricBuilder;
import org.simmetrics.metrics.CosineSimilarity;
import org.simmetrics.tokenizers.Tokenizers;

import com.github.powerlibraries.io.In;
import com.github.powerlibraries.io.Out;

import Dictionary.DictionaryGenerator;
import Dictionary.DictionaryImplementation;
import Dictionary.LowLevelHashDictionary;
import Measures.Measure;
import Measures.MeasureFactory;
import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.FloatFeature;
import de.hpi.companies.util.DBPediaExtractor;
import utils.Ngram;
import utils.Orderable;

public class SectorMatch2 extends FloatFeature {
	
	private DictionaryGenerator dict;
	private Measure measure;
	private StringMetric similarity = StringMetricBuilder.with(new CosineSimilarity<>()).tokenize(Tokenizers.qGram(3)).build();
	
	public SectorMatch2() {
		LowLevelHashDictionary lDict = new LowLevelHashDictionary(3);
		dict = new DictionaryImplementation(lDict);
		measure=MeasureFactory.create(MeasureFactory.COSINE_SIMILARITY, 0.5);
		try {
			for(String f:new String[]{"sectors.txt", "dbpSectors.txt"}) {
				for(String line : In.resource(SectorMatch2.class, f).withUTF8().readLines()) {
					dict.addTerm(line);
				}
			}
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
		
		//DictionaryReader.createDictionaryFromCorpus("/home/attickid/ned.train",3,d1);
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Set<String> companies = new HashSet<>();
		new DBPediaExtractor("select STR(?l), COUNT(?comp) where {?comp dbo:industry ?ind. ?ind rdfs:label ?l}") {
        	@Override
        	protected void processValue(String[] cols) {
        		if(Integer.parseInt(cols[1])>10)
				companies.add(clean(cleanString(cols[0])));
        	}

			private String clean(String in) {
				return StringUtils.split(in, '(')[0].trim();
			}
        }.startQuery();
        
        Out.file("src/main/resources/de/hpi/companies/algo/features/specific/dbpSectors.txt")
			.withUTF8()
			.writeLines(companies.stream().sorted().iterator());
	}

	@Override
	public void calculateFeatures(Token[] tokens) {
		float[] results = new float[tokens.length];
		Arrays.fill(results, -1);
		List<Token> tokenList = Arrays.asList(tokens);
		for(int i=0;i<tokens.length;i++) {
			for(int last=i+1;last<=tokens.length;last++) {
				String joined = join(tokenList.subList(i, last));
				float best = bestResult(joined);
				for(int j=i;j<last;j++)
					results[j]=Math.max(results[j], best);
			}
		}
		
		for(int i=0;i<tokens.length;i++)
			tokens[i].setFeature(this, results[i]);
	}

	private String join(List<Token> l) {
		StringBuilder sb = new StringBuilder();
		sb.append(l.get(0).getRawForm());
		for(int i=1;i<l.size();i++) {
			if(l.get(i-1).hasWhitespaceAfter())
				sb.append(' ');
			sb.append(l.get(i).getRawForm());
		}
		return sb.toString();
	}
	
	

	private float bestResult(String value) {
		String s=Ngram.preprocessTerm(value, dict.getNgramSize());
        
        //looking for the range in which strings have to be looked up
        int max=measure.max(s,dict.getNgramSize());
        int min=measure.min(s,dict.getNgramSize());
        
        
        //set of matches
        HashSet<Integer> solutions=new HashSet<Integer>();
        
        //iterate through all the posible sizes
        for(int l=min;l<=max;l++){
            //min overlap called t in the paper
            int min_overlap=measure.t(s,dict.getNgramSize(), l);
            
            //look for solutions for strings with size l
            ArrayList<Integer> matches=overlap_nonnaive(s,min_overlap,dict,l);
            
            //add the found solutions to the set of solutions
            for(int i=0;i<matches.size();i++){
                solutions.add(matches.get(i));
            }
        }
        
        //decode solutions, since they are used with id's a translation to strings have to be done :)
        float best =-1;
        for(Integer resultId:solutions) {
        	best=Math.max(best, cosine(dict.getTerm(resultId),"^^"+value+"^^"));
        }
        return best;
	}

	private float cosine(String a, String b) {
		return similarity.compare(a, b);
	}

	@Override
	public String transformToString(Float value) {
		return Integer.toString((int)(value*20));
	}
	
	
	
	/*
     * Implementation of the efficient algorithm given in the paper
     * Searches for matches of size l for string S, whose minmum overlap of ngrams is min_overlap, in the dictionary d
     * @param s string we are looking for
     * @param min_overlap min number of matching ngrams between s and Y, for Y to be a match for S
     * @param d dictionary
     * @param l size of Y, given that Y is a match for S
     * @return the list of Ids which are matches to S of size L and whose min number of common ngrams with S is min_overlap
     */
    private static ArrayList<Integer> overlap_nonnaive(String s, int min_overlap, DictionaryGenerator d, int l){
     //list of matches to return at the end
     ArrayList<Integer> listOfMatches=new ArrayList<Integer>(); 
     
     //list of ngrams of the string S
        ArrayList<String> ngramList=Ngram.splitIntoNGrams(s, d.getNgramSize());
        
        //count of how many times a string has matched, in order to make sure the overlap is higher than min_overlap
       HashMap<Integer,Integer> m=new HashMap<Integer,Integer>();
       
       //order listOfNgrams increnmentally to the least common ngram to the most common
       PriorityQueue<Orderable<ArrayList<Integer>>> queueEntitiesPerNgram=new PriorityQueue<>();
       
       
       for(int i=0;i<ngramList.size();i++){
           ArrayList<Integer> tempListOfTermsForNgramI=d.searchTerm(l, ngramList.get(i));
           Orderable<ArrayList<Integer>> orderable_temp=new Orderable<ArrayList<Integer>>(tempListOfTermsForNgramI,i,tempListOfTermsForNgramI.size());
           queueEntitiesPerNgram.add(orderable_temp); 
       }
       
      
       List<Orderable<ArrayList<Integer>>> queueIntoList = new LinkedList<Orderable<ArrayList<Integer>>>(queueEntitiesPerNgram);
       ArrayList<Orderable<ArrayList<Integer>>> listOfEntitiesPerNgram=new ArrayList<Orderable<ArrayList<Integer>>>(queueIntoList);
            
      int numberOfNgramsForS=Ngram.getNumberOfNgrams(s, d.getNgramSize());
       
       for(int k=0;k<=numberOfNgramsForS-min_overlap;k++){
           for(int z=0;z<listOfEntitiesPerNgram.get(k).getObject().size();z++){
                int currentMatch=listOfEntitiesPerNgram.get(k).getObject().get(z);
                int currentCount=0;
                if(m.containsKey(currentMatch)){
                    currentCount=m.get(currentMatch);
                }
                else{
                    m.put(currentMatch, 0);
                }
                int newValue=currentCount+1;
                m.put(currentMatch,newValue);
                
           }
       
       }
       
      
       
            List<Integer> listOfM_ = new LinkedList<Integer>(m.keySet());
            ArrayList<Integer> listOfM=new ArrayList<Integer>(listOfM_);
            
       for(int k=numberOfNgramsForS-min_overlap+1;k<listOfEntitiesPerNgram.size();k++){
           for(int z=0;z<listOfM.size();z++){
               int currentZ=listOfM.get(z);
               
               //if z is found in the list K
               if(find(currentZ,listOfEntitiesPerNgram.get(k).getObject())){
                   m.put(currentZ,m.get(currentZ)+1);
               }
               if(min_overlap<=m.get(currentZ)){
                   listOfMatches.add(currentZ);
               }
               
           }
       
       }
    
       return listOfMatches;
     
    }
	
    /*
     * Auxiliary Function for looking overlaps.
     * listOfIds has to be ordered since a binarySeach is done to look for currentZ in listOfIds
     * @param currentZ a current Id of an entity
     * @param listOfIds list of entities Ids (ordered listOfIds)
     * @return whether currentZ is in the listOfIds
     */
    private static boolean find(int currentZ, ArrayList<Integer> listOfIds) {
       boolean found=false;
      int result=Arrays.binarySearch(listOfIds.toArray(), (Integer) currentZ);
       if(result>=0){
           found=true;
       }
       return found;
    }
}
