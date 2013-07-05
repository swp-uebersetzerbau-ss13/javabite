package swp_compiler_ss13.javabite.backend.translation;

import static swp_compiler_ss13.javabite.backend.utils.ConstantUtils.convertBooleanConstant;
import static swp_compiler_ss13.javabite.backend.utils.ConstantUtils.isBooleanConstant;
import static swp_compiler_ss13.javabite.backend.utils.ConstantUtils.isConstant;
import static swp_compiler_ss13.javabite.backend.utils.ConstantUtils.removeConstantSign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.utils.ByteUtils;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils;
import swp_compiler_ss13.javabite.backend.utils.ConstantUtils;

public abstract class AbstractBuilder<T extends AbstractBuilder<T>> {

	// saves all jump instructions for later offset calculation
	private final List<JumpInstruction> jumpInstructions;
	// saves all jump targets (labels) for jump instruction creation
	private final Map<String, Instruction> jumpTargets;
	// indicates last instruction was a label, denoting a jump location
	private boolean labelFlag;
	// label name of last label
	private final Stack<String> labelNames;
	// the classfile instance of this program
	protected final Classfile classfile;
	// the method name of this program
	protected final String methodName;
	// the list of operations of this program
	protected final List<Operation> operations;
	// determines, whether the System exit method has already been added/
	// a return statement is present in the tac
	protected boolean returnFlag;
	// sizes of last array declaration, in reverse order on the stack
	protected final Stack<String> arrayLengths;
	// name of last array seen
	protected String arrayName;

	public AbstractBuilder(final Classfile classfile, final String methodName) {
		this.jumpInstructions = new ArrayList<>();
		this.jumpTargets = new HashMap<>();
		this.labelNames = new Stack<>();
		this.classfile = classfile;
		this.methodName = methodName;
		this.operations = new ArrayList<>();
		this.arrayLengths = new Stack<>();
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

		// caluclate jump offset for every jump, set as argument of jump
		for (final JumpInstruction in : jumpInstructions) {
			Instruction target = in.getTargetInstruction();
			if (target == null) {
				// target instruction is null -> target label is set
				// get instruction by label name
				target = jumpTargets.get(in.getTargetLabel());
			}
			assert target != null : "jump target is null";
			// calculate offset delta from jump instruction to target
			final int offset = target.getOffset() - in.getOffset();
			if (offset > Short.MAX_VALUE) {
				in.setMnemonic(Mnemonic.GOTO_W);
				in.setArguments(ByteUtils.intToByteArray(offset));
			} else {
				in.setArguments(ByteUtils.shortToByteArray((short) offset));
			}
		}

		return new Program(operations);
	}

	protected T add(final Operation operation) {
		operations.add(operation);

		if (labelFlag) {
			labelFlag = false;
			while (!labelNames.isEmpty()) {
				jumpTargets.put(labelNames.pop(), operation.getInstruction(0));
			}
		}

		for (final Instruction instruction : operation.getInstructions()) {
			if (instruction instanceof JumpInstruction) {
				jumpInstructions.add((JumpInstruction) instruction);
			}
		}

		return (T) this;
	}

	protected void addLabel(final String labelName) {
		labelFlag = true;
		labelNames.push(labelName);
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

	// INSTRUCTION CREATORS ------------------------------------------------

	protected static Instruction localLoadVariableInstruction(final byte index,
			final ClassfileUtils.LocalVariableType variableType) {
		assert index > 0 : "index is zero";
		return new Instruction(variableType.varLoadOp.withIndex(index), index);
	}

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
	protected Instruction localLoadInstruction(final String arg1,
			final ClassfileUtils.LocalVariableType variableType) {
		if (isBooleanConstant(arg1)) {
			return new Instruction(convertBooleanConstant(arg1));
		} else if (isConstant(arg1)) {
			assert variableType != null : "variable type is null";
			final short index = classfile.getIndexOfConstantInConstantPool(
					variableType.constantPoolType, removeConstantSign(arg1));
			assert index > 0 : "index is zero";
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
			final byte index = classfile.addVariableToMethodsCode(methodName,
					arg1, variableType);
			return localLoadVariableInstruction(index, variableType);
		}
	}

	/**
	 * TODO javadoc
	 * 
	 * @param index
	 * @param variableType
	 * @return
	 */
	protected static Instruction localStoreInstruction(final byte index,
			final ClassfileUtils.LocalVariableType variableType) {
		assert index > 0 : "index is zero";
		return new Instruction(variableType.varStoreOp.withIndex(index), index);
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
	protected Instruction localStoreInstruction(final String result,
			final ClassfileUtils.LocalVariableType variableType) {
		final byte index = classfile.addVariableToMethodsCode(methodName,
				result, variableType);
		return localStoreInstruction(index, variableType);
	}

	protected Instruction fieldStoreInstruction(
			final ClassfileUtils.FieldSignature signature) {
		final short fieldIndex = classfile
				.addFieldrefConstantToConstantPool(signature);
		assert fieldIndex > 0 : "index is zero";
		return new Instruction(Mnemonic.PUTFIELD,
				ByteUtils.shortToByteArray(fieldIndex));
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
	protected Operation newObjectOperation(
			final ClassfileUtils.MethodSignature constructor, final String store) {
		final Operation.Builder op = Operation.Builder.newBuilder();
		final short classIndex = classfile
				.addClassConstantToConstantPool(constructor.methodClass);
		assert classIndex > 0 : "index is zero";
		final short cstrIndex = classfile
				.addMethodrefConstantToConstantPool(constructor);
		assert cstrIndex > 0 : "index is zero";
		if (store != null) {
			// TODO bla
			op.add(Mnemonic.ALOAD_0);
		}
		op.add(Mnemonic.NEW, ByteUtils.shortToByteArray(classIndex));
		op.add(Mnemonic.DUP);
		op.add(Mnemonic.INVOKESPECIAL, ByteUtils.shortToByteArray(cstrIndex));
		// TODO refactor
		if (store != null) {
			final ClassfileUtils.FieldSignature fieldSignature = new ClassfileUtils.FieldSignature(
					store, classfile.getClassname(),
					constructor.methodClass.getClassNameAsType());
			final short fieldIndex = classfile
					.addFieldrefConstantToConstantPool(fieldSignature);
			assert fieldIndex > 0 : "index is zero";
			op.add(Mnemonic.PUTFIELD, ByteUtils.shortToByteArray(fieldIndex));
		}
		return op.build();
	}

	protected Operation fieldArrayInit(
			final ClassfileUtils.ClassSignature arraySignature,
			final short cpoolIndex, final short classCstrCpoolIndex,
			final int dimensionsLeft, final List<Byte> indexVars,
			final boolean last) {
		final ClassfileUtils.FieldSignature arrayField = new ClassfileUtils.FieldSignature(
				arrayName, classfile.getClassname(),
				arraySignature.getClassNameAsType());
		final short arrayFieldIndex = classfile
				.addFieldrefConstantToConstantPool(arrayField);
		return arrayInit(
				new Instruction(Mnemonic.GETFIELD,
						ByteUtils.shortToByteArray(arrayFieldIndex)),
				cpoolIndex, classCstrCpoolIndex, dimensionsLeft, indexVars,
				last);
	}

	protected Operation localArrayInit(final short cpoolIndex,
			final short classCstrCpoolIndex, final int dimensionsLeft,
			final List<Byte> indexVars, final boolean last) {
		final byte arrayVarIndex = classfile.addVariableToMethodsCode(
				methodName, arrayName, ClassfileUtils.LocalVariableType.AREF);
		return arrayInit(
				new Instruction(Mnemonic.ALOAD.withIndex(arrayVarIndex),
						arrayVarIndex), cpoolIndex, classCstrCpoolIndex,
				dimensionsLeft, indexVars, last);
	}

	/**
	 * Initializes an array of structs. Because it is an array of objects, every
	 * object has to be instantiated before the array can be used. This has to
	 * be done for every dimension.
	 * 
	 * @param classCpoolIndex
	 *            index of array class
	 * @param classCstrCpoolIndex
	 *            index of constructor of array class
	 * @param dimensionsLeft
	 *            number of array dimensions left to visit
	 * @param indexVars
	 *            currently used index variables to access visited dimensions
	 * @param last
	 *            recursion anchor, will be true if visiting last dimension
	 * @return new operation sequence
	 */
	private Operation arrayInit(final Instruction arrayLoadInstruction,
			final short classCpoolIndex, final short classCstrCpoolIndex,
			final int dimensionsLeft, List<Byte> indexVars, final boolean last) {
		final Operation.Builder op = Operation.Builder.newBuilder();
		if (indexVars == null)
			indexVars = new ArrayList<>(dimensionsLeft);

		if (!last) {
			op.add(Mnemonic.ICONST_0);

			final byte currentVarIndex = classfile.addVariableToMethodsCode(
					methodName, UUID.randomUUID().toString(),
					ClassfileUtils.LocalVariableType.BOOLEAN);

			op.add(localStoreInstruction(currentVarIndex,
					ClassfileUtils.LocalVariableType.BOOLEAN));

			final Instruction varLoadInstruction = localLoadVariableInstruction(
					currentVarIndex, ClassfileUtils.LocalVariableType.BOOLEAN);
			op.add(varLoadInstruction);

			op.add(arrayLoadInstruction);

			for (final byte varIndex : indexVars) {
				op.add(Mnemonic.ILOAD.withIndex(varIndex), varIndex);
				op.add(Mnemonic.AALOAD);
			}

			op.add(Mnemonic.ARRAYLENGTH);

			final Instruction cmpTarget = new Instruction(Mnemonic.NOP);
			final JumpInstruction zeroCmp = new JumpInstruction(
					Mnemonic.IF_ICMPGE, cmpTarget);
			op.add(zeroCmp);
			jumpInstructions.add(zeroCmp);

			indexVars.add(currentVarIndex);
			op.add(arrayInit(arrayLoadInstruction, classCpoolIndex,
					classCstrCpoolIndex, dimensionsLeft - 1, indexVars,
					dimensionsLeft == 1));

			op.add(Mnemonic.IINC, currentVarIndex, (byte) 1);
			final JumpInstruction ji = new JumpInstruction(Mnemonic.GOTO,
					varLoadInstruction);
			op.add(ji);
			jumpInstructions.add(ji);

			op.add(cmpTarget);

		} else {
			op.add(arrayLoadInstruction);

			for (int i = 0, lastIndex = indexVars.size() - 1; i <= lastIndex; i++) {
				final byte varIndex = indexVars.get(i);
				op.add(Mnemonic.ILOAD.withIndex(varIndex), varIndex);
				if (i < lastIndex)
					op.add(Mnemonic.AALOAD);
			}

			op.add(Mnemonic.NEW, ByteUtils.shortToByteArray(classCpoolIndex));
			op.add(Mnemonic.DUP);
			op.add(Mnemonic.INVOKESPECIAL,
					ByteUtils.shortToByteArray(classCstrCpoolIndex));
			op.add(Mnemonic.AASTORE);
		}

		return op.build();
	}

	protected Operation fieldArrayCreateOp(
			final ClassfileUtils.ClassSignature arraySignature,
			final byte primitiveTag) {
		final Operation.Builder op = Operation.Builder.newBuilder();
		final ClassfileUtils.FieldSignature arrayField = new ClassfileUtils.FieldSignature(
				arrayName, classfile.getClassname(),
				arraySignature.getClassNameAsType());
		op.add(arrayCreateOp(arraySignature, primitiveTag));
		op.add(fieldStoreInstruction(arrayField));
		return op.build();
	}

	protected Operation fieldArrayCreateOp(final ClassfileUtils.JavaType type) {
		final byte dimensions = (byte) arrayLengths.size();
		final ClassfileUtils.ClassSignature arrayClass = new ClassfileUtils.ClassSignature(
				type.classSignature.getClassNameAsContainer(), dimensions);
		return fieldArrayCreateOp(arrayClass, type.value);
	}

	protected Operation localArrayCreateOp(
			final ClassfileUtils.ClassSignature arraySignature,
			final byte primitiveTag) {
		final Operation.Builder op = Operation.Builder.newBuilder();
		op.add(arrayCreateOp(arraySignature, primitiveTag));
		op.add(localStoreInstruction(arrayName,
				ClassfileUtils.LocalVariableType.AREF));
		return op.build();
	}

	protected Operation localArrayCreateOp(final ClassfileUtils.JavaType type) {
		final byte dimensions = (byte) arrayLengths.size();
		final ClassfileUtils.ClassSignature arrayClass = new ClassfileUtils.ClassSignature(
				type.classSignature.getClassNameAsContainer(), dimensions);
		return localArrayCreateOp(arrayClass, type.value);
	}

	/**
	 * Creates a new array. There are three types of arrays: single dimension
	 * primitive type, single dimension object type, multi dimension object
	 * type. Because multi dimensional arrays contain arrays, both primitive and
	 * object types can be stored within.
	 * 
	 * TODO javadoc
	 * 
	 * @return new operation instance
	 */
	private Operation arrayCreateOp(
			final ClassfileUtils.ClassSignature arrayClass,
			final byte primitiveTag) {
		assert !ConstantUtils.isIgnoreParam(arrayName) : "array name is empty";
		assert !arrayLengths.isEmpty() : "array has zero dimensions";
		final Operation.Builder op = Operation.Builder.newBuilder();

		op.add(Mnemonic.ALOAD_0);

		// add all dimensions to stack for array creation
		while (!arrayLengths.isEmpty()) {
			op.add(localLoadInstruction(arrayLengths.pop(),
					ClassfileUtils.LocalVariableType.LONG));
			op.add(Mnemonic.L2I);
		}

		if (arrayClass.arrayDimensions > 1) {
			// if more than 1 dimension, create a multi dimensional array
			// every multi dimensional array is an array of references
			final short classIndex = classfile
					.addClassConstantToConstantPool(arrayClass
							.getClassNameAsType());
			assert classIndex > 0 : "index is zero";
			final byte[] classIndexArray = ByteUtils
					.shortToByteArray(classIndex);
			assert classIndexArray.length == 2 : "array class index byte count is not 2: "
					+ classIndexArray.length;
			op.add(Mnemonic.MULTIANEWARRAY, classIndexArray[0],
					classIndexArray[1], arrayClass.arrayDimensions);
		} else if (arrayClass.isPrimitive) {
			// if single dimensional and primitive, create with type tagByte
			op.add(Mnemonic.NEWARRAY, primitiveTag);
		} else {
			// if single dimensional and complex (object), create with
			// class reference
			final short classIndex = classfile
					.addClassConstantToConstantPool(arrayClass.baseClassName);
			op.add(Mnemonic.ANEWARRAY, ByteUtils.shortToByteArray(classIndex));
		}

		return op.build();
	}

	protected Operation fieldStructSetFieldOp(final String valueName,
			final String structName, final String fieldName,
			final ClassfileUtils.LocalVariableType variableType) {
		final Operation.Builder op = Operation.Builder.newBuilder();
		op.add(Mnemonic.ALOAD_0);
		op.add(localLoadInstruction(valueName, variableType));
		op.add(structSetFieldOp(structName, fieldName, variableType));
		return op.build();
	}

	protected Operation localStructSetFieldOp(final String valueName,
			final String structClassName, final String structVarName,
			final String fieldName,
			final ClassfileUtils.LocalVariableType variableType) {
		final Operation.Builder op = Operation.Builder.newBuilder();
		op.add(localLoadInstruction(structVarName,
				ClassfileUtils.LocalVariableType.AREF));
		op.add(localLoadInstruction(valueName, variableType));
		op.add(structSetFieldOp(structClassName, fieldName, variableType));
		return op.build();
	}

	private Operation structSetFieldOp(final String structName,
			final String fieldName,
			final ClassfileUtils.LocalVariableType variableType) {
		final Operation.Builder op = Operation.Builder.newBuilder();
		final String fieldType;
		if (variableType.javaType != null) {
			fieldType = variableType.javaType.classSignature
					.getClassNameAsContainer();
		} else {
			fieldType = structName + "_" + fieldName;
		}
		final ClassfileUtils.FieldSignature fieldSignature = new ClassfileUtils.FieldSignature(
				fieldName, structName, fieldType);
		op.add(fieldStoreInstruction(fieldSignature));
		return op.build();
	}

}
