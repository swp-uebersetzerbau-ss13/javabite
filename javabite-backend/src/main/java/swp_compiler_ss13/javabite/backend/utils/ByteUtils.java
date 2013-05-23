package swp_compiler_ss13.javabite.backend.utils;

import java.util.List;

public class ByteUtils {

	public static byte[] shortToByteArray(final short x) {
		return new byte[] { (byte) ((x >> 8) & 0xff), (byte) (x & 0xff) };
	}

	public static String hexFromInt(final byte b) {
		String tmp = Integer.toHexString(((b + 256) % 256));
		if (tmp.length() < 2) {
			tmp = "0" + tmp;
		}

		return tmp;
	}

	public static String hexFromShort(final short input) {
		final StringBuilder sb = new StringBuilder();

		for (int i = 1; i >= 0; i--) {
			final byte b = (byte) (input << (i * 8));
			final String tmp = Integer.toHexString(((b + 256) % 256));
			if (tmp.length() < 2) {
				sb.append(0).append(tmp).append(" ");
			} else {
				sb.append(tmp).append(" ");
			}
		}

		return sb.toString();
	}

	public static String hexFromInt(final int input) {
		final StringBuilder sb = new StringBuilder();

		for (int i = 3; i >= 0; i--) {
			final byte b = (byte) (input << (i * 8));
			final String tmp = Integer.toHexString(((b + 256) % 256));
			if (tmp.length() < 2) {
				sb.append(0).append(tmp).append(" ");
			} else {
				sb.append(tmp).append(" ");
			}
		}

		return sb.toString();
	}

	public static String hexFromBytes(final byte[] bytes) {
		final StringBuilder sb = new StringBuilder();
		for (final byte b : bytes) {
			sb.append(hexFromInt(b)).append(" ");
		}
		return sb.toString();
	}

	public static String hexFromBytes(final List<Byte> bytes) {
		final StringBuilder sb = new StringBuilder();
		for (final Byte b : bytes) {
			sb.append(hexFromInt(b)).append(" ");
		}
		return sb.toString();
	}
}
