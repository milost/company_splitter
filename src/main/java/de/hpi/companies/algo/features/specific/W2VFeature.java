package de.hpi.companies.algo.features.specific;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.VocabWord;

import com.github.powerlibraries.io.In;
import com.github.powerlibraries.io.Out;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.features.StringFeature;

public class W2VFeature extends StringFeature {
	public static void main(String[] args) throws IOException {
		/*
		List<String> names=new ArrayList<>(700000);
		try(Parser p = new Parser("../data/companies.json")) {
			while(p.hasNext()) {
				names.add(p.next());
			}
		}
		Tokenizer myTokenizer=new Tokenizer();
		SentenceIterator sentences = new CollectionSentenceIterator(names);
		sentences.setPreProcessor(new SentencePreProcessor() {
			@Override
			public String preProcess(String sentence) {
				return Arrays.stream(myTokenizer.tokenize(sentence))
						.map(t->t.getRawForm().toLowerCase())
						.reduce((a,b)->a+" "+b).get();
				
			}
		});
		
		TokenizerFactory tokenizer = new DefaultTokenizerFactory();
		
		Word2Vec vec = new Word2Vec.Builder()
	            .batchSize(50) //# words per minibatch.
	            .minWordFrequency(5) // 
	            .useAdaGrad(false) //
	            .layerSize(200) // word feature vector size
	            .iterations(1) // # iterations to train
	            .learningRate(0.025) // 
	            .minLearningRate(1e-3) // learning rate decays wrt # words. floor learning
	            .negativeSample(10) // sample size 10 words
	            .iterate(sentences) //
	            .negativeSample(0)
	            .tokenizerFactory(tokenizer)
	            .build();
	    vec.fit();
	    
	    */
		WordVectors vec = WordVectorSerializer.loadTxtVectors(new File("src/main/resources/de/hpi/companies/algo/features/specific/w2v.txt"));
		
		List<Word> words=new ArrayList<>();
		Collection<VocabWord> vocab=vec.vocab().vocabWords();
		for(VocabWord w:vocab) {
			String word=w.getLabel();
			words.add(new Word(word, vec.getWordVector(word)));
		}
		List<CentroidCluster<Word>> cluster = new KMeansPlusPlusClusterer<Word>(900, -1, (a,b) -> {
			double p=0;
			double as=0;
			double bs=0;
			for(int i=0;i<a.length;i++) {
				p+=a[i]*b[i];
				as+=a[i]*a[i];
				bs+=b[i]*b[i];
			}
			return p/(Math.sqrt(as*bs));
		}).cluster(words);
		//List<Cluster<Word>> cluster = new PDBSCANClusterer<Word>(1, 3).cluster(words);
		Out.file("src/main/resources/de/hpi/companies/algo/features/specific/cluster.bin").writeObject(cluster);
		for(Cluster<Word> c:cluster) {
			System.out.println("\n\n\nCLUSTER");
			for(Word w:c.getPoints())
				System.out.println("\t"+w.word);
		}
	    //WordVectorSerializer.writeWordVectors(vec, "src/main/resources/de/hpi/companies/algo/features/specific/w2v.txt");
	}
	
	private static class Word implements Clusterable, Serializable {

		private String word;
		private double[] vector;

		public Word(String word, double[] vector) {
			this.word=word;
			this.vector=vector;
		}

		@Override
		public double[] getPoint() {
			return vector;
		}
		
	}

	private HashMap<String, Integer> clusterMap=new HashMap<>();
	
	public W2VFeature() {
		try {
			List<Cluster<Word>> cluster = In.resource(W2VFeature.class, "cluster.bin").readObject();
			Integer id=Integer.valueOf(0);
			for(Cluster<Word> c:cluster) {
				for(Word w:c.getPoints()) {
					clusterMap.put(w.word, id);
				}
				id++;
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void calculateFeatures(Token[] tokens) {
		for(Token t:tokens) {
			Integer id = clusterMap.get(t.getRawForm().toLowerCase());
			if(id!=null)
				t.setFeature(this, id.toString());
			else
				t.setFeature(this, "NONE");
		}
	}

}
