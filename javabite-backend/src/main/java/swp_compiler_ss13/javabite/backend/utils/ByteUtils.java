package swp_compiler_ss13.javabite.backend.utils;

import java.util.List;

public class ByteUtils {
	
	public static String hexFromInt(byte b) {
		String tmp = Integer.toHexString(((b + 256) % 256));
		if (tmp.length() < 2) {
			tmp = "0" + tmp;
		}
		
		return tmp;
	}
	
	public static String hexFromShort(short input) {
		StringBuilder sb = new StringBuilder();
		
		for(int i = 1; i >= 0;i--) {
			byte b = (byte) (input << (i*8));
			String tmp = Integer.toHexString(((b + 256) % 256));
			if (tmp.length() < 2) {
				sb.append(0).append(tmp).append(" ");
			} else {
				sb.append(tmp).append(" ");
			}
		}
		
		return sb.toString();
	}
	
	public static String hexFromInt(int input) {
		StringBuilder sb = new StringBuilder();
		
		for(int i = 3; i >= 0;i--) {
			byte b = (byte) (input << (i*8));
			String tmp = Integer.toHexString(((b + 256) % 256));
			if (tmp.length() < 2) {
				sb.append(0).append(tmp).append(" ");
			} else {
				sb.append(tmp).append(" ");
			}
		}
		
		return sb.toString();
	}
	
	public static String hexFromBytes(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for(byte b : bytes) {
			sb.append(hexFromInt(b)).append(" ");
		}
		return sb.toString();
	}
	
	public static String hexFromBytes(List<Byte> bytes) {
		StringBuilder sb = new StringBuilder();
		for(Byte b : bytes) {
			sb.append(hexFromInt(b)).append(" ");
		}
		return sb.toString();
	}
}
