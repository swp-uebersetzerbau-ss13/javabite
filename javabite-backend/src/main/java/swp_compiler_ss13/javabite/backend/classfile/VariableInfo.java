package swp_compiler_ss13.javabite.backend.classfile;

class VariableInfo {

	public final short startPc;
	public final short length;
	public final short nameIndex;
	public final short descriptorIndex;
	public final short index;

	public VariableInfo(final short startPc, final short length,
			final short nameIndex, final short descriptorIndex,
			final short index) {
		this.startPc = startPc;
		this.length = length;
		this.nameIndex = nameIndex;
		this.descriptorIndex = descriptorIndex;
		this.index = index;
	}

}
