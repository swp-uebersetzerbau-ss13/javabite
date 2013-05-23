package swp_compiler_ss13.javabite.quadtruple;


import swp_compiler_ss13.common.backend.Quadruple;

/**
 * Implements the Quadruple-Interface. 
 * Quadrupels has the structure like 
 * <b>op arg1 arg2 res</b>
 * @author Alpin Sahin
 *
 */
public class QuadrupleJB implements Quadruple {

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
	public QuadrupleJB(Operator operator, String arg1, String arg2, String res) {
		this.operator = operator;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.res = res;
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