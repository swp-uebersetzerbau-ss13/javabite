package swp_compiler_ss13.javabite.backend.classfile;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.shortUpperByte;
import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.shortLowerByte;

class VariableInfo {

	private short length;
	private byte[] info;

	public VariableInfo(final short startPc, final short length,
			final short nameIndex, final short descriptorIndex,
			final short index) {
		this.length = length;
		this.info = new byte[] { shortUpperByte(startPc),
				shortLowerByte(startPc), shortUpperByte(length),
				shortLowerByte(length), shortUpperByte(nameIndex),
				shortLowerByte(nameIndex), shortUpperByte(descriptorIndex),
				shortLowerByte(descriptorIndex), shortUpperByte(index),
				shortLowerByte(index) };
	}

	public short getLength() {
		return length;
	}

	public byte[] toByteArray() {
		return info;
	}

}
