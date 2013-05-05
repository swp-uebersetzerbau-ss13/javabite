package swp_compiler_ss13.javabite.parser.grammar;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;

import swp_compiler_ss13.javabite.parser.grammar.Grammar;
import swp_compiler_ss13.javabite.parser.grammar.Item;
import swp_compiler_ss13.javabite.parser.grammar.Symbol;

public class GrammarTestcase {
	
	// for both g1 and g2
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
	
	// just for g2
	SimpleNT EM=new SimpleNT("E'");
	SimpleNT TM=new SimpleNT("T'");
	SimpleNT S=new SimpleNT("S");
	
	
	Logger logger=LoggerFactory.getLogger(this.getClass());
	
	
	
	
	/**
	 * left-recursive language of drachenbuch (S.244)
	 * Pay atention to the type in the book ( E is F in the grammar at one place)
	 */
	
	final Grammar<SimpleT,SimpleNT> g1=new Grammar<>(E,E_,T_EOF,T_EPSILON);
	/**
	 * as postfix:
	 * http://www.cs.uaf.edu/~cs331/notes/FirstFollow.pdf
	 */
	final Grammar<SimpleT,SimpleNT> g2=new Grammar<>(E,S,T_EOF,T_EPSILON);
	
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
		
		
		g2.addProduction(E, list(T,EM));
		g2.addProduction(EM, list(T_ADD,T,EM),list(T_EPSILON));
		g2.addProduction(T, list(F,TM));
		g2.addProduction(TM, list(T_MUL,F,TM),list(T_EPSILON));
		g2.addProduction(F, list(T_OPEN,E,T_CLOSE),list(T_ID));
		
	}
	
	@Test
	public void testIfCompiles(){
		assertTrue(true);
	}
	
	@Test
	public void testProductionCorrectnessNum(){
		assertTrue("There must be 2 NonTerminals ( incl. artificial start)", g1.getProductions().size()==4);
	}
	
	@Test
	public void testProductionCorrectnessContant(){
		assertTrue("There must exist 4 productions",g1.getProductions().get(E).size()==2);
		assertTrue("There must exist 2 productions",g1.getProductions().get(T).size()==2);
	}
	
	
	@Test
	public void testCorrectClosureI7(){
		Map<SimpleNT,Set<Item<SimpleT,SimpleNT>>> param=new HashMap<>();
		Set<Item<SimpleT,SimpleNT>> set=new HashSet<>();
		Item<SimpleT,SimpleNT> it= new Item<SimpleT,SimpleNT>(list(T,T_MUL), list(F));
		set.add(it);
		param.put(T, set);
		
		Map<SimpleNT,Set<Item<SimpleT,SimpleNT>>> res= g1.getClosure(param);
		assertTrue(res.get(F).contains(new Item<>(list(), list(T_OPEN,E,T_CLOSE))));
		assertTrue(res.get(F).contains(new Item<>(list(), list(T_ID))));
		assertEquals("same size of result", 3,Utils.countItemsRecursive(res));
	}

	
	@Test
	public void testCorrectClosureI4(){
		Map<SimpleNT,Set<Item<SimpleT,SimpleNT>>> param=new HashMap<>();
		Set<Item<SimpleT,SimpleNT>> set=new HashSet<>();
		Item<SimpleT,SimpleNT> it= new Item<SimpleT,SimpleNT>(list(T_OPEN), list(E,T_CLOSE));
		set.add(it);
		param.put(F, set);
		
		Map<SimpleNT,Set<Item<SimpleT,SimpleNT>>> res= g1.getClosure(param);
		assertTrue(res.get(E).contains(new Item<>(list(), list(E,T_ADD,T))));
		assertTrue(res.get(E).contains(new Item<>(list(), list(T))));
		assertTrue(res.get(T).contains(new Item<>(list(), list(T,T_MUL,F))));
		assertFalse(res.get(T).contains(new Item<>(list(), list(T,F))));
		assertTrue(res.get(T).contains(new Item<>(list(), list(F))));
		assertTrue(res.get(F).contains(new Item<>(list(), list(T_OPEN,E,T_CLOSE))));
		assertTrue(res.get(F).contains(new Item<>(list(), list(T_ID))));
		assertEquals("same size of result", 7,Utils.countItemsRecursive(res));
		assertEquals("should be untouched", 1,Utils.countItemsRecursive(param));
	}
	
	
	@Test
	public void testFirstSetG1(){
		Set<SimpleT> first= g1.getFirstSet(E);
		assertTrue("Must contains at least this element",first.contains(T_OPEN));
	}
	
	@Test
	public void testFollowSetG1(){
		Set<SimpleT> follow= g1.getFollowSet(E);
		assertTrue("Must contains at least this element",follow.contains(T_CLOSE));
	}
	
	@Test
	public void testFirstSetG2(){
		Set<SimpleT> firstE= g2.getFirstSet(E);
		Set<SimpleT> firstF= g2.getFirstSet(F);
		Set<SimpleT> firstT= g2.getFirstSet(T);
		Set<SimpleT> firstEM= g2.getFirstSet(EM);
		Set<SimpleT> firstTM= g2.getFirstSet(TM);
		
		assertEquals(firstE, new HashSet(list(T_OPEN,T_ID)));
		assertEquals(firstE,firstF);
		assertEquals(firstE,firstT);
		
		assertEquals(firstEM, new HashSet(list(T_ADD,T_EPSILON)));
		assertEquals(firstTM, new HashSet(list(T_MUL,T_EPSILON)));
		
	}
	
	@Test
	public void testFollowSetG2(){
		Set<SimpleT> followE= g2.getFollowSet(E);
		assertEquals(new HashSet(list(T_CLOSE,T_EOF)),followE);
		
		Set<SimpleT> followEM= g2.getFollowSet(EM);
		Set<SimpleT> followT= g2.getFollowSet(T);
		Set<SimpleT> followTM= g2.getFollowSet(TM);
		Set<SimpleT> followF= g2.getFollowSet(F);
		
		assertEquals(followE, followEM);
		assertEquals(new HashSet(list(T_CLOSE,T_EOF,T_ADD)),followT);
		assertEquals(followT, followTM);
		assertEquals(new HashSet(list(T_CLOSE,T_EOF,T_ADD,T_MUL)),followF);
		
		
	}
	

}


