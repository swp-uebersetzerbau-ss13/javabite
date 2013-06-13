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
public class Classfile {

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
		ACC_PUBLIC(0x0001), ACC_PRIVATE(0x0002), ACC_PROTECTED(0x0002), ACC_STATIC(
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
	public enum InfoTag {
		NONE(0x00, false), UTF8(0x01, false), LONG(0x05, true), DOUBLE(0x06,
				true), CLASS(0x07, false), STRING(0x08, false), METHODREF(0x0a,
				false), NAMEANDTYPE(0x0c, false), FIELDREF(0x09, false);

		final byte tag;
		final boolean wide;

		InfoTag(final int tag, final boolean wide) {
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

	public static final byte[] MAJOR_VERSION_J2SE_7 = { (byte) 0, (byte) 51 };
	public static final byte[] MAJOR_VERSION_J2SE_6 = { (byte) 0, (byte) 50 };
	public static final byte[] MAJOR_VERSION_J2SE_5 = { (byte) 0, (byte) 49 };

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
	private final byte[] majorVersion = MAJOR_VERSION_J2SE_7;
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
	 * <h1>generateInputstream</h1>
	 * <p>
	 * This method generates an Inputstream containing all information of the
	 * classfile, which can be obtained by using the classfile's method
	 * "writeTo(OutputStream os)".
	 * </p>
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * @see #writeTo(OutputStream)
	 */
	public InputStream generateInputstream() {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final DataOutputStream classfileDOS = new DataOutputStream(baos);

		writeTo(classfileDOS);

		return new ByteArrayInputStream(baos.toByteArray());
	}

	/**
	 * <h1>writeTo</h1>
	 * <p>
	 * This method uses an output stream and adds all bytes of the classfile
	 * meeting the jvm standard for javabyte classfiles. To do this, it uses the
	 * information in its member variables and the writeTo methods of its member
	 * objects.
	 * </p>
	 * 
	 * @author Robert, Marco
	 * @param os
	 *            the output stream to which the bytes are written
	 * @see Classfile.ConstantPool#writeTo(java.io.DataOutputStream)
	 * @see Classfile.MethodArea#writeTo(java.io.DataOutputStream)
	 */
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
	 * <h1>getName</h1>
	 * <p>
	 * This method returns the classfile's name.
	 * </p>
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 */
	public String getName() {
		return name;
	}

	/**
	 * <h1>addLongConstantToConstantPool</h1>
	 * <p>
	 * This method creates an longInfo-entry meeting the jvm classfile constant
	 * pool CONSTANT_LONG_info standard in the constantPool of this classfile.
	 * The generated entry is appended to the existing list, if it is not
	 * already in it. The entry's index is returned.
	 * </p>
	 * 
	 * @author Eike, Marco
	 * @since 26.05.2013
	 * @param value
	 *            long value of the value which is to be generated
	 * @return short index of a long info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 */
	public short addLongConstantToConstantPool(final long value) {
		return constantPool.generateConstantLongInfo(value);
	}

	/**
	 * <h1>addDoubleConstantToConstantPool</h1>
	 * <p>
	 * This method creates an doubleInfo-entry meeting the jvm classfile
	 * constant pool CONSTANT_DOUBLE_info standard in the constantPool of this
	 * classfile. The generated entry is appended to the existing list, if it is
	 * not already in it. The entry's index is returned.
	 * </p>
	 * 
	 * @author Eike, Marco
	 * @since 26.05.2013
	 * @param value
	 *            double value of the value which is to be generated
	 * @param keyValue
	 *            original (unparsed) double value as string to assemble map key
	 * @return short index of a double info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 */
	public short addDoubleConstantToConstantPool(final double value,
			final String keyValue) {
		return constantPool.generateConstantDoubleInfo(value, keyValue);
	}

	/**
	 * <h1>addStringConstantToConstantPool</h1>
	 * <p>
	 * This method creates an stringInfo-entry meeting the jvm classfile
	 * constant pool CONSTANT_STRING_info standard in the constantPool of this
	 * classfile. The generated entry is appended to the existing list, if it is
	 * not already in it. The entry's index is returned.
	 * </p>
	 * 
	 * @author Eike, Marco
	 * @since 26.05.2013
	 * @param value
	 *            string value of the value which is to be generated
	 * @return short index of a string info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 */
	public short addStringConstantToConstantPool(final String value) {
		return constantPool.generateConstantStringInfo(value);
	}

	/**
	 * <h1>addUTF8ConstantToConstantPool</h1>
	 * <p>
	 * This method creates an utf8Info-entry meeting the jvm classfile constant
	 * pool CONSTANT_UTF8_info standard in the constantPool of this classfile.
	 * The generated entry is appended to the existing list, if it is not
	 * already in it. The entry's index is returned.
	 * </p>
	 * 
	 * @author Eike, Marco
	 * @since 26.05.2013
	 * @param value
	 *            string value of the value which is to be generated
	 * @return short index of a utf8 info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 */
	public short addUTF8ConstantToConstantPool(final String value) {
		return constantPool.generateConstantUTF8Info(value);
	}

	/**
	 * <h1>addClassConstantToConstantPool</h1>
	 * <p>
	 * This method creates an classInfo-entry meeting the jvm classfile constant
	 * pool CONSTANT_CLASS_info standard in the constantPool of this classfile.
	 * The generated entry is appended to the existing list, if it is not
	 * already in it. The entry's index is returned.
	 * </p>
	 * 
	 * @author Eike, Marco
	 * @since 26.05.2013
	 * @param value
	 *            string value of the value which is to be generated
	 * @return short index of a class info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 */
	public short addClassConstantToConstantPool(final String value) {
		return constantPool.generateConstantClassInfo(value);
	}

	/**
	 * <h1>addMethodrefConstantToConstantPool</h1>
	 * <p>
	 * This method creates an methodrefInfo-entry meeting the jvm classfile
	 * constant pool CONSTANT_Methodref_info standard in the constantPool of
	 * this classfile. The generated entry is appended to the existing list, if
	 * it is not already in it. The entry's index is returned. Therefore this
	 * method adds all necessary data to the constant pool needed to call the
	 * defined method via the instruction invokestatic.
	 * </p>
	 * 
	 * @author Marco
	 * @since 13.05.2013
	 * @param methodName
	 *            string name of the method
	 * @param methodNameDescriptor
	 *            string method descriptor as specified by the jvm specification
	 * @param classNameEIF
	 *            string describing the method's class' class name encoded in
	 *            internal form according to the jvm specification
	 * @return short index of a methodref info entry in the constant pool of
	 *         this classfile meeting the parameters.
	 */
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
	 * <h1>addFieldrefConstantToConstantPool</h1>
	 * <p>
	 * This method creates a fieldrefInfo-entry meeting the jvm classfile
	 * constant pool CONSTANT_Fieldref_info standard in the constantPool of this
	 * classfile. The generated entry is appended to the existing list, if it is
	 * not already in it. The entry's index is returned. Therefore this method
	 * adds all necessary data to the constant pool needed to get data from
	 * another class via the instruction getstatic.
	 * </p>
	 * 
	 * @author Marco
	 * @since 30.05.2013
	 * @param fieldName
	 *            string name of the field
	 * @param fieldNameDescriptor
	 *            string field descriptor as specified by the jvm specification
	 * @param classNameEIF
	 *            string describing the method's class' class name encoded in
	 *            internal form according to the jvm specification
	 * @return short index of a fieldref info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 */
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
	 * <h1>getIndexOfConstantInConstantPool</h1>
	 * <p>
	 * This method looks up the index of a constant in the constant pool of this
	 * classfile.<br/>
	 * <br/>
	 * The constant name (key) must be in an appropriate format:<br/>
	 * LONG - {long value}<br/>
	 * DOUBLE - {double value}<br/>
	 * STRING - {string value}<br/>
	 * CLASS - {class description}<br/>
	 * UTF8 - {utf8 value}<br/>
	 * Methodref - {class index}.{nameAndType index}<br/>
	 * NameAndType - {method name}{method descriptor}
	 * </p>
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 * @param constantType
	 *            InfoTag type of the constant
	 * @param constantName
	 *            String name of the constant
	 * @return index of the constant in the constant pool of this classfile.
	 */
	public short getIndexOfConstantInConstantPool(final InfoTag constantType,
			final String constantName) {
		return constantPool.getIndexOfConstant(constantType, constantName);
	};

	/**
	 * <h1>addMethodToMethodArea</h1>
	 * <p>
	 * This method calls the addMethod method of the classfile's method area to
	 * add and initialize a new method.
	 * </p>
	 * 
	 * @author Marco
	 * @since 29.04.2013
	 * @param methodName
	 *            String name of the method
	 * @param methodDescriptor
	 *            String method descriptor as specified by jvm specification
	 * @param accessFlags
	 *            arbitrary amount of method access flags
	 */
	public void addMethodToMethodArea(final String methodName,
			final String methodDescriptor,
			final MethodAccessFlag... accessFlags) {

		// add constants to this classfile's constant pool
		final short nameIndex = addUTF8ConstantToConstantPool(methodName);
		final short descriptorIndex = addUTF8ConstantToConstantPool(methodDescriptor);
		final short codeIndex = addUTF8ConstantToConstantPool("Code");
		final short stackMapTableIndex = addUTF8ConstantToConstantPool("StackMapTable");
		final short localVariableTableIndex = addUTF8ConstantToConstantPool("LocalVariableTable");

		methodArea.addMethod(methodName, nameIndex, descriptorIndex, codeIndex,
				stackMapTableIndex, localVariableTableIndex, methodDescriptor,
				accessFlags);
	}

	/**
	 * <h1>addVariableToMethodsCode</h1>
	 * <p>
	 * This method adds a new variable to a methods code by allocating
	 * appropriate space in the local variable table of the method.
	 * </p>
	 * 
	 * @author Marco
	 * @since 29.04.2013
	 * @param methodName
	 *            String name of the method
	 * @param variableName
	 *            String name of the variable
	 * @param variableType
	 *            VariableType variable type of the variable
	 */
	public void addVariableToMethodsCode(final String methodName,
			final String variableName, final VariableType variableType) {

		methodArea.addVariableToMethodsCode(methodName, variableName,
				variableType);
	}

	/**
	 * <h1>addLongVariableToMethodsCode</h1>
	 * <p>
	 * This method adds explicitly a LONG variable to a methods code using the
	 * Classfile method addVariableToMethodsCode with an appropriate variable
	 * type.
	 * </p>
	 * 
	 * @author Eike, Marco
	 * @since 25.05.2013
	 * @param methodName
	 *            String name of the method
	 * @param variableName
	 *            String name of the variable
	 * @see Classfile#addVariableToMethodsCode(String, String, VariableType)
	 */
	public void addLongVariableToMethodsCode(final String methodName,
			final String variableName) {

		addVariableToMethodsCode(methodName, variableName, VariableType.LONG);
	}

	/**
	 * <h1>addDoubleVariableToMethodsCode</h1>
	 * <p>
	 * This method adds explicitly a DOUBLE variable to a methods code using the
	 * Classfile method addVariableToMethodsCode with an appropriate variable
	 * type.
	 * </p>
	 * 
	 * @author Eike, Marco
	 * @since 25.05.2013
	 * @param methodName
	 *            String name of the method
	 * @param variableName
	 *            String name of the variable
	 * @see Classfile#addVariableToMethodsCode(String, String, VariableType)
	 */
	public void addDoubleVariableToMethodsCode(final String methodName,
			final String variableName) {

		addVariableToMethodsCode(methodName, variableName, VariableType.DOUBLE);
	}

	/**
	 * <h1>addStringVariableToMethodsCode</h1>
	 * <p>
	 * This method adds explicitly a STRING variable to a methods code using the
	 * Classfile method addVariableToMethodsCode with an appropriate variable
	 * type.
	 * </p>
	 * 
	 * @author Eike, Marco
	 * @since 25.05.2013
	 * @param methodName
	 *            String name of the method
	 * @param variableName
	 *            String name of the variable
	 * @see Classfile#addVariableToMethodsCode(String, String, VariableType)
	 */
	public void addStringVariableToMethodsCode(final String methodName,
			final String variableName) {

		addVariableToMethodsCode(methodName, variableName, VariableType.STRING);
	}

	/**
	 * <h1>addBooleanVariableToMethodsCode</h1>
	 * <p>
	 * This method adds explicitly a BOOLEAN variable to a methods code using
	 * the Classfile method addVariableToMethodsCode with an appropriate
	 * variable type.
	 * </p>
	 * 
	 * @author Eike, Marco
	 * @since 25.05.2013
	 * @param methodName
	 *            String name of the method
	 * @param variableName
	 *            String name of the variable
	 * @see Classfile#addVariableToMethodsCode(String, String, VariableType)
	 */
	public void addBooleanVariableToMethodsCode(final String methodName,
			final String variableName) {

		addVariableToMethodsCode(methodName, variableName, VariableType.BOOLEAN);
	}

	/**
	 * <h1>getIndexOfVariableInMethod</h1>
	 * <p>
	 * This method looks up the index of a variable in the local variable space
	 * of the code attribute of the specified method and returns it.
	 * </p>
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 * @param methodName
	 *            String name of the method
	 * @param variableName
	 *            String name of the variable
	 * @return index of the variable in local variable space of the code
	 *         attribute of the specified method. variable space has a size of 1
	 *         byte
	 */
	public byte getIndexOfVariableInMethod(final String methodName,
			final String variableName) {

		return methodArea.getIndexOfVariableInMethod(methodName, variableName);
	}

	/**
	 * <h1>addInstructionToMethodsCode</h1>
	 * <p>
	 * This method adds a new Instruction to the code area of the code attribute
	 * of the provided method of the method area of this classfile using the
	 * method
	 * {@link Classfile.MethodArea#addInstructionToMethodsCode(String, Instruction)}
	 * .
	 * </p>
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 * @param methodName
	 *            String name of the method
	 * @param instruction
	 *            instance of class Instruction
	 * @see Classfile.MethodArea
	 * @see Classfile.MethodArea#addInstructionToMethodsCode(String,
	 *      Instruction)
	 * @see Instruction
	 */
	public void addInstructionToMethodsCode(final String methodName,
			final Instruction instruction) {
		methodArea.addInstructionToMethodsCode(methodName, instruction);
	}

	/**
	 * <h1>addInstructionsToMethodsCode</h1>
	 * <p>
	 * This method adds new Instructions to the code area of the code attribute
	 * of the provided method of the method area of this classfile using the
	 * Classfile method addInstructionToMethodsCode.
	 * </p>
	 * 
	 * @author Eike
	 * @since 09.05.2013
	 * @param methodName
	 *            String name of the method
	 * @param instructions
	 *            Collection of instances of class Instruction
	 * @see Classfile#addInstructionToMethodsCode(String, Instruction)
	 */
	public void addInstructionsToMethodsCode(final String methodName,
			final Instruction[] instructions) {
		for (final Instruction instruction : instructions) {
			methodArea.addInstructionToMethodsCode(methodName, instruction);
		}
	}
}
