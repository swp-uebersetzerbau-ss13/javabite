package swp_compiler_ss13.javabite.backend.translation;

import java.util.List;

/**
 * <h1>Program</h1>
 * <p>
 * Representation of a program instruction block. Contains a list of operations
 * which consist of instructions.
 * </p>
 * 
 * @author eike
 * @since 02.05.2013 23:41:39
 * 
 */
public class Program {

	private final List<Operation> operations;

	public Program(final List<Operation> operations) {
		this.operations = operations;
	}

	/**
	 * Returns this programs instructions.
	 * 
	 * @return the instructions
	 */
	public Instruction[] toInstructionsArray() {
		if (operations == null) {
			return null;
		}
		int icount = 0;
		for (final Operation op : operations) {
			icount += op.getInstructionCount();
		}
		final Instruction[] instructions = new Instruction[icount];
		int currIndex = 0;
		for (final Operation op : operations) {

			System.arraycopy(op.getInstructions(), 0, instructions, currIndex,
					op.getInstructionCount());
			currIndex += op.getInstructionCount();
		}
		return instructions;
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
				+ (operations == null ? 0 : operations.hashCode());
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
		if (!(obj instanceof Program))
			return false;
		final Program other = (Program) obj;
		if (operations == null) {
			if (other.operations != null)
				return false;
		} else if (!operations.equals(other.operations))
			return false;
		return true;
	}

}
