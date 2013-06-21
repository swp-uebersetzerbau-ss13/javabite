package swp_compiler_ss13.javabite.backend.translation;

/**
 * Extension of Instruction-class. Instances of this class symbolize a jump
 * instruction to the target instruction. Because jumps can be both forward and
 * backward, the offsets are only calculated after completing a program.
 * 
 * @author eike
 * @since May 30, 2013 6:51:26 PM
 */
public class JumpInstruction extends Instruction {

	private final Instruction targetInstruction;
	private final String targetLabel;

	public JumpInstruction(final Mnemonic mnemonic,
			final Instruction targetInstruction) {
		super(mnemonic);
		this.targetInstruction = targetInstruction;
		this.targetLabel = null;
	}

	public JumpInstruction(final Mnemonic mnemonic, final String targetLabel) {
		super(mnemonic);
		this.targetInstruction = null;
		this.targetLabel = targetLabel;
	}

	/**
	 * @return the targetLabel
	 */
	public String getTargetLabel() {
		return targetLabel;
	}

	/**
	 * @return the targetInstruction
	 */
	public Instruction getTargetInstruction() {
		return targetInstruction;
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
		result = prime * result
				+ (targetLabel == null ? 0 : targetLabel.hashCode());
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
		if (targetLabel == null) {
			if (other.targetLabel != null)
				return false;
		} else if (!targetLabel.equals(other.targetLabel))
			return false;
		return true;
	}

}
