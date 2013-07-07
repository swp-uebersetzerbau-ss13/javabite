package swp_compiler_ss13.javabite.backend.utils;

/**
 * Created with IntelliJ IDEA. User: eike Date: 7/7/13 Time: 12:52 AM To change
 * this template use File | Settings | File Templates.
 */
public class FieldSignature {

	public final ClassSignature fieldClass;
	public final String fieldName;
	public final ClassSignature fieldType;

	public FieldSignature(final String fieldName, final String fieldClass,
			final String fieldType) {
		this.fieldName = fieldName;
		this.fieldClass = new ClassSignature(fieldClass);
		this.fieldType = new ClassSignature(fieldType);
	}

	public FieldSignature(final String fieldName, final Class<?> fieldClass,
			final Class<?> fieldType) {
		this.fieldClass = new ClassSignature(fieldClass);
		this.fieldName = fieldName;
		this.fieldType = new ClassSignature(fieldType);
	}

	@Override
	public String toString() {
		// example: java/lang/System.out:Ljava/lang/PrintStream;
		return (fieldClass != null ? fieldClass + "." : "") + fieldName + ":"
				+ fieldType.typeClassName;
	}
}
