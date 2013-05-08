package swp_compiler_ss13.javabite.codegen;

import swp_compiler_ss13.common.backend.Quadruple;

public class QuadrupleImpl implements Quadruple {

	private final Operator operator; // This is the Operator for quadruple
	private final String argument1; // This is the first argument for the
									// quadruple
	private final String argument2; // This is the second argument for the
									// quadruple
	private final String result; // This is the result for the quadruple

	/**
	 * Constructor
	 * @param operator
	 * @param argument1
	 * @param argument2
	 * @param result
	 */
	public QuadrupleImpl(Operator operator, String argument1, String argument2,
			String result) {
		this.operator = operator;
		this.argument1 = argument1;
		this.argument2 = argument2;
		this.result = result;
	}

	@Override
	public Operator getOperator() {
		return this.operator;
	}

	@Override
	public String getArgument1() {
		return this.argument1;
	}

	@Override
	public String getArgument2() {
		return this.argument2;
	}

	@Override
	public String getResult() {
		return this.result;
	}

}
