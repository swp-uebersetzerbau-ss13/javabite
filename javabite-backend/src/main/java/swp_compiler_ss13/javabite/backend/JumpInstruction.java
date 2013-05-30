package swp_compiler_ss13.javabite.backend;

public class JumpInstruction extends Instruction {

	private Instruction targetInstruction;

	public JumpInstruction(final int size, final Mnemonic mnemonic,
			final Instruction targetInstruction, final byte... arguments) {
		super(size, mnemonic, arguments);
		this.targetInstruction = targetInstruction;
	}

	public JumpInstruction(final int size, final Mnemonic mnemonic,
			final byte... arguments) {
		super(size, mnemonic, arguments);
	}

	public JumpInstruction(int size, Mnemonic mnemonic) {
		super(size, mnemonic);
	}

	public Instruction getTargetInstruction() {
		return targetInstruction;
	}

	public void setTargetInstruction(Instruction targetInstruction) {
		this.targetInstruction = targetInstruction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((targetInstruction == null) ? 0 : targetInstruction
						.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof JumpInstruction))
			return false;
		JumpInstruction other = (JumpInstruction) obj;
		if (targetInstruction == null) {
			if (other.targetInstruction != null)
				return false;
		} else if (!targetInstruction.equals(other.targetInstruction))
			return false;
		return true;
	}

}
