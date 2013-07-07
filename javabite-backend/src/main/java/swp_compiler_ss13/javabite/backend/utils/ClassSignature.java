package swp_compiler_ss13.javabite.backend.utils;

import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA. User: eike Date: 7/7/13 Time: 12:53 AM To change
 * this template use File | Settings | File Templates.
 */
public class ClassSignature {

	public final String baseClassName;
	public final String className;
	public final String typeClassName;
	public final boolean isPrimitive;
	public final boolean isArray;
	public final byte arrayDimensions;

	public ClassSignature(final Class<?> clazz) {
		this(getClassName(clazz), null, clazz.isPrimitive());
	}

	public ClassSignature(final String... classNameParts) {
		this(StringUtils.join(classNameParts, '_'), null, false);
	}

	public ClassSignature(final String className) {
		this(className, null, false);
	}

	public ClassSignature(final int arrayDimensions,
			final String... classNameParts) {
		this(StringUtils.join(classNameParts, '_'), arrayDimensions, false);
	}

	public ClassSignature(final int arrayDimensions, final String className) {
		this(className, arrayDimensions, false);
	}

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

	public static boolean isPrimitiveClass(final String s) {
		return s.length() == 1 && "VIJDZBCFS".contains(s);
	}

	public static int countArrayDimensions(final String s) {
		return StringUtils.countMatches(s, "[");
	}

	public static String classAsType(final String className) {
		if (isPrimitiveClass(className))
			return className;
		return (!className.startsWith("L") ? "L" : "") + className
				+ (!className.endsWith(";") ? ";" : "");
	}

	public static String cleanClassName(String className) {
		className = className.replaceAll("\\[*", "");
		if (className.endsWith(";")) {
			className = className.substring(1, className.length() - 1);
		}
		return className;
	}

	private static String classNameWithDimensions(final String cleanClassName,
			final int arrayDimensions) {
		return StringUtils.leftPad("", arrayDimensions, '[') + cleanClassName;
	}

}
