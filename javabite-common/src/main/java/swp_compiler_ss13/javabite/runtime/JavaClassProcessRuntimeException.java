package swp_compiler_ss13.javabite.runtime;

public class JavaClassProcessRuntimeException extends RuntimeException {
	public JavaClassProcessRuntimeException(String message) {
		super(message);
	}
	
	public JavaClassProcessRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	private static final long serialVersionUID = 1L;
}
