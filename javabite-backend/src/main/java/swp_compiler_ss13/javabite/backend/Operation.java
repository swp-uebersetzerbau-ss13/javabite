package swp_compiler_ss13.javabite.backend;

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
public class Operation
{

	/**
	 * <h1>OperationBuilder</h1>
	 * <p>
	 * This class provides the builder pattern to create new instances of the
	 * Operation-class.
	 * </p>
	 * 
	 * @author eike
	 * @since 07.05.2013 00:41:08
	 */
	public static class OperationBuilder
	{

		private int size;
		private final List<Instruction> instructions;

		private OperationBuilder() {
			this.instructions = new ArrayList<>();
		}

		/**
		 * Creates a new instance of this builder class
		 * 
		 * @return new builder instance
		 */
		public static OperationBuilder newBuilder() {
			return new OperationBuilder();
		}

		/**
		 * Add a new instruction to this builder instance.
		 * 
		 * @param mnemonic
		 *            bytecode mnemonic
		 * @param argsSize
		 *            byte size of passed arguments, if needed by opcode
		 * @param arguments
		 *            arguments to be passed along the bytecode instruction
		 * @return instance of this builder
		 */
		public OperationBuilder add(final Mnemonic mnemonic,
				final int argsSize, final byte... arguments) {
			final int size;
			final Instruction instruction;
			if (mnemonic.getArgsCount() > 0) {
				assert arguments != null
						&& arguments.length == mnemonic.getArgsCount();
				size = 1 + argsSize;
				instruction = new Instruction(size, mnemonic, arguments);
			} else {
				size = 1;
				instruction = new Instruction(size, mnemonic);
			}
			instructions.add(instruction);
			this.size += size;
			return this;
		}

		/**
		 * Adds a new instruction to this operation with only a mnemonic and no
		 * arguments.
		 * 
		 * @param mnemonic
		 *            the mnemonic of the instruction to add
		 * @return this operation builder instance
		 */
		public OperationBuilder add(final Mnemonic mnemonic) {
			return add(mnemonic, 0);
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

	private final List<Instruction> instructions;
	private final int size;
	private Operator operator;

	private Operation(final List<Instruction> instructions, final int size) {
		this.instructions = instructions;
		this.size = size;
	}

	/**
	 * Returns this operations instructions
	 * 
	 * @return the instructions
	 */
	public List<Instruction> getInstructions() {
		return instructions;
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
		return instructions.size();
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
		final String nl = System.getProperty("line.separator");
		final StringBuilder sb = new StringBuilder();
		if (instructions != null) {
			for (final Instruction instruction : instructions) {
				sb.append(instruction.toString()).append(nl);
			}
		}
		return sb.toString();
	}

}
