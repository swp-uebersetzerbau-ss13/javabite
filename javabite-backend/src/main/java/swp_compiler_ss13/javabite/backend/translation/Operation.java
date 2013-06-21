package swp_compiler_ss13.javabite.backend.translation;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.common.backend.Quadruple.Operator;

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
	private Operator operator;

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
	 * Returns this operations operator
	 * 
	 * @return the operator
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * Returns this operations instruction count
	 * 
	 * @return the instruction count
	 */
	public int getInstructionCount() {
		return instructions.length;
	}

	/**
	 * Returns this operations byte count
	 * 
	 * @return the byte count
	 */
	public int getByteCount() {
		return size;
	}

	/**
	 * Returns this operation as a byte array
	 * 
	 * @return the byte array
	 */
	public byte[] toByteArray() {
		final ByteBuffer bb = ByteBuffer.allocate(size);
		if (instructions != null) {
			for (final Instruction instruction : instructions) {
				bb.put(instruction.toByteArray());
			}
		}
		return bb.array();
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
				+ (instructions == null ? 0 : instructions.hashCode());
		result = prime * result + (operator == null ? 0 : operator.hashCode());
		result = prime * result + size;
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
		if (!(obj instanceof Operation))
			return false;
		final Operation other = (Operation) obj;
		if (instructions == null) {
			if (other.instructions != null)
				return false;
		} else if (!instructions.equals(other.instructions))
			return false;
		if (operator != other.operator)
			return false;
		if (size != other.size)
			return false;
		return true;
	}

	/**
	 * <h1>Builder</h1>
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
			final Operation op = new Operation(instructions, size);
			return op;
		}

	}

}
