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

/**
 * A Grammar object determines (surprisingly) a Grammar. Plus, some methods
 * support an easy LR-parsing of it.
 * 
 * @author Till
 * 
 * @param <T>
 *            Terminals
 * @param <NT>
 *            Non Terminals
 */
public class Grammar<T extends Symbol, NT extends Symbol> {
	NT artificial_start_symbol;
	T epsilonSymbol; // eps-sym
	Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * The structure of productions ( and used to define a Grammar):
	 * A->AsA|AA||B B->b is represented as 2 Keys ( A and B). List of A: 4
	 * elements ( List(A,s,A),List(A,A),List(),List(B) ) List of B: 1 element (
	 * List(b) )
	 */
	final Map<NT, Set<List<Symbol>>> productions = new HashMap<>();

	private final Map<NT, Set<T>> firstSets = new HashMap<>();
	boolean firstSets_valid;
	private final Map<NT, Set<T>> followSets = new HashMap<>();
	boolean followSets_valid;

	NT startSymbol; // Start Symbol

	T word_end;

	/**
	 * Creates the grammar
	 * 
	 * @param start
	 *            original start symbol
	 * @param additional_start
	 *            additional start symbol
	 * @param word_end
	 *            token to define the end of the word
	 * @param epsilon_symbol
	 *            token to define the epsilon
	 */
	public Grammar(NT start, NT additional_start, T word_end, T epsilon_symbol) {

		this.epsilonSymbol = epsilon_symbol;
		this.artificial_start_symbol = additional_start;
		this.word_end = word_end;
		// add start production
		Set<List<Symbol>> startTrans = new HashSet<>();
		List<Symbol> startProdRight = new LinkedList<>();
		startProdRight.add(start);
		startTrans.add(startProdRight);
		this.productions.put(artificial_start_symbol, startTrans);
		this.startSymbol = start;
		firstSets_valid = false;
		followSets_valid = false;

	}

	/**
	 * Adds a production or productions
	 * 
	 * @param nT
	 *            The NonTerminal
	 * @param head
	 *            the first production
	 * @param rest
	 *            other productions (optional) to simple determining multiple
	 *            productions with the same NonTerminal
	 * @return
	 */

	@SafeVarargs
	public final Grammar<T, NT> addProduction(NT nT, List<Symbol> head,
			List<Symbol>... rest) {
		firstSets_valid = false;
		followSets_valid = false;

		// get List for productions
		Set<List<Symbol>> productionsOfNT = productions.get(nT);
		if (productionsOfNT == null) {
			productionsOfNT = new HashSet<>();
			productions.put(nT, productionsOfNT);
		}
		// productionsOfNT!=null and is the right one
		productionsOfNT.add(head);
		Collections.addAll(productionsOfNT, rest);
		// all productions should be inserted now
		return this;
	}

	/**
	 * just a mapper for getClosure() for multiple productions
	 * 
	 * @param current
	 *            the productions you want to have "closured"
	 * @return the closures
	 */
	public Map<NT, Set<Item<T, NT>>> getClosure(
			Map<NT, Set<Item<T, NT>>> current) {
		Map<NT, Set<Item<T, NT>>> res = new HashMap<>();
		for (Entry<NT, Set<Item<T, NT>>> ent : current.entrySet()) {
			for (Item<T, NT> prod : ent.getValue()) {
				Map<NT, Set<Item<T, NT>>> single_res = getClosure(ent.getKey(),
						prod);
				res = utils.merge(res, single_res);
			}
		}
		return res;
	}

	/**
	 * gets the kernel element and returns every possible item in closure of @prod
	 * 
	 * @param nT
	 *            the NonTermonal of the production
	 * @param prod
	 *            the production
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<NT, Set<Item<T, NT>>> getClosure(NT nT, Item<T, NT> prod) {
		Map<NT, Set<Item<T, NT>>> closures = new HashMap<>();

		// Since the production itself is in the closure, it can be added
		Set<Item<T, NT>> nTset = new HashSet<>();
		closures.put(nT, nTset);

		if (prod.right.isEmpty() || isTerminal(prod.right.get(0))) {
			// nothing to do here
		} else {
			NT toCheck = (NT) prod.right.get(0);
			Queue<NT> ntq = new LinkedList<>();
			ntq.add(toCheck);
			while (!ntq.isEmpty()) {
				toCheck = ntq.poll();
				if (closures.containsKey(toCheck))
					continue;
				nTset = new HashSet<>();
				closures.put(toCheck, nTset);
				for (List<Symbol> list : productions.get(toCheck)) {
					nTset.add(new Item<T, NT>(new LinkedList<Symbol>(), list));
					if (isNonTerminal(list.get(0))) {
						ntq.add((NT) list.get(0));
					}
				}
			}
		}

		return closures;

	}

	/**
	 * returns the first set for the nonterminal
	 * @param nt nonterminal
	 * @return the firstset
	 */
	public Set<T> getFirstSet(NT nt) {
		ensureValidFirstSets();
		return firstSets.get(nt);
	}

	/**
	 * ensures that the first sets are valid for the current productions
	 */
	private void ensureValidFirstSets() {
		if (firstSets_valid)
			return;
		firstSets.clear();
		for (NT nt : productions.keySet()) {
			firstSets.put(nt, new HashSet<T>());
		}
		computeAllFirstSets();
		firstSets_valid = true;
	}

	/**
	 * the "firstSets" attribute will be set with this method
	 */
	@SuppressWarnings("unchecked")
	private void computeAllFirstSets() {
		
		/**
		 * We use this method using the cardinality of the sets to see, if
		 * the data has been changed in one iteration. It's the most reliable solution atm.
		 */
		int cardinality_last = -1;
		int cardinality_current = getCardinalityUnionFirstSets();
		/**
		 * In the first step, we have to find as much epsilon productions as possible.
		 * If we make a mistake here, all the following steps will fail.
		 */
		do {
			for (NT nt : productions.keySet()) {
				Set<List<Symbol>> prods = productions.get(nt);
				for (List<Symbol> prod : prods) {
					// nt -> prod(0),...,prod(prod.size()-1)
					if (prod.size()==0){
						logger.error("may not happen, an epsilon should be represented by the epsilonSymbol");
					}
					if (prod.size()==1 && prod.get(0).equals(epsilonSymbol)){
						// trivial epsilon production
						firstSets.get(nt).add(epsilonSymbol);
					}
					// check, if an epsilon production is possible within this production
					boolean eps_possible=true;
					for (Symbol s : prod){
						if (isTerminal(s) || !firstSets.get((NT)s).contains(epsilonSymbol)){
							eps_possible=false;
							break;
						}	
					}
					// if an epsilon production is possible, add it to the first set
					if (eps_possible){
						firstSets.get(nt).add(epsilonSymbol);
					}
				}
			}
			// check if the result has been changed and retry if so
			cardinality_last = cardinality_current;
			cardinality_current = getCardinalityUnionFirstSets();
		
		} while (cardinality_current > cardinality_last);

		
		/**
		 * the next step to build the first sets with terminals.
		 * We use the retry-if-changed method too.
		 */
		cardinality_last = -1;
		cardinality_current = getCardinalityUnionFirstSets();
		do {
			for (NT nt : productions.keySet()) {
				Set<List<Symbol>> prods = productions.get(nt);
				for (List<Symbol> prod : prods) {
					// check which production is the first without an epsilon production
					Symbol firstNonEpsilable = null;
					LOOK_FOR_FIRST:
					for (Symbol s : prod){
						// nt -> prod(0),prod(1),...,s,prod(i+1),...,prod(prod.size()-1)
						
						if (isTerminal(s)){
							firstNonEpsilable=s;
							break LOOK_FOR_FIRST;
						}
						else if (!firstSets.get((NT)s).contains(epsilonSymbol)){
							firstNonEpsilable=s;
							break LOOK_FOR_FIRST;
						}
					}
					// firstNonEpsilable is the first production without an epsilon production
					if (firstNonEpsilable==null){
						firstSets.get(nt).add(epsilonSymbol);
					}
					else if (isTerminal(firstNonEpsilable)){
						firstSets.get(nt).add((T)firstNonEpsilable);
					}
					else{
						// isNonTerminal
						firstSets.get(nt).addAll(firstSets.get((NT)firstNonEpsilable));
					}
					
				}
			}
			// the same story as above....
			cardinality_last = cardinality_current;
			cardinality_current = getCardinalityUnionFirstSets();
		
		} while (cardinality_current > cardinality_last);

	}

	/**
	 * computes the follow set
	 * 
	 * @param t
	 *            terminals
	 * @return A set of Terminals
	 */
	public Set<T> getFollowSet(NT nt) {
		ensureValidFollowSets();
		return followSets.get(nt);
	}

	/**
	 * like ensureValidFollowSets() with firstSets
	 */
	private void ensureValidFollowSets() {
		ensureValidFirstSets();
		if (followSets_valid)
			return;
		followSets.clear();
		for (NT nt : productions.keySet())
			followSets.put(nt, new HashSet<T>());
		computeAllFollowSets();
		followSets_valid = true;
	}

	/**
	 * the "followSets" attribute is set in this method
	 */
	@SuppressWarnings("unchecked")
	private void computeAllFollowSets() {
		// first, add start production
		followSets.get(artificial_start_symbol).add(word_end);
		
		
		// second, add everything possible by using the first sets
		for (NT nt : productions.keySet()) {
			Set<List<Symbol>> prods = productions.get(nt);
			for (List<Symbol> prod : prods) {
				int last_index = prod.size() - 1;
				for (int i = 0; i < last_index; i++) {
					// nt -> prod(0),prod(1),...,prod(i),...,prod(last_index)
					Symbol prod_i = prod.get(i);
					if (isNonTerminal(prod_i)) {
						Symbol prod_i1 = prod.get(i + 1);
						if (isNonTerminal(prod_i1)) {
							// prod_i and prod_i1 is NT
							// new set is necessary to keep epsilons...
							// epsilones? epsilies?
							Set<T> first = new HashSet<>(
									getFirstSet((NT) prod_i1));
							first.remove(epsilonSymbol);
							followSets.get(prod_i).addAll(first);
						} else {
							// prod_i is NT, prod_i1 is T
							followSets.get(prod_i).add((T) prod_i1);
						}

					}
				}
			}
		}

		// necessary through the circular dependencies.
		// As indicator if something changed we use the total sum elements
		// in the followSets of each terminal
		int cardinality_last = -1;
		int cardinality_current = getCardinalityUnionFollowSets();

		do {
			for (NT nt : productions.keySet()) {
				Set<List<Symbol>> prods = productions.get(nt);
				NEXT_PROD:
				for (List<Symbol> prod : prods) {
					// nt-> prod(0),...,prod(prod.size()-1)
					// property holds:
					// nt-> prod(0),...,prod(i-1),firstNonElipsableFromReverse,prod(i+1),...,prod(prod.size()-1)
					// where: for all j=i+1..prod.size()-1 : epsilon is in First(j)
					// abstract: A->aBb . First(b) contains epsilon -> add Follow(A) to Follow(B)
					NT A=nt;
					Symbol B;
					int i=prod.size()-1;
					if (isTerminal(prod.get(i))){
						continue NEXT_PROD;
					}
					do{
						// invatiant here: First(b) contains epsilon
						B=prod.get(i);
						followSets.get(B).addAll(followSets.get(A));
						i--;
					}while (i>=0&&isNonTerminal(prod.get(i))&&firstSets.get(prod.get(i)).contains(epsilonSymbol));
					if (i>=0 && isNonTerminal(prod.get(i))){
						B=prod.get(i);
						followSets.get(B).addAll(followSets.get(A));
					}
					
				}
			}
			cardinality_last = cardinality_current;
			cardinality_current = getCardinalityUnionFollowSets();
		} while (cardinality_current > cardinality_last);

	}

	/**
	 * computes the cardinality of all the union of the followsets
	 * @return the cardinality of the union
	 */
	private int getCardinalityUnionFollowSets() {
		int i = 0;
		for (Set<T> followSet : followSets.values())
			i += followSet.size();
		return i;
	}
	
	/**
	 * computes the cardinality of all the union of the firstsets
	 * @return the cardinality of the union
	 */
	private int getCardinalityUnionFirstSets() {
		int i = 0;
		for (Set<T> followSet : firstSets.values())
			i += followSet.size();
		return i;
	}

	public final Map<NT, Set<List<Symbol>>> getProductions() {
		return productions;
	}


	/**
	 * Since generics are not available in runtime, we need this little hacky
	 * thing to identify, whether it's a terminal or not.
	 * 
	 * @param s
	 *            the symbol
	 * @return if it's an instance of NonTerminal
	 */
	private boolean isNonTerminal(Symbol s) {
		return productions.containsKey(s);
	}

	/**
	 * Since generics are not available in runtime, we need this little hacky
	 * thing to identify, whether it's a terminal or not.
	 * 
	 * @param s
	 *            the symbol
	 * @return if it's an instance of Terminal
	 */
	private boolean isTerminal(Symbol s) {
		return !isNonTerminal(s);
	}

	/**
	 * Just returns the productions added so far
	 */
	public String toString() {
		StringBuilder strb = new StringBuilder();
		for (NT nt : productions.keySet()) {
			for (List<Symbol> prod : productions.get(nt)) {
				strb.append(nt + "\t -> " + prod + "\n");
			}
		}
		return strb.toString();
	}

}
