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
	
	// general classfile constantPool information being used while
	// classfile initialization
	private String thisClassNameEIF;
	private String superClassNameEIF;
	
	
	// General Classfile structure information
	private byte[] magic = {(byte)0xca,(byte)0xfe,(byte)0xba,(byte)0xbe};
	private byte[] minorVersion = {(byte)0x00,(byte)0x00};
	private byte[] majorVersion = {(byte)0x00,(byte)0x33};
	protected ConstantPool constantPool;
	// Access Flags TODO
	private ArrayList<Byte> thisClass;
	private ArrayList<Byte> superClass;
	// Interfaces
	// FieldArea
	protected MethodArea methodArea;
	// AttribueArea
	
	/**
	 * Classfile constructor. This constructor instantiates the classfile's
	 * constantPool, fieldArea, methodArea and attributeArea and sets basic
	 * classfile information.
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * @param name string describing the classfile's name
	 * @param thisClassNameEIF string describing this classname encoded
	 * in internal form
	 * @param superClassNameEIF string describing the superclass' classname 
	 * encoded in internal form
	 * 
	 */
	public Classfile(String name, String thisClassNameEIF, 
			String superClassNameEIF) {
		
		// set basic parameters
		this.name = name;
		this.thisClassNameEIF = thisClassNameEIF;
		this.superClassNameEIF = superClassNameEIF;
		
		// instantiate constantPool, fieldArea, methodArea and attributeArea
		this.constantPool = new ConstantPool();
		this.methodArea = new MethodArea();
		
		// initialize Classfile
		this.initializeClassfile();
	}
	
	/**
	 * initializeClassfile function. This function initializes the classfile.
	 * It sets basic constantPool values and creates an init function.
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * 
	 */
	private int initializeClassfile() {
		// initialize constantPool
		
		// add thisClassNameEIF to ConstantPool, get back the index in the
		// constantPool and set this.thisClass to it
		this.thisClass = ByteCalculator.shortToByteArrayList(
				(short)(this.addConstantToConstantPool("CLASS", this.thisClassNameEIF)));
		
		// add superClassNameEIF to ConstantPool, get back the index in the
		// constantPool and set this.superClass to it
		this.superClass = ByteCalculator.shortToByteArrayList(
				(short)(this.addConstantToConstantPool("CLASS", this.superClassNameEIF)));
		
		return 0;
	}
	
	/**
	 * generateInputstream function. This function generates an Inputstream
	 * containing all information of the classfile, which can be obtained
	 * by using the classfile function "getBytes()".
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
	 * of all the necessary classfile-information meeting the 
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
		
		// get bytes of constantPool
		Iterator<Byte> constantPoolIterator = this.constantPool.getBytes().iterator();
		while(constantPoolIterator.hasNext()) {
			classfileBytes.add(constantPoolIterator.next());
		}
		
		// ruturn classfiles bytes
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
	 * constantTypes: LONG, CLASS
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * @param constantType type of constantPool information
	 * @param value value of the constantPool information as String
	 * @return returns the constantPool index of the created entry
	 * 
	 */
	public int addConstantToConstantPool(String constantType, String value) {
		int index = 0;
		
		switch (constantType) {
		case "LONG":
			long longValue = Long.parseLong(value);
			index = this.constantPool.generateConstantLongInfo(longValue);
			break;
		case "CLASS":
			index = this.constantPool.generateConstantClassInfo(value);
			break;
		case "DOUBLE":
			break;
		case "STRING":
			break;
		}
		
		return index;
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
			ArrayList<Byte> length = ByteCalculator.shortToByteArrayList(
					(short) this.entryList.size());
			constantPoolBytes.addAll(length);
			
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
			
			ArrayList<Byte> info = ByteCalculator.longToByteArrayList(value); 
			
			CPInfo longInfo = new CPInfo((byte)0x05, info);
			this.entryList.add(longInfo);
			
			// return index + 1
			return this.entryList.size();
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
		 * generateConstantClassInfo function. This function creates 
		 * an ClassInfo-entry meeting the 
		 * JVM-classfile-constantPool-CONSTANT_Class_info standard
		 * in the constantPool. The generated entry is appended to 
		 * the existing  List.
		 * 
		 * @author Marco
		 * @since 28.04.2013
		 * 
		 */
		private int generateConstantClassInfo(String value) {
			short nameIndex = (short) this.generateConstantUTF8Info(value);
			
			ArrayList<Byte> info = ByteCalculator.shortToByteArrayList(nameIndex); 
			
			CPInfo longInfo = new CPInfo((byte)0x07, info);
			this.entryList.add(longInfo);
			
			// return index + 1
			return this.entryList.size();
		}
		
		/**
		 * generateConstantUTF8Info function. This function creates 
		 * an UTF8Info-entry meeting the 
		 * JVM-classfile-constantPool-CONSTANT_Utf8_info standard
		 * in the constantPool. The generated entry is appended to 
		 * the existing  List.
		 * 
		 * @author Marco
		 * @since 28.04.2013
		 * 
		 */
		private int generateConstantUTF8Info(String value) {
			// get ByteArrayList translating String to modified UTF8
			ArrayList<Byte> info = new ArrayList<Byte>(); 
			byte[] bytes = value.getBytes();
			info.addAll(ByteCalculator.shortToByteArrayList((short) bytes.length));
			info.addAll(ByteCalculator.byteArrayToByteArrayList(bytes));
			
			CPInfo longInfo = new CPInfo((byte)0x01, info);
			this.entryList.add(longInfo);
			
			// return index + 1
			return this.entryList.size();
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
			
			// General CPInfo structure information
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
	 * @since 28.04.2013
	 * 
	 */
	private class MethodArea {
		
		ArrayList<Method> methodList;
		
		private MethodArea() {
			methodList = new ArrayList<Method>();
		}
		
		
		
		
		
		/**
		 * Method class. This class represents all information needed
		 * to create a JVM-classfile-Method. 
		 * 
		 * @author Marco
		 * @since 28.04.2013
		 * 
		 */
		private class Method {
			
			// General method structure information
			private short accessFlags;
			private short nameIndex;
			private short descriptorIndex;
			private short attributesCount;
			// Attributes
			private CodeAttribute codeAttribute;
			
			private Method() {
				
			}
			
			
			
			
			
			/**
			 * CodeAttribute class. This class represents all information needed
			 * to create a JVM-classfile-Method-CodeAttribute. 
			 * 
			 * @author Marco
			 * @since 28.04.2013
			 * 
			 */
			private class CodeAttribute {
				// General codeAttribute structure information
				private short attributeNameIndex;
				private int attributeLength;
				private short maxStack;
				private short maxLocals;
				private int codeLength;
				private ArrayList<Byte> code;
				private short attributesCount; 
				
			}
		}
	}
}
