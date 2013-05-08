package swp_compiler_ss13.javabite.backend;

/**
 * <h1>Mnemonic-enum</h1>
 * <p>
 * This enum holds all available bytecode mnemonics and their respective
 * bytecode bytes.
 * </p>
 * 
 * @author eike
 * @since 21.04.2013 00:55:21
 */
public enum Mnemonic
{

	// @formatter:off
	NOP             (0x00),
	ACONST_NULL     (0x01),
	ICONST_M1       (0x02),
	ICONST_0        (0x03),
	ICONST_1        (0x04),
	ICONST_2        (0x05),
	ICONST_3        (0x06),
	ICONST_4        (0x07),
	ICONST_5        (0x08),
	LCONST_0        (0x09),
	LCONST_1        (0x0A),
	FCONST_0        (0x0B),
	FCONST_1        (0x0C),
	FCONST_2        (0x0D),
	DCONST_0        (0x0E),
	DCONST_1        (0x0F),
	BIPUSH          (0x10, 1),
	SIPUSH          (0x11, 2),
	LDC             (0x12, 1),
	LDC_W           (0x13, 2),
	LDC2_W          (0x14, 2),
	ILOAD           (0x15, 1),
	LLOAD           (0x16, 1),
	FLOAD           (0x17, 1),
	DLOAD           (0x18, 1),
	ALOAD           (0x19, 1),
	ILOAD_0         (0x1A),
	ILOAD_1         (0x1B),
	ILOAD_2         (0x1C),
	ILOAD_3         (0x1D),
	LLOAD_0         (0x1E),
	LLOAD_1         (0x1F),
	LLOAD_2         (0x20),
	LLOAD_3         (0x21),
	FLOAD_0         (0x22),
	FLOAD_1         (0x23),
	FLOAD_2         (0x24),
	FLOAD_3         (0x25),
	DLOAD_0         (0x26),
	DLOAD_1         (0x27),
	DLOAD_2         (0x28),
	DLOAD_3         (0x29),
	ALOAD_0         (0x2A),
	ALOAD_1         (0x2B),
	ALOAD_2         (0x2C),
	ALOAD_3         (0x2D),
	IALOAD          (0x2E),
	LALOAD          (0x2F),
	FALOAD          (0x30),
	DALOAD          (0x31),
	AALOAD          (0x32),
	BALOAD          (0x33),
	CALOAD          (0x34),
	SALOAD          (0x35),
	ISTORE          (0x36, 1),
	LSTORE          (0x37, 1),
	FSTORE          (0x38, 1),
	DSTORE          (0x39, 1),
	ASTORE          (0x3A, 1),
	ISTORE_0        (0x3B),
	ISTORE_1        (0x3C),
	ISTORE_2        (0x3D),
	ISTORE_3        (0x3E),
	LSTORE_0        (0x3F),
	LSTORE_1        (0x40),
	LSTORE_2        (0x41),
	LSTORE_3        (0x42),
	FSTORE_0        (0x43),
	FSTORE_1        (0x44),
	FSTORE_2        (0x45),
	FSTORE_3        (0x46),
	DSTORE_0        (0x47),
	DSTORE_1        (0x48),
	DSTORE_2        (0x49),
	DSTORE_3        (0x4A),
	ASTORE_0        (0x4B),
	ASTORE_1        (0x4C),
	ASTORE_2        (0x4D),
	ASTORE_3        (0x4E),
	IASTORE         (0x4F),
	LASTORE         (0x50),
	FASTORE         (0x51),
	DASTORE         (0x52),
	AASTORE         (0x53),
	BASTORE         (0x54),
	CASTORE         (0x55),
	SASTORE         (0x56),
	POP             (0x57),
	POP2            (0x58),
	DUP             (0x59),
	DUP_X1          (0x5A),
	DUP_X2          (0x5B),
	DUP2            (0x5C),
	DUP2_X1         (0x5D),
	DUP2_X2         (0x5E),
	SWAP            (0x5F),
	IADD            (0x60),
	LADD            (0x61),
	FADD            (0x62),
	DADD            (0x63),
	ISUB            (0x64),
	LSUB            (0x65),
	FSUB            (0x66),
	DSUB            (0x67),
	IMUL            (0x68),
	LMUL            (0x69),
	FMUL            (0x6A),
	DMUL            (0x6B),
	IDIV            (0x6C),
	LDIV            (0x6D),
	FDIV            (0x6E),
	DDIV            (0x6F),
	IREM            (0x70),
	LREM            (0x71),
	FREM            (0x72),
	DREM            (0x73),
	INEG            (0x74),
	LNEG            (0x75),
	FNEG            (0x76),
	DNEG            (0x77),
	ISHL            (0x78),
	LSHL            (0x79),
	ISHR            (0x7A),
	LSHR            (0x7B),
	IUSHR           (0x7C),
	LUSHR           (0x7D),
	IAND            (0x7E),
	LAND            (0x7F),
	IOR             (0x80),
	LOR             (0x81),
	IXOR            (0x82),
	LXOR            (0x83),
	IINC            (0x84, 2),
	I2L             (0x85),
	I2F             (0x86),
	I2D             (0x87),
	L2I             (0x88),
	L2F             (0x89),
	L2D             (0x8A),
	F2I             (0x8B),
	F2L             (0x8C),
	F2D             (0x8D),
	D2I             (0x8E),
	D2L             (0x8F),
	D2F             (0x90),
	I2B             (0x91),
	I2C             (0x92),
	I2S             (0x93),
	LCMP            (0x94),
	FCMPL           (0x95),
	FCMPG           (0x96),
	DCMPL           (0x97),
	DCMPG           (0x98),
	IFEQ            (0x99, 2),
	IFNE            (0x9A, 2),
	IFLT            (0x9B, 2),
	IFGE            (0x9C, 2),
	IFGT            (0x9D, 2),
	IFLE            (0x9E, 2),
	IF_ICMPEQ       (0x9F, 2),
	IF_ICMPNE       (0xA0, 2),
	IF_ICMPLT       (0xA1, 2),
	IF_ICMPGE       (0xA2, 2),
	IF_ICMPGT       (0xA3, 2),
	IF_ICMPLE       (0xA4, 2),
	IF_ACMPEQ       (0xA5, 2),
	IF_ACMPNE       (0xA6, 2),
	GOTO            (0xA7, 2),
	JSR             (0xA8, 2),
	RET             (0xA9, 1),
	TABLESWITCH     (0xAA, 4), // also 4+
	LOOKUPSWITCH    (0xAB, 4), // also 4+
	IRETURN         (0xAC),
	LRETURN         (0xAD),
	FRETURN         (0xAE),
	DRETURN         (0xAF),
	ARETURN         (0xB0),
	RETURN          (0xB1),
	GETSTATIC       (0xB2, 2),
	PUTSTATIC       (0xB3, 2),
	GETFIELD        (0xB4, 2),
	PUTFIELD        (0xB5, 2),
	INVOKEVIRTUAL   (0xB6, 2),
	INVOKESPECIAL   (0xB7, 2),
	INVOKESTATIC    (0xB8, 2),
	INVOKEINTERFACE (0xB9, 4),
	INVOKEDYNAMIC   (0xBA, 4),
	NEW             (0xBB, 2),
	NEWARRAY        (0xBC, 1),
	ANEWARRAY       (0xBD, 2),
	ARRAYLENGTH     (0xBE),
	ATHROW          (0xBF),
	CHECKCAST       (0xC0, 2),
	INSTANCEOF      (0xC1, 2),
	MONITORENTER    (0xC2),
	MONITOREXIT     (0xC3),
	WIDE            (0xC4, 3), // also 5
	MULTIANEWARRAY  (0xC5, 3),
	IFNULL          (0xC6, 2),
	IFNONNULL       (0xC7, 2),
	GOTO_W          (0xC8, 4),
	JSR_W           (0xC9, 4),
	BREAKPOINT      (0xCA),
	// 0xCB .. 0XFD not assigned
	IMPDEP1         (0xFE),
	IMPDEP2         (0xFF);
	// @formatter:on

	private final byte byteCode;
	private final int argc;

	private Mnemonic(int hexCode, int argc) {
		this.byteCode = (byte) hexCode;
		this.argc = argc;
	}

	private Mnemonic(int hexCode) {
		this(hexCode, 0);
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

	private static Mnemonic varMnemonic(final String prefix, final int value) {
		try {
			final String name = prefix + "_"
					+ (value == -1 ? "M1" : Integer.toString(value));
			final Mnemonic m = valueOf(name);
			return m;
		} catch (IllegalArgumentException e) {
			return valueOf(prefix);
		}
	}

	public static Mnemonic ICONST(final int value) {
		// if (value >= -1 && value <= 5)
		return varMnemonic("ICONST", value);
		// return null;
	}

	public static Mnemonic LCONST(final int value) {
		// if (value >= 0 && value <= 1)
		return varMnemonic("LCONST", value);
		// return null;
	}

	public static Mnemonic FCONST(final int value) {
		// if (value >= 0 && value <= 2)
		return varMnemonic("FCONST", value);
		// return null;
	}

	public static Mnemonic DCONST(final int value) {
		// if (value >= 0 && value <= 1)
		return varMnemonic("DCONST", value);
		// return null;
	}

	public static Mnemonic ILOAD(final int value) {
		// if (value >= 0 && value <= 3)
		return varMnemonic("ILOAD", value);
		// return null;
	}

	public static Mnemonic LLOAD(final int value) {
		// if (value >= 0 && value <= 3)
		return varMnemonic("LLOAD", value);
		// return null;
	}

	public static Mnemonic FLOAD(final int value) {
		// if (value >= 0 && value <= 3)
		return varMnemonic("FLOAD", value);
		// return null;
	}

	public static Mnemonic DLOAD(final int value) {
		// if (value >= 0 && value <= 3)
		return varMnemonic("DLOAD", value);
		// return null;
	}

	public static Mnemonic ALOAD(final int value) {
		// if (value >= 0 && value <= 3)
		return varMnemonic("ALOAD", value);
		// return null;
	}

	public static Mnemonic ISTORE(final int value) {
		// if (value >= 0 && value <= 3)
		return varMnemonic("ISTORE", value);
		// return null;
	}

	public static Mnemonic LSTORE(final int value) {
		// if (value >= 0 && value <= 3)
		return varMnemonic("LSTORE", value);
		// return null;
	}

	public static Mnemonic FSTORE(final int value) {
		// if (value >= 0 && value <= 3)
		return varMnemonic("FSTORE", value);
		// return null;
	}

	public static Mnemonic DSTORE(final int value) {
		// if (value >= 0 && value <= 3)
		return varMnemonic("DSTORE", value);
		// return null;
	}

	public static Mnemonic ASTORE(final int value) {
		// if (value >= 0 && value <= 3)
		return varMnemonic("ASTORE", value);
		// return null;
	}

}
