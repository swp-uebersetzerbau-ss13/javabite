package swp_compiler_ss13.javabite.backend;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.byteArrayToHexString;
import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.byteToHexString;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

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
			// TODO: argument count is not necessarily byte count!
			// ex: LDC2_W takes 1 argument of 2 bytes
			// Mnemonic LDC2_W has ArgCount 1, arguments array has length 2
			// fix this?
			assert arguments.length >= mnemonic.getArgsCount();
			this.arguments = arguments;
			size = 1 + arguments.length;
		} else {
			this.arguments = null;
			size = 1;
		}
	}

	/**
	 * Create a new instruction class instance
	 * 
	 * @param size
	 *            byte size
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
				logger.debug("{}", byteToHexString(mnemonic.getBytecode()));
			}

			if (getArguments() != null) {
				for (final Byte b : getArguments()) {
					outputStream.writeByte(b);
				}

				if (logger.isDebugEnabled()) {
					logger.debug("arguments");
					logger.debug("{}", byteArrayToHexString(getArguments()));
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
		return mnemonic.getStackChange();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(arguments);
		result = prime * result + (mnemonic == null ? 0 : mnemonic.hashCode());
		result = prime * result + size;
		result = prime * result + mnemonic.getStackChange();
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
		if (obj == null)
			return false;
		if (!(obj instanceof Instruction))
			return false;
		final Instruction other = (Instruction) obj;
		if (!Arrays.equals(arguments, other.arguments))
			return false;
		if (mnemonic != other.mnemonic)
			return false;
		if (size != other.size)
			return false;
		return true;
	}

}
