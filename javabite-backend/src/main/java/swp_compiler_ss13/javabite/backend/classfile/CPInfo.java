package swp_compiler_ss13.javabite.backend.classfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swp_compiler_ss13.javabite.backend.utils.ByteUtils;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.ConstantPoolType;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * <h1>CPInfo</h1>
 * <p>
 * This class represents all information needed to create a JVM-Classfile
 * constant pool entry. The constant pool of a JVM-Classfile has entries of
 * different kinds, but every entry consists of a tagByte and of a Byte-List
 * containing further information.
 * </p>
 * 
 * @since 27.04.2013
 */
public class CPInfo {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// General CPInfo structure information
	private final ConstantPoolType tag;
	private final byte[] info;

	CPInfo(final ConstantPoolType tag, final byte[] info) {
		this.tag = tag;
		this.info = info;
	}

	CPInfo() {
		tag = ConstantPoolType.NONE;
		info = null;
	}

	/**
	 * <h1>writeTo</h1>
	 * <p>
	 * This method uses an output stream and adds all bytes of a classfile's
	 * constant pools CPInfo object meeting the jvm standard for javabyte
	 * classfile constant pool CPInfo structures. To do this, it uses the
	 * information in its member variables.
	 * </p>
	 * 
	 * @param classfileDOS
	 *            DataOutputStream to which the bytes are written
	 */
	void writeTo(final DataOutputStream classfileDOS) {
		// write only, if CPInfo is no dummy entry
		if (info != null) {
			try {
				classfileDOS.writeByte(tag.tagByte);

				classfileDOS.write(info);

				if (logger.isDebugEnabled()) {
					logger.debug("CPInfo tagByte");
					logger.debug("{}", ByteUtils.byteToHexString(tag.tagByte));
					logger.debug("CPInfo info");
					logger.debug("{}", ByteUtils.byteArrayToHexString(info));
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	public ConstantPoolType getTag() {
		return tag;
	}

	public byte[] getInfo() {
		return info;
	}
}
