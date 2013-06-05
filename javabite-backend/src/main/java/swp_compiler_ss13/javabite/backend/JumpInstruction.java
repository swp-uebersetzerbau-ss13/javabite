package swp_compiler_ss13.javabite.backend;

/**
 * Extension of Instruction-class. Instances of this class symbolize a jump
 * instruction to the target instruction. Because jumps can be both forward and
 * backward, the offsets are only calculated after completing a program.
 * 
 * @author eike
 * @since May 30, 2013 6:51:26 PM
 */
public class JumpInstruction extends Instruction {

	private Instruction targetInstruction;

	public JumpInstruction(final Mnemonic mnemonic, final byte... arguments) {
		super(mnemonic, arguments);
	}

	public JumpInstruction(final Mnemonic mnemonic,
			final Instruction targetInstruction, final byte... arguments) {
		super(mnemonic, arguments);
		this.targetInstruction = targetInstruction;
	}

	public JumpInstruction(final Mnemonic mnemonic) {
		super(mnemonic);
	}

	public JumpInstruction(final Mnemonic mnemonic,
			final Instruction targetInstruction) {
		super(mnemonic);
		this.targetInstruction = targetInstruction;
	}

	public Instruction getTargetInstruction() {
		return targetInstruction;
	}

	public void setTargetInstruction(final Instruction targetInstruction) {
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
				+ (targetInstruction == null ? 0 : targetInstruction.hashCode());
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
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof JumpInstruction))
			return false;
		final JumpInstruction other = (JumpInstruction) obj;
		if (targetInstruction == null) {
			if (other.targetInstruction != null)
				return false;
		} else if (!targetInstruction.equals(other.targetInstruction))
			return false;
		return true;
	}

}
