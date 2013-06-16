package swp_compiler_ss13.javabite.backend.classfile;

import java.util.List;
import java.util.Stack;

interface StackMapFrame {

	static class Range {

		private final int start;
		private final int end;

		public Range(final int start, final int end) {
			assert start <= end;
			this.start = start;
			this.end = end;
		}

		public boolean isInRange(final int i) {
			return i >= start && i <= end;
		}

	}

	static enum FrameType {
		SAME_FRAME(0, 63), SAME_LOCALS_1_STACK_ITEM_FRAME(64, 127), CHOP_FRAME(
				248, 250), SAME_FRAME_EXTENDED(251), APPEND_FRAME(252, 254), FULL_FRAME(
				255);

		private final Range tagRange;

		FrameType(final int start, final int end) {
			tagRange = new Range(start, end);
		}

		FrameType(final int start) {
			this(start, start);
		}

		public boolean isInRange(final int i) {
			return tagRange.isInRange(i);
		}
	}

	static class SameFrame implements StackMapFrame {

		@Override
		public FrameType getFrameType() {
			return FrameType.SAME_FRAME;
		}

	}

	static class SameLocals1StackItemFrame implements StackMapFrame {

		private Stack<VerificationTypeInfo> stack;

		public int getNumberOfStackItems() {
			return stack != null ? stack.size() : 0;
		}

		public Stack<VerificationTypeInfo> getStack() {
			return stack;
		}

		@Override
		public FrameType getFrameType() {
			return FrameType.SAME_LOCALS_1_STACK_ITEM_FRAME;
		}

	}

	static class ChopFrame implements StackMapFrame {

		private int offsetDelta;

		public int getOffsetDelta() {
			return offsetDelta;
		}

		@Override
		public FrameType getFrameType() {
			return FrameType.CHOP_FRAME;
		}

	}

	static class SameFrameExtended implements StackMapFrame {

		private int offsetDelta;

		public int getOffsetDelta() {
			return offsetDelta;
		}

		@Override
		public FrameType getFrameType() {
			return FrameType.SAME_FRAME_EXTENDED;
		}

	}

	static class AppendFrame implements StackMapFrame {

		private int offsetDelta;
		private List<VerificationTypeInfo> locals;

		public int getOffsetDelta() {
			return offsetDelta;
		}

		public int getNumberOfLocals() {
			return locals != null ? locals.size() : 0;
		}

		public List<VerificationTypeInfo> getLocals() {
			return locals;
		}

		@Override
		public FrameType getFrameType() {
			return FrameType.APPEND_FRAME;
		}

	}

	static class FullFrame implements StackMapFrame {

		private int offsetDelta;
		private List<VerificationTypeInfo> locals;
		private Stack<VerificationTypeInfo> stack;

		public int getOffsetDelta() {
			return offsetDelta;
		}

		public int getNumberOfLocals() {
			return locals != null ? locals.size() : 0;
		}

		public List<VerificationTypeInfo> getLocals() {
			return locals;
		}

		public int getNumberOfStackItems() {
			return stack != null ? stack.size() : 0;
		}

		public Stack<VerificationTypeInfo> getStack() {
			return stack;
		}

		@Override
		public FrameType getFrameType() {
			return FrameType.FULL_FRAME;
		}

	}

	FrameType getFrameType();

}
