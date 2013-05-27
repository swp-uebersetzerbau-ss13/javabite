package swp_compiler_ss13.javabite.backend.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteUtils {

	private static final Pattern HEX_BYTES = Pattern.compile("(.{2})");

	private static final Logger logger = LoggerFactory
			.getLogger(ByteUtils.class);

	/**
	 * TODO javadoc
	 * 
	 * @param x
	 * @return
	 */
	public static byte[] shortToByteArray(final short x) {
		return new byte[] { (byte) ((x >> 8) & 0xff), (byte) (x & 0xff) };
	}

	/**
	 * TODO javadoc
	 * 
	 * @param x
	 * @return
	 */
	public static byte[] longToByteArray(final long x) {
		return new byte[] { (byte) ((x >> 56) & 0xff),
				(byte) ((x >> 48) & 0xff), (byte) ((x >> 40) & 0xff),
				(byte) ((x >> 32) & 0xff), (byte) ((x >> 24) & 0xff),
				(byte) ((x >> 16) & 0xff), (byte) ((x >> 8) & 0xff),
				(byte) (x & 0xff) };
	}

	/**
	 * TODO javadoc
	 * 
	 * @param x
	 * @return
	 */
	public static byte[] doubleToByteArray(final double x) {
		final ByteBuffer byteBuffer = ByteBuffer.allocate(8);
		byteBuffer.putDouble(x);
		return byteBuffer.array();
	}

	private static String splitHexBytes(final String hex) {
		return HEX_BYTES.matcher(hex).replaceAll("$1 ");
	}

	public static String toHexString(final int i) {
		return splitHexBytes(String.format("%08x", Integer.valueOf(i)));
	}

	public static String toHexString(final short i) {
		return splitHexBytes(String.format("%08x", Short.valueOf(i)));
	}

	public static String toHexString(final byte i) {
		return splitHexBytes(String.format("%08x", Byte.valueOf(i)));
	}

	/**
	 * TODO javadoc
	 * 
	 * @param bytes
	 * @return
	 */
	public static String hexFromBytes(final byte[] bytes) {
		final StringBuilder sb = new StringBuilder();
		for (final byte b : bytes) {
			sb.append(toHexString(b)).append(" ");
		}
		return sb.toString();
	}

	/**
	 * TODO javadoc
	 * 
	 * @param bytes
	 * @return
	 */
	public static String hexFromBytes(final Iterable<Byte> bytes) {
		final StringBuilder sb = new StringBuilder();
		for (final Byte b : bytes) {
			sb.append(toHexString(b)).append(" ");
		}
		return sb.toString();
	}

	@SuppressWarnings("unused")
	private void visualizeTargetCode(final Map<String, InputStream> targetCodeIS) {

		for (final String classname : targetCodeIS.keySet()) {

			final StringBuilder sb = new StringBuilder();
			sb.append("Classname : " + classname + "\n");
			sb.append("Content : \n\n");

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
					final String tmp = Integer.toHexString(((b + 256) % 256));
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

}
