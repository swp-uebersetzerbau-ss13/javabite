package swp_compiler_ss13.javabite.backend;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.Operation.OperationBuilder;
import swp_compiler_ss13.javabite.backend.classfile.IClassfile;
import swp_compiler_ss13.javabite.backend.classfile.IClassfile.ArrayType;
import swp_compiler_ss13.javabite.backend.classfile.IClassfile.InfoTag;
import swp_compiler_ss13.javabite.backend.translation.Translator;
import swp_compiler_ss13.javabite.backend.utils.ByteUtils;

/**
 * <h1>Program</h1>
 * <p>
 * Representation of a program instruction block. Contains a list of operations
 * which consist of instructions.
 * </p>
 * 
 * @author eike
 * @since 02.05.2013 23:41:39
 * 
 */
public class Program {

	/**
	 * <h1>ProgramBuilder</h1>
	 * <p>
	 * This class provides a builder pattern implementation for the program
	 * class.
	 * </p>
	 * 
	 * @author eike
	 * @since May 18, 2013 12:29:51 AM
	 */
	public static class ProgramBuilder {

		private static final Pattern P_CONST_SIGN = Pattern.compile("#.*?");

		public static ProgramBuilder newBuilder(final int initialOffset,
				final IClassfile classfile, final String methodName) {
			return new ProgramBuilder(initialOffset, classfile, methodName);
		}

		public static ProgramBuilder newBuilder(final IClassfile classfile,
				final String methodName) {
			return newBuilder(0, classfile, methodName);
		}

		private final List<Operation> operations;
		private final IClassfile classfile;
		private final String methodName;

		private Map<String, Instruction> jumpTargets;

		// determines, whether the System exit method has already been added/
		// a return statement is present in the tac
		private boolean returnFlag;
		// index of methodref info of system exit method in constant pool
		private short systemExitIndex;
		// index of fieldref info of system out in constant pool
		private short systemOutIndex;
		// index of methodref info of print method in constant pool
		private short printIndex;
		// indicates last instruction was a label, denoting a jump location
		private boolean labelFlag;
		// label name of last label
		private String labelName;
		// name of last array seen
		private String arrayName;
		// sizes of last array declaration, in reverse order on the stack
		private final Stack<String> arraySizes = new Stack<>();

		private ProgramBuilder(final int initialOffset,
				final IClassfile classfile, final String methodName) {
			operations = new ArrayList<>();
			this.classfile = classfile;
			this.methodName = methodName;
			returnFlag = false;
			systemExitIndex = 0;
			systemOutIndex = 0;
			printIndex = 0;
		}

		private ProgramBuilder add(final Operation operation) {
			operations.add(operation);
			if (labelFlag) {
				if (jumpTargets == null)
					jumpTargets = new HashMap<>();
				labelFlag = false;
				jumpTargets.put(labelName, operation.getInstruction(0));
			}
			return this;
		}

		/**
		 * Builds the current program instance and returns it.
		 * 
		 * @return the program instance built by this builder instance
		 */
		public Program build() {
			// check, whether there is a return instruction in the end
			// if not, set it
			if (!returnFlag) {
				final OperationBuilder op = OperationBuilder.newBuilder();
				op.add(Mnemonic.RETURN);
				operations.add(op.build());
			}
			return new Program(operations);
		}

		private boolean isConstant(final String s) {
			return s.startsWith(Translator.SYM_CONST);
		}

		private String removeConstantSign(final String s) {
			final Matcher m = P_CONST_SIGN.matcher(s);
			return m.replaceAll("");
		}

		private Instruction getJumpTarget(final String s) {
			if (jumpTargets == null)
				return null;
			return jumpTargets.get(s);
		}

		// INSTRUCTION CREATORS ------------------------------------------------

		/**
		 * TODO javadoc
		 * 
		 * @param wide
		 * @param arg1
		 * @param constType
		 * @param varLoadOp
		 */
		private Instruction loadOp(final boolean wide, final String arg1,
				final InfoTag constType, final String varLoadOp) {
			if (isConstant(arg1)) {
				final short index = classfile.getIndexOfConstantInConstantPool(
						constType, removeConstantSign(arg1));
				assert index > 0;
				if (wide) {
					// return new Instruction(3, Mnemonic.LDC2_W,
					// ByteUtils.shortToByteArray(index));
					return new Instruction(Mnemonic.LDC2_W,
							ByteUtils.shortToByteArray(index));
				} else if (index >= 256) {
					// return new Instruction(1, Mnemonic.LDC_W,
					// ByteUtils.shortToByteArray(index));
					return new Instruction(Mnemonic.LDC_W,
							ByteUtils.shortToByteArray(index));
				} else {
					// return new Instruction(1, Mnemonic.LDC, (byte) index);
					return new Instruction(Mnemonic.LDC, (byte) index);
				}
			} else {
				final byte index = classfile.getIndexOfVariableInMethod(
						methodName, arg1);
				assert index > 0;
				// return new Instruction(1, Mnemonic.getMnemonic(varLoadOp,
				// index), index);
				return new Instruction(Mnemonic.getMnemonic(varLoadOp, index),
						index);
			}
		}

		/**
		 * TODO javadoc
		 * 
		 * @param arg1
		 * @return
		 */
		private Instruction loadBooleanOp(final String arg1) {
			if (isConstant(arg1)) {
				final short value = (short) (Translator.CONST_TRUE.equals(arg1
						.toUpperCase()) ? 1 : 0);
				// return new Instruction(1, Mnemonic.ICONST(value),
				// ByteUtils.shortToByteArray(value));
				return new Instruction(Mnemonic.ICONST(value),
						ByteUtils.shortToByteArray(value));
			} else {
				final byte index = classfile.getIndexOfVariableInMethod(
						methodName, arg1);
				// return new Instruction(1, Mnemonic.ILOAD(index), index);
				return new Instruction(Mnemonic.ILOAD(index), index);
			}
		}

		/**
		 * TODO javadoc
		 * 
		 * @param result
		 * @param storeOp
		 * @return
		 */
		private Instruction storeOp(final String result, final String storeOp) {
			final byte index = classfile.getIndexOfVariableInMethod(methodName,
					result);
			// return new Instruction(1, Mnemonic.getMnemonic(storeOp, index),
			// index);
			return new Instruction(Mnemonic.getMnemonic(storeOp, index), index);
		}

		// GENERIC OPERATIONS --------------------------------------------------

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 * @param constType
		 * @param loadOp
		 * @param convertOp
		 * @param storeOp
		 * @return
		 */
		private ProgramBuilder assignNumber(final Quadruple q,
				final InfoTag constType, final String loadOp,
				final Mnemonic convertOp, final String storeOp) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			op.add(loadOp(true, q.getArgument1(), constType, loadOp));
			if (convertOp != null) {
				op.add(convertOp);
			}
			op.add(storeOp(q.getResult(), storeOp));
			return add(op.build());
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 * @param constType
		 * @param loadOp
		 * @param calcOp
		 * @param storeOp
		 * @return
		 */
		private ProgramBuilder calculateNumber(final Quadruple q,
				final InfoTag constType, final String loadOp,
				final Mnemonic calcOp, final String storeOp) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			op.add(loadOp(true, q.getArgument1(), constType, loadOp));
			op.add(loadOp(true, q.getArgument2(), constType, loadOp));
			op.add(calcOp);
			op.add(storeOp(q.getResult(), storeOp));
			return add(op.build());
		}

		/**
		 * compares two numbers and stores the result at range [-1,1] on the
		 * stack
		 * 
		 * @param q
		 *            the quadruple of the comparation
		 * @param type
		 *            the data type of the numbers to compare. Long or Double
		 * @param loadOp
		 *            the load operation to perform for loading the numbers
		 * @param compareOp
		 *            the comparation operation to perform
		 * @param jumpOp
		 *            the jump operation to perform after comparation. TODO more
		 *            explanation
		 * @return this builder instance
		 */
		private ProgramBuilder compareNumber(final Quadruple q,
				final InfoTag type, final String loadOp,
				final Mnemonic compareOp, final String jumpOp) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			// final Instruction iCmpFalse = new Instruction(1,
			// Mnemonic.ICONST_0);
			final Instruction iCmpFalse = new Instruction(Mnemonic.ICONST_0);
			final Instruction iStore = storeOp(q.getResult(), "ISTORE");

			op.add(loadOp(true, q.getArgument1(), type, loadOp));
			op.add(loadOp(true, q.getArgument2(), type, loadOp));
			op.add(compareOp);
			//op.add(new JumpInstruction(3, Mnemonic.getMnemonic(jumpOp), iCmpFalse));
			op.add(new JumpInstruction(Mnemonic.getMnemonic(jumpOp), iCmpFalse));
			op.add(Mnemonic.ICONST_1);
			//op.add(new JumpInstruction(3, Mnemonic.GOTO, iStore));
			op.add(new JumpInstruction(Mnemonic.GOTO, iStore));
			op.add(iCmpFalse);
			op.add(iStore);
			return add(op.build());
		}

		/**
		 * Generic binary boolean operation. TAC supports AND and OR.
		 * 
		 * @param q
		 *            quadruple of operation
		 * @param mnemonic
		 *            mnemonic of binary boolean bytecode operation
		 * @return this builder instance
		 */
		private ProgramBuilder booleanOp(final Quadruple q,
				final Mnemonic mnemonic) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			op.add(loadBooleanOp(q.getArgument1()));
			if (!"!".equals(q.getArgument2())) {
				op.add(loadBooleanOp(q.getArgument2()));
			}
			op.add(mnemonic);
			op.add(storeOp(q.getResult(), "ISTORE"));
			return add(op.build());
		}

		/**
		 * Operation to print a constant value or the content of a variable of
		 * the types string, long or double. Loads the System.out object and
		 * calls the virtual method println
		 * 
		 * @param arg1
		 *            constant or variable name to be printed
		 * @param type
		 *            type of arg1
		 * @param varLoadOp
		 *            operation to use for loading arg1 as a variable
		 * @return this builder instance
		 */
		private ProgramBuilder print(final String arg1, final InfoTag type,
				final String varLoadOp) {
			addPrintMethodToClassfile();
			final OperationBuilder op = OperationBuilder.newBuilder();
			op.add(Mnemonic.GETSTATIC, 2,
					ByteUtils.shortToByteArray(systemOutIndex));
			op.add(loadOp(type.isWide(), arg1, type, varLoadOp));
			op.add(Mnemonic.INVOKEVIRTUAL, 2,
					ByteUtils.shortToByteArray(printIndex));
			return add(op.build());
		}

		/**
		 * Creates an array of a primitive datatype. TODO javadoc
		 * 
		 * @param type
		 * @return
		 */
		private ProgramBuilder arrayCreate(final ArrayType type) {
			// long array declaration
			assert arrayName != null && !arraySizes.isEmpty();
			final OperationBuilder op = OperationBuilder.newBuilder();
			for (final String size : arraySizes) {
				op.add(loadOp(true, size, InfoTag.LONG, "LLOAD"));
				op.add(Mnemonic.L2I);
			}
			op.add(Mnemonic.NEWARRAY, 1, type.getValue());
			op.add(storeOp(arrayName, "ASTORE"));
			return add(op.build());
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 * @param loadOp
		 * @param storeOp
		 * @return
		 */
		private ProgramBuilder arrayGet(final Quadruple q,
				final Mnemonic loadOp, final String storeOp) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			final byte arrayIndex = classfile.getIndexOfVariableInMethod(
					methodName, q.getArgument1());
			op.add(Mnemonic.getMnemonic("ALOAD", arrayIndex), 1, arrayIndex);
			op.add(loadOp(true, q.getArgument2(), InfoTag.LONG, "LLOAD"));
			op.add(Mnemonic.L2I);
			op.add(loadOp);
			op.add(storeOp(q.getResult(), storeOp));
			return add(op.build());
		}

		private ProgramBuilder arraySet(final Quadruple q, final boolean wide,
				final InfoTag constType, final String varLoadOp,
				final Mnemonic storeOp) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			final byte arrayIndex = classfile.getIndexOfVariableInMethod(
					methodName, q.getArgument1());
			op.add(Mnemonic.getMnemonic("ALOAD", arrayIndex), 1, arrayIndex);
			op.add(loadOp(true, q.getArgument2(), InfoTag.LONG, "LLOAD"));
			op.add(Mnemonic.L2I);
			op.add(loadOp(wide, q.getResult(), constType, varLoadOp));
			op.add(storeOp);
			return add(op.build());
		}

		// CONSTANT POOL HELPERS -----------------------------------------------

		/**
		 * <h1>addSystemExitMethodToClassfile</h1>
		 * <p>
		 * This method checks whether the return flag is already set and if not,
		 * it'll add the needed system exit data to the classfile's constant
		 * pool.
		 * </p>
		 * 
		 * @author Marco
		 * @since 13.05.2013
		 * 
		 * @return short index into the constant pool of the system exit's
		 *         methodref entry.
		 */
		private short addSystemExitMethodToClassfile() {
			if (!returnFlag) {
				returnFlag = true;
				// TODO externalize static strings
				systemExitIndex = classfile.addMethodrefConstantToConstantPool(
						"exit", "(I)V", "java/lang/System");
			}
			return systemExitIndex;
		}

		/**
		 * <h1>addPrintMethodToClassfile</h1>
		 * <p>
		 * This method checks whether the print index is already set and if not,
		 * it'll add the needed print data to the classfile's constant pool.
		 * </p>
		 * 
		 * @author Marco
		 * @since 30.05.2013
		 */
		private void addPrintMethodToClassfile() {

			// add system.out fieldref info to constant pool, if necessary
			if (systemOutIndex == 0) {
				// TODO externalize static strings
				systemOutIndex = classfile.addFieldrefConstantToConstantPool(
						"out", "Ljava/io/PrintStream;", "java/lang/System");
			}

			// add print methodref info to constant pool, if necessary
			if (printIndex == 0) {
				// TODO externalize static strings
				printIndex = classfile
						.addMethodrefConstantToConstantPool("print",
								"(Ljava/lang/String;)V", "java/io/PrintStream");
			}
		}

		/**
		 * TODO javadoc
		 * 
		 * @return
		 */
		private short addStringClassToClassfile() {
			return classfile.addClassConstantToConstantPool("java/lang/String");
		}

		// OPERATIONS ----------------------------------------------------------

		/*
		 * === M1 === FINISHED
		 */

		public ProgramBuilder declareLong(final Quadruple q) {
			if (q.getResult() == null) {
				return arrayCreate(ArrayType.LONG);
			}

			return this;
		}

		public ProgramBuilder declareDouble(final Quadruple q) {
			if (q.getResult() == null) {
				return arrayCreate(ArrayType.DOUBLE);
			}

			return this;
		}

		public ProgramBuilder declareString(final Quadruple q) {
			if (q.getResult() == null) {
				assert arrayName != null && !arraySizes.isEmpty();
				final OperationBuilder op = OperationBuilder.newBuilder();
				for (final String size : arraySizes) {
					op.add(loadOp(true, size, InfoTag.LONG, "LLOAD"));
					op.add(Mnemonic.L2I);
				}
				op.add(Mnemonic.ANEWARRAY, 2,
						ByteUtils.shortToByteArray(addStringClassToClassfile()));
				op.add(storeOp(arrayName, "ASTORE"));
				return add(op.build());
			}

			return this;
		}

		public ProgramBuilder declareBoolean(final Quadruple q) {
			if (q.getResult() == null) {
				return arrayCreate(ArrayType.BOOLEAN);
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
		public ProgramBuilder longToDouble(final Quadruple q) {
			return assignNumber(q, InfoTag.LONG, "LLOAD", Mnemonic.L2D,
					"DSTORE");
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
		public ProgramBuilder doubleToLong(final Quadruple q) {
			return assignNumber(q, InfoTag.DOUBLE, "DLOAD", Mnemonic.D2L,
					"LSTORE");
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
		public ProgramBuilder assignLong(final Quadruple q) {
			return assignNumber(q, InfoTag.LONG, "LLOAD", null, "LSTORE");
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
		public ProgramBuilder assignDouble(final Quadruple q) {
			return assignNumber(q, InfoTag.DOUBLE, "DLOAD", null, "DSTORE");
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
		public ProgramBuilder assignString(final Quadruple q) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			op.add(loadOp(false, q.getArgument1(), InfoTag.STRING, "ALOAD"));
			op.add(storeOp(q.getResult(), "ASTORE"));
			return add(op.build());
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
		public ProgramBuilder assignBoolean(final Quadruple q) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			op.add(loadBooleanOp(q.getArgument1()));
			op.add(storeOp(q.getResult(), "ISTORE"));
			return add(op.build());
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
		public ProgramBuilder addLong(final Quadruple q) {
			return calculateNumber(q, InfoTag.LONG, "LLOAD", Mnemonic.LADD,
					"LSTORE");
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
		public ProgramBuilder addDouble(final Quadruple q) {
			return calculateNumber(q, InfoTag.DOUBLE, "DLOAD", Mnemonic.DADD,
					"DSTORE");
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
		public ProgramBuilder subLong(final Quadruple q) {
			return calculateNumber(q, InfoTag.LONG, "LLOAD", Mnemonic.LSUB,
					"LSTORE");
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
		public ProgramBuilder subDouble(final Quadruple q) {
			return calculateNumber(q, InfoTag.DOUBLE, "DLOAD", Mnemonic.DSUB,
					"DSTORE");
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
		public ProgramBuilder mulLong(final Quadruple q) {
			return calculateNumber(q, InfoTag.LONG, "LLOAD", Mnemonic.LMUL,
					"LSTORE");
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
		public ProgramBuilder mulDouble(final Quadruple q) {
			return calculateNumber(q, InfoTag.DOUBLE, "DLOAD", Mnemonic.DMUL,
					"DSTORE");
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
		public ProgramBuilder divLong(final Quadruple q) {
			return calculateNumber(q, InfoTag.LONG, "LLOAD", Mnemonic.LDIV,
					"LSTORE");
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
		public ProgramBuilder divDouble(final Quadruple q) {
			return calculateNumber(q, InfoTag.DOUBLE, "DLOAD", Mnemonic.DDIV,
					"DSTORE");
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
		public ProgramBuilder returnLong(final Quadruple q) {
			final short systemExitIndex = addSystemExitMethodToClassfile();

			final OperationBuilder op = OperationBuilder.newBuilder();
			op.add(loadOp(true, q.getArgument1(), InfoTag.LONG, "LLOAD"));
			op.add(Mnemonic.L2I);
			op.add(Mnemonic.INVOKESTATIC, 2,
					ByteUtils.shortToByteArray(systemExitIndex));
			op.add(Mnemonic.RETURN);
			return add(op.build());
		}

		/*
		 * === M2 === WORK IN PROGRESS
		 */

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder notBoolean(final Quadruple q) {
			return booleanOp(q, Mnemonic.INEG);
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder orBoolean(final Quadruple q) {
			return booleanOp(q, Mnemonic.IOR);
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder andBoolean(final Quadruple q) {
			return booleanOp(q, Mnemonic.IAND);
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder compareLongE(final Quadruple q) {
			return compareNumber(q, InfoTag.LONG, "LLOAD", Mnemonic.LCMP,
					"IFNE");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder compareLongG(final Quadruple q) {
			return compareNumber(q, InfoTag.LONG, "LLOAD", Mnemonic.LCMP,
					"IFLE");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder compareLongL(final Quadruple q) {
			return compareNumber(q, InfoTag.LONG, "LLOAD", Mnemonic.LCMP,
					"IFGE");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder compareLongGE(final Quadruple q) {
			return compareNumber(q, InfoTag.LONG, "LLOAD", Mnemonic.LCMP,
					"IFLT");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder compareLongLE(final Quadruple q) {
			return compareNumber(q, InfoTag.LONG, "LLOAD", Mnemonic.LCMP,
					"IFGT");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder compareDoubleE(final Quadruple q) {
			return compareNumber(q, InfoTag.DOUBLE, "DLOAD", Mnemonic.DCMPL,
					"IFNE");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder compareDoubleG(final Quadruple q) {
			return compareNumber(q, InfoTag.DOUBLE, "DLOAD", Mnemonic.DCMPL,
					"IFLE");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder compareDoubleL(final Quadruple q) {
			return compareNumber(q, InfoTag.DOUBLE, "DLOAD", Mnemonic.DCMPG,
					"IFGE");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder compareDoubleGE(final Quadruple q) {
			return compareNumber(q, InfoTag.DOUBLE, "DLOAD", Mnemonic.DCMPL,
					"IFLT");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder compareDoubleLE(final Quadruple q) {
			return compareNumber(q, InfoTag.DOUBLE, "DLOAD", Mnemonic.DCMPG,
					"IFGT");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder label(final Quadruple q) {
			labelFlag = true;
			labelName = q.getArgument1();
			return this;
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder branch(final Quadruple q) {
			// TODO implement
			final OperationBuilder op = OperationBuilder.newBuilder();
			if ("!".equals(q.getResult())) {
				// unconditional branch
				final Instruction target = getJumpTarget(q.getArgument1());
				//op.add(new JumpInstruction(3, Mnemonic.GOTO, target));
				op.add(new JumpInstruction(Mnemonic.GOTO, target));
			} else {
				final Instruction trueTarget = getJumpTarget(q.getArgument1());
				final Instruction falseTarget = getJumpTarget(q.getArgument2());
				final Instruction endNop = new Instruction(Mnemonic.NOP);
				// conditional branch
				op.add(loadBooleanOp(q.getResult()));
				//op.add(new JumpInstruction(2, Mnemonic.IFEQ, falseTarget));
				op.add(new JumpInstruction(Mnemonic.IFEQ, falseTarget));
				op.add(trueTarget);
				//op.add(new JumpInstruction(3, Mnemonic.GOTO, endNop));
				op.add(new JumpInstruction(Mnemonic.GOTO, endNop));
				op.add(falseTarget);
				op.add(endNop);
			}
			return add(op.build());
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder printBoolean(final Quadruple q) {
			addPrintMethodToClassfile();
			final OperationBuilder op = OperationBuilder.newBuilder();
			op.add(Mnemonic.GETSTATIC, 2,
					ByteUtils.shortToByteArray(systemOutIndex));
			op.add(loadBooleanOp(q.getArgument1()));
			op.add(Mnemonic.INVOKEVIRTUAL, 2,
					ByteUtils.shortToByteArray(printIndex));
			return add(op.build());
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder printLong(final Quadruple q) {
			return print(q.getArgument1(), InfoTag.LONG, "LDC2_W");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder printDouble(final Quadruple q) {
			return print(q.getArgument1(), InfoTag.DOUBLE, "LDC2_W");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder printString(final Quadruple q) {
			return print(q.getArgument1(), InfoTag.STRING, "LDC");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder declareArray(final Quadruple q) {
			if (q.getResult() != null) {
				arrayName = q.getResult();
			}
			arraySizes.push(q.getArgument1());
			return this;
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder arrayGetLong(final Quadruple q) {
			return arrayGet(q, Mnemonic.LALOAD, "LSTORE");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder arrayGetDouble(final Quadruple q) {
			return arrayGet(q, Mnemonic.DALOAD, "DSTORE");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder arrayGetBoolean(final Quadruple q) {
			return arrayGet(q, Mnemonic.BALOAD, "ISTORE");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder arrayGetString(final Quadruple q) {
			return arrayGet(q, Mnemonic.AALOAD, "ASTORE");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder arrayGetArray(final Quadruple q) {
			return arrayGet(q, Mnemonic.AALOAD, "ASTORE");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder arrayGetReference(final Quadruple q) {
			return arrayGet(q, Mnemonic.AALOAD, "ASTORE");
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder arraySetLong(final Quadruple q) {
			return arraySet(q, true, InfoTag.LONG, "LDC2_W", Mnemonic.LASTORE);
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder arraySetDouble(final Quadruple q) {
			return arraySet(q, true, InfoTag.DOUBLE, "LDC2_W", Mnemonic.DASTORE);
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder arraySetBoolean(final Quadruple q) {
			// TODO implement
			return this;
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder arraySetString(final Quadruple q) {
			// TODO implement
			return this;
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 *            the operation quadruple
		 * @return this program builders instance
		 */
		public ProgramBuilder arraySetArray(final Quadruple q) {
			// TODO implement
			return this;
		}

		/*
		 * === M3 === WORK IN PROGRESS
		 */

	}

	private final List<Operation> operations;

	private Program(final List<Operation> operations) {
		this.operations = operations;
	}

	/**
	 * Returns this programs operations
	 * 
	 * @return the operations
	 */
	public List<Operation> toOperationsList() {
		return operations;
	}

	/**
	 * Returns this programs instructions.
	 * 
	 * @return the instructions
	 */
	public Instruction[] toInstructionsArray() {
		int icount = 0;
		for (final Operation op : operations) {
			icount += op.getInstructionCount();
		}
		final Instruction[] instructions = new Instruction[icount];
		int currIndex = 0;
		if (operations != null) {
			for (final Operation op : operations) {

				System.arraycopy(op.getInstructions(), 0, instructions,
						currIndex, op.getInstructionCount());
				currIndex += op.getInstructionCount();
			}
		}
		return instructions;
	}

	/**
	 * Returns this programs bytes
	 * 
	 * @return the byte array
	 */
	public byte[] toByteArray() {
		final ByteBuffer bb = ByteBuffer.allocate(getByteCount());
		if (operations != null) {
			for (final Operation operation : operations) {
				bb.put(operation.toByteArray());
			}
		}
		return bb.array();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		if (operations != null) {
			for (final Operation operation : operations) {
				sb.append(operation.toString());
			}
		}
		return sb.toString();
	}

	/**
	 * Returns this programs hex string
	 * 
	 * @return the hex string
	 */
	public String toHexString() {
		return ByteUtils.hexFromBytes(toByteArray());
	}

	/**
	 * Returns this programs operations count
	 * 
	 * @return the operator count
	 */
	public int getOperationsCount() {
		return operations.size();
	}

	/**
	 * Returns this programs byte count
	 * 
	 * @return the byte count
	 */
	public int getByteCount() {
		int count = 0;
		if (operations != null) {
			for (final Operation operation : operations) {
				count += operation.getByteCount();
			}
		}
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (operations == null ? 0 : operations.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Program))
			return false;
		final Program other = (Program) obj;
		if (operations == null) {
			if (other.operations != null)
				return false;
		} else if (!operations.equals(other.operations))
			return false;
		return true;
	}

}
