package swp_compiler_ss13.javabite.backend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.javabite.backend.Instruction;
import swp_compiler_ss13.javabite.backend.Mnemonic;
import swp_compiler_ss13.javabite.backend.utils.ByteCalculator;

/**
 * Classfile class. This class represents all information needed to create a
 * JVM-classfile.
 * 
 * @author Marco
 * @since 27.04.2013
 * 
 */
public class Classfile implements IClassfile {

	Logger logger=LoggerFactory.getLogger(this.getClass());
	
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
	// InterfacesArea left out
	private short fieldsCount;
	// FieldArea left out
	protected MethodArea methodArea;
	private short attributesCount;
	// AttribueArea left out

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
		this.attributesCount = 0;
		
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
		List<Byte> methodRefByteList = ByteCalculator
				.shortToByteList(methodrefIndex);

		// add code to initialize-method
		Instruction InstrAload = new Instruction(1, Mnemonic.ALOAD_0, null);
		Instruction InstrInvokespecial = new Instruction(3,
				Mnemonic.INVOKESPECIAL, methodRefByteList);
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
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream classfileDOS = new DataOutputStream(baos);
		
		this.writeTo(classfileDOS);
		
		return new ByteArrayInputStream(baos.toByteArray());
	}

	
	public void writeTo(OutputStream classfileOS) {
		try {
			if(logger.isDebugEnabled()) {
				logger.debug("magic(4B), minorVersion(2B), majorVersion(2B)");
				logger.debug("{} {} {}", hexFromBytes(magic), hexFromBytes(minorVersion), hexFromBytes(majorVersion));
			}
			
			DataOutputStream classfileDOS = (DataOutputStream) classfileOS;
			
			// write metainformation
			classfileDOS.write(this.magic);
			classfileDOS.write(this.minorVersion);
			classfileDOS.write(this.majorVersion);
			
			// write constantPool content
			this.constantPool.writeTo(classfileDOS);

			classfileDOS.writeShort(this.accessFlags);
			classfileDOS.writeShort(this.thisClassIndex);
			classfileDOS.writeShort(this.superClassIndex);
			classfileDOS.writeShort(this.interfaceCount);
			classfileDOS.writeShort(this.fieldsCount);
			
			if(logger.isDebugEnabled()) {
				logger.debug("accessFlags(2), thisClassIndex(2), superClassIndex(2), interfaceCount(2), fieldsCount(2)");
				logger.debug("{} {} {} {} {}", 
						hexFromShort(accessFlags), 
						hexFromShort(thisClassIndex), 
						hexFromShort(superClassIndex), 
						hexFromShort(interfaceCount), 
						hexFromShort(fieldsCount));
			}
			
			this.methodArea.writeTo(classfileDOS);

			classfileDOS.writeShort(this.attributesCount);
		
			if(logger.isDebugEnabled()) {
				logger.debug("accessFlags(2), thisClassIndex(2), superClassIndex(2), interfaceCount(2), fieldsCount(2)");
				logger.debug("{}", hexFromShort(this.attributesCount));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		Logger logger=LoggerFactory.getLogger(this.getClass());

		private List<CPInfo> entryList;
		private Map<String, Short> cpEntryMap;

		private ConstantPool() {
			entryList = new ArrayList<CPInfo>();
			cpEntryMap = new HashMap<String, Short>();
		}
				
		public void writeTo(DataOutputStream classfileDOS) {
			
			try {
				if(logger.isDebugEnabled()) {
					logger.debug("constantPool size");
					logger.debug("{}", hexFromInt(this.entryList.size()));
				}				
				
				classfileDOS.writeShort((short) this.entryList.size());
				
				for (CPInfo entry : entryList) {
					entry.writeTo(classfileDOS);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
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

			List<Byte> info = ByteCalculator
					.shortToByteList(nameIndex);

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

			List<Byte> info = ByteCalculator
					.shortToByteList(nameIndex);

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
					.shortToByteList((short) bytes.length));
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
				info.addAll(ByteCalculator.shortToByteList(classIndex));
				info.addAll(ByteCalculator
						.shortToByteList(nameAndTypeIndex));

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
				info.addAll(ByteCalculator.shortToByteList(nameIndex));
				info.addAll(ByteCalculator
						.shortToByteList(descriptorIndex));

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
			Logger logger=LoggerFactory.getLogger(this.getClass());
		
			// General CPInfo structure information
			private byte tag;
			private List<Byte> info;

			// TODO: kill that List<Byte> 
			private CPInfo(byte tag, List<Byte> info) {
				this.tag = tag;
				this.info = info;
			}

			public void writeTo(DataOutputStream classfileDOS) {
				try {
					classfileDOS.writeByte(this.tag);
					for(Byte b : info) {
						classfileDOS.writeByte(b);
					}
					
					if(logger.isDebugEnabled()) {
						logger.debug("CPInfo tag");
						logger.debug("{}", hexFromInt(tag));
						logger.debug("CPInfo info");
						logger.debug("{}", hexFromBytes(info));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
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
		Logger logger=LoggerFactory.getLogger(this.getClass());
		
		private HashMap<String, Method> methodMap;

		private MethodArea() {
			this.methodMap = new HashMap<String, Method>();
		}

		private void writeTo(DataOutputStream classfileDOS) {
					
			try {
				classfileDOS.writeShort(this.methodMap.size());
				
				if(logger.isDebugEnabled()) {
					logger.debug("method amount");
					logger.debug("{}", hexFromShort((short) this.methodMap.size()));
				}
				
				// get method_info - bytes of methods
				for (Method method : this.methodMap.values()) {
					method.writeTo(classfileDOS);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
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
			Logger logger=LoggerFactory.getLogger(this.getClass());
			
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
			
			private void writeTo(DataOutputStream classfileDOS) {
				try {
					classfileDOS.writeShort(this.accessFlags);
					classfileDOS.writeShort(this.nameIndex);
					classfileDOS.writeShort(this.descriptorIndex);
					classfileDOS.writeShort(this.attributesCount);
					
					if(logger.isDebugEnabled()) {
						logger.debug("accessFlags, nameIndex, descriptorIndex, attributesCount:");
						logger.debug("{} {} {} {}",							
								hexFromShort(accessFlags),
								hexFromShort(nameIndex),
								hexFromShort(descriptorIndex),
								hexFromShort(attributesCount));
					}
					
					codeAttribute.writeTo(classfileDOS);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
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
			private class CodeAttribute	{
				
				Logger logger=LoggerFactory.getLogger(this.getClass());

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
				
				private void writeTo(DataOutputStream classfileDOS) {
					
					ByteArrayOutputStream attributesBAOS = new ByteArrayOutputStream();
					DataOutputStream attributesDOS = new DataOutputStream(attributesBAOS);
					
					ByteArrayOutputStream codeBAOS = new ByteArrayOutputStream();
					DataOutputStream codeDOS = new DataOutputStream(codeBAOS);
					
					try {
						attributesDOS.writeShort(this.maxStack);
						attributesDOS.writeShort(this.maxLocals);

						
						for(Instruction instruction : codeArea) {
							instruction.writeTo(codeDOS);
						}

						attributesDOS.writeInt(codeDOS.size());
						
						attributesDOS.write(codeBAOS.toByteArray());
						
						attributesDOS.writeShort(this.exceptionTableLength);
						attributesDOS.writeShort(this.attributesCount);
						
						classfileDOS.writeShort(this.codeIndex);
						classfileDOS.writeInt(attributesDOS.size());
						classfileDOS.write(attributesBAOS.toByteArray());	
						
						if(logger.isDebugEnabled()) {
							logger.debug("codeIndex");
							logger.debug("{}", hexFromInt(codeIndex));
							logger.debug("code size");
							logger.debug("{}", hexFromInt(codeDOS.size()));
							logger.debug("code");							
							logger.debug("{}", hexFromBytes(codeBAOS.toByteArray()));
							logger.debug("attributes size");
							logger.debug("{}", hexFromInt(attributesDOS.size()));
							logger.debug("attributes");
							logger.debug("{}", hexFromBytes(attributesBAOS.toByteArray()));
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
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
