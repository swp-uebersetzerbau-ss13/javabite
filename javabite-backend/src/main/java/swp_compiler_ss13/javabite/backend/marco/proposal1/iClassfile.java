package swp_compiler_ss13.javabite.backend.marco.proposal1;

import java.io.InputStream;
import java.util.ArrayList;

public interface iClassfile {

	/**
	 * generateInputstream function. This function generates an Inputstream
	 * containing all information of the classfile, which can be obtained
	 * by using the classfile function "getBytes()".
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	public InputStream generateInputstream();
	
	/**
	 * getBytes function. This function creates a Byte-List
	 * of all the necessary classfile-information meeting the 
	 * JVM-classfile standard.
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	public ArrayList<Byte> getBytes();
	
	/**
	 * getName function. This function returns the classfile's name.
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	public String getName();
	
	/**
	 * addConstantToConstantPool function. This function adds a new constant
	 * to the classfile's constant pool using the constant pool's functions.
	 * What constant pool function is to be used has to be determined using
	 * the parameter "constantType". If the constant is already existent,
	 * its existent index in the constantPool is returned, otherwise the new 
	 * index.
	 * 
	 * possible constantTypes: LONG, DOUBLE, CLASS, STRING, UTF8
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * @param constantType type of constantPool information
	 * @param value value of the constantPool information as String
	 * @return returns the constantPool index of the created or existing entry
	 * 
	 */
	public int addConstantToConstantPool(String constantType, String value);
	
	/**
	 * getIndexOfConstantInConstantPool function. This function looks up the index of a 
	 * constant in the constant pool of this classfile.
	 * 
	 * possible constantTypes: LONG, DOUBLE, CLASS, STRING, UTF8
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 * @param constantName String name of the constant
	 * @param constantType String type of the constant
	 * @return index of the constant in the constant pool of this classfile.
	 */
	public short getIndexOfConstantInConstantPool(String constantName, String constantType);
	
	/**
	 * addMethodToMethodArea function. This function adds the necessary entries
	 * to the constantPool and then calls the addMethod function of this.methodAre 
	 * to add and initialize a new method.
	 * 
	 * @author Marco
	 * @since 29.04.2013
	 * @param methodName String name of the method
	 * @param methodDescriptor String method descriptor as specified by jvm specification
	 * 
	 */
	public int addMethodToMethodArea(String methodName, String methodDescriptor);
	
	/**
	 * addVariableToMethodsCode function. This function adds a new variable to a methods
	 * code by allocating appropriate space in the local variable table of the method.
	 * the variable name and its index in this table are saved in a hashmap.
	 * 
	 * @author Marco
	 * @since 29.04.2013
	 * @param methodName String name of the method
	 * @param variableName String name of the variable
	 * @param variableType String variableType of the variable.
	 */
	public void addVariableToMethodsCode(String methodName, String variableName, String variableType);
	
	/**
	 * getIndexOfVariableInMethod function. This function looks up the index of a variable
	 * in the local variable space of the code attribute of the specified method.
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 * @param methodName String name of the method
	 * @param variableName String name of the variable
	 * @return index of the variable in local variable space of the code attribute 
	 * of the specified method
	 */
	public short getIndexOfVariableInMethod(String methodName, String variableName);
	
	/**
	 * addInstructionToMethodsCode function. This function adds a new Instruction to the
	 * codeArea of the codeAttribute of the provided method of the methodArea of this
	 * classfile.
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 * @param methodName String name of the method
	 * @param instruction instance of class Instruction
	 * 
	 */
	public void addInstructionToMethodsCode(String methodName, Instruction instruction);
}
