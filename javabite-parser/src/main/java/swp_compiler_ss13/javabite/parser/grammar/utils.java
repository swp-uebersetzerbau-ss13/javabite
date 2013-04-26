package swp_compiler_ss13.javabite.parser.grammar;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class utils {
	/**
	 * merges two sets 
	 * @param a a set
	 * @param b a set
	 * @return the union of a and b with respect to the given data structure
	 */
	public static<NT extends Symbol,T extends Symbol> Map<NT,Set<Item<T,NT>>> merge(Map<NT,Set<Item<T,NT>>> a, Map<NT,Set<Item<T,NT>>> b){
		Map<NT,Set<Item<T,NT>>> res= new HashMap<>();
		res.putAll(a);
		// for all in b
		for (Entry<NT, Set<Item<T,NT>>> ent : b.entrySet()){
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
}
