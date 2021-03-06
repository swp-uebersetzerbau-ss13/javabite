package swp_compiler_ss13.javabite.backend.classfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.FieldAccessFlag;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.intToHexString;

/**
 * <h1>FieldArea</h1>
 * <p>
 * This class represents all information needed to create a jvm classfile fields
 * area. (Actually, there is not something like a field area in the jvm
 * specification, so this "area" stands for the following part of the
 * specification: u2 fields_count; field_info fields[fields_count]; )
 * </p>
 * 
 * @since 19.06.2013
 */
class FieldArea {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * <h1>entryList</h1>
	 * <p>
	 * List of FieldInfo objects representing entries in the field area
	 * </p>
	 */
	private final List<FieldInfo> entryList;

	public FieldArea() {
		entryList = new ArrayList<>();
	}

	/**
	 * <h1>writeTo</h1>
	 * <p>
	 * This method uses an output stream and adds all bytes of the classfile's
	 * field area meeting the jvm standard for javabyte classfile field areas .
	 * To do this, it uses the information in its member variables and the
	 * writeTo methods of its member objects.
	 * </p>
	 * 
	 * @param classfileDOS
	 *            DataOutputStream to which the bytes are written
	 */
	void writeTo(final DataOutputStream classfileDOS) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("field area size");
				logger.debug("{}", intToHexString(entryList.size()));
			}

			/*
			 * write fields count
			 */
			classfileDOS.writeShort((short) entryList.size());

			for (final FieldInfo entry : entryList) {
				entry.writeTo(classfileDOS);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * <h1>addFieldToFieldArea</h1>
	 * <p>
	 * This method adds a field info structure to this field area by
	 * instantiating a new fieldInfo object and adding it to this field area's
	 * entrylist.
	 * </p>
	 * 
	 * @since 19.06.2013
	 * @param nameIndex
	 *            short index into this classfile's constant pool of field's
	 *            string name.
	 * @param descriptorIndex
	 *            short index into this classfile's constant pool of field's
	 *            string descriptor.
	 * @param accessFlags
	 *            list of access flags for the field
	 */
	void addField(final short nameIndex, final short descriptorIndex,
			final FieldAccessFlag... accessFlags) {

		final FieldInfo fieldInfo = new FieldInfo(nameIndex, descriptorIndex,
				accessFlags);
		entryList.add(fieldInfo);
	}
}
