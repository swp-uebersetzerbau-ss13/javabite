package swp_compiler_ss13.javabite.parser.grammar;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class SLRAutomatonTest {

	
	SimpleT T_ADD=new SimpleT("+");
	SimpleT T_MUL=new SimpleT("*");
	SimpleT T_OPEN=new SimpleT("(");
	SimpleT T_CLOSE=new SimpleT(")");
	SimpleT T_ID=new SimpleT("id");
	SimpleT T_EOF=new SimpleT("$");
	SimpleT T_EPSILON=new SimpleT("[eps]");
	
	
	
	SimpleNT E=new SimpleNT("E");
	SimpleNT T=new SimpleNT("T");
	SimpleNT F=new SimpleNT("F");
	SimpleNT E_=new SimpleNT("E'");
	
	/**
	 * left-recursive language of drachenbuch (S.244)
	 * Pay atention to the type in the book ( E is F in the grammar at one place)
	 */
	
	final Grammar<SimpleT,SimpleNT> g1=new Grammar<>(E,E_,T_EOF,T_EPSILON);
	final Grammar<SimpleT,SimpleNT> g2=new Grammar<>(E,E_,T_EOF,T_EPSILON);
	
	private List<Symbol> list(Symbol... syms){
		return Arrays.asList(syms);
	}
	private List<SimpleT> listTok(SimpleT... syms){
		return Arrays.asList(syms);
	}
	
	
	@Before
	public void setup(){
		g1.addProduction(E, 
				list(E,T_ADD,T),
				list(T));
		g1.addProduction(T,
				list(T,T_MUL,F),
				list(F)
				);
		g1.addProduction(F, 
				list(T_OPEN,E,T_CLOSE),
				list(T_ID));
		
		g2.addProduction(E, list(E,F), list(T_EPSILON));
		g2.addProduction(F,list(T_MUL));
		
	}
	
	@Test
	public void testAutomaton(){
		SLRAutomaton<SimpleT, SimpleNT> automaton=new SLRAutomaton<>(g1);
		
		assertEquals("should have exact this number of states",13,automaton.getNStates());
	}
	
	@Test
	public void testDerivationSequenceSimple(){
		SLRAutomaton<SimpleT, SimpleNT> automaton=new SLRAutomaton<>(g1);
		
		Word<SimpleT,SimpleNT> original=new Word<SimpleT,SimpleNT>(g1, listTok(T_ID));
		List<Production<SimpleT,SimpleNT>> productions=automaton.getDerivationASsSequence(original);
		Collections.reverse(productions);
		Word<SimpleT,SimpleNT> derivated=Word.getWordFromRightMostDerivation(g1, productions); 
		assertEquals("must be the same",original,derivated);
	}
	
	@Test
	public void testDerivationSequenceComplex(){
		SLRAutomaton<SimpleT, SimpleNT> automaton=new SLRAutomaton<>(g1);
		Word<SimpleT,SimpleNT> original=new Word<SimpleT,SimpleNT>(g1, listTok(T_ID,T_MUL,T_ID));
		List<Production<SimpleT,SimpleNT>> productions=automaton.getDerivationASsSequence(original);
		Collections.reverse(productions);
		Word<SimpleT,SimpleNT> derivated=Word.getWordFromRightMostDerivation(g1, productions); 
		assertEquals("must be the same",original,derivated);
	}
	
	@Test
	public void testDerivationSequenceVeryComplex(){
		SLRAutomaton<SimpleT, SimpleNT> automaton=new SLRAutomaton<>(g1);
		Word<SimpleT,SimpleNT> original=new Word<SimpleT,SimpleNT>(g1, listTok(T_ID,T_MUL,T_ID,T_ADD,T_ID,T_MUL,T_OPEN,T_ID,T_ADD,T_ID,T_CLOSE,T_ADD,T_ID));
		List<Production<SimpleT,SimpleNT>> productions=automaton.getDerivationASsSequence(original);
		Collections.reverse(productions);
		Word<SimpleT,SimpleNT> derivated=Word.getWordFromRightMostDerivation(g1, productions); 
		assertEquals("must be the same",original,derivated);
	}
	
	@Test
	public void testDerivationSequenceSimpleBrace(){
		SLRAutomaton<SimpleT, SimpleNT> automaton=new SLRAutomaton<>(g1);
		Word<SimpleT,SimpleNT> original=new Word<SimpleT,SimpleNT>(g1, listTok(T_OPEN,T_ID,T_CLOSE));
		List<Production<SimpleT,SimpleNT>> productions=automaton.getDerivationASsSequence(original);
		Collections.reverse(productions);
		Word<SimpleT,SimpleNT> derivated=Word.getWordFromRightMostDerivation(g1, productions); 
		assertEquals("must be the same",original,derivated);
	}
	
	@Test
	public void testDerivationSequenceSimpleBraceLeftMul(){
		SLRAutomaton<SimpleT, SimpleNT> automaton=new SLRAutomaton<>(g1);
		Word<SimpleT,SimpleNT> original=new Word<SimpleT,SimpleNT>(g1, listTok(T_ID,T_MUL,T_OPEN,T_ID,T_CLOSE));
		List<Production<SimpleT,SimpleNT>> productions=automaton.getDerivationASsSequence(original);
		Collections.reverse(productions);
		Word<SimpleT,SimpleNT> derivated=Word.getWordFromRightMostDerivation(g1, productions); 
		assertEquals("must be the same",original,derivated);
	}
	
	@Test
	public void testDerivationSequenceSimpleBraceRightMul(){
		SLRAutomaton<SimpleT, SimpleNT> automaton=new SLRAutomaton<>(g1);
		Word<SimpleT,SimpleNT> original=new Word<SimpleT,SimpleNT>(g1, listTok(T_OPEN,T_ID,T_CLOSE,T_MUL,T_ID));
		List<Production<SimpleT,SimpleNT>> productions=automaton.getDerivationASsSequence(original);
		Collections.reverse(productions);
		Word<SimpleT,SimpleNT> derivated=Word.getWordFromRightMostDerivation(g1, productions); 
		assertEquals("must be the same",original,derivated);
	}
	
	@Test
	public void testDerivationSequenceSimpleBraceRightDouble(){
		SLRAutomaton<SimpleT, SimpleNT> automaton=new SLRAutomaton<>(g1);
		Word<SimpleT,SimpleNT> original=new Word<SimpleT,SimpleNT>(g1, listTok(T_OPEN,T_OPEN,T_ID,T_CLOSE,T_CLOSE));
		List<Production<SimpleT,SimpleNT>> productions=automaton.getDerivationASsSequence(original);
		Collections.reverse(productions);
		Word<SimpleT,SimpleNT> derivated=Word.getWordFromRightMostDerivation(g1, productions); 
		assertEquals("must be the same",original,derivated);
	}
	
	@Test
	public void testDerivationEpsilon(){
		SLRAutomaton<SimpleT, SimpleNT> automaton=new SLRAutomaton<>(g2);
		Word<SimpleT,SimpleNT> original=new Word<SimpleT,SimpleNT>(g2, listTok());
		List<Production<SimpleT,SimpleNT>> productions=automaton.getDerivationASsSequence(original);
		Collections.reverse(productions);
		Word<SimpleT,SimpleNT> derivated=Word.getWordFromRightMostDerivation(g2, productions); 
		assertEquals("must be the same",original,derivated);
	}
	
	@Test
	public void testDerivationEpsilonOneProd(){
		SLRAutomaton<SimpleT, SimpleNT> automaton=new SLRAutomaton<>(g2);
		Word<SimpleT,SimpleNT> original=new Word<SimpleT,SimpleNT>(g2, listTok(T_MUL));
		List<Production<SimpleT,SimpleNT>> productions=automaton.getDerivationASsSequence(original);
		Collections.reverse(productions);
		Word<SimpleT,SimpleNT> derivated=Word.getWordFromRightMostDerivation(g2, productions); 
		assertEquals("must be the same",original,derivated);
	}
	
	@Test
	public void testDerivationEpsilonMultipleProd(){
		SLRAutomaton<SimpleT, SimpleNT> automaton=new SLRAutomaton<>(g2);
		Word<SimpleT,SimpleNT> original=new Word<SimpleT,SimpleNT>(g2, listTok(T_MUL,T_MUL,T_MUL,T_MUL,T_MUL,T_MUL,T_MUL));
		List<Production<SimpleT,SimpleNT>> productions=automaton.getDerivationASsSequence(original);
		Collections.reverse(productions);
		Word<SimpleT,SimpleNT> derivated=Word.getWordFromRightMostDerivation(g2, productions); 
		assertEquals("must be the same",original,derivated);
	}
	
	

}
