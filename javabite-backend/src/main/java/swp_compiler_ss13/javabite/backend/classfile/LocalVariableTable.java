package swp_compiler_ss13.javabite.backend.classfile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class LocalVariableTable implements Attribute {

	private final short attributeNameIndex;
	private final List<VariableInfo> localVariableTable;
	private int attributeLength;

	public LocalVariableTable(final short attributeNameIndex) {
		this.attributeNameIndex = attributeNameIndex;
		this.localVariableTable = new ArrayList<>();
		this.attributeLength = 0;
	}

	public void addLocalVariable(final VariableInfo vi) {
		localVariableTable.add(vi);
		attributeLength += vi.getLength();
	}

	@Override
	public void writeTo(final DataOutputStream out) {
		try {
			out.writeShort(attributeNameIndex);
			out.writeInt(attributeLength);
			out.writeShort(localVariableTable.size());
			for (final VariableInfo vi : localVariableTable) {
				out.write(vi.toByteArray());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
