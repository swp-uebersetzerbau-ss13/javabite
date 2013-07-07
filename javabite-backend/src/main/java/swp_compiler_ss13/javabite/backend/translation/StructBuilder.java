package swp_compiler_ss13.javabite.backend.translation;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.utils.ClassSignature;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils;
import swp_compiler_ss13.javabite.backend.utils.ConstantUtils;
import swp_compiler_ss13.javabite.backend.utils.MethodSignature;

public class StructBuilder extends AbstractBuilder {

	public StructBuilder(final Classfile classfile, final String methodName) {
		super(classfile, methodName);
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
		assert q.getOperator() == Quadruple.Operator.DECLARE_LONG : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 0, 1, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			add(fieldArrayCreateOp(ClassfileUtils.JavaType.LONG));
		} else {
			add(fieldStructSetFieldOp(q.getArgument1(),
					classfile.getClassname(), q.getResult(),
					ClassfileUtils.LocalVariableType.LONG));
		}
		return this;
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
			add(fieldArrayCreateOp(ClassfileUtils.JavaType.DOUBLE));
		} else {
			add(fieldStructSetFieldOp(q.getArgument1(),
					classfile.getClassname(), q.getResult(),
					ClassfileUtils.LocalVariableType.DOUBLE));
		}
		return this;
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
			add(fieldArrayCreateOp(ClassfileUtils.JavaType.STRING));
		} else {
			add(fieldStructSetFieldOp(q.getArgument1(),
					classfile.getClassname(), q.getResult(),
					ClassfileUtils.LocalVariableType.STRING));
		}
		return this;
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
			add(fieldArrayCreateOp(ClassfileUtils.JavaType.BOOLEAN));
		} else {
			add(fieldStructSetFieldOp(q.getArgument1(),
					classfile.getClassname(), q.getResult(),
					ClassfileUtils.LocalVariableType.BOOLEAN));
		}
		return this;
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
		arrayLengths.add(q.getArgument1());
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

		final MethodSignature constructor;
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			constructor = new MethodSignature("<init>", q.getArgument2(),
					void.class);
			final short classIndex = classfile
					.addClassConstantToConstantPool(constructor.methodClass);
			assert classIndex > 0 : "index is zero";
			final short cstrIndex = classfile
					.addMethodrefConstantToConstantPool(constructor);
			assert cstrIndex > 0 : "index is zero";

			final int arrayDimensions = arrayLengths.size();
			final ClassSignature arrayClass = new ClassSignature(
					arrayDimensions, q.getArgument2());

			add(fieldArrayCreateOp(arrayClass, (byte) 0));
			add(fieldArrayInit(arrayClass, classIndex, cstrIndex,
					arrayDimensions, null, false));

		} else {
			constructor = new MethodSignature("<init>",
					classfile.getClassname() + "_" + q.getResult(), void.class);

			add(newObjectOperation(constructor, q.getResult()));
		}
		return this;
	}

}
