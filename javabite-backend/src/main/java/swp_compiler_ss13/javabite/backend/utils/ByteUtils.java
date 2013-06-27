package swp_compiler_ss13.javabite.backend.utils;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.regex.Pattern;

public final class ByteUtils {

	private static final Pattern P_HEX_BYTES = Pattern.compile("(.{2})");

	private static final Logger logger = LoggerFactory
			.getLogger(ByteUtils.class);

	/**
	 * splits a short value into a byte array. Array starts with higher bytes.
	 * 
	 * @param x
	 *            short to split into array
	 * @return byte array
	 */
	public static byte[] shortToByteArray(final short x) {
		return new byte[] { (byte) (x >> 8 & 0xff), (byte) (x & 0xff) };
	}

	/**
	 * splits an int value into a byte array. Array starts with higher bytes.
	 * 
	 * @param x
	 *            int to split into array
	 * @return byte array
	 */
	public static byte[] intToByteArray(final int x) {
		return new byte[] { (byte) (x >> 24 & 0xff), (byte) (x >> 16 & 0xff),
				(byte) (x >> 8 & 0xff), (byte) (x & 0xff) };
	}

	/**
	 * splits a long value into a byte array. Array starts with higher bytes.
	 * 
	 * @param x
	 *            long to split into array
	 * @return byte array
	 */
	public static byte[] longToByteArray(final long x) {
		return new byte[] { (byte) (x >> 56 & 0xff), (byte) (x >> 48 & 0xff),
				(byte) (x >> 40 & 0xff), (byte) (x >> 32 & 0xff),
				(byte) (x >> 24 & 0xff), (byte) (x >> 16 & 0xff),
				(byte) (x >> 8 & 0xff), (byte) (x & 0xff) };
	}

	/**
	 * splits a double value into a byte array. Array starts with higher bytes.
	 * 
	 * @param x
	 *            double to split into array
	 * @return byte array
	 */
	public static byte[] doubleToByteArray(final double x) {
		final ByteBuffer byteBuffer = ByteBuffer.allocate(8);
		byteBuffer.putDouble(x);
		return byteBuffer.array();
	}

	/**
	 * Split a hexadecimal representation of bytes into columns of 2 (eg. 1
	 * byte)
	 * 
	 * @param hex
	 *            hexadecimal string of bytes
	 * @return hexadecimal string with bytes splitted into columns of 2
	 */
	private static String splitHexBytes(final String hex) {
		return P_HEX_BYTES.matcher(hex).replaceAll("$1 ");
	}

	/**
	 * Returns a hexadecimal string representation of an integer value.
	 * 
	 * @param i
	 *            integer to turn into a hex string
	 * @return hex string
	 */
	public static String intToHexString(final int i) {
		return splitHexBytes(String.format("%08x", i));
	}

	/**
	 * Returns a hexadecimal string representation of a short value.
	 * 
	 * @param i
	 *            short to turn into a hex string
	 * @return hex string
	 */
	public static String shortToHexString(final short i) {
		return splitHexBytes(String.format("%04x", i));
	}

	/**
	 * Returns a hexadecimal string representation of a byte value.
	 * 
	 * @param i
	 *            byte to turn into a hex string
	 * @return hex string
	 */
	public static String byteToHexString(final byte i) {
		return String.format("%02x", i);
	}

	/**
	 * Returns a hexadecimal string representation of a byte array.
	 * 
	 * @param bytes
	 *            byte array to turn into a hex string
	 * @return hex string
	 */
	public static String byteArrayToHexString(final byte[] bytes) {
		final StringBuilder sb = new StringBuilder();
		for (final byte b : bytes) {
			sb.append(byteToHexString(b)).append(" ");
		}
		return sb.toString();
	}

	/**
	 * Returns a hexadecimal string representation of a byte iterable.
	 * 
	 * @param bytes
	 *            byte iterable to turn into a hex string
	 * @return hex string
	 */
	public static String byteIterableToHexString(final Iterable<Byte> bytes) {
		final StringBuilder sb = new StringBuilder();
		for (final Byte b : bytes) {
			sb.append(byteToHexString(b)).append(" ");
		}
		return sb.toString();
	}

	@SuppressWarnings("unused")
	private void visualizeTargetCode(final Map<String, InputStream> targetCodeIS) {

		for (final String classname : targetCodeIS.keySet()) {

			final StringBuilder sb = new StringBuilder();
			sb.append("Classname : ").append(classname).append("\n")
					.append("Content : \n\n");

			final ByteArrayInputStream is = (ByteArrayInputStream) targetCodeIS
					.get(classname);
			final DataInputStream dis = new DataInputStream(is);

			final File testFile = new File("Program.class");
			FileOutputStream outStream;

			try {
				outStream = new FileOutputStream(testFile);
				IOUtils.copy(dis, outStream);
			} catch (final Exception e) {
				e.printStackTrace();
			}

			try {
				logger.info("Classfile written to: "
						+ testFile.getCanonicalPath());
			} catch (final IOException e1) {
				e1.printStackTrace();
			}

			int i = 0;
			byte b;
			try {
				while ((b = (byte) dis.read()) != -1) {
					final String tmp = Integer.toHexString((b + 256) % 256);
					if (tmp.length() < 2) {
						sb.append(0).append(tmp).append(" ");
					} else {
						sb.append(tmp).append(" ");
					}

					i++;
					if (i == 16) {
						sb.append("\n");
						i = 0;
					}
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
			System.out.println(sb.toString());
		}
	}

	private ByteUtils() {
	}

}
