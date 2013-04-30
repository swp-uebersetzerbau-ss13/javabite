package swp_compiler_ss13.javabite.backend.marco.proposal1;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private short thisClassIndex;
	private short superClassIndex;
	// Interfaces
	// FieldArea
	protected MethodArea methodArea;
	// AttribueArea
	
	// TypeLengthMap	
	public static final HashMap<String, Short> typeLengthMap = new HashMap<String, Short>() {{
		put("LONG", (short) 2);
		put("DOUBLE", (short) 2);
		put("STRING", (short) 1);
		put("BOOL", (short) 1);
	}};
	
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
		this.thisClassIndex = (short)(this.addConstantToConstantPool("CLASS", this.thisClassNameEIF));
		// add superClassNameEIF to ConstantPool, get back the index in the
		// constantPool and set this.superClass to it
		this.superClassIndex = (short)(this.addConstantToConstantPool("CLASS", this.superClassNameEIF));
		
		
		// add initialize-method to methodArea and set invoke parameter
		this.addMethodToMethodArea("<init>", "()V");
		short initNATIndex = 
				(short) this.constantPool.generateConstantNameAndTypeInfo("<init>", "()V");
		short methodrefIndex = 
				(short) this.constantPool.generateConstantMethodrefInfo(this.thisClassIndex, initNATIndex);
		ArrayList<Byte> methodRefByteArrayList = ByteCalculator.shortToByteArrayList(methodrefIndex);
		
		
		// add code to initialize-method
		Instruction InstrAload = new Instruction(1, Mnemonic.ALOAD_0, null);
		Instruction InstrInvokespecial = new Instruction(3, Mnemonic.INVOKESPECIAL, methodRefByteArrayList);
		Instruction InstrReturn = new Instruction(1, Mnemonic.RETURN, null);
		this.addInstructionToMethodsCode("<init>", InstrAload);
		this.addInstructionToMethodsCode("<init>", InstrInvokespecial);
		this.addInstructionToMethodsCode("<init>", InstrReturn);
		
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
		for (Byte b : this.constantPool.getBytes()) {
			classfileBytes.add(b);
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
	 * the parameter "constantType". If the constant is already existent,
	 * its existent index in the constantPool is returned, otherwise the new 
	 * index.
	 * 
	 * constantTypes: LONG, DOUBLE, CLASS, STRING, UTF8
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * @param constantType type of constantPool information
	 * @param value value of the constantPool information as String
	 * @return returns the constantPool index of the created or existing entry
	 * 
	 */
	public int addConstantToConstantPool(String constantType, String value) {
		
		int index = 0;
		// the constantType+value form the key of the map
		String key = constantType + value;
		
		// if key already exists, return its value
		if((index = this.constantPool.getCPMapEntry(key)) != 0) {
			return index;
		}
		
		switch (constantType) {
		case "LONG":
			long longValue = Long.parseLong(value);
			index = this.constantPool.generateConstantLongInfo(longValue);
			break;
		case "CLASS":
			index = this.constantPool.generateConstantClassInfo(value);
			break;
		case "DOUBLE":
			double doubleValue = Double.parseDouble(value);
			index = this.constantPool.generateConstantDoubleInfo(doubleValue);
			break;
		case "STRING":
			index = this.constantPool.generateConstantStringInfo(value);
			break;
		case "UTF8":
			index = this.constantPool.generateConstantUTF8Info(value);
			break;
		}
		
		this.constantPool.addCPMapEntry(key, (short) index);
		return index;
	}
	
	/**
	 * addMethodToMethodArea function. This function adds the necessary entries
	 * to the constantPool and then calls the addMethod function of this.methodAre 
	 * to add and initialize a new method.
	 * 
	 * @author Marco
	 * @since 29.04.2013
	 * 
	 */
	public int addMethodToMethodArea(String methodName, String methodDescriptor) {
		
		return this.methodArea.addMethod(methodName, methodDescriptor);
	}
	
	/**
	 * addVariableToMethodsCode function.
	 * 
	 * @author Marco
	 * @since 29.04.2013
	 * 
	 */
	public void addVariableToMethodsCode(String methodName, String variableName, String variableType) {
		
		this.methodArea.addVariableToMethodsCode(methodName, variableName, variableType);
	}
	
	/**
	 * addInstructionToMethodsCode function. This function adds a new Instruction to the
	 * codeArea of the codeAttribute of the provided method of this methodArea.
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 * 
	 */
	public void addInstructionToMethodsCode(String methodName, Instruction instruction) {
		
		this.methodArea.addInstructionToMethodsCode(methodName, instruction);
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

		private List<CPInfo> entryList;
		private Map<String,Short> cpEntryMap;
		
		private ConstantPool() {
			entryList = new ArrayList<CPInfo>();
			cpEntryMap = new HashMap<String,Short>();
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
			for (CPInfo entry : entryList) {
				constantPoolBytes.addAll(entry.getBytes());
			}
			
			return constantPoolBytes;
		}
		
		/**
		 * generateConstantLongInfo function. This function creates 
		 * a LongInfo-entry meeting the 
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
		 * generateConstantDoubleInfo function. This function creates 
		 * a DoubleInfo-entry meeting the 
		 * JVM-classfile-constantPool-CONSTANT_Dobule_info standard
		 * in the constantPool. The generated entry is appended to 
		 * the existing  List.
		 * 
		 * @author Marco, Robert
		 * @since 29.04.2013
		 * 
		 */
		private int generateConstantDoubleInfo(double value) {
			
			ArrayList<Byte> info = ByteCalculator.doubleToByteArrayList(value); 
			
			CPInfo doubleInfo = new CPInfo((byte)0x06,info);
			this.entryList.add(doubleInfo);
			
			// return index + 1
			return this.entryList.size();
		}
		
		/**
		 * generateConstantStringInfo function. This function creates 
		 * an StringInfo-entry meeting the 
		 * JVM-classfile-constantPool-CONSTANT_String_info standard
		 * in the constantPool. The generated entry is appended to 
		 * the existing  List.
		 * 
		 * @author Marco
		 * @since 29.04.2013
		 * 
		 */
		private int generateConstantStringInfo(String value) {
			short nameIndex = (short) this.generateConstantUTF8Info(value);
			
			ArrayList<Byte> info = ByteCalculator.shortToByteArrayList(nameIndex); 
			
			CPInfo stringInfo = new CPInfo((byte)0x08, info);
			this.entryList.add(stringInfo);
			
			// return index + 1
			return this.entryList.size();
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
		 * generateConstantMethodrefInfo function. This function creates 
		 * an MethodrefInfo-entry meeting the 
		 * JVM-classfile-constantPool-CONSTANT_Methodref_info standard
		 * in the constantPool. The generated entry is appended to 
		 * the existing  List.
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 */
		private int generateConstantMethodrefInfo(short classIndex, short nameAndTypeIndex) {
			
			if ((classIndex != 0) && (nameAndTypeIndex != 0)) {
				ArrayList<Byte> info = new ArrayList<Byte>(); 
				info.addAll(ByteCalculator.shortToByteArrayList(classIndex));
				info.addAll(ByteCalculator.shortToByteArrayList(nameAndTypeIndex));
				
				CPInfo methodrefInfo = new CPInfo((byte)0x0A, info);
				this.entryList.add(methodrefInfo);
				
				this.addCPMapEntry("METHODREF" + classIndex + "." + nameAndTypeIndex, (short) this.entryList.size());
				
				// return index + 1
				return this.entryList.size();
			} else {
				return 0;
			}
		}
		
		/**
		 * generateConstantNameAndTypeInfo function. This function creates 
		 * an NameAndTypeInfo-entry meeting the 
		 * JVM-classfile-constantPool-CONSTANT_NameAndType_info standard
		 * in the constantPool. The generated entry is appended to 
		 * the existing  List.
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * 
		 */
		private int generateConstantNameAndTypeInfo(String name, String descriptor) {
			short nameIndex = this.getCPMapEntry("UTF8" + name);
			short descriptorIndex = this.getCPMapEntry("UTF8" + descriptor);
			
			if ((nameIndex != 0) && (descriptorIndex != 0)) {
				ArrayList<Byte> info = new ArrayList<Byte>(); 
				info.addAll(ByteCalculator.shortToByteArrayList(nameIndex));
				info.addAll(ByteCalculator.shortToByteArrayList(descriptorIndex));
				
				CPInfo nameAndTypeInfo = new CPInfo((byte)0x0C, info);
				this.entryList.add(nameAndTypeInfo);
				
				this.addCPMapEntry("NAMEANDTYPE" + name + descriptor, (short) this.entryList.size());
				
				// return index + 1
				return this.entryList.size();
			} else {
				return 0;
			}
		}
		
		/**
		 * addCPMapEntry function. 
		 * 
		 * @author Robert, Marco
		 * @since 29.04.2013
		 * 
		 */
		public int addCPMapEntry(String key, short value) {
			this.cpEntryMap.put(key, value);
			return 0;
		}
		
		/**
		 * cpMapEntryExists function. 
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * 
		 */
		public boolean cpMapEntryExists(String key) {
			if (this.cpEntryMap.containsKey(key)) {
				return true;
			}
			return false;
		}
		
		/**
		 * getCPMapEntry function. This function checks, whether the key still
		 * exists and if it does, it'll return the corresponding value, else 0;
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * 
		 */
		public Short getCPMapEntry(String key) {
			if (cpMapEntryExists(key)) {
				return this.cpEntryMap.get(key);
			}
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
	 * to create a JVM-classfile-methods-area. 
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * 
	 */
	private class MethodArea {
		
		private HashMap<String, Method> methodMap;
		
		private MethodArea() {
			this.methodMap = new HashMap<String, Method>();
		}
		
		/**
		 * addMethod function. This function adds and initializes a 
		 * new method to the methodList of this methodArea.
		 * 
		 * @author Marco
		 * @since 29.04.2013
		 * 
		 */
		private int addMethod(String methodName, String methodDescriptor) {
			Method newMethod = new Method(methodName, methodDescriptor);
			
			methodMap.put(methodName, newMethod);
			
			return 0;
		}
		
		/**
		 * getMethod function. This function gets the method begin described
		 * by the paramter methodName
		 * 
		 * @author Marco
		 * @since 29.04.2013
		 * 
		 */
		private Method getMethodByMethodName(String methodName) {
			
			return this.methodMap.get(methodName);
		}
		
		/**
		 * addVariableToMethodsCode function.
		 * 
		 * @author Marco
		 * @since 29.04.2013
		 * 
		 */
		private void addVariableToMethodsCode(String methodName, String variableName, String variableType) {
			
			Method method = this.getMethodByMethodName(methodName);
			method.addVariableToCodeAttribute(variableName, variableType);
		}
		
		/**
		 * addInstructionToMethodsCode function. This function adds a new Instruction to the
		 * codeArea of the codeAttribute of the provided method.
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * 
		 */
		private void addInstructionToMethodsCode(String methodName, Instruction instruction) {
			Method method = this.getMethodByMethodName(methodName);
			method.addInstructionToCodeAttribute(instruction);
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
			private String methodName;
			private String methodDescriptor;
			
			// General method structure information
			private short accessFlags;
			private short nameIndex;
			private short descriptorIndex;
			private short attributesCount;
			// Attributes
			private CodeAttribute codeAttribute;
			
			private Method(String methodName, String methodDescriptor) {
				this.methodName = methodName;
				this.methodName = methodDescriptor;
				
				this.nameIndex = (short) Classfile.this.addConstantToConstantPool("UTF8", methodName);
				this.descriptorIndex = (short) Classfile.this.addConstantToConstantPool("UTF8", methodDescriptor);
				
				this.attributesCount = 1;
				short codeIndex = (short) Classfile.this.addConstantToConstantPool("UTF8", "Code");
				this.codeAttribute = new CodeAttribute(codeIndex);
				
			}
			
			/**
			 * addVariableToCodeAttribute function. 
			 * 
			 * @author Marco
			 * @since 29.04.2013
			 * 
			 */
			private void addVariableToCodeAttribute(String variableName, String variableType) {
			
				this.codeAttribute.addVariable(variableName, variableType);
			}
			
			/**
			 * addInstructionToCodeAttribute function. This function adds a new Instruction to the
			 * codeArea of the codeAttribute of this method.
			 * 
			 * @author Marco
			 * @since 30.04.2013
			 * 
			 */
			private void addInstructionToCodeAttribute(Instruction instruction) {
				
				this.codeAttribute.addInstruction(instruction);
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
				private HashMap<String, Short> variableMap;
				
				// General codeAttribute structure information
				private short codeIndex;
				private short maxStack;
				private short maxLocals;
				private ArrayList<Instruction> codeArea;
				private short exceptionTableLength;
				private short attributesCount; 
				
				private CodeAttribute(short codeIndex) {
					this.codeIndex = codeIndex;
					
					this.variableMap = new HashMap<String, Short>();
					this.codeArea = new ArrayList<Instruction>();
					
					this.maxStack = 1;
					this.maxLocals = 1;
					this.exceptionTableLength = 0;
					this.attributesCount = 0;
					this.exceptionTableLength = 0;
					this.attributesCount = 0;
				};
				
				/**
				 * addVariable function. This function adds a new variable to the local
				 * variable space considering the variable name. if it's already existent,
				 * nothing happens.
				 * 
				 * @author Marco
				 * @since 29.04.2013
				 * 
				 */
				private void addVariable(String variableName, String variableType) {
					
					if (!this.variableMap.containsKey(variableName)) {
						this.variableMap.put(variableName, this.maxLocals);
						this.maxLocals += Classfile.typeLengthMap.get(variableType);
					}
				}
				
				/**
				 * addInstruction function. This function adds a new Instruction to the
				 * codeArea of this codeAttribute
				 * 
				 * @author Marco
				 * @since 30.04.2013
				 * 
				 */
				private void addInstruction(Instruction instruction) {
					
					this.codeArea.add(instruction);
				}
			}
		}
	}
}
