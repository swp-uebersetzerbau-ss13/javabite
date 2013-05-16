package swp_compiler_ss13.javabite.backend;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.hexFromBytes;
import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.hexFromInt;

import java.io.DataOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a bytecode instruction, with its opcode and arguments.
 * 
 * @author eike
 * @since 23.04.2013
 * 
 */
public class Instruction {

	private static final Logger logger = LoggerFactory
			.getLogger(Instruction.class);

	// offset to predecessor
	// private int offset;
	private Mnemonic mnemonic;
	private byte[] arguments;
	private final int size;
	private final short stackChange;

	public Instruction(final int size, final Mnemonic mnemonic,
			final byte... arguments) {
		this.size = size;
		this.mnemonic = mnemonic;
		this.arguments = arguments;
		this.stackChange = this.mnemonic.getStackChange();
	}

	public Instruction(final int size, final Mnemonic mnemonic) {
		this(size, mnemonic, null);
	}

	/**
	 * getBytes function. This function creates a Byte-List of all the
	 * information meeting the JVM-instruction standard.
	 * 
	 * @author Marco
	 * @since 03.05.2013
	 * 
	 */
	// public ArrayList<Byte> getBytess() {
	// final ArrayList<Byte> instructionBytes = new ArrayList<Byte>();
	//
	// // get op byte
	// instructionBytes.add(this.mnemonic.getBytecode());
	// // get arguments' bytes
	// if (this.getArguments() != null) {
	// instructionBytes.addAll(this.getArguments());
	// }
	//
	// return instructionBytes;
	// }

	public void writeTo(final DataOutputStream outputStream) {
		try {
			outputStream.writeByte(this.mnemonic.getBytecode());

			if (logger.isDebugEnabled()) {
				logger.debug("mnemonic bcode");
				logger.debug("{}", hexFromInt(this.mnemonic.getBytecode()));
			}

			if (this.getArguments() != null) {
				for (final Byte b : this.getArguments()) {
					outputStream.writeByte(b);
				}

				if (logger.isDebugEnabled()) {
					logger.debug("arguments");
					logger.debug("{}", hexFromBytes(this.getArguments()));
				}
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}

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

	public void setMnemonic(final Mnemonic mnemonic) {
		this.mnemonic = mnemonic;
	}

	public byte[] getArguments() {
		return arguments;
	}

	public void setArguments(final byte[] arguments) {
		this.arguments = arguments;
	}

	public int getByteCount() {
		return size;
	}

	public byte[] toByteArray() {
		final byte[] bytes = new byte[size];
		bytes[0] = mnemonic.getBytecode();
		if (arguments != null) {
			System.arraycopy(arguments, 0, bytes, 1, arguments.length);
		}
		return bytes;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(mnemonic.name());
		if (arguments != null) {
			for (final byte b : arguments) {
				sb.append(" ").append(String.format("%02X", b));
			}
		}
		return sb.toString();
	}

	public short getStackChange() {
		return stackChange;
	}

}
