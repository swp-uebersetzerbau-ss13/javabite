package swp_compiler_ss13.javabite.quadtruple;


import swp_compiler_ss13.common.backend.Quadruple;

/**
 * Implements the Quadruple-Interface. 
 * Quadrupels has the structure like 
 * <b>op arg1 arg2 res</b>
 * @author Alpin Sahin
 *
 */
public class QuadrupleJb implements Quadruple {

	/**
	 * The quadruple operator
	 */
	private final Operator operator;
	
	/**
	 * The first quadruple argument
	 */
	private final String arg1;
	
	/**
	 * The second quadruple argument
	 */
	private final String arg2;
	
	/**
	 * The quadruple result
	 */
	private final String res;
	
	/**
	 * The constructor for the quadruple
	 * @param operator
	 * @param arg1
	 * @param arg2
	 * @param res
	 */
	public QuadrupleJb(Operator operator, String arg1, String arg2, String res) {
		this.operator = operator;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.res = res;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arg1 == null) ? 0 : arg1.hashCode());
		result = prime * result + ((arg2 == null) ? 0 : arg2.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((res == null) ? 0 : res.hashCode());
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
		QuadrupleJb other = (QuadrupleJb) obj;
		if (arg1 == null) {
			if (other.arg1 != null)
				return false;
		} else if (!arg1.equals(other.arg1))
			return false;
		if (arg2 == null) {
			if (other.arg2 != null)
				return false;
		} else if (!arg2.equals(other.arg2))
			return false;
		if (operator != other.operator)
			return false;
		if (res == null) {
			if (other.res != null)
				return false;
		} else if (!res.equals(other.res))
			return false;
		return true;
	}

	@Override
	public Operator getOperator() {
		return this.operator;
	}
	
	@Override
	public String getArgument1() {
		return this.arg1;
	}
	
	@Override
	public String getArgument2() {
		return this.arg2;
	}

	@Override
	public String getResult() {
		return this.res;
	}
	
	public String toString(){
		return this.operator.toString()+" "+this.arg1+" "+this.arg2+" "+this.res;	
	}
}