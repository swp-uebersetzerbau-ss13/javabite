package swp_compiler_ss13.javabite.backend.classfile;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.shortToHexString;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.javabite.backend.translation.Instruction;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.LocalVariableType;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.MethodAccessFlag;

/**
 * <h1>MethodArea</h1>
 * <p>
 * This class represents all information needed to create a JVM-Classfile
 * methods' area.
 * </p>
 * 
 * @author Marco
 * @since 28.04.2013
 */
class MethodArea {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * <h1>methodMap</h1>
	 * <p>
	 * This map maps method names to its Method objects.
	 * </p>
	 * 
	 * @see Method
	 */
	private final HashMap<String, Method> methodMap;

	MethodArea() {
		methodMap = new HashMap<>();
	}

	/**
	 * <h1>writeTo</h1>
	 * <p>
	 * This method uses an output stream and adds all bytes of the classfile's
	 * method area meeting the jvm standard for javabyte classfile method areas.
	 * To do this, it uses the information in its member variables and the
	 * writeTo methods of its member objects.
	 * </p>
	 * 
	 * @param classfileDOS
	 *            DataOutputStream to which the bytes are written
	 */
	void writeTo(final DataOutputStream classfileDOS) {

		try {
			classfileDOS.writeShort(methodMap.size());

			if (logger.isDebugEnabled()) {
				logger.debug("method count");
				logger.debug("{}", shortToHexString((short) methodMap.size()));
			}

			// get method_info - bytes of methods
			for (final Method method : methodMap.values()) {
				method.writeTo(classfileDOS);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * <h1>addMethod</h1>
	 * <p>
	 * This method adds and initializes a new Method object to the method list
	 * of this method area.
	 * </p>
	 * 
	 * @since 29.04.2013
	 * @param methodName
	 *            String name of the method
	 * @param methodDescriptor
	 *            String method descriptor as specified by jvm specification
	 * @param accessFlags
	 *            arbitrary amount of method access flags
	 * @see Method
	 */
	// TODO: UPDATE JAVADOC
	void addMethod(final String methodName, final short nameIndex,
			final short descriptorIndex, final short codeIndex,
			final String methodDescriptor,
			final MethodAccessFlag... accessFlags) {

		final Method newMethod = new Method(nameIndex, descriptorIndex,
				codeIndex, accessFlags);

		methodMap.put(methodName, newMethod);
	}

	/**
	 * <h1>getMethodByMethodName</h1>
	 * <p>
	 * This method gets and returns the method described by the parameter
	 * methodName.
	 * </p>
	 * 
	 * @since 29.04.2013
	 * @param methodName
	 *            String name of method which is supposed to be returned
	 * @return Method object
	 * @see Method
	 */
	private Method getMethodByMethodName(final String methodName) {
		return methodMap.get(methodName);
	}

	/**
	 * <h1>addVariableToMethodsCode</h1>
	 * <p>
	 * This method looks up the specified Method object in the method area of
	 * this classfile using the method {@link #getMethodByMethodName(String)}
	 * and adds a new variable to a methods code by allocating appropriate space
	 * in the local variable table of the method using the looked up Method
	 * object's method
	 * {@link Method#addVariableToCodeAttribute(String, swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.LocalVariableType)}
	 * .
	 * </p>
	 * 
	 * @since 29.04.2013
	 * @param methodName
	 *            String name of the method
	 * @param variableName
	 *            String name of the variable
	 * @param localVariableType
	 *            LocalVariableType variable type of the variable
	 * @see Method
	 * @see Method#addVariableToCodeAttribute(String,
	 *      swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.LocalVariableType)
	 */
	byte addVariableToMethodsCode(final String methodName,
			final String variableName, final LocalVariableType localVariableType) {

		final Method method = getMethodByMethodName(methodName);
		return method.addVariableToCodeAttribute(variableName,
				localVariableType);
	}

	/**
	 * <h1>getIndexOfVariableInMethod</h1>
	 * <p>
	 * This method looks up the specified Method object in the method area of
	 * this classfile using the method {@link #getMethodByMethodName(String)}
	 * and calls the method {@link Method#getIndexOfVariable(String)} on this
	 * object using the specified variable name.
	 * </p>
	 * 
	 * @since 30.04.2013
	 * @param methodName
	 *            String name of the method
	 * @param variableName
	 *            String name of the variable
	 * @return index of the variable in local variable space of the code
	 *         attribute of the specified method
	 * @see #getMethodByMethodName(String)
	 * @see Method
	 * @see Method#getIndexOfVariable(String)
	 */
	byte getIndexOfVariableInMethod(final String methodName,
			final String variableName) {
		final Method method = getMethodByMethodName(methodName);
		return method.getIndexOfVariable(variableName);
	}

	/**
	 * <h1>addInstructionToMethodsCode</h1>
	 * <p>
	 * This method looks up the specified Method object in the method area of
	 * this classfile using the method {@link #getMethodByMethodName(String)}
	 * and adds a new Instruction to the code area of the code attribute of the
	 * looked up Method object using the objects method
	 * {@link Method#addInstructionsToCodeAttribute(swp_compiler_ss13.javabite.backend.translation.Instruction...)}
	 * .
	 * </p>
	 * 
	 * @since 30.04.2013
	 * @param methodName
	 *            String name of the method
	 * @param instructions
	 *            instance of class Instruction
	 * @see #getMethodByMethodName(String)
	 * @see Method
	 * @see Method#addInstructionsToCodeAttribute(swp_compiler_ss13.javabite.backend.translation.Instruction...)
	 * @see Instruction
	 */
	void addInstructionsToMethodsCode(final String methodName,
			final Instruction... instructions) {
		final Method method = getMethodByMethodName(methodName);
		method.addInstructionsToCodeAttribute(instructions);
	}

}
