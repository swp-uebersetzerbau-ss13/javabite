package swp_compiler_ss13.javabite.backend.temp1;

import swp_compiler_ss13.javabite.backend.Mnemonic;

/**
 * BytecodeVisualizer class The BytecodeVisualizer is essentially the same as
 * BytecodeBuilder, except that is is used explicitly for visualizing the
 * bytecode. That means, that the bytecode is appended directly with the used
 * mnemonics and all used arguments. The result is readable, but not executable.
 * 
 * @author eike
 * @since 21.04.2013 00:59:11
 */
public class BytecodeVisualizer implements BytecodeAppender {

	private final StringBuilder stringBuilder;
	private final String instructionSep;

	public BytecodeVisualizer(final String instructionSep) {
		this.stringBuilder = new StringBuilder();
		this.instructionSep = instructionSep;
	}

	public BytecodeVisualizer() {
		this(System.getProperty("line.separator"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fub.ss13com.whilst.backend.BytecodeAppender#append(de.fub.ss13com.whilst
	 * .backend.Mnemonic)
	 */
	@Override
	public BytecodeAppender append(Mnemonic mnemonic) {
		stringBuilder.append(mnemonic.getMnemonic()).append(instructionSep);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fub.ss13com.whilst.backend.BytecodeAppender#append(de.fub.ss13com.whilst
	 * .backend.Mnemonic, int)
	 */
	@Override
	public BytecodeAppender append(Mnemonic mnemonic, int argument1) {
		stringBuilder.append(mnemonic.getMnemonic()).append(" ").append(argument1)
		    .append(instructionSep);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fub.ss13com.whilst.backend.BytecodeAppender#append(de.fub.ss13com.whilst
	 * .backend.Mnemonic, int, int)
	 */
	@Override
	public BytecodeAppender append(Mnemonic mnemonic, int argument1, int argument2) {
		stringBuilder.append(mnemonic.getMnemonic()).append(" ").append(argument1)
		    .append(" ").append(argument2).append(instructionSep);
		return this;
	}

	@Override
	public String toString() {
		return stringBuilder.toString();
	}

}
