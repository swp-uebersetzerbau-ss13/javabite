package swp_compiler_ss13.javabite.backend.classfile;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swp_compiler_ss13.javabite.backend.utils.ByteUtils;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.ConstantPoolType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.*;

/**
 * <h1>ConstantPool</h1>
 * <p>
 * This class represents all information needed to create a jvm classfile
 * constant pool being part of every jvm classfile.
 * </p>
 * 
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
	 * @see Classfile#getIndexInConstantPool(swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.ConstantPoolType,
	 *      String)
	 */
	private final Map<String, Short> cpEntryMap;

	public ConstantPool() {
		entryList = new ArrayList<>();
		cpEntryMap = new HashMap<>();
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
			throw new ConstantPoolFullException(
					"The ConstantPool is exceeded. You can't store more constants.");
		}
	}

	/**
	 * <h1>generateConstantLongInfo</h1>
	 * <p>
	 * This method creates a LongInfo-entry meeting the jvm classfile constant
	 * pool CONSTANT_LONG_info standard in the constant pool. If the entry does
	 * not exist yet, it'll be appended to the existing list and the new entry
	 * will be returned. Otherwise the existing entry's index is returned.
	 * </p>
	 * 
	 * @since 27.04.2013
	 * @param value
	 *            long value of entry, which is to be generated
	 * @return short index of a LONG info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
	 *      #jvms-4.4.5>CONSTANT_LONG Specification</a>
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
	 *      #jvms-4.4>Constant Pool Specification</a>
	 */
	short generateConstantLongInfo(final long value) {
		checkConstantPoolSize(2);

		final String key = ConstantPoolType.LONG.name() + value;

		// return existing entry's index, if it exists already
		if (cpMapEntryExists(key)) {
			return getCPMapEntry(key);
		}

		// generate entry
		final byte[] info = longToByteArray(value);
		final CPInfo longInfo = new CPInfo(ConstantPoolType.LONG, info);
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
	 * @since 29.04.2013
	 * @param value
	 *            double value of entry, which is to be generated
	 * @param keyValue
	 *            original (unparsed) double value as string to assemble map key
	 * @return short index of a DOUBLE info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4.5>
	 *      Double info</a>
	 */
	short generateConstantDoubleInfo(final double value, final String keyValue) {
		checkConstantPoolSize(2);
		final String key = ConstantPoolType.DOUBLE.name() + keyValue;

		// return existing entry's index, if it exists already
		if (cpMapEntryExists(key)) {
			return getCPMapEntry(key);
		}

		// generate entry
		final byte[] info = doubleToByteArray(value);
		final CPInfo doubleInfo = new CPInfo(ConstantPoolType.DOUBLE, info);
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
	 * @since 29.04.2013
	 * @param value
	 *            string value of entry, which is to be generated
	 * @param removeQuotationMarks
	 *            determines, whether quotation marks are supposed to be removed
	 *            from value or not
	 * @return short index of a STRING info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
	 *      #jvms-4.4.3>CONSTANT_STRING Specification</a>
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
	 *      #jvms-4.4>Constant Pool Specification</a>
	 */
	short generateConstantStringInfo(String value, boolean removeQuotationMarks) {
		checkConstantPoolSize(1);
		final String key = ConstantPoolType.STRING.name() + value;

		// return existing entry's index, if it exists already
		if (cpMapEntryExists(key)) {
			return getCPMapEntry(key);
		}

		// remove quotation marks, if necessary
		assert value.length() >= 2;
		if (removeQuotationMarks) {
			value = StringEscapeUtils.unescapeJava(value.substring(1,
					value.length() - 1));
		} else {
			value = StringEscapeUtils.unescapeJava(value);
		}
		// generate UTF8-entry
		final short nameIndex = generateConstantUTF8Info(value);
		// generate String entry
		final byte[] info = ByteUtils.shortToByteArray(nameIndex);
		final CPInfo stringInfo = new CPInfo(ConstantPoolType.STRING, info);
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
	 * @since 28.04.2013
	 * @param value
	 *            string value of entry, which is to be generated
	 * @return short index of a CLASS info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
	 *      #jvms-4.4.1>CONSTANT_Class Specification</a>
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
	 *      #jvms-4.4>Constant Pool Specification</a>
	 */
	short generateConstantClassInfo(final String value) {
		checkConstantPoolSize(1);
		final String key = ConstantPoolType.CLASS.name() + value;

		// return existing entry's index, if it exists already
		if (cpMapEntryExists(key)) {
			return getCPMapEntry(key);
		}

		// generate UTF8-entry
		final short nameIndex = generateConstantUTF8Info(value);
		// generate CLASS-entry
		final byte[] info = ByteUtils.shortToByteArray(nameIndex);
		final CPInfo longInfo = new CPInfo(ConstantPoolType.CLASS, info);
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
	 * @since 28.04.2013
	 * @param value
	 *            string value of entry, which is to be generated
	 * @return short index of an UTF8 info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
	 *      #jvms-4.4.7>CONSTANT_UTF8 Specification</a>
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
	 *      #jvms-4.4>Constant Pool Specification</a>
	 */
	short generateConstantUTF8Info(final String value) {
		checkConstantPoolSize(1);
		final String key = ConstantPoolType.UTF8.name() + value;

		// return existing entry's index, if it exists already
		if (cpMapEntryExists(key)) {
			return getCPMapEntry(key);
		}

		// create bytes of CPInfo entry according to jvm specification
		final int strlen = value.length();
		int utflen = 0;
		int c, count = 0;

		// use charAt instead of copying String to char array
		for (int i = 0; i < strlen; i++) {
			c = value.charAt(i);
			if (c >= 0x0001 && c <= 0x007F) {
				utflen++;
			} else if (c > 0x07FF) {
				utflen += 3;
			} else {
				utflen += 2;
			}
		}

		if (utflen > 65535)
			throw new RuntimeException("encoded string too long: " + utflen
					+ " bytes");

		byte[] bytearr;
		bytearr = new byte[utflen + 2];
		bytearr[count++] = (byte) (utflen >>> 8 & 0xFF);
		bytearr[count++] = (byte) (utflen & 0xFF);

		int i;
		for (i = 0; i < strlen; i++) {
			c = value.charAt(i);
			if (!(c >= 0x0001 && c <= 0x007F))
				break;
			bytearr[count++] = (byte) c;
		}

		for (; i < strlen; i++) {
			c = value.charAt(i);
			if (c >= 0x0001 && c <= 0x007F) {
				bytearr[count++] = (byte) c;

			} else if (c > 0x07FF) {
				bytearr[count++] = (byte) (0xE0 | c >> 12 & 0x0F);
				bytearr[count++] = (byte) (0x80 | c >> 6 & 0x3F);
				bytearr[count++] = (byte) (0x80 | c & 0x3F);
			} else {
				bytearr[count++] = (byte) (0xC0 | c >> 6 & 0x1F);
				bytearr[count++] = (byte) (0x80 | c & 0x3F);
			}
		}

		final CPInfo utf8Info = new CPInfo(ConstantPoolType.UTF8, bytearr);
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
	 * @since 30.04.2013
	 * @param classIndex
	 *            short index of a CLASS info entry in this constant pool
	 * @param nameAndTypeIndex
	 *            short index of a NameAndType entry in this constant pool
	 * @return short index of a Methodref info entry in the constant pool of
	 *         this classfile meeting the parameters.
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
	 *      #jvms-4.4.2>CONSTANT_METHODREF Specification</a>
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
	 *      #jvms-4.4>Constant Pool Specification</a>
	 */
	short generateConstantMethodrefInfo(final short classIndex,
			final short nameAndTypeIndex) {
		checkConstantPoolSize(1);
		final String key = ConstantPoolType.METHODREF.name() + classIndex + "."
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

			final CPInfo methodrefInfo = new CPInfo(ConstantPoolType.METHODREF,
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
	 * @since 30.05.2013
	 * @param classIndex
	 *            short index of a CLASS info entry in this constant pool
	 * @param nameAndTypeIndex
	 *            short index of a NameAndType entry in this constant pool
	 * @param fieldName
	 *            string name of the field
	 * @param classNameEIF
	 *            classname encoded in internal form meeting the jvm
	 *            specification
	 * @return short index of a Fieldref info entry in the constant pool of this
	 *         classfile meeting the parameters.
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
	 *      #jvms-4.4.2>CONSTANT_FIELDREG Specification</a>
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
	 *      #jvms-4.4>Constant Pool Specification</a>
	 */
	short generateConstantFieldrefInfo(final short classIndex,
			final short nameAndTypeIndex, final String fieldName,
			final String classNameEIF) {
		checkConstantPoolSize(1);
		final String key = ConstantPoolType.FIELDREF.name() + classNameEIF
				+ fieldName;

		// return existing entry's index, if it exists already
		if (cpMapEntryExists(key)) {
			return getCPMapEntry(key);
		}

		if (classIndex != 0 && nameAndTypeIndex != 0) {
			// generate entry
			final ByteBuffer info = ByteBuffer.allocate(4);
			info.put(shortToByteArray(classIndex));
			info.put(shortToByteArray(nameAndTypeIndex));

			final CPInfo fieldrefInfo = new CPInfo(ConstantPoolType.FIELDREF,
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
	 * @since 30.04.2013
	 * @param name
	 *            string name of the method
	 * @param descriptor
	 *            string method descriptor as specified by the jvm specification
	 * @return short index of a NameAndType info entry in the constant pool of
	 *         this classfile meeting the parameters.
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
	 *      #jvms-4.4.6>CONSTANT_NAME_AND_TYPE Specification</a>
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
	 *      #jvms-4.4>Constant Pool Specification</a>
	 */
	short generateConstantNameAndTypeInfo(final String name,
			final String descriptor) {
		checkConstantPoolSize(1);
		final String key = ConstantPoolType.NAMEANDTYPE.name() + name
				+ descriptor;

		// return existing entry's index, if it exists already
		if (cpMapEntryExists(key)) {
			return getCPMapEntry(key);
		}

		// check, whether name exists already, else add it
		final String nameKey = ConstantPoolType.UTF8.name() + name;
		final short nameIndex;
		if (cpMapEntryExists(nameKey)) {
			nameIndex = getCPMapEntry(nameKey);
		} else {
			nameIndex = generateConstantUTF8Info(name);
		}

		// check, whether descriptor exists already, else add it
		final String descriptorKey = ConstantPoolType.UTF8.name() + descriptor;
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

			final CPInfo nameAndTypeInfo = new CPInfo(
					ConstantPoolType.NAMEANDTYPE, info.array());
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
	 * @since 30.04.2013
	 * @param constantName
	 *            String name of the constant
	 * @param constantType
	 *            InfoTag type of the constant
	 * @return index of the constant in this constant pool
	 * @see Classfile#getIndexInConstantPool(swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.ConstantPoolType,
	 *      String)
	 */
	public short getIndexOfConstant(final ConstantPoolType constantType,
			final String constantName) {
		final Short index = cpEntryMap.get(constantType.name() + constantName);
		if (index != null) {
			return index;
		} else {
			return 0;
		}
	}

	/**
	 * <h1>addCPMapEntry</h1>
	 * <p>
	 * Add a new entry to {@link #cpEntryMap} according to the method's
	 * parameters.
	 * </p>
	 * 
	 * @since 29.04.2013
	 * @param key
	 *            string key which is to be used in the mapping
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
	 * @since 30.04.2013
	 * @param key
	 *            String key which is to be checked
	 * @return true, if entry exists, else false
	 */
	boolean cpMapEntryExists(final String key) {
		return cpEntryMap.containsKey(key);
	}

	/**
	 * <h1>getCPMapEntry</h1>
	 * <p>
	 * This method checks, whether the key still exists using the method
	 * {@link #cpMapEntryExists(String)} and if it does, it'll return the
	 * corresponding value, else 0;
	 * 
	 * @since 30.04.2013
	 * @param key
	 *            String key which is to be checked
	 * @return short index of constant pool entry, if it exists, else 0
	 * @see #cpMapEntryExists(String)
	 */
	public Short getCPMapEntry(final String key) {
		if (cpMapEntryExists(key)) {
			return cpEntryMap.get(key);
		}
		return 0;
	}
}
