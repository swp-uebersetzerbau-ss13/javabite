package swp_compiler_ss13.javabite.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import swp_compiler_ss13.common.backend.Quadruple.Operator;

/**
 * This class represents a list of instructions and maps every tac-operation to
 * their bytecode equivalent. It is used to carry logical groups of
 * instructions.
 * 
 * @author eike
 * @since 23.04.2013
 * 
 */
public class Operation
{

	/**
	 * <h1>OperationBuilder-class</h1>
	 * <p>
	 * This class provides the builder pattern to create new instances of the
	 * Operation-class.
	 * </p>
	 * 
	 * @author eike
	 * @since 07.05.2013 00:41:08
	 * 
	 */
	public static class OperationBuilder
	{

		// private int offset = 0;
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
				final int argsSize, final Byte... arguments) {
			final int size;
			final Instruction instruction;
			if (mnemonic.getArgsCount() > 0) {
				if (arguments == null
						|| arguments.length != mnemonic.getArgsCount()) {
					throw new RuntimeException(
							"unexpected number of arguments: 0, expected "
									+ mnemonic.getArgsCount());
				} else {
					size = 1 + argsSize;
					instruction = new Instruction(size, mnemonic,
							Arrays.asList(arguments));
				}
			} else {
				size = 1;
				instruction = new Instruction(size, mnemonic);
			}
			instructions.add(instruction);
			// offset += size;
			return this;
		}

		public OperationBuilder add(final Mnemonic mnemonic) {
			return add(mnemonic, 0);
		}

		/**
		 * Assemble the object.
		 * 
		 * @return new object, created by this builder
		 */
		public Operation build() {
			return new Operation(instructions);
		}

	}

	private final List<Instruction> instructions;
	private int size;
	private Operator operator;

	private Operation(final List<Instruction> instructions) {
		this.instructions = instructions;
	}

	public List<Instruction> getInstructions() {
		return instructions;
	}

	public int getSize() {
		return size;
	}

	public Operator getOperator() {
		return operator;
	}

	public int getInstructionCount() {
		return instructions.size();
	}

	// public void setOffset(int offset) {
	// for (final Instruction instruction : instructions) {
	// offset += instruction.setOffset(offset);
	// }
	// }

	@Override
	public String toString() {
		final String nl = System.getProperty("line.separator");
		final StringBuilder sb = new StringBuilder();
		for (final Instruction instruction : instructions) {
			sb.append(instruction.toString()).append(nl);
		}
		return sb.toString();
	}

}
