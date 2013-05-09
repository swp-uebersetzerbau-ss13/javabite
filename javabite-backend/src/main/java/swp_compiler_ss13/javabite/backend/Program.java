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

		private ProgramBuilder convertNum(final Quadruple q,
				final String loadOp, final Mnemonic convertOp,
				final String storeOp) {
			final OperationBuilder op = OperationBuilder.newBuilder();
			if (isConstant(q.getArgument1())) {
				final short index = classfile.getIndexOfConstantInConstantPool(
						q.getArgument1(), Translator.TYPE_LONG);
				op.add(Mnemonic.LDC2_W, 2, toByteArray(index));
			} else {
				final short index = classfile.getIndexOfVariableInMethod(
						methodName, q.getArgument1());
				// NOTE: LLOAD only valid for index <= 255, for index <= 65536
				// use WIDE
				op.add(Mnemonic.getMnemonic(loadOp, index), 1,
						toByteArray(index));
			}
			op.add(convertOp);
			final short index = classfile.getIndexOfVariableInMethod(
					methodName, q.getResult());
			op.add(Mnemonic.getMnemonic(storeOp, index), 1, toByteArray(index));
			return add(op.build());
		}

		public ProgramBuilder longToDouble(final Quadruple q) {
			return convertNum(q, "LLOAD", Mnemonic.L2D, "DSTORE");
		}

		public ProgramBuilder doubleToLong(final Quadruple q) {
			return convertNum(q, "DLOAD", Mnemonic.D2L, "LSTORE");
		}

		public ProgramBuilder assignLong(final Quadruple q) {
			return this;
		}

		public ProgramBuilder assignDouble(final Quadruple q) {
			return this;
		}

		public ProgramBuilder assignString(final Quadruple q) {
			return this;
		}

		public ProgramBuilder assignBoolean(final Quadruple q) {
			return this;
		}

		public ProgramBuilder addLong(final Quadruple q) {
			return this;
		}

		public ProgramBuilder addDouble(final Quadruple q) {
			return this;
		}

		public ProgramBuilder subLong(final Quadruple q) {
			return this;
		}

		public ProgramBuilder subDouble(final Quadruple q) {
			return this;
		}

		public ProgramBuilder mulLong(final Quadruple q) {
			return this;
		}

		public ProgramBuilder mulDouble(final Quadruple q) {
			return this;
		}

		public ProgramBuilder divLong(final Quadruple q) {
			return this;
		}

		public ProgramBuilder divDouble(final Quadruple q) {
			return this;
		}

		public ProgramBuilder returnLong(final Quadruple q) {
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
