package swp_compiler_ss13.javabite.backend;

import swp_compiler_ss13.javabite.backend.Operation.OperationBuilder;

import static swp_compiler_ss13.javabite.backend.Mnemonic.*;

public final class Operations
{

	public static Operation UNARY_MINUS_NUM(final int op1) {
		return OperationBuilder.newBuilder() // new builder
				.add(5, 0, LLOAD, op1) // load long
				.add(1, 5, LNEG) // negate long
				.add(5, 6, LSTORE, op1) // store long
				.build();
	}

	public static Operation UNARY_MINUS_REAL(final int op1) {
		return OperationBuilder.newBuilder() // new builder
				.add(5, 0, DLOAD, op1) // load double
				.add(1, 5, DNEG) // negate double
				.add(5, 6, DSTORE, op1) // store double
				.build();
	}

	public static Operation ADD_NUM(final int op1, final int op2, final int dst) {
		return OperationBuilder.newBuilder() // new builder
				.add(5, 0, LLOAD, op1) // load left long
				.add(5, 5, LLOAD, op2) // load right long
				.add(1, 6, LADD) // add longs
				.add(5, 7, LSTORE, dst) // store long
				.build();
	}

	public static Operation ADD_REAL(final int op1, final int op2, final int dst) {
		return OperationBuilder.newBuilder() // new builder
				.add(5, 0, DLOAD, op1) // load left double
				.add(5, 5, DLOAD, op2) // load right double
				.add(1, 6, DADD) // add doubles
				.add(5, 7, DSTORE, dst) // store double
				.build();
	}

	private Operations() {
		throw new AssertionError();
	}

}
