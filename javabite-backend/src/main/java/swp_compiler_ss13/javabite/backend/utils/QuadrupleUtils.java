package swp_compiler_ss13.javabite.backend.utils;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public final class QuadrupleUtils {

	public static Quadruple copyOf(final Quadruple q,
			final Quadruple.Operator op, final String arg1, final String arg2,
			final String res) {
		return new QuadrupleJb(op != null ? op : q.getOperator(),
				arg1 != null ? arg1 : q.getArgument1(), arg2 != null ? arg2
						: q.getArgument2(), res != null ? res : q.getResult());
	}

	private QuadrupleUtils() {
	}
}
