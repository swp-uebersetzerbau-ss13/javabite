package swp_compiler_ss13.javabite.backend.marco.proposal1;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

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
	public static List<Byte> shortToByteList(short value) {
		DataOutputStream dos = new DataOutputStream(new ByteArrayOutputStream());
		
		try {
			dos.writeShort(value);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<Byte> byteList = new ArrayList<>();
		ByteBuffer byteBuffer = ByteBuffer.allocate(2);
		
		byteBuffer.putShort(value);
		
		for(byte b : byteBuffer.array()) {
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
