package swp_compiler_ss13.javabite.backend;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

public interface IClassfile {
	/**
	 * enum ClassfileAccessFlag. Possible classfile access flags.
	 * 
	 * @author Marco
	 * @since 03.05.2013
	 */
	public enum ClassfileAccessFlag {
		ACC_PUBLIC((short) 0x0001), ACC_FINAL((short) 0x0010), ACC_SUPER(
				(short) 0x0020), ACC_INTERFACE((short) 0x0200), ACC_ABSTRACT(
				(short) 0x0400), ACC_SYNTHETIC((short) 0x1000), ACC_ANNOTATION(
				(short) 0x2000), ACC_ENUM((short) 0x4000);

		private final short value;

		ClassfileAccessFlag(final short value) {
			this.value = value;
		}

		public short getValue() {
			return this.value;
		}
	}

	/**
	 * enum MethodAccessFlag. Possible method access flags.
	 * 
	 * @author Marco
	 * @since 03.05.2013
	 */
	public enum MethodAccessFlag {
		ACC_PUBLIC((short) 0x0001), ACC_PRIVATE((short) 0x0002), ACC_PROTECTED(
				(short) 0x0002), ACC_STATIC((short) 0x0008), ACC_FINAL(
				(short) 0x0010), ACC_SYNCHRONIZED((short) 0x0020), ACC_BRIDGE(
				(short) 0x0040), ACC_VARARGS((short) 0x0080), ACC_NATIVE(
				(short) 0x0100), ACC_ABSTRACT((short) 0x0400), ACC_STRICT(
				(short) 0x0800), ACC_SYNTHETIC((short) 0x1000);

		private final short value;

		MethodAccessFlag(final short value) {
			this.value = value;
		}

		public short getValue() {
			return this.value;
		}
	}

	/**
	 * enum VariableTypes. This enumeration defines types and their length in a
	 * local variable space of a methods code attribute.
	 * 
	 * @author Marco
	 * @since 03.05.2013
	 */
	public enum VariableType {

		LONG((short) 2), DOUBLE((short) 2), STRING((short) 1), BOOLEAN(
				(short) 1);

		private final short length;

		VariableType(final short length) {
			this.length = length;
		}

		public short getLength() {
			return this.length;
		}

	}

	/**
	 * enum ConstantTypes. This enumeration defines the available types for
	 * entries in the constant pool of a classfile
	 * 
	 * @author eike
	 * @since May 12, 2013 4:52:43 PM
	 */
	public enum ConstantType {
		LONG, DOUBLE, STRING, CLASS, UTF8
	}

	/**
	 * generateInputstream function. This function generates an Inputstream
	 * containing all information of the classfile, which can be obtained by
	 * using the classfile function "getBytes()".
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	public InputStream generateInputstream();

	/**
	 * 
	 */
	public void writeTo(OutputStream os);

	/**
	 * getName function. This function returns the classfile's name.
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	public String getName();

	/**
	 * addSystemCall function. This function adds all necessary data to the
	 * constant pool needed to call the defined function via the instruction
	 * invokeStatic.
	 * 
	 * @author Marco
	 * @since 13.05.2013
	 * 
	 * @param systemFunctionName
	 *            String name of the system function
	 * @param systemFunctionNameDescriptor
	 *            String system function descriptor as specified by jvm
	 *            specification
	 * @param superClassNameEIF
	 *            describing the superclass' classname encoded in internal form
	 */
	public short addDataForSystemCall(String systemFunctionName,
			String systemFunctionNameDescriptor, String superClassNameEIF);

	/**
	 * addConstantToConstantPool function. This function adds a new constant to
	 * the classfile's constant pool using the constant pool's functions. What
	 * constant pool function is to be used has to be determined using the
	 * parameter "constantType". If the constant is already existent, its
	 * existent index in the constantPool is returned, otherwise the new index.
	 * 
	 * possible constantTypes: LONG, DOUBLE, CLASS, STRING, UTF8
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * @param constantType
	 *            type of constantPool information
	 * @param value
	 *            value of the constantPool information as String
	 * @return returns the constantPool index of the created or existing entry
	 * 
	 */
	public int addConstantToConstantPool(ConstantType constantType, String value);

	/*
	 * see addConstantToConstantPool(ConstantType, String)
	 */
	public int addLongConstantToConstantPool(long value);

	/*
	 * see addConstantToConstantPool(ConstantType, String)
	 */
	public int addDoubleConstantToConstantPool(double value);

	/*
	 * see addConstantToConstantPool(ConstantType, String)
	 */
	public int addStringConstantToConstantPool(String value);

	/**
	 * getIndexOfConstantInConstantPool function. This function looks up the
	 * index of a constant in the constant pool of this classfile.
	 * 
	 * possible constantTypes: LONG, DOUBLE, CLASS, STRING, UTF8
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 * @param constantType
	 *            String type of the constant
	 * @param constantName
	 *            String name of the constant
	 * @return index of the constant in the constant pool of this classfile.
	 */
	public short getIndexOfConstantInConstantPool(ConstantType constantType,
			String constantName);

	/**
	 * addMethodToMethodArea function. This function adds the necessary entries
	 * to the constantPool and then calls the addMethod function of
	 * this.methodAre to add and initialize a new method.
	 * 
	 * @author Marco
	 * @since 29.04.2013
	 * @param methodName
	 *            String name of the method
	 * @param methodDescriptor
	 *            String method descriptor as specified by jvm specification
	 * @param accessFlags
	 *            arbitrary amount of method access flags.
	 */
	public int addMethodToMethodArea(String methodName,
			String methodDescriptor, MethodAccessFlag... accessFlags);

	/**
	 * addVariableToMethodsCode function. This function adds a new variable to a
	 * methods code by allocating appropriate space in the local variable table
	 * of the method. the variable name and its index in this table are saved in
	 * a hashmap.
	 * 
	 * @author Marco
	 * @since 29.04.2013
	 * @param methodName
	 *            String name of the method
	 * @param variableName
	 *            String name of the variable
	 * @param variableType
	 *            String variableType of the variable.
	 */
	public void addVariableToMethodsCode(String methodName,
			String variableName, VariableType variableType);

	/**
	 * getIndexOfVariableInMethod function. This function looks up the index of
	 * a variable in the local variable space of the code attribute of the
	 * specified method.
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 * @param methodName
	 *            String name of the method
	 * @param variableName
	 *            String name of the variable
	 * @return index of the variable in local variable space of the code
	 *         attribute of the specified method. variable space has a size of 1
	 *         byte.
	 */
	public byte getIndexOfVariableInMethod(final String methodName,
			final String variableName);

	/**
	 * addInstructionToMethodsCode function. This function adds a new
	 * Instruction to the codeArea of the codeAttribute of the provided method
	 * of the methodArea of this classfile.
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 * @param methodName
	 *            String name of the method
	 * @param instruction
	 *            instance of class Instruction
	 * 
	 */
	public void addInstructionToMethodsCode(String methodName,
			Instruction instruction);

	/**
	 * addInstructionsToMethodsCode function. This function adds new
	 * Instructions to the codeArea of the codeAttribute of the provided method
	 * of the methodArea of this classfile.
	 * 
	 * @author eike
	 * @since 09.05.2013
	 * @param methodName
	 * @param instructions
	 */
	public void addInstructionsToMethodsCode(String methodName,
			Collection<Instruction> instructions);
}
