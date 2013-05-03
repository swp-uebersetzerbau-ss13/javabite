package swp_compiler_ss13.javabite.backend.marco.proposal1;

import static swp_compiler_ss13.javabite.backend.marco.proposal1.Mnemonic.ArgumentType.*;

/**
 * Mnemonic enum.
 * 
 * This enum holds all available bytecode mnemonics and their respective
 * bytecode bytes.
 * 
 * @author eike
 * @since 21.04.2013 00:55:21
 */
public enum Mnemonic
{

	// @formatter:off
	NOP((byte) 0x00),
	ACONST_NULL((byte) 0x01),
	ICONST_M1((byte) 0x02),
	ICONST_0((byte) 0x03),
	ICONST_1((byte) 0x04),
	ICONST_2((byte) 0x05),
	ICONST_3((byte) 0x06),
	ICONST_4((byte) 0x07),
	ICONST_5((byte) 0x08),
	LCONST_0((byte) 0x09),
	LCONST_1((byte) 0x0A),
	FCONST_0((byte) 0x0B),
	FCONST_1((byte) 0x0C),
	FCONST_2((byte) 0x0D),
	DCONST_0((byte) 0x0E),
	DCONST_1((byte) 0x0F),
	BIPUSH((byte) 0x10, BYTE),
	SIPUSH((byte) 0x11, BYTE,BYTE),
	LDC((byte) 0x12, INDX),
	LDC_W((byte) 0x13, INDX, INDX),
	LDC2_W((byte) 0x14, INDX, INDX),
	ILOAD((byte) 0x15, INDX),
	LLOAD((byte) 0x16, INDX),
	FLOAD((byte) 0x17, INDX),
	DLOAD((byte) 0x18,INDX),
	ALOAD((byte) 0x19, INDX),
	ILOAD_0((byte) 0x1A),
	ILOAD_1((byte) 0x1B),
	ILOAD_2((byte) 0x1C),
	ILOAD_3((byte) 0x1D),
	LLOAD_0((byte) 0x1E),
	LLOAD_1((byte) 0x1F),
	LLOAD_2((byte) 0x20),
	LLOAD_3((byte) 0x21),
	FLOAD_0((byte) 0x22),
	FLOAD_1((byte) 0x23),
	FLOAD_2((byte) 0x24),
	FLOAD_3((byte) 0x25),
	DLOAD_0((byte) 0x26),
	DLOAD_1((byte) 0x27),
	DLOAD_2((byte) 0x28),
	DLOAD_3((byte) 0x29),
	ALOAD_0((byte) 0x2A),
	ALOAD_1((byte) 0x2B),
	ALOAD_2((byte) 0x2C),
	ALOAD_3((byte) 0x2D),
	IALOAD((byte) 0x2E),
	LALOAD((byte) 0x2F),
	FALOAD((byte) 0x30),
	DALOAD((byte) 0x31),
	AALOAD((byte) 0x32),
	BALOAD((byte) 0x33),
	CALOAD((byte) 0x34),
	SALOAD((byte) 0x35),
	ISTORE((byte) 0x36, INDX),
	LSTORE((byte) 0x37, INDX),
	FSTORE((byte) 0x38,INDX),
	DSTORE((byte) 0x39, INDX),
	ASTORE((byte) 0x3A, INDX),
	ISTORE_0((byte) 0x3B),
	ISTORE_1((byte) 0x3C),
	ISTORE_2((byte) 0x3D),
	ISTORE_3((byte) 0x3E),
	LSTORE_0((byte) 0x3F),
	LSTORE_1((byte) 0x40),
	LSTORE_2((byte) 0x41),
	LSTORE_3((byte) 0x42),
	FSTORE_0((byte) 0x43),
	FSTORE_1((byte) 0x44),
	FSTORE_2((byte) 0x45),
	FSTORE_3((byte) 0x46),
	DSTORE_0((byte) 0x47),
	DSTORE_1((byte) 0x48),
	DSTORE_2((byte) 0x49),
	DSTORE_3((byte) 0x4A),
	ASTORE_0((byte) 0x4B),
	ASTORE_1((byte) 0x4C),
	ASTORE_2((byte) 0x4D),
	ASTORE_3((byte) 0x4E),
	IASTORE((byte) 0x4F),
	LASTORE((byte) 0x50),
	FASTORE((byte) 0x51),
	DASTORE((byte) 0x52),
	AASTORE((byte) 0x53),
	BASTORE((byte) 0x54),
	CASTORE((byte) 0x55),
	SASTORE((byte) 0x56),
	POP((byte) 0x57),
	POP2((byte) 0x58),
	DUP((byte) 0x59),
	DUP_X1((byte) 0x5A),
	DUP_X2((byte) 0x5B),
	DUP2((byte) 0x5C),
	DUP2_X1((byte) 0x5D),
	DUP2_X2((byte) 0x5E),
	SWAP((byte) 0x5F),
	IADD((byte) 0x60),
	LADD((byte) 0x61),
	FADD((byte) 0x62),
	DADD((byte) 0x63),
	ISUB((byte) 0x64),
	LSUB((byte) 0x65),
	FSUB((byte) 0x66),
	DSUB((byte) 0x67),
	IMUL((byte) 0x68),
	LMUL((byte) 0x69),
	FMUL((byte) 0x6A),
	DMUL((byte) 0x6B),
	IDIV((byte) 0x6C),
	LDIV((byte) 0x6D),
	FDIV((byte) 0x6E),
	DDIV((byte) 0x6F),
	IREM((byte) 0x70),
	LREM((byte) 0x71),
	FREM((byte) 0x72),
	DREM((byte) 0x73),
	INEG((byte) 0x74),
	LNEG((byte) 0x75),
	FNEG((byte) 0x76),
	DNEG((byte) 0x77),
	ISHL((byte) 0x78),
	LSHL((byte) 0x79),
	ISHR((byte) 0x7A),
	LSHR((byte) 0x7B),
	IUSHR((byte) 0x7C),
	LUSHR((byte) 0x7D),
	IAND((byte) 0x7E),
	LAND((byte) 0x7F),
	IOR((byte) 0x80),
	LOR((byte) 0x81),
	IXOR((byte) 0x82),
	LXOR((byte) 0x83),
	IINC((byte) 0x84, INDX, BYTE),
	I2L((byte) 0x85),
	I2F((byte) 0x86),
	I2D((byte) 0x87),
	L2I((byte) 0x88),
	L2F((byte) 0x89),
	L2D((byte) 0x8A),
	F2I((byte) 0x8B),
	F2L((byte) 0x8C),
	F2D((byte) 0x8D),
	D2I((byte) 0x8E),
	D2L((byte) 0x8F),
	D2F((byte) 0x90),
	I2B((byte) 0x91),
	I2C((byte) 0x92),
	I2S((byte) 0x93),
	LCMP((byte) 0x94),
	FCMPL((byte) 0x95),
	FCMPG((byte) 0x96),
	DCMPL((byte) 0x97),
	DCMPG((byte) 0x98),
	IFEQ((byte) 0x99, BYTE, BYTE),
	IFNE((byte) 0x9A,BYTE, BYTE),
	IFLT((byte) 0x9B, BYTE, BYTE),
	IFGE((byte) 0x9C, BYTE,BYTE),
	IFGT((byte) 0x9D, BYTE, BYTE),
	IFLE((byte) 0x9E, BYTE, BYTE),
	IF_ICMPEQ((byte) 0x9F, BYTE, BYTE),
	IF_ICMPNE((byte) 0xA0, BYTE, BYTE),
	IF_ICMPLT((byte) 0xA1, BYTE, BYTE),
	IF_ICMPGE((byte) 0xA2, BYTE, BYTE),
	IF_ICMPGT((byte) 0xA3, BYTE, BYTE),
	IF_ICMPLE((byte) 0xA4, BYTE, BYTE),
	IF_ACMPEQ((byte) 0xA5, BYTE, BYTE),
	IF_ACMPNE((byte) 0xA6, BYTE, BYTE),
	GOTO((byte) 0xA7, BYTE, BYTE),
	JSR((byte) 0xA8),
	RET((byte) 0xA9),
	TABLESWITCH((byte) 0xAA),
	// missing args
	LOOKUPSWITCH((byte) 0xAB),
	// missing args
	IRETURN((byte) 0xAC),
	LRETURN((byte) 0xAD),
	FRETURN((byte) 0xAE),
	DRETURN((byte) 0xAF),
	ARETURN((byte) 0xB0),
	RETURN((byte) 0xB1),
	GETSTATIC((byte) 0xB2, INDX, INDX),
	PUTSTATIC((byte) 0xB3, BYTE, BYTE),
	GETFIELD((byte) 0xB4, INDX, INDX),
	PUTFIELD((byte) 0xB5, BYTE, BYTE),
	INVOKEVIRTUAL((byte) 0xB6, BYTE, BYTE),
	INVOKESPECIAL((byte) 0xB7, BYTE, BYTE),
	INVOKESTATIC((byte) 0xB8, BYTE, BYTE),
	INVOKEINTERFACE((byte) 0xB9, BYTE, BYTE,BYTE, BYTE),
	INVOKEDYNAMIC((byte) 0xBA, BYTE, BYTE, BYTE, BYTE),
	NEW((byte) 0xBB, BYTE, BYTE),
	NEWARRAY((byte) 0xBC, INDX),
	ANEWARRAY((byte) 0xBD, BYTE, BYTE),
	ARRAYLENGTH((byte) 0xBE),
	ATHROW((byte) 0xBF),
	CHECKCAST((byte) 0xC0, BYTE, BYTE),
	INSTANCEOF((byte) 0xC1, BYTE, BYTE),
	MONITORENTER((byte) 0xC2),
	MONITOREXIT((byte) 0xC3),
	WIDE((byte) 0xC4),
	// missing args
	MULTIANEWARRAY((byte) 0xC5, BYTE, BYTE, BYTE),
	IFNULL((byte) 0xC6, BYTE,BYTE),
	IFNONNULL((byte) 0xC7, BYTE, BYTE),
	GOTO_W((byte) 0xC8,BYTE, BYTE, BYTE, BYTE),
	JSR_W((byte) 0xC9, BYTE, BYTE, BYTE, BYTE),
	BREAKPOINT((byte) 0xCA),
	// 0xCB .. 0XFD not assigned
	IMPDEP1((byte) 0xFE),
	IMPDEP2((byte) 0xFF);
	// @formatter:on

	public static enum ArgumentType
	{
		BYTE, INDX
	}

	private final int size;
	private final byte byteCode;
	private final ArgumentType[] argumentTypes;

	private Mnemonic(byte hexCode, final ArgumentType... argumentTypes) {
		this.byteCode = hexCode;
		this.argumentTypes = argumentTypes;
		this.size = 1 + (argumentTypes != null ? argumentTypes.length : 0);
	}

	public ArgumentType[] getArgumentTypes() {
		return argumentTypes;
	}

	public int getArgumentCount() {
		return argumentTypes.length;
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

	public int getSize() {
		return size;
	}

}
