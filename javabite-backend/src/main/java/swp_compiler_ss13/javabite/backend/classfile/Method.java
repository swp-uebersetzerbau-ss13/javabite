package swp_compiler_ss13.javabite.backend.classfile;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.shortToHexString;

import java.io.DataOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.javabite.backend.Instruction;
import swp_compiler_ss13.javabite.backend.classfile.IClassfile.MethodAccessFlag;
import swp_compiler_ss13.javabite.backend.classfile.IClassfile.VariableType;

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
	private final CodeAttribute codeAttribute;

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
	 * @author Marco
	 * @since 28.04.2013
	 * @param methodName
	 *            String name of the method to be instantiated
	 * @param methodDescriptor
	 *            String descriptor of the method to be instantiated
	 * @param accessFlags
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
			final short codeIndex, final MethodAccessFlag... accessFlags) {
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
		codeAttribute = new CodeAttribute(codeIndex);

		for (final MethodAccessFlag a : accessFlags) {
			this.accessFlags = (short) (this.accessFlags | a.getValue());
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
	 * @author Robert, Marco
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
	 * {@link CodeAttribute#addVariable(String, swp_compiler_ss13.javabite.backend.classfile.IClassfile.VariableType)}
	 * .
	 * </p>
	 * 
	 * @author Marco
	 * @since 29.04.2013
	 * @param variableName
	 *            String name of the variable
	 * @param variableType
	 *            VariableType variable type of the variable
	 * @see VariableType
	 * @see CodeAttribute
	 * @see CodeAttribute#addVariable(String,
	 *      swp_compiler_ss13.javabite.backend.classfile.IClassfile.VariableType)
	 */
	void addVariableToCodeAttribute(final String variableName,
			final VariableType variableType) {
		codeAttribute.addVariable(variableName, variableType);
	}

	/**
	 * <h1>getIndexOfVariable</h1>
	 * <p>
	 * This method looks up the index of a variable name in the code attribute
	 * of this method using the CodeAttribute's method
	 * {@link CodeAttribute#getIndexOfVariable(String)}.
	 * </p>
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 * @param variableName
	 *            String name of the variable
	 * @return index of the variable in local variable space of this method.
	 * @see CodeAttribute
	 * @see CodeAttribute#getIndexOfVariable(String)
	 */
	byte getIndexOfVariable(final String variableName) {
		return codeAttribute.getIndexOfVariable(variableName);
	}

	/**
	 * <h1>addInstructionToCodeAttribute</h1>
	 * <p>
	 * This method adds a new Instruction object to the code area of the code
	 * attribute of this method using the CodeAttribute's method
	 * {@link CodeAttribute#addInstruction(Instruction)}.
	 * </p>
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 * @param instruction
	 *            instance of class Instruction
	 * @see CodeAttribute
	 * @see CodeAttribute#addInstruction(Instruction)
	 * @see Instruction
	 */
	void addInstructionToCodeAttribute(final Instruction instruction) {

		codeAttribute.addInstruction(instruction);
	}
}
