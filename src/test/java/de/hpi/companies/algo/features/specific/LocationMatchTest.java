package de.hpi.companies.algo.features.specific;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import de.hpi.companies.algo.Token;
import de.hpi.companies.algo.Tokenizer;

@RunWith(Parameterized.class)
public class LocationMatchTest {
	
	private LocationMatch lm = new LocationMatch();
	
	@Parameters
    public static Collection<Object[]> data() {
    	return Arrays.asList(new Object[][] {
    		{"Frankfurter Allee", new boolean[]{true, true}},
    		{"Frankfurter Allee 42", new boolean[]{true, true, false, false}},
    		{"Hallo Berlin Dude", new boolean[]{false, true, false}},
    		{"Offenbach Christiansen", new boolean[]{true, false}},
    		{"Erich-Mühsam-Straße 26 Wohnung", new boolean[]{true, true, true, true, true, false, false, false}},
    		{"hanklafat-Straße", new boolean[]{false, false, true}},
        });
    }

    @Parameter
    public String value;

    @Parameter(value = 1)
    public boolean[] expected;

	
	
	
	@Test
	public void testExamples() throws IOException {
		
		assertArrayEquals(expected, test(value));
	}
	
	private boolean[] test(String v) {
		Token[] toks=Tokenizer.tokenize(v);
		lm.calculateFeatures(toks);
		boolean[] res = new boolean[toks.length];
		for(int i=0;i<res.length;i++)
			res[i]=Boolean.valueOf(toks[i].getFeature(lm));
		return res;
	}
}