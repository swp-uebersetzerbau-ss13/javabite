package swp_compiler_ss13.javabite.backend.temp2;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.javabite.backend.Mnemonic;

/**
 * The OperationTemplate class is used to create and store templates for
 * operations. A operation can consist of multiple bytecode instructions and can
 * be inserted anywhere in a program. The instructions are designed to receive a
 * initial offset and then calculate the inner offset of each instruction on
 * their own. This way, only the initial offset has to be carried by the program
 * and the offset calculation is less error prone. <br/>
 * The template is used to check the operation arguments (count).
 * 
 * @author eike
 * @since 21.04.2013 15:28:37
 * 
 */
public class OperationTemplate {

	/**
	 * The Operation class is the representation of a final operation, with
	 * calculated inner byte offset, ready to be appended into a program.
	 * 
	 * @author eike
	 * @since 21.04.2013 15:30:23
	 * 
	 */
	public static class Operation {

		private final byte[] bytes;

		private Operation(final int offset, final List<Mnemonic> instructions,
		    final Object... objects) {
			int size = 0;
			for(final Mnemonic instruction : instructions) {
				size += instruction.getSize();
			}
			ByteBuffer bb = ByteBuffer.allocate(size);
			
			int currentOffset = offset;
			for(final Mnemonic instruction : instructions) {
				// TODO implement
			}
			
			this.bytes = bb.array();
		}
		
		public byte[] getBytes() {
			return bytes;
		}

		public int getSize() {
			return bytes.length;
		}

	}

	/**
	 * The OperationTemplateBuilder is a convenient way to assemble multiple
	 * instructions into one template, which then can be used to produce multiple
	 * operations of the same template, byt maybe with different byte offsets.
	 * 
	 * @author eike
	 * @since 21.04.2013 15:31:04
	 * 
	 */
	public static class OperationTemplateBuilder {

		private List<Mnemonic> instructions;

		private OperationTemplateBuilder() {
			this.instructions = new ArrayList<>();
		}

		/**
		 * Create a new builder instance
		 * 
		 * @return new builder instance
		 */
		public static <T> OperationTemplateBuilder newBuilder() {
			return new OperationTemplateBuilder();
		}

		/**
		 * Add a mnemonic to this builder instance.
		 * 
		 * @param mnemonic
		 *          the mnemonic
		 * @return this builder instance
		 */
		public OperationTemplateBuilder add(final Mnemonic mnemonic) {
			instructions.add(mnemonic);
			return this;
		}

		/**
		 * Assemble all added mnemonics into a new operation template instance.
		 * 
		 * @return new operation template instance
		 */
		public OperationTemplate build() {
			return new OperationTemplate(instructions);
		}

	}

	private final int argumentCount;
	private final List<Mnemonic> instructions;

	private OperationTemplate(final List<Mnemonic> instructions) {
		this.instructions = instructions;
		int argumentCount = 0;
		for (final Mnemonic instruction : instructions) {
			argumentCount += instruction.getArgumentCount();
		}
		this.argumentCount = argumentCount;
	}

	/**
	 * Returns the operation with the calculated offset. Each instruction knows
	 * its own width, which is added to the current offset and used for the next
	 * operation.
	 * 
	 * @param offset
	 *          the initial offset
	 * @param args
	 *          the arguments
	 * @return the operation, starting at offset and with inserted args
	 */
	public Operation getOperation(final int offset, final Object... args) {
		assert argumentCount == args.length;
		return new Operation(offset, instructions, args);
	}

}
