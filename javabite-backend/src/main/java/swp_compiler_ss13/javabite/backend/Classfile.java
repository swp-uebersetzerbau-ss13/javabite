package swp_compiler_ss13.javabite.backend;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.*;

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

import java.nio.ByteBuffer;

import swp_compiler_ss13.javabite.backend.utils.ByteUtils;

/**
 * <h1>Classfile</h1>
 * <p>
 * This class represents all information needed to create a JVM-Classfile.
 * </p>
 * 
 * @author Marco
 * @since 27.04.2013
 */
public class Classfile implements IClassfile {

	private static final Logger logger = LoggerFactory
			.getLogger(Classfile.class);

	// name of File
	private final String name;

	/*
	 * general classfile constant pool information being used while classfile
	 * initialization
	 */
	private final String thisClassNameEIF;
	private final String superClassNameEIF;

	// general classfile structure information
	private final byte[] magic = { (byte) 0xca, (byte) 0xfe, (byte) 0xba,
			(byte) 0xbe };
	private final byte[] minorVersion = { (byte) 0x00, (byte) 0x00 };
	private final byte[] majorVersion = { (byte) 0x00, (byte) 0x33 };
	protected ConstantPool constantPool;
	private short accessFlags;
	private short thisClassIndex;
	private short superClassIndex;
	private final short interfaceCount;
	// interface area left out
	private final short fieldsCount;
	// field area left out
	protected MethodArea methodArea;
	private final short attributesCount;

	// attribute area left out

	/**
	 * <h1>Classfile</h1>
	 * <p>
	 * This class represents all information needed to create a JVM-Classfile.
	 * </p>
	 * 
	 * <h1>Classfile constructor</h1>
	 * <p>
	 * The constructor instantiates the classfile's constant pool, field area,
	 * method area and attribute area and sets basic classfile information.
	 * </p>
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * @param name
	 *            string describing the classfile's name being used when the
	 *            actual file is created.
	 * @param thisClassNameEIF
	 *            string describing this class name encoded in internal form
	 *            according to the jvm specification.
	 * @param superClassNameEIF
	 *            string describing the superclass' class name encoded in
	 *            internal form according to the jvm specification.
	 * @param accessFlags
	 *            arbitrary amount of classfile access flags.
	 */
	public Classfile(final String name, final String thisClassNameEIF,
			final String superClassNameEIF,
			final ClassfileAccessFlag... accessFlags) {

		// set basic parameters
		this.name = name;
		this.thisClassNameEIF = thisClassNameEIF;
		this.superClassNameEIF = superClassNameEIF;
		this.interfaceCount = 0;
		this.fieldsCount = 0;
		this.attributesCount = 0;

		for (final ClassfileAccessFlag accessFlag : accessFlags) {
			this.accessFlags = (short) (this.accessFlags | accessFlag
					.getValue());
		}

		// instantiate constant pool, field area, method area and attribute area
		this.constantPool = new ConstantPool();
		this.methodArea = new MethodArea();

		// initialize classfile
		this.initializeClassfile();
	}

	/**
	 * <h1>initializeClassfile</h1>
	 * <p>
	 * This method initializes the classfile. It sets basic constant pool values
	 * and creates an init method (constructor).
	 * </p>
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 */
	private void initializeClassfile() {
		/*
		 * add the class' name encoded in internal form to constant pool, get
		 * back its index in the constant pool and set member variable
		 * thisClassIndex to it
		 */
		this.thisClassIndex = this
				.addClassConstantToConstantPool(this.thisClassNameEIF);
		/*
		 * add the super class' name encoded in internal form to constant pool,
		 * get back its index in the constant pool and set member variable
		 * superClassIndex to it
		 */
		this.superClassIndex = this
				.addClassConstantToConstantPool(this.superClassNameEIF);

		/*
		 * add initialize-method (constructor) to method area and set invoke
		 * parameter
		 */
		// TODO externalize static strings
		this.addMethodToMethodArea("<init>", "()V",
				Classfile.MethodAccessFlag.ACC_PUBLIC);
		// TODO replace with addMethodref
		final short initNATIndex = this.constantPool
				.generateConstantNameAndTypeInfo("<init>", "()V");
		final short methodrefIndex = this.constantPool
				.generateConstantMethodrefInfo(this.superClassIndex,
						initNATIndex);
		final byte[] methodRefByteArray = ByteUtils
				.shortToByteArray(methodrefIndex);

		// add code to initialize-method
		final Instruction InstrAload = new Instruction(1, Mnemonic.ALOAD_0,
				null);
		final Instruction InstrInvokespecial = new Instruction(3,
				Mnemonic.INVOKESPECIAL, methodRefByteArray);
		final Instruction InstrReturn = new Instruction(1, Mnemonic.RETURN,
				null);
		this.addInstructionToMethodsCode("<init>", InstrAload);
		this.addInstructionToMethodsCode("<init>", InstrInvokespecial);
		this.addInstructionToMethodsCode("<init>", InstrReturn);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream generateInputstream() {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final DataOutputStream classfileDOS = new DataOutputStream(baos);

		this.writeTo(classfileDOS);

		return new ByteArrayInputStream(baos.toByteArray());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeTo(final OutputStream classfileOS) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("magic(4B), minorVersion(2B), majorVersion(2B)");
				logger.debug("{} {} {}", hexFromBytes(magic),
						hexFromBytes(minorVersion), hexFromBytes(majorVersion));
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
				logger.debug("{} {} {} {} {}", toHexString(accessFlags),
						toHexString(thisClassIndex),
						toHexString(superClassIndex),
						toHexString(interfaceCount), toHexString(fieldsCount));
			}

			this.methodArea.writeTo(classfileDOS);

			classfileDOS.writeShort(this.attributesCount);

			if (logger.isDebugEnabled()) {
				logger.debug("accessFlags(2), thisClassIndex(2), superClassIndex(2), interfaceCount(2), fieldsCount(2)");
				logger.debug("{}", toHexString(this.attributesCount));
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short addLongConstantToConstantPool(final long value) {
		return this.constantPool.generateConstantLongInfo(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short addDoubleConstantToConstantPool(final double value) {
		return this.constantPool.generateConstantDoubleInfo(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short addStringConstantToConstantPool(final String value) {
		return this.constantPool.generateConstantStringInfo(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short addUTF8ConstantToConstantPool(final String value) {
		return this.constantPool.generateConstantUTF8Info(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short addClassConstantToConstantPool(final String value) {
		return this.constantPool.generateConstantClassInfo(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short addMethodrefConstantToConstantPool(final String methodName,
			final String methodNameDescriptor, final String classNameEIF) {

		// add class
		final short classIndex = this
				.addClassConstantToConstantPool(classNameEIF);
		// add NAT
		final short natIndex = this.constantPool
				.generateConstantNameAndTypeInfo(methodName,
						methodNameDescriptor);
		// add methodref
		final short methodrefIndex = this.constantPool
				.generateConstantMethodrefInfo(classIndex, natIndex);

		return methodrefIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short addFieldrefConstantToConstantPool(final String fieldName,
			final String fieldNameDescriptor, final String classNameEIF) {

		// add class
		final short classIndex = this
				.addClassConstantToConstantPool(classNameEIF);
		// add NAT
		final short natIndex = this.constantPool
				.generateConstantNameAndTypeInfo(fieldName, fieldNameDescriptor);
		// add fieldref
		final short fieldrefIndex = this.constantPool
				.generateConstantFieldrefInfo(classIndex, natIndex);

		return fieldrefIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public short getIndexOfConstantInConstantPool(final InfoTag constantType,
			final String constantName) {
		return this.constantPool.getIndexOfConstant(constantType, constantName);
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMethodToMethodArea(final String methodName,
			final String methodDescriptor,
			final MethodAccessFlag... accessFlags) {

		this.methodArea.addMethod(methodName, methodDescriptor, accessFlags);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addVariableToMethodsCode(final String methodName,
			final String variableName, final VariableType variableType) {

		this.methodArea.addVariableToMethodsCode(methodName, variableName,
				variableType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addLongVariableToMethodsCode(final String methodName,
			final String variableName) {

		this.addVariableToMethodsCode(methodName, variableName,
				VariableType.LONG);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addDoubleVariableToMethodsCode(final String methodName,
			final String variableName) {

		this.addVariableToMethodsCode(methodName, variableName,
				VariableType.DOUBLE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addStringVariableToMethodsCode(final String methodName,
			final String variableName) {

		this.addVariableToMethodsCode(methodName, variableName,
				VariableType.STRING);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte getIndexOfVariableInMethod(final String methodName,
			final String variableName) {

		return this.methodArea.getIndexOfVariableInMethod(methodName,
				variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addInstructionToMethodsCode(final String methodName,
			final Instruction instruction) {
		this.methodArea.addInstructionToMethodsCode(methodName, instruction);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addInstructionsToMethodsCode(final String methodName,
			final Collection<Instruction> instructions) {
		for (final Instruction instruction : instructions) {
			this.methodArea
					.addInstructionToMethodsCode(methodName, instruction);
		}
	}

	/**
	 * <h1>ConstantPool</h1>
	 * <p>
	 * This class represents all information needed to create a
	 * JVM-classfile-constantPool being part of every JVM-Classfile.
	 * </p>
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 */
	private class ConstantPool {
		Logger logger = LoggerFactory.getLogger(this.getClass());

		/**
		 * <h1>entryList</h1>
		 * <p>
		 * List of CPInfo objects representing entries in the constant pool
		 * </p>
		 */
		private final List<CPInfo> entryList;
		/**
		 * <h1>cpEntryMap</h1>
		 * <p>
		 * This map maps constant pool entry keys to its indices in the constant
		 * pool.
		 * </p>
		 * 
		 * @see Classfile#getIndexOfConstantInConstantPool(swp_compiler_ss13.javabite.backend.IClassfile.InfoTag,
		 *      String)
		 */
		private final Map<String, Short> cpEntryMap;

		private ConstantPool() {
			entryList = new ArrayList<CPInfo>();
			cpEntryMap = new HashMap<String, Short>();
		}

		/**
		 * <h1>writeTo</h1>
		 * <p>
		 * This method uses an output stream and adds all bytes of the
		 * classfile's constant pool meeting the jvm standard for javabyte
		 * classfile constant pools. To do this, it uses the information in its
		 * member variables and the writeTo methods of its member objects.
		 * </p>
		 * 
		 * @author Robert, Marco
		 * @param classfileDOS
		 *            DataOutputStream to which the bytes are written
		 */
		private void writeTo(final DataOutputStream classfileDOS) {

			try {
				if (logger.isDebugEnabled()) {
					logger.debug("constantPool size");
					logger.debug("{}", toHexString(this.entryList.size() + 1));
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
		 * <h1>checkConstantPoolSize</h1>
		 * <p>
		 * TODO Check whether there is an overflow... (>256) -> exception
		 * </p>
		 */
		private void checkConstantPoolSize() {

		}

		/**
		 * <h1>generateConstantLongInfo</h1>
		 * <p>
		 * This method creates a LongInfo-entry meeting the jvm classfile
		 * constant pool CONSTANT_LONG_info standard in the constant pool. If
		 * the entry does not exist yet, it'll be appended to the existing list
		 * and the new entry will be returned. Otherwise the existing entry's
		 * index is returned.
		 * </p>
		 * 
		 * @author Marco
		 * @since 27.04.2013
		 * @param value
		 *            long value of entry, which is to be generated
		 * @return short index of a LONG info entry in the constant pool of this
		 *         classfile meeting the parameters.
		 */
		private short generateConstantLongInfo(final long value) {
			checkConstantPoolSize();
			String key = InfoTag.LONG.name() + String.valueOf(value);

			// return existing entry's index, if it exists already
			if (this.getCPMapEntry(key) > 0) {
				return this.getCPMapEntry(key);
			}

			// generate entry
			final byte[] info = longToByteArray(value);
			final CPInfo longInfo = new CPInfo(InfoTag.LONG, info);
			this.entryList.add(longInfo);
			final CPInfo longInfo2ndPartDummy = new CPInfo();
			this.entryList.add(longInfo2ndPartDummy);

			short index = (short) (this.entryList.size() - 1);
			this.addCPMapEntry(key, index);
			return index;
		}

		/**
		 * <h1>generateConstantDoubleInfo</h1>
		 * <p>
		 * This method creates a DoubleInfo-entry meeting the jvm classfile
		 * constant pool CONSTANT_DOUBLE_info standard in the constant pool. If
		 * the entry does not exist yet, it'll be appended to the existing list
		 * and the new entry will be returned. Otherwise the existing entry's
		 * index is returned.
		 * </p>
		 * 
		 * @author Marco, Robert
		 * @since 29.04.2013
		 * @param value
		 *            double value of entry, which is to be generated
		 * @return short index of a DOUBLE info entry in the constant pool of
		 *         this classfile meeting the parameters.
		 */
		private short generateConstantDoubleInfo(final double value) {
			checkConstantPoolSize();
			String key = InfoTag.DOUBLE.name() + String.valueOf(value);

			// return existing entry's index, if it exists already
			if (this.getCPMapEntry(key) > 0) {
				return this.getCPMapEntry(key);
			}

			// generate entry
			final byte[] info = doubleToByteArray(value);
			final CPInfo doubleInfo = new CPInfo(InfoTag.DOUBLE, info);
			this.entryList.add(doubleInfo);
			final CPInfo doubleInfo2ndPartDummy = new CPInfo();
			this.entryList.add(doubleInfo2ndPartDummy);

			short index = (short) (this.entryList.size() - 1);
			this.addCPMapEntry(key, index);
			return index;
		}

		/**
		 * <h1>generateConstantStringInfo</h1>
		 * <p>
		 * This method creates a StringInfo-entry meeting the jvm classfile
		 * constant pool CONSTANT_STRING_info standard in the constant pool. If
		 * the entry does not exist yet, it'll be appended to the existing list
		 * and the new entry will be returned. Otherwise the existing entry's
		 * index is returned.
		 * </p>
		 * 
		 * @author Marco
		 * @since 29.04.2013
		 * @param value
		 *            string value of entry, which is to be generated
		 * @return short index of a STRING info entry in the constant pool of
		 *         this classfile meeting the parameters.
		 */
		private short generateConstantStringInfo(final String value) {
			checkConstantPoolSize();
			String key = InfoTag.STRING.name() + value;

			// return existing entry's index, if it exists already
			if (this.getCPMapEntry(key) > 0) {
				return this.getCPMapEntry(key);
			}

			// generate UTF8-entry
			final short nameIndex = this.generateConstantUTF8Info(value);
			// generate String entry
			final byte[] info = ByteUtils.shortToByteArray(nameIndex);
			final CPInfo stringInfo = new CPInfo(InfoTag.STRING, info);
			this.entryList.add(stringInfo);

			// return index + 1
			short index = (short) this.entryList.size();
			this.addCPMapEntry(key, index);
			return index;
		}

		/**
		 * <h1>generateConstantClassInfo</h1>
		 * <p>
		 * This method creates a ClassInfo-entry meeting the jvm classfile
		 * constant pool CONSTANT_CLASS_info standard in the constant pool. If
		 * the entry does not exist yet, it'll be appended to the existing list
		 * and the new entry will be returned. Otherwise the existing entry's
		 * index is returned.
		 * </p>
		 * 
		 * @author Marco
		 * @since 28.04.2013
		 * @param value
		 *            string value of entry, which is to be generated
		 * @return short index of a CLASS info entry in the constant pool of
		 *         this classfile meeting the parameters.
		 */
		private short generateConstantClassInfo(final String value) {
			checkConstantPoolSize();
			String key = InfoTag.CLASS.name() + value;

			// return existing entry's index, if it exists already
			if (this.getCPMapEntry(key) > 0) {
				return this.getCPMapEntry(key);
			}

			// generate UTF8-entry
			final short nameIndex = this.generateConstantUTF8Info(value);
			// generate CLASS-entry
			final byte[] info = ByteUtils.shortToByteArray(nameIndex);
			final CPInfo longInfo = new CPInfo(InfoTag.CLASS, info);
			this.entryList.add(longInfo);

			// return index + 1
			short index = (short) this.entryList.size();
			this.addCPMapEntry(key, index);
			return index;
		}

		/**
		 * <h1>generateConstantUTF8Info</h1>
		 * <p>
		 * This method creates an UTF8Info-entry meeting the jvm classfile
		 * constant pool CONSTANT_UTF8_info standard in the constant pool. If
		 * the entry does not exist yet, it'll be appended to the existing list
		 * and the new entry will be returned. Otherwise the existing entry's
		 * index is returned.
		 * </p>
		 * 
		 * @author Marco
		 * @since 28.04.2013
		 * @param value
		 *            string value of entry, which is to be generated
		 * @return short index of an UTF8 info entry in the constant pool of
		 *         this classfile meeting the parameters.
		 */
		private short generateConstantUTF8Info(final String value) {
			checkConstantPoolSize();
			String key = InfoTag.UTF8.name() + value;

			// return existing entry's index, if it exists already
			if (this.getCPMapEntry(key) > 0) {
				return this.getCPMapEntry(key);
			}

			// generate entry
			ByteBuffer info = ByteBuffer.allocate(value.length() + 2);
			info.put(shortToByteArray((short) value.length()));
			info.put(value.getBytes());

			final CPInfo utf8Info = new CPInfo(InfoTag.UTF8, info.array());
			this.entryList.add(utf8Info);

			// return index + 1
			short index = (short) this.entryList.size();
			this.addCPMapEntry(key, index);
			return index;
		}

		/**
		 * <h1>generateConstantMethodrefInfo</h1>
		 * <p>
		 * This method creates a MethodrefInfo-entry meeting the jvm classfile
		 * constant pool CONSTANT_Methodref_info standard in the constant pool.
		 * If the entry does not exist yet, it'll be appended to the existing
		 * list and the new entry will be returned. Otherwise the existing
		 * entry's index is returned.
		 * </p>
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * @param classIndex
		 *            short index of a CLASS info entry in this constant pool
		 * @param nameAndTypeIndex
		 *            short index of a NameAndType entry in this constant pool
		 * @return short index of a Methodref info entry in the constant pool of
		 *         this classfile meeting the parameters.
		 */
		private short generateConstantMethodrefInfo(final short classIndex,
				final short nameAndTypeIndex) {
			checkConstantPoolSize();
			String key = InfoTag.METHODREF.name() + classIndex + "."
					+ nameAndTypeIndex;

			// return existing entry's index, if it exists already
			if (this.getCPMapEntry(key) > 0) {
				return this.getCPMapEntry(key);
			}

			if ((classIndex != 0) && (nameAndTypeIndex != 0)) {
				// generate entry
				ByteBuffer info = ByteBuffer.allocate(4);
				info.put(shortToByteArray(classIndex));
				info.put(shortToByteArray(nameAndTypeIndex));

				final CPInfo methodrefInfo = new CPInfo(InfoTag.METHODREF,
						info.array());
				this.entryList.add(methodrefInfo);

				// return index + 1
				short index = (short) this.entryList.size();
				this.addCPMapEntry(key, index);
				return index;
			} else {
				return 0;
			}
		}

		/**
		 * <h1>generateConstantFieldrefInfo</h1>
		 * <p>
		 * This method creates a FieldrefInfo-entry meeting the jvm classfile
		 * constant pool CONSTANT_Fieldref_info standard in the constant pool.
		 * If the entry does not exist yet, it'll be appended to the existing
		 * list and the new entry will be returned. Otherwise the existing
		 * entry's index is returned.
		 * </p>
		 * 
		 * @author Marco
		 * @since 30.05.2013
		 * @param classIndex
		 *            short index of a CLASS info entry in this constant pool
		 * @param nameAndTypeIndex
		 *            short index of a NameAndType entry in this constant pool
		 * @return short index of a Fieldref info entry in the constant pool of
		 *         this classfile meeting the parameters.
		 */
		private short generateConstantFieldrefInfo(final short classIndex,
				final short nameAndTypeIndex) {
			checkConstantPoolSize();
			String key = InfoTag.METHODREF.name() + classIndex + "."
					+ nameAndTypeIndex;

			// return existing entry's index, if it exists already
			if (this.getCPMapEntry(key) > 0) {
				return this.getCPMapEntry(key);
			}

			if ((classIndex != 0) && (nameAndTypeIndex != 0)) {
				// generate entry
				ByteBuffer info = ByteBuffer.allocate(4);
				info.put(shortToByteArray(classIndex));
				info.put(shortToByteArray(nameAndTypeIndex));

				final CPInfo fieldrefInfo = new CPInfo(InfoTag.FIELDREF,
						info.array());
				this.entryList.add(fieldrefInfo);

				// return index + 1
				short index = (short) this.entryList.size();
				this.addCPMapEntry(key, index);
				return index;
			} else {
				return 0;
			}
		}

		/**
		 * <h1>generateConstantNameAndTypeInfo</h1>
		 * <p>
		 * This method creates an NameAndTypeInfo-entry meeting the jvm
		 * classfile constant pool CONSTANT_NameAndType_info standard in the
		 * constant pool. If the entry does not exist yet, it'll be appended to
		 * the existing list and the new entry will be returned. Otherwise the
		 * existing entry's index is returned.
		 * </p>
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * @param name
		 *            string name of the method
		 * @param descriptor
		 *            string method descriptor as specified by the jvm
		 *            specification
		 * @return short index of a NameAndType info entry in the constant pool
		 *         of this classfile meeting the parameters.
		 */
		private short generateConstantNameAndTypeInfo(final String name,
				final String descriptor) {
			checkConstantPoolSize();
			String key = InfoTag.NAMEANDTYPE.name() + name + descriptor;

			// return existing entry's index, if it exists already
			if (this.getCPMapEntry(key) > 0) {
				return this.getCPMapEntry(key);
			}

			// check, whether name exists already, else add it
			String nameKey = InfoTag.UTF8.name() + name;
			final short nameIndex;
			if (this.getCPMapEntry(nameKey) > 0) {
				nameIndex = this.getCPMapEntry(nameKey);
			} else {
				nameIndex = this.generateConstantUTF8Info(name);
			}

			// check, whether descriptor exists already, else add it
			String descriptorKey = InfoTag.UTF8.name() + descriptor;
			final short descriptorIndex;
			if (this.getCPMapEntry(descriptorKey) > 0) {
				descriptorIndex = this.getCPMapEntry(descriptorKey);
			} else {
				descriptorIndex = this.generateConstantUTF8Info(descriptor);
			}

			if ((nameIndex != 0) && (descriptorIndex != 0)) {
				// generate key
				ByteBuffer info = ByteBuffer.allocate(4);
				info.put(shortToByteArray(nameIndex));
				info.put(shortToByteArray(descriptorIndex));

				final CPInfo nameAndTypeInfo = new CPInfo(InfoTag.NAMEANDTYPE,
						info.array());
				this.entryList.add(nameAndTypeInfo);

				// return index + 1
				short index = (short) this.entryList.size();
				this.addCPMapEntry(key, index);
				return index;
			} else {
				return 0;
			}
		}

		/**
		 * <h1>getIndexOfConstant</h1>
		 * <p>
		 * This method looks up the index of a constant.
		 * </p>
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * @param constantName
		 *            String name of the constant
		 * @param constantType
		 *            InfoTag type of the constant
		 * @return index of the constant in this constant pool
		 * @see Classfile#getIndexOfConstantInConstantPool(swp_compiler_ss13.javabite.backend.IClassfile.InfoTag,
		 *      String)
		 */
		public short getIndexOfConstant(final InfoTag constantType,
				final String constantName) {
			final Short index = this.cpEntryMap.get(constantType.name()
					+ constantName);
			if (index != null) {
				return index;
			} else {
				return 0;
			}
		};

		/**
		 * <h1>addCPMapEntry</h1>
		 * <p>
		 * Add a new entry to {@link #cpEntryMap} according to the method's
		 * parameters.
		 * </p>
		 * 
		 * @author Robert, Marco
		 * @since 29.04.2013
		 * @param key
		 *            String key which is to be used in the mapping
		 * @param value
		 *            short value which is to be used in the mapping
		 */
		public int addCPMapEntry(final String key, final short value) {
			this.cpEntryMap.put(key, value);
			return 0;
		}

		/**
		 * <h1>cpMapEntryExists</h1>
		 * <p>
		 * This method checks, whether a certain key is already used in the
		 * {@link #cpEntryMap}.
		 * </p>
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * @param key
		 *            String key which is to be checked
		 */
		public boolean cpMapEntryExists(final String key) {
			if (this.cpEntryMap.containsKey(key)) {
				return true;
			}
			return false;
		}

		/**
		 * <h1>getCPMapEntry</h1>
		 * <p>
		 * This method checks, whether the key still exists using the method
		 * {@link #cpMapEntryExists(String)} and if it does, it'll return the
		 * corresponding value, else 0;
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * @param key
		 *            String key which is to be checked
		 * @see #cpMapEntryExists(String)
		 */
		public Short getCPMapEntry(final String key) {
			if (cpMapEntryExists(key)) {
				return this.cpEntryMap.get(key);
			}
			return 0;
		}

		/**
		 * <h1>CPInfo</h1>
		 * <p>
		 * This class represents all information needed to create a
		 * JVM-Classfile constant pool entry. The constant pool of a
		 * JVM-Classfile has entries of different kinds, but every entry
		 * consists of a tag and of a Byte-List containing further information.
		 * </p>
		 * 
		 * @author Marco
		 * @since 27.04.2013
		 */
		private class CPInfo {
			Logger logger = LoggerFactory.getLogger(this.getClass());

			// General CPInfo structure information
			private final InfoTag tag;
			private final byte[] info;

			private CPInfo(final InfoTag tag, final byte[] info) {
				this.tag = tag;
				this.info = info;
			}

			private CPInfo() {
				this.tag = InfoTag.NONE;
				this.info = null;
			}

			/**
			 * <h1>writeTo</h1>
			 * <p>
			 * This method uses an output stream and adds all bytes of a
			 * classfile's constant pools CPInfo object meeting the jvm standard
			 * for javabyte classfile constant pool CPInfo structures. To do
			 * this, it uses the information in its member variables.
			 * </p>
			 * 
			 * @author Robert, Marco
			 * @param classfileDOS
			 *            DataOutputStream to which the bytes are written
			 */
			public void writeTo(final DataOutputStream classfileDOS) {
				// write only, if CPInfo is no dummy entry
				if (this.info != null) {
					try {
						classfileDOS.writeByte(this.tag.getByte());

						classfileDOS.write(info);

						if (logger.isDebugEnabled()) {
							logger.debug("CPInfo tag");
							logger.debug("{}", toHexString(tag.getByte()));
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
	 * <h1>MethodArea</h1>
	 * <p>
	 * This class represents all information needed to create a JVM-Classfile
	 * methods' area.
	 * </p>
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 */
	private class MethodArea {
		Logger logger = LoggerFactory.getLogger(this.getClass());

		/**
		 * <h1>methodMap</h1>
		 * <p>
		 * This map maps method names to its Method objects.
		 * </p>
		 * 
		 * @see Method
		 */
		private final HashMap<String, Method> methodMap;

		private MethodArea() {
			this.methodMap = new HashMap<String, Method>();
		}

		/**
		 * <h1>writeTo</h1>
		 * <p>
		 * This method uses an output stream and adds all bytes of the
		 * classfile's method area meeting the jvm standard for javabyte
		 * classfile method areas. To do this, it uses the information in its
		 * member variables and the writeTo methods of its member objects.
		 * </p>
		 * 
		 * @author Robert, Marco
		 * @param classfileDOS
		 *            DataOutputStream to which the bytes are written
		 */
		private void writeTo(final DataOutputStream classfileDOS) {

			try {
				classfileDOS.writeShort(this.methodMap.size());

				if (logger.isDebugEnabled()) {
					logger.debug("method count");
					logger.debug("{}",
							toHexString((short) this.methodMap.size()));
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
		 * <h1>addMethod</h1>
		 * <p>
		 * This method adds and initializes a new Method object to the method
		 * list of this method area.
		 * </p>
		 * 
		 * @author Marco
		 * @since 29.04.2013
		 * @param methodName
		 *            String name of the method
		 * @param methodDescriptor
		 *            String method descriptor as specified by jvm specification
		 * @param accessFlags
		 *            arbitrary amount of method access flags
		 * @see Method
		 */
		private void addMethod(final String methodName,
				final String methodDescriptor,
				final MethodAccessFlag... accessFlags) {
			final Method newMethod = new Method(methodName, methodDescriptor,
					accessFlags);

			methodMap.put(methodName, newMethod);
		}

		/**
		 * <h1>getMethodByMethodName</h1>
		 * <p>
		 * This method gets and returns the method described by the parameter
		 * methodName.
		 * </p>
		 * 
		 * @author Marco
		 * @since 29.04.2013
		 * @param methodName
		 *            String name of method which is supposed to be returned
		 * @return Method object
		 * @see Method
		 */
		private Method getMethodByMethodName(final String methodName) {

			return this.methodMap.get(methodName);
		}

		/**
		 * <h1>addVariableToMethodsCode</h1>
		 * <p>
		 * This method looks up the specified Method object in the method area
		 * of this classfile using the method
		 * {@link #getMethodByMethodName(String)} and adds a new variable to a
		 * methods code by allocating appropriate space in the local variable
		 * table of the method using the looked up Method object's method
		 * {@link Method#addVariableToCodeAttribute(String, swp_compiler_ss13.javabite.backend.IClassfile.VariableType)}
		 * .
		 * </p>
		 * 
		 * @author Marco
		 * @since 29.04.2013
		 * @param methodName
		 *            String name of the method
		 * @param variableName
		 *            String name of the variable
		 * @param variableType
		 *            VariableType variable type of the variable
		 * @see Method
		 * @see Method#addVariableToCodeAttribute(String,
		 *      swp_compiler_ss13.javabite.backend.IClassfile.VariableType)
		 */
		private void addVariableToMethodsCode(final String methodName,
				final String variableName, final VariableType variableType) {

			final Method method = this.getMethodByMethodName(methodName);
			method.addVariableToCodeAttribute(variableName, variableType);
		}

		/**
		 * <h1>getIndexOfVariableInMethod</h1>
		 * <p>
		 * This method looks up the specified Method object in the method area
		 * of this classfile using the method
		 * {@link #getMethodByMethodName(String)} and calls the method
		 * {@link Method#getIndexOfVariable(String)} on this object using the
		 * specified variable name.
		 * </p>
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * @param methodName
		 *            String name of the method
		 * @param variableName
		 *            String name of the variable
		 * @return index of the variable in local variable space of the code
		 *         attribute of the specified method
		 * @see #getMethodByMethodName(String)
		 * @see Method
		 * @see Method#getIndexOfVariable(String)
		 */
		private byte getIndexOfVariableInMethod(final String methodName,
				final String variableName) {
			final Method method = this.getMethodByMethodName(methodName);
			return method.getIndexOfVariable(variableName);
		}

		/**
		 * <h1>addInstructionToMethodsCode</h1>
		 * <p>
		 * This method looks up the specified Method object in the method area
		 * of this classfile using the method
		 * {@link #getMethodByMethodName(String)} and adds a new Instruction to
		 * the code area of the code attribute of the looked up Method object
		 * using the objects method
		 * {@link Method#addInstructionToCodeAttribute(Instruction)}.
		 * </p>
		 * 
		 * @author Marco
		 * @since 30.04.2013
		 * @param methodName
		 *            String name of the method
		 * @param instruction
		 *            instance of class Instruction
		 * @see #getMethodByMethodName(String)
		 * @see Method
		 * @see Method#addInstructionToCodeAttribute(Instruction)
		 * @see Instruction
		 */
		private void addInstructionToMethodsCode(final String methodName,
				final Instruction instruction) {
			final Method method = this.getMethodByMethodName(methodName);
			method.addInstructionToCodeAttribute(instruction);
		}

		/**
		 * <h1>Method</h1>
		 * <p>
		 * This class represents all information needed to create a
		 * JVM-Classfile method.
		 * </p>
		 * 
		 * @author Marco
		 * @since 28.04.2013
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

			/**
			 * <h1>Method</h1>
			 * <p>
			 * This class represents all information needed to create a
			 * JVM-Classfile method.
			 * </p>
			 * 
			 * <h1>Method constructor</h1>
			 * <p>
			 * The constructor adds some constants to this classfile being
			 * needed for a new Method object. Furthermore it sets the method's
			 * {@link #accessFlags} according to the parameters.
			 * </p>
			 * 
			 * @author Marco
			 * @since 28.04.2013
			 * @param methodName
			 *            String name of the method to be instantiated
			 * @param methodDescriptor
			 *            String descriptor of the method to be instantiated
			 * @param accessFlags
			 *            arbitrary amount of MethodAccessFlag
			 * @see #accessFlags
			 * @see Classfile#addUTF8ConstantToConstantPool(String)
			 * @see MethodAccessFlag
			 */
			private Method(final String methodName,
					final String methodDescriptor,
					final MethodAccessFlag... accessFlags) {

				// add constants to this classfile's constant pool
				this.nameIndex = Classfile.this
						.addUTF8ConstantToConstantPool(methodName);
				this.descriptorIndex = Classfile.this
						.addUTF8ConstantToConstantPool(methodDescriptor);

				for (final MethodAccessFlag a : accessFlags) {
					this.accessFlags = (short) (this.accessFlags | a.getValue());
				}

				/*
				 * for convenience the attributes count is set explicitly as we
				 * need only the Code attribute
				 */
				this.attributesCount = 1;
				final short codeIndex = Classfile.this
						.addUTF8ConstantToConstantPool("Code");
				this.codeAttribute = new CodeAttribute(codeIndex);
			}

			/**
			 * <h1>writeTo</h1>
			 * <p>
			 * This method uses an output stream and adds all bytes of the
			 * classfile method area's method meeting the jvm standard for
			 * javabyte classfile method area methods. To do this, it uses the
			 * information in its member variables and the writeTo methods of
			 * its member objects.
			 * </p>
			 * 
			 * @author Robert, Marco
			 * @param classfileDOS
			 *            DataOutputStream to which the bytes are written
			 */
			private void writeTo(final DataOutputStream classfileDOS) {
				try {
					classfileDOS.writeShort(this.accessFlags);
					classfileDOS.writeShort(this.nameIndex);
					classfileDOS.writeShort(this.descriptorIndex);
					classfileDOS.writeShort(this.attributesCount);

					if (logger.isDebugEnabled()) {
						logger.debug("accessFlags, nameIndex, descriptorIndex, attributesCount:");
						logger.debug("{} {} {} {}", toHexString(accessFlags),
								toHexString(nameIndex),
								toHexString(descriptorIndex),
								toHexString(attributesCount));
					}

					codeAttribute.writeTo(classfileDOS);
				} catch (final IOException e) {
					e.printStackTrace();
				}

			}

			/**
			 * <h1>addVariableToCodeAttribute</h1>
			 * <p>
			 * This method adds a new variable to a methods code attribute by
			 * allocating appropriate space in the local variable table of the
			 * method using the CodeAttribute method
			 * {@link CodeAttribute#addVariable(String, swp_compiler_ss13.javabite.backend.IClassfile.VariableType)}
			 * .
			 * </p>
			 * 
			 * @author Marco
			 * @since 29.04.2013
			 * @param variableName
			 *            String name of the variable
			 * @param variableType
			 *            VariableType variable type of the variable
			 * @see VariableType
			 * @see CodeAttribute
			 * @see CodeAttribute#addVariable(String,
			 *      swp_compiler_ss13.javabite.backend.IClassfile.VariableType)
			 */
			private void addVariableToCodeAttribute(final String variableName,
					final VariableType variableType) {

				this.codeAttribute.addVariable(variableName, variableType);
			}

			/**
			 * <h1>getIndexOfVariable</h1>
			 * <p>
			 * This method looks up the index of a variable name in the code
			 * attribute of this method using the CodeAttribute's method
			 * {@link CodeAttribute#getIndexOfVariable(String)}.
			 * </p>
			 * 
			 * @author Marco
			 * @since 30.04.2013
			 * @param variableName
			 *            String name of the variable
			 * @return index of the variable in local variable space of this
			 *         method.
			 * @see CodeAttribute
			 * @see CodeAttribute#getIndexOfVariable(String)
			 */
			private byte getIndexOfVariable(final String variableName) {
				return this.codeAttribute.getIndexOfVariable(variableName);
			}

			/**
			 * <h1>addInstructionToCodeAttribute</h1>
			 * <p>
			 * This method adds a new Instruction object to the code area of the
			 * code attribute of this method using the CodeAttribute's method
			 * {@link CodeAttribute#addInstruction(Instruction)}.
			 * </p>
			 * 
			 * @author Marco
			 * @since 30.04.2013
			 * @param instruction
			 *            instance of class Instruction
			 * @see CodeAttribute
			 * @see CodeAttribute#addInstruction(Instruction)
			 * @see Instruction
			 */
			private void addInstructionToCodeAttribute(
					final Instruction instruction) {

				this.codeAttribute.addInstruction(instruction);
			}

			/**
			 * <h1>CodeAttribute</h1>
			 * <p>
			 * This class represents all information needed to create a
			 * JVM-Classfile method code attribute.
			 * </p>
			 * 
			 * @author Marco
			 * @since 28.04.2013
			 */
			private class CodeAttribute {

				Logger logger = LoggerFactory.getLogger(this.getClass());

				/**
				 * <h1>variableMap</h1>
				 * <p>
				 * This map maps variable names to its indices in the local
				 * variable space. The variable space is 1 byte long.
				 * </p>
				 */
				private final HashMap<String, Byte> variableMap;

				// general code attribute structure information
				private final short codeIndex;
				private short maxStack;
				private short maxLocals;
				/**
				 * <h1>codeArea</h1>
				 * <p>
				 * Ordered List of Instruction objects.
				 * 
				 * @see Instruction </p>
				 */
				private final ArrayList<Instruction> codeArea;
				private final short exceptionTableLength;
				private final short attributesCount;

				/**
				 * <h1>CodeAttribute</h1>
				 * <p>
				 * This class represents all information needed to create a
				 * JVM-Classfile method code attribute.
				 * </p>
				 * 
				 * <h1>CodeAttribute constructor</h1>
				 * <p>
				 * The constructor initializes the CodeAttribute object.
				 * </p>
				 * 
				 * @author Marco
				 * @since 28.04.2013
				 * @param codeIndex
				 *            short index into this classfile's constant pool of
				 *            string "Code".
				 */
				private CodeAttribute(final short codeIndex) {
					this.codeIndex = codeIndex;
					this.variableMap = new HashMap<String, Byte>();
					this.codeArea = new ArrayList<Instruction>();

					this.maxStack = 1;
					this.maxLocals = 1;
					this.exceptionTableLength = 0;
					this.attributesCount = 0;
				};

				/**
				 * <h1>writeTo</h1>
				 * <p>
				 * This method uses an output stream and adds all bytes of the
				 * classfile method area method's code attribute meeting the jvm
				 * standard for javabyte classfile method area method code
				 * attributes. To do this, it uses the information in its member
				 * variables.
				 * </p>
				 * 
				 * @author Robert, Marco
				 * @param classfileDOS
				 *            DataOutputStream to which the bytes are written
				 */
				private void writeTo(final DataOutputStream classfileDOS) {

					final ByteArrayOutputStream attributesBAOS = new ByteArrayOutputStream();
					final DataOutputStream attributesDOS = new DataOutputStream(
							attributesBAOS);

					final ByteArrayOutputStream codeBAOS = new ByteArrayOutputStream();
					final DataOutputStream codeDOS = new DataOutputStream(
							codeBAOS);

					this.maxStack = calculateMaxStack();

					try {
						attributesDOS.writeShort(this.maxStack);
						logger.debug("MAX_STACK: " + this.maxStack);
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
							logger.debug("{}", toHexString(codeIndex));
							logger.debug("code size");
							logger.debug("{}", toHexString(codeDOS.size()));
							logger.debug("code");
							logger.debug("{}",
									hexFromBytes(codeBAOS.toByteArray()));
							logger.debug("attributes size");
							logger.debug("{}",
									toHexString(attributesDOS.size()));
							logger.debug("attributes");
							logger.debug("{}",
									hexFromBytes(attributesBAOS.toByteArray()));
						}
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}

				/**
				 * <h1>calculateMaxStack</h1>
				 * <p>
				 * This method calculates the size the stack maximally can reach
				 * during calculations and returns the value. Therefore it
				 * processes the Instruction objects in the {@link #codeArea}
				 * and uses the Intruction objects' methods
				 * {@link Instruction#getStackChange()}.
				 * </p>
				 * 
				 * @author Eike
				 * @since 13.05.2013
				 * @return short max stack size that can be reached
				 * @see #codeArea
				 * @see Instruction
				 * @see Instruction#getStackChange()
				 */
				private short calculateMaxStack() {
					short maxStackSize = 0;
					short stackSize = 0;
					for (final Instruction instruction : codeArea) {
						stackSize += instruction.getStackChange();
						if (stackSize > maxStackSize) {
							maxStackSize = stackSize;
						}
					}
					return maxStackSize;
				}

				/**
				 * <h1>addVariable</h1>
				 * <p>
				 * This method adds a new variable to the local variable space
				 * considering the variable's name and type. If it's already
				 * existent, nothing will happen.
				 * </p>
				 * 
				 * @author Marco
				 * @since 29.04.2013
				 * @param variableName
				 *            String name of the variable to be added
				 * @param variableType
				 *            VariableType variable type of the variable to be
				 *            added
				 * @see VariableType
				 * @see #variableMap
				 */
				private void addVariable(final String variableName,
						final VariableType variableType) {

					if (!this.variableMap.containsKey(variableName)) {
						this.variableMap.put(variableName,
								(byte) this.maxLocals);
						this.maxLocals += variableType.getLength();
					}
				}

				/**
				 * <h1>getIndexOfVariable</h1>
				 * <p>
				 * This method looks up the variable name in the
				 * {@link #variableMap} of this code attribute.
				 * </p>
				 * 
				 * @author Marco
				 * @since 30.04.2013
				 * @param variableName
				 *            String name of the variable
				 * @return index of the variable in local variable space of this
				 *         code attribute.
				 * @see #variableMap
				 */
				private byte getIndexOfVariable(final String variableName) {
					// TODO check size of variable space overflow -> exception

					if (this.variableMap.containsKey(variableName)) {
						return this.variableMap.get(variableName);
					} else {
						return 0;
					}
				}

				/**
				 * <h1>addInstruction</h1>
				 * <p>
				 * This method adds a new Instruction to the code area of this
				 * code attribute
				 * </p>
				 * 
				 * @author Marco
				 * @since 30.04.2013
				 * @param instruction
				 *            instance of class Instruction
				 * @see #codeArea
				 * @see Instruction
				 */
				private void addInstruction(final Instruction instruction) {

					this.codeArea.add(instruction);
				}
			}
		}
	}
}
