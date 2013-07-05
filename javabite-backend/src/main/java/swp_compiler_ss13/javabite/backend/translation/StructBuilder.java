package swp_compiler_ss13.javabite.backend.translation;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils;
import swp_compiler_ss13.javabite.backend.utils.ConstantUtils;

public class StructBuilder extends AbstractBuilder<StructBuilder> {

	public StructBuilder(final Classfile classfile, final String methodName) {
		super(classfile, methodName);
	}

	// private Operation structInitField(final Quadruple q,
	// final ClassfileUtils.LocalVariableType variableType) {
	//
	// return fieldStructSetFieldOp(q.getArgument1(),
	// classfile.getClassname(), q.getResult(), variableType);
	//
	// final Operation.Builder op = Operation.Builder.newBuilder();
	// op.add(Mnemonic.ALOAD_0);
	// op.add(localLoadInstruction(q.getArgument1(), variableType));
	// final String structName = classfile.getClassname();
	// final String fieldType;
	// if (variableType.javaType != null) {
	// fieldType = variableType.javaType.classSignature
	// .getClassNameAsType();
	// } else {
	// fieldType = structName + "_" + q.getArgument2();
	// }
	// final ClassfileUtils.FieldSignature fieldSignature = new
	// ClassfileUtils.FieldSignature(
	// q.getResult(), structName, fieldType);
	// op.add(fieldStoreInstruction(fieldSignature));
	// return op.build();
	// }

	// OPERATIONS ----------------------------------------------------------

	/**
	 * Initializes a long field in a struct class
	 * 
	 * @param q
	 *            quadruple of operation
	 * @return this builders instance
	 */
	public StructBuilder fieldDeclareLong(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_LONG : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 0, 1, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			return add(fieldArrayCreateOp(ClassfileUtils.JavaType.LONG));
		}
		// return add(structInitField(q,
		// ClassfileUtils.LocalVariableType.LONG));
		return add(fieldStructSetFieldOp(q.getArgument1(),
				classfile.getClassname(), q.getResult(),
				ClassfileUtils.LocalVariableType.LONG));
	}

	/**
	 * Initializes a double field in a struct class
	 * 
	 * @param q
	 *            quadruple of operation
	 * @return this builders instance
	 */
	public StructBuilder fieldDeclareDouble(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_DOUBLE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 0, 1, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			return add(fieldArrayCreateOp(ClassfileUtils.JavaType.DOUBLE));
		}
		// return add(structInitField(q,
		// ClassfileUtils.LocalVariableType.DOUBLE));
		return add(fieldStructSetFieldOp(q.getArgument1(),
				classfile.getClassname(), q.getResult(),
				ClassfileUtils.LocalVariableType.DOUBLE));
	}

	/**
	 * Initializes a string field in a struct class
	 * 
	 * @param q
	 *            quadruple of operation
	 * @return this builders instance
	 */
	public StructBuilder fieldDeclareString(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_STRING : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 0, 1, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			return add(fieldArrayCreateOp(ClassfileUtils.JavaType.STRING));
		}
		// return add(structInitField(q,
		// ClassfileUtils.LocalVariableType.STRING));
		return add(fieldStructSetFieldOp(q.getArgument1(),
				classfile.getClassname(), q.getResult(),
				ClassfileUtils.LocalVariableType.STRING));
	}

	/**
	 * Initializes a boolean field in a struct class
	 * 
	 * @param q
	 *            quadruple of operation
	 * @return this builders instance
	 */
	public StructBuilder fieldDeclareBoolean(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_BOOLEAN : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 0, 1, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			return add(fieldArrayCreateOp(ClassfileUtils.JavaType.BOOLEAN));
		}
		// return add(structInitField(q,
		// ClassfileUtils.LocalVariableType.BOOLEAN));
		return add(fieldStructSetFieldOp(q.getArgument1(),
				classfile.getClassname(), q.getResult(),
				ClassfileUtils.LocalVariableType.BOOLEAN));
	}

	/**
	 * Initializes an array field in a struct class
	 * 
	 * @param q
	 *            quadruple of operation
	 * @return this builders instance
	 */
	public StructBuilder fieldDeclareArray(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_ARRAY : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 1, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		if (!ConstantUtils.isIgnoreParam(q.getResult())) {
			arrayName = q.getResult();
		}
		arrayLengths.push(q.getArgument1());
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
		assert q.getOperator() == Quadruple.Operator.DECLARE_STRUCT : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 1, 2, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);

		final ClassfileUtils.MethodSignature constructor;
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			constructor = new ClassfileUtils.MethodSignature("<init>",
					q.getArgument2(), void.class);
			final short classIndex = classfile
					.addClassConstantToConstantPool(constructor.methodClass);
			assert classIndex > 0 : "index is zero";
			final short cstrIndex = classfile
					.addMethodrefConstantToConstantPool(constructor);
			assert cstrIndex > 0 : "index is zero";

			final int arrayDimensions = arrayLengths.size();
			final ClassfileUtils.ClassSignature arrayClass = new ClassfileUtils.ClassSignature(
					q.getArgument2(), arrayDimensions);

			add(fieldArrayCreateOp(arrayClass, (byte) 0));
			add(fieldArrayInit(arrayClass, classIndex, cstrIndex,
					arrayDimensions, null, false));

		} else
			constructor = new ClassfileUtils.MethodSignature("<init>",
					classfile.getClassname() + "_" + q.getResult(), void.class);

		return add(newObjectOperation(constructor, q.getResult()));
	}

}
