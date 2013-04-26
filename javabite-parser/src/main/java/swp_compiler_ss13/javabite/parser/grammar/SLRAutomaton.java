package swp_compiler_ss13.javabite.parser.grammar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Represents the Automaton of a SLR parser.
 * 
 * @author Till
 *
 * @param <T> The class of Terminals
 * @param <NT> The class of nonTerminals
 */
public class SLRAutomaton<T extends Symbol, NT extends Symbol> {
	public SLRAutomaton(Grammar<T,NT> referenceGrammar) {
		this.g=referenceGrammar;
		build();
	}
	
	/**
	 * Analogous to Singleton, but may have multiple instances,
	 * dependent on the kernel of the state
	 * @return
	 */
	private State getStateFor(Map<NT,Set<Item<T,NT>>> kernel){
		for (State st : states){
			if (st.kernel_items.equals(kernel)) return st;
		}
		State st=new State();
		states.add(st);
		st.kernel_items=kernel;
		return st;
	}
	
	/**
	 * the mayor function. It converts the given grammar to the automaton.
	 * It's not the exact definition as it is in the drachenbuch but it's almost.
	 * In this definition there exists a better criteria to stop. 
	 * 
	 */
	private void build() {
		// create first state and closure
		NT s_symbol=g.artificial_start_symbol;
		Set<Item<T,NT>> initialKernel=new HashSet<>();
		initialKernel.add(new Item<T,NT>(new LinkedList<Symbol>(),g.productions.get(s_symbol).iterator().next()));
		Map<NT,Set<Item<T,NT>>> initialKernelProd=new HashMap<>();
		initialKernelProd.put(s_symbol, initialKernel);
		State initialState=getStateFor(initialKernelProd);
		
		
		Stack<State> statesToVisit=new Stack<>();
		statesToVisit.push(initialState);
		
		int i=0;
		while (!statesToVisit.isEmpty()){
			if (i++>20) break;
			State state=statesToVisit.pop();
			state.closure_items=g.getClosure(state.kernel_items);
			Map<Symbol,Map<NT,Set<Item<T,NT>>>> edge_to_kernel_set=new HashMap<>();
			Map<NT, Set<Item<T, NT>>> productions=utils.merge(state.kernel_items, state.closure_items);
			for (Entry<NT,Set<Item<T,NT>>> prodSet : productions.entrySet()){
				NT nt=prodSet.getKey();
				for (Item<T,NT> prod : prodSet.getValue()){
					if (prod.isAtEnd()) continue;
					Item<T,NT> new_item=prod.copy();
					Symbol edge_symbol=new_item.shift();
					Map<NT,Set<Item<T,NT>>> edge_kernel=edge_to_kernel_set.get(edge_symbol);
					if (edge_kernel==null){
						edge_kernel=new HashMap<>();
						edge_to_kernel_set.put(edge_symbol, edge_kernel);
					}
					Set<Item<T,NT>> nt_productions=edge_kernel.get(nt);
					if (nt_productions==null){
						nt_productions=new HashSet<>();
						edge_kernel.put(nt, nt_productions);
					}
					nt_productions.add(new_item);
				}
			}
			for (Entry<Symbol, Map<NT, Set<Item<T, NT>>>> transition : edge_to_kernel_set.entrySet()){
				State next=getStateFor(transition.getValue());
				if (state.transition.containsKey(transition.getKey())){
					if (state.transition.get(transition.getKey()).equals(next)){
						continue;
					}
					else{
						logger.info("EXCEPTION");
						logger.info("state: \n{}",state);
						logger.info("Edge symbol: {}",transition.getKey());
						logger.info("next state old:\n{}",state.transition.get(transition.getKey()));
						logger.info("state new  : \n{}",next);
						throw new RuntimeException("AW dawg...");
					}
				}
				state.transition.put(transition.getKey(),next);
				statesToVisit.push(next);
			}
		}
		
			
		
	}

	Integer state_num=0;
	Grammar<T,NT> g;
	State initialState;
	final Set<State> states=new HashSet<>();
	Logger logger=LoggerFactory.getLogger(this.getClass());
	
	public int getNStates(){
		return states.size();
	}
	
	/**
	 * represents a state in the automaton
	 * @author Till
	 *
	 */
	class State{

		/**
		 * a constructor with a "unique" id
		 */
		State() {
			id=state_num++;
		}
		
		
		Map<NT,Set<Item<T,NT>>> kernel_items=new HashMap<>();
		Map<NT,Set<Item<T,NT>>> closure_items=new HashMap<>();
		final Map<Symbol,State> transition=new HashMap<>();
		int id;
		
		private SLRAutomaton<T, NT> getOuterType() {
			return SLRAutomaton.this;
		}
		
		public String toString(){
			String str="";
			str+="--------------------------------------------\nState["+id+"]\n";
			str+=kernel_items;
			str+="\n++++++++++++++++++++++++\n"+closure_items+"\n++++++++++++++++++++++++\n";
			for (Entry<Symbol,State> s : transition.entrySet())
				str+=s.getKey()+"->"+s.getValue().id+",";
			str+="\n--------------------------------------------\n";
			return str;
		}
	}
	
	
}
