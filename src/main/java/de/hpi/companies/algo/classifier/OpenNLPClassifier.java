package de.hpi.companies.algo.classifier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.primitives.Doubles;

import de.hpi.companies.algo.Token;
import opennlp.model.AbstractModel;
import opennlp.model.Event;
import opennlp.model.EventStream;
import opennlp.model.TwoPassDataIndexer;
import opennlp.perceptron.BinaryPerceptronModelReader;
import opennlp.perceptron.BinaryPerceptronModelWriter;
import opennlp.perceptron.PerceptronTrainer;

public class OpenNLPClassifier<T> extends AClassifier<T> implements ProbabilityReturner {

	private static final boolean DEBUG = false; 
	public static enum Algorithm {GIS, PERCEPTRON, QN};
	
	private AbstractModel model;
	private double[] probs;

	/** only for serialization **/
	public OpenNLPClassifier() {this(null);};
	public OpenNLPClassifier(TagExtractor<T> tagExtractor) {
		super(tagExtractor);
	}
	
	@Override
	public void train(Collection<Token[]> names) throws IOException {
		PerceptronTrainer trainer=new PerceptronTrainer();
		if(!DEBUG) {
			try {
				Field f = trainer.getClass().getDeclaredField("printMessages");
				f.setAccessible(true);
				f.set(trainer, false);
			}catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
			
		model = trainer.trainModel(100, new TwoPassDataIndexer(
					new TokenStream(names, t -> 
										new Event(
												getTag(t).toString(), 
												Arrays.stream(getFeatureManager().getFeatureArray(t)).map(cf->cf.getSimplified()).toArray(String[]::new)
										)
					)
				),0);
	}

	
	private static class TokenStream implements EventStream {
		
		private Iterator<Token> tokenStream;
		private Function<Token, Event> eventMapper;
		private Event next;
		private boolean fetched = false;

		public TokenStream(Collection<Token[]> names, Function<Token, Event> eventMapper) throws IOException {
			this.eventMapper=eventMapper;
			tokenStream = names.stream().flatMap( ts -> Arrays.stream(ts)).iterator();
		}

		@Override
		public Event next() throws IOException {
			Event e = fetch();
			fetched = false;
			next = null;
			return e;
		}

		private Event fetch() {
			if(fetched)
				return next;
			else {
				fetched=true;
				next = search();
				return next;
			}
		}

		private Event search() {
			if(tokenStream.hasNext()) {
				Token t=tokenStream.next();
				if(t.getExpectedTag()!=null) {
					Event e=eventMapper.apply(t);
					if(ArrayUtils.contains(e.getContext(), null))
						throw new NullPointerException(Arrays.toString(e.getContext()));
					return e;
				}
				else
					return search();
			}
			else
				return null;
		}

		@Override
		public boolean hasNext() throws IOException {
			return fetch()!=null;
		}
		
	}
	
	@Override
	public List<T> getTags(Token[] tokens) {
		List<T> res= new ArrayList<T>(tokens.length);
		probs = new double[tokens.length];
		for(int i=0;i<tokens.length;i++) {
			String[] features = Arrays.stream(getFeatureManager().getFeatureArray(tokens[i])).map(cf->cf.getSimplified()).toArray(String[]::new);
			if(ArrayUtils.contains(features, null))
				throw new NullPointerException(Arrays.toString(features));
			double[] outcome = model.eval(features);
			probs[i]=Doubles.max(outcome);
			res.add(valueOf(model.getBestOutcome(outcome)));
		}
		return res;
	}
	
	@Override
	public double[] getLastProbabilities() {
		return probs;
	}
	
	@Override
	public <NT> AClassifier<NT> createClassifier(TagExtractor<NT> ex) {
		return new OpenNLPClassifier<NT>(ex);
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BinaryPerceptronModelWriter writer = new BinaryPerceptronModelWriter(model, new DataOutputStream(baos));
			writer.persist();
			writer.close();
			byte[] bytes = baos.toByteArray();
			System.out.println("Maxent model is "+bytes.length+" bytes long");
			out.writeInt(bytes.length);
			out.write(bytes);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		try {
			byte[] bytes = new byte[in.readInt()];
			in.readFully(bytes);
			BinaryPerceptronModelReader reader = new BinaryPerceptronModelReader(new DataInputStream(new ByteArrayInputStream(bytes)));
			reader.checkModelType();
			model = reader.constructModel();
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
