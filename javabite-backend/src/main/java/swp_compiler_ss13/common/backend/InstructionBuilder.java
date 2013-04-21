package swp_compiler_ss13.common.backend;

/**
 * InstructionBuilder class. The InstructionBuilder class is used to create many
 * bytecode instructions and provides all operations required by the three
 * address code specification of the WhilstCompiler. These function as comfort
 * methods to construct predefined operations fast and reliable.
 * 
 * @author eike
 * @since 21.04.2013 01:25:54
 */
public class InstructionBuilder {

	private final BytecodeAppender bytecodeAppender;

	/**
	 * Create a new instance of InstructionBuilder class.
	 * 
	 * @param bytecodeAppender
	 *          The Appender class to be used to append operations.
	 */
	public InstructionBuilder(final BytecodeAppender bytecodeAppender) {
		this.bytecodeAppender = bytecodeAppender;
	}

	/**
	 * Create a new instance of InstructionBuilder with the BytecodeBuilder as the
	 * predefined Appender class. The BytecodeBuilder is used to build direct
	 * bytecode for file creation and are not read-friendly.
	 * 
	 * @return new instance of InstructionBuilder
	 */
	public static InstructionBuilder createWithBytecodeBuilder() {
		return new InstructionBuilder(new BytecodeBuilder());
	}

	/**
	 * Create a new instance of InstructionBuilder with the BytecodeVisualizer as
	 * the predefined Appender class. The BytecodeVisualizer is used to display
	 * bytecode in a readable format and cannot be used in a classfile.
	 * 
	 * @return new instance of InstructionBuilder
	 */
	public static InstructionBuilder createWithBytecodeVisualizer() {
		return new InstructionBuilder(new BytecodeVisualizer());
	}

	/**
	 * UNARY_MINUS_NUM operation This operation negates a number found at the
	 * localVariable index.
	 * 
	 * @param localVariable
	 *          variable index of value to be negated
	 * @return this InstructionBuilder instance
	 */
	public InstructionBuilder UNARY_MINUS_NUM(final int localVariable) {
		bytecodeAppender.append(Mnemonic.ILOAD, localVariable)
		    .append(Mnemonic.INEG).append(Mnemonic.ISTORE, localVariable);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return bytecodeAppender.toString();
	}

}
