package swp_compiler_ss13.javabite.backend;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.hexFromBytes;
import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.hexFromInt;
import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.hexFromShort;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.javabite.backend.utils.ByteCalculator;
import swp_compiler_ss13.javabite.backend.utils.ByteUtils;

/**
 * Classfile class. This class represents all information needed to create a JVM-classfile.
 * 
 * @author Marco
 * @since 27.04.2013
 * 
 */
public class Classfile implements IClassfile {

	private static final Logger logger = LoggerFactory.getLogger(Classfile.class);

	// Name of File
	private final String name;

	// general classfile constantPool information being used while
	// classfile initialization
	private final String thisClassNameEIF;
	private final String superClassNameEIF;

	// General Classfile structure information
	private final byte[] magic = { (byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe };
	private final byte[] minorVersion = { (byte) 0x00, (byte) 0x00 };
	private final byte[] majorVersion = { (byte) 0x00, (byte) 0x33 };
	protected ConstantPool constantPool;
	private short accessFlags;
	private short thisClassIndex;
	private short superClassIndex;
	private final short interfaceCount;
	// InterfaceArea left out
	private final short fieldsCount;
	// FieldArea left out
	protected MethodArea methodArea;
	private final short attributesCount;
	// AttributeArea left out

	/**
	 * Classfile constructor. This constructor instantiates the classfile's constantPool, fieldArea, methodArea and attributeArea and sets basic classfile
	 * information.
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * @param name
	 *            string describing the classfile's name
	 * @param thisClassNameEIF
	 *            string describing this classname encoded in internal form
	 * @param superClassNameEIF
	 *            string describing the superclass' classname encoded in internal form
	 * @param accessFlags
	 *            arbitrary amount of classfile access flags.
	 */
	public Classfile(final String name, final String thisClassNameEIF, final String superClassNameEIF, final ClassfileAccessFlag... accessFlags) {

		// set basic parameters
		this.name = name;
		this.thisClassNameEIF = thisClassNameEIF;
		this.superClassNameEIF = superClassNameEIF;
		this.interfaceCount = 0;
		this.fieldsCount = 0;
		this.attributesCount = 0;

		for (final ClassfileAccessFlag accessFlag : accessFlags) {
			this.accessFlags = (short) (this.accessFlags | accessFlag.getValue());
		}

		// instantiate constantPool, fieldArea, methodArea and attributeArea
		this.constantPool = new ConstantPool();
		this.methodArea = new MethodArea();

		// initialize Classfile
		this.initializeClassfile();
	}

	/**
	 * initializeClassfile function. This function initializes the classfile. It sets basic constantPool values and creates an init function.
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * 
	 */
	private void initializeClassfile() {
		// initialize constantPool

		// add thisClassNameEIF to ConstantPool, get back the index in the
		// constantPool and set this.thisClass to it
		this.thisClassIndex = (short) (this.addConstantToConstantPool(ConstantType.CLASS, this.thisClassNameEIF));
		// add superClassNameEIF to ConstantPool, get back the index in the
		// constantPool and set this.superClass to it
		this.superClassIndex = (short) (this.addConstantToConstantPool(ConstantType.CLASS, this.superClassNameEIF));

		// add initialize-method to methodArea and set invoke parameter
		this.addMethodToMethodArea("<init>", "()V", Classfile.MethodAccessFlag.ACC_PUBLIC);
		final short initNATIndex = (short) this.constantPool.generateConstantNameAndTypeInfo("<init>", "()V");
		final short methodrefIndex = (short) this.constantPool.generateConstantMethodrefInfo(this.superClassIndex, initNATIndex);
		final byte[] methodRefByteArray = ByteUtils.shortToByteArray(methodrefIndex);

		// add code to initialize-method
		final Instruction InstrAload = new Instruction(1, Mnemonic.ALOAD_0, null);
		final Instruction InstrInvokespecial = new Instruction(3, Mnemonic.INVOKESPECIAL, methodRefByteArray);
		final Instruction InstrReturn = new Instruction(1, Mnemonic.RETURN, null);
		this.addInstructionToMethodsCode("<init>", InstrAload);
		this.addInstructionToMethodsCode("<init>", InstrInvokespecial);
		this.addInstructionToMethodsCode("<init>", InstrReturn);
	}

	/**
	 * generateInputstream function. (see interface iClassfile)
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	@Override
	public InputStream generateInputstream() {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final DataOutputStream classfileDOS = new DataOutputStream(baos);

		// TMP
		Instruction ret = new Instruction(1, Mnemonic.RETURN);
		this.addInstructionToMethodsCode("main", ret);

		this.writeTo(classfileDOS);

		return new ByteArrayInputStream(baos.toByteArray());
	}

	@Override
	public void writeTo(final OutputStream classfileOS) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("magic(4B), minorVersion(2B), majorVersion(2B)");
				logger.debug("{} {} {}", hexFromBytes(magic), hexFromBytes(minorVersion), hexFromBytes(majorVersion));
			}

			final DataOutputStream classfileDOS = (DataOutputStream) classfileOS;

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

			if (logger.isDebugEnabled()) {
				logger.debug("accessFlags(2), thisClassIndex(2), superClassIndex(2), interfaceCount(2), fieldsCount(2)");
				logger.debug("{} {} {} {} {}", hexFromShort(accessFlags), hexFromShort(thisClassIndex), hexFromShort(superClassIndex),
						hexFromShort(interfaceCount), hexFromShort(fieldsCount));
			}

			this.methodArea.writeTo(classfileDOS);

			classfileDOS.writeShort(this.attributesCount);

			if (logger.isDebugEnabled()) {
				logger.debug("accessFlags(2), thisClassIndex(2), superClassIndex(2), interfaceCount(2), fieldsCount(2)");
				logger.debug("{}", hexFromShort(this.attributesCount));
			}

		} catch (final IOException e) {
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
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * addConstantToConstantPool function. (see interface iClassfile)
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 */
	@Override
	public int addConstantToConstantPool(final ConstantType constantType, final String value) {

		int index = 0;
		// the constantType+value form the key of the map
		final String key = constantType + value;

		// if key already exists, return its value
		if ((index = this.constantPool.getCPMapEntry(key)) != 0) {
			return index;
		}

		switch (constantType) {
		case LONG:
			final long longValue = Long.parseLong(value);
			index = this.constantPool.generateConstantLongInfo(longValue);
			break;
		case CLASS:
			index = this.constantPool.generateConstantClassInfo(value);
			break;
		case DOUBLE:
			final double doubleValue = Double.parseDouble(value);
			index = this.constantPool.generateConstantDoubleInfo(doubleValue);
			break;
		case STRING:
			index = this.constantPool.generateConstantStringInfo(value);
			break;
		case UTF8:
			index = this.constantPool.generateConstantUTF8Info(value);
			break;
		}

		this.constantPool.addCPMapEntry(key, (short) index);
		return index;
	}

	@Override
	public int addLongConstantToConstantPool(final long value) {
		return addConstantToConstantPool(ConstantType.LONG, Long.toString(value));
	}

	@Override
	public int addDoubleConstantToConstantPool(final double value) {
		return addConstantToConstantPool(ConstantType.DOUBLE, Double.toString(value));
	}

	@Override
	public int addStringConstantToConstantPool(final String value) {
		return addConstantToConstantPool(ConstantType.STRING, value);
	}

	/**
	 * getIndexOfConstantInMethodsCP function. (see interface iClassfile)
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 */
	@Override
	public short getIndexOfConstantInConstantPool(final ConstantType constantType, final String constantName) {
		return this.constantPool.getIndexOfConstant(constantType, constantName);
	};

	/**
	 * addMethodToMethodArea function. (see interface iClassfile)
	 * 
	 * @author Marco
	 * @since 29.04.2013
	 */
	@Override
	public int addMethodToMethodArea(final String methodName, final String methodDescriptor, final MethodAccessFlag... accessFlags) {

		return this.methodArea.addMethod(methodName, methodDescriptor, accessFlags);
	}

	/**
	 * addVariableToMethodsCode function. (see interface iClassfile)
	 * 
	 * @author Marco
	 * @since 29.04.2013
	 */
	@Override
	public void addVariableToMethodsCode(final String methodName, final String variableName, final VariableType variableType) {

		this.methodArea.addVariableToMethodsCode(methodName, variableName, variableType);
	}

	/**
	 * getIndexOfVariableInMethod function. (see interface iClassfile)
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 */
	@Override
	public short getIndexOfVariableInMethod(final String methodName, final String variableName) {

		return this.methodArea.getIndexOfVariableInMethod(methodName, variableName);
	}

	/**
	 * addInstructionToMethodsCode function. (see interface iClassfile)
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 */
	@Override
	public void addInstructionToMethodsCode(final String methodName, final Instruction instruction) {
		this.methodArea.addInstructionToMethodsCode(methodName, instruction);
	}

	/**
	 * addInstructionsToMethodsCode function. (see iterface iClassfile)
	 * 
	 * @author eike
	 * @since 09.05.2013
	 */
	@Override
	public void addInstructionsToMethodsCode(final String methodName, final Collection<Instruction> instructions) {
		for (final Instruction instruction : instructions) {
			this.methodArea.addInstructionToMethodsCode(methodName, instruction);
		}
	}

	/**
	 * ConstantPool class. This class represents all information needed to create a JVM-classfile-constantPool being part of every JVM-Classfile.
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	private class ConstantPool {
		Logger logger = LoggerFactory.getLogger(this.getClass());

		private final List<CPInfo> entryList;
		private final Map<String, Short> cpEntryMap;

		private ConstantPool() {
			entryList = new ArrayList<CPInfo>();
			cpEntryMap = new HashMap<String, Short>();
		}

		public void writeTo(final DataOutputStream classfileDOS) {

			try {
				if (logger.isDebugEnabled()) {
					logger.debug("constantPool size");
					logger.debug("{}", hexFromInt(this.entryList.size() + 1));
				}

				// specification determines size as size of cp plus 1
				classfileDOS.writeShort((short) (this.entryList.size() + 1));

				for (final CPInfo entry : entryList) {
					entry.writeTo(classfileDOS);
				}

			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * generateConstantLongInfo function. This function creates a LongInfo-entry meeting the JVM-classfile-constantPool-CONSTANT_Long_info standard in the
		 * constantPool. The generated entry is appended to the existing List.
		 * 
		 * @author Marco
		 * @since 27.04.2013
		 * 
		 */
		private int generateConstantLongInfo(final long value) {

			final ArrayList<Byte> info = ByteCalculator.longToByteArrayList(value);

			final CPInfo longInfo = new CPInfo((byte) 0x05, info);
			this.entryList.add(longInfo);
			final CPInfo longInfo2ndPartDummy = new CPInfo();
			this.entryList.add(longInfo2ndPartDummy);

			// return index
			return this.entryList.size() - 1;
		}

		/**
		 * generateConstantDoubleInfo function. This function creates a DoubleInfo-entry meeting the JVM-classfile-constantPool-CONSTANT_Dobule_info standard in
		 * the constantPool. The generated entry is appended to the existing List.
		 * 
		 * @author Marco, Robert
		 * @since 29.04.2013
		 * 
		 */
		private int generateConstantDoubleInfo(final double value) {

			final ArrayList<Byte> info = ByteCalculator.doubleToByteArrayList(value);

			final CPInfo doubleInfo = new CPInfo((byte) 0x06, info);
			this.entryList.add(doubleInfo);
			final CPInfo doubleInfo2ndPartDummy = new CPInfo();
			this.entryList.add(doubleInfo2ndPartDummy);

			// return index
			return this.entryList.size() - 1;
		}

		/**
		 * generateConstantStringInfo function. This function creates an StringInfo-entry meeting the JVM-classfile-constantPool-CONSTANT_String_info standard
		 * in the constantPool. The generated entry is appended to the existing List.
		 * 
		 * @author Marco
		 * @since 29.04.2013
		 * 
		 */
		private int generateConstantStringInfo(final String value) {
			final short nameIndex = (short) this.generateConstantUTF8Info(value);

			final List<Byte> info = ByteCalculator.shortToByteList(nameIndex);

			final CPInfo stringInfo = new CPInfo((byte) 0x08, info);
			this.entryList.add(stringInfo);

			// return index + 1
			return this.entryList.size();
		}

		/**
		 * generateConstantClassInfo function. This function creates an ClassInfo-entry meeting the JVM-classfile-constantPool-CONSTANT_Class_info standard in
		 * the constantPool. The generated entry is appended to the existing List.
		 * 
		 * @author Marco
		 * @since 28.04.2013
		 * 
		 */
		private int generateConstantClassInfo(final String value) {
			final short nameIndex = (short) this.generateConstantUTF8Info(value);

			final List<Byte> info = ByteCalculator.shortToByteList(nameIndex);

			final CPInfo longInfo = new CPInfo((byte) 0x07, info);
			this.entryList.add(longInfo);

			// return index + 1
			return this.entryList.size();
		}

		/**
		 * generateConstantUTF8Info function. This function creates an UTF8Info-entry meeting the JVM-classfile-constantPool-CONSTANT_Utf8_info standard in the
		 * constantPool. The generated entry is appended to the existing List.
		 * 
		 * @author Marco
		 * @since 28.04.2013
		 * 
		 */
		private int generateConstantUTF8Info(final String value) {
			// get ByteArrayList translating String to modified UTF8
			final ArrayList<Byte> info = new ArrayList<Byte>();
			final byte[] bytes = value.getBytes();
			info.addAll(ByteCalculator.shortToByteList((short) bytes.length));
			info.addAll(ByteCalculator.byteArrayToByteArrayList(bytes));

			final CPInfo longInfo = new CPInfo((byte) 0x01, info);
			this.entryList.add(longInfo);

			// return index + 1
			return this.entryList.size();
		}

		/**
		 * getIndexOfConstant function. This function looks up the index of a constant.
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
		public short getIndexOfConstant(final ConstantType constantType, final String constantName) {
			final Short index = this.cpEntryMap.get(constantType.name() + constantName);
			if (index != null) {
				return index;
			} else {
				return 0;
			}
		};

		/**
		 * generateConstantMethodrefInfo function. This function creates an MethodrefInfo-entry meeting the JVM-classfile-constantPool-CONSTANT_Methodref_info
		 * standard in the constantPool. The generated entry is appended to the existing List.
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 */
		private int generateConstantMethodrefInfo(final short classIndex, final short nameAndTypeIndex) {

			if ((classIndex != 0) && (nameAndTypeIndex != 0)) {
				final ArrayList<Byte> info = new ArrayList<Byte>();
				info.addAll(ByteCalculator.shortToByteList(classIndex));
				info.addAll(ByteCalculator.shortToByteList(nameAndTypeIndex));

				final CPInfo methodrefInfo = new CPInfo((byte) 0x0A, info);
				this.entryList.add(methodrefInfo);

				this.addCPMapEntry("METHODREF" + classIndex + "." + nameAndTypeIndex, (short) this.entryList.size());

				// return index + 1
				return this.entryList.size();
			} else {
				return 0;
			}
		}

		/**
		 * generateConstantNameAndTypeInfo function. This function creates an NameAndTypeInfo-entry meeting the
		 * JVM-classfile-constantPool-CONSTANT_NameAndType_info standard in the constantPool. The generated entry is appended to the existing List.
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * 
		 */
		private int generateConstantNameAndTypeInfo(final String name, final String descriptor) {
			final short nameIndex = this.getCPMapEntry("UTF8" + name);
			final short descriptorIndex = this.getCPMapEntry("UTF8" + descriptor);

			if ((nameIndex != 0) && (descriptorIndex != 0)) {
				final ArrayList<Byte> info = new ArrayList<Byte>();
				info.addAll(ByteCalculator.shortToByteList(nameIndex));
				info.addAll(ByteCalculator.shortToByteList(descriptorIndex));

				final CPInfo nameAndTypeInfo = new CPInfo((byte) 0x0C, info);
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
		public int addCPMapEntry(final String key, final short value) {
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
		public boolean cpMapEntryExists(final String key) {
			if (this.cpEntryMap.containsKey(key)) {
				return true;
			}
			return false;
		}

		/**
		 * getCPMapEntry function. This function checks, whether the key still exists and if it does, it'll return the corresponding value, else 0;
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * 
		 */
		public Short getCPMapEntry(final String key) {
			if (cpMapEntryExists(key)) {
				return this.cpEntryMap.get(key);
			}
			return 0;
		}

		/**
		 * CPInfo class. This class represents all information needed to create a JVM-classfile-constantPool-entry. The constantPool of a JVM-classfile has
		 * entries of different kinds, but every entry consists of a tag and of a Byte-List containing further information.
		 * 
		 * @author Marco
		 * @since 27.04.2013
		 * 
		 */
		private class CPInfo {
			Logger logger = LoggerFactory.getLogger(this.getClass());

			// General CPInfo structure information
			private final byte tag;
			private final List<Byte> info;

			// TODO: kill that List<Byte>
			private CPInfo(final byte tag, final List<Byte> info) {
				this.tag = tag;
				this.info = info;
			}

			private CPInfo() {
				this.tag = 0;
				this.info = null;
			}

			public void writeTo(final DataOutputStream classfileDOS) {
				// write only, if CPInfo is no dummy entry
				if (this.info != null) {
					try {
						classfileDOS.writeByte(this.tag);
						for (final Byte b : info) {
							classfileDOS.writeByte(b);
						}

						if (logger.isDebugEnabled()) {
							logger.debug("CPInfo tag");
							logger.debug("{}", hexFromInt(tag));
							logger.debug("CPInfo info");
							logger.debug("{}", hexFromBytes(info));
						}
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * MethodArea class. This class represents all information needed to create a JVM-classfile-methods-area.
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * 
	 */
	private class MethodArea {
		Logger logger = LoggerFactory.getLogger(this.getClass());

		private final HashMap<String, Method> methodMap;

		private MethodArea() {
			this.methodMap = new HashMap<String, Method>();
		}

		private void writeTo(final DataOutputStream classfileDOS) {

			try {
				classfileDOS.writeShort(this.methodMap.size());

				if (logger.isDebugEnabled()) {
					logger.debug("method amount");
					logger.debug("{}", hexFromShort((short) this.methodMap.size()));
				}

				// get method_info - bytes of methods
				for (final Method method : this.methodMap.values()) {
					method.writeTo(classfileDOS);
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * addMethod function. This function adds and initializes a new method to the methodList of this methodArea.
		 * 
		 * @author Marco
		 * @since 29.04.2013
		 * 
		 */
		private int addMethod(final String methodName, final String methodDescriptor, final MethodAccessFlag... accessFlags) {
			final Method newMethod = new Method(methodName, methodDescriptor, accessFlags);

			methodMap.put(methodName, newMethod);

			return 0;
		}

		/**
		 * getMethod function. This function gets the method described by the paramter methodName
		 * 
		 * @author Marco
		 * @since 29.04.2013
		 * 
		 */
		private Method getMethodByMethodName(final String methodName) {

			return this.methodMap.get(methodName);
		}

		/**
		 * addVariableToMethodsCode function.
		 * 
		 * @author Marco
		 * @since 29.04.2013
		 * 
		 */
		private void addVariableToMethodsCode(final String methodName, final String variableName, final VariableType variableType) {

			final Method method = this.getMethodByMethodName(methodName);
			method.addVariableToCodeAttribute(variableName, variableType);
		}

		/**
		 * getIndexOfVariableInMethod function. This function looks up the specified method in method are of this classfile and calls the function
		 * getIndexOfVariable() on this method using the specified variable name.
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * @param methodName
		 *            String name of the method
		 * @param variableName
		 *            String name of the variable
		 * @return index of the variable in local variable space of the code attribute of the specified method
		 */
		private short getIndexOfVariableInMethod(final String methodName, final String variableName) {
			final Method method = this.getMethodByMethodName(methodName);
			return method.getIndexOfVariable(variableName);
		}

		/**
		 * addInstructionToMethodsCode function. This function adds a new Instruction to the codeArea of the codeAttribute of the provided method.
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * 
		 */
		private void addInstructionToMethodsCode(final String methodName, final Instruction instruction) {
			final Method method = this.getMethodByMethodName(methodName);
			method.addInstructionToCodeAttribute(instruction);
		}

		/**
		 * Method class. This class represents all information needed to create a JVM-classfile-Method.
		 * 
		 * @author Marco
		 * @since 28.04.2013
		 * 
		 */
		private class Method {
			Logger logger = LoggerFactory.getLogger(this.getClass());

			// General method structure information
			private short accessFlags;
			private final short nameIndex;
			private final short descriptorIndex;
			private final short attributesCount;
			// Attributes
			private final CodeAttribute codeAttribute;

			private Method(final String methodName, final String methodDescriptor, final MethodAccessFlag... accessFlags) {

				this.nameIndex = (short) Classfile.this.addConstantToConstantPool(ConstantType.UTF8, methodName);
				this.descriptorIndex = (short) Classfile.this.addConstantToConstantPool(ConstantType.UTF8, methodDescriptor);

				for (final MethodAccessFlag a : accessFlags) {
					this.accessFlags = (short) (this.accessFlags | a.getValue());
				}

				this.attributesCount = 1;
				final short codeIndex = (short) Classfile.this.addConstantToConstantPool(ConstantType.UTF8, "Code");

				this.codeAttribute = new CodeAttribute(codeIndex);
			}

			private void writeTo(final DataOutputStream classfileDOS) {
				try {
					classfileDOS.writeShort(this.accessFlags);
					classfileDOS.writeShort(this.nameIndex);
					classfileDOS.writeShort(this.descriptorIndex);
					classfileDOS.writeShort(this.attributesCount);

					if (logger.isDebugEnabled()) {
						logger.debug("accessFlags, nameIndex, descriptorIndex, attributesCount:");
						logger.debug("{} {} {} {}", hexFromShort(accessFlags), hexFromShort(nameIndex), hexFromShort(descriptorIndex),
								hexFromShort(attributesCount));
					}

					codeAttribute.writeTo(classfileDOS);
				} catch (final IOException e) {
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
			private void addVariableToCodeAttribute(final String variableName, final VariableType variableType) {

				this.codeAttribute.addVariable(variableName, variableType);
			}

			/**
			 * getIndexOfVariable function. This function looks up the variable name in the code attribute of this method.
			 * 
			 * @author Marco
			 * @since 30.04.2013
			 * @param variableName
			 *            String name of the variable
			 * @return index of the variable in local variable space of this method.
			 */
			private short getIndexOfVariable(final String variableName) {
				return this.codeAttribute.getIndexOfVariable(variableName);
			}

			/**
			 * addInstructionToCodeAttribute function. This function adds a new Instruction to the codeArea of the codeAttribute of this method.
			 * 
			 * @author Marco
			 * @since 30.04.2013
			 * 
			 */
			private void addInstructionToCodeAttribute(final Instruction instruction) {

				this.codeAttribute.addInstruction(instruction);
			}

			/**
			 * CodeAttribute class. This class represents all information needed to create a JVM-classfile-Method-CodeAttribute.
			 * 
			 * @author Marco
			 * @since 28.04.2013
			 * 
			 */
			private class CodeAttribute {

				Logger logger = LoggerFactory.getLogger(this.getClass());

				private final HashMap<String, Short> variableMap;

				// General codeAttribute structure information
				private final short codeIndex;
				private short maxStack;
				private short maxLocals;
				private final ArrayList<Instruction> codeArea;
				private final short exceptionTableLength;
				private final short attributesCount;

				private CodeAttribute(final short codeIndex) {
					this.codeIndex = codeIndex;

					this.variableMap = new HashMap<String, Short>();
					this.codeArea = new ArrayList<Instruction>();

					this.maxStack = 1;
					this.maxLocals = 1;
					this.exceptionTableLength = 0;
					this.attributesCount = 0;
				};

				private void writeTo(final DataOutputStream classfileDOS) {

					final ByteArrayOutputStream attributesBAOS = new ByteArrayOutputStream();
					final DataOutputStream attributesDOS = new DataOutputStream(attributesBAOS);

					final ByteArrayOutputStream codeBAOS = new ByteArrayOutputStream();
					final DataOutputStream codeDOS = new DataOutputStream(codeBAOS);

					this.maxStack = calculateMaxStack();

					try {
						attributesDOS.writeShort(this.maxStack);
						attributesDOS.writeShort(this.maxLocals);

						for (final Instruction instruction : codeArea) {
							instruction.writeTo(codeDOS);
						}

						attributesDOS.writeInt(codeDOS.size());

						attributesDOS.write(codeBAOS.toByteArray());

						attributesDOS.writeShort(this.exceptionTableLength);
						attributesDOS.writeShort(this.attributesCount);

						classfileDOS.writeShort(this.codeIndex);
						classfileDOS.writeInt(attributesDOS.size());
						classfileDOS.write(attributesBAOS.toByteArray());

						if (logger.isDebugEnabled()) {
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
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}

				private short calculateMaxStack() {
					short maxStackSize = 0;
					short stackSize = 0;
					for(final Instruction instruction : codeArea) {
						stackSize += instruction.getStackChange();
						if(stackSize > maxStackSize) {
							maxStackSize = stackSize;
						}
					}
					return maxStackSize;
				}

				/**
				 * addVariable function. This function adds a new variable to the local variable space considering the variable name. if it's already existent,
				 * nothing happens.
				 * 
				 * @author Marco
				 * @since 29.04.2013
				 * 
				 */
				private void addVariable(final String variableName, final VariableType variableType) {

					if (!this.variableMap.containsKey(variableName)) {
						this.variableMap.put(variableName, this.maxLocals);
						this.maxLocals += variableType.getLength();
					}
				}

				/**
				 * getIndexOfVariable function. This function looks up the variable name in the variableMap of this code attribute.
				 * 
				 * @author Marco
				 * @since 30.04.2013
				 * @param variableName
				 *            String name of the variable
				 * @return index of the variable in local variable space of this method.
				 */
				private short getIndexOfVariable(final String variableName) {

					if (this.variableMap.containsKey(variableName)) {
						return this.variableMap.get(variableName);
					} else {
						return 0;
					}
				}

				/**
				 * addInstruction function. This function adds a new Instruction to the codeArea of this codeAttribute
				 * 
				 * @author Marco
				 * @since 30.04.2013
				 * 
				 */
				private void addInstruction(final Instruction instruction) {

					this.codeArea.add(instruction);
				}
			}
		}
	}
}
