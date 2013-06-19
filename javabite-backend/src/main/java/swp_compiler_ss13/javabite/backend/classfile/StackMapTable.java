package swp_compiler_ss13.javabite.backend.classfile;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

class StackMapTable implements Attribute {

	private final short attributeNameIndex;
	private final List<StackMapFrame> entries;

	public StackMapTable(final short attributeNameIndex) {
		this.attributeNameIndex = attributeNameIndex;
		this.entries = new ArrayList<>();
	}

	@Override
	public void writeTo(final DataOutputStream out) {
		// TODO Auto-generated method stub

	}

}
