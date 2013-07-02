package swp_compiler_ss13.javabite.backend.translation;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.utils.ByteUtils;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils;

public class StructBuilder extends AbstractBuilder<StructBuilder> {

	public StructBuilder(final Classfile classfile, final String methodName) {
		super(classfile, methodName);
	}

	@Override
	protected StructBuilder add(final Operation operation) {
		super.add(operation);
		return this;
	}

	private Operation structInitField(final Quadruple q,
			final ClassfileUtils.LocalVariableType variableType) {
		final Operation.Builder op = Operation.Builder.newBuilder();
		op.add(Mnemonic.ALOAD_0);
		op.add(loadInstruction(q.getArgument1(), variableType));
		final String structName = classfile.getClassname();
		final String fieldType;
		final boolean isPrimitive = variableType.isPrimitive();
		if (variableType.javaType != null) {
			fieldType = variableType.javaType.className;
		} else {
			fieldType = structName + "_" + q.getArgument2();
		}
		final ClassfileUtils.FieldSignature fieldSignature = new ClassfileUtils.FieldSignature(
				q.getResult(), structName, fieldType, isPrimitive);
		final short fieldIndex = classfile
				.addFieldrefConstantToConstantPool(fieldSignature);
		assert fieldIndex > 0;
		op.add(Mnemonic.PUTFIELD, ByteUtils.shortToByteArray(fieldIndex));
		return op.build();
	}

	// OPERATIONS ----------------------------------------------------------

	public StructBuilder declareLong(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_LONG;
		assert hasArgsCount(q, 1, 2);
		return add(structInitField(q, ClassfileUtils.LocalVariableType.LONG));
	}

	public StructBuilder declareDouble(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_DOUBLE;
		assert hasArgsCount(q, 1, 2);
		return add(structInitField(q, ClassfileUtils.LocalVariableType.DOUBLE));
	}

	public StructBuilder declareString(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_STRING;
		assert hasArgsCount(q, 1, 2);
		return add(structInitField(q, ClassfileUtils.LocalVariableType.STRING));
	}

	public StructBuilder declareBoolean(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_BOOLEAN;
		assert hasArgsCount(q, 1, 2);
		return add(structInitField(q, ClassfileUtils.LocalVariableType.BOOLEAN));
	}

	public StructBuilder declareArray(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_ARRAY;
		assert hasArgsCount(q, 2);
		// TODO implement
		return this;
	}

	public StructBuilder declareStruct(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_STRUCT;
		assert hasArgsCount(q, 2);
		final ClassfileUtils.MethodSignature constructor = new ClassfileUtils.MethodSignature(
				"<init>", classfile.getClassname() + "_" + q.getResult(),
				void.class);
		return add(createOperation(constructor, q.getResult()));
	}

}
