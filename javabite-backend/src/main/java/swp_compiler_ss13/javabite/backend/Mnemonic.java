package swp_compiler_ss13.javabite.backend;

/**
 * <h1>Mnemonic</h1>
 * <p>
 * This enum holds all available bytecode mnemonics and their respective
 * bytecode bytes.
 * </p>
 * 
 * @author eike
 * @since 21.04.2013 00:55:21
 */
public enum Mnemonic {

	// @formatter:off
	NOP	            (0x00),
	ACONST_NULL	    (0x01, 1),
	ICONST_M1	    (0x02, 1),
	ICONST_0	    (0x03, 1),
	ICONST_1	    (0x04, 1),
	ICONST_2	    (0x05, 1),
	ICONST_3	    (0x06, 1),
	ICONST_4	    (0x07, 1),
	ICONST_5	    (0x08, 1),
	LCONST_0	    (0x09, 2),
	LCONST_1	    (0x0A, 2),
	FCONST_0	    (0x0B, 1),
	FCONST_1	    (0x0C, 1),
	FCONST_2	    (0x0D, 1),
	DCONST_0	    (0x0E, 2),
	DCONST_1	    (0x0F, 2),
	BIPUSH	        (0x10, 1, 1),
	SIPUSH	        (0x11, 2, 2),
	LDC	            (0x12, 1, 1),
	LDC_W	        (0x13, 1, 2),
	LDC2_W	        (0x14, 2, 2),
	ILOAD	        (0x15, 1, 1),
	LLOAD	        (0x16, 1, 1),
	FLOAD	        (0x17, 1, 1),
	DLOAD	        (0x18, 1, 1),
	ALOAD	        (0x19, 1, 1),
	ILOAD_0	        (0x1A, 1),
	ILOAD_1	        (0x1B, 1),
	ILOAD_2	        (0x1C, 1),
	ILOAD_3	        (0x1D, 1),
	LLOAD_0	        (0x1E, 2),
	LLOAD_1	        (0x1F, 2),
	LLOAD_2	        (0x20, 2),
	LLOAD_3	        (0x21, 2),
	FLOAD_0	        (0x22, 1),
	FLOAD_1	        (0x23, 1),
	FLOAD_2	        (0x24, 1),
	FLOAD_3	        (0x25, 1),
	DLOAD_0	        (0x26, 2),
	DLOAD_1	        (0x27, 2),
	DLOAD_2	        (0x28, 2),
	DLOAD_3	        (0x29, 2),
	ALOAD_0	        (0x2A, 1),
	ALOAD_1	        (0x2B, 1),
	ALOAD_2	        (0x2C, 1),
	ALOAD_3	        (0x2D, 1),
	IALOAD	        (0x2E, 1),
	LALOAD	        (0x2F, 2),
	FALOAD	        (0x30, 1),
	DALOAD	        (0x31, 2),
	AALOAD	        (0x32, 1),
	BALOAD	        (0x33, 1),
	CALOAD	        (0x34, 1),
	SALOAD	        (0x35, 1),
	ISTORE	        (0x36, -1, 1),
	LSTORE	        (0x37, -2, 1),
	FSTORE	        (0x38, -1, 1),
	DSTORE	        (0x39, -2, 1),
	ASTORE	        (0x3A, -1, 1),
	ISTORE_0	    (0x3B, -1),
	ISTORE_1	    (0x3C, -1),
	ISTORE_2	    (0x3D, -1),
	ISTORE_3	    (0x3E, -1),
	LSTORE_0	    (0x3F, -2),
	LSTORE_1	    (0x40, -2),
	LSTORE_2	    (0x41, -2),
	LSTORE_3	    (0x42, -2),
	FSTORE_0	    (0x43, -1),
	FSTORE_1	    (0x44, -1),
	FSTORE_2	    (0x45, -1),
	FSTORE_3	    (0x46, -1),
	DSTORE_0	    (0x47, -2),
	DSTORE_1	    (0x48, -2),
	DSTORE_2	    (0x49, -2),
	DSTORE_3	    (0x4A, -2),
	ASTORE_0	    (0x4B, -1),
	ASTORE_1	    (0x4C, -1),
	ASTORE_2	    (0x4D, -1),
	ASTORE_3	    (0x4E, -1),
	IASTORE	        (0x4F, 2),
	LASTORE	        (0x50, 2),
	FASTORE	        (0x51, 2),
	DASTORE	        (0x52, 2),
	AASTORE	        (0x53, 2),
	BASTORE	        (0x54, 2),
	CASTORE	        (0x55, 2),
	SASTORE	        (0x56, 2),
	POP	            (0x57, -1),
	POP2	        (0x58, -2),
	DUP	            (0x59, 1),
	DUP_X1	        (0x5A, 1),
	DUP_X2	        (0x5B, 1),
	DUP2	        (0x5C, 2),
	DUP2_X1	        (0x5D, 2),
	DUP2_X2	        (0x5E, 2),
	SWAP	        (0x5F),
	IADD	        (0x60, -1),
	LADD	        (0x61, -2),
	FADD	        (0x62, -1),
	DADD	        (0x63, -2),
	ISUB	        (0x64, -1),
	LSUB	        (0x65, -2),
	FSUB	        (0x66, -1),
	DSUB	        (0x67, -2),
	IMUL	        (0x68, -1),
	LMUL	        (0x69, -2),
	FMUL	        (0x6A, -1),
	DMUL	        (0x6B, -2),
	IDIV	        (0x6C, -1),
	LDIV	        (0x6D, -2),
	FDIV	        (0x6E, -1),
	DDIV	        (0x6F, -2),
	IREM	        (0x70, -1),
	LREM	        (0x71, -2),
	FREM	        (0x72, -1),
	DREM	        (0x73, -2),
	INEG	        (0x74),
	LNEG	        (0x75),
	FNEG	        (0x76),
	DNEG	        (0x77),
	ISHL	        (0x78),
	LSHL	        (0x79),
	ISHR	        (0x7A),
	LSHR	        (0x7B),
	IUSHR	        (0x7C),
	LUSHR	        (0x7D),
	IAND	        (0x7E, -1),
	LAND	        (0x7F, -2),
	IOR	            (0x80, -1),
	LOR	            (0x81, -2),
	IXOR	        (0x82, -1),
	LXOR	        (0x83, -2),
	IINC	        (0x84, 0, 2),
	I2L	            (0x85, 1),
	I2F	            (0x86),
	I2D	            (0x87, 1),
	L2I	            (0x88, -1),
	L2F	            (0x89, -1),
	L2D	            (0x8A),
	F2I	            (0x8B),
	F2L	            (0x8C, 1),
	F2D	            (0x8D, 1),
	D2I	            (0x8E, -1),
	D2L	            (0x8F),
	D2F	            (0x90, -1),
	I2B	            (0x91),
	I2C	            (0x92),
	I2S	            (0x93),
	LCMP	        (0x94, -2),
	FCMPL	        (0x95, -1),
	FCMPG	        (0x96, -1),
	DCMPL	        (0x97, -2),
	DCMPG	        (0x98, -2),
	IFEQ	        (0x99, -1, 2),
	IFNE	        (0x9A, -1, 2),
	IFLT	        (0x9B, -1, 2),
	IFGE	        (0x9C, -1, 2),
	IFGT	        (0x9D, -1, 2),
	IFLE	        (0x9E, -1, 2),
	IF_ICMPEQ	    (0x9F, -2, 2),
	IF_ICMPNE	    (0xA0, -2, 2),
	IF_ICMPLT	    (0xA1, -2, 2),
	IF_ICMPGE	    (0xA2, -2, 2),
	IF_ICMPGT	    (0xA3, -2, 2),
	IF_ICMPLE	    (0xA4, -2, 2),
	IF_ACMPEQ	    (0xA5, -2, 2),
	IF_ACMPNE	    (0xA6, -2, 2),
	GOTO	        (0xA7, 0, 2),
	JSR	            (0xA8, 1, 2),
	RET	            (0xA9, 0, 1),
	TABLESWITCH	    (0xAA, 0, 4),
	// also 4+
	LOOKUPSWITCH	(0xAB, 0, 4),
	// also 4+
	IRETURN	        (0xAC, -1),
	LRETURN	        (0xAD, -2),
	FRETURN	        (0xAE, -1),
	DRETURN	        (0xAF, -2),
	ARETURN	        (0xB0, -1),
	RETURN	        (0xB1),
	GETSTATIC	    (0xB2, 0, 2),
	PUTSTATIC	    (0xB3, 0, 2),
	GETFIELD	    (0xB4, 0, 2),
	PUTFIELD	    (0xB5, 0, 2),
	INVOKEVIRTUAL	(0xB6, 0, 2),
	INVOKESPECIAL	(0xB7, 0, 2),
	INVOKESTATIC	(0xB8, 0, 2),
	INVOKEINTERFACE	(0xB9, 0, 4),
	INVOKEDYNAMIC	(0xBA, 0, 4),
	NEW	            (0xBB, 0, 2),
	NEWARRAY	    (0xBC, 0, 1),
	ANEWARRAY	    (0xBD, 0, 2),
	ARRAYLENGTH	    (0xBE),
	ATHROW	        (0xBF),
	CHECKCAST	    (0xC0, 0, 2),
	INSTANCEOF	    (0xC1, 0, 2),
	MONITORENTER	(0xC2),
	MONITOREXIT	    (0xC3),
	WIDE	        (0xC4, 0, 3),
	// also 5
	MULTIANEWARRAY	(0xC5, 0, 3),
	IFNULL	        (0xC6, 0, 2),
	IFNONNULL	    (0xC7, 0, 2),
	GOTO_W	        (0xC8, 0, 4),
	JSR_W	        (0xC9, 0, 4),
	BREAKPOINT	    (0xCA),
	// 0xCB .. 0XFD not assigned
	IMPDEP1	        (0xFE),
	IMPDEP2	        (0xFF);
	// @formatter:on

	private final byte byteCode;
	private final short stackChange;
	private final int argc;

	private Mnemonic(final int hexCode, final int stackChange, final int argc) {
		this.byteCode = (byte) hexCode;
		this.stackChange = (short) stackChange;
		this.argc = argc;
	}

	private Mnemonic(final int hexCode, final int stackChange) {
		this(hexCode, stackChange, 0);
	}

	private Mnemonic(final int hexCode) {
		this(hexCode, 0, 0);
	}

	/**
	 * Returns the mnemonic name of this bytecode operation. The mnemonic will
	 * be returned in all uppercase letters, because the enum-method name() is
	 * used to retrieve the mnemonic.
	 * 
	 * @return the mnemonic of this operation
	 */
	public String getMnemonic() {
		return name();
	}

	public int getArgsCount() {
		return argc;
	}

	/**
	 * Returns the bytecode operation byte as a string in hexadecimal format,
	 * eg. 01 or 5A. This is the string representation of the byte, and is
	 * created by calling Integer.toHexString(int). Note that the hexadecimal
	 * prefix 0x is missing. This prefix is intentionally omitted, so that this
	 * method can be used to visualize toe bytecode.
	 * 
	 * @return hexadecimal string of byte
	 */
	public String getBytecodeString() {
		return Integer.toHexString(byteCode);
	}

	/**
	 * Returns the bytecode operation byte as integer.
	 * 
	 * @return bytecode byte as integer
	 */
	public byte getBytecode() {
		return byteCode;
	}

	public static Mnemonic getMnemonic(final String prefix, final int value) {
		try {
			final String name = prefix + "_"
					+ (value == -1 ? "M1" : Integer.toString(value));
			final Mnemonic m = valueOf(name);
			return m;
		} catch (final IllegalArgumentException e) {
			return valueOf(prefix);
		}
	}

	public static Mnemonic getMnemonic(final String prefix) {
		return valueOf(prefix);
	}

	public static Mnemonic ICONST(final int value) {
		return getMnemonic("ICONST", value);
	}

	public static Mnemonic LCONST(final int value) {
		return getMnemonic("LCONST", value);
	}

	public static Mnemonic FCONST(final int value) {
		return getMnemonic("FCONST", value);
	}

	public static Mnemonic DCONST(final int value) {
		return getMnemonic("DCONST", value);
	}

	public static Mnemonic ILOAD(final int value) {
		return getMnemonic("ILOAD", value);
	}

	public static Mnemonic LLOAD(final int value) {
		return getMnemonic("LLOAD", value);
	}

	public static Mnemonic FLOAD(final int value) {
		return getMnemonic("FLOAD", value);
	}

	public static Mnemonic DLOAD(final int value) {
		return getMnemonic("DLOAD", value);
	}

	public static Mnemonic ALOAD(final int value) {
		return getMnemonic("ALOAD", value);
	}

	public static Mnemonic ISTORE(final int value) {
		return getMnemonic("ISTORE", value);
	}

	public static Mnemonic LSTORE(final int value) {
		return getMnemonic("LSTORE", value);
	}

	public static Mnemonic FSTORE(final int value) {
		return getMnemonic("FSTORE", value);
	}

	public static Mnemonic DSTORE(final int value) {
		return getMnemonic("DSTORE", value);
	}

	public static Mnemonic ASTORE(final int value) {
		return getMnemonic("ASTORE", value);
	}

	public short getStackChange() {
		return stackChange;
	}

}
