package swp_compiler_ss13.javabite.backend.translation;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.utils.*;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static swp_compiler_ss13.javabite.backend.utils.ConstantUtils.isIgnoreParam;

/**
 * <h1>MainBuilder</h1>
 * <p>
 * This class provides a builder pattern implementation for the program class.
 * </p>
 * 
 * @author eike
 * @since May 18, 2013 12:29:51 AM
 */
public class MainBuilder extends AbstractBuilder {

	// gathers all struct name parts when accessing structs and substructs
	private final StringBuilder structNameBuilder;
	// stores struct names for reference variables
	private final Map<String, String> structChains;
	// stores index of local variable where a printstream for printing is stored
	private byte printStreamIndex;

	public MainBuilder(final Classfile classfile, final String methodName) {
		super(classfile, methodName);
		structNameBuilder = new StringBuilder();
		structChains = new HashMap<>();

		if (classfile.isPrintFlag()) {
			createPrintStream();
		}
	}

	private String getCompoundStructName(final Quadruple q) {
		if (classfile.isToplevelStruct(q.getArgument1())) {
			structNameBuilder.setLength(0);
			structNameBuilder.append("_").append(q.getArgument1());
			structChains.put(q.getArgument1(), "_" + q.getArgument1());
		}
		if (q.getOperator() == Quadruple.Operator.STRUCT_GET_REFERENCE) {
			structNameBuilder.append("_").append(q.getArgument2());
			structChains.put(q.getResult(), structNameBuilder.toString());
		} else if (q.getOperator() == Quadruple.Operator.ARRAY_GET_REFERENCE) {
			structChains.put(q.getResult(), structNameBuilder.toString());
		}
		return classfile.getClassname() + structChains.get(q.getArgument1());
	}

	// CLASS SIGNATURES -------------------------------------------------------

	public static final ClassSignature PRINTSTREAM_CLASS = new ClassSignature(
			PrintStream.class);

	// METHOD SIGNATURES ------------------------------------------------------

	/**
	 * @see java.lang.System#exit(int)
	 */
	public static final MethodSignature SYSTEM_EXIT_METHOD = new MethodSignature(
			"exit", System.class, void.class, int.class);

	/**
	 * @see Long#toString()
	 */
	public static final MethodSignature LONG_TOSTRING_METHOD = new MethodSignature(
			"toString", Long.class, String.class, long.class);

	/**
	 * @see Double#toString()
	 */
	public static final MethodSignature DOUBLE_TOSTRING_METHOD = new MethodSignature(
			"toString", Double.class, String.class, double.class);

	/**
	 * @see Boolean#toString()
	 */
	public static final MethodSignature BOOLEAN_TOSTRING_METHOD = new MethodSignature(
			"toString", Boolean.class, String.class, boolean.class);

	/**
	 * @see java.io.PrintStream#print(String)
	 */
	public static final MethodSignature PRINTSTREAM_PRINT_METHOD = new MethodSignature(
			"print", PrintStream.class, void.class, String.class);

	/**
	 * @see java.lang.StringBuilder#append(String)
	 */
	public static final MethodSignature STRINGBUILDER_APPEND_METHOD = new MethodSignature(
			"append", StringBuilder.class, StringBuilder.class, String.class);

	/**
	 * @see java.lang.StringBuilder
	 */
	public static final MethodSignature STRINGBUILDER_NEW_METHOD = new MethodSignature(
			"<init>", StringBuilder.class, void.class);

	/**
	 * @see StringBuilder#toString()
	 */
	public static final MethodSignature STRINGBUILDER_TOSTRING_METHOD = new MethodSignature(
			"toString", StringBuilder.class, String.class);

	/**
	 * @see java.io.PrintStream
	 */
	public static final MethodSignature PRINTSTREAM_NEW_METHOD = new MethodSignature(
			"<init>", PrintStream.class, void.class, OutputStream.class,
			boolean.class, String.class);

	// FIELD SIGNATURES -------------------------------------------------------

	/**
	 * @see java.lang.System#out
	 */
	public static final FieldSignature SYSTEM_OUT_FIELD = new FieldSignature(
			"out", System.class, PrintStream.class);

	// GENERIC OPERATIONS -----------------------------------------------------

	/**
	 * Assigns a number (constant or variable) to a variable. If needed, the
	 * number is converted into another format. This convertion is done by the
	 * passed convert operation, if not null.
	 * 
	 * @param q
	 *            quadruple of operation
	 * @param loadVariableType
	 *            type of variable/constant to load
	 * @param convertOp
	 *            operation to perform for converting the number
	 * @param storeVariableType
	 *            type of variable/constant to store
	 * @return new operation instance
	 */
	private Operation localValueAssignOp(final Quadruple q,
			final ClassfileUtils.LocalVariableType loadVariableType,
			final Mnemonic convertOp,
			final ClassfileUtils.LocalVariableType storeVariableType) {
		final Operation.Builder op = new Operation.Builder();
		op.add(localLoadInstruction(q.getArgument1(), loadVariableType));
		if (convertOp != null) {
			op.add(convertOp);
		}
		op.add(localStoreInstruction(q.getResult(),
				storeVariableType != null ? storeVariableType
						: loadVariableType));
		return op.build();
	}

	/**
	 * loads two numbers, performes a caluclation on them and stores the result
	 * back into a variable.
	 * 
	 * @param q
	 *            quadruple of operation
	 * @param variableType
	 *            type of variable/constant to load and store
	 * @param calcOp
	 *            operation to perform for calculation
	 * @return new operation instance
	 */
	private Operation localNumberCalculateOp(final Quadruple q,
			final ClassfileUtils.LocalVariableType variableType,
			final Mnemonic calcOp) {
		final Operation.Builder op = new Operation.Builder();
		op.add(localLoadInstruction(q.getArgument1(), variableType));
		op.add(localLoadInstruction(q.getArgument2(), variableType));
		op.add(calcOp);
		op.add(localStoreInstruction(q.getResult(), variableType));
		return op.build();
	}

	/**
	 * compares two numbers and stores the result at range [-1,1] on the stack
	 * 
	 * Explanation: Because java bytecode number comparison produces numbers
	 * instead of boolean values, we have to use conditional jumps to assign
	 * boolean values to the desired variable. The default behavoir of this
	 * construct is:
	 * <ol>
	 * <li>compare numbers</li>
	 * <li>jump to false-branch if comparison-result is 0</li>
	 * <li>enter true-branch and load true</li>
	 * <li>jump over false-branch</li>
	 * <li>enter false branch and load false</li>
	 * <li>save result into variable.</li>
	 * </ol>
	 * 
	 * Because this behavior is static, we can use fixed jump offsets.
	 * 
	 * @param q
	 *            the quadruple of the comparation
	 * @param loadVariableType
	 *            type of variable/constant to load
	 * @param compareOp
	 *            the comparation operation to perform
	 * @param jumpOp
	 *            the jump operation to perform after comparation.
	 * @return new operation instance
	 */
	private Operation localNumberCompareOp(final Quadruple q,
			final ClassfileUtils.LocalVariableType loadVariableType,
			final Mnemonic compareOp, final Mnemonic jumpOp) {
		final Operation.Builder op = new Operation.Builder();
		// 1: load first variable/constant
		op.add(localLoadInstruction(q.getArgument1(), loadVariableType));
		// 2: load second variable/constant
		op.add(localLoadInstruction(q.getArgument2(), loadVariableType));
		// 3: execute comparison (pushes -1/0/1 to the stack)
		op.add(compareOp);
		// 4: execute a conditional jump with target line 7
		final Instruction loadFalse = new Instruction(Mnemonic.ICONST_0);
		final JumpInstruction jumpFalse = new JumpInstruction(jumpOp, loadFalse);
		op.add(jumpFalse);
		// 5: load 1 (true) onto the stack
		op.add(Mnemonic.ICONST_1);
		// 6: execute an unconditional jump with target line 8
		final Instruction storeRes = localStoreInstruction(q.getResult(),
				ClassfileUtils.LocalVariableType.BOOLEAN);
		final JumpInstruction jumpStore = new JumpInstruction(Mnemonic.GOTO,
				storeRes);
		op.add(jumpStore);
		// 7: load 0 (false) onto the stack
		op.add(loadFalse);
		// 8: store result 1/0 into variable
		op.add(storeRes);
		return op.build();
	}

	/**
	 * Generic binary boolean operation. TAC supports AND and OR.
	 * 
	 * @param q
	 *            quadruple of operation
	 * @param mnemonic
	 *            mnemonic of binary boolean bytecode operation
	 * @return new operation instance
	 */
	private Operation localBooleanOp(final Quadruple q, final Mnemonic mnemonic) {
		final Operation.Builder op = new Operation.Builder();
		op.add(localLoadInstruction(q.getArgument1(),
				ClassfileUtils.LocalVariableType.BOOLEAN));
		op.add(localLoadInstruction(q.getArgument2(),
				ClassfileUtils.LocalVariableType.BOOLEAN));
		op.add(mnemonic);
		op.add(localStoreInstruction(q.getResult(),
				ClassfileUtils.LocalVariableType.BOOLEAN));
		return op.build();
	}

	/**
	 * Returns the value of an array at a specified index. Because the available
	 * type is by definition LONG, and the internal array index type of java is
	 * INT, every index specification must be transformed into INT, with L2I.
	 * 
	 * @param q
	 *            quadruple of operation
	 * @param variableType
	 *            type of variable/constant to load and store
	 * @return new operation instance
	 */
	private Operation localArrayGetOp(final Quadruple q,
			final ClassfileUtils.LocalVariableType variableType) {
		final Operation.Builder op = new Operation.Builder();
		// load lv array
		op.add(localLoadInstruction(q.getArgument1(),
				ClassfileUtils.LocalVariableType.AREF));
		// load lv array index
		op.add(localLoadInstruction(q.getArgument2(),
				ClassfileUtils.LocalVariableType.LONG));
		// convert array index to int
		op.add(Mnemonic.L2I);
		op.add(variableType.arrayLoadOp);
		op.add(localStoreInstruction(q.getResult(), variableType));
		return op.build();
	}

	/**
	 * Sets a value into an array at the specified index. Because the specified
	 * language does not distinguish between int and long and therefore always
	 * uses long, and java cannot use long as array index, the long value has to
	 * be always converted into int.
	 * 
	 * @param q
	 *            quadruple of operation
	 * @param variableType
	 *            type of variable/constant to load and store
	 * @return new operation instance
	 */
	private Operation localArraySetOp(final Quadruple q,
			final ClassfileUtils.LocalVariableType variableType) {
		final Operation.Builder op = new Operation.Builder();
		// load lv array
		op.add(localLoadInstruction(q.getArgument1(),
				ClassfileUtils.LocalVariableType.AREF));
		// load lv array index
		op.add(localLoadInstruction(q.getArgument2(),
				ClassfileUtils.LocalVariableType.LONG));
		// convert array index to int
		op.add(Mnemonic.L2I);
		op.add(localLoadInstruction(q.getResult(), variableType));
		op.add(variableType.arrayStoreOp);
		return op.build();
	}

	/**
	 * Turns the specified value to a string. This is done by calling the
	 * appropriate toString method of a wrapper class.
	 * 
	 * @param q
	 *            quadruple of operation
	 * @param variableType
	 *            type of variable/constant to load
	 * @return new operation instance
	 */
	private Operation toStringOp(final Quadruple q,
			final MethodSignature toStringSig,
			final ClassfileUtils.LocalVariableType variableType) {
		final Operation.Builder op = new Operation.Builder();
		final short toStringIndex = classfile
				.addMethodrefToConstantPool(toStringSig);
		assert toStringIndex > 0 : "index is zero";
		op.add(localLoadInstruction(q.getArgument1(), variableType));
		op.add(Mnemonic.INVOKESTATIC, ByteUtils.shortToByteArray(toStringIndex));
		op.add(localStoreInstruction(q.getResult(),
				ClassfileUtils.LocalVariableType.STRING));
		return op.build();
	}

	/**
	 * Retrieves a field value from a struct.
	 * 
	 * @param q
	 *            quadruple of operation
	 * @param variableType
	 *            type of variable/constant to store
	 * @return new operation instance
	 */
	private Operation localStructGetFieldOp(final Quadruple q,
			final ClassfileUtils.LocalVariableType variableType) {
		final String structName = getCompoundStructName(q);
		final Operation.Builder op = new Operation.Builder();
		op.add(localLoadInstruction(q.getArgument1(),
				ClassfileUtils.LocalVariableType.AREF));
		final ClassSignature fieldClass;
		if (variableType.javaType != null) {
			// field is primitive or string
			fieldClass = variableType.javaType.classSignature;
		} else if (classfile.isSublevelStruct(structName + "_"
				+ q.getArgument2())) {
			// field is struct
			fieldClass = new ClassSignature(structName + "_" + q.getArgument2());
		} else {
			// field is array
			final String arrayPath = structName + "_" + q.getArgument2();
			final String arrayType = classfile
					.getStructMemberArrayType(arrayPath);
			fieldClass = new ClassSignature(arrayType);
		}
		final FieldSignature fieldSignature = new FieldSignature(
				q.getArgument2(), structName, fieldClass.typeClassName);
		final short fieldIndex = classfile
				.addFieldrefToConstantPool(fieldSignature);
		assert fieldIndex > 0 : "index is zero";
		op.add(Mnemonic.GETFIELD, ByteUtils.shortToByteArray(fieldIndex));
		op.add(localStoreInstruction(q.getResult(), variableType));
		return op.build();
	}

	private byte createPrintStream() {
		if (printStreamIndex == 0) {
			final short psIndex = classfile
					.addClassToConstantPool(PRINTSTREAM_CLASS);
			final short outIndex = classfile
					.addFieldrefToConstantPool(SYSTEM_OUT_FIELD);
			final short charsetIndex = classfile.addStringToConstantPool(
					"UTF-8", false);
			final short initIndex = classfile
					.addMethodrefToConstantPool(PRINTSTREAM_NEW_METHOD);
			printStreamIndex = classfile.addVariableToMethod(methodName, UUID
					.randomUUID().toString().replaceAll("-", ""),
					ClassfileUtils.LocalVariableType.AREF);

			final Operation.Builder op = new Operation.Builder();

			op.add(Mnemonic.NEW, ByteUtils.shortToByteArray(psIndex));
			op.add(Mnemonic.DUP);
			op.add(Mnemonic.GETSTATIC, ByteUtils.shortToByteArray(outIndex));
			op.add(Mnemonic.ICONST_1);
			op.add(localLoadInstruction(charsetIndex, false));
			op.add(Mnemonic.INVOKESPECIAL,
					ByteUtils.shortToByteArray(initIndex));
			op.add(Mnemonic.ASTORE.withIndex(printStreamIndex),
					printStreamIndex);

			add(op.build());
		}
		return printStreamIndex;
	}

	// OPERATIONS ----------------------------------------------------------

	public MainBuilder nop() {
		addNop();
		return this;
	}

	/*
	 * === M1 === FINISHED
	 */

	/**
	 * Declare long. This operation is used in two ways: declaring a long (with
	 * expected arguments) or declaring a long array (without arguments, but
	 * preceeding array declarations). Because usual long declarations are
	 * already filtered out at this point of code generation, this operation
	 * only serves to create arrays.
	 * 
	 * @param q
	 *            quadruple of operation
	 * @return this builders instance
	 */
	public MainBuilder declareLong(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_LONG : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 0, 1, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			add(localArrayCreateOp(ClassfileUtils.JavaType.LONG));
		}

		return this;
	}

	/**
	 * Declare double. This operation is used in two ways: declaring a double
	 * (with expected arguments) or declaring a double array (without arguments,
	 * but preceeding array declarations). Because usual double declarations are
	 * already filtered out at this point of code generation, this operation
	 * only serves to create arrays.
	 * 
	 * @param q
	 *            quadruple of operation
	 * @return this builders instance
	 */
	public MainBuilder declareDouble(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_DOUBLE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 0, 1, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			add(localArrayCreateOp(ClassfileUtils.JavaType.DOUBLE));
			return this;
		}

		return this;
	}

	/**
	 * Declare string. This operation is used in two ways: declaring a string
	 * (with expected arguments) or declaring a string array (without arguments,
	 * but preceeding array declarations). Because usual string declarations are
	 * already filtered out at this point of code generation, this operation
	 * only serves to create arrays.
	 * 
	 * @param q
	 *            quadruple of operation
	 * @return this builders instance
	 */
	public MainBuilder declareString(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_STRING : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 0, 1, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			add(localArrayCreateOp(ClassfileUtils.JavaType.STRING));
			return this;
		}

		return this;
	}

	/**
	 * Declare boolean. This operation is used in two ways: declaring a boolean
	 * (with expected arguments) or declaring a boolean array (without
	 * arguments, but preceeding array declarations). Because usual boolean
	 * declarations are already filtered out at this point of code generation,
	 * this operation only serves to create arrays.
	 * 
	 * @param q
	 *            quadruple of operation
	 * @return this builders instance
	 */
	public MainBuilder declareBoolean(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_BOOLEAN : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 0, 1, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			add(localArrayCreateOp(ClassfileUtils.JavaType.BOOLEAN));
			return this;
		}

		return this;
	}

	/**
	 * TAC Operation: LONG_TO_DOUBLE
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>LONG_TO_DOUBLE</td>
	 * <td>source name</td>
	 * <td></td>
	 * <td>destination name</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder longToDouble(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.LONG_TO_DOUBLE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localValueAssignOp(q, ClassfileUtils.LocalVariableType.LONG,
				Mnemonic.L2D, ClassfileUtils.LocalVariableType.DOUBLE));
		return this;
	}

	/**
	 * TAC Operation: DOUBLE_TO_LONG
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>DOUBLE_TO_LONG</td>
	 * <td>source name</td>
	 * <td></td>
	 * <td>destination name</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder doubleToLong(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DOUBLE_TO_LONG : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localValueAssignOp(q, ClassfileUtils.LocalVariableType.DOUBLE,
				Mnemonic.D2L, ClassfileUtils.LocalVariableType.LONG));
		return this;
	}

	/**
	 * TAC Operation: ASSIGN_LONG
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>ASSIGN_LONG</td>
	 * <td>source</td>
	 * <td></td>
	 * <td>destination name</td>
	 * <td>source may be either an identifier or a Long constant</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder assignLong(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.ASSIGN_LONG : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localValueAssignOp(q, ClassfileUtils.LocalVariableType.LONG, null,
				null));
		return this;
	}

	/**
	 * TAC Operation: ASSIGN_DOUBLE
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>ASSIGN_DOUBLE</td>
	 * <td>source</td>
	 * <td></td>
	 * <td>destination name</td>
	 * <td>source may be either an identifier or a Double constant</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder assignDouble(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.ASSIGN_DOUBLE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localValueAssignOp(q, ClassfileUtils.LocalVariableType.DOUBLE,
				null, null));
		return this;
	}

	/**
	 * TAC Operation: ASSIGN_STRING
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>ASSIGN_STRING</td>
	 * <td>source</td>
	 * <td></td>
	 * <td>destination name</td>
	 * <td>source may be either an identifier or a String constant</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder assignString(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.ASSIGN_STRING : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localValueAssignOp(q, ClassfileUtils.LocalVariableType.STRING,
				null, null));
		return this;
	}

	/**
	 * TAC Operation: ASSIGN_BOOLEAN
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>ASSIGN_BOOLEAN</td>
	 * <td>source</td>
	 * <td></td>
	 * <td>destination name</td>
	 * <td>source may be either an identifier or a Boolean constant</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder assignBoolean(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.ASSIGN_BOOLEAN : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		final Operation.Builder op = new Operation.Builder();
		op.add(localLoadInstruction(q.getArgument1(),
				ClassfileUtils.LocalVariableType.BOOLEAN));
		op.add(localStoreInstruction(q.getResult(),
				ClassfileUtils.LocalVariableType.BOOLEAN));
		add(op.build());
		return this;
	}

	/**
	 * TAC Operation: ADD_LONG
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>ADD_LONG</td>
	 * <td>left hand</td>
	 * <td>right hand</td>
	 * <td>destination</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder addLong(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.ADD_LONG : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCalculateOp(q, ClassfileUtils.LocalVariableType.LONG,
				Mnemonic.LADD));
		return this;
	}

	/**
	 * TAC Operation: ADD_DOUBLE
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>ADD_DOUBLE</td>
	 * <td>left hand</td>
	 * <td>right hand</td>
	 * <td>destination</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder addDouble(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.ADD_DOUBLE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCalculateOp(q, ClassfileUtils.LocalVariableType.DOUBLE,
				Mnemonic.DADD));
		return this;
	}

	/**
	 * TAC Operation: SUB_LONG
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>SUB_LONG</td>
	 * <td>left hand</td>
	 * <td>right hand</td>
	 * <td>destination</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder subLong(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.SUB_LONG : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCalculateOp(q, ClassfileUtils.LocalVariableType.LONG,
				Mnemonic.LSUB));
		return this;
	}

	/**
	 * TAC Operation: SUB_DOUBLE
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>SUB_DOUBLE</td>
	 * <td>left hand</td>
	 * <td>right hand</td>
	 * <td>destination</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder subDouble(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.SUB_DOUBLE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCalculateOp(q, ClassfileUtils.LocalVariableType.DOUBLE,
				Mnemonic.DSUB));
		return this;
	}

	/**
	 * TAC Operation: MUL_LONG
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>MUL_LONG</td>
	 * <td>left hand</td>
	 * <td>right hand</td>
	 * <td>destination</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder mulLong(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.MUL_LONG : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCalculateOp(q, ClassfileUtils.LocalVariableType.LONG,
				Mnemonic.LMUL));
		return this;
	}

	/**
	 * TAC Operation: MUL_DOUBLE
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>MUL_DOUBLE</td>
	 * <td>left hand</td>
	 * <td>right hand</td>
	 * <td>destination</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder mulDouble(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.MUL_DOUBLE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCalculateOp(q, ClassfileUtils.LocalVariableType.DOUBLE,
				Mnemonic.DMUL));
		return this;
	}

	/**
	 * TAC Operation: DIV_LONG
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>DIV_LONG</td>
	 * <td>left hand</td>
	 * <td>right hand</td>
	 * <td>destination</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder divLong(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DIV_LONG : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCalculateOp(q, ClassfileUtils.LocalVariableType.LONG,
				Mnemonic.LDIV));
		return this;
	}

	/**
	 * TAC Operation: DIV_DOUBLE
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>DIV_DOUBLE</td>
	 * <td>left hand</td>
	 * <td>right hand</td>
	 * <td>destination</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder divDouble(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DIV_DOUBLE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCalculateOp(q, ClassfileUtils.LocalVariableType.DOUBLE,
				Mnemonic.DDIV));
		return this;
	}

	/**
	 * TAC Operation: RETURN
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>RETURN</td>
	 * <td>return value</td>
	 * <td></td>
	 * <td></td>
	 * <td>return value may be an identifier or a constant</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder returnLong(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.RETURN : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 1) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		final short systemExitIndex = classfile
				.addMethodrefToConstantPool(SYSTEM_EXIT_METHOD);
		final Operation.Builder op = new Operation.Builder();
		op.add(localLoadInstruction(q.getArgument1(),
				ClassfileUtils.LocalVariableType.LONG));
		op.add(Mnemonic.L2I);
		op.add(Mnemonic.INVOKESTATIC,
				ByteUtils.shortToByteArray(systemExitIndex));
		op.add(Mnemonic.RETURN);
		add(op.build());
		return this;
	}

	/*
	 * === M2 === WORK IN PROGRESS
	 */

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>OR_BOOLEAN</td>
	 * <td>lhs</td>
	 * <td>rhs</td>
	 * <td>destination</td>
	 * <td>destination := lhs or rhs</td>
	 * </tr>
	 * <tr>
	 * <td>AND_BOOLEAN</td>
	 * <td>lhs</td>
	 * <td>rhs</td>
	 * <td>destination</td>
	 * <td>destination := lhs and rhs</td>
	 * </tr>
	 * </tbody>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder notBoolean(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.NOT_BOOLEAN : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		final Operation.Builder op = new Operation.Builder();

		final Instruction falseOp = new Instruction(Mnemonic.ICONST_0);
		final Instruction storeOp = localStoreInstruction(q.getResult(),
				ClassfileUtils.LocalVariableType.BOOLEAN);
		final JumpInstruction jumpFalse = new JumpInstruction(Mnemonic.IFNE,
				falseOp);
		final JumpInstruction jumpTrue = new JumpInstruction(Mnemonic.GOTO,
				storeOp);

		op.add(localLoadInstruction(q.getArgument1(),
				ClassfileUtils.LocalVariableType.BOOLEAN));
		op.add(jumpFalse);
		op.add(Mnemonic.ICONST_1);
		op.add(jumpTrue);
		op.add(falseOp);
		op.add(storeOp);
		add(op.build());
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>OR_BOOLEAN</td>
	 * <td>lhs</td>
	 * <td>rhs</td>
	 * <td>destination</td>
	 * <td>destination := lhs or rhs</td>
	 * </tr>
	 * </tbody>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder orBoolean(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.OR_BOOLEAN : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localBooleanOp(q, Mnemonic.IOR));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>AND_BOOLEAN</td>
	 * <td>lhs</td>
	 * <td>rhs</td>
	 * <td>destination</td>
	 * <td>destination := lhs and rhs</td>
	 * </tr>
	 * </tbody>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder andBoolean(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.AND_BOOLEAN : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localBooleanOp(q, Mnemonic.IAND));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>COMPARE_LONG_E</td>
	 * <td>lhs</td>
	 * <td>rhs</td>
	 * <td>destination</td>
	 * <td>lhs equals rhs ?</td>
	 * </tr>
	 * <tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder compareLongE(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.COMPARE_LONG_E : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCompareOp(q, ClassfileUtils.LocalVariableType.LONG,
				Mnemonic.LCMP, Mnemonic.IFNE));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>COMPARE_LONG_G</td>
	 * <td>lhs</td>
	 * <td>rhs</td>
	 * <td>destination</td>
	 * <td>lhs greater than rhs ?</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder compareLongG(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.COMPARE_LONG_G : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCompareOp(q, ClassfileUtils.LocalVariableType.LONG,
				Mnemonic.LCMP, Mnemonic.IFLE));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>COMPARE_LONG_L</td>
	 * <td>lhs</td>
	 * <td>rhs</td>
	 * <td>destination</td>
	 * <td>lhs less than rhs ?</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder compareLongL(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.COMPARE_LONG_L : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCompareOp(q, ClassfileUtils.LocalVariableType.LONG,
				Mnemonic.LCMP, Mnemonic.IFGE));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <tr>
	 * <td>COMPARE_LONG_GE</td>
	 * <td>lhs</td>
	 * <td>rhs</td>
	 * <td>destination</td>
	 * <td>lhs greater than or equals rhs ?</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder compareLongGE(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.COMPARE_LONG_GE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCompareOp(q, ClassfileUtils.LocalVariableType.LONG,
				Mnemonic.LCMP, Mnemonic.IFLT));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>COMPARE_LONG_LE</td>
	 * <td>lhs</td>
	 * <td>rhs</td>
	 * <td>destination</td>
	 * <td>lhs less than or equals rhs ?</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder compareLongLE(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.COMPARE_LONG_LE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCompareOp(q, ClassfileUtils.LocalVariableType.LONG,
				Mnemonic.LCMP, Mnemonic.IFGT));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>COMPARE_DOUBLE_E</td>
	 * <td>lhs</td>
	 * <td>rhs</td>
	 * <td>destination</td>
	 * <td>lhs equals rhs ?</td>
	 * </tr>
	 * <tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder compareDoubleE(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.COMPARE_DOUBLE_E : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCompareOp(q, ClassfileUtils.LocalVariableType.DOUBLE,
				Mnemonic.DCMPL, Mnemonic.IFNE));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>COMPARE_DOUBLE_G</td>
	 * <td>lhs</td>
	 * <td>rhs</td>
	 * <td>destination</td>
	 * <td>lhs greater than rhs ?</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder compareDoubleG(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.COMPARE_DOUBLE_G : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCompareOp(q, ClassfileUtils.LocalVariableType.DOUBLE,
				Mnemonic.DCMPL, Mnemonic.IFLE));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>COMPARE_DOUBLE_L</td>
	 * <td>lhs</td>
	 * <td>rhs</td>
	 * <td>destination</td>
	 * <td>lhs less than rhs ?</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder compareDoubleL(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.COMPARE_DOUBLE_L : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCompareOp(q, ClassfileUtils.LocalVariableType.DOUBLE,
				Mnemonic.DCMPG, Mnemonic.IFGE));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>COMPARE_DOUBLE_GE</td>
	 * <td>lhs</td>
	 * <td>rhs</td>
	 * <td>destination</td>
	 * <td>lhs greater than or equals rhs ?</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder compareDoubleGE(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.COMPARE_DOUBLE_GE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCompareOp(q, ClassfileUtils.LocalVariableType.DOUBLE,
				Mnemonic.DCMPL, Mnemonic.IFLT));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>COMPARE_DOUBLE_LE</td>
	 * <td>lhs</td>
	 * <td>rhs</td>
	 * <td>destination</td>
	 * <td>lhs less than or equals rhs ?</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder compareDoubleLE(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.COMPARE_DOUBLE_LE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localNumberCompareOp(q, ClassfileUtils.LocalVariableType.DOUBLE,
				Mnemonic.DCMPG, Mnemonic.IFGT));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>LABEL</td>
	 * <td>unique identifier</td>
	 * <td></td>
	 * <td></td>
	 * <td>is a NOP</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder label(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.LABEL : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 1) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		addLabel(q.getArgument1());
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>BRANCH</td>
	 * <td>target</td>
	 * <td></td>
	 * <td></td>
	 * <td>unconditional branch</td>
	 * </tr>
	 * <tr>
	 * <td>BRANCH</td>
	 * <td>true target</td>
	 * <td>false target</td>
	 * <td>condition</td>
	 * <td>conditional branch, condition is a boolean variable</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder branch(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.BRANCH : "quadruple has wrong operator: "
				+ q.getOperator();
		final Operation.Builder op = new Operation.Builder();
		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			// unconditional branch
			assert ConstantUtils.hasArgsCount(q, 1) : "quadruple has wrong args count: "
					+ ConstantUtils.getArgsCount(q);
			final JumpInstruction jumpOp = new JumpInstruction(Mnemonic.GOTO,
					q.getArgument1());
			op.add(jumpOp);
		} else {
			// conditional branch
			assert ConstantUtils.hasArgsCount(q, 2, 3) : "quadruple has wrong args count: "
					+ ConstantUtils.getArgsCount(q);
			op.add(localLoadInstruction(q.getResult(),
					ClassfileUtils.LocalVariableType.BOOLEAN));
			if (!isIgnoreParam(q.getArgument1())) {
				final JumpInstruction trueJump = new JumpInstruction(
						Mnemonic.IFNE, q.getArgument1());
				op.add(trueJump);
			}
			if (!isIgnoreParam(q.getArgument2())) {
				final JumpInstruction falseJump = new JumpInstruction(
						Mnemonic.GOTO, q.getArgument2());
				op.add(falseJump);
			}
		}
		add(op.build());
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>PRINT_STRING</td>
	 * <td>value</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder printString(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.PRINT_STRING : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 1) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		assert printStreamIndex != 0 : "print stream index is zero";
		final Operation.Builder op = new Operation.Builder();

		// add print methodref info to constant pool, if necessary
		final short printIndex = classfile
				.addMethodrefToConstantPool(PRINTSTREAM_PRINT_METHOD);

		op.add(Mnemonic.ALOAD.withIndex(printStreamIndex), printStreamIndex);
		op.add(localLoadInstruction(q.getArgument1(),
				ClassfileUtils.LocalVariableType.STRING));
		op.add(Mnemonic.INVOKEVIRTUAL, ByteUtils.shortToByteArray(printIndex));
		add(op.build());
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>DECLARE_ARRAY</td>
	 * <td>size</td>
	 * <td></td>
	 * <td>name</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder declareArray(final Quadruple q) {
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
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>ARRAY_GET_LONG</td>
	 * <td>array name or reference</td>
	 * <td>element index</td>
	 * <td>destination name</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder arrayGetLong(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.ARRAY_GET_LONG : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localArrayGetOp(q, ClassfileUtils.LocalVariableType.LONG));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>ARRAY_GET_DOUBLE</td>
	 * <td>array name or reference</td>
	 * <td>element index</td>
	 * <td>destination name</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder arrayGetDouble(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.ARRAY_GET_DOUBLE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localArrayGetOp(q, ClassfileUtils.LocalVariableType.DOUBLE));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>ARRAY_GET_BOOLEAN</td>
	 * <td>array name or reference</td>
	 * <td>element index</td>
	 * <td>destination name</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder arrayGetBoolean(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.ARRAY_GET_BOOLEAN : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localArrayGetOp(q, ClassfileUtils.LocalVariableType.BOOLEAN));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>ARRAY_GET_STRING</td>
	 * <td>array name or reference</td>
	 * <td>element index</td>
	 * <td>destination name</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder arrayGetString(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.ARRAY_GET_STRING : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localArrayGetOp(q, ClassfileUtils.LocalVariableType.STRING));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>ARRAY_GET_REFERENCE</td>
	 * <td>array name or reference</td>
	 * <td>element index</td>
	 * <td>destination name</td>
	 * <td>destination will contain a reference to the array at the index</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder arrayGetReference(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.ARRAY_GET_REFERENCE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		getCompoundStructName(q);
		add(localArrayGetOp(q, ClassfileUtils.LocalVariableType.AREF));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>ARRAY_SET_LONG</td>
	 * <td>array name or reference</td>
	 * <td>element index</td>
	 * <td>source</td>
	 * <td>source may be either an identifier or a Long constant</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder arraySetLong(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.ARRAY_SET_LONG : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localArraySetOp(q, ClassfileUtils.LocalVariableType.LONG));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>ARRAY_SET_DOUBLE</td>
	 * <td>array name or reference</td>
	 * <td>element index</td>
	 * <td>source</td>
	 * <td>source may be either an identifier or a Double constant</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder arraySetDouble(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.ARRAY_SET_DOUBLE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localArraySetOp(q, ClassfileUtils.LocalVariableType.DOUBLE));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>ARRAY_SET_BOOLEAN</td>
	 * <td>array name or reference</td>
	 * <td>element index</td>
	 * <td>source</td>
	 * <td>source may be either an identifier or a Boolean constant</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder arraySetBoolean(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.ARRAY_SET_BOOLEAN : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localArraySetOp(q, ClassfileUtils.LocalVariableType.BOOLEAN));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>ARRAY_SET_STRING</td>
	 * <td>array name or reference</td>
	 * <td>element index</td>
	 * <td>source</td>
	 * <td>source may be either an identifier or a String constant</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder arraySetString(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.ARRAY_SET_STRING : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localArraySetOp(q, ClassfileUtils.LocalVariableType.STRING));
		return this;
	}

	/*
	 * === M3 === FINISHED
	 */

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>BOOLEAN_TO_STRING</td>
	 * <td>source name</td>
	 * <td></td>
	 * <td>destination name</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder booleanToString(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.BOOLEAN_TO_STRING : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(toStringOp(q, BOOLEAN_TOSTRING_METHOD,
				ClassfileUtils.LocalVariableType.BOOLEAN));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>LONG_TO_STRING</td>
	 * <td>source name</td>
	 * <td></td>
	 * <td>destination name</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder longToString(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.LONG_TO_STRING : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(toStringOp(q, LONG_TOSTRING_METHOD,
				ClassfileUtils.LocalVariableType.LONG));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>DOUBLE_TO_STRING</td>
	 * <td>source name</td>
	 * <td></td>
	 * <td>destination name</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder doubleToString(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DOUBLE_TO_STRING : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 2) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(toStringOp(q, DOUBLE_TOSTRING_METHOD,
				ClassfileUtils.LocalVariableType.DOUBLE));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>DECLARE_STRUCT</td>
	 * <td>size</td>
	 * <td></td>
	 * <td>name</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder declareStruct(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.DECLARE_STRUCT : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 1, 2, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);

		// create the signature of the default constructor
		final MethodSignature constructor = new MethodSignature.Builder(
				"<init>").ofClass(q.getArgument2()).build();
		final short classIndex = classfile
				.addClassToConstantPool(constructor.methodClass);
		assert classIndex > 0 : "index is zero";
		final short cstrIndex = classfile
				.addMethodrefToConstantPool(constructor);
		assert cstrIndex > 0 : "index is zero";

		if (ConstantUtils.isIgnoreParam(q.getResult())) {
			final int arrayDimensions = arrayLengths.size();
			final ClassSignature arrayClass = new ClassSignature(
					arrayDimensions, q.getArgument2());
			add(localArrayCreateOp(arrayClass, (byte) 0));

			add(localArrayInit(classIndex, cstrIndex, arrayDimensions, null,
					false));
			return this;
		}
		// instantiate a new object with classname from arg2, the
		// constructor signature, and store the new instance in result

		final Operation.Builder op = new Operation.Builder();
		assert cstrIndex > 0 : "index is zero";
		op.add(Mnemonic.NEW, ByteUtils.shortToByteArray(classIndex));
		op.add(Mnemonic.DUP);
		op.add(Mnemonic.INVOKESPECIAL, ByteUtils.shortToByteArray(cstrIndex));
		op.add(localStoreInstruction(q.getResult(),
				ClassfileUtils.LocalVariableType.AREF));
		add(op.build());
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>STRUCT_GET_LONG</td>
	 * <td>struct name or reference</td>
	 * <td>member name</td>
	 * <td>destination name</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder structGetLong(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.STRUCT_GET_LONG : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localStructGetFieldOp(q, ClassfileUtils.LocalVariableType.LONG));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>STRUCT_GET_DOUBLE</td>
	 * <td>struct name or reference</td>
	 * <td>member name</td>
	 * <td>destination name</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder structGetDouble(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.STRUCT_GET_DOUBLE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localStructGetFieldOp(q, ClassfileUtils.LocalVariableType.DOUBLE));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>STRUCT_GET_BOOLEAN</td>
	 * <td>struct name or reference</td>
	 * <td>member name</td>
	 * <td>destination name</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder structGetBoolean(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.STRUCT_GET_BOOLEAN : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localStructGetFieldOp(q, ClassfileUtils.LocalVariableType.BOOLEAN));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>STRUCT_GET_STRING</td>
	 * <td>struct name or reference</td>
	 * <td>member name</td>
	 * <td>destination name</td>
	 * <td></td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder structGetString(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.STRUCT_GET_STRING : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localStructGetFieldOp(q, ClassfileUtils.LocalVariableType.STRING));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>STRUCT_GET_REFERENCE</td>
	 * <td>struct name or reference</td>
	 * <td>member name</td>
	 * <td>destination name</td>
	 * <td>destination will contain a reference to the array or struct that is
	 * the member</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder structGetReference(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.STRUCT_GET_REFERENCE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localStructGetFieldOp(q, ClassfileUtils.LocalVariableType.AREF));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>STRUCT_SET_LONG</td>
	 * <td>struct name or reference</td>
	 * <td>member name</td>
	 * <td>source</td>
	 * <td>source must be either an identifier or a Long constant</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder structSetLong(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.STRUCT_SET_LONG : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localStructSetFieldOp(q.getResult(), getCompoundStructName(q),
				q.getArgument1(), q.getArgument2(),
				ClassfileUtils.LocalVariableType.LONG));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>STRUCT_SET_DOUBLE</td>
	 * <td>struct name or reference</td>
	 * <td>member name</td>
	 * <td>source</td>
	 * <td>source must be either an identifier or a Double constant</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder structSetDouble(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.STRUCT_SET_DOUBLE : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localStructSetFieldOp(q.getResult(), getCompoundStructName(q),
				q.getArgument1(), q.getArgument2(),
				ClassfileUtils.LocalVariableType.DOUBLE));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>STRUCT_SET_BOOLEAN</td>
	 * <td>struct name or reference</td>
	 * <td>member name</td>
	 * <td>source</td>
	 * <td>source must be either an identifier or a Boolean constant</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder structSetBoolean(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.STRUCT_SET_BOOLEAN : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localStructSetFieldOp(q.getResult(), getCompoundStructName(q),
				q.getArgument1(), q.getArgument2(),
				ClassfileUtils.LocalVariableType.BOOLEAN));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>STRUCT_SET_STRING</td>
	 * <td>struct name or reference</td>
	 * <td>member name</td>
	 * <td>source</td>
	 * <td>source must be either an identifier or a String constant</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder structSetString(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.STRUCT_SET_STRING : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		add(localStructSetFieldOp(q.getResult(), getCompoundStructName(q),
				q.getArgument1(), q.getArgument2(),
				ClassfileUtils.LocalVariableType.STRING));
		return this;
	}

	/**
	 * <table>
	 * <thead>
	 * <tr>
	 * <th>Operator</th>
	 * <th>Argument 1</th>
	 * <th>Argument 2</th>
	 * <th>Result</th>
	 * <th>Remarks</th>
	 * </tr>
	 * </thead> <tbody>
	 * <tr>
	 * <td>CONCAT_STRING</td>
	 * <td>lhs</td>
	 * <td>rhs</td>
	 * <td>destination</td>
	 * <td>destination := lhs concatenated with rhs</td>
	 * </tr>
	 * </tbody>
	 * </table>
	 * 
	 * @param q
	 *            the operation quadruple
	 * @return this program builders instance
	 */
	public MainBuilder concatString(final Quadruple q) {
		assert q.getOperator() == Quadruple.Operator.CONCAT_STRING : "quadruple has wrong operator: "
				+ q.getOperator();
		assert ConstantUtils.hasArgsCount(q, 3) : "quadruple has wrong args count: "
				+ ConstantUtils.getArgsCount(q);
		final Operation.Builder op = new Operation.Builder();
		final short appendMethod = classfile
				.addMethodrefToConstantPool(STRINGBUILDER_APPEND_METHOD);
		final short stringBuilderToString = classfile
				.addMethodrefToConstantPool(STRINGBUILDER_TOSTRING_METHOD);

		op.add(fieldNewObjectOperation(STRINGBUILDER_NEW_METHOD, null));

		if (!isIgnoreParam(q.getArgument1())) {
			op.add(localLoadInstruction(q.getArgument1(),
					ClassfileUtils.LocalVariableType.STRING));
			op.add(Mnemonic.INVOKEVIRTUAL, 1,
					ByteUtils.shortToByteArray(appendMethod));
		}

		if (!isIgnoreParam(q.getArgument2())) {
			op.add(localLoadInstruction(q.getArgument2(),
					ClassfileUtils.LocalVariableType.STRING));
			op.add(Mnemonic.INVOKEVIRTUAL, 1,
					ByteUtils.shortToByteArray(appendMethod));
		}

		if (!isIgnoreParam(q.getResult())) {
			op.add(Mnemonic.INVOKEVIRTUAL, 1,
					ByteUtils.shortToByteArray(stringBuilderToString));
			op.add(localStoreInstruction(q.getResult(),
					ClassfileUtils.LocalVariableType.STRING));
		}
		add(op.build());
		return this;
	}

}
