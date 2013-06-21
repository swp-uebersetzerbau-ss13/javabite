package swp_compiler_ss13.javabite.backend.utils;

public final class ClassfileUtils {

	/**
	 * <h1>ClassfileAccessFlag</h1>
	 * <p>
	 * Enumeration of possible classfile access flags.
	 * </P>
	 * 
	 * @author Marco
	 * @since 03.05.2013
	 */
	public enum ClassfileAccessFlag {
		ACC_PUBLIC(0x0001), ACC_FINAL(0x0010), ACC_SUPER(0x0020), ACC_INTERFACE(
				0x0200), ACC_ABSTRACT(0x0400), ACC_SYNTHETIC(0x1000), ACC_ANNOTATION(
				0x2000), ACC_ENUM(0x4000);

		private final short value;

		ClassfileAccessFlag(final int value) {
			this.value = (short) value;
		}

		public short getValue() {
			return value;
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
	public enum MethodAccessFlag {
		ACC_PUBLIC(0x0001), ACC_PRIVATE(0x0002), ACC_PROTECTED(0x0004), ACC_STATIC(
				0x0008), ACC_FINAL(0x0010), ACC_SYNCHRONIZED(0x0020), ACC_BRIDGE(
				0x0040), ACC_VARARGS(0x0080), ACC_NATIVE(0x0100), ACC_ABSTRACT(
				0x0400), ACC_STRICT(0x0800), ACC_SYNTHETIC(0x1000);

		private final short value;

		MethodAccessFlag(final int value) {
			this.value = (short) value;
		}

		public short getValue() {
			return value;
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
	public enum FieldAccessFlag {
		ACC_PUBLIC(0x0001), ACC_PRIVATE(0x0002), ACC_PROTECTED(0x0004), ACC_STATIC(
				0x0008), ACC_FINAL(0x0010), ACC_VOLATILE(0x0040), ACC_TRANSIENT(
				0x0080), ACC_SYNTHETIC(0x1000), ACC_ENUM(0x4000);

		private final short value;

		FieldAccessFlag(final int value) {
			this.value = (short) value;
		}

		public short getValue() {
			return value;
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
	public enum VariableType {
		LONG(2), DOUBLE(2), STRING(1), BOOLEAN(1), AREF(1);

		private final short length;

		VariableType(final int length) {
			this.length = (short) length;
		}

		public short getLength() {
			return length;
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
		NONE(0x00, false), UTF8(0x01, false), LONG(0x05, true), DOUBLE(0x06,
				true), CLASS(0x07, false), STRING(0x08, false), METHODREF(0x0a,
				false), NAMEANDTYPE(0x0c, false), FIELDREF(0x09, false);

		final byte tag;
		final boolean wide;

		ConstantPoolType(final int tag, final boolean wide) {
			this.tag = (byte) tag;
			this.wide = wide;
		}

		public byte getByte() {
			return tag;
		}

		public boolean isWide() {
			return wide;
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
	public enum ArrayType {
		BOOLEAN(0x04, "Z"), DOUBLE(0x07, "D"), LONG(0x0b, "J"), STRING(
				"java/lang/String");

		private final byte value;
		private final String className;

		ArrayType(final int value, final String className) {
			this.value = (byte) value;
			this.className = className;
		}

		ArrayType(final String className) {
			this(0, className);
		}

		public byte getValue() {
			return value;
		}

		public String getClassName() {
			return className;
		}

		public boolean isPrimitive() {
			return value != 0;
		}
	}

	private ClassfileUtils() {
	}

}
