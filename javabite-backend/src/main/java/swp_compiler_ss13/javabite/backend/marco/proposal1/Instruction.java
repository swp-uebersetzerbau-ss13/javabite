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

	/**
	 * getBytes function. This function creates a Byte-List of all the
	 * information meeting the JVM-instruction standard.
	 * 
	 * @author Marco
	 * @since 03.05.2013
	 * 
	 */
	public ArrayList<Byte> getBytes() {
		ArrayList<Byte> instructionBytes = new ArrayList<Byte>();
		
		// get op byte
		instructionBytes.add(this.mnemonic.getBytecode());
		// get arguments' bytes
		if (this.getArguments() != null) {
			instructionBytes.addAll(this.getArguments());
		}
		
		return instructionBytes;
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
