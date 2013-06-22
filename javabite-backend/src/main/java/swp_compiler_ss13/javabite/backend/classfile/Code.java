package swp_compiler_ss13.javabite.backend.classfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swp_compiler_ss13.javabite.backend.translation.Instruction;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.LocalVariableType;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.*;

/**
 * <h1>CodeAttribute</h1>
 * <p>
 * This class represents all information needed to create a JVM-Classfile method
 * code attribute.
 * 
 * @see <a
 *      href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.3">JVM
 *      attributes Specification</a> </p>
 * 
 * @author Marco
 * @since 28.04.2013
 */
class Code {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * <h1>variableMap</h1>
	 * <p>
	 * This map maps variable names to its indices in the local variable space.
	 * The variable space is 1 byte long.
	 * </p>
	 */
	private final HashMap<String, Byte> variableMap;

	// general code attribute structure information
	private final short codeIndex;
	private short maxStack;
	private short maxLocals;
	/**
	 * <h1>codeArea</h1>
	 * <p>
	 * Ordered List of Instruction objects.
	 * 
	 * @see Instruction </p>
	 */
	private final List<Instruction> codeArea;
	private final short exceptionTableLength;
	private List<Attribute> attributes;

	/**
	 * <h1>CodeAttribute</h1>
	 * <p>
	 * This class represents all information needed to create a JVM-Classfile
	 * method code attribute.
	 * </p>
	 * 
	 * <h1>CodeAttribute constructor</h1>
	 * <p>
	 * The constructor initializes the CodeAttribute object.
	 * </p>
	 * 
	 * @since 28.04.2013
	 * @param codeIndex
	 *            short index into this classfile's constant pool of string
	 *            "Code".
	 */
	public Code(final short codeIndex) {
		this.codeIndex = codeIndex;
		variableMap = new HashMap<>();
		codeArea = new ArrayList<>();

		maxStack = 1;
		maxLocals = 1;
		exceptionTableLength = 0;
	}

	/**
	 * <h1>writeTo</h1>
	 * <p>
	 * This method uses an output stream and adds all bytes of the classfile
	 * method area method's code attribute meeting the jvm standard for javabyte
	 * classfile method area method code attributes. To do this, it uses the
	 * information in its member variables.
	 * </p>
	 * 
	 * @param classfileDOS
	 *            DataOutputStream to which the bytes are written
	 */
	void writeTo(final DataOutputStream classfileDOS) {

		// TODO use bytebuffer or classfileDOS directly?
		final ByteArrayOutputStream attributesBAOS = new ByteArrayOutputStream();
		final DataOutputStream attributesDOS = new DataOutputStream(
				attributesBAOS);

		final ByteArrayOutputStream codeBAOS = new ByteArrayOutputStream();
		final DataOutputStream codeDOS = new DataOutputStream(codeBAOS);

		maxStack = calculateMaxStack();

		try {
			logger.debug("MAX_STACK: " + maxStack);

			// maximal stack size
			attributesDOS.writeShort(maxStack);

			// maximal local variable count
			attributesDOS.writeShort(maxLocals);

			// code attribute
			for (final Instruction instruction : codeArea) {
				instruction.writeTo(codeDOS);
			}
			// TODO why use codeDOS for size() and codeBAOS for toByteArray() ?
			attributesDOS.writeInt(codeDOS.size());
			attributesDOS.write(codeBAOS.toByteArray());

			// exception table attribute (unused)
			attributesDOS.writeShort(exceptionTableLength);

			// attributes attribute
			if (attributes != null) {
				attributesDOS.writeShort(attributes.size());
				for (final Attribute ca : attributes) {
					ca.writeTo(attributesDOS);
				}
			} else {
				attributesDOS.writeShort(0);
			}

			classfileDOS.writeShort(codeIndex);
			classfileDOS.writeInt(attributesDOS.size());
			classfileDOS.write(attributesBAOS.toByteArray());

			if (logger.isDebugEnabled()) {
				logger.debug("codeIndex");
				logger.debug("{}", shortToHexString(codeIndex));
				logger.debug("code size");
				logger.debug("{}", intToHexString(codeDOS.size()));
				logger.debug("code");
				logger.debug("{}", byteArrayToHexString(codeBAOS.toByteArray()));
				logger.debug("attributes size");
				logger.debug("{}", intToHexString(attributesDOS.size()));
				logger.debug("attributes");
				logger.debug("{}",
						byteArrayToHexString(attributesBAOS.toByteArray()));
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * <h1>calculateMaxStack</h1>
	 * <p>
	 * This method calculates the size the stack maximally can reach during
	 * calculations and returns the value. Therefore it processes the
	 * Instruction objects in the {@link #codeArea} and uses the Intruction
	 * objects' methods {@link Instruction#getStackChange()}.
	 * </p>
	 * 
	 * @since 13.05.2013
	 * @return short max stack size that can be reached
	 * @see #codeArea
	 * @see Instruction
	 * @see Instruction#getStackChange()
	 */
	private short calculateMaxStack() {
		short maxStackSize = 0;
		short stackSize = 0;
		for (final Instruction instruction : codeArea) {
			stackSize += instruction.getStackChange();
			if (stackSize > maxStackSize) {
				maxStackSize = stackSize;
			}
		}
		return maxStackSize;
	}

	/**
	 * <h1>addVariable</h1>
	 * <p>
	 * This method adds a new variable to the local variable space considering
	 * the variable's name and type. If it's already existent, nothing will
	 * happen.maxLocals
	 * </p>
	 * 
	 * @since 29.04.2013
	 * @param variableName
	 *            String name of the variable to be added
	 * @param localVariableType
	 *            LocalVariableType variable type of the variable to be added
	 * @see swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.LocalVariableType
	 * @see #variableMap
	 */
	void addVariable(final String variableName,
			final LocalVariableType localVariableType) {

		if (!variableMap.containsKey(variableName)) {
			variableMap.put(variableName, (byte) maxLocals);
			maxLocals += localVariableType.length;
		}
	}

	/**
	 * <h1>getIndexOfVariable</h1>
	 * <p>
	 * This method looks up the variable name in the {@link #variableMap} of
	 * this code attribute.
	 * </p>
	 * 
	 * @since 30.04.2013
	 * @param variableName
	 *            String name of the variable
	 * @return index of the variable in local variable space of this code
	 *         attribute.
	 * @see #variableMap
	 */
	byte getIndexOfVariable(final String variableName) {
		// TODO check size of variable space overflow -> exception

		if (variableMap.containsKey(variableName)) {
			return variableMap.get(variableName);
		} else {
			return 0;
		}
	}

	/**
	 * <h1>addInstruction</h1>
	 * <p>
	 * This method adds a new Instruction to the code area of this code
	 * attribute
	 * </p>
	 * 
	 * @since 30.04.2013
	 * @param instruction
	 *            instance of class Instruction
	 * @see #codeArea
	 * @see Instruction
	 */
	void addInstruction(final Instruction instruction) {
		codeArea.add(instruction);
	}

}
