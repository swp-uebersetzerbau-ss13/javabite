package swp_compiler_ss13.javabite.backend.classfile;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.shortToHexString;

import java.io.DataOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.javabite.backend.translation.Instruction;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.MethodAccessFlag;

/**
 * <h1>Method</h1>
 * <p>
 * This class represents all information needed to create a JVM-Classfile
 * method.
 * </p>
 * 
 * @author Marco
 * @since 28.04.2013
 */
class Method {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// General method structure information
	private short accessFlags;
	private final short nameIndex;
	private final short descriptorIndex;
	/*
	 * for convenience the attributes count is set explicitly as we need only
	 * the Code attribute
	 */
	private final short attributesCount = 1;
	// Attributes
	private final Code codeAttribute;

	/**
	 * <h1>Method</h1>
	 * <p>
	 * This class represents all information needed to create a JVM-Classfile
	 * method.
	 * </p>
	 * 
	 * <h1>Method constructor</h1>
	 * <p>
	 * The constructor adds some constants to this classfile being needed for a
	 * new Method object. Furthermore it sets the method's {@link #accessFlags}
	 * according to the parameters.
	 * </p>
	 * 
	 * @since 28.04.2013
	 * @param fieldAccessFlags
	 *            arbitrary amount of MethodAccessFlag
	 * @see #accessFlags
	 * @see Classfile#addUTF8ConstantToConstantPool(String)
	 * @see MethodAccessFlag
	 */
	// private Method(final String methodName, final String methodDescriptor,
	// final MethodAccessFlag... accessFlags) {
	//
	// // add constants to this classfile's constant pool
	// this.nameIndex = Classfile.this
	// .addUTF8ConstantToConstantPool(methodName);
	// this.descriptorIndex = Classfile.this
	// .addUTF8ConstantToConstantPool(methodDescriptor);
	//
	// for (final MethodAccessFlag a : accessFlags) {
	// this.accessFlags = (short) (this.accessFlags | a.getValue());
	// }
	//
	// /*
	// * for convenience the attributes count is set explicitly as we need
	// * only the Code attribute
	// */
	// this.attributesCount = 1;
	// final short codeIndex = Classfile.this
	// .addUTF8ConstantToConstantPool("Code");
	// this.codeAttribute = new CodeAttribute(codeIndex);
	// }

	// TODO: UPDATE JAVADOC
	Method(final short nameIndex, final short descriptorIndex,
			final short codeIndex, final MethodAccessFlag... fieldAccessFlags) {
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
		codeAttribute = new Code(codeIndex);

		for (final MethodAccessFlag faf : fieldAccessFlags) {
			accessFlags = (short) (accessFlags | faf.value);
		}

	}

	/**
	 * <h1>writeTo</h1>
	 * <p>
	 * This method uses an output stream and adds all bytes of the classfile
	 * method area's method meeting the jvm standard for javabyte classfile
	 * method area methods. To do this, it uses the information in its member
	 * variables and the writeTo methods of its member objects.
	 * </p>
	 * 
	 * @param classfileDOS
	 *            DataOutputStream to which the bytes are written
	 */
	void writeTo(final DataOutputStream classfileDOS) {
		try {
			classfileDOS.writeShort(accessFlags);
			classfileDOS.writeShort(nameIndex);
			classfileDOS.writeShort(descriptorIndex);
			classfileDOS.writeShort(attributesCount);

			if (logger.isDebugEnabled()) {
				logger.debug("accessFlags, nameIndex, descriptorIndex, attributesCount:");
				logger.debug("{} {} {} {}", shortToHexString(accessFlags),
						shortToHexString(nameIndex),
						shortToHexString(descriptorIndex),
						shortToHexString(attributesCount));
			}

			codeAttribute.writeTo(classfileDOS);
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * <h1>addVariableToCodeAttribute</h1>
	 * <p>
	 * This method adds a new variable to a methods code attribute by allocating
	 * appropriate space in the local variable table of the method using the
	 * CodeAttribute method
	 * {@link Code#addVariable(String, swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.LocalVariableType)}
	 * .
	 * </p>
	 * 
	 * @since 29.04.2013
	 * @param variableName
	 *            String name of the variable
	 * @param localVariableType
	 *            LocalVariableType variable type of the variable
	 * @see swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.LocalVariableType
	 * @see Code
	 * @see Code#addVariable(String,
	 *      swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.LocalVariableType)
	 */
	byte addVariableToCodeAttribute(final String variableName,
			final ClassfileUtils.LocalVariableType localVariableType) {
		return codeAttribute.addVariable(variableName, localVariableType);
	}

	/**
	 * <h1>getIndexOfVariable</h1>
	 * <p>
	 * This method looks up the index of a variable name in the code attribute
	 * of this method using the CodeAttribute's method
	 * {@link Code#getIndexOfVariable(String)}.
	 * </p>
	 * 
	 * @since 30.04.2013
	 * @param variableName
	 *            String name of the variable
	 * @return index of the variable in local variable space of this method.
	 * @see Code
	 * @see Code#getIndexOfVariable(String)
	 */
	byte getIndexOfVariable(final String variableName) {
		return codeAttribute.getIndexOfVariable(variableName);
	}

	/**
	 * <h1>addInstructionToCodeAttribute</h1>
	 * <p>
	 * This method adds a new Instruction object to the code area of the code
	 * attribute of this method using the CodeAttribute's method
	 * {@link Code#addInstructions(swp_compiler_ss13.javabite.backend.translation.Instruction...)}.
	 * </p>
	 * 
	 * @since 30.04.2013
	 * @param instructions
	 *            instances of class Instruction
	 * @see Code
	 * @see Code#addInstructions(swp_compiler_ss13.javabite.backend.translation.Instruction...)
	 * @see Instruction
	 */
	void addInstructionsToCodeAttribute(final Instruction... instructions) {
		codeAttribute.addInstructions(instructions);
	}
}
