package swp_compiler_ss13.javabite.backend;

import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.common.backend.Quadruple;

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

		public static ProgramBuilder newBuilder(final int initialOffset) {
			return new ProgramBuilder(initialOffset);
		}

		public static ProgramBuilder newBuilder() {
			return newBuilder(0);
		}

//		private final int initialOffset;
//		private int currentOffset;
		private List<Operation> operations;

		private ProgramBuilder(final int initialOffset) {
//			this.initialOffset = initialOffset;
//			this.currentOffset = initialOffset;
			this.operations = new ArrayList<>();
		}

		public ProgramBuilder add(Operation operation) {
			operations.add(operation);
//			for (Instruction instruction : operation.getInstructions()) {
//				instruction.setOffset(instruction.getOffset() + currentOffset);
//			}
//			currentOffset += operation.getSize();
			return this;
		}

		public Program build() {
			return new Program(operations);
		}
		
		public ProgramBuilder declareLong(final Quadruple q) { return this; }
		public ProgramBuilder declareDouble(final Quadruple q) { return this; }
		public ProgramBuilder declareString(final Quadruple q) { return this; }
		public ProgramBuilder declareBoolean(final Quadruple q) { return this; }
		
		public ProgramBuilder longToDouble(final Quadruple q) { return this; }
		public ProgramBuilder doubleToLong(final Quadruple q) { return this; }
		
		public ProgramBuilder assignLong(final Quadruple q) { return this; }
		public ProgramBuilder assignDouble(final Quadruple q) { return this; }
		public ProgramBuilder assignString(final Quadruple q) { return this; }
		public ProgramBuilder assignBoolean(final Quadruple q) { return this; }

		public ProgramBuilder addLong(final Quadruple q) { return this; }
		public ProgramBuilder addDouble(final Quadruple q) { return this; }

		public ProgramBuilder subLong(final Quadruple q) { return this; }
		public ProgramBuilder subDouble(final Quadruple q) { return this; }

		public ProgramBuilder mulLong(final Quadruple q) { return this; }
		public ProgramBuilder mulDouble(final Quadruple q) { return this; }

		public ProgramBuilder divLong(final Quadruple q) { return this; }
		public ProgramBuilder divDouble(final Quadruple q) { return this; }
		
		public ProgramBuilder returnLong(final Quadruple q) { return this; }

	}

	private final List<Operation> operations;

	private Program(final List<Operation> operations) {
		this.operations = operations;
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
