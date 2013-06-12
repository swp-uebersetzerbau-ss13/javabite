package swp_compiler_ss13.javabite.backend.classfile;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.doubleToByteArray;
import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.intToHexString;
import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.longToByteArray;
import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.shortToByteArray;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.javabite.backend.classfile.IClassfile.InfoTag;
import swp_compiler_ss13.javabite.backend.utils.ByteUtils;

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
public class ConstantPool {

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
	 * @see Classfile#getIndexOfConstantInConstantPool(swp_compiler_ss13.javabite.backend.classfile.IClassfile.InfoTag,
	 *      String)
	 */
	private final Map<String, Short> cpEntryMap;

	public ConstantPool() {
		entryList = new ArrayList<CPInfo>();
		cpEntryMap = new HashMap<String, Short>();
	}

	/**
	 * <h1>writeTo</h1>
	 * <p>
	 * This method uses an output stream and adds all bytes of the classfile's
	 * constant pool meeting the jvm standard for javabyte classfile constant
	 * pools. To do this, it uses the information in its member variables and
	 * the writeTo methods of its member objects.
	 * </p>
	 * 
	 * @author Robert, Marco
	 * @param classfileDOS
	 *            DataOutputStream to which the bytes are written
	 */
	void writeTo(final DataOutputStream classfileDOS) {

		try {
			if (logger.isDebugEnabled()) {
				logger.debug("constantPool size");
				logger.debug("{}", intToHexString(entryList.size() + 1));
			}

			/*
			 * write constant_pool_count specification determines size as size
			 * of cp plus 1
			 */
			classfileDOS.writeShort((short) (entryList.size() + 1));

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
	 * The method checks whether the constant pool max size (1 byte = 256
	 * entries) is reached. When adding a constant to constant pool will
	 * exceeding the max size of the constant pool the RuntimeException
	 * ConstantPoolFullExcetion will be thrown, otherwise the method quit
	 * silently.
	 * </p>
	 * 
	 * @param typeSize
	 *            size of entries needed for store constant to constant pool.
	 *            LONG and DOUBLE need two. The other constants need 1 entry.
	 */
	private void checkConstantPoolSize(final int typeSize) {
		if (entryList.size() + typeSize > 256) {
			throw new ConstantPoolFullExcetion(
					"The ConstantPool is exceeded. You can't store more constants.");
		}
	}

	// TODO: add exception
	/**
	 * <h1>generateConstantLongInfo</h1>
	 * <p>
	 * This method creates a LongInfo-entry meeting the jvm classfile constant
	 * pool CONSTANT_LONG_info standard in the constant pool. If the entry does
	 * not exist yet, it'll be appended to the existing list and the new entry
	 * will be returned. Otherwise the existing entry's index is returned.
	 * </p>
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * @param value
	 *            long value of entry, which is to be generated
	 * @return short index of a LONG info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 */
	short generateConstantLongInfo(final long value) {
		checkConstantPoolSize(2);

		final String key = InfoTag.LONG.name() + value;

		// return existing entry's index, if it exists already
		if (cpMapEntryExists(key)) {
			return getCPMapEntry(key);
		}

		// generate entry
		final byte[] info = longToByteArray(value);
		final CPInfo longInfo = new CPInfo(InfoTag.LONG, info);
		entryList.add(longInfo);
		final CPInfo longInfo2ndPartDummy = new CPInfo();
		entryList.add(longInfo2ndPartDummy);

		final short index = (short) (entryList.size() - 1);
		addCPMapEntry(key, index);
		return index;
	}

	/**
	 * <h1>generateConstantDoubleInfo</h1>
	 * <p>
	 * This method creates a DoubleInfo-entry meeting the jvm classfile constant
	 * pool CONSTANT_DOUBLE_info standard in the constant pool. If the entry
	 * does not exist yet, it'll be appended to the existing list and the new
	 * entry will be returned. Otherwise the existing entry's index is returned.
	 * </p>
	 * 
	 * @author Marco, Robert
	 * @since 29.04.2013
	 * @param value
	 *            double value of entry, which is to be generated
	 * @param keyValue
	 *            original (unparsed) double value as string to assemble map key
	 * @return short index of a DOUBLE info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 */
	short generateConstantDoubleInfo(final double value, final String keyValue) {
		checkConstantPoolSize(2);
		final String key = InfoTag.DOUBLE.name() + keyValue;

		// return existing entry's index, if it exists already
		if (cpMapEntryExists(key)) {
			return getCPMapEntry(key);
		}

		// generate entry
		final byte[] info = doubleToByteArray(value);
		final CPInfo doubleInfo = new CPInfo(InfoTag.DOUBLE, info);
		entryList.add(doubleInfo);
		final CPInfo doubleInfo2ndPartDummy = new CPInfo();
		entryList.add(doubleInfo2ndPartDummy);

		final short index = (short) (entryList.size() - 1);
		addCPMapEntry(key, index);
		return index;
	}

	/**
	 * <h1>generateConstantStringInfo</h1>
	 * <p>
	 * This method creates a StringInfo-entry meeting the jvm classfile constant
	 * pool CONSTANT_STRING_info standard in the constant pool. If the entry
	 * does not exist yet, it'll be appended to the existing list and the new
	 * entry will be returned. Otherwise the existing entry's index is returned.
	 * </p>
	 * 
	 * @author Marco
	 * @since 29.04.2013
	 * @param value
	 *            string value of entry, which is to be generated
	 * @return short index of a STRING info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 */
	short generateConstantStringInfo(String value) {
		checkConstantPoolSize(1);
		final String key = InfoTag.STRING.name() + value;

		// return existing entry's index, if it exists already
		if (cpMapEntryExists(key)) {
			return getCPMapEntry(key);
		}

		assert value.length() >= 2;
		value = new String(value.substring(1, value.length() - 1));
		value = StringEscapeUtils.unescapeJava(value);
		// generate UTF8-entry
		final short nameIndex = generateConstantUTF8Info(value);
		// generate String entry
		final byte[] info = ByteUtils.shortToByteArray(nameIndex);
		final CPInfo stringInfo = new CPInfo(InfoTag.STRING, info);
		entryList.add(stringInfo);

		// return index + 1
		final short index = (short) entryList.size();
		addCPMapEntry(key, index);
		return index;
	}

	/**
	 * <h1>generateConstantClassInfo</h1>
	 * <p>
	 * This method creates a ClassInfo-entry meeting the jvm classfile constant
	 * pool CONSTANT_CLASS_info standard in the constant pool. If the entry does
	 * not exist yet, it'll be appended to the existing list and the new entry
	 * will be returned. Otherwise the existing entry's index is returned.
	 * </p>
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * @param value
	 *            string value of entry, which is to be generated
	 * @return short index of a CLASS info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 */
	short generateConstantClassInfo(final String value) {
		checkConstantPoolSize(1);
		final String key = InfoTag.CLASS.name() + value;

		// return existing entry's index, if it exists already
		if (cpMapEntryExists(key)) {
			return getCPMapEntry(key);
		}

		// generate UTF8-entry
		final short nameIndex = generateConstantUTF8Info(value);
		// generate CLASS-entry
		final byte[] info = ByteUtils.shortToByteArray(nameIndex);
		final CPInfo longInfo = new CPInfo(InfoTag.CLASS, info);
		entryList.add(longInfo);

		// return index + 1
		final short index = (short) entryList.size();
		addCPMapEntry(key, index);
		return index;
	}

	/**
	 * <h1>generateConstantUTF8Info</h1>
	 * <p>
	 * This method creates an UTF8Info-entry meeting the jvm classfile constant
	 * pool CONSTANT_UTF8_info standard in the constant pool. If the entry does
	 * not exist yet, it'll be appended to the existing list and the new entry
	 * will be returned. Otherwise the existing entry's index is returned.
	 * </p>
	 * 
	 * @author Marco
	 * @since 28.04.2013
	 * @param value
	 *            string value of entry, which is to be generated
	 * @return short index of an UTF8 info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 */
	short generateConstantUTF8Info(final String value) {
		checkConstantPoolSize(1);
		final String key = InfoTag.UTF8.name() + value;

		// return existing entry's index, if it exists already
		if (cpMapEntryExists(key)) {
			return getCPMapEntry(key);
		}

		// generate entry
		final Charset c = Charset.availableCharsets().get("UTF-8");
		final byte[] b = value.getBytes(c);
		final ByteBuffer info = ByteBuffer.allocate(b.length + 2);
		info.put(shortToByteArray((short) b.length));
		info.put(b);

		final CPInfo utf8Info = new CPInfo(InfoTag.UTF8, info.array());
		entryList.add(utf8Info);

		// return index + 1
		final short index = (short) entryList.size();
		addCPMapEntry(key, index);
		return index;
	}

	/**
	 * <h1>generateConstantMethodrefInfo</h1>
	 * <p>
	 * This method creates a MethodrefInfo-entry meeting the jvm classfile
	 * constant pool CONSTANT_Methodref_info standard in the constant pool. If
	 * the entry does not exist yet, it'll be appended to the existing list and
	 * the new entry will be returned. Otherwise the existing entry's index is
	 * returned.
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
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
	 *      #jvms-4.4.2>COMSTANT_METHODREF</a>
	 */
	short generateConstantMethodrefInfo(final short classIndex,
			final short nameAndTypeIndex) {
		checkConstantPoolSize(1);
		final String key = InfoTag.METHODREF.name() + classIndex + "."
				+ nameAndTypeIndex;

		// return existing entry's index, if it exists already
		if (cpMapEntryExists(key)) {
			return getCPMapEntry(key);
		}

		if (classIndex != 0 && nameAndTypeIndex != 0) {
			// generate entry
			final ByteBuffer info = ByteBuffer.allocate(4);
			info.put(shortToByteArray(classIndex));
			info.put(shortToByteArray(nameAndTypeIndex));

			final CPInfo methodrefInfo = new CPInfo(InfoTag.METHODREF,
					info.array());
			entryList.add(methodrefInfo);

			// return index + 1
			final short index = (short) entryList.size();
			addCPMapEntry(key, index);
			return index;
		} else {
			return 0;
		}
	}

	/**
	 * <h1>generateConstantFieldrefInfo</h1>
	 * <p>
	 * This method creates a FieldrefInfo-entry meeting the jvm classfile
	 * constant pool CONSTANT_Fieldref_info standard in the constant pool. If
	 * the entry does not exist yet, it'll be appended to the existing list and
	 * the new entry will be returned. Otherwise the existing entry's index is
	 * returned.
	 * </p>
	 * 
	 * @author Marco
	 * @since 30.05.2013
	 * @param classIndex
	 *            short index of a CLASS info entry in this constant pool
	 * @param nameAndTypeIndex
	 *            short index of a NameAndType entry in this constant pool
	 * @return short index of a Fieldref info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 */
	short generateConstantFieldrefInfo(final short classIndex,
			final short nameAndTypeIndex) {
		checkConstantPoolSize(1);
		final String key = InfoTag.FIELDREF.name() + classIndex + "."
				+ nameAndTypeIndex;

		// return existing entry's index, if it exists already
		if (cpMapEntryExists(key)) {
			return getCPMapEntry(key);
		}

		if (classIndex != 0 && nameAndTypeIndex != 0) {
			// generate entry
			final ByteBuffer info = ByteBuffer.allocate(4);
			info.put(shortToByteArray(classIndex));
			info.put(shortToByteArray(nameAndTypeIndex));

			final CPInfo fieldrefInfo = new CPInfo(InfoTag.FIELDREF,
					info.array());
			entryList.add(fieldrefInfo);

			// return index + 1
			final short index = (short) entryList.size();
			addCPMapEntry(key, index);
			return index;
		} else {
			return 0;
		}
	}

	/**
	 * <h1>generateConstantNameAndTypeInfo</h1>
	 * <p>
	 * This method creates an NameAndTypeInfo-entry meeting the jvm classfile
	 * constant pool CONSTANT_NameAndType_info standard in the constant pool. If
	 * the entry does not exist yet, it'll be appended to the existing list and
	 * the new entry will be returned. Otherwise the existing entry's index is
	 * returned.
	 * </p>
	 * 
	 * @author Marco
	 * @since 30.04.2013
	 * @param name
	 *            string name of the method
	 * @param descriptor
	 *            string method descriptor as specified by the jvm specification
	 * @return short index of a NameAndType info entry in the constant pool of
	 *         this classfile meeting the parameters.
	 */
	short generateConstantNameAndTypeInfo(final String name,
			final String descriptor) {
		checkConstantPoolSize(1);
		final String key = InfoTag.NAMEANDTYPE.name() + name + descriptor;

		// return existing entry's index, if it exists already
		if (cpMapEntryExists(key)) {
			return getCPMapEntry(key);
		}

		// check, whether name exists already, else add it
		final String nameKey = InfoTag.UTF8.name() + name;
		final short nameIndex;
		if (cpMapEntryExists(nameKey)) {
			nameIndex = getCPMapEntry(nameKey);
		} else {
			nameIndex = generateConstantUTF8Info(name);
		}

		// check, whether descriptor exists already, else add it
		final String descriptorKey = InfoTag.UTF8.name() + descriptor;
		final short descriptorIndex;
		if (cpMapEntryExists(descriptorKey)) {
			descriptorIndex = getCPMapEntry(descriptorKey);
		} else {
			descriptorIndex = generateConstantUTF8Info(descriptor);
		}

		if (nameIndex != 0 && descriptorIndex != 0) {
			// generate key
			final ByteBuffer info = ByteBuffer.allocate(4);
			info.put(shortToByteArray(nameIndex));
			info.put(shortToByteArray(descriptorIndex));

			final CPInfo nameAndTypeInfo = new CPInfo(InfoTag.NAMEANDTYPE,
					info.array());
			entryList.add(nameAndTypeInfo);

			// return index + 1
			final short index = (short) entryList.size();
			addCPMapEntry(key, index);
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
	 * @see Classfile#getIndexOfConstantInConstantPool(swp_compiler_ss13.javabite.backend.classfile.IClassfile.InfoTag,
	 *      String)
	 */
	public short getIndexOfConstant(final InfoTag constantType,
			final String constantName) {
		final Short index = cpEntryMap.get(constantType.name() + constantName);
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
	public boolean addCPMapEntry(final String key, final short value) {
		cpEntryMap.put(key, value);
		return true;
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
	boolean cpMapEntryExists(final String key) {
		if (cpEntryMap.containsKey(key)) {
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
			return cpEntryMap.get(key);
		}
		return 0;
	}
}
