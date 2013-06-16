package swp_compiler_ss13.javabite.backend.classfile;

import java.io.DataOutputStream;

public abstract class Attribute {

	private final short attributeNameIndex;

	public Attribute(final short attributeNameIndex) {
		this.attributeNameIndex = attributeNameIndex;
	}

	/**
	 * <h1>getAttributeNameIndex</h1>
	 * <p>
	 * The value of the attribute_name_index item must be a valid index into the
	 * constant_pool table. The constant_pool entry at that index must be a
	 * CONSTANT_Utf8_info (§4.4.7) structure representing the name of the
	 * attribute.
	 * </p>
	 * 
	 * @return index of attribute name as string in the constant pool
	 */
	public short getAttributeNameIndex() {
		return attributeNameIndex;
	}

	/**
	 * <h1>getAttributeLength</h1>
	 * <p>
	 * The value of the attribute_length item indicates the length of the
	 * attribute, excluding the initial six bytes.
	 * </p>
	 * 
	 * @return byte size of attribute
	 */
	public abstract int getAttributeLength();

	/**
	 * <h1>writeTo</h1>
	 * <p>
	 * This method uses an output stream and adds all bytes of the classfile
	 * method area method's code attribute meeting the jvm standard for javabyte
	 * classfile method area method code attributes. To do this, it uses the
	 * information in its member variables.
	 * </p>
	 * 
	 * @author Robert, Marco
	 * @param classfileDOS
	 *            DataOutputStream to which the bytes are written
	 */
	abstract void writeTo(DataOutputStream out);

}
