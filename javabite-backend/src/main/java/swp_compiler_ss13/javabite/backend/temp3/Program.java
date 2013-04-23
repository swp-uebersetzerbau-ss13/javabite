package swp_compiler_ss13.javabite.backend.temp3;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class Program {

	public static class ProgramBuilder {

		public static ProgramBuilder newBuilder(final int initialOffset) {
			return new ProgramBuilder(initialOffset);
		}

		public static ProgramBuilder newBuilder() {
			return newBuilder(0);
		}

		private final int initialOffset;
		private int currentOffset;
		private List<Operation> operations;

		private ProgramBuilder(final int initialOffset) {
			this.initialOffset = initialOffset;
			this.currentOffset = initialOffset;
			this.operations = new ArrayList<>();
		}

		public ProgramBuilder add(@Nonnull Operation operation) {
			operations.add(operation);
			return this;
		}

		public Program build() {
			return new Program(operations);
		}

	}

	private final List<Operation> operations;

	private Program(@Nonnull final List<Operation> operations) {
		this.operations = operations;
	}

	public byte[] toBytes() {
		// TODO implement
		return null;
	}

	@Override
	public String toString() {
		// TODO implement
		return null;
	}

	public int getSize() {
		return operations.size();
	}

}
