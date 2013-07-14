package swp_compiler_ss13.javabite.backend.utils;

import org.apache.commons.lang.StringUtils;

/**
 * <h1>ClassSignature</h1>
 * <p>
 * This class is used to create a class signature, which is used by the jvm to
 * reference classes of any type.
 * </p>
 */
public class ClassSignature {

	public final String baseClassName;
	public final String className;
	public final String typeClassName;
	public final boolean isPrimitive;
	public final boolean isArray;
	public final byte arrayDimensions;

	/**
	 * Creates a new ClassSignature instance.
	 * 
	 * @param clazz
	 *            class to create a signature from
	 */
	public ClassSignature(final Class<?> clazz) {
		this(getClassName(clazz), null, clazz.isPrimitive());
	}

	/**
	 * Creates a new ClassSignature instance
	 * 
	 * @param classNameStart
	 *            first part of class name
	 * @param classNameParts
	 *            last parts of class name
	 */
	public ClassSignature(final String classNameStart,
			final String... classNameParts) {
		this(joinClassName(classNameStart, classNameParts), null, false);
	}

	/**
	 * Creates a new ClassSignature instance
	 * 
	 * @param className
	 *            name of class
	 */
	public ClassSignature(final String className) {
		this(className, null, false);
	}

	/**
	 * Creates a new ClassSignature instance
	 * 
	 * @param arrayDimensions
	 *            number of array dimensions
	 * @param classNameStart
	 *            first part of class name
	 * @param classNameParts
	 *            last parts of class name
	 */
	public ClassSignature(final int arrayDimensions,
			final String classNameStart, final String... classNameParts) {
		this(joinClassName(classNameStart, classNameParts), arrayDimensions,
				false);
	}

	/**
	 * Creates a new ClassSignature instance
	 * 
	 * @param arrayDimensions
	 *            number of array dimensions
	 * @param className
	 *            name of class
	 */
	public ClassSignature(final int arrayDimensions, final String className) {
		this(className, arrayDimensions, false);
	}

	/**
	 * Creates a new ClassSignature instance
	 * 
	 * @param className
	 *            name of class
	 * @param arrayDimensions
	 *            number of array dimensions
	 * @param isPrimitive
	 *            true if class is primitive (int, long, double...)
	 */
	private ClassSignature(final String className,
			final Integer arrayDimensions, final boolean isPrimitive) {
		this.isPrimitive = isPrimitive || isPrimitiveClass(className);

		final int dimensions = arrayDimensions == null ? countArrayDimensions(className)
				: arrayDimensions;
		final String cleanClassName = cleanClassName(className);
		baseClassName = cleanClassName;
		this.className = classNameWithDimensions(cleanClassName, dimensions);

		isArray = dimensions > 0;

		if (isPrimitive) {
			typeClassName = this.className;
		} else {
			typeClassName = StringUtils.leftPad("", dimensions, '[')
					+ classAsType(cleanClassName);
		}
		this.arrayDimensions = (byte) dimensions;
	}

	@Override
	public String toString() {
		return className;
	}

	/**
	 * Returns the class name of the passed class. The class name is the direct
	 * and simple name of the class, without packages, without type specifiers
	 * (L;), and if it is a primitive type, the equivalent identifier char.
	 * 
	 * @param clazz
	 *            class to return name from
	 * @return name of class
	 */
	public static String getClassName(final Class<?> clazz) {
		if (clazz.isPrimitive()) {
			if (clazz == void.class)
				return "V";
			if (clazz == int.class)
				return "I";
			if (clazz == long.class)
				return "J";
			if (clazz == double.class)
				return "D";
			if (clazz == boolean.class)
				return "Z";
			if (clazz == byte.class)
				return "B";
			if (clazz == char.class)
				return "C";
			if (clazz == float.class)
				return "F";
			if (clazz == short.class)
				return "S";
			return null;
		} else {
			String className = clazz.getName().replaceAll("\\.", "/");
			if (className.endsWith(";")) {
				className = className.substring(0, className.length() - 1)
						.replaceFirst("L", "");
			}
			return className;
		}
	}

	/**
	 * Returns true if the passed class name describes a primitive class. Note:
	 * this method does not distunguish between the primitive type class
	 * identifiers (I, J, D, etc) and possible class names. Therefore, if a
	 * class "J" exists, this method will still return true, because J is also a
	 * primitive type, long.
	 * 
	 * @param s
	 *            name of class
	 * @return true if class name describes a primitive type
	 */
	public static boolean isPrimitiveClass(final String s) {
		return s.length() == 1 && "VIJDZBCFS".contains(s);
	}

	/**
	 * Counts the array dimensions of the class name. The array dimensions are
	 * equivalent to the number of opening braces [.
	 * 
	 * @param s
	 *            name of class
	 * @return number of array dimensions in the class name
	 */
	public static int countArrayDimensions(final String s) {
		return StringUtils.countMatches(s, "[");
	}

	/**
	 * Appends the type informations to a class name, if it's class is not
	 * primitive. Non-primitive class names are formatted like Lclassname; if
	 * used as a type. The jvm requires this format to distinguish between
	 * primitive and nonprimitive types.
	 * 
	 * @param className
	 *            name of class
	 * @return class name as type
	 */
	public static String classAsType(final String className) {
		if (isPrimitiveClass(className))
			return className;
		return (!className.startsWith("L") ? "L" : "") + className
				+ (!className.endsWith(";") ? ";" : "");
	}

	/**
	 * Cleans a class name of any array dimensions and type information.
	 * 
	 * @param className
	 *            class name
	 * @return the cleaned class name
	 */
	public static String cleanClassName(String className) {
		className = className.replaceAll("\\[*", "");
		if (className.endsWith(";")) {
			className = className.substring(1, className.length() - 1);
		}
		return className;
	}

	/**
	 * Appends array dimensions to a class name
	 * 
	 * @param cleanClassName
	 *            the class name to append array dimensions to
	 * @param arrayDimensions
	 *            number of array dimensions
	 * @return the new class name
	 */
	public static String classNameWithDimensions(final String cleanClassName,
			final int arrayDimensions) {
		return StringUtils.leftPad("", arrayDimensions, '[') + cleanClassName;
	}

	/**
	 * Filters out every non legal character for java identifiers.
	 * 
	 * @param s
	 *            string to check for invalid characters
	 * @return valid java identifier
	 */
	public static String rectifyJavaIdentifier(final String s) {
		final StringBuilder sb = new StringBuilder();

		final char[] c = s.toCharArray();
		if (Character.isJavaIdentifierStart(c[0])) {
			sb.append(Character.toUpperCase(c[0]));
		}

		for (int i = 1; i < c.length; i++) {
			if (Character.isJavaIdentifierPart(c[i])) {
				sb.append(c[i]);
			}
		}

		return sb.toString();
	}

	/**
	 * Joins a class name with a special delimiter
	 * 
	 * @param classNameStart
	 *            the first part of the new class name
	 * @param classNameParts
	 *            the last parts of the new class name
	 * @return the joined new class name
	 */
	public static String joinClassName(final String classNameStart,
			final String... classNameParts) {
		if (classNameParts != null && classNameParts.length > 0) {
			return classNameStart + '_' + StringUtils.join(classNameParts, '_');
		}
		return classNameStart;
	}

}
