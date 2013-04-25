package swp_compiler_ss13.javabite.grammar;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Grammar object determines (surprisingly) a Grammar.
 * Plus, some methods support an easy LR-parsing of it.
 * @author Till
 *
 * @param <T> NonTerminals 
 * @param <NT> Terminals
 */
public class Grammar<T extends Symbol,NT extends Symbol> {
	
	
	/**
	 * Represents the handles used to create the LR-Parser-Automaton
	 * A->As.A ( Handle is "As") is represented as:
	 * nT: A
	 * left: List(A,s)
	 * right: List(A)
	 * 
	 * @author Till
	 *
	 */
	class Item{
		private Item(NT nT, List<Symbol> left, List<Symbol> right) {
			this.nT = nT;
			this.left = left;
			this.right = right;
		}
		NT nT;
		List<Symbol> left;
		List<Symbol> right;
	}
	
	class Automaton{
		
	}
	
	/**
	 * The structure of productions ( and used to define a Grammar):
	 * A->AsA|AA||B
	 * B->b
	 * is represented as 2 Keys ( A and B).
	 * List of A: 4 elements ( List(A,s,A),List(A,A),List(),List(B) )
	 * List of B: 1 element ( List(b) ) 
	 */
	final Map<NT,List<List<Symbol>>> productions=new HashMap<>();
	
	/**
	 * pretty self Explainable
	 */
	NT start_symbol;
	
	Logger logger=LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Adds a production or productions 
	 * @param nT The NonTerminal
	 * @param head the first production
	 * @param rest other productions (optional) to simple determining multiple productions
	 * with the same NonTerminal 
	 * @return
	 */
	@SafeVarargs
	final Grammar<T, NT> addProduction(NT nT, List<Symbol> head, List<Symbol>... rest){
		// get List for productions
		List<List<Symbol>> productionsOfNT=productions.get(nT);
		if (productionsOfNT==null){
			productionsOfNT=new LinkedList<>();
			productions.put(nT, productionsOfNT);
		}
		// productionsOfNT!=null and is the right one
		productionsOfNT.add(head);
		Collections.addAll(productionsOfNT, rest);
		// all productions should be inserted now
		logger.info("Added "+(rest.length+1)+" productions for NonTerminal "+nT);
		return this;
	}
	
	/**
	 * gets the kernel element and returns every possible item in closure of @prod
	 * @param nT the NonTermonal of the production
	 * @param prod the production
	 * @return
	 */
	Map<NT,Set<Item>> getClosure(NT nT, Item prod){
		// TODO: IMPLEMENT ME!
		return null;
	}
	
	/**
	 * merges two sets 
	 * @param a a set
	 * @param b a set
	 * @return the union of a and b with respect to the given data structure
	 */
	Map<NT,Set<Item>> merge(Map<NT,Set<Item>> a, Map<NT,Set<Item>> b){
		Map<NT,Set<Item>> res= new HashMap<>();
		res.putAll(a);
		// for all in b
		for (Entry<NT, Set<Item>> ent : b.entrySet()){
			// a doesn't contain, just add
			if (!res.containsKey(ent.getKey())){
				res.put(ent.getKey(),ent.getValue());
			}
			// a contains, merge
			else{
				res.get(ent.getKey()).addAll(ent.getValue());
			}
		}
		return res;
	}
	
	/**
	 * just a mapper for getClosure() for multiple productions
	 * @param current the productions you want to have "closured"
	 * @return the closures
	 */
	Map<NT,Set<Item>> getClosure(Map<NT,Set<Item>> current){
		Map<NT,Set<Item>> res= new HashMap<>();
		for (Entry<NT, Set<Item>> ent : current.entrySet()){
			for (Item prod: ent.getValue()){
				Map<NT,Set<Item>> single_res = getClosure(ent.getKey(),prod);
				res=merge(res, single_res);
			}
		}
		return res;
	}
	
	
	/**
	 * Just returns the productions added so far
	 */
	public String toString(){
		StringBuilder strb=new StringBuilder();
		for(NT nt: productions.keySet()){
			for (List<Symbol> prod : productions.get(nt)){
				strb.append(nt+"\t -> "+prod);
				strb.append(nt+"\t -> "+prod);		
			}
		}
		return strb.toString();
	}
}
