package swp_compiler_ss13.javabite.backend.translation;

import static swp_compiler_ss13.javabite.backend.utils.ConstantUtils.convertBooleanConstant;
import static swp_compiler_ss13.javabite.backend.utils.ConstantUtils.isBooleanConstant;
import static swp_compiler_ss13.javabite.backend.utils.ConstantUtils.isConstant;
import static swp_compiler_ss13.javabite.backend.utils.ConstantUtils.removeConstantSign;

import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.utils.ByteUtils;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils;
import swp_compiler_ss13.javabite.backend.utils.ConstantUtils;

public abstract class AbstractBuilder<T extends AbstractBuilder<?>> {

	// the classfile instance of this program
	protected final Classfile classfile;
	// the method name of this program
	protected final String methodName;
	// the list of operations of this program
	protected final List<Operation> operations;
	// determines, whether the System exit method has already been added/
	// a return statement is present in the tac
	protected boolean returnFlag;

	public AbstractBuilder(final Classfile classfile, final String methodName) {
		this.classfile = classfile;
		this.methodName = methodName;
		operations = new ArrayList<>();
	}

	public Program build() {
		// check, whether there is a return instruction in the end
		// if not, set it
		if (!returnFlag) {
			addReturnOp();
		}

		// calculate offsets for jumping
		int currentOffset = 0;
		for (final Operation op : operations) {
			for (final Instruction in : op.getInstructions()) {
				in.setOffset(currentOffset);
				currentOffset += in.getByteCount();
			}
		}

		prepareBuild();

		return new Program(operations);
	}

	protected void prepareBuild() {
	}

	protected AbstractBuilder<T> add(final Operation operation) {
		operations.add(operation);
		return this;
	}

	private void addReturnOp() {
		final Operation.Builder op = Operation.Builder.newBuilder();
		op.add(Mnemonic.RETURN);
		add(op.build());
	}

	protected void addNop() {
		final Operation.Builder op = Operation.Builder.newBuilder();
		op.add(Mnemonic.NOP);
		add(op.build());
	}

	protected static boolean hasArgsCount(final Quadruple q,
			final int... argsCounts) {
		int argc = 0;
		if (!ConstantUtils.SYMBOL_IGNORE_PARAM.equals(q.getArgument1()))
			argc++;
		if (!ConstantUtils.SYMBOL_IGNORE_PARAM.equals(q.getArgument2()))
			argc++;
		if (!ConstantUtils.SYMBOL_IGNORE_PARAM.equals(q.getResult()))
			argc++;
		for (final int i : argsCounts) {
			if (i == argc)
				return true;
		}
		return false;
	}

	// INSTRUCTION CREATORS ------------------------------------------------

	/**
	 * Creates a load instruction, that can be added to a program flow. This
	 * operation can distinguish between constants and variables, but needs
	 * information on the size of the variable to be loaded.
	 * 
	 * @param arg1
	 *            argument containing constant or variable name
	 * @param variableType
	 *            type of variable/constant to load
	 */
	protected Instruction loadInstruction(final String arg1,
			final ClassfileUtils.LocalVariableType variableType) {
		if (isBooleanConstant(arg1)) {
			return new Instruction(convertBooleanConstant(arg1));
		} else if (isConstant(arg1)) {
			assert variableType != null;
			final short index = classfile.getIndexOfConstantInConstantPool(
					variableType.constantPoolType, removeConstantSign(arg1));
			assert index > 0;
			if (variableType.wide) {
				return new Instruction(Mnemonic.LDC2_W,
						ByteUtils.shortToByteArray(index));
			} else if (index >= 256) {
				return new Instruction(Mnemonic.LDC_W,
						ByteUtils.shortToByteArray(index));
			} else {
				return new Instruction(Mnemonic.LDC, (byte) index);
			}
		} else {
			final byte index = classfile.getIndexOfVariableInMethod(methodName,
					arg1);
			assert index > 0;
			return new Instruction(variableType.varLoadOp.withIndex(index),
					index);
		}
	}

	/**
	 * creates a store operation, which stores the value on the stack into a
	 * variable identified by the result-string.
	 * 
	 * @param result
	 *            name of variable to store value in
	 * @param variableType
	 *            type of variable/constant to store
	 * @return new instruction
	 */
	protected Instruction storeInstruction(final String result,
			final ClassfileUtils.LocalVariableType variableType) {
		final byte index = classfile.getIndexOfVariableInMethod(methodName,
				result);
		assert index > 0;
		return new Instruction(variableType.varStoreOp.withIndex(index), index);
	}

	/**
	 * Creates a series of operations to create a new object. The newly created
	 * object is stored inside a local variable (if a store-target is present)
	 * 
	 * @param constructor
	 *            signature of constructor method
	 * @param store
	 *            name of variable to store object instance in
	 * @return new operation instance
	 */
	protected Operation createOperation(
			final ClassfileUtils.MethodSignature constructor, final String store) {
		final Operation.Builder op = Operation.Builder.newBuilder();
		final short classIndex = classfile
				.addClassConstantToConstantPool(constructor.methodClass);
		assert classIndex > 0;
		final short cstrIndex = classfile
				.addMethodrefConstantToConstantPool(constructor);
		assert cstrIndex > 0;
		if (store != null) {
			// TODO bla
			op.add(Mnemonic.ALOAD_0);
		}
		op.add(Mnemonic.NEW, ByteUtils.shortToByteArray(classIndex));
		op.add(Mnemonic.DUP);
		op.add(Mnemonic.INVOKESPECIAL, ByteUtils.shortToByteArray(cstrIndex));
		if (store != null) {
			final ClassfileUtils.FieldSignature fieldSignature = new ClassfileUtils.FieldSignature(
					store, classfile.getClassname(), constructor.methodClass,
					false);
			final short fieldIndex = classfile
					.addFieldrefConstantToConstantPool(fieldSignature);
			assert fieldIndex > 0;
			op.add(Mnemonic.PUTFIELD, ByteUtils.shortToByteArray(fieldIndex));
		}
		return op.build();
	}

}
