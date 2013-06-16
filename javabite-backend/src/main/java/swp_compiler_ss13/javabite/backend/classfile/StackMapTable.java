package swp_compiler_ss13.javabite.backend.classfile;

import java.io.DataOutputStream;
import java.util.List;

public class StackMapTable extends Attribute {

	private List<StackMapFrame> entries;

	public StackMapTable(final short attributeNameIndex) {
		super(attributeNameIndex);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getAttributeLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public short getNumberOfEntries() {
		return entries != null ? (short) entries.size() : 0;
	}

	public List<StackMapFrame> getEntries() {
		return entries;
	}

	@Override
	void writeTo(final DataOutputStream out) {
		// TODO Auto-generated method stub

	}

}
