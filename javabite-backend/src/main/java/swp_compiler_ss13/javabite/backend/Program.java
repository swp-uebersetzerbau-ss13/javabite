package swp_compiler_ss13.javabite.backend;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.Operation.OperationBuilder;
import swp_compiler_ss13.javabite.backend.classfile.IClassfile;
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

		private ProgramBuilder(final int initialOffset,
				final IClassfile classfile, final String methodName) {
			this.operations = new ArrayList<>();
			this.classfile = classfile;
			this.methodName = methodName;
			this.returnFlag = false;
			this.systemExitIndex = 0;
			this.systemOutIndex = 0;
			this.printIndex = 0;
		}

		private ProgramBuilder add(final Operation operation) {
			operations.add(operation);
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
			if (!this.returnFlag) {
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

		private void putJumpTarget(final String s, final Instruction i) {
			if (labelFlag) {
				if (jumpTargets == null)
					jumpTargets = new HashMap<>();
				labelFlag = false;
				jumpTargets.put(s, i);
			}
		}

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
					return new Instruction(2, Mnemonic.LDC2_W,
							ByteUtils.shortToByteArray(index));
				} else if (index >= 256) {
					return new Instruction(1, Mnemonic.LDC_W,
							ByteUtils.shortToByteArray(index));
				} else {
					return new Instruction(1, Mnemonic.LDC, (byte) index);
				}
			} else {
				final byte index = classfile.getIndexOfVariableInMethod(
						methodName, arg1);
				assert index > 0;
				return new Instruction(1,
						Mnemonic.getMnemonic(varLoadOp, index), index);
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
				return new Instruction(1, Mnemonic.ICONST(value),
						ByteUtils.shortToByteArray(value));
			} else {
				final byte index = classfile.getIndexOfVariableInMethod(
						methodName, arg1);
				return new Instruction(1, Mnemonic.ILOAD(index), index);
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
			return new Instruction(1, Mnemonic.getMnemonic(storeOp, index),
					index);
		}

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
		 * TODO javadoc
		 * 
		 * @param q
		 * @param constType
		 * @param loadOp
		 * @param compareOp
		 * @param jumpOp
		 * @return
		 */
		private ProgramBuilder compareNumber(final Quadruple q,
				final InfoTag constType, final String loadOp,
				final Mnemonic compareOp, final String jumpOp) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			final Instruction iCmpFalse = new Instruction(1, Mnemonic.ICONST_0);
			final Instruction iStore = storeOp(q.getResult(), "ISTORE");

			op.add(loadOp(true, q.getArgument1(), constType, loadOp));
			op.add(loadOp(true, q.getArgument2(), constType, loadOp));
			op.add(compareOp);
			op.add(new JumpInstruction(3, Mnemonic.getMnemonic(jumpOp),
					iCmpFalse));
			op.add(Mnemonic.ICONST_1);
			op.add(new JumpInstruction(3, Mnemonic.GOTO, iStore));
			op.add(iCmpFalse);
			op.add(iStore);
			return add(op.build());
		}

		/**
		 * TODO javadoc
		 * 
		 * @param q
		 * @param mnemonic
		 * @return
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
			if (!this.returnFlag) {
				this.returnFlag = true;
				// TODO externalize static strings
				this.systemExitIndex = this.classfile
						.addMethodrefConstantToConstantPool("exit", "(I)V",
								"java/lang/System");
			}
			return this.systemExitIndex;
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
			if (this.systemOutIndex == 0) {
				// TODO externalize static strings
				this.systemOutIndex = this.classfile
						.addFieldrefConstantToConstantPool("out",
								"Ljava/io/PrintStream;", "java/lang/System");
			}

			// add print methodref info to constant pool, if necessary
			if (this.printIndex == 0) {
				// TODO externalize static strings
				this.printIndex = this.classfile
						.addMethodrefConstantToConstantPool("print",
								"(Ljava/lang/String;)V", "java/io/PrintStream");
			}
		}

		/*
		 * === M1 === FINISHED
		 */

		public ProgramBuilder declareLong(final Quadruple q) {
			throw new UnsupportedOperationException();
		}

		public ProgramBuilder declareDouble(final Quadruple q) {
			throw new UnsupportedOperationException();
		}

		public ProgramBuilder declareString(final Quadruple q) {
			throw new UnsupportedOperationException();
		}

		public ProgramBuilder declareBoolean(final Quadruple q) {
			throw new UnsupportedOperationException();
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
			final short systemExitIndex = this.addSystemExitMethodToClassfile();

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

		public ProgramBuilder notBoolean(final Quadruple q) {
			return booleanOp(q, Mnemonic.INEG);
		}

		public ProgramBuilder orBoolean(final Quadruple q) {
			return booleanOp(q, Mnemonic.IOR);
		}

		public ProgramBuilder andBoolean(final Quadruple q) {
			return booleanOp(q, Mnemonic.IAND);
		}

		public ProgramBuilder compareLongE(final Quadruple q) {
			return compareNumber(q, InfoTag.LONG, "LLOAD", Mnemonic.LCMP,
					"IFNE");
		}

		public ProgramBuilder compareLongG(final Quadruple q) {
			return compareNumber(q, InfoTag.LONG, "LLOAD", Mnemonic.LCMP,
					"IFLE");
		}

		public ProgramBuilder compareLongL(final Quadruple q) {
			return compareNumber(q, InfoTag.LONG, "LLOAD", Mnemonic.LCMP,
					"IFGE");
		}

		public ProgramBuilder compareLongGE(final Quadruple q) {
			return compareNumber(q, InfoTag.LONG, "LLOAD", Mnemonic.LCMP,
					"IFLT");
		}

		public ProgramBuilder compareLongLE(final Quadruple q) {
			return compareNumber(q, InfoTag.LONG, "LLOAD", Mnemonic.LCMP,
					"IFGT");
		}

		public ProgramBuilder compareDoubleE(final Quadruple q) {
			return compareNumber(q, InfoTag.DOUBLE, "DLOAD", Mnemonic.DCMPL,
					"IFNE");
		}

		public ProgramBuilder compareDoubleG(final Quadruple q) {
			return compareNumber(q, InfoTag.DOUBLE, "DLOAD", Mnemonic.DCMPL,
					"IFLE");
		}

		public ProgramBuilder compareDoubleL(final Quadruple q) {
			return compareNumber(q, InfoTag.DOUBLE, "DLOAD", Mnemonic.DCMPG,
					"IFGE");
		}

		public ProgramBuilder compareDoubleGE(final Quadruple q) {
			return compareNumber(q, InfoTag.DOUBLE, "DLOAD", Mnemonic.DCMPL,
					"IFLT");
		}

		public ProgramBuilder compareDoubleLE(final Quadruple q) {
			return compareNumber(q, InfoTag.DOUBLE, "DLOAD", Mnemonic.DCMPG,
					"IFGT");
		}

		public ProgramBuilder label(final Quadruple q) {
			labelFlag = true;
			return this;
		}

		public ProgramBuilder branch(final Quadruple q) {
			// TODO implement
			final OperationBuilder op = OperationBuilder.newBuilder();
			if ("!".equals(q.getResult())) {
				// unconditional branch
				final Instruction target = getJumpTarget(q.getArgument1());
				op.add(new JumpInstruction(3, Mnemonic.GOTO, target));
			} else {
				final Instruction trueTarget = getJumpTarget(q.getArgument1());
				final Instruction falseTarget = getJumpTarget(q.getArgument2());
				// conditional branch
			}
			return add(op.build());
		}

		public ProgramBuilder printBoolean(final Quadruple q) {
			// TODO implement
			// syso boolean
			return this;
		}

		public ProgramBuilder printLong(final Quadruple q) {
			// TODO implement
			// syso long
			return this;
		}

		public ProgramBuilder printDouble(final Quadruple q) {
			// TODO implement
			// syso double
			return this;
		}

		public ProgramBuilder printString(final Quadruple q) {
			// TODO implement
			// syso string
			addPrintMethodToClassfile();
			return this;
		}

		public ProgramBuilder arrayGetLong(final Quadruple q) {
			// TODO implement
			return this;
		}

		public ProgramBuilder arrayGetDouble(final Quadruple q) {
			// TODO implement
			return this;
		}

		public ProgramBuilder arrayGetBoolean(final Quadruple q) {
			// TODO implement
			return this;
		}

		public ProgramBuilder arrayGetString(final Quadruple q) {
			// TODO implement
			return this;
		}

		public ProgramBuilder arrayGetArray(final Quadruple q) {
			// TODO implement
			return this;
		}

		public ProgramBuilder arrayGetReference(final Quadruple q) {
			// TODO implement
			return this;
		}

		public ProgramBuilder arraySetLong(final Quadruple q) {
			// TODO implement
			return this;
		}

		public ProgramBuilder arraySetDouble(final Quadruple q) {
			// TODO implement
			return this;
		}

		public ProgramBuilder arraySetBoolean(final Quadruple q) {
			// TODO implement
			return this;
		}

		public ProgramBuilder arraySetString(final Quadruple q) {
			// TODO implement
			return this;
		}

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
	public List<Instruction> toInstructionsList() {
		int icount = 0;
		for (final Operation op : operations) {
			icount += op.getInstructionCount();
		}
		final List<Instruction> instructions = new ArrayList<Instruction>(
				icount);
		if (operations != null) {
			for (final Operation op : operations) {
				instructions.addAll(op.getInstructions());
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
				+ ((operations == null) ? 0 : operations.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Program))
			return false;
		Program other = (Program) obj;
		if (operations == null) {
			if (other.operations != null)
				return false;
		} else if (!operations.equals(other.operations))
			return false;
		return true;
	}

}
