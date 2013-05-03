package swp_compiler_ss13.javabite.parser.grammar;

/**
 * just a neccessary classname for a reasonable filename
 * @author Till
 *
 */
public class SimpleGrammar{
	
}
/**
 * simple NonTerminal class wrapping a string
 * @author Till
 *
 */
class SimpleNT implements Symbol{
	@Override
	public String toString() {
		return id;
	}

	public SimpleNT(String id) {
		super();
		this.id = id;
	}

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
		SimpleNT other = (SimpleNT) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	String id;
}

/**
 * simple Terminal class wrapping a string
 * @author Till
 *
 */
class SimpleT implements Symbol{
	@Override
	public String toString() {
		return id ;
	}

	public SimpleT(String id) {
		super();
		this.id = id;
	}

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
		SimpleT other = (SimpleT) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	String id;
}