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
		arrayDimensions = new Stack<>();
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
		op.add(localLoadInstruction(q.getArgument1(), variableType));
		final String structName = classfile.getClassname();
		final String fieldType;
		if (variableType.javaType != null) {
			fieldType = variableType.javaType.classSignature
					.getClassNameAsType();
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
	private Operation fieldArrayCreateOp(final ClassfileUtils.JavaType type) {
		assert !ConstantUtils.isIgnoreParam(arrayName)
				&& !arrayDimensions.isEmpty();
		final Operation.Builder op = Operation.Builder.newBuilder();
		final byte dimensions = (byte) arrayDimensions.size();

		final ClassfileUtils.ClassSignature arrayClass = new ClassfileUtils.ClassSignature(
				type.classSignature.getClassNameAsContainer(), dimensions);

		op.add(Mnemonic.ALOAD_0);

		// add all dimensions to stack for array creation
		while (!arrayDimensions.isEmpty()) {
			op.add(localLoadInstruction(arrayDimensions.pop(),
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
					.addClassConstantToConstantPool(type.classSignature);
			op.add(Mnemonic.ANEWARRAY, ByteUtils.shortToByteArray(classIndex));
		}
		final ClassfileUtils.FieldSignature fieldSignature = new ClassfileUtils.FieldSignature(
				arrayName, classfile.getClassname(),
				arrayClass.getClassNameAsType());
		op.add(structPutfieldInstruction(fieldSignature));
		return op.build();
	}

	// OPERATIONS ----------------------------------------------------------

	/**
	 * Initializes a long field in a struct class
	 * 
	 * @param q
	 *            quadruple of operation
	 * @return this builders instance
	 */
	public StructBuilder fieldDeclareLong(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_LONG;
		assert hasArgsCount(q, 0, 1, 2);
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			return add(fieldArrayCreateOp(ClassfileUtils.JavaType.LONG));
		}
		return add(structInitField(q, ClassfileUtils.LocalVariableType.LONG));
	}

	/**
	 * Initializes a double field in a struct class
	 * 
	 * @param q
	 *            quadruple of operation
	 * @return this builders instance
	 */
	public StructBuilder fieldDeclareDouble(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_DOUBLE;
		assert hasArgsCount(q, 0, 1, 2);
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			return add(fieldArrayCreateOp(ClassfileUtils.JavaType.DOUBLE));
		}
		return add(structInitField(q, ClassfileUtils.LocalVariableType.DOUBLE));
	}

	/**
	 * Initializes a string field in a struct class
	 * 
	 * @param q
	 *            quadruple of operation
	 * @return this builders instance
	 */
	public StructBuilder fieldDeclareString(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_STRING;
		assert hasArgsCount(q, 0, 1, 2);
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			return add(fieldArrayCreateOp(ClassfileUtils.JavaType.STRING));
		}
		return add(structInitField(q, ClassfileUtils.LocalVariableType.STRING));
	}

	/**
	 * Initializes a boolean field in a struct class
	 * 
	 * @param q
	 *            quadruple of operation
	 * @return this builders instance
	 */
	public StructBuilder fieldDeclareBoolean(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_BOOLEAN;
		assert hasArgsCount(q, 0, 1, 2);
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			return add(fieldArrayCreateOp(ClassfileUtils.JavaType.BOOLEAN));
		}
		return add(structInitField(q, ClassfileUtils.LocalVariableType.BOOLEAN));
	}

	/**
	 * Initializes an array field in a struct class
	 * 
	 * @param q
	 *            quadruple of operation
	 * @return this builders instance
	 */
	public StructBuilder fieldDeclareArray(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_ARRAY;
		assert hasArgsCount(q, 1, 2);
		if (!ConstantUtils.isIgnoreParam(q.getResult())) {
			arrayName = q.getResult();
		}
		arrayDimensions.push(q.getArgument1());
		return this;
	}

	/**
	 * Initializes a struct field in a struct class
	 * 
	 * @param q
	 *            quadruple of operation
	 * @return this builders instance
	 */
	public StructBuilder fieldDeclareStruct(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_STRUCT;
		assert hasArgsCount(q, 1, 2);

		final ClassfileUtils.MethodSignature constructor;
		if (ConstantUtils.isIgnoreParam(q.getResult()))
			constructor = new ClassfileUtils.MethodSignature("<init>",
					q.getArgument2(), void.class);
		else
			constructor = new ClassfileUtils.MethodSignature("<init>",
					classfile.getClassname() + "_" + q.getResult(), void.class);

		return add(newObjectOperation(constructor, q.getResult()));
	}

}
