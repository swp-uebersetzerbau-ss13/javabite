package swp_compiler_ss13.javabite.backend.translation;

import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.utils.*;

import java.util.*;

import static swp_compiler_ss13.javabite.backend.utils.ConstantUtils.*;

public abstract class AbstractBuilder {

	// saves all jump instructions for later offset calculation
	private final List<JumpInstruction> jumpInstructions;
	// saves all jump targets (labels) for jump instruction creation
	private final Map<String, Instruction> jumpTargets;
	// label name of last label
	private final Stack<String> labelNames;
	// the classfile instance of this program
	protected final Classfile classfile;
	// the method name of this program
	protected final String methodName;
	// the list of operations of this program
	protected final List<Operation> operations;
	// sizes of last array declaration, in reverse order on the stack
	protected final List<String> arrayLengths;
	// name of last array seen
	protected String arrayName;

	public AbstractBuilder(final Classfile classfile, final String methodName) {
		this.jumpInstructions = new ArrayList<>();
		this.jumpTargets = new HashMap<>();
		this.labelNames = new Stack<>();
		this.classfile = classfile;
		this.methodName = methodName;
		this.operations = new ArrayList<>();
		this.arrayLengths = new ArrayList<>();
	}

	/**
	 * Builds the instance of the program. Various tasks are performed before
	 * creation of the program instance. The default behavior is to 1. ensure a
	 * return statement at the end of the program, 2. to calculate the effective
	 * byte offset of each operation and 3. to replace all jumping instruction
	 * placeholders with real instructions and their calculated offsets.
	 * 
	 * @return new program instance
	 */
	public Program build() {
		boolean needsReturn = !(operations.isEmpty() || operations
				.get(operations.size() - 1).getLastInstruction().getMnemonic() == Mnemonic.RETURN);

		// check, whether there is a return instruction in the end
		// if not, set it
		if (!labelNames.isEmpty() && !needsReturn) {
			addNop();
		}

		if (needsReturn) {
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
		for (final ListIterator<JumpInstruction> instructionIterator = jumpInstructions
				.listIterator(); instructionIterator.hasNext();) {
			final JumpInstruction in = instructionIterator.next();
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
				final JumpInstruction newJi = JumpInstruction.copyOf(in,
						Mnemonic.GOTO_W, null);
				newJi.setArguments(ByteUtils.intToByteArray(offset));
				instructionIterator.set(newJi);
			} else {
				in.setArguments(ByteUtils.shortToByteArray((short) offset));
			}
		}

		return new Program(operations);
	}

	/**
	 * Adds an operation to this program. Each operation is added to a list of
	 * operations. Additionally, it is checked if labels were found before this
	 * operation, then these operations are registered as jumping targets, or
	 * contains jumping instructions, which are registered as such.
	 * 
	 * @param operation
	 *            operation to add
	 */
	protected void add(final Operation operation) {
		operations.add(operation);

		// check if jumping targets are expected
		while (!labelNames.isEmpty()) {
			jumpTargets.put(labelNames.pop(), operation.getInstruction(0));
		}

		// check for jumping instructions
		for (final Instruction instruction : operation.getInstructions()) {
			if (instruction instanceof JumpInstruction) {
				jumpInstructions.add((JumpInstruction) instruction);
			}
		}
	}

	/**
	 * adds a label to the stack of expected labels. Sets the label flag to
	 * true, if not already true.
	 * 
	 * @param labelName
	 *            name of label
	 */
	protected void addLabel(final String labelName) {
		labelNames.push(labelName);
	}

	/**
	 * adds a return operation to the program.
	 */
	private void addReturnOp() {
		final Operation.Builder op = new Operation.Builder();
		op.add(Mnemonic.RETURN);
		add(op.build());
	}

	/**
	 * adds a nop operation to the program.
	 */
	protected void addNop() {
		final Operation.Builder op = new Operation.Builder();
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

	protected Instruction fieldStoreInstruction(final FieldSignature signature) {
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
	protected Operation fieldNewObjectOperation(
			final MethodSignature constructor, final String store) {
		final Operation.Builder op = new Operation.Builder();
		final short classIndex = classfile
				.addClassConstantToConstantPool(constructor.methodClass);
		assert classIndex > 0 : "index is zero";
		final short cstrIndex = classfile
				.addMethodrefConstantToConstantPool(constructor);
		assert cstrIndex > 0 : "index is zero";
		if (store != null) {
			op.add(Mnemonic.ALOAD_0);
		}
		op.add(Mnemonic.NEW, ByteUtils.shortToByteArray(classIndex));
		op.add(Mnemonic.DUP);
		op.add(Mnemonic.INVOKESPECIAL, ByteUtils.shortToByteArray(cstrIndex));
		if (store != null) {
			final FieldSignature fieldSignature = new FieldSignature(store,
					classfile.getClassname(),
					constructor.methodClass.typeClassName);
			final short fieldIndex = classfile
					.addFieldrefConstantToConstantPool(fieldSignature);
			assert fieldIndex > 0 : "index is zero";
			op.add(Mnemonic.PUTFIELD, ByteUtils.shortToByteArray(fieldIndex));
		}
		return op.build();
	}

	protected Operation fieldArrayInit(final ClassSignature arraySignature,
			final short cpoolIndex, final short classCstrCpoolIndex,
			final int dimensionsLeft, final List<Byte> indexVars,
			final boolean last) {
		final FieldSignature arrayField = new FieldSignature(arrayName,
				classfile.getClassname(), arraySignature.typeClassName);
		final short arrayFieldIndex = classfile
				.addFieldrefConstantToConstantPool(arrayField);
		final Operation arrayLoad = new Operation.Builder()
				.add(Mnemonic.ALOAD_0)
				.add(Mnemonic.GETFIELD,
						ByteUtils.shortToByteArray(arrayFieldIndex)).build();
		return arrayInit(arrayLoad, cpoolIndex, classCstrCpoolIndex,
				dimensionsLeft, indexVars, last);
	}

	protected Operation localArrayInit(final short cpoolIndex,
			final short classCstrCpoolIndex, final int dimensionsLeft,
			final List<Byte> indexVars, final boolean last) {
		final byte arrayVarIndex = classfile.addVariableToMethodsCode(
				methodName, arrayName, ClassfileUtils.LocalVariableType.AREF);
		final Operation arrayLoad = new Operation.Builder().add(
				Mnemonic.ALOAD.withIndex(arrayVarIndex), arrayVarIndex).build();
		return arrayInit(arrayLoad, cpoolIndex, classCstrCpoolIndex,
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
	private Operation arrayInit(final Operation arrayLoadOperation,
			final short classCpoolIndex, final short classCstrCpoolIndex,
			final int dimensionsLeft, List<Byte> indexVars, final boolean last) {
		final Operation.Builder op = new Operation.Builder();
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

			op.add(arrayLoadOperation);

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
			op.add(arrayInit(arrayLoadOperation, classCpoolIndex,
					classCstrCpoolIndex, dimensionsLeft - 1, indexVars,
					dimensionsLeft == 1));

			op.add(Mnemonic.IINC, currentVarIndex, (byte) 1);
			final JumpInstruction ji = new JumpInstruction(Mnemonic.GOTO,
					varLoadInstruction);
			op.add(ji);
			jumpInstructions.add(ji);

			op.add(cmpTarget);

		} else {
			op.add(arrayLoadOperation);

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

	protected Operation fieldArrayCreateOp(final ClassSignature arraySignature,
			final byte primitiveTag) {
		final Operation.Builder op = new Operation.Builder();
		final FieldSignature arrayField = new FieldSignature(arrayName,
				classfile.getClassname(), arraySignature.typeClassName);
		op.add(arrayCreateOp(arraySignature, primitiveTag));
		op.add(fieldStoreInstruction(arrayField));
		return op.build();
	}

	protected Operation fieldArrayCreateOp(final ClassfileUtils.JavaType type) {
		final byte dimensions = (byte) arrayLengths.size();
		final ClassSignature arrayClass = new ClassSignature(dimensions,
				type.classSignature.className);
		return fieldArrayCreateOp(arrayClass, type.value);
	}

	protected Operation localArrayCreateOp(final ClassSignature arraySignature,
			final byte primitiveTag) {
		final Operation.Builder op = new Operation.Builder();
		op.add(arrayCreateOp(arraySignature, primitiveTag));
		op.add(localStoreInstruction(arrayName,
				ClassfileUtils.LocalVariableType.AREF));
		return op.build();
	}

	protected Operation localArrayCreateOp(final ClassfileUtils.JavaType type) {
		final byte dimensions = (byte) arrayLengths.size();
		final ClassSignature arrayClass = new ClassSignature(dimensions,
				type.classSignature.className);
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
	private Operation arrayCreateOp(final ClassSignature arrayClass,
			final byte primitiveTag) {
		assert !ConstantUtils.isIgnoreParam(arrayName) : "array name is empty";
		assert !arrayLengths.isEmpty() : "array has zero dimensions";
		final Operation.Builder op = new Operation.Builder();

		op.add(Mnemonic.ALOAD_0);

		// add all dimensions to stack for array creation
		for (final String arrayLength : arrayLengths) {
			op.add(localLoadInstruction(arrayLength,
					ClassfileUtils.LocalVariableType.LONG));
			op.add(Mnemonic.L2I);
		}
		arrayLengths.clear();

		if (arrayClass.arrayDimensions > 1) {
			// if more than 1 dimension, create a multi dimensional array
			// every multi dimensional array is an array of references
			final short classIndex = classfile
					.addClassConstantToConstantPool(arrayClass.typeClassName);
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

	/**
	 * Sets the field of a struct stored inside a field to a specified value.
	 * 
	 * @param valueName
	 *            name (variable name or constant value) of value to set
	 * @param structClassName
	 *            class name of struct
	 * @param fieldName
	 *            field name of field to set
	 * @param valueType
	 *            data type of value
	 * @return new operation instance
	 */
	protected Operation fieldStructSetFieldOp(final String valueName,
			final String structClassName, final String fieldName,
			final ClassfileUtils.LocalVariableType valueType) {
		final Operation.Builder op = new Operation.Builder();
		op.add(Mnemonic.ALOAD_0);
		op.add(localLoadInstruction(valueName, valueType));
		op.add(structSetFieldOp(structClassName, fieldName, valueType));
		return op.build();
	}

	/**
	 * Sets the field of a struct stored inside a local variable to a specified
	 * value. Because a field reference is needed to store a value inside a
	 * struct field, both the struct classname and variable name are needed to
	 * perform this action.
	 * 
	 * @param valueName
	 *            name (variable name or constant value) of value to set
	 * @param structClassName
	 *            class name of struct
	 * @param structVarName
	 *            variable name of struct
	 * @param fieldName
	 *            field name of field to set
	 * @param valueType
	 *            data type of value
	 * @return new operation instance
	 */
	protected Operation localStructSetFieldOp(final String valueName,
			final String structClassName, final String structVarName,
			final String fieldName,
			final ClassfileUtils.LocalVariableType valueType) {
		final Operation.Builder op = new Operation.Builder();
		op.add(localLoadInstruction(structVarName,
				ClassfileUtils.LocalVariableType.AREF));
		op.add(localLoadInstruction(valueName, valueType));
		op.add(structSetFieldOp(structClassName, fieldName, valueType));
		return op.build();
	}

	private Operation structSetFieldOp(final String structName,
			final String fieldName,
			final ClassfileUtils.LocalVariableType variableType) {
		final Operation.Builder op = new Operation.Builder();
		final String fieldType;
		if (variableType.javaType != null) {
			fieldType = variableType.javaType.classSignature.className;
		} else {
			fieldType = structName + "_" + fieldName;
		}
		final FieldSignature fieldSignature = new FieldSignature(fieldName,
				structName, fieldType);
		op.add(fieldStoreInstruction(fieldSignature));
		return op.build();
	}

}
