package swp_compiler_ss13.javabite.backend.marco.proposal1;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Classfile class. This class represents all information needed
 * to create a JVM-classfile.
 * 
 * @author Marco
 * @since 27.04.2013
 * 
 */
public class Classfile {
	// Name of File
	private String name;
	
	// General Classfile structure information
	private byte[] magic = {(byte)0xca,(byte)0xfe,(byte)0xba,(byte)0xbe};
	private byte[] minorVersion = {(byte)0x00,(byte)0x00};
	private byte[] majorVersion = {(byte)0x00,(byte)0x33};
	protected ConstantPool constantPool;
	// Access Flags
	// This Class
	// Super Class
	// Interfaces
	// FieldArea
	protected MethodArea methodArea;
	// AttribueArea
	
	/**
	 * Classfile constructor. This constructor instantiates the classfile's
	 * constantPool, fieldArea, methodArea and attributeArea.
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	public Classfile(String name) {
		this.name = name;
		this.constantPool = new ConstantPool();
		this.methodArea = new MethodArea();
	}
	
	/**
	 * generateInputstream function. This function generates an Inputstream
	 * containing all information of the classfile.
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	public InputStream generateInputstream() {
		return null;
	}
	
	/**
	 * getBytes function. This function creates a Byte-List
	 * of all the classfile-information meeting the 
	 * JVM-classfile standard.
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	public ArrayList<Byte> getBytes() {
		ArrayList<Byte> classfileBytes = new ArrayList<Byte>();
		
		// Metainformation
		for (int i = 0; i < this.magic.length; i++) {
			classfileBytes.add(this.magic[i]);
		}
		for (int i = 0; i < this.minorVersion.length; i++) {
			classfileBytes.add(this.minorVersion[i]);
		}
		for (int i = 0; i < this.majorVersion.length; i++) {
			classfileBytes.add(this.majorVersion[i]);
		}
		
		// get Bytes of constant_pool
		Iterator<Byte> constantPoolIterator = this.constantPool.getBytes().iterator();
		while(constantPoolIterator.hasNext()) {
			classfileBytes.add(constantPoolIterator.next());
		}
		
		return classfileBytes;
	}
	
	/**
	 * getName function. This function returns the classfile's name.
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * addConstantToConstantPool function. This function adds a new constant
	 * to the classfile's constant pool using the constant pool's functions.
	 * What constant pool function is to be used has to be determined using
	 * the parameter "constantType".
	 * 
	 * constantTypes: LONG
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * @param constantType
	 * 
	 */
	public int addConstantToConstantPool(String constantType, String value) {
		switch (constantType) {
		case "LONG":
			long longValue = Long.parseLong(value);
			this.constantPool.generateConstantLongInfo(longValue);
			break;
		}
		
		return 0;
	}
	
	
	
	
	
	/**
	 * ConstantPool class. This class represents all information needed
	 * to create a JVM-classfile-constantPool being part of every 
	 * JVM-Classfile.
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	private class ConstantPool {
		
		ArrayList<CPInfo> entryList;
		
		private ConstantPool() {
			entryList = new ArrayList<CPInfo>();
		}
		
		/**
		 * getBytes function. This function creates a Byte-List
		 * of all the information meeting the 
		 * JVM-classfile-constantPool standard.
		 * 
		 * @author Marco
		 * @since 27.04.2013
		 * 
		 */
		private ArrayList<Byte> getBytes() {
			ArrayList<Byte> constantPoolBytes = new ArrayList<Byte>();
			
			// get u2 constant_pool_count
			short length = (short) this.entryList.size();
			
			ByteBuffer buf = ByteBuffer.allocate(2);
			buf.order(ByteOrder.BIG_ENDIAN);
			buf.putShort(length);
			byte[] bufArray = buf.array();
			
			for (int i = 0; i < 2; i++) {
				constantPoolBytes.add(bufArray[i]);
			}
			
			// get cp_info constant_pool[constant_pool_count-1]
			Iterator<CPInfo> entryListIterator = entryList.iterator();
			while(entryListIterator.hasNext()) {
				CPInfo entry = entryListIterator.next();
				constantPoolBytes.addAll(entry.getBytes());
			}
			
			return constantPoolBytes;
		}
		
		/**
		 * generateConstantLongInfo function. This function creates 
		 * an LongInfo-entry meeting the 
		 * JVM-classfile-constantPool-CONSTANT_Long_info standard
		 * in the constantPool. The generated entry is appended to 
		 * the existing  List.
		 * 
		 * @author Marco
		 * @since 27.04.2013
		 * 
		 */
		private int generateConstantLongInfo(long value) {
			ByteBuffer buf = ByteBuffer.allocate(8);
			buf.order(ByteOrder.BIG_ENDIAN);
			buf.putLong(value);
			byte[] bufArray = buf.array();
			
			ArrayList<Byte> info = new ArrayList<Byte>();
			
			for (int i = 0; i < 8; i++) {
				info.add(bufArray[i]);
			}
			
			CPInfo longInfo = new CPInfo((byte)0x05, info);
			this.entryList.add(longInfo);
			
			return 0;
		}
		
		/**
		 * ToDO!!! generateConstantStringInfo function. 
		 * 
		 * @author Marco
		 * @since 27.04.2013
		 * 
		 */
		private int generateConstantStringInfo() {
			
			return 0;
		}
		
		/**
		 * ToDO!!! generateConstantDoubleInfo function. 
		 * 
		 * @author Marco
		 * @since 27.04.2013
		 * 
		 */
		private int generateConstantDoubleInfo() {
			
			return 0;
		}
		
		/**
		 * ToDO!!! generateConstantClassInfo function. 
		 * 
		 * @author Marco
		 * @since 27.04.2013
		 * 
		 */
		private int generateConstantClassInfo() {
			
			return 0;
		}
		
		/**
		 * ToDO!!! generateConstantUTF8Info function. 
		 * 
		 * @author Marco
		 * @since 27.04.2013
		 * 
		 */
		private int generateConstantUTF8Info() {
			
			return 0;
		}
		
		/**
		 * ToDO!!! generateConstantMethodrefInfo function. 
		 * 
		 * @author Marco
		 * @since 27.04.2013
		 * 
		 */
		private int generateConstantMethodrefInfo() {
			
			return 0;
		}
		
		/**
		 * ToDO!!! generateConstantNameAndTypeInfo function. 
		 * 
		 * @author Marco
		 * @since 27.04.2013
		 * 
		 */
		private int generateConstantNameAndTypeInfo() {
			
			return 0;
		}
		
		
		
		
		
		/**
		 * CPInfo class. This class represents all information needed
		 * to create a JVM-classfile-constantPool-entry. 
		 * The constantPool of a JVM-classfile has entries of 
		 * different kinds, but every entry consists of a tag and of 
		 * a Byte-List containing further information.
		 * 
		 * @author Marco
		 * @since 27.04.2013
		 * 
		 */
		private class CPInfo {
			private byte tag;
			private ArrayList<Byte> info;
			
			private CPInfo(byte tag, ArrayList<Byte> info) {
				this.tag = tag;
				this.info = info;
			}
			
			/**
			 * getBytes function. This function creates a Byte-List
			 * of all the information meeting the 
			 * JVM-classfile-constantPool-information standard.
			 * 
			 * @author Marco
			 * @since 27.04.2013
			 * 
			 */
			private ArrayList<Byte> getBytes() {
				ArrayList<Byte> cpinfoBytes = new ArrayList<Byte>();
				cpinfoBytes.add(this.tag);
				cpinfoBytes.addAll(this.info);
				
				return cpinfoBytes;
			}
		}
	}
	
	
	
	
	
	/**
	 * MethodArea class. This class represents all information needed
	 * to create a JVM-classfile-Methods-area. 
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	private class MethodArea {
		
	}
}
