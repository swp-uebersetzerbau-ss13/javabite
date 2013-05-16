package swp_compiler_ss13.javabite.backend.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * ByteCalculator class.
 * 
 * @author Marco
 * @since 28.04.2013
 * 
 */
public class ByteCalculator {

	/**
	 * shortToByteArrayList function.
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * 
	 */
	public static List<Byte> shortToByteList(final short value) {
		final DataOutputStream dos = new DataOutputStream(
				new ByteArrayOutputStream());

		try {
			dos.writeShort(value);
		} catch (final IOException e) {
			e.printStackTrace();
		}

		final List<Byte> byteList = new ArrayList<>();
		final ByteBuffer byteBuffer = ByteBuffer.allocate(2);

		byteBuffer.putShort(value);

		for (final byte b : byteBuffer.array()) {
			byteList.add(b);
		}

		return byteList;
	}

	/**
	 * intToByteArrayList function.
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * 
	 */
	public static ArrayList<Byte> intToByteArrayList(final int value) {
		final ArrayList<Byte> byteArrayList = new ArrayList<Byte>();

		final ByteBuffer buf = ByteBuffer.allocate(4);
		buf.order(ByteOrder.BIG_ENDIAN);
		buf.putInt(value);
		final byte[] bufArray = buf.array();

		for (int i = 0; i < 4; i++) {
			byteArrayList.add(bufArray[i]);
		}

		return byteArrayList;
	}

	/**
	 * longToByteArrayList function.
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * 
	 */
	public static ArrayList<Byte> longToByteArrayList(final long value) {
		final ArrayList<Byte> byteArrayList = new ArrayList<Byte>();

		final ByteBuffer buf = ByteBuffer.allocate(8);
		buf.order(ByteOrder.BIG_ENDIAN);
		buf.putLong(value);
		final byte[] bufArray = buf.array();

		for (int i = 0; i < 8; i++) {
			byteArrayList.add(bufArray[i]);
		}

		return byteArrayList;
	}

	/**
	 * longToByteArrayList function.
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * 
	 */
	public static ArrayList<Byte> doubleToByteArrayList(final double value) {
		final ArrayList<Byte> byteArrayList = new ArrayList<Byte>();

		final ByteBuffer buf = ByteBuffer.allocate(8);
		buf.order(ByteOrder.BIG_ENDIAN);
		buf.putDouble(value);
		final byte[] bufArray = buf.array();

		for (int i = 0; i < 8; i++) {
			byteArrayList.add(bufArray[i]);
		}

		return byteArrayList;
	}

	/**
	 * longToByteArrayList function.
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * 
	 */
	public static ArrayList<Byte> stringToByteArrayList(final String value) {
		// TODO: make implementation from String to byte
		return null;
	}

	/**
	 * byteArrayToByteArrayList function.
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * 
	 */
	public static ArrayList<Byte> byteArrayToByteArrayList(
			final byte[] valueArray) {
		final ArrayList<Byte> byteArrayList = new ArrayList<Byte>();

		for (int i = 0; i < valueArray.length; i++) {
			byteArrayList.add(valueArray[i]);
		}

		return byteArrayList;
	}

}
