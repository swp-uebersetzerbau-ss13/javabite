package swp_compiler_ss13.javabite.backend.external;

import swp_compiler_ss13.common.backend.Quadruple;

/**
 * @author robert
 * 
 */
public class QuadrupleImpl implements Quadruple {

	public QuadrupleImpl(final Operator op, final String arg1,
			final String arg2, final String res) {
		this.op = op;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.res = res;
	}

	private final Operator op;
	private final String arg1;
	private final String arg2;
	private final String res;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Operator getOperator() {
		return this.op;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getArgument1() {
		return this.arg1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getArgument2() {
		return this.arg2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getResult() {
		return this.res;
	}

}
