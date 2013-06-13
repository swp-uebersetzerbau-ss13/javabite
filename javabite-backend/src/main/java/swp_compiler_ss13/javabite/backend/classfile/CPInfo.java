package swp_compiler_ss13.javabite.backend.classfile;

import java.io.DataOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.javabite.backend.classfile.Classfile.InfoTag;
import swp_compiler_ss13.javabite.backend.utils.ByteUtils;

/**
 * <h1>CPInfo</h1>
 * <p>
 * This class represents all information needed to create a JVM-Classfile
 * constant pool entry. The constant pool of a JVM-Classfile has entries of
 * different kinds, but every entry consists of a tag and of a Byte-List
 * containing further information.
 * </p>
 * 
 * @author Marco
 * @since 27.04.2013
 */
public class CPInfo {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	// General CPInfo structure information
	private final InfoTag tag;
	private final byte[] info;

	CPInfo(final InfoTag tag, final byte[] info) {
		this.tag = tag;
		this.info = info;
	}

	CPInfo() {
		tag = InfoTag.NONE;
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
	 * @author Robert, Marco
	 * @param classfileDOS
	 *            DataOutputStream to which the bytes are written
	 */
	void writeTo(final DataOutputStream classfileDOS) {
		// write only, if CPInfo is no dummy entry
		if (info != null) {
			try {
				classfileDOS.writeByte(tag.getByte());

				classfileDOS.write(info);

				if (logger.isDebugEnabled()) {
					logger.debug("CPInfo tag");
					logger.debug("{}", ByteUtils.byteToHexString(tag.getByte()));
					logger.debug("CPInfo info");
					logger.debug("{}", ByteUtils.byteArrayToHexString(info));
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	public InfoTag getTag() {
		return tag;
	}

	public byte[] getInfo() {
		return info;
	}
}
