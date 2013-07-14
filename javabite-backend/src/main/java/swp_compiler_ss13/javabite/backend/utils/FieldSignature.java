package swp_compiler_ss13.javabite.backend.utils;

/**
 * <h1>FieldSignature</h1>
 * <p>
 * This class is a helper class to create field signatures used by the jvm to
 * reference class fields.
 * </p>
 */
public class FieldSignature {

	public final ClassSignature fieldClass;
	public final String fieldName;
	public final ClassSignature fieldType;

	/**
	 * Create a new instance of the FieldSignature class
	 * 
	 * @param fieldName
	 *            name of field
	 * @param fieldClass
	 *            class containing the field
	 * @param fieldType
	 *            field class type
	 */
	public FieldSignature(final String fieldName, final String fieldClass,
			final String fieldType) {
		this.fieldName = fieldName;
		this.fieldClass = new ClassSignature(fieldClass);
		this.fieldType = new ClassSignature(fieldType);
	}

	/**
	 * Create a new instance of the FieldSignature class
	 * 
	 * @param fieldName
	 *            name of field
	 * @param fieldClass
	 *            class containing the field
	 * @param fieldType
	 *            field class type
	 */
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
