package swp_compiler_ss13.javabite.backend.classfile;

import java.io.DataOutputStream;

public class StackMapTableAttribute extends Attribute {

	public StackMapTableAttribute(short attributeNameIndex) {
		super(attributeNameIndex);
		// TODO Auto-generated constructor stub
	}

	@Override
	int getAttributeLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	void writeTo(DataOutputStream out) {
		// TODO Auto-generated method stub

	}

}
