package swp_compiler_ss13.javabite.backend.marco.proposal1;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import swp_compiler_ss13.javabite.backend.Mnemonic;
import swp_compiler_ss13.javabite.backend.Quadruple.Operator;
import swp_compiler_ss13.javabite.backend.temp3.Instruction;

public class Operation
{

	public static class OperationBuilder
	{

		private final List<Instruction> instructions;

		private OperationBuilder() {
			this.instructions = new ArrayList<>();
		}

		public static OperationBuilder newBuilder() {
			return new OperationBuilder();
		}

		public OperationBuilder add(final Instruction instruction) {
			instructions.add(instruction);
			return this;
		}

		public OperationBuilder add(final int size, final int offset,
				@Nonnull final Mnemonic mnemonic, Object... arguments) {
			instructions
					.add(new Instruction(size, offset, mnemonic, arguments));
			return this;
		}

		public Operation build() {
			return new Operation(instructions);
		}

	}

	private List<Instruction> instructions;
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

	public void setOffset(int offset) {
		for (final Instruction instruction : instructions) {
			offset += instruction.setOffset(offset);
		}
	}

}
