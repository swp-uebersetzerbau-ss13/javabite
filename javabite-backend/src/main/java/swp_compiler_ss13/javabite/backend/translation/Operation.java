package swp_compiler_ss13.javabite.backend.translation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <h1>Operation</h1>
 * <p>
 * This class represents a list of instructions and maps every tac-operation to
 * their bytecode equivalent. It is used to carry logical groups of
 * instructions.
 * </p>
 * 
 * @author eike
 * @since 23.04.2013
 * 
 */
public class Operation {

	private final Instruction[] instructions;
	private final int size;

	private Operation(final List<Instruction> instructions, final int size) {
		this.instructions = instructions.toArray(new Instruction[instructions
				.size()]);
		this.size = size;
	}

	/**
	 * Returns this operations instructions
	 * 
	 * @return the instructions
	 */
	public Instruction[] getInstructions() {
		return instructions;
	}

	/**
	 * Returns the instruction at index
	 * 
	 * @param index
	 *            index of instruction
	 * @return instruction at index
	 */
	public Instruction getInstruction(final int index) {
		return instructions[index];
	}

	/**
	 * Returns this operations instruction count
	 * 
	 * @return the instruction count
	 */
	public int getInstructionCount() {
		return instructions.length;
	}

	@Override
	public String toString() {
		final String nl = "\n";// System.getProperty("line.separator");
		final StringBuilder sb = new StringBuilder();
		if (instructions != null) {
			for (final Instruction instruction : instructions) {
				sb.append(instruction.toString()).append(nl);
			}
		}
		return sb.toString();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		final Operation operation = (Operation) o;

		if (size != operation.size)
			return false;
		if (!Arrays.equals(instructions, operation.instructions))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = instructions != null ? Arrays.hashCode(instructions) : 0;
		result = 31 * result + size;
		return result;
	}

	/**
	 * <h1>MainBuilder</h1>
	 * <p>
	 * This class provides the builder pattern to create new instances of the
	 * Operation-class.
	 * </p>
	 * 
	 * @author eike
	 * @since 07.05.2013 00:41:08
	 */
	public static class Builder {

		private int size;
		private final List<Instruction> instructions;

		private Builder() {
			instructions = new ArrayList<>();
		}

		/**
		 * Creates a new instance of this builder class
		 * 
		 * @return new builder instance
		 */
		public static Builder newBuilder() {
			return new Builder();
		}

		/**
		 * Adds an instruction to this operation
		 * 
		 * @param instruction
		 *            the instruction to add
		 * @return this builders instance
		 */
		public Builder add(final Instruction instruction) {
			instructions.add(instruction);
			size += instruction.getByteCount();
			return this;
		}

		public Builder add(final Operation operation) {
			Collections.addAll(instructions, operation.getInstructions());
			size += operation.size;
			return this;
		}

		/**
		 * Add a new instruction to this builder instance.
		 * 
		 * @param mnemonic
		 *            bytecode mnemonic
		 * @param arguments
		 *            arguments to be passed along the bytecode instruction
		 * @return instance of this builder
		 */
		public Builder add(final Mnemonic mnemonic, final byte... arguments) {
			final Instruction instruction;
			if (mnemonic.getArgsCount() > 0) {
				assert arguments != null
						&& arguments.length == mnemonic.getArgsCount();
				// instruction = new Instruction(1 + argsSize, mnemonic,
				// arguments);
				instruction = new Instruction(mnemonic, arguments);
			} else {
				// instruction = new Instruction(1, mnemonic);
				instruction = new Instruction(mnemonic);
			}
			return add(instruction);
		}

		/**
		 * Assemble the object.
		 * 
		 * @return new object, created by this builder
		 */
		public Operation build() {
			return new Operation(instructions, size);
		}

	}

}
