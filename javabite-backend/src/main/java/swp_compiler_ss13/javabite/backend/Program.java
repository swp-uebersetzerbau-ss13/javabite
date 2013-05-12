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

		private ProgramBuilder(final int initialOffset, final IClassfile classfile, final String methodName) {
			this.operations = new ArrayList<>();
			this.classfile = classfile;
			this.methodName = methodName;
		}

		private ProgramBuilder add(final Operation operation) {
			operations.add(operation);
			return this;
		}

		public Program build() {
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

		/*
		 * TODO: add support for assigning other local variables than prescribed by loadOp!
		 * 
		 * TODO: loadOp only valid for index <= 255, for index <= 65536 use WIDE
		 * 
		 * TODO: extract load/store methods
		 */
		private ProgramBuilder assignValue(final Quadruple q, final ConstantType dataType, final String loadOp, final Mnemonic convertOp, final String storeOp) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			if (isConstant(q.getArgument1())) {
				final short index = classfile.getIndexOfConstantInConstantPool(dataType, removeConstantSign(q.getArgument1()));
				assert index > 0;
				op.add(Mnemonic.LDC2_W, 2, ByteUtils.shortToByteArray(index));
			} else {
				final short index = classfile.getIndexOfVariableInMethod(methodName, q.getArgument1());
				assert index > 0;
				op.add(Mnemonic.getMnemonic(loadOp, index), 1, ByteUtils.shortToByteArray(index));
			}
			if (convertOp != null) {
				op.add(convertOp);
			}
			final short index = classfile.getIndexOfVariableInMethod(methodName, q.getResult());
			op.add(Mnemonic.getMnemonic(storeOp, index), 1, ByteUtils.shortToByteArray(index));
			return add(op.build());
		}

		private ProgramBuilder calculate(final Quadruple q, final ConstantType dataType, final String loadOp, final Mnemonic calcOp, final String storeOp) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			if (isConstant(q.getArgument1())) {
				final short index = classfile.getIndexOfConstantInConstantPool(dataType, removeConstantSign(q.getArgument1()));
				op.add(Mnemonic.LDC2_W, 2, ByteUtils.shortToByteArray(index));
			} else {
				final short index = classfile.getIndexOfVariableInMethod(methodName, q.getArgument1());
				op.add(Mnemonic.getMnemonic(loadOp, index), 1, ByteUtils.shortToByteArray(index));
			}
			if (isConstant(q.getArgument2())) {
				final short index = classfile.getIndexOfConstantInConstantPool(dataType, removeConstantSign(q.getArgument2()));
				op.add(Mnemonic.LDC2_W, 2, ByteUtils.shortToByteArray(index));
			} else {
				final short index = classfile.getIndexOfVariableInMethod(methodName, q.getArgument2());
				op.add(Mnemonic.getMnemonic(loadOp, index), 1, ByteUtils.shortToByteArray(index));
			}
			op.add(calcOp);
			final short index = classfile.getIndexOfVariableInMethod(methodName, q.getResult());
			op.add(Mnemonic.getMnemonic(storeOp, index), 1, ByteUtils.shortToByteArray(index));
			return add(op.build());
		}

		public ProgramBuilder longToDouble(final Quadruple q) {
			return assignValue(q, ConstantType.LONG, "LLOAD", Mnemonic.L2D, "DSTORE");
		}

		public ProgramBuilder doubleToLong(final Quadruple q) {
			return assignValue(q, ConstantType.DOUBLE, "DLOAD", Mnemonic.D2L, "LSTORE");
		}

		public ProgramBuilder assignLong(final Quadruple q) {
			return assignValue(q, ConstantType.LONG, "LLOAD", null, "LSTORE");
		}

		public ProgramBuilder assignDouble(final Quadruple q) {
			return assignValue(q, ConstantType.DOUBLE, "DLOAD", null, "DSTORE");
		}

		/*
		 * TODO: string constant will not be loaded properly: isConstant => LDC2_W is wrong
		 */
		public ProgramBuilder assignString(final Quadruple q) {
			return assignValue(q, ConstantType.STRING, "LDC", null, "ASTORE");
		}

		public ProgramBuilder assignBoolean(final Quadruple q) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			if (isConstant(q.getArgument1())) {
				final short value = (short) (Translator.CONST_TRUE.equals(q.getArgument1().toUpperCase()) ? 1 : 0);
				op.add(Mnemonic.ICONST(value), 1, ByteUtils.shortToByteArray(value));
			} else {
				final short index = classfile.getIndexOfVariableInMethod(methodName, q.getArgument1());
				op.add(Mnemonic.ILOAD(index), 1, ByteUtils.shortToByteArray(index));
			}
			final short index = classfile.getIndexOfVariableInMethod(methodName, q.getResult());
			op.add(Mnemonic.ISTORE(index), 1, ByteUtils.shortToByteArray(index));
			return add(op.build());
		}

		public ProgramBuilder addLong(final Quadruple q) {
			return calculate(q, ConstantType.LONG, "LLOAD", Mnemonic.LADD, "LSTORE");
		}

		public ProgramBuilder addDouble(final Quadruple q) {
			return calculate(q, ConstantType.DOUBLE, "DLOAD", Mnemonic.DADD, "DSTORE");
		}

		public ProgramBuilder subLong(final Quadruple q) {
			return calculate(q, ConstantType.LONG, "LLOAD", Mnemonic.LSUB, "LSTORE");
		}

		public ProgramBuilder subDouble(final Quadruple q) {
			return calculate(q, ConstantType.DOUBLE, "DLOAD", Mnemonic.DADD, "DSTORE");
		}

		public ProgramBuilder mulLong(final Quadruple q) {
			return calculate(q, ConstantType.LONG, "LLOAD", Mnemonic.LMUL, "LSTORE");
		}

		public ProgramBuilder mulDouble(final Quadruple q) {
			return calculate(q, ConstantType.DOUBLE, "DLOAD", Mnemonic.DMUL, "DSTORE");
		}

		public ProgramBuilder divLong(final Quadruple q) {
			return calculate(q, ConstantType.LONG, "LLOAD", Mnemonic.LDIV, "LSTORE");
		}

		public ProgramBuilder divDouble(final Quadruple q) {
			return calculate(q, ConstantType.DOUBLE, "DLOAD", Mnemonic.DDIV, "DSTORE");
		}

		public ProgramBuilder returnLong(final Quadruple q) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			if (isConstant(q.getArgument1())) {
				final short index = classfile.getIndexOfConstantInConstantPool(ConstantType.LONG, removeConstantSign(q.getArgument1()));
				op.add(Mnemonic.LDC2_W, 2, ByteUtils.shortToByteArray(index));
			} else {
				final short index = classfile.getIndexOfVariableInMethod(methodName, q.getArgument1());
				op.add(Mnemonic.LLOAD(index), 1, ByteUtils.shortToByteArray(index));
			}
			op.add(Mnemonic.LRETURN);
			return add(op.build());
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
