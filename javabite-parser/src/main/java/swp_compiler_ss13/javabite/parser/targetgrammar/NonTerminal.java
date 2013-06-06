package swp_compiler_ss13.javabite.parser.targetgrammar;

import swp_compiler_ss13.javabite.parser.grammar.Symbol;

public class NonTerminal implements Symbol{
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		NonTerminal other = (NonTerminal) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	String id;
	public NonTerminal(String id){
		this.id=id;
	}
	
	public String toString(){
		return id;
	}
}
