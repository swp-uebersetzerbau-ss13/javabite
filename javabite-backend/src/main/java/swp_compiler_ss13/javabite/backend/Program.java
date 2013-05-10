package swp_compiler_ss13.javabite.backend;

import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.Operation.OperationBuilder;
import swp_compiler_ss13.javabite.backend.translation.Translator;

/**
 * Representation of a program instruction block. Contains a list of operations
 * which consist of instructions.
 * 
 * @author eike
 * @since 02.05.2013 23:41:39
 * 
 */
public class Program
{

	public static class ProgramBuilder
	{

		public static ProgramBuilder newBuilder(final int initialOffset,
				final IClassfile classfile, final String methodName) {
			return new ProgramBuilder(initialOffset, classfile, methodName);
		}

		public static ProgramBuilder newBuilder(final IClassfile classfile,
				final String methodName) {
			return newBuilder(0, classfile, methodName);
		}

		// private final int initialOffset;
		// private int currentOffset;
		private final List<Operation> operations;
		private final IClassfile classfile;
		private final String methodName;

		private ProgramBuilder(final int initialOffset,
				final IClassfile classfile, final String methodName) {
			// this.initialOffset = initialOffset;
			// this.currentOffset = initialOffset;
			this.operations = new ArrayList<>();
			this.classfile = classfile;
			this.methodName = methodName;
		}

		private ProgramBuilder add(final Operation operation) {
			operations.add(operation);
			// for (Instruction instruction : operation.getInstructions()) {
			// instruction.setOffset(instruction.getOffset() + currentOffset);
			// }
			// currentOffset += operation.getSize();
			return this;
		}

		public Program build() {
			return new Program(operations);
		}

		private boolean isConstant(final String s) {
			return s.startsWith(Translator.SYM_CONST);
		}

		private Byte[] toByteArray(final short x) {
			return new Byte[] { (byte) (x & 0xff), (byte) ((x >> 8) & 0xff) };
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
		 * TODO: add support for assigning other local variables than prescribed
		 * by loadOp!
		 * 
		 * TODO: loadOp only valid for index <= 255, for index <= 65536 use WIDE
		 * 
		 */
		private ProgramBuilder assignValue(final Quadruple q,
				final String dataType, final String loadOp,
				final Mnemonic convertOp, final String storeOp) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			if (isConstant(q.getArgument1())) {
				final short index = classfile.getIndexOfConstantInConstantPool(
						q.getArgument1(), dataType);
				op.add(Mnemonic.LDC2_W, 2, toByteArray(index));
			} else {
				final short index = classfile.getIndexOfVariableInMethod(
						methodName, q.getArgument1());
				op.add(Mnemonic.getMnemonic(loadOp, index), 1,
						toByteArray(index));
			}
			if (convertOp != null) {
				op.add(convertOp);
			}
			final short index = classfile.getIndexOfVariableInMethod(
					methodName, q.getResult());
			op.add(Mnemonic.getMnemonic(storeOp, index), 1, toByteArray(index));
			return add(op.build());
		}

		public ProgramBuilder longToDouble(final Quadruple q) {
			return assignValue(q, "LONG", "LLOAD", Mnemonic.L2D, "DSTORE");
		}

		public ProgramBuilder doubleToLong(final Quadruple q) {
			return assignValue(q, "DOUBLE", "DLOAD", Mnemonic.D2L, "LSTORE");
		}

		public ProgramBuilder assignLong(final Quadruple q) {
			return assignValue(q, "LONG", "LLOAD", null, "LSTORE");
		}

		public ProgramBuilder assignDouble(final Quadruple q) {
			return assignValue(q, "DOUBLE", "DLOAD", null, "DSTORE");
		}

		public ProgramBuilder assignString(final Quadruple q) {
			// TODO implement
			return this;
		}

		public ProgramBuilder assignBoolean(final Quadruple q) {
			// TODO implement
			return this;
		}

		private ProgramBuilder calculate(final Quadruple q,
				final String dataType, final String loadOp,
				final Mnemonic calcOp, final String storeOp) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			if (isConstant(q.getArgument1())) {
				final short index = classfile.getIndexOfConstantInConstantPool(
						q.getArgument1(), dataType);
				op.add(Mnemonic.LDC2_W, 2, toByteArray(index));
			} else {
				final short index = classfile.getIndexOfVariableInMethod(
						methodName, q.getArgument1());
				op.add(Mnemonic.getMnemonic(loadOp, index), 1,
						toByteArray(index));
			}
			if (isConstant(q.getArgument2())) {
				final short index = classfile.getIndexOfConstantInConstantPool(
						q.getArgument2(), dataType);
				op.add(Mnemonic.LDC2_W, 2, toByteArray(index));
			} else {
				final short index = classfile.getIndexOfVariableInMethod(
						methodName, q.getArgument2());
				op.add(Mnemonic.getMnemonic(loadOp, index), 1,
						toByteArray(index));
			}
			op.add(calcOp);
			final short index = classfile.getIndexOfVariableInMethod(
					methodName, q.getResult());
			op.add(Mnemonic.getMnemonic(storeOp, index), 1, toByteArray(index));
			return add(op.build());
		}

		public ProgramBuilder addLong(final Quadruple q) {
			return calculate(q, "LONG", "LLOAD", Mnemonic.LADD, "LSTORE");
		}

		public ProgramBuilder addDouble(final Quadruple q) {
			return calculate(q, "DOUBLE", "DLOAD", Mnemonic.DADD, "DSTORE");
		}

		public ProgramBuilder subLong(final Quadruple q) {
			return calculate(q, "LONG", "LLOAD", Mnemonic.LSUB, "LSTORE");
		}

		public ProgramBuilder subDouble(final Quadruple q) {
			return calculate(q, "DOUBLE", "DLOAD", Mnemonic.DADD, "DSTORE");
		}

		public ProgramBuilder mulLong(final Quadruple q) {
			return calculate(q, "LONG", "LLOAD", Mnemonic.LMUL, "LSTORE");
		}

		public ProgramBuilder mulDouble(final Quadruple q) {
			return calculate(q, "DOUBLE", "DLOAD", Mnemonic.DMUL, "DSTORE");
		}

		public ProgramBuilder divLong(final Quadruple q) {
			return calculate(q, "LONG", "LLOAD", Mnemonic.LDIV, "LSTORE");
		}

		public ProgramBuilder divDouble(final Quadruple q) {
			return calculate(q, "DOUBLE", "DLOAD", Mnemonic.DDIV, "DSTORE");
		}

		public ProgramBuilder returnLong(final Quadruple q) {
			// TODO implement
			return this;
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
		final List<Instruction> instructions = new ArrayList<Instruction>(
				icount);
		for (final Operation op : operations) {
			instructions.addAll(op.getInstructions());
		}
		return instructions;
	}

	public byte[] toBytes() {
		// TODO implement
		return null;
	}

	@Override
	public String toString() {
		// TODO implement
		return null;
	}

	public int getSize() {
		return operations.size();
	}

}
