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
 * Representation of a program instruction block. Contains a list of operations which consist of instructions.
 * 
 * @author eike
 * @since 02.05.2013 23:41:39
 * 
 */
public class Program {

	public static class ProgramBuilder {

		private static final Pattern P_CONST_SIGN = Pattern.compile("#.*?");

		public static ProgramBuilder newBuilder(final int initialOffset, final IClassfile classfile, final String methodName) {
			return new ProgramBuilder(initialOffset, classfile, methodName);
		}

		public static ProgramBuilder newBuilder(final IClassfile classfile, final String methodName) {
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

		private ProgramBuilder(final int initialOffset, final IClassfile classfile, final String methodName) {
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

		public ProgramBuilder declareLong(final Quadruple q) {
			return this;
		}

		public ProgramBuilder declareDouble(final Quadruple q) {
			return this;
		}

		public ProgramBuilder declareString(final Quadruple q) {
			return this;
		}

		public ProgramBuilder declareBoolean(final Quadruple q) {
			return this;
		}

		private void load(final OperationBuilder op, final String arg1, final ConstantType constType, final String varLoadOp) {
			if (isConstant(arg1)) {
				final short index = classfile.getIndexOfConstantInConstantPool(constType, removeConstantSign(arg1));
				assert index > 0;
				if (index < 256) {
					op.add(Mnemonic.LDC, 1, (byte) index);
				} else {
					op.add(Mnemonic.LDC_W, 1, ByteUtils.shortToByteArray(index));
				}
			} else {
				final byte index = classfile.getIndexOfVariableInMethod(methodName, arg1);
				assert index > 0;
				op.add(Mnemonic.getMnemonic(varLoadOp, index), 1, index);
			}
		}

		private void loadWide(final OperationBuilder op, final String arg1, final ConstantType constType, final String varLoadOp) {
			if (isConstant(arg1)) {
				final short index = classfile.getIndexOfConstantInConstantPool(constType, removeConstantSign(arg1));
				assert index > 0;
				op.add(Mnemonic.LDC2_W, 2, ByteUtils.shortToByteArray(index));
			} else {
				final byte index = classfile.getIndexOfVariableInMethod(methodName, arg1);
				assert index > 0;
				op.add(Mnemonic.getMnemonic(varLoadOp, index), 1, index);
			}
		}

		private void store(final OperationBuilder op, final String result, final String storeOp) {
			final byte index = classfile.getIndexOfVariableInMethod(methodName, result);
			op.add(Mnemonic.getMnemonic(storeOp, index), 1, index);
		}

		private ProgramBuilder assignNumber(final Quadruple q, final ConstantType dataType, final String loadOp, final Mnemonic convertOp, final String storeOp) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			loadWide(op, q.getArgument1(), dataType, loadOp);
			if (convertOp != null) {
				op.add(convertOp);
			}
			store(op, q.getResult(), storeOp);
			return add(op.build());
		}

		private ProgramBuilder calculateNumber(final Quadruple q, final ConstantType dataType, final String loadOp, final Mnemonic calcOp, final String storeOp) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			loadWide(op, q.getArgument1(), dataType, loadOp);
			loadWide(op, q.getArgument2(), dataType, loadOp);
			op.add(calcOp);
			store(op, q.getResult(), storeOp);
			return add(op.build());
		}

		public ProgramBuilder longToDouble(final Quadruple q) {
			return assignNumber(q, ConstantType.LONG, "LLOAD", Mnemonic.L2D, "DSTORE");
		}

		public ProgramBuilder doubleToLong(final Quadruple q) {
			return assignNumber(q, ConstantType.DOUBLE, "DLOAD", Mnemonic.D2L, "LSTORE");
		}

		public ProgramBuilder assignLong(final Quadruple q) {
			return assignNumber(q, ConstantType.LONG, "LLOAD", null, "LSTORE");
		}

		public ProgramBuilder assignDouble(final Quadruple q) {
			return assignNumber(q, ConstantType.DOUBLE, "DLOAD", null, "DSTORE");
		}

		public ProgramBuilder assignString(final Quadruple q) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			load(op, q.getArgument1(), ConstantType.STRING, "ALOAD");
			store(op, q.getResult(), "ASTORE");
			return add(op.build());
		}

		public ProgramBuilder assignBoolean(final Quadruple q) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			if (isConstant(q.getArgument1())) {
				final short value = (short) (Translator.CONST_TRUE.equals(q.getArgument1().toUpperCase()) ? 1 : 0);
				op.add(Mnemonic.ICONST(value), 1, ByteUtils.shortToByteArray(value));
			} else {
				final byte index = classfile.getIndexOfVariableInMethod(methodName, q.getArgument1());
				op.add(Mnemonic.ILOAD(index), 1, index);
			}
			final byte index = classfile.getIndexOfVariableInMethod(methodName, q.getResult());
			op.add(Mnemonic.ISTORE(index), 1, index);
			return add(op.build());
		}

		public ProgramBuilder addLong(final Quadruple q) {
			return calculateNumber(q, ConstantType.LONG, "LLOAD", Mnemonic.LADD, "LSTORE");
		}

		public ProgramBuilder addDouble(final Quadruple q) {
			return calculateNumber(q, ConstantType.DOUBLE, "DLOAD", Mnemonic.DADD, "DSTORE");
		}

		public ProgramBuilder subLong(final Quadruple q) {
			return calculateNumber(q, ConstantType.LONG, "LLOAD", Mnemonic.LSUB, "LSTORE");
		}

		public ProgramBuilder subDouble(final Quadruple q) {
			return calculateNumber(q, ConstantType.DOUBLE, "DLOAD", Mnemonic.DADD, "DSTORE");
		}

		public ProgramBuilder mulLong(final Quadruple q) {
			return calculateNumber(q, ConstantType.LONG, "LLOAD", Mnemonic.LMUL, "LSTORE");
		}

		public ProgramBuilder mulDouble(final Quadruple q) {
			return calculateNumber(q, ConstantType.DOUBLE, "DLOAD", Mnemonic.DMUL, "DSTORE");
		}

		public ProgramBuilder divLong(final Quadruple q) {
			return calculateNumber(q, ConstantType.LONG, "LLOAD", Mnemonic.LDIV, "LSTORE");
		}

		public ProgramBuilder divDouble(final Quadruple q) {
			return calculateNumber(q, ConstantType.DOUBLE, "DLOAD", Mnemonic.DDIV, "DSTORE");
		}

		public ProgramBuilder returnLong(final Quadruple q) {
			short systemExitIndex = this.addSystemExitFunctionToClassfile();

			final OperationBuilder op = OperationBuilder.newBuilder();
			if (isConstant(q.getArgument1())) {
				final short index = classfile.getIndexOfConstantInConstantPool(ConstantType.LONG, removeConstantSign(q.getArgument1()));
				op.add(Mnemonic.LDC2_W, 2, ByteUtils.shortToByteArray(index));
			} else {
				final byte index = classfile.getIndexOfVariableInMethod(methodName, q.getArgument1());
				op.add(Mnemonic.LLOAD(index), 1, index);
			}
			op.add(Mnemonic.L2I);
			op.add(Mnemonic.INVOKESTATIC, 1, ByteUtils.shortToByteArray(systemExitIndex));
			op.add(Mnemonic.RETURN);
			return add(op.build());
		}

		/**
		 * addSystemExitFunctionToClassfile function. This function checks whether the return flag is already set and if not, it'll add the system exit data to
		 * the classfile.
		 * 
		 * @author Marco
		 * @since 13.05.2013
		 * 
		 * @return systemExitIndex the constant pool index of the methodref entry of the system exit function.
		 */
		private short addSystemExitFunctionToClassfile() {
			if (!this.returnFlag) {
				this.returnFlag = true;
				this.systemExitIndex = this.classfile.addDataForSystemCall("exit", "(I)V", "java/lang/System");
			}
			return this.systemExitIndex;
		}

	}

	private final List<Operation> operations;

	private Program(final List<Operation> operations) {
		this.operations = operations;
	}

	public List<Operation> toOperationsList() {
		return operations;
	}

	public List<Instruction> toInstructionsList() {
		int icount = 0;
		for (final Operation op : operations) {
			icount += op.getInstructionCount();
		}
		final List<Instruction> instructions = new ArrayList<Instruction>(icount);
		if (operations != null) {
			for (final Operation op : operations) {
				instructions.addAll(op.getInstructions());
			}
		}
		return instructions;
	}

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

	public int getOperatorCount() {
		return operations.size();
	}

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
