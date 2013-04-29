package swp_compiler_ss13.javabite.backend.marco.proposal1;

/**
 * @author robert
 *
 */
public class QuadrupleImpl implements Quadruple {

	public QuadrupleImpl(Operator op, String arg1, String arg2, String res) {
		this.op = op;
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.res = res;
	}

	private Operator op;
	private String arg1;
	private String arg2;
	private String res;
	
	@Override
	public Operator getOperator() {
		return this.op;
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

}
