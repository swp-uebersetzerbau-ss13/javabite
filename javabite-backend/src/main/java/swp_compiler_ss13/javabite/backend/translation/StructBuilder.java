package swp_compiler_ss13.javabite.backend.translation;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.utils.ByteUtils;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils;
import swp_compiler_ss13.javabite.backend.utils.ConstantUtils;

import java.util.Stack;

public class StructBuilder extends AbstractBuilder<StructBuilder> {

	// sizes of last array declaration, in reverse order on the stack
	private final Stack<String> arrayDimensions;
	// name of last array seen
	private String arrayName;

	public StructBuilder(final Classfile classfile, final String methodName) {
		super(classfile, methodName);
		this.arrayDimensions = new Stack<>();
	}

	@Override
	protected StructBuilder add(final Operation operation) {
		super.add(operation);
		return this;
	}

	private Instruction structPutfieldInstruction(
			final ClassfileUtils.FieldSignature signature) {
		final short fieldIndex = classfile
				.addFieldrefConstantToConstantPool(signature);
		assert fieldIndex > 0;
		return new Instruction(Mnemonic.PUTFIELD,
				ByteUtils.shortToByteArray(fieldIndex));
	}

	private Operation structInitField(final Quadruple q,
			final ClassfileUtils.LocalVariableType variableType) {
		final Operation.Builder op = Operation.Builder.newBuilder();
		op.add(Mnemonic.ALOAD_0);
		op.add(loadInstruction(q.getArgument1(), variableType));
		final String structName = classfile.getClassname();
		final String fieldType;
		if (variableType.javaType != null) {
			fieldType = variableType.javaType.className;
		} else {
			fieldType = structName + "_" + q.getArgument2();
		}
		final ClassfileUtils.FieldSignature fieldSignature = new ClassfileUtils.FieldSignature(
				q.getResult(), structName, fieldType);
		op.add(structPutfieldInstruction(fieldSignature));
		return op.build();
	}

	/**
	 * Creates a new array. There are three types of arrays: single dimension
	 * primitive type, single dimension object type, multi dimension object
	 * type. Because multi dimensional arrays contain arrays, both primitive and
	 * object types can be stored within.
	 * 
	 * @param type
	 *            datatype of array contents
	 * @return new operation instance
	 */
	private Operation arrayCreateOp(final ClassfileUtils.JavaType type) {
		assert !ConstantUtils.isIgnoreParam(arrayName)
				&& !arrayDimensions.isEmpty();
		final Operation.Builder op = Operation.Builder.newBuilder();
		final byte dimensions = (byte) arrayDimensions.size();

		final ClassfileUtils.ClassSignature arrayClass = new ClassfileUtils.ClassSignature(
				type.className, dimensions);

        op.add(Mnemonic.ALOAD_0);

		// add all dimensions to stack for array creation
		while (!arrayDimensions.isEmpty()) {
			op.add(loadInstruction(arrayDimensions.pop(),
					ClassfileUtils.LocalVariableType.LONG));
			op.add(Mnemonic.L2I);
		}

		if (dimensions > 1) {
			// if more than 1 dimension, create a multi dimensional array
			// every multi dimensional array is an array of references

			final short classIndex = classfile
					.addClassConstantToConstantPool(arrayClass
							.getClassNameAsType());
			assert classIndex > 0;
			final byte[] classIndexArray = ByteUtils
					.shortToByteArray(classIndex);
			assert classIndexArray.length == 2;
			op.add(Mnemonic.MULTIANEWARRAY, classIndexArray[0],
					classIndexArray[1], dimensions);
		} else if (type.isPrimitive()) {
			// if single dimensional and primitive, create with type tagByte
			op.add(Mnemonic.NEWARRAY, type.value);
		} else {
			// if single dimensional and complex (object), create with
			// class reference
			final short classIndex = classfile
					.addClassConstantToConstantPool(type.className);
			op.add(Mnemonic.ANEWARRAY, ByteUtils.shortToByteArray(classIndex));
		}
		final ClassfileUtils.FieldSignature fieldSignature = new ClassfileUtils.FieldSignature(
				arrayName, classfile.getClassname(),
				arrayClass.getClassNameAsType());
		op.add(structPutfieldInstruction(fieldSignature));
		return op.build();
	}

	// OPERATIONS ----------------------------------------------------------

	public StructBuilder declareLong(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_LONG;
		assert hasArgsCount(q, 0, 1, 2);
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			return add(arrayCreateOp(ClassfileUtils.JavaType.LONG));
		}
		return add(structInitField(q, ClassfileUtils.LocalVariableType.LONG));
	}

	public StructBuilder declareDouble(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_DOUBLE;
		assert hasArgsCount(q, 0, 1, 2);
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			return add(arrayCreateOp(ClassfileUtils.JavaType.DOUBLE));
		}
		return add(structInitField(q, ClassfileUtils.LocalVariableType.DOUBLE));
	}

	public StructBuilder declareString(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_STRING;
		assert hasArgsCount(q, 0, 1, 2);
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			return add(arrayCreateOp(ClassfileUtils.JavaType.STRING));
		}
		return add(structInitField(q, ClassfileUtils.LocalVariableType.STRING));
	}

	public StructBuilder declareBoolean(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_BOOLEAN;
		assert hasArgsCount(q, 0, 1, 2);
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			return add(arrayCreateOp(ClassfileUtils.JavaType.BOOLEAN));
		}
		return add(structInitField(q, ClassfileUtils.LocalVariableType.BOOLEAN));
	}

	public StructBuilder declareArray(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_ARRAY;
		assert hasArgsCount(q, 2);
		if (!ConstantUtils.isIgnoreParam(q.getResult())) {
			arrayName = q.getResult();
		}
		arrayDimensions.push(q.getArgument1());
		return this;
	}

	public StructBuilder declareStruct(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_STRUCT;
		assert hasArgsCount(q, 2);
		final ClassfileUtils.MethodSignature constructor = new ClassfileUtils.MethodSignature(
				"<init>", classfile.getClassname() + "_" + q.getResult(),
				void.class);
		return add(newObjectOperation(constructor, q.getResult()));
	}

}
