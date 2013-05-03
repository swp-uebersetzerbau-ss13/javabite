package swp_compiler_ss13.javabite.backend.marco.proposal1;

import java.util.ArrayList;

public class Instruction
{

	// offset to predecessor
	// private int offset;
	private Mnemonic mnemonic;
	private ArrayList<Byte> arguments;
	private final int size;

	public Instruction(final int size, final Mnemonic mnemonic,
			ArrayList<Byte> arguments) {
		this.mnemonic = mnemonic;
		this.arguments = arguments;
		this.size = size;
	}

	/*
	 * TODO for MS2 public int getOffset() { return offset; }
	 * 
	 * public int setOffset(int offset) { this.offset = offset; return offset +
	 * size; }
	 */

	public Mnemonic getMnemonic() {
		return mnemonic;
	}

	public void setMnemonic(Mnemonic mnemonic) {
		this.mnemonic = mnemonic;
	}

	public ArrayList<Byte> getArguments() {
		return arguments;
	}

	public void setArguments(ArrayList<Byte> arguments) {
		this.arguments = arguments;
	}

	public int getSize() {
		return size;
	}

}
