package swp_compiler_ss13.javabite.parser.grammar;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Utils {
	/**
	 * merges two sets 
	 * @param a a set
	 * @param b a set
	 * @return the union of a and b with respect to the given data structure
	 */
	public static<NT extends Symbol,T extends Symbol> Map<NT,Set<Item<T,NT>>> merge(Map<NT,Set<Item<T,NT>>> a, Map<NT,Set<Item<T,NT>>> b){
		Map<NT,Set<Item<T,NT>>> res= new HashMap<>();
		for (NT nt : a.keySet()){
			res.put(nt, new HashSet<>(a.get(nt)));
		}
		for (NT nt : b.keySet()){
			if (res.containsKey(nt)){
				res.get(nt).addAll(b.get(nt));
			}
			else{
				res.put(nt, new HashSet<>(b.get(nt)));
			}
		}
		return res;
		
	}
	
	public static<R,S extends Collection> int countItemsRecursive(Map<R, S> m){
		int i=0;
		for (Map.Entry<R,S> e : m.entrySet()){
			i+=e.getValue().size();
		}
		return i;
	}
	
	static public List<Symbol> list(Symbol... syms){
		return Arrays.asList(syms);
	}
	
	
}
