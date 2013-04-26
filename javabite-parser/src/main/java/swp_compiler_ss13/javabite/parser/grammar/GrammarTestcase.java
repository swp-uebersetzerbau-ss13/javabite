package swp_compiler_ss13.javabite.parser.grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class GrammarTestcase {
	
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
	public void testIfCompiles(){
		assertTrue(true);
	}
	
	@Test
	public void testProductionCorrectnessNum(){
		assertTrue("There must be 2 NonTerminals ( incl. artificial start)", g1.productions.size()==4);
	}
	
	@Test
	public void testProductionCorrectnessContant(){
		assertTrue("There must exist 4 productions",g1.productions.get(E).size()==2);
		assertTrue("There must exist 2 productions",g1.productions.get(T).size()==2);
	}
	
	@Test
	public void testCorrectClosureI7(){
		Map<SimpleNT,Set<Item<SimpleT,SimpleNT>>> res= g1.getClosure(T, new Item<SimpleT,SimpleNT>(
				list(T,T_MUL), list(F)
				));
		assertTrue("There should be one transition",res.get(T).size()==0);
		assertTrue("There should be two transitions",res.get(F).size()==2);
		assertTrue("There should be no transition",res.get(E)==null);
	}
	
	@Test
	public void testCorrectClosureI0(){
		Map<SimpleNT,Set<Item<SimpleT,SimpleNT>>> res= g1.getClosure(E_, new Item<SimpleT,SimpleNT>(
				list(), list(E)
				));
		assertTrue("There should be transitions for 4 NTs",res.size()==4);
		assertTrue("There should be one transition",res.get(E_).size()==0);
		assertTrue("There should be two transition",res.get(E).size()==2);
		assertTrue("There should be two transition",res.get(F).size()==2);
		assertTrue("There should be two transition",res.get(T).size()==2);	
	}
	
	@Test
	public void testCorrectClosureI11(){
		Map<SimpleNT,Set<Item<SimpleT,SimpleNT>>> res= g1.getClosure(F, new Item<SimpleT,SimpleNT>(
				list(T_OPEN,E,T_CLOSE), list()
				));
		
		assertTrue("There should be no additional NT",res.size()==1);
	}
	
	
	@Test
	public void testAutomaton(){
		SLRAutomaton<SimpleT, SimpleNT> automaton=new SLRAutomaton<>(g1);
		assertEquals("should have exact this number of states",12,automaton.getNStates());
	}
	

}

class SimpleNT implements Symbol{
	@Override
	public String toString() {
		return id;
	}

	public SimpleNT(String id) {
		super();
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleNT other = (SimpleNT) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	String id;
}
class SimpleT implements Symbol{
	@Override
	public String toString() {
		return id ;
	}

	public SimpleT(String id) {
		super();
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleT other = (SimpleT) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	String id;
}
