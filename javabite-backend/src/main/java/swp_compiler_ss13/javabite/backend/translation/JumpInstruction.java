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
		targetLabel = null;
	}

	public JumpInstruction(final Mnemonic mnemonic, final String targetLabel) {
		super(mnemonic);
		targetInstruction = null;
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

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;

		final JumpInstruction that = (JumpInstruction) o;

		if (targetInstruction != null ? !targetInstruction
				.equals(that.targetInstruction)
				: that.targetInstruction != null)
			return false;
		if (targetLabel != null ? !targetLabel.equals(that.targetLabel)
				: that.targetLabel != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31
				* result
				+ (targetInstruction != null ? targetInstruction.hashCode() : 0);
		result = 31 * result
				+ (targetLabel != null ? targetLabel.hashCode() : 0);
		return result;
	}

}
