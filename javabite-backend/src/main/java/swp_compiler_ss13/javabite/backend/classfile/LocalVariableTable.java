package swp_compiler_ss13.javabite.backend.classfile;

import java.io.DataOutputStream;
import java.util.List;

public class LocalVariableTable extends Attribute {

	private List<VariableInfo> localVariableTable;

	public LocalVariableTable(final short attributeNameIndex) {
		super(attributeNameIndex);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getAttributeLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public short getLocalVariableTableLength() {
		return localVariableTable != null ? (short) localVariableTable.size()
				: 0;
	}

	public List<VariableInfo> getLocalVariableTable() {
		return localVariableTable;
	}

	@Override
	void writeTo(final DataOutputStream out) {
		// TODO Auto-generated method stub

	}

}
