package swp_compiler_ss13.javabite.parser.grammar;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
	 * 
	 * @param start Start Symbol of the Grammar
	 */
	public Grammar(NT start,NT additional_start, T word_end) {

		this.artificial_start_symbol=additional_start;
		this.word_end=word_end;
		// add start production
		Set<List<Symbol>> startTrans=new HashSet<>();
		List<Symbol> startProdRight=new LinkedList<>();
		startProdRight.add(start);
		startTrans.add(startProdRight);
		this.productions.put(artificial_start_symbol, startTrans);
	}
	
	
	/**
	 * The structure of productions ( and used to define a Grammar):
	 * A->AsA|AA||B
	 * B->b
	 * is represented as 2 Keys ( A and B).
	 * List of A: 4 elements ( List(A,s,A),List(A,A),List(),List(B) )
	 * List of B: 1 element ( List(b) ) 
	 */
	final Map<NT,Set<List<Symbol>>> productions=new HashMap<>();
	
	/**
	 * pretty self Explainable
	 */
	NT artificial_start_symbol;
	T word_end;
	
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
		Set<List<Symbol>> productionsOfNT=productions.get(nT);
		if (productionsOfNT==null){
			productionsOfNT=new HashSet<>();
			productions.put(nT, productionsOfNT);
		}
		// productionsOfNT!=null and is the right one
		productionsOfNT.add(head);
		Collections.addAll(productionsOfNT, rest);
		// all productions should be inserted now
		return this;
	}
	
	/**
	 * gets the kernel element and returns every possible item in closure of @prod
	 * @param nT the NonTermonal of the production
	 * @param prod the production
	 * @return
	 */
	public Map<NT,Set<Item<T,NT>>> getClosure(NT nT, Item<T,NT> prod){
		Map<NT,Set<Item<T,NT>>> closures=new HashMap<>();
		
		// Since the production itself is in the closure, it can be added
		Set<Item<T,NT>> nTset=new HashSet<>();
		closures.put(nT, nTset);
		
		if (prod.right.isEmpty() || isTerminal(prod.right.get(0))){
			// nothing to do here
		}
		else {
			NT toCheck=(NT)prod.right.get(0);
			Queue<NT> ntq=new LinkedList<>();
			ntq.add(toCheck);
			while (!ntq.isEmpty()){
				toCheck=ntq.poll();
				if (closures.containsKey(toCheck)) continue;
				nTset=new HashSet<>();
				closures.put(toCheck, nTset);
				for (List<Symbol> list : productions.get(toCheck)){
					nTset.add(new Item<T,NT>(new LinkedList<Symbol>(), list));
					if (isNonTerminal(list.get(0))){
						ntq.add((NT)list.get(0));
					}
				}
			}
		}

		return closures;
		
	}
	
	
	/**
	 * just a mapper for getClosure() for multiple productions
	 * @param current the productions you want to have "closured"
	 * @return the closures
	 */
	public Map<NT,Set<Item<T,NT>>> getClosure(Map<NT,Set<Item<T,NT>>> current){
		Map<NT,Set<Item<T,NT>>> res= new HashMap<>();
		for (Entry<NT, Set<Item<T,NT>>> ent : current.entrySet()){
			for (Item<T,NT> prod: ent.getValue()){
				Map<NT,Set<Item<T,NT>>> single_res = getClosure(ent.getKey(),prod);
				res=utils.merge(res, single_res);
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
				strb.append(nt+"\t -> "+prod+"\n");		
			}
		}
		return strb.toString();
	}
	
	/**
	 * Since generics are not available in runtime, we need this little hacky thing 
	 * to identify, whether its a terminal or not.
	 * @param s the symbol
	 * @return if it's an instance of NonTerminal
	 */
	private boolean isNonTerminal(Symbol s){
		return productions.containsKey(s);
	}
	
	/**
	 * Since generics are not available in runtime, we need this little hacky thing 
	 * to identify, whether its a terminal or not.
	 * @param s the symbol
	 * @return if it's an instance of Terminal
	 */
	private boolean isTerminal(Symbol s){
		return !isNonTerminal(s);
	}
	
}
