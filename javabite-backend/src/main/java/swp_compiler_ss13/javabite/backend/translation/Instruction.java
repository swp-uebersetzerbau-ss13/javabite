package swp_compiler_ss13.javabite.backend.translation;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.javabite.backend.utils.ByteUtils;

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

	private int byteCount;
	private Mnemonic mnemonic;
	private byte[] arguments;
	private int offset;

	/**
	 * Create a new instruction class instance
	 * 
	 * @param mnemonic
	 *            mnemonic of opcode
	 * @param arguments
	 *            arguments for opcode
	 */
	public Instruction(final Mnemonic mnemonic, final byte... arguments) {
		this.mnemonic = mnemonic;
		if (mnemonic.getArgsCount() > 0) {
			this.arguments = arguments;
		} else {
			this.arguments = null;
		}
		byteCount = 1 + mnemonic.getArgsCount();
	}

	/**
	 * Create a new instruction class instance
	 * 
	 * @param mnemonic
	 *            mnemonic of opcode
	 */
	public Instruction(final Mnemonic mnemonic) {
		this(mnemonic, null);
	}

	/**
	 * Write this instructions bytes to an output stream
	 * 
	 * @param outputStream
	 *            the output stream to use for writing
	 */
	public void writeTo(final DataOutputStream outputStream) {
		try {
			outputStream.writeByte(mnemonic.getBytecode());

			if (logger.isDebugEnabled()) {
				logger.debug("mnemonic bcode");
				logger.debug("{}",
						ByteUtils.byteToHexString(mnemonic.getBytecode()));
			}

			if (getArguments() != null) {
				for (final Byte b : getArguments()) {
					outputStream.writeByte(b);
				}

				if (logger.isDebugEnabled()) {
					logger.debug("arguments");
					logger.debug("{}",
							ByteUtils.byteArrayToHexString(getArguments()));
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
	@SuppressWarnings("unused")
	public Mnemonic getMnemonic() {
		return mnemonic;
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
		return byteCount;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset
	 *            the offset to set
	 */
	public void setOffset(final int offset) {
		this.offset = offset;
	}

	/**
	 * @param mnemonic
	 *            the mnemonic to set
	 */
	public void setMnemonic(final Mnemonic mnemonic) {
		this.mnemonic = mnemonic;
		byteCount = 1 + mnemonic.getArgsCount();
	}

	/**
	 * Returns a byte array of this instruction
	 * 
	 * @return the byte array
	 */
	public byte[] toByteArray() {
		final byte[] bytes = new byte[byteCount];
		bytes[0] = mnemonic.getBytecode();
		if (arguments != null) {
			System.arraycopy(arguments, 0, bytes, 1, arguments.length);
		}
		return bytes;
	}

	/**
	 * Returns this instructions stack change value
	 * 
	 * @return the stack change value
	 */
	public short getStackChange() {
		return mnemonic.getStackChange();
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

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		final Instruction that = (Instruction) o;

		if (byteCount != that.byteCount)
			return false;
		if (offset != that.offset)
			return false;
		if (!Arrays.equals(arguments, that.arguments))
			return false;
		if (mnemonic != that.mnemonic)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = byteCount;
		result = 31 * result + (mnemonic != null ? mnemonic.hashCode() : 0);
		result = 31 * result
				+ (arguments != null ? Arrays.hashCode(arguments) : 0);
		result = 31 * result + offset;
		return result;
	}

}
