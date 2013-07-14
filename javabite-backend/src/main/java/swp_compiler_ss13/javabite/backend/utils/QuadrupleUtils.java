package swp_compiler_ss13.javabite.backend.utils;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

/**
 * <h1>QuadrupleUtils</h1>
 * <p>
 * This class offsers static methods to extend the functionability of
 * quadruples.
 * </p>
 */
public final class QuadrupleUtils {

	/**
	 * Copies a quadruple and its contents, replaces every part of the new
	 * quadruple with the passed values, if they are not null. All other
	 * arguments are taken from the source quadruple
	 * 
	 * @param q
	 *            quadruple to copy
	 * @param op
	 *            operation to replace or null
	 * @param arg1
	 *            argument1 to replace or null
	 * @param arg2
	 *            argument2 to replace or null
	 * @param res
	 *            result to replace or null
	 * @return new quadruple instance
	 */
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
