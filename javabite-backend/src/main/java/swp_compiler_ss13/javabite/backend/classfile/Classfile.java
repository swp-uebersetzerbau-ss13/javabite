package swp_compiler_ss13.javabite.backend.classfile;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.byteArrayToHexString;
import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.shortToHexString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.javabite.backend.translation.Instruction;
import swp_compiler_ss13.javabite.backend.translation.Mnemonic;
import swp_compiler_ss13.javabite.backend.utils.ByteUtils;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.ClassfileAccessFlag;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.ConstantPoolType;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.FieldAccessFlag;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.MethodAccessFlag;

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

	public static final byte[] MAJOR_VERSION_J2SE_7 = { (byte) 0, (byte) 51 };
	public static final byte[] MAJOR_VERSION_J2SE_6 = { (byte) 0, (byte) 50 };
	public static final byte[] MAJOR_VERSION_J2SE_5 = { (byte) 0, (byte) 49 };

	private static final Logger logger = LoggerFactory
			.getLogger(Classfile.class);

	// name of File
	private final String name;
	private final boolean isStruct;

	// used only in main classfile
	// TODO moar commentz
	private final Set<String> toplevelStructs;
	private final Set<String> sublevelStructs;
	private final Map<String, String> structMemberArrayTypes;

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
	private final byte[] majorVersion = MAJOR_VERSION_J2SE_5;
	protected ConstantPool constantPool;
	private short accessFlags;
	private short thisClassIndex;
	private short superClassIndex;
	private final short interfaceCount;
	// interface area left out
	protected FieldArea fieldArea;
	protected MethodArea methodArea;
	private final short attributesCount;

	private byte[] constructorIndex;

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
	 * @param isStruct
	 *            boolean flag which determines, whether this classfile is the
	 *            main classfile or a struct classfile
	 * @param accessFlags
	 *            arbitrary amount of classfile access flags.
	 */
	public Classfile(final String name, final String thisClassNameEIF,
			final String superClassNameEIF, final boolean isStruct,
			final ClassfileAccessFlag... accessFlags) {

		// set basic parameters
		this.name = name;
		this.isStruct = isStruct;
		toplevelStructs = new HashSet<>();
		sublevelStructs = new HashSet<>();
		structMemberArrayTypes = new HashMap<>();
		this.thisClassNameEIF = thisClassNameEIF;
		this.superClassNameEIF = superClassNameEIF;
		interfaceCount = 0;
		attributesCount = 0;

		for (final ClassfileAccessFlag accessFlag : accessFlags) {
			this.accessFlags = (short) (this.accessFlags | accessFlag.value);
		}

		// instantiate constant pool, field area, method area and attribute area
		constantPool = new ConstantPool();
		methodArea = new MethodArea();
		fieldArea = new FieldArea();

		// initialize classfile
		initializeClassfile();
	}

	public byte[] getConstructorIndex() {
		return constructorIndex;
	}

	public void addToplevelStruct(final String structName) {
		toplevelStructs.add(structName);
	}

	public boolean isToplevelStruct(final String structName) {
		return toplevelStructs.contains(structName);
	}

	public void addSublevelStruct(final String structName) {
		sublevelStructs.add(structName);
	}

	public boolean isSublevelStruct(final String structName) {
		return sublevelStructs.contains(structName);
	}

	public void addStructMemberArray(final String arrayPath,
			final String arrayType) {
		structMemberArrayTypes.put(arrayPath, arrayType);
	}

	public String getStructMemberArrayType(final String arrayPath) {
		return structMemberArrayTypes.get(arrayPath);
	}

	/**
	 * <h1>initializeClassfile</h1>
	 * <p>
	 * This method initializes the classfile. It sets basic constant pool values
	 * and creates an init method (constructor).
	 * </p>
	 * 
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
		addMethodToMethodArea("<init>", "()V", MethodAccessFlag.ACC_PUBLIC);

		// if this is a struct, the initialization of the object will be done by
		// the program builder, later in the program
		// TODO replace with addMethodref
		final short initNATIndex = constantPool
				.generateConstantNameAndTypeInfo("<init>", "()V");
		constructorIndex = ByteUtils.shortToByteArray(constantPool
				.generateConstantMethodrefInfo(superClassIndex, initNATIndex));
		if (!isStruct) {
			// add code to initialize-method
			final Instruction InstrAload = new Instruction(Mnemonic.ALOAD_0);
			final Instruction InstrInvokespecial = new Instruction(
					Mnemonic.INVOKESPECIAL, constructorIndex);
			final Instruction InstrReturn = new Instruction(Mnemonic.RETURN);
			addInstructionsToMethodsCode("<init>", InstrAload,
					InstrInvokespecial, InstrReturn);
		}
	}

	/**
	 * <h1>generateInputstream</h1>
	 * <p>
	 * This method generates an Inputstream containing all information of the
	 * classfile, which can be obtained by using the classfile's method
	 * "writeTo(OutputStream os)".
	 * </p>
	 * 
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
	 * @param classfileOS
	 *            the output stream to which the bytes are written
	 * @see ConstantPool#writeTo(java.io.DataOutputStream)
	 * @see MethodArea#writeTo(java.io.DataOutputStream)
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

			fieldArea.writeTo(classfileDOS);

			if (logger.isDebugEnabled()) {
				logger.debug("accessFlags(2), thisClassIndex(2), superClassIndex(2), interfaceCount(2)");
				logger.debug("{} {} {} {}", shortToHexString(accessFlags),
						shortToHexString(thisClassIndex),
						shortToHexString(superClassIndex),
						shortToHexString(interfaceCount));
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
	 * <h1>getFilename</h1>
	 * <p>
	 * This method returns the classfile's name.
	 * </p>
	 * 
	 * @since 27.04.2013
	 */
	public String getFilename() {
		return name;
	}

	public String getClassname() {
		return thisClassNameEIF;
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
	 * @since 26.05.2013
	 * @param value
	 *            string value of the value which is to be generated
	 * @return short index of a class info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 */
	public short addClassConstantToConstantPool(final String value) {
		return constantPool.generateConstantClassInfo(value);
	}

	public short addClassConstantToConstantPool(
			final ClassfileUtils.ClassSignature classSignature) {
		return addClassConstantToConstantPool(classSignature
				.getClassNameAsContainer());
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
	 * @since 13.05.2013
	 * @param signature
	 *            Signature of method to add to the constant pool
	 * @return short index of a methodref info entry in the constant pool of
	 *         this classfile meeting the parameters.
	 */
	public short addMethodrefConstantToConstantPool(
			final ClassfileUtils.MethodSignature signature) {
		// add class
		final short classIndex = addClassConstantToConstantPool(signature.methodClass);
		// add NAT
		final short natIndex = constantPool.generateConstantNameAndTypeInfo(
				signature.methodName, signature.methodDescriptor);
		// add methodref
		return constantPool.generateConstantMethodrefInfo(classIndex, natIndex);
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
		return constantPool.generateConstantFieldrefInfo(classIndex, natIndex,
				fieldName, classNameEIF);
	}

	public short addFieldrefConstantToConstantPool(
			final ClassfileUtils.FieldSignature signature) {
		return addFieldrefConstantToConstantPool(signature.fieldName,
				signature.fieldType.getClassNameAsType(),
				signature.fieldClass.getClassNameAsContainer());
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
	 * @since 30.04.2013
	 * @param constantType
	 *            InfoTag type of the constant
	 * @param constantName
	 *            String name of the constant
	 * @return index of the constant in the constant pool of this classfile.
	 */
	public short getIndexOfConstantInConstantPool(
			final ConstantPoolType constantType, final String constantName) {
		return constantPool.getIndexOfConstant(constantType, constantName);
	}

	/**
	 * <h1>addMethodToMethodArea</h1>
	 * <p>
	 * This method calls the addMethod method of the classfile's method area to
	 * add and initialize a new method.
	 * </p>
	 * 
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

		methodArea.addMethod(methodName, nameIndex, descriptorIndex, codeIndex,
				methodDescriptor, accessFlags);
	}

	/**
	 * <h1>addVariableToMethodsCode</h1>
	 * <p>
	 * This method adds a new variable to a methods code by allocating
	 * appropriate space in the local variable table of the method.
	 * </p>
	 * 
	 * @since 29.04.2013
	 * @param methodName
	 *            String name of the method
	 * @param variableName
	 *            String name of the variable
	 * @param localVariableType
	 *            LocalVariableType variable type of the variable
	 */
	public byte addVariableToMethodsCode(final String methodName,
			final String variableName,
			final ClassfileUtils.LocalVariableType localVariableType) {

		return methodArea.addVariableToMethodsCode(methodName, variableName,
				localVariableType);
	}

	/**
	 * <h1>addLongVariableToMethodsCode</h1>
	 * <p>
	 * This method adds explicitly a LONG variable to a methods code using the
	 * Classfile method addVariableToMethodsCode with an appropriate variable
	 * type.
	 * </p>
	 * 
	 * @since 25.05.2013
	 * @param methodName
	 *            String name of the method
	 * @param variableName
	 *            String name of the variable
	 * @see Classfile#addVariableToMethodsCode(String, String,
	 *      swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.LocalVariableType)
	 */
	public void addLongVariableToMethodsCode(final String methodName,
			final String variableName) {

		addVariableToMethodsCode(methodName, variableName,
				ClassfileUtils.LocalVariableType.LONG);
	}

	/**
	 * <h1>addDoubleVariableToMethodsCode</h1>
	 * <p>
	 * This method adds explicitly a DOUBLE variable to a methods code using the
	 * Classfile method addVariableToMethodsCode with an appropriate variable
	 * type.
	 * </p>
	 * 
	 * @since 25.05.2013
	 * @param methodName
	 *            String name of the method
	 * @param variableName
	 *            String name of the variable
	 * @see Classfile#addVariableToMethodsCode(String, String,
	 *      swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.LocalVariableType)
	 */
	public void addDoubleVariableToMethodsCode(final String methodName,
			final String variableName) {

		addVariableToMethodsCode(methodName, variableName,
				ClassfileUtils.LocalVariableType.DOUBLE);
	}

	/**
	 * <h1>addStringVariableToMethodsCode</h1>
	 * <p>
	 * This method adds explicitly a STRING variable to a methods code using the
	 * Classfile method addVariableToMethodsCode with an appropriate variable
	 * type.
	 * </p>
	 * 
	 * @since 25.05.2013
	 * @param methodName
	 *            String name of the method
	 * @param variableName
	 *            String name of the variable
	 * @see Classfile#addVariableToMethodsCode(String, String,
	 *      swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.LocalVariableType)
	 */
	public void addStringVariableToMethodsCode(final String methodName,
			final String variableName) {

		addVariableToMethodsCode(methodName, variableName,
				ClassfileUtils.LocalVariableType.STRING);
	}

	/**
	 * <h1>addBooleanVariableToMethodsCode</h1>
	 * <p>
	 * This method adds explicitly a BOOLEAN variable to a methods code using
	 * the Classfile method addVariableToMethodsCode with an appropriate
	 * variable type.
	 * </p>
	 * 
	 * @since 25.05.2013
	 * @param methodName
	 *            String name of the method
	 * @param variableName
	 *            String name of the variable
	 * @see Classfile#addVariableToMethodsCode(String, String,
	 *      swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.LocalVariableType)
	 */
	public void addBooleanVariableToMethodsCode(final String methodName,
			final String variableName) {

		addVariableToMethodsCode(methodName, variableName,
				ClassfileUtils.LocalVariableType.BOOLEAN);
	}

	/**
	 * <h1>addInstructionsToMethodsCode</h1>
	 * <p>
	 * This method adds new Instructions to the code area of the code attribute
	 * of the provided method of the method area of this classfile using the
	 * Classfile method addInstructionToMethodsCode.
	 * </p>
	 * 
	 * @since 09.05.2013
	 * @param methodName
	 *            String name of the method
	 * @param instructions
	 *            Collection of instances of class Instruction
	 * @see Classfile#addInstructionsToMethodsCode(String,
	 *      swp_compiler_ss13.javabite.backend.translation.Instruction[])
	 */
	public void addInstructionsToMethodsCode(final String methodName,
			final Instruction... instructions) {
		methodArea.addInstructionsToMethodsCode(methodName, instructions);
	}

	/**
	 * <h1>addFieldToFieldArea</h1>
	 * <p>
	 * This method adds new field to this classfile's field area using the field
	 * area's method addField.
	 * </p>
	 * 
	 * @since 24.06.2013
	 * @param fieldName
	 *            String name of the field
	 * @param fieldDescriptor
	 *            String descriptor of the field
	 * @param accessFlags
	 *            list of access flags for the field
	 */
	public void addFieldToFieldArea(final String fieldName,
			final String fieldDescriptor, final FieldAccessFlag... accessFlags) {
		// first generate appropriate constants in the constant pool
		final short fieldNameIndex = constantPool
				.generateConstantUTF8Info(fieldName);
		final short fieldDescriptorIndex = constantPool
				.generateConstantUTF8Info(fieldDescriptor);
		// add fields
		fieldArea.addField(fieldNameIndex, fieldDescriptorIndex, accessFlags);
	}

}
