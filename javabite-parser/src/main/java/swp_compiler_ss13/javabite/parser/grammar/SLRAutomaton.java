package swp_compiler_ss13.javabite.parser.grammar;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.javabite.parser.grammar.exceptions.AmbiguityInDerivationGrammarException;
import swp_compiler_ss13.javabite.parser.grammar.exceptions.WordNotInLanguageGrammarException;

/**
 * Represents the Automaton of a SLR parser.
 * 
 * @author Till
 * 
 * @param <T>
 *            The class of Terminals
 * @param <NT>
 *            The class of nonTerminals
 */
public class SLRAutomaton<T extends Symbol, NT extends Symbol> {

	Integer state_num = 0;
	Grammar<T, NT> g;
	State initialState;
	final Set<State> states = new HashSet<>();
	final State acceptState = new State();
	AMBIGUITY_POLICY policy = AMBIGUITY_POLICY.DIE;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public SLRAutomaton(Grammar<T, NT> referenceGrammar) {
		this.g = referenceGrammar;
		// this.g.removeEpsilonProductions();
		build();
	}

	public void setAmbiguityPolicy(AMBIGUITY_POLICY policy) {
		this.policy = policy;
	}

	/**
	 * Analogous to Singleton, but may have multiple instances, dependent on the
	 * kernel of the state
	 * 
	 * @return
	 */
	private State getStateFor(Map<NT, Set<Item<T, NT>>> kernel) {
		for (State st : states) {
			if (st.kernel_items.equals(kernel)) {
				return st;
			}
		}

		State st = new State();
		states.add(st);
		st.kernel_items = kernel;
		st.closure_items = g.getClosure(kernel);
		return st;
	}

	private int getAllStateItems() {
		int i = 0;
		for (State st : states) {
			i += Utils.countItemsRecursive(st.closure_items);
			i += Utils.countItemsRecursive(st.kernel_items);
		}
		return i;
	}

	/**
	 * the mayor function. It converts the given grammar to the automaton. It's
	 * not the exact definition as it is in the drachenbuch but it's almost. In
	 * this definition there exists a better criteria to stop.
	 * 
	 */
	private void build() {
		// create first state and closure
		NT s_symbol = g.artificial_start_symbol;
		Set<Item<T, NT>> initialKernel = new HashSet<>();
		// possible eps-production
		// initialKernel.add(new Item<T, NT>(new LinkedList<Symbol>(),
		// g.productions.get(s_symbol).iterator().next()));
		for (List<Symbol> l : g.productions.get(s_symbol)) {
			initialKernel.add(new Item<T, NT>(new LinkedList<Symbol>(), l));
		}

		Map<NT, Set<Item<T, NT>>> initialKernelProd = new HashMap<>();
		initialKernelProd.put(s_symbol, initialKernel);
		initialState = getStateFor(initialKernelProd);

		int card_last = -1;
		int card_atm = getAllStateItems();

		do {

			for (State state : new HashSet<>(this.states)) {
				Map<Symbol, Map<NT, Set<Item<T, NT>>>> edge_to_kernel_set = new HashMap<>();
				Map<NT, Set<Item<T, NT>>> productions = Utils.merge(
						state.kernel_items, state.closure_items);

				for (Entry<NT, Set<Item<T, NT>>> prodSet : productions
						.entrySet()) {
					NT nt = prodSet.getKey();
					for (Item<T, NT> prod : prodSet.getValue()) {
						// for all productions in kernel and closure ->
						// prod(0),...,prod(n)
						if (prod.isAtEnd())
							continue;
						// for all productions in kernel and closure for
						// prod(0),... prod(i) , . ,prod(i+1) ..., prod(n)
						Item<T, NT> new_item = prod.copy();
						// edge symbol = prod(i+1)
						Symbol edge_symbol = new_item.shift();

						Map<NT, Set<Item<T, NT>>> edge_kernel = edge_to_kernel_set
								.get(edge_symbol);
						if (edge_kernel == null) {
							edge_kernel = new HashMap<>();
							edge_to_kernel_set.put(edge_symbol, edge_kernel);
						}

						Set<Item<T, NT>> nt_productions = edge_kernel.get(nt);
						if (nt_productions == null) {
							nt_productions = new HashSet<>();
							edge_kernel.put(nt, nt_productions);
						}
						nt_productions.add(new_item);
					}
				}
				for (Entry<Symbol, Map<NT, Set<Item<T, NT>>>> transition : edge_to_kernel_set
						.entrySet()) {
					State next = getStateFor(transition.getValue());
					if (state.transition.containsKey(transition.getKey())) {
						if (state.transition.get(transition.getKey()).equals(
								next)) {
							continue;
						} else {
							// it's okay
						}
					}
					state.transition.put(transition.getKey(), next);

				}
			}
			card_last = card_atm;
			card_atm = getAllStateItems();
		} while (card_last < card_atm);

	}

	public List<Production<T, NT>> getDerivationASsSequence(
			Word<T, NT> word_total) {

		Stack<State> state_stack = new Stack<>();
		Stack<Symbol> symbol_stack = new Stack<>();
		state_stack.push(initialState);
		symbol_stack.push(g.word_end);

		List<Production<T, NT>> productions = new LinkedList<>();
		Queue<T> word = new ArrayDeque<>(word_total);
		word.add(g.word_end);
		while (!state_stack.isEmpty()) {
			State state = state_stack.peek();
			if (state.isStateAccepting())
				break;

			Symbol a = word.peek();
			if (g.isTerminal(a)) {
				T t = (T) a;
				State nextStateTrans = state.transition.get(t);
				Set<Production> nextProdFollow = possibleReductionsInState(t,
						state);
				if (nextStateTrans != null && !nextProdFollow.isEmpty()
						|| nextStateTrans == null && nextProdFollow.isEmpty()
						|| nextProdFollow.size() > 1) {
					
					if (nextStateTrans == null && nextProdFollow.isEmpty()){
						logger.warn(
								"behaviour not defined, policy set to DIE. next state by shift: {}, next states by follow: {}",
								nextStateTrans, nextProdFollow);
						logger.warn("current terminal: {}", a);
						logger.warn("productions so far: {}", productions);
						logger.warn("state stack {}", state_stack);
						logger.warn("symbol stack {}", symbol_stack);
						logger.warn("word actual {}", word);
						logger.warn("word complete {}", word_total);
						logger.warn("states:");
						//for (State st : states) {
						//	logger.warn("\n{}", st.descriptionAsString());
						//}
						throw new WordNotInLanguageGrammarException(
								(swp_compiler_ss13.javabite.parser.targetgrammar.Terminal) t);
					}
					
					if (policy == AMBIGUITY_POLICY.DIE) {
						logger.warn(
								"behaviour not defined, policy set to DIE. next state by shift: {}, next states by follow: {}",
								nextStateTrans, nextProdFollow);
						logger.warn("current terminal: {}", a);
						logger.warn("productions so far: {}", productions);
						logger.warn("state stack {}", state_stack);
						logger.warn("symbol stack {}", symbol_stack);
						logger.warn("word actual {}", word);
						logger.warn("word complete {}", word_total);
						logger.warn("states:");
						//for (State st : states) {
						//	logger.warn("\n{}", st.descriptionAsString());
						//}

						if (nextStateTrans != null && !nextProdFollow.isEmpty()) {
							if (t instanceof swp_compiler_ss13.javabite.parser.targetgrammar.Terminal) {
								throw new AmbiguityInDerivationGrammarException(
										(swp_compiler_ss13.javabite.parser.targetgrammar.Terminal) t);
							} else
								logger.warn("Dont know how to react...");
						} else if (nextStateTrans == null
								&& nextProdFollow.isEmpty()) {
							if (t instanceof swp_compiler_ss13.javabite.parser.targetgrammar.Terminal) {
								throw new WordNotInLanguageGrammarException(
										(swp_compiler_ss13.javabite.parser.targetgrammar.Terminal) t);
							} else
								logger.warn("Dont know how to react...");
						} else if (nextProdFollow.size() > 1) {
							if (t instanceof swp_compiler_ss13.javabite.parser.targetgrammar.Terminal) {
								throw new AmbiguityInDerivationGrammarException(
										(swp_compiler_ss13.javabite.parser.targetgrammar.Terminal) t);
							} else
								logger.warn("Dont know how to react...");
						} else {
							throw new RuntimeException(
									"Enexptected state reached");
						}
					}
					else if (policy==AMBIGUITY_POLICY.PREFER_REDUCE){
						if (nextProdFollow.size()==1){
							// disable shift
							nextStateTrans=null;
						}
						else{
							logger.error("policy PREFER_REDUCE is not applicable, because multiple or none (exact :{}) reductions are possible",nextProdFollow.size());
							if (nextProdFollow.size()>1)
								throw new AmbiguityInDerivationGrammarException(
										(swp_compiler_ss13.javabite.parser.targetgrammar.Terminal) t);
						}
					}
					else if (policy==AMBIGUITY_POLICY.PREFER_SHIFT){
						logger.info("policy PREFER_SHIFT is applicable and used");
						// diable of reduce not necessary
						nextProdFollow=null;
					}
					else{
						throw new RuntimeException("Invalid Policy");
					}
				}
				// do shift if possible
				if (nextStateTrans != null) {
					word.poll();
					symbol_stack.push(a);
					state_stack.push(nextStateTrans);
				} else {
					// reduce
					Production todo = nextProdFollow.iterator().next();
					List<Symbol> original_right_side = new LinkedList<>();
					for (int i = todo.right.size() - 1; i >= 0; i--) {
						state_stack.pop();
						original_right_side.add(symbol_stack.pop());
					}
					Collections.reverse(original_right_side);
					State stateBeforeProduction = state_stack.peek();
					State nextState = stateBeforeProduction.transition
							.get(todo.left);
					
					state_stack.push(nextState);
					symbol_stack.push(todo.left);
					todo.right = original_right_side;
					productions.add(todo);
				}
			}
		}
		return productions;
	}

	private Set<Production> possibleReductionsInState(T t, State st) {
		Set<Production> res = new HashSet<>();
		for (NT nt : st.kernel_items.keySet()) {
			for (Item<T, NT> item : st.kernel_items.get(nt)) {
				if (item.isAtEnd() && g.getFollowSet(nt).contains(t)) {
					res.add(new Production(nt, item.left));
				}
			}
		}
		for (NT nt : st.closure_items.keySet()) {
			for (Item<T, NT> item : st.closure_items.get(nt)) {
				if (item.isAtEnd() && g.getFollowSet(nt).contains(t)) {
					res.add(new Production(nt, item.left));
				}
			}
		}
		return res;
	}

	public int getNStates() {
		return states.size();
	}

	/**
	 * represents a state in the automaton
	 * 
	 * @author Till
	 * 
	 */
	class State {

		/**
		 * a constructor with a "unique" id
		 */
		State() {
			id = state_num++;
		}

		Map<NT, Set<Item<T, NT>>> kernel_items = new HashMap<>();
		Map<NT, Set<Item<T, NT>>> closure_items = new HashMap<>();
		final Map<Symbol, State> transition = new HashMap<>();
		int id;

		public String descriptionAsString() {
			String str = "";
			str += "--------------------------------------------\nState[" + id
					+ ", accepts: " + isStateAccepting() + "]\n";
			str += kernel_items;
			str += "\n++++++++++++++++++++++++\n" + closure_items
					+ "\n++++++++++++++++++++++++\n";
			for (Entry<Symbol, State> s : transition.entrySet())
				str += s.getKey() + "->" + s.getValue().id + ",";
			str += "\n--------------------------------------------\n";
			// return str;
			return id + " | " + (kernel_items) + " | " + (closure_items)
					+ " | " + transition + " | final: " + isStateAccepting();
		}

		public String toString() {
			return new Integer(id).toString();
		}

		public boolean isStateAccepting() {
			Set<Item<T, NT>> art_kernel_items = kernel_items
					.get(g.artificial_start_symbol);
			if (art_kernel_items == null || art_kernel_items.size() != 1) {
				return false;
			}
			Item<T, NT> unique_item = art_kernel_items.iterator().next();

			return unique_item.left.equals(g.productions
					.get(g.artificial_start_symbol).iterator().next());
		}
	}

	public enum AMBIGUITY_POLICY {
		DIE, PREFER_SHIFT, PREFER_REDUCE
	}

}
