package swp_compiler_ss13.javabite.backend.temp3;

import javax.annotation.Nonnull;

import swp_compiler_ss13.javabite.backend.Mnemonic;

public class Instruction {

	// offset to predecessor
	private int offset;
	private Mnemonic mnemonic;
	private Object[] arguments;
	private final int size;

	public Instruction(final int size, final int offset,
	    @Nonnull final Mnemonic mnemonic, Object... arguments) {
		this.offset = offset;
		this.mnemonic = mnemonic;
		this.arguments = arguments;
		this.size = size;
	}

	public int getOffset() {
		return offset;
	}

	public int setOffset(int offset) {
		this.offset = offset;
		return offset + size;
	}

	public Mnemonic getMnemonic() {
		return mnemonic;
	}

	public void setMnemonic(Mnemonic mnemonic) {
		this.mnemonic = mnemonic;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	public int getSize() {
		return size;
	}

}
