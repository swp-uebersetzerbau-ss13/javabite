package swp_compiler_ss13.javabite.backend.classfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swp_compiler_ss13.javabite.backend.utils.ByteUtils;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.FieldAccessFlag;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <h1>FieldInfo</h1>
 * <p>
 * This class represents all information needed to create a JVM-Classfile field
 * info structure.
 * </p>
 * 
 * @since 19.06.2013
 */
class FieldInfo {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// General field info structure information
	private short accessFlags;
	private final short nameIndex;
	private final short descriptorIndex;
	private final short attributesCount = 0;

	/**
	 * <h1>FieldInfo</h1>
	 * <p>
	 * This class represents all information needed to create a JVM-Classfile
	 * field info structure.
	 * </p>
	 * 
	 * <h1>FieldInfo constructor</h1>
	 * <p>
	 * The constructor initializes the field info structure object.
	 * </p>
	 * 
	 * @since 19.06.2013
	 * @param nameIndex
	 *            short index into this classfile's constant pool of field's
	 *            string name.
	 * @param descriptorIndex
	 *            short index into this classfile's constant pool of field's
	 *            string descriptor.
	 * @param fieldAccessFlags
	 */
	FieldInfo(final short nameIndex, final short descriptorIndex,
			final FieldAccessFlag... fieldAccessFlags) {

		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;

		for (final FieldAccessFlag faf : fieldAccessFlags) {
			accessFlags = (short) (accessFlags | faf.value);
		}
	}

	/**
	 * <h1>writeTo</h1>
	 * <p>
	 * This method uses an output stream and adds all bytes of a classfile's
	 * field area's field info object meeting the jvm standard for javabyte
	 * classfile field area's field info structures. To do this, it uses the
	 * information in its member variables.
	 * </p>
	 * 
	 * @param classfileDOS
	 *            DataOutputStream to which the bytes are written
	 */
	void writeTo(final DataOutputStream classfileDOS) {
		try {

			classfileDOS.writeShort(accessFlags);
			classfileDOS.writeShort(nameIndex);
			classfileDOS.writeShort(descriptorIndex);
			classfileDOS.writeShort(attributesCount);

			if (logger.isDebugEnabled()) {
				logger.debug("Field Info Structure");
				logger.debug("Access Flags");
				logger.debug("{}", ByteUtils.shortToHexString(accessFlags));
				logger.debug("Name Index");
				logger.debug("{}", ByteUtils.shortToHexString(nameIndex));
				logger.debug("Descriptor Index");
				logger.debug("{}", ByteUtils.shortToHexString(descriptorIndex));
				logger.debug("Attributes Count");
				logger.debug("{}", ByteUtils.shortToHexString(attributesCount));
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}

	}
}
