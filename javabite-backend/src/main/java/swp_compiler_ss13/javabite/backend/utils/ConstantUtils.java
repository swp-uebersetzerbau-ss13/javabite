package swp_compiler_ss13.javabite.backend.utils;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.translation.Mnemonic;

/**
 * <h1>ConstantUtils</h1>
 * <p>
 * This class provides constant strings and static methods to help working with
 * constant values from the tac.
 * </p>
 */
public final class ConstantUtils {

	/**
	 * This character is used to denote a constant value
	 */
	public static final String SYMBOL_CONSTANT_VALUE = "#";

	/**
	 * This character is used to fill in unused arguments in a quadruple
	 */
	public static final String SYMBOL_IGNORE_PARAM = Quadruple.EmptyArgument;

	/**
	 * The boolean constant value false
	 */
	public static final String CONSTANT_VALUE_FALSE = "#FALSE";

	/**
	 * The boolean constant value true
	 */
	public static final String CONSTANT_VALUE_TRUE = "#TRUE";

	/**
	 * The default long constant value 0L
	 */
	public static final String DEFAULT_VALUE_LONG = "#0";

	/**
	 * The default double constant value 0.0D
	 */
	public static final String DEFAULT_VALUE_DOUBLE = "#0.0";

	/**
	 * The default boolean value false
	 */
	public static final String DEFAULT_VALUE_BOOLEAN = CONSTANT_VALUE_FALSE;

	/**
	 * The default string value "" (empty string)
	 */
	public static final String DEFAULT_VALUE_STRING = "#\"\"";

	/**
	 * Returns true if the passed string contains a boolean constant value
	 * 
	 * @param s
	 *            constant value
	 * @return true if boolean constant
	 */
	public static boolean isBooleanConstant(final String s) {
		return s.equalsIgnoreCase(CONSTANT_VALUE_TRUE)
				|| s.equalsIgnoreCase(CONSTANT_VALUE_FALSE);
	}

	/**
	 * Returns true if the passed string contains a constant value (boolean,
	 * string, long or double)
	 * 
	 * @param s
	 *            constant string
	 * @return true if constant
	 */
	public static boolean isConstant(final String s) {
		return s != null && s.startsWith(ConstantUtils.SYMBOL_CONSTANT_VALUE);
	}

	/**
	 * Returns true if the passed string contains the ignore character
	 * 
	 * @param s
	 *            input string
	 * @return true if ignore
	 */
	public static boolean isIgnoreParam(final String s) {
		return s != null && s.equals(SYMBOL_IGNORE_PARAM);
	}

	/**
	 * Removes the constant sign from the passed string if it is present
	 * 
	 * @param s
	 *            input string
	 * @return string without leading constant sign
	 */
	public static String removeConstantSign(final String s) {
		return s != null && s.startsWith("#") ? s.substring(1) : s;
	}

	/**
	 * Removes the quotation marks from the passed string. This method does not
	 * check if they are present, it just removes the first and last character.
	 * 
	 * @param s
	 *            input string
	 * @return string without quotation marks
	 */
	public static String removeQuotationMarks(final String s) {
		return s != null && s.length() > 1 ? s.substring(1, s.length() - 1) : s;
	}

	/**
	 * Converts boolean constants to their mnemonic equivalent. Because the jvm
	 * uses integer values (0, 1) for representing boolean values, the quadruple
	 * values have to be converted.
	 * 
	 * @param arg
	 * @return
	 */
	public static Mnemonic convertBooleanConstant(final String arg) {
		return CONSTANT_VALUE_TRUE.equalsIgnoreCase(arg) ? Mnemonic.ICONST_1
				: Mnemonic.ICONST_0;
	}

	/**
	 * Returns the number of arguments in the quadruple that are not to be
	 * ignored (not equals ignore character)
	 * 
	 * @param q
	 *            quadruple to count arguments
	 * @return number of arguments in the quadruple
	 */
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

	/**
	 * Returns true if the quadruple contains as many arguments as specified.
	 * 
	 * @param q
	 *            quadruple with arguments
	 * @param argsCounts
	 *            numbers of arguments to be expected
	 * @return true if any number if argsCount equals the number of arguments in
	 *         the quadruple
	 */
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
