package swp_compiler_ss13.javabite.backend;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.IClassfile.ConstantType;
import swp_compiler_ss13.javabite.backend.Operation.OperationBuilder;
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

		// determines, whether the System exit function has already been added/
		// a return statement is present in the tac
		private boolean returnFlag;
		// index of methodRef of system exit function in constant pool
		private short systemExitIndex;

		private ProgramBuilder(final int initialOffset,
				final IClassfile classfile, final String methodName) {
			this.operations = new ArrayList<>();
			this.classfile = classfile;
			this.methodName = methodName;
			this.returnFlag = false;
			this.systemExitIndex = 0;
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

		private void load(final OperationBuilder op, final boolean wide,
				final String arg1, final ConstantType constType,
				final String varLoadOp) {
			if (isConstant(arg1)) {
				final short index = classfile.getIndexOfConstantInConstantPool(
						constType, removeConstantSign(arg1));
				assert index > 0;
				if (wide) {
					op.add(Mnemonic.LDC2_W, 2,
							ByteUtils.shortToByteArray(index));
				} else if (index >= 256) {
					op.add(Mnemonic.LDC_W, 1, ByteUtils.shortToByteArray(index));
				} else {
					op.add(Mnemonic.LDC, 1, (byte) index);
				}
			} else {
				final byte index = classfile.getIndexOfVariableInMethod(
						methodName, arg1);
				assert index > 0;
				op.add(Mnemonic.getMnemonic(varLoadOp, index), 1, index);
			}
		}

		private void store(final OperationBuilder op, final String result,
				final String storeOp) {
			final byte index = classfile.getIndexOfVariableInMethod(methodName,
					result);
			op.add(Mnemonic.getMnemonic(storeOp, index), 1, index);
		}

		private ProgramBuilder assignNumber(final Quadruple q,
				final ConstantType dataType, final String loadOp,
				final Mnemonic convertOp, final String storeOp) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			load(op, true, q.getArgument1(), dataType, loadOp);
			if (convertOp != null) {
				op.add(convertOp);
			}
			store(op, q.getResult(), storeOp);
			return add(op.build());
		}

		private ProgramBuilder calculateNumber(final Quadruple q,
				final ConstantType dataType, final String loadOp,
				final Mnemonic calcOp, final String storeOp) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			load(op, true, q.getArgument1(), dataType, loadOp);
			load(op, true, q.getArgument2(), dataType, loadOp);
			op.add(calcOp);
			store(op, q.getResult(), storeOp);
			return add(op.build());
		}

		/**
		 * addSystemExitFunctionToClassfile function. This function checks
		 * whether the return flag is already set and if not, it'll add the
		 * system exit data to the classfile.
		 * 
		 * @author Marco
		 * @since 13.05.2013
		 * 
		 * @return systemExitIndex the constant pool index of the methodref
		 *         entry of the system exit function.
		 */
		private short addSystemExitFunctionToClassfile() {
			if (!this.returnFlag) {
				this.returnFlag = true;
				this.systemExitIndex = this.classfile.addDataForSystemCall(
						"exit", "(I)V", "java/lang/System");
			}
			return this.systemExitIndex;
		}

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
			return assignNumber(q, ConstantType.LONG, "LLOAD", Mnemonic.L2D,
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
			return assignNumber(q, ConstantType.DOUBLE, "DLOAD", Mnemonic.D2L,
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
			return assignNumber(q, ConstantType.LONG, "LLOAD", null, "LSTORE");
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
			return assignNumber(q, ConstantType.DOUBLE, "DLOAD", null, "DSTORE");
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
			load(op, false, q.getArgument1(), ConstantType.STRING, "ALOAD");
			store(op, q.getResult(), "ASTORE");
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
			if (isConstant(q.getArgument1())) {
				final short value = (short) (Translator.CONST_TRUE.equals(q
						.getArgument1().toUpperCase()) ? 1 : 0);
				op.add(Mnemonic.ICONST(value), 1,
						ByteUtils.shortToByteArray(value));
			} else {
				final byte index = classfile.getIndexOfVariableInMethod(
						methodName, q.getArgument1());
				op.add(Mnemonic.ILOAD(index), 1, index);
			}
			final byte index = classfile.getIndexOfVariableInMethod(methodName,
					q.getResult());
			op.add(Mnemonic.ISTORE(index), 1, index);
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
			return calculateNumber(q, ConstantType.LONG, "LLOAD",
					Mnemonic.LADD, "LSTORE");
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
			return calculateNumber(q, ConstantType.DOUBLE, "DLOAD",
					Mnemonic.DADD, "DSTORE");
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
			return calculateNumber(q, ConstantType.LONG, "LLOAD",
					Mnemonic.LSUB, "LSTORE");
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
			return calculateNumber(q, ConstantType.DOUBLE, "DLOAD",
					Mnemonic.DSUB, "DSTORE");
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
			return calculateNumber(q, ConstantType.LONG, "LLOAD",
					Mnemonic.LMUL, "LSTORE");
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
			return calculateNumber(q, ConstantType.DOUBLE, "DLOAD",
					Mnemonic.DMUL, "DSTORE");
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
			return calculateNumber(q, ConstantType.LONG, "LLOAD",
					Mnemonic.LDIV, "LSTORE");
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
			return calculateNumber(q, ConstantType.DOUBLE, "DLOAD",
					Mnemonic.DDIV, "DSTORE");
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
			final short systemExitIndex = this
					.addSystemExitFunctionToClassfile();

			final OperationBuilder op = OperationBuilder.newBuilder();
			if (isConstant(q.getArgument1())) {
				final short index = classfile
						.getIndexOfConstantInConstantPool(ConstantType.LONG,
								removeConstantSign(q.getArgument1()));
				op.add(Mnemonic.LDC2_W, 2, ByteUtils.shortToByteArray(index));
			} else {
				final byte index = classfile.getIndexOfVariableInMethod(
						methodName, q.getArgument1());
				op.add(Mnemonic.LLOAD(index), 1, index);
			}
			op.add(Mnemonic.L2I);
			op.add(Mnemonic.INVOKESTATIC, 2,
					ByteUtils.shortToByteArray(systemExitIndex));
			op.add(Mnemonic.RETURN);
			return add(op.build());
		}

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

}
