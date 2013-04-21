package swp_compiler_ss13.javabite.backend.temp2;

import java.nio.ByteBuffer;
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

	/**
	 * Creates a new instance of this builder class.
	 * 
	 * @param startOffset
	 *          the initial offset to start with byte counting.
	 * @return a new instance of this builder class
	 */
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

	/**
	 * Adds a new operation by template to this program. The internal offset
	 * counter will take care of the appropriate byte counting.
	 * 
	 * @param template
	 *          the template of the operation to be added
	 * @param args
	 *          the arguments to use with the template. Mind that each operation
	 *          has a set count of arguments and that this operation will fail if
	 *          not the right amount of arguments is supplied.
	 * @return this builder instance
	 */
	public ProgramBuilder add(final OperationTemplate template,
	    final Object... args) {
		add(template.getOperation(currentOffset, args));
		return this;
	}

	/**
	 * Returns a byte array of all operations of this program.
	 * 
	 * @return byte array of operations
	 */
	public byte[] getBytes() {
		int bytesize = 0;
		for (final Operation operation : operations) {
			bytesize += operation.getSize();
		}
		final ByteBuffer bb = ByteBuffer.allocate(bytesize);
		for (final Operation operation : operations) {
			bb.put(operation.getBytes());
		}
		return bb.array();
	}

}
