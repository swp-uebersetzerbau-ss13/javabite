package swp_compiler_ss13.javabite.parser.grammar;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Represents a production with a pre-production as handle.
 * @author Till
 *
 * @param <T> The Class of Terminals
 * @param <NT> The Class of NonTerminals
 */
public class Item<T extends Symbol, NT extends Symbol> {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + ((right == null) ? 0 : right.hashCode());
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
		Item other = (Item) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}
	/**
	 * creates a item with the given sequences
	 * @param left the values already read
	 * @param right the handle
	 */
	public Item(List<Symbol> left, List<Symbol> right) {
		this.left = left;
		this.right = right;
	}
	List<Symbol> left;
	List<Symbol> right;
	
	/**
	 * returns whether there is nothing left to read
	 * @return if the handle ( right sided value) is empty
	 */
	boolean isAtEnd(){
		return right.isEmpty(); 
	}
	
	/**
	 * Copies the Item
	 * @return a Copy of the Item ( be careful, it's not a deep copy!)
	 */
	Item<T,NT> copy(){
		return new Item<>(new LinkedList<Symbol>(left), new LinkedList<Symbol>(right));
	}
	
	/**
	 * Shifts the handle from
	 * l_1,...,l_m,.,r_1,....,r_n to
	 * l_1,...,l_m,r_1,.,r_2,...,r_n
	 * @return
	 */
	Symbol shift(){
		Symbol tmp=right.remove(0);
		left.add(tmp);
		return tmp;
	}
	
	public String toString(){
		String str="";
		for (Symbol l : left){
			str+=l;
		}
		str+=".";
		for (Symbol l : right){
			str+=l;
		}
		return str;
		
	}
	

}
