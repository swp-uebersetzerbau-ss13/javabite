package swp_compiler_ss13.javabite.backend.classfile;

public class ConstantPoolFullException extends RuntimeException {

	private static final long serialVersionUID = -7951621102400099202L;

	public ConstantPoolFullException() {
		super();
	}

	public ConstantPoolFullException(final String message) {
		super(message);
	}
}
