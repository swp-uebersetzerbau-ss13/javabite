package swp_compiler_ss13.javabite.backend.temp2;

import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.javabite.backend.temp2.OperationTemplate.Operation;

/**
 * The ProgramBuilder class is used to append multiple operations into a program
 * flow, and takes operation templates. These templates sometimes require
 * additional arguments, which can be passed too. Internally, a current byte
 * offset is carried and used to calculate the inner byte offset of each
 * operation, which can consist of multiple bytecode instructions. After adding
 * a new operation, the current offset is increased.
 * 
 * @author eike
 * @since 21.04.2013 15:33:58
 * 
 */
public class ProgramBuilder {

	private int currentOffset;
	private List<Operation> operations;

	public static ProgramBuilder newBuilder(final int startOffset) {
		return new ProgramBuilder(startOffset);
	}

	public static ProgramBuilder newBuilder() {
		return new ProgramBuilder(0);
	}

	private ProgramBuilder(final int startOffset) {
		this.currentOffset = startOffset;
		this.operations = new ArrayList<>();
	}

	private void add(final Operation operation) {
		operations.add(operation);
		currentOffset += operation.getSize();
	}

	public ProgramBuilder add(final OperationTemplate template,
	    final Object... args) {
		add(template.getOperation(currentOffset, args));
		return this;
	}

	public String visualize() {
		// TODO implement
		return null;
	}

	public Byte[] bytes() {
		// TODO implement
		return null;
	}

}
