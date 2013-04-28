package swp_compiler_ss13.javabite.parser.grammar;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import swp_compiler_ss13.javabite.parser.grammar.Grammar;
import swp_compiler_ss13.javabite.parser.grammar.SLRAutomaton;
import swp_compiler_ss13.javabite.parser.grammar.Symbol;

public class SLRAutomatonTest {

	SimpleT T_ADD=new SimpleT("+");
	SimpleT T_MUL=new SimpleT("*");
	SimpleT T_OPEN=new SimpleT("(");
	SimpleT T_CLOSE=new SimpleT(")");
	SimpleT T_ID=new SimpleT("id");
	SimpleT T_EOF=new SimpleT("$");
	
	
	
	SimpleNT E=new SimpleNT("E");
	SimpleNT T=new SimpleNT("T");
	SimpleNT F=new SimpleNT("F");
	SimpleNT E_=new SimpleNT("E'");
	
	/**
	 * left-recursive language of drachenbuch (S.244)
	 * Pay atention to the type in the book ( E is F in the grammar at one place)
	 */
	
	final Grammar<SimpleT,SimpleNT> g1=new Grammar<>(E,E_,T_EOF);
	
	private List<Symbol> list(Symbol... syms){
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
		
	}
	
	@Test
	public void testAutomaton(){
		SLRAutomaton<SimpleT, SimpleNT> automaton=new SLRAutomaton<>(g1);
		assertEquals("should have exact this number of states",12,automaton.getNStates());
	}
	
	

}
