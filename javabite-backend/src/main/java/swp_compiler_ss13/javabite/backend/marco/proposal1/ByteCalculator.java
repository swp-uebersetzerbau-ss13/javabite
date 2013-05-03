package swp_compiler_ss13.javabite.backend.marco.proposal1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * ByteCalculator class.
 * 
 * @author Marco
 * @since 28.04.2013
 * 
 */
public class ByteCalculator
{

	/**
	 * shortToByteArrayList function.
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * 
	 */
	public static ArrayList<Byte> shortToByteArrayList(short value) {
		ArrayList<Byte> byteArrayList = new ArrayList<Byte>();

		ByteBuffer buf = ByteBuffer.allocate(2);
		buf.order(ByteOrder.BIG_ENDIAN);
		buf.putShort(value);
		byte[] bufArray = buf.array();

		for (int i = 0; i < 2; i++) {
			byteArrayList.add(bufArray[i]);
		}

		return byteArrayList;
	}

	/**
	 * intToByteArrayList function.
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * 
	 */
	public static ArrayList<Byte> intToByteArrayList(int value) {
		ArrayList<Byte> byteArrayList = new ArrayList<Byte>();

		ByteBuffer buf = ByteBuffer.allocate(4);
		buf.order(ByteOrder.BIG_ENDIAN);
		buf.putInt(value);
		byte[] bufArray = buf.array();

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
	public static ArrayList<Byte> longToByteArrayList(long value) {
		ArrayList<Byte> byteArrayList = new ArrayList<Byte>();

		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.order(ByteOrder.BIG_ENDIAN);
		buf.putLong(value);
		byte[] bufArray = buf.array();

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
	public static ArrayList<Byte> doubleToByteArrayList(double value) {
		ArrayList<Byte> byteArrayList = new ArrayList<Byte>();

		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.order(ByteOrder.BIG_ENDIAN);
		buf.putDouble(value);
		byte[] bufArray = buf.array();

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
	public static ArrayList<Byte> stringToByteArrayList(String value) {
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
	public static ArrayList<Byte> byteArrayToByteArrayList(byte[] valueArray) {
		ArrayList<Byte> byteArrayList = new ArrayList<Byte>();

		for (int i = 0; i < valueArray.length; i++) {
			byteArrayList.add(valueArray[i]);
		}

		return byteArrayList;
	}

}
