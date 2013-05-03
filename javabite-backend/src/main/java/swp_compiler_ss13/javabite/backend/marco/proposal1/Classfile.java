package swp_compiler_ss13.javabite.backend.marco.proposal1;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classfile class. This class represents all information needed to create a
 * JVM-classfile.
 * 
 * @author Marco
 * @since 27.04.2013
 * 
 */
public class Classfile implements IClassfile
{

	// Name of File
	private String name;

	// general classfile constantPool information being used while
	// classfile initialization
	private String thisClassNameEIF;
	private String superClassNameEIF;

	// General Classfile structure information
	private byte[] magic = { (byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe };
	private byte[] minorVersion = { (byte) 0x00, (byte) 0x00 };
	private byte[] majorVersion = { (byte) 0x00, (byte) 0x33 };
	protected ConstantPool constantPool;
	private short accessFlags;
	private short thisClassIndex;
	private short superClassIndex;
	private short interfaceCount;
	// Interfaces
	private short fieldsCount;
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
	 * @param name
	 *            string describing the classfile's name
	 * @param thisClassNameEIF
	 *            string describing this classname encoded in internal form
	 * @param superClassNameEIF
	 *            string describing the superclass' classname encoded in
	 *            internal form
	 * @param accessFlags arbitrary amount of classfile access flags.
	 */
	public Classfile(String name, String thisClassNameEIF,
			String superClassNameEIF, ClassfileAccessFlag... accessFlags) {

		// set basic parameters
		this.name = name;
		this.thisClassNameEIF = thisClassNameEIF;
		this.superClassNameEIF = superClassNameEIF;
		this.interfaceCount = 0;
		this.fieldsCount = 0;
		
		for (ClassfileAccessFlag a : accessFlags) {
			this.accessFlags = (short)(this.accessFlags | a.getValue()); 
		}

		// instantiate constantPool, fieldArea, methodArea and attributeArea
		this.constantPool = new ConstantPool();
		this.methodArea = new MethodArea();

		// initialize Classfile
		this.initializeClassfile();
	}

	/**
	 * initializeClassfile function. This function initializes the classfile. It
	 * sets basic constantPool values and creates an init function.
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * 
	 */
	private int initializeClassfile() {
		// initialize constantPool

		// add thisClassNameEIF to ConstantPool, get back the index in the
		// constantPool and set this.thisClass to it
		this.thisClassIndex = (short) (this.addConstantToConstantPool("CLASS",
				this.thisClassNameEIF));
		// add superClassNameEIF to ConstantPool, get back the index in the
		// constantPool and set this.superClass to it
		this.superClassIndex = (short) (this.addConstantToConstantPool("CLASS",
				this.superClassNameEIF));

		// add initialize-method to methodArea and set invoke parameter
		this.addMethodToMethodArea("<init>", "()V", Classfile.MethodAccessFlag.ACC_PUBLIC);
		short initNATIndex = (short) this.constantPool
				.generateConstantNameAndTypeInfo("<init>", "()V");
		short methodrefIndex = (short) this.constantPool
				.generateConstantMethodrefInfo(this.thisClassIndex,
						initNATIndex);
		ArrayList<Byte> methodRefByteArrayList = ByteCalculator
				.shortToByteArrayList(methodrefIndex);

		// add code to initialize-method
		Instruction InstrAload = new Instruction(1, Mnemonic.ALOAD_0, null);
		Instruction InstrInvokespecial = new Instruction(3,
				Mnemonic.INVOKESPECIAL, methodRefByteArrayList);
		Instruction InstrReturn = new Instruction(1, Mnemonic.RETURN, null);
		this.addInstructionToMethodsCode("<init>", InstrAload);
		this.addInstructionToMethodsCode("<init>", InstrInvokespecial);
		this.addInstructionToMethodsCode("<init>", InstrReturn);

		return 0;
	}

	/**
	 * generateInputstream function. (see interface iClassfile)
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	public InputStream generateInputstream() {
		return null;
	}

	/**
	 * getBytes function. (see interface iClassfile)
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	public ArrayList<Byte> getBytes() {
		ArrayList<Byte> classfileBytes = new ArrayList<Byte>();

		// Metainformation
		for (Byte b : this.magic) {
			classfileBytes.add(b);
		}
		for (Byte b : this.minorVersion) {
			classfileBytes.add(b);
		}
		for (Byte b : this.majorVersion) {
			classfileBytes.add(b);
		}

		// get bytes of constantPool
		classfileBytes.addAll(this.constantPool.getBytes());
		// get access flags bytes
		classfileBytes.addAll(ByteCalculator.shortToByteArrayList(this.accessFlags));
		// get this class index bytes
		classfileBytes.addAll(ByteCalculator.shortToByteArrayList(this.thisClassIndex));
		// get super class index bytes
		classfileBytes.addAll(ByteCalculator.shortToByteArrayList(this.superClassIndex));
		// get interface count bytes
		classfileBytes.addAll(ByteCalculator.shortToByteArrayList(this.interfaceCount));
		// get fields count bytes
		classfileBytes.addAll(ByteCalculator.shortToByteArrayList(this.fieldsCount));
		// get bytes of methodArea
		classfileBytes.addAll(this.methodArea.getBytes());
		

		// ruturn classfiles bytes
		return classfileBytes;
	}

	/**
	 * getName function. (see interface iClassfile)
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * addConstantToConstantPool function. (see interface iClassfile)
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 */
	public int addConstantToConstantPool(String constantType, String value) {

		int index = 0;
		// the constantType+value form the key of the map
		String key = constantType + value;

		// if key already exists, return its value
		if ((index = this.constantPool.getCPMapEntry(key)) != 0) {
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
	 * getIndexOfConstantInMethodsCP function. (see interface iClassfile)
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 */
	public short getIndexOfConstantInConstantPool(String constantName,
			String constantType) {
		return this.constantPool.getIndexOfConstant(constantName, constantType);
	};

	/**
	 * addMethodToMethodArea function. (see interface iClassfile)
	 * 
	 * @author Marco
	 * @since 29.04.2013
	 */
	public int addMethodToMethodArea(String methodName, String methodDescriptor, MethodAccessFlag... accessFlags) {

		return this.methodArea.addMethod(methodName, methodDescriptor, accessFlags);
	}

	/**
	 * addVariableToMethodsCode function. (see interface iClassfile)
	 * 
	 * @author Marco
	 * @since 29.04.2013
	 */
	public void addVariableToMethodsCode(String methodName,
			String variableName, VariableTypes variableType) {

		this.methodArea.addVariableToMethodsCode(methodName, variableName,
				variableType);
	}

	/**
	 * getIndexOfVariableInMethod function. (see interface iClassfile)
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 */
	public short getIndexOfVariableInMethod(String methodName,
			String variableName) {

		return this.methodArea.getIndexOfVariableInMethod(methodName,
				variableName);
	}

	/**
	 * addInstructionToMethodsCode function. (see interface iClassfile)
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 */
	public void addInstructionToMethodsCode(String methodName,
			Instruction instruction) {

		this.methodArea.addInstructionToMethodsCode(methodName, instruction);
	}

	/**
	 * ConstantPool class. This class represents all information needed to
	 * create a JVM-classfile-constantPool being part of every JVM-Classfile.
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	private class ConstantPool
	{

		private List<CPInfo> entryList;
		private Map<String, Short> cpEntryMap;

		private ConstantPool() {
			entryList = new ArrayList<CPInfo>();
			cpEntryMap = new HashMap<String, Short>();
		}

		/**
		 * getBytes function. This function creates a Byte-List of all the
		 * information meeting the JVM-classfile-constantPool standard.
		 * 
		 * @author Marco
		 * @since 27.04.2013
		 * 
		 */
		private ArrayList<Byte> getBytes() {
			ArrayList<Byte> constantPoolBytes = new ArrayList<Byte>();

			// get u2 constant_pool_count
			ArrayList<Byte> length = ByteCalculator
					.shortToByteArrayList((short) this.entryList.size());
			constantPoolBytes.addAll(length);

			// get cp_info constant_pool[constant_pool_count-1]
			for (CPInfo entry : entryList) {
				constantPoolBytes.addAll(entry.getBytes());
			}

			return constantPoolBytes;
		}

		/**
		 * generateConstantLongInfo function. This function creates a
		 * LongInfo-entry meeting the
		 * JVM-classfile-constantPool-CONSTANT_Long_info standard in the
		 * constantPool. The generated entry is appended to the existing List.
		 * 
		 * @author Marco
		 * @since 27.04.2013
		 * 
		 */
		private int generateConstantLongInfo(long value) {

			ArrayList<Byte> info = ByteCalculator.longToByteArrayList(value);

			CPInfo longInfo = new CPInfo((byte) 0x05, info);
			this.entryList.add(longInfo);

			// return index + 1
			return this.entryList.size();
		}

		/**
		 * generateConstantDoubleInfo function. This function creates a
		 * DoubleInfo-entry meeting the
		 * JVM-classfile-constantPool-CONSTANT_Dobule_info standard in the
		 * constantPool. The generated entry is appended to the existing List.
		 * 
		 * @author Marco, Robert
		 * @since 29.04.2013
		 * 
		 */
		private int generateConstantDoubleInfo(double value) {

			ArrayList<Byte> info = ByteCalculator.doubleToByteArrayList(value);

			CPInfo doubleInfo = new CPInfo((byte) 0x06, info);
			this.entryList.add(doubleInfo);

			// return index + 1
			return this.entryList.size();
		}

		/**
		 * generateConstantStringInfo function. This function creates an
		 * StringInfo-entry meeting the
		 * JVM-classfile-constantPool-CONSTANT_String_info standard in the
		 * constantPool. The generated entry is appended to the existing List.
		 * 
		 * @author Marco
		 * @since 29.04.2013
		 * 
		 */
		private int generateConstantStringInfo(String value) {
			short nameIndex = (short) this.generateConstantUTF8Info(value);

			ArrayList<Byte> info = ByteCalculator
					.shortToByteArrayList(nameIndex);

			CPInfo stringInfo = new CPInfo((byte) 0x08, info);
			this.entryList.add(stringInfo);

			// return index + 1
			return this.entryList.size();
		}

		/**
		 * generateConstantClassInfo function. This function creates an
		 * ClassInfo-entry meeting the
		 * JVM-classfile-constantPool-CONSTANT_Class_info standard in the
		 * constantPool. The generated entry is appended to the existing List.
		 * 
		 * @author Marco
		 * @since 28.04.2013
		 * 
		 */
		private int generateConstantClassInfo(String value) {
			short nameIndex = (short) this.generateConstantUTF8Info(value);

			ArrayList<Byte> info = ByteCalculator
					.shortToByteArrayList(nameIndex);

			CPInfo longInfo = new CPInfo((byte) 0x07, info);
			this.entryList.add(longInfo);

			// return index + 1
			return this.entryList.size();
		}

		/**
		 * generateConstantUTF8Info function. This function creates an
		 * UTF8Info-entry meeting the
		 * JVM-classfile-constantPool-CONSTANT_Utf8_info standard in the
		 * constantPool. The generated entry is appended to the existing List.
		 * 
		 * @author Marco
		 * @since 28.04.2013
		 * 
		 */
		private int generateConstantUTF8Info(String value) {
			// get ByteArrayList translating String to modified UTF8
			ArrayList<Byte> info = new ArrayList<Byte>();
			byte[] bytes = value.getBytes();
			info.addAll(ByteCalculator
					.shortToByteArrayList((short) bytes.length));
			info.addAll(ByteCalculator.byteArrayToByteArrayList(bytes));

			CPInfo longInfo = new CPInfo((byte) 0x01, info);
			this.entryList.add(longInfo);

			// return index + 1
			return this.entryList.size();
		}

		/**
		 * getIndexOfConstant function. This function looks up the index of a
		 * constant.
		 * 
		 * possible constantTypes: LONG, DOUBLE, CLASS, STRING, UTF8
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * @param constantName
		 *            String name of the constant
		 * @param constantType
		 *            String type of the constant
		 * @return index of the constant in this constant pool.
		 */
		public short getIndexOfConstant(String constantName, String constantType) {
			if (this.cpMapEntryExists(constantType + constantName)) {
				return this.cpEntryMap.get(constantType + constantName);
			} else {
				return 0;
			}
		};

		/**
		 * generateConstantMethodrefInfo function. This function creates an
		 * MethodrefInfo-entry meeting the
		 * JVM-classfile-constantPool-CONSTANT_Methodref_info standard in the
		 * constantPool. The generated entry is appended to the existing List.
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 */
		private int generateConstantMethodrefInfo(short classIndex,
				short nameAndTypeIndex) {

			if ((classIndex != 0) && (nameAndTypeIndex != 0)) {
				ArrayList<Byte> info = new ArrayList<Byte>();
				info.addAll(ByteCalculator.shortToByteArrayList(classIndex));
				info.addAll(ByteCalculator
						.shortToByteArrayList(nameAndTypeIndex));

				CPInfo methodrefInfo = new CPInfo((byte) 0x0A, info);
				this.entryList.add(methodrefInfo);

				this.addCPMapEntry("METHODREF" + classIndex + "."
						+ nameAndTypeIndex, (short) this.entryList.size());

				// return index + 1
				return this.entryList.size();
			} else {
				return 0;
			}
		}

		/**
		 * generateConstantNameAndTypeInfo function. This function creates an
		 * NameAndTypeInfo-entry meeting the
		 * JVM-classfile-constantPool-CONSTANT_NameAndType_info standard in the
		 * constantPool. The generated entry is appended to the existing List.
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * 
		 */
		private int generateConstantNameAndTypeInfo(String name,
				String descriptor) {
			short nameIndex = this.getCPMapEntry("UTF8" + name);
			short descriptorIndex = this.getCPMapEntry("UTF8" + descriptor);

			if ((nameIndex != 0) && (descriptorIndex != 0)) {
				ArrayList<Byte> info = new ArrayList<Byte>();
				info.addAll(ByteCalculator.shortToByteArrayList(nameIndex));
				info.addAll(ByteCalculator
						.shortToByteArrayList(descriptorIndex));

				CPInfo nameAndTypeInfo = new CPInfo((byte) 0x0C, info);
				this.entryList.add(nameAndTypeInfo);

				this.addCPMapEntry("NAMEANDTYPE" + name + descriptor,
						(short) this.entryList.size());

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
		 * CPInfo class. This class represents all information needed to create
		 * a JVM-classfile-constantPool-entry. The constantPool of a
		 * JVM-classfile has entries of different kinds, but every entry
		 * consists of a tag and of a Byte-List containing further information.
		 * 
		 * @author Marco
		 * @since 27.04.2013
		 * 
		 */
		private class CPInfo
		{

			// General CPInfo structure information
			private byte tag;
			private ArrayList<Byte> info;

			private CPInfo(byte tag, ArrayList<Byte> info) {
				this.tag = tag;
				this.info = info;
			}

			/**
			 * getBytes function. This function creates a Byte-List of all the
			 * information meeting the JVM-classfile-constantPool-information
			 * standard.
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
	 * MethodArea class. This class represents all information needed to create
	 * a JVM-classfile-methods-area.
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * 
	 */
	private class MethodArea
	{

		private HashMap<String, Method> methodMap;

		private MethodArea() {
			this.methodMap = new HashMap<String, Method>();
		}

		/**
		 * getBytes function. This function creates a Byte-List of all the
		 * information meeting the JVM-classfile-methodsArea standard.
		 * 
		 * @author Marco
		 * @since 03.05.2013
		 * 
		 */
		private ArrayList<Byte> getBytes() {
			ArrayList<Byte> methodAreaBytes = new ArrayList<Byte>();

			// get u2 methods_count
			ArrayList<Byte> length = ByteCalculator
					.shortToByteArrayList((short) this.methodMap.size());
			methodAreaBytes.addAll(length);

			// get method_info - bytes of methods
			for (Method method : this.methodMap.values()) {
				methodAreaBytes.addAll(method.getBytes());
			}

			return methodAreaBytes;
		}
		
		/**
		 * addMethod function. This function adds and initializes a new method
		 * to the methodList of this methodArea.
		 * 
		 * @author Marco
		 * @since 29.04.2013
		 * 
		 */
		private int addMethod(String methodName, String methodDescriptor, MethodAccessFlag... accessFlags) {
			Method newMethod = new Method(methodName, methodDescriptor, accessFlags);

			methodMap.put(methodName, newMethod);

			return 0;
		}

		/**
		 * getMethod function. This function gets the method described by the
		 * paramter methodName
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
		private void addVariableToMethodsCode(String methodName,
				String variableName, VariableTypes variableType) {

			Method method = this.getMethodByMethodName(methodName);
			method.addVariableToCodeAttribute(variableName, variableType);
		}

		/**
		 * getIndexOfVariableInMethod function. This function looks up the
		 * specified method in method are of this classfile and calls the
		 * function getIndexOfVariable() on this method using the specified
		 * variable name.
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * @param methodName
		 *            String name of the method
		 * @param variableName
		 *            String name of the variable
		 * @return index of the variable in local variable space of the code
		 *         attribute of the specified method
		 */
		private short getIndexOfVariableInMethod(String methodName,
				String variableName) {
			Method method = this.getMethodByMethodName(methodName);
			return method.getIndexOfVariable(variableName);
		}

		/**
		 * addInstructionToMethodsCode function. This function adds a new
		 * Instruction to the codeArea of the codeAttribute of the provided
		 * method.
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * 
		 */
		private void addInstructionToMethodsCode(String methodName,
				Instruction instruction) {
			Method method = this.getMethodByMethodName(methodName);
			method.addInstructionToCodeAttribute(instruction);
		}

		/**
		 * Method class. This class represents all information needed to create
		 * a JVM-classfile-Method.
		 * 
		 * @author Marco
		 * @since 28.04.2013
		 * 
		 */
		private class Method
		{
			// General method structure information
			private short accessFlags;
			private short nameIndex;
			private short descriptorIndex;
			private short attributesCount;
			// Attributes
			private CodeAttribute codeAttribute;

			private Method(String methodName, String methodDescriptor, MethodAccessFlag... accessFlags) {

				this.nameIndex = (short) Classfile.this
						.addConstantToConstantPool("UTF8", methodName);
				this.descriptorIndex = (short) Classfile.this
						.addConstantToConstantPool("UTF8", methodDescriptor);

				for (MethodAccessFlag a : accessFlags) {
					this.accessFlags = (short)(this.accessFlags | a.getValue()); 
				}
				
				this.attributesCount = 1;
				short codeIndex = (short) Classfile.this
						.addConstantToConstantPool("UTF8", "Code");
				this.codeAttribute = new CodeAttribute(codeIndex);

			}
			
			/**
			 * getBytes function. This function creates a Byte-List of all the
			 * information meeting the JVM-classfile-method standard.
			 * 
			 * @author Marco
			 * @since 03.05.2013
			 * 
			 */
			private ArrayList<Byte> getBytes() {
				ArrayList<Byte> methodBytes = new ArrayList<Byte>();
				
				// get access flags bytes
				methodBytes.addAll(ByteCalculator.shortToByteArrayList(this.accessFlags));
				// get name index bytes
				methodBytes.addAll(ByteCalculator.shortToByteArrayList(this.nameIndex));
				// get descriptor index bytes
				methodBytes.addAll(ByteCalculator.shortToByteArrayList(this.descriptorIndex));
				// get attributes count bytes
				methodBytes.addAll(ByteCalculator.shortToByteArrayList(this.attributesCount));
				
				// get bytes of code attribute
				this.codeAttribute.getBytes();
				
				return methodBytes;
			}

			/**
			 * addVariableToCodeAttribute function.
			 * 
			 * @author Marco
			 * @since 29.04.2013
			 * 
			 */
			private void addVariableToCodeAttribute(String variableName,
					VariableTypes variableType) {

				this.codeAttribute.addVariable(variableName, variableType);
			}

			/**
			 * getIndexOfVariable function. This function looks up the variable
			 * name in the code attribute of this method.
			 * 
			 * @author Marco
			 * @since 30.04.2013
			 * @param variableName
			 *            String name of the variable
			 * @return index of the variable in local variable space of this
			 *         method.
			 */
			private short getIndexOfVariable(String variableName) {

				return this.codeAttribute.getIndexOfVariable(variableName);
			}

			/**
			 * addInstructionToCodeAttribute function. This function adds a new
			 * Instruction to the codeArea of the codeAttribute of this method.
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
			private class CodeAttribute
			{

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
				};
				
				/**
				 * getBytes function. This function creates a Byte-List of all the
				 * information meeting the JVM-classfile-method-codeAttribute standard.
				 * 
				 * @author Marco
				 * @since 03.05.2013
				 * 
				 */
				private ArrayList<Byte> getBytes() {
					ArrayList<Byte> codeAttributeBytes = new ArrayList<Byte>();
					
					// get code attribute name index bytes
					codeAttributeBytes.addAll(ByteCalculator.shortToByteArrayList(this.codeIndex));
					// get attribute length bytes
					ArrayList<Byte> thisAttributeBytes = new ArrayList<Byte>();
					
					// get max stack bytes
					thisAttributeBytes.addAll(ByteCalculator.shortToByteArrayList(this.maxStack));
					// get max locals bytes
					thisAttributeBytes.addAll(ByteCalculator.shortToByteArrayList(this.maxStack));
					
					// get code length and code bytes
					ArrayList<Byte> codeBytes = new ArrayList<Byte>();
					for (Instruction instruction : codeArea) {
						codeBytes.addAll(instruction.getBytes());
					}/*
					thisAttributeBytes.addAll(ByteCalculator.intToByteArrayList(codeBytes.size()));
					thisAttributeBytes.addAll(codeBytes);
					
					// get exception table length bytes
					thisAttributeBytes.addAll(ByteCalculator.shortToByteArrayList(this.exceptionTableLength));
					// get attributes count bytes
					thisAttributeBytes.addAll(ByteCalculator.shortToByteArrayList(this.attributesCount));
					
					// put together attribute bytes and attribute count
					codeAttributeBytes.addAll(ByteCalculator.intToByteArrayList(thisAttributeBytes.size()));
					codeAttributeBytes.addAll(thisAttributeBytes);
					*/
					return codeAttributeBytes;
				}

				/**
				 * addVariable function. This function adds a new variable to
				 * the local variable space considering the variable name. if
				 * it's already existent, nothing happens.
				 * 
				 * @author Marco
				 * @since 29.04.2013
				 * 
				 */
				private void addVariable(String variableName,
						VariableTypes variableType) {

					if (!this.variableMap.containsKey(variableName)) {
						this.variableMap.put(variableName, this.maxLocals);
						this.maxLocals += variableType.getLength();
					}
				}

				/**
				 * getIndexOfVariable function. This function looks up the
				 * variable name in the variableMap of this code attribute.
				 * 
				 * @author Marco
				 * @since 30.04.2013
				 * @param variableName
				 *            String name of the variable
				 * @return index of the variable in local variable space of this
				 *         method.
				 */
				private short getIndexOfVariable(String variableName) {

					if (this.variableMap.containsKey(variableName)) {
						return this.variableMap.get(variableName);
					} else {
						return 0;
					}
				}

				/**
				 * addInstruction function. This function adds a new Instruction
				 * to the codeArea of this codeAttribute
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
