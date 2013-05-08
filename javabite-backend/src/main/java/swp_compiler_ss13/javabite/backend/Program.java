package swp_compiler_ss13.javabite.backend;

import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.javabite.backend.temp3.Instruction;

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

		@SuppressWarnings("unused")
		private final int initialOffset;
		private int currentOffset;
		private List<Operation> operations;

		private ProgramBuilder(final int initialOffset) {
			this.initialOffset = initialOffset;
			this.currentOffset = initialOffset;
			this.operations = new ArrayList<>();
		}

		public ProgramBuilder add(Operation operation) {
			operations.add(operation);
			for (Instruction instruction : operation.getInstructions()) {
				instruction.setOffset(instruction.getOffset() + currentOffset);
			}
			currentOffset += operation.getSize();
			return this;
		}

		public Program build() {
			return new Program(operations);
		}

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
