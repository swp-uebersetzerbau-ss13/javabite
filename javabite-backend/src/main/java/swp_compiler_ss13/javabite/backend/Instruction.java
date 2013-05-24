package swp_compiler_ss13.javabite.backend;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.hexFromBytes;
import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.hexFromByte;

import java.io.DataOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h1>Instruction</h1>
 * <p>
 * This class represents a bytecode instruction, with its opcode and arguments.
 * </p>
 * 
 * @author eike
 * @since 23.04.2013
 * 
 */
public class Instruction {

	private static final Logger logger = LoggerFactory
			.getLogger(Instruction.class);

	// offset to predecessor
	private Mnemonic mnemonic;
	private byte[] arguments;
	private final int size;
	private final short stackChange;

	/**
	 * Create a new instruction class instance
	 * 
	 * @param size
	 *            byte size
	 * @param mnemonic
	 *            mnemonic of opcode
	 * @param arguments
	 *            arguments for opcode
	 */
	public Instruction(final int size, final Mnemonic mnemonic,
			final byte... arguments) {
		this.size = size;
		this.mnemonic = mnemonic;
		this.arguments = arguments;
		this.stackChange = this.mnemonic.getStackChange();
	}

	/**
	 * Create a new instruction class instance
	 * 
	 * @param size
	 *            byte size
	 * @param mnemonic
	 *            mnemonic of opcode
	 */
	public Instruction(final int size, final Mnemonic mnemonic) {
		this(size, mnemonic, null);
	}

	/**
	 * Write this instructions bytes to an output stream
	 * 
	 * @param outputStream
	 *            the output stream to use for writing
	 */
	public void writeTo(final DataOutputStream outputStream) {
		try {
			outputStream.writeByte(this.mnemonic.getBytecode());

			if (logger.isDebugEnabled()) {
				logger.debug("mnemonic bcode");
				logger.debug("{}", hexFromByte(this.mnemonic.getBytecode()));
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

	/**
	 * Returns this instructions mnemonic
	 * 
	 * @return the mnemonic
	 */
	public Mnemonic getMnemonic() {
		return mnemonic;
	}

	public void setMnemonic(final Mnemonic mnemonic) {
		this.mnemonic = mnemonic;
	}

	/**
	 * Returns this instructions arguments
	 * 
	 * @return the arguments
	 */
	public byte[] getArguments() {
		return arguments;
	}

	public void setArguments(final byte[] arguments) {
		this.arguments = arguments;
	}

	/**
	 * Returns this instructions size (number of bytes)
	 * 
	 * @return the byte count
	 */
	public int getByteCount() {
		return size;
	}

	/**
	 * Returns a byte array of this instruction
	 * 
	 * @return the byte array
	 */
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

	/**
	 * Returns this instructions stack change value
	 * 
	 * @return the stack change value
	 */
	public short getStackChange() {
		return stackChange;
	}

}
