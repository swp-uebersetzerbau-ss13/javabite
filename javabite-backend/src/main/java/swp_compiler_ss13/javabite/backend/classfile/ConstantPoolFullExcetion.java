package swp_compiler_ss13.javabite.backend.classfile;

public class ConstantPoolFullExcetion extends RuntimeException {

	private static final long serialVersionUID = -7951621102400099202L;

	public ConstantPoolFullExcetion() {
		super();
	}

	public ConstantPoolFullExcetion(final String message) {
		super(message);
	}
}
