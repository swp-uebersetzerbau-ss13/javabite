package swp_compiler_ss13.javabite.backend.classfile;

import java.io.DataOutputStream;

interface Attribute {

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
	void writeTo(DataOutputStream out);

}
