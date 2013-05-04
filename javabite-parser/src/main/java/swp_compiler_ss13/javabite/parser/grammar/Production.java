package swp_compiler_ss13.javabite.parser.grammar;

import java.util.List;

public class Production<T extends Symbol, NT extends Symbol> {
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
		Production other = (Production) obj;
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
	public String toString(){
		return left+"->"+right;
	}
	public Production(NT left, List<Symbol> right) {
		super();
		this.left = left;
		this.right = right;
	}
	NT left;
	List<Symbol> right;
}
