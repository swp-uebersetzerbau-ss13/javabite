package swp_compiler_ss13.javabite.backend.utils;

import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.javabite.backend.translation.Mnemonic;

public final class ClassfileUtils {

	public static final EnumSet<Operator> OPERATOR_LONG_TYPES = EnumSet.of(
			Operator.ASSIGN_LONG, Operator.ADD_LONG, Operator.SUB_LONG,
			Operator.MUL_LONG, Operator.DIV_LONG, Operator.DECLARE_ARRAY,
			Operator.ARRAY_GET_LONG, Operator.ARRAY_GET_DOUBLE,
			Operator.ARRAY_GET_BOOLEAN, Operator.ARRAY_GET_STRING,
			Operator.ARRAY_GET_REFERENCE, Operator.COMPARE_LONG_E,
			Operator.COMPARE_LONG_G, Operator.COMPARE_LONG_L,
			Operator.COMPARE_LONG_GE, Operator.COMPARE_LONG_LE,
			Operator.RETURN, Operator.STRUCT_SET_LONG, Operator.DECLARE_LONG,
			Operator.DECLARE_STRUCT);

	public static final EnumSet<Operator> OPERATOR_DOUBLE_TYPES = EnumSet.of(
			Operator.ASSIGN_DOUBLE, Operator.ADD_DOUBLE, Operator.SUB_DOUBLE,
			Operator.MUL_DOUBLE, Operator.DIV_DOUBLE,
			Operator.COMPARE_DOUBLE_E, Operator.COMPARE_DOUBLE_G,
			Operator.COMPARE_DOUBLE_L, Operator.COMPARE_DOUBLE_GE,
			Operator.COMPARE_DOUBLE_LE, Operator.STRUCT_SET_DOUBLE,
			Operator.DECLARE_DOUBLE);

	public static final EnumSet<Operator> OPERATOR_STRING_TYPES = EnumSet.of(
			Operator.ASSIGN_STRING, Operator.PRINT_STRING,
			Operator.CONCAT_STRING, Operator.STRUCT_SET_STRING,
			Operator.DECLARE_STRING);

	public static final EnumSet<Operator> OPERATOR_BOOLEAN_TYPES = EnumSet.of(
			Operator.ASSIGN_BOOLEAN, Operator.STRUCT_SET_BOOLEAN,
			Operator.DECLARE_BOOLEAN);

	/**
	 * <h1>ClassfileAccessFlag</h1>
	 * <p>
	 * Enumeration of possible classfile access flags.
	 * </P>
	 * 
	 * @author Marco
	 * @since 03.05.2013
	 */
	@SuppressWarnings("unused")
	public enum ClassfileAccessFlag {
		ACC_PUBLIC(0x0001), ACC_FINAL(0x0010), ACC_SUPER(0x0020), ACC_INTERFACE(
				0x0200), ACC_ABSTRACT(0x0400), ACC_SYNTHETIC(0x1000), ACC_ANNOTATION(
				0x2000), ACC_ENUM(0x4000);

		public final short value;

		ClassfileAccessFlag(final int value) {
			this.value = (short) value;
		}

	}

	/**
	 * <h1>MethodAccessFlag</h1>
	 * <p>
	 * Enumeration of possible method access flags.
	 * </p>
	 * 
	 * @author Marco
	 * @since 03.05.2013
	 */
	@SuppressWarnings("unused")
	public enum MethodAccessFlag {
		ACC_PUBLIC(0x0001), ACC_PRIVATE(0x0002), ACC_PROTECTED(0x0004), ACC_STATIC(
				0x0008), ACC_FINAL(0x0010), ACC_SYNCHRONIZED(0x0020), ACC_BRIDGE(
				0x0040), ACC_VARARGS(0x0080), ACC_NATIVE(0x0100), ACC_ABSTRACT(
				0x0400), ACC_STRICT(0x0800), ACC_SYNTHETIC(0x1000);

		public final short value;

		MethodAccessFlag(final int value) {
			this.value = (short) value;
		}

	}

	/**
	 * <h1>FieldAccessFlag</h1>
	 * <p>
	 * Enumeration of possible field access flags.
	 * </p>
	 * 
	 * @author Marco
	 * @since 03.05.2013
	 */
	@SuppressWarnings("unused")
	public enum FieldAccessFlag {
		ACC_PUBLIC(0x0001), ACC_PRIVATE(0x0002), ACC_PROTECTED(0x0004), ACC_STATIC(
				0x0008), ACC_FINAL(0x0010), ACC_VOLATILE(0x0040), ACC_TRANSIENT(
				0x0080), ACC_SYNTHETIC(0x1000), ACC_ENUM(0x4000);

		public final short value;

		FieldAccessFlag(final int value) {
			this.value = (short) value;
		}

	}

	/**
	 * <h1>VariableTypes</h1>
	 * <p>
	 * This enumeration defines possible types and their length of entries in
	 * the local variable space of a methods code attribute.
	 * </p>
	 * 
	 * @author Marco
	 * @since 03.05.2013
	 */
	public enum LocalVariableType {
		LONG(2, ConstantPoolType.LONG, JavaType.LONG, Mnemonic.LDC2_W,
				Mnemonic.LLOAD, Mnemonic.LALOAD, Mnemonic.LSTORE,
				Mnemonic.LASTORE), DOUBLE(2, ConstantPoolType.DOUBLE,
				JavaType.DOUBLE, Mnemonic.LDC2_W, Mnemonic.DLOAD,
				Mnemonic.DALOAD, Mnemonic.DSTORE, Mnemonic.DASTORE), STRING(1,
				ConstantPoolType.STRING, JavaType.STRING, Mnemonic.LDC,
				Mnemonic.ALOAD, Mnemonic.AALOAD, Mnemonic.ASTORE,
				Mnemonic.AASTORE), BOOLEAN(1, null, JavaType.BOOLEAN, null,
				Mnemonic.ILOAD, Mnemonic.IALOAD, Mnemonic.ISTORE,
				Mnemonic.IASTORE), AREF(1, null, null, null, Mnemonic.ALOAD,
				Mnemonic.AALOAD, Mnemonic.ASTORE, Mnemonic.AASTORE);

		public final short length;
		public final boolean wide;
		public final ConstantPoolType constantPoolType;
		public final JavaType javaType;
		public final Mnemonic constantLoadOp;
		public final Mnemonic varLoadOp;
		public final Mnemonic arrayLoadOp;
		public final Mnemonic varStoreOp;
		public final Mnemonic arrayStoreOp;

		LocalVariableType(final int length,
				final ConstantPoolType constantPoolType,
				final JavaType javaType, final Mnemonic constantLoadOp,
				final Mnemonic varLoadOp, final Mnemonic arrayLoadOp,
				final Mnemonic varStoreOp, final Mnemonic arrayStoreOp) {
			this.length = (short) length;
			wide = length == 2;
			this.constantPoolType = constantPoolType;
			this.javaType = javaType;
			this.constantLoadOp = constantLoadOp;
			this.varLoadOp = varLoadOp;
			this.arrayLoadOp = arrayLoadOp;
			this.varStoreOp = varStoreOp;
			this.arrayStoreOp = arrayStoreOp;
		}
	}

	/**
	 * <h1>InfoTag</h1>
	 * <p>
	 * This enumeration defines the types of entries in a classfile's constant
	 * pool.
	 * </p>
	 * 
	 * @author Eike
	 * @since May 25, 2013 1:27:22 AM
	 */
	public enum ConstantPoolType {
		NONE(0x00), UTF8(0x01), LONG(0x05), DOUBLE(0x06), CLASS(0x07), STRING(
				0x08), METHODREF(0x0a), NAMEANDTYPE(0x0c), FIELDREF(0x09);

		public final byte tagByte;

		ConstantPoolType(final int tagByte) {
			this.tagByte = (byte) tagByte;
		}

		/**
		 * <h1>getByOperator</h1>
		 * <p>
		 * This method maps operators to the respective data types of which the
		 * constants possibly being used in this operation are expected to be.
		 * For instance: ASSIGN_LONG maps to ConstantPoolType.LONG, because it
		 * assigns a long and a possibly used constant is expected to be exactly
		 * of this type.
		 * </p>
		 * 
		 * TODO: Expand operator by expected constant type or "multiple"?
		 * 
		 * @since 09.05.2013
		 * 
		 * @param operator
		 *            the operator to be examined
		 * @return ConstantPoolType value which describes, of which type the
		 *         operation constants are expected to be
		 */
		public static ConstantPoolType getByOperator(final Operator operator) {
			if (OPERATOR_LONG_TYPES.contains(operator))
				return LONG;
			if (OPERATOR_DOUBLE_TYPES.contains(operator))
				return DOUBLE;
			if (OPERATOR_STRING_TYPES.contains(operator))
				return STRING;
			return null;
		}

	}

	/**
	 * PrimitiveArrayType enum. This enum holds possible values for array types.
	 * It is used to create arrays, because the opcode NEWARRAY requires a type
	 * argument.
	 * 
	 * @author eike
	 * @since Jun 3, 2013 8:29:55 PM
	 */
	public enum JavaType {
		BOOLEAN(0x04, "Z"), DOUBLE(0x07, "D"), LONG(0x0b, "J"), STRING(
				"java/lang/String");

		public final byte value;
		public final String className;

		JavaType(final int value, final String className) {
			this.value = (byte) value;
			this.className = className;
		}

		JavaType(final String classEif) {
			this(0, classEif);
		}

		public boolean isPrimitive() {
			return value != 0;
		}

		public static JavaType getByOperator(final Operator operator) {
			if (OPERATOR_LONG_TYPES.contains(operator))
				return LONG;
			if (OPERATOR_DOUBLE_TYPES.contains(operator))
				return DOUBLE;
			if (OPERATOR_STRING_TYPES.contains(operator))
				return STRING;
			if (OPERATOR_BOOLEAN_TYPES.contains(operator))
				return BOOLEAN;
			return null;
		}

	}

	public static String typeByQuadruples(final Quadruple quad) {
		return JavaType.getByOperator(quad.getOperator()).className;
	}

	public static String typeByQuadruples(final List<Quadruple> tac) {
		switch (tac.get(0).getOperator()) {
		case DECLARE_ARRAY:
			int dimensions = 0;
			for (final Quadruple quad : tac) {
				if (quad.getOperator() == Operator.DECLARE_ARRAY) {
					dimensions++;
				}
			}
			return StringUtils.leftPad("", dimensions, '[')
					+ JavaType.getByOperator(tac.get(tac.size() - 1)
							.getOperator()).className;

		case DECLARE_STRUCT:
			return tac.get(0).getResult();

		default:
			return null;
		}
	}

	public static class MethodSignature {

		public final String methodClass;
		public final String methodName;
		public final String methodReturnClass;
		public final String[] methodArgsClasses;
		public final String methodDescriptor;

		public MethodSignature(final String methodName,
				final Class<?> methodClass, final Class<?> methodReturnClass,
				final Class<?>... params) {
			this.methodClass = getClassName(methodClass, false);
			this.methodName = methodName;
			this.methodReturnClass = getClassName(methodReturnClass, true);
			if (params == null) {
				methodArgsClasses = null;
			} else {
				methodArgsClasses = new String[params.length];
				for (int i = 0; i < params.length; i++) {
					methodArgsClasses[i] = getClassName(params[i], true);
				}
			}
			methodDescriptor = "(" + StringUtils.join(methodArgsClasses) + ")"
					+ this.methodReturnClass;
		}

		@Override
		public String toString() {
			return methodClass + "." + methodName + ":" + methodDescriptor;
		}

	}

	public static class FieldSignature {

		public final String fieldClass;
		public final String fieldName;
		public final String fieldDescriptor;

		public FieldSignature(final String fieldName,
				final Class<?> containerClass, final Class<?> fieldClass) {
			this.fieldClass = getClassName(containerClass, false);
			this.fieldName = fieldName;
			fieldDescriptor = getClassName(fieldClass, true);
		}

		@Override
		public String toString() {
			return fieldClass + "." + fieldName + ":" + fieldDescriptor;
		}
	}

	public static String getClassName(final Class<?> clazz,
			final boolean isParam) {
		if (clazz.isPrimitive()) {
			return getPrimitiveClassName(clazz);
		}
		if (isParam) {
			if (clazz.isArray())
				return clazz.getName().replaceAll("\\.", "/");
			return "L" + clazz.getName().replaceAll("\\.", "/") + ";";
		}
		return clazz.getName().replaceAll("\\.", "/");
	}

	public static String getPrimitiveClassName(final Class<?> clazz) {
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
	}

	private ClassfileUtils() {
	}

}
