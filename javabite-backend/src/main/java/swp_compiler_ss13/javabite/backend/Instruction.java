package swp_compiler_ss13.javabite.backend;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.hexFromBytes;
import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.hexFromInt;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a bytecode instruction, with its opcode and arguments.
 * 
 * @author eike
 * @since 23.04.2013
 * 
 */
public class Instruction
{

	private static final Logger logger = LoggerFactory
			.getLogger(Instruction.class);

	// offset to predecessor
	// private int offset;
	private Mnemonic mnemonic;
	private List<Byte> arguments;
	private final int size;

	public Instruction(final int size, final Mnemonic mnemonic,
			final List<Byte> arguments) {
		this.size = size;
		this.mnemonic = mnemonic;
		this.arguments = arguments;
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
	public ArrayList<Byte> getBytes() {
		final ArrayList<Byte> instructionBytes = new ArrayList<Byte>();

		// get op byte
		instructionBytes.add(this.mnemonic.getBytecode());
		// get arguments' bytes
		if (this.getArguments() != null) {
			instructionBytes.addAll(this.getArguments());
		}

		return instructionBytes;
	}

	public void writeTo(final DataOutputStream outputStream) {
		try {
			outputStream.writeByte(this.mnemonic.getBytecode());

			if (logger.isDebugEnabled()) {
				logger.info("mnemonic bcode");
				logger.info("{}", hexFromInt(this.mnemonic.getBytecode()));
			}

			if (this.getArguments() != null) {
				for (final Byte b : this.getArguments()) {
					outputStream.writeByte(b);
				}

				if (logger.isDebugEnabled()) {
					logger.info("arguments");
					logger.info("{}", hexFromBytes(this.getArguments()));
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

	public List<Byte> getArguments() {
		return arguments;
	}

	public void setArguments(final List<Byte> arguments) {
		this.arguments = arguments;
	}

	public int getSize() {
		return size;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(mnemonic.name());
		for (final Byte b : arguments) {
			sb.append(" ").append(b);
		}
		return sb.toString();
	}

}
