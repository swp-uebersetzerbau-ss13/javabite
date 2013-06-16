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
	private String targetLabel;

	public JumpInstruction(final Mnemonic mnemonic) {
		super(mnemonic);
	}

	public JumpInstruction(final Mnemonic mnemonic,
			final Instruction targetInstruction) {
		super(mnemonic);
		this.targetInstruction = targetInstruction;
	}

	public JumpInstruction(final Mnemonic mnemonic,
			final String targetInstruction) {
		super(mnemonic);
		targetLabel = targetInstruction;
	}

	/**
	 * @return the targetLabel
	 */
	public String getTargetLabel() {
		return targetLabel;
	}

	/**
	 * @param targetLabel
	 *            the targetLabel to set
	 */
	public void setTargetLabel(final String targetLabel) {
		this.targetLabel = targetLabel;
	}

	/**
	 * @return the targetInstruction
	 */
	public Instruction getTargetInstruction() {
		return targetInstruction;
	}

	/**
	 * @param targetInstruction
	 *            the targetInstruction to set
	 */
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
