package swp_compiler_ss13.javabite.backend.utils;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.translation.Mnemonic;

public final class ConstantUtils {

	// necessary string constants
	public static final String SYMBOL_CONSTANT_VALUE = "#";
	public static final String SYMBOL_IGNORE_PARAM = Quadruple.EmptyArgument;

	public static final String CONSTANT_VALUE_FALSE = "#FALSE";
	public static final String CONSTANT_VALUE_TRUE = "#TRUE";

	public static final String DEFAULT_VALUE_LONG = "#0";
	public static final String DEFAULT_VALUE_DOUBLE = "#0.0";
	public static final String DEFAULT_VALUE_BOOLEAN = CONSTANT_VALUE_FALSE;
	public static final String DEFAULT_VALUE_STRING = "#\"\"";

	public static boolean isBooleanConstant(final String s) {
		return s.equalsIgnoreCase(CONSTANT_VALUE_TRUE)
				|| s.equalsIgnoreCase(CONSTANT_VALUE_FALSE);
	}

	public static boolean isConstant(final String s) {
		return s != null && s.startsWith(ConstantUtils.SYMBOL_CONSTANT_VALUE);
	}

	public static boolean isIgnoreParam(final String s) {
		return s != null && s.equals(SYMBOL_IGNORE_PARAM);
	}

	public static String removeConstantSign(final String s) {
		return s.startsWith("#") ? s.substring(1) : s;
	}

	public static Mnemonic convertBooleanConstant(final String arg) {
		return CONSTANT_VALUE_TRUE.equalsIgnoreCase(arg) ? Mnemonic.ICONST_1
				: Mnemonic.ICONST_0;
	}

	public static int getArgsCount(final Quadruple q) {
		int argc = 0;
		if (!ConstantUtils.SYMBOL_IGNORE_PARAM.equals(q.getArgument1()))
			argc++;
		if (!ConstantUtils.SYMBOL_IGNORE_PARAM.equals(q.getArgument2()))
			argc++;
		if (!ConstantUtils.SYMBOL_IGNORE_PARAM.equals(q.getResult()))
			argc++;
		return argc;
	}

	public static boolean hasArgsCount(final Quadruple q,
			final int... argsCounts) {
		final int argc = getArgsCount(q);
		for (final int i : argsCounts) {
			if (i == argc)
				return true;
		}
		return false;
	}

	private ConstantUtils() {
	}

}
