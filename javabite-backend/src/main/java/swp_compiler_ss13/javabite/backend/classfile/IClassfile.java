package swp_compiler_ss13.javabite.backend.classfile;

import java.io.InputStream;
import java.io.OutputStream;

import swp_compiler_ss13.javabite.backend.Instruction;

public interface IClassfile {
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
		LONG(2), DOUBLE(2), STRING(1), BOOLEAN(1), AREF(1), INTEGER(1);

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
	 * ArrayType enum. This enum holds possible values for array types. It is
	 * used to create arrays, because the opcode NEWARRAY requires a type
	 * argument.
	 * 
	 * @author eike
	 * @since Jun 3, 2013 8:29:55 PM
	 */
	public enum ArrayType {
		BOOLEAN(0x04), DOUBLE(0x07), LONG(0x0b);

		private final byte value;

		ArrayType(final int value) {
			this.value = (byte) value;
		}

		public byte getValue() {
			return value;
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
	 * @author Marco
	 * @since 27.04.2013
	 * @see #writeTo(OutputStream)
	 */
	public InputStream generateInputstream();

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
	public void writeTo(OutputStream os);

	/**
	 * <h1>getName</h1>
	 * <p>
	 * This method returns the classfile's name.
	 * </p>
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 */
	public String getName();

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
	public short addLongConstantToConstantPool(long value);

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
	 * @return short index of a double info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 */
	public short addDoubleConstantToConstantPool(double value);

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
	public short addStringConstantToConstantPool(String value);

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
	public short addUTF8ConstantToConstantPool(String value);

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
	public short addClassConstantToConstantPool(String value);

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
	public short addMethodrefConstantToConstantPool(String methodName,
			String methodNameDescriptor, String classNameEIF);

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
	public short addFieldrefConstantToConstantPool(String fieldName,
			String fieldNameDescriptor, String classNameEIF);

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
	public short getIndexOfConstantInConstantPool(InfoTag constantType,
			String constantName);

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
	public void addMethodToMethodArea(String methodName,
			String methodDescriptor, MethodAccessFlag... accessFlags);

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
	public void addVariableToMethodsCode(String methodName,
			String variableName, VariableType variableType);

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
	void addLongVariableToMethodsCode(String methodName, String variableName);

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
	void addDoubleVariableToMethodsCode(String methodName, String variableName);

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
	void addStringVariableToMethodsCode(String methodName, String variableName);

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
	void addBooleanVariableToMethodsCode(String methodName, String variableName);

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
			final String variableName);

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
	public void addInstructionToMethodsCode(String methodName,
			Instruction instruction);

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
	public void addInstructionsToMethodsCode(String methodName,
			Instruction[] instructions);
}
