package swp_compiler_ss13.javabite.backend.classfile;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.byteArrayToHexString;
import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.shortToHexString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.javabite.backend.Instruction;
import swp_compiler_ss13.javabite.backend.Mnemonic;
import swp_compiler_ss13.javabite.backend.utils.ByteUtils;

/**
 * <h1>Classfile</h1>
 * <p>
 * This class represents all information needed to create a JVM-Classfile.
 * </p>
 * 
 * @author Marco
 * @since 27.04.2013
 */
public class Classfile implements IClassfile {

	private static final Logger logger = LoggerFactory
			.getLogger(Classfile.class);

	// name of File
	private final String name;

	/*
	 * general classfile constant pool information being used while classfile
	 * initialization
	 */
	private final String thisClassNameEIF;
	private final String superClassNameEIF;

	// general classfile structure information
	private final byte[] magic = { (byte) 0xca, (byte) 0xfe, (byte) 0xba,
			(byte) 0xbe };
	private final byte[] minorVersion = { (byte) 0x00, (byte) 0x00 };
	private final byte[] majorVersion = { (byte) 0x00, (byte) 0x33 };
	protected ConstantPool constantPool;
	private short accessFlags;
	private short thisClassIndex;
	private short superClassIndex;
	private final short interfaceCount;
	// interface area left out
	private final short fieldsCount;
	// field area left out
	protected MethodArea methodArea;
	private final short attributesCount;

	// attribute area left out

	/**
	 * <h1>Classfile</h1>
	 * <p>
	 * This class represents all information needed to create a JVM-Classfile.
	 * </p>
	 * 
	 * <h1>Classfile constructor</h1>
	 * <p>
	 * The constructor instantiates the classfile's constant pool, field area,
	 * method area and attribute area and sets basic classfile information.
	 * </p>
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * @param name
	 *            string describing the classfile's name being used when the
	 *            actual file is created.
	 * @param thisClassNameEIF
	 *            string describing this class name encoded in internal form
	 *            according to the jvm specification.
	 * @param superClassNameEIF
	 *            string describing the superclass' class name encoded in
	 *            internal form according to the jvm specification.
	 * @param accessFlags
	 *            arbitrary amount of classfile access flags.
	 */
	public Classfile(final String name, final String thisClassNameEIF,
			final String superClassNameEIF,
			final ClassfileAccessFlag... accessFlags) {

		// set basic parameters
		this.name = name;
		this.thisClassNameEIF = thisClassNameEIF;
		this.superClassNameEIF = superClassNameEIF;
		interfaceCount = 0;
		fieldsCount = 0;
		attributesCount = 0;

		for (final ClassfileAccessFlag accessFlag : accessFlags) {
			this.accessFlags = (short) (this.accessFlags | accessFlag
					.getValue());
		}

		// instantiate constant pool, field area, method area and attribute area
		constantPool = new ConstantPool();
		methodArea = new MethodArea();

		// initialize classfile
		initializeClassfile();
	}

	/**
	 * <h1>initializeClassfile</h1>
	 * <p>
	 * This method initializes the classfile. It sets basic constant pool values
	 * and creates an init method (constructor).
	 * </p>
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 */
	private void initializeClassfile() {
		/*
		 * add the class' name encoded in internal form to constant pool, get
		 * back its index in the constant pool and set member variable
		 * thisClassIndex to it
		 */
		thisClassIndex = addClassConstantToConstantPool(thisClassNameEIF);
		/*
		 * add the super class' name encoded in internal form to constant pool,
		 * get back its index in the constant pool and set member variable
		 * superClassIndex to it
		 */
		superClassIndex = addClassConstantToConstantPool(superClassNameEIF);

		/*
		 * add initialize-method (constructor) to method area and set invoke
		 * parameter
		 */
		// TODO externalize static strings
		addMethodToMethodArea("<init>", "()V",
				Classfile.MethodAccessFlag.ACC_PUBLIC);
		// TODO replace with addMethodref
		final short initNATIndex = constantPool
				.generateConstantNameAndTypeInfo("<init>", "()V");
		final short methodrefIndex = constantPool
				.generateConstantMethodrefInfo(superClassIndex, initNATIndex);
		final byte[] methodRefByteArray = ByteUtils
				.shortToByteArray(methodrefIndex);

		// add code to initialize-method
		// final Instruction InstrAload = new Instruction(1, Mnemonic.ALOAD_0,
		// null);
		final Instruction InstrAload = new Instruction(Mnemonic.ALOAD_0);
		// final Instruction InstrInvokespecial = new Instruction(3,
		// Mnemonic.INVOKESPECIAL, methodRefByteArray);
		final Instruction InstrInvokespecial = new Instruction(
				Mnemonic.INVOKESPECIAL, methodRefByteArray);
		// final Instruction InstrReturn = new Instruction(1, Mnemonic.RETURN,
		// null);
		final Instruction InstrReturn = new Instruction(Mnemonic.RETURN);
		addInstructionToMethodsCode("<init>", InstrAload);
		addInstructionToMethodsCode("<init>", InstrInvokespecial);
		addInstructionToMethodsCode("<init>", InstrReturn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream generateInputstream() {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final DataOutputStream classfileDOS = new DataOutputStream(baos);

		writeTo(classfileDOS);

		return new ByteArrayInputStream(baos.toByteArray());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeTo(final OutputStream classfileOS) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("magic(4B), minorVersion(2B), majorVersion(2B)");
				logger.debug("{} {} {}", byteArrayToHexString(magic),
						byteArrayToHexString(minorVersion),
						byteArrayToHexString(majorVersion));
			}

			final DataOutputStream classfileDOS = (DataOutputStream) classfileOS;

			// write metainformation
			classfileDOS.write(magic);
			classfileDOS.write(minorVersion);
			classfileDOS.write(majorVersion);

			// write constantPool content
			constantPool.writeTo(classfileDOS);

			classfileDOS.writeShort(accessFlags);
			classfileDOS.writeShort(thisClassIndex);
			classfileDOS.writeShort(superClassIndex);
			classfileDOS.writeShort(interfaceCount);
			classfileDOS.writeShort(fieldsCount);

			if (logger.isDebugEnabled()) {
				logger.debug("accessFlags(2), thisClassIndex(2), superClassIndex(2), interfaceCount(2), fieldsCount(2)");
				logger.debug("{} {} {} {} {}", shortToHexString(accessFlags),
						shortToHexString(thisClassIndex),
						shortToHexString(superClassIndex),
						shortToHexString(interfaceCount),
						shortToHexString(fieldsCount));
			}

			methodArea.writeTo(classfileDOS);

			classfileDOS.writeShort(attributesCount);

			if (logger.isDebugEnabled()) {
				logger.debug("accessFlags(2), thisClassIndex(2), superClassIndex(2), interfaceCount(2), fieldsCount(2)");
				logger.debug("{}", shortToHexString(attributesCount));
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short addLongConstantToConstantPool(final long value) {
		return constantPool.generateConstantLongInfo(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short addDoubleConstantToConstantPool(final double value) {
		return constantPool.generateConstantDoubleInfo(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short addStringConstantToConstantPool(final String value) {
		return constantPool.generateConstantStringInfo(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short addUTF8ConstantToConstantPool(final String value) {
		return constantPool.generateConstantUTF8Info(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short addClassConstantToConstantPool(final String value) {
		return constantPool.generateConstantClassInfo(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short addMethodrefConstantToConstantPool(final String methodName,
			final String methodNameDescriptor, final String classNameEIF) {

		// add class
		final short classIndex = addClassConstantToConstantPool(classNameEIF);
		// add NAT
		final short natIndex = constantPool.generateConstantNameAndTypeInfo(
				methodName, methodNameDescriptor);
		// add methodref
		final short methodrefIndex = constantPool
				.generateConstantMethodrefInfo(classIndex, natIndex);

		return methodrefIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short addFieldrefConstantToConstantPool(final String fieldName,
			final String fieldNameDescriptor, final String classNameEIF) {

		// add class
		final short classIndex = addClassConstantToConstantPool(classNameEIF);
		// add NAT
		final short natIndex = constantPool.generateConstantNameAndTypeInfo(
				fieldName, fieldNameDescriptor);
		// add fieldref
		final short fieldrefIndex = constantPool.generateConstantFieldrefInfo(
				classIndex, natIndex);

		return fieldrefIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short getIndexOfConstantInConstantPool(final InfoTag constantType,
			final String constantName) {
		return constantPool.getIndexOfConstant(constantType, constantName);
	};

	/**
	 * {@inheritDoc}
	 */
	// TODO: UPDATE JAVADOC
	@Override
	public void addMethodToMethodArea(final String methodName,
			final String methodDescriptor,
			final MethodAccessFlag... accessFlags) {

		// add constants to this classfile's constant pool
		final short nameIndex = addUTF8ConstantToConstantPool(methodName);
		final short descriptorIndex = addUTF8ConstantToConstantPool(methodDescriptor);
		final short codeIndex = addUTF8ConstantToConstantPool("Code");

		methodArea.addMethod(methodName, nameIndex, descriptorIndex, codeIndex,
				methodDescriptor, accessFlags);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addVariableToMethodsCode(final String methodName,
			final String variableName, final VariableType variableType) {

		methodArea.addVariableToMethodsCode(methodName, variableName,
				variableType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addLongVariableToMethodsCode(final String methodName,
			final String variableName) {

		addVariableToMethodsCode(methodName, variableName, VariableType.LONG);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addDoubleVariableToMethodsCode(final String methodName,
			final String variableName) {

		addVariableToMethodsCode(methodName, variableName, VariableType.DOUBLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addStringVariableToMethodsCode(final String methodName,
			final String variableName) {

		addVariableToMethodsCode(methodName, variableName, VariableType.STRING);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addBooleanVariableToMethodsCode(final String methodName,
			final String variableName) {

		addVariableToMethodsCode(methodName, variableName, VariableType.BOOLEAN);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte getIndexOfVariableInMethod(final String methodName,
			final String variableName) {

		return methodArea.getIndexOfVariableInMethod(methodName, variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addInstructionToMethodsCode(final String methodName,
			final Instruction instruction) {
		methodArea.addInstructionToMethodsCode(methodName, instruction);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addInstructionsToMethodsCode(final String methodName,
			final Instruction[] instructions) {
		for (final Instruction instruction : instructions) {
			methodArea.addInstructionToMethodsCode(methodName, instruction);
		}
	}
}
