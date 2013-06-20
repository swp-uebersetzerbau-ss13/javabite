package swp_compiler_ss13.javabite.backend.classfile;

import java.util.List;

class StackMapTable {

	// attribute_name_index: index of string "StackMapTable" in constant pool
	private short attributeNameIndex;
	// attribute_length: length of whole attribute
	private int attributeLength;
	// number_of_entries: entries.size()
	// entries: list of entries
	private List<StackMapFrame> entries;

	public StackMapTable(final short attributeNameIndex) {
		this.attributeNameIndex = attributeNameIndex;
		attributeLength = 0;
	}

	/**
	 * @return the attributeNameIndex
	 */
	public short getAttributeNameIndex() {
		return attributeNameIndex;
	}

	/**
	 * @param attributeNameIndex
	 *            the attributeNameIndex to set
	 */
	public void setAttributeNameIndex(final short attributeNameIndex) {
		this.attributeNameIndex = attributeNameIndex;
	}

	/**
	 * @return the attributeLength
	 */
	public int getAttributeLength() {
		return attributeLength;
	}

	/**
	 * @param attributeLength
	 *            the attributeLength to set
	 */
	public void setAttributeLength(final int attributeLength) {
		this.attributeLength = attributeLength;
	}

	public short getNumberOfEntries() {
		return (short) entries.size();
	}

	static abstract class StackMapFrame {

		private final FrameType frameType;
		private final byte frameTypeTag;

		private StackMapFrame(final FrameType frameType, final byte tag) {
			this.frameType = frameType;
			frameTypeTag = tag;
			assert tag >= frameType.tagRangeFrom && tag <= frameType.tagRangeTo;
		}

		private StackMapFrame(final FrameType frameType) {
			this(frameType, frameType.tagRangeFrom);
		}

		/**
		 * @return the frameType
		 */
		public FrameType getFrameType() {
			return frameType;
		}

		/**
		 * @return the frameTypeTag
		 */
		public byte getFrameTypeTag() {
			return frameTypeTag;
		}

		static enum FrameType {
			SAME_FRAME(0, 63), SAME_LOCALS_1_STACK_ITEM_FRAME(64, 127), SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED(
					247), CHOP_FRAME(248, 250), SAME_FRAME_EXTENDED(251), APPEND_FRAME(
					252, 254), FULL_FRAME(255);

			private final byte tagRangeFrom;
			private final byte tagRangeTo;

			FrameType(final int tagRangeFrom, final int tagRangeTo) {
				this.tagRangeFrom = (byte) tagRangeFrom;
				this.tagRangeTo = (byte) tagRangeTo;
			}

			FrameType(final int tagRangeFrom) {
				this(tagRangeFrom, tagRangeFrom);
			}

		}

		static class SameFrame extends StackMapFrame {
			public SameFrame(final byte tag) {
				super(FrameType.SAME_FRAME, tag);
			}
		}

		static class SameLocals1StackItemFrame extends StackMapFrame {
			VerificationTypeInfo stack;

			public SameLocals1StackItemFrame(final byte tag) {
				super(FrameType.SAME_LOCALS_1_STACK_ITEM_FRAME, tag);
			}
		}

		static class SameLocals1StackItemFrameExtended extends StackMapFrame {
			short offsetDelta;
			VerificationTypeInfo stack;

			public SameLocals1StackItemFrameExtended() {
				super(FrameType.SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED);
			}
		}

		static class ChopFrame extends StackMapFrame {
			short offsetDelta;

			public ChopFrame(final byte tag) {
				super(FrameType.CHOP_FRAME, tag);
			}
		}

		static class SameFrameExtended extends StackMapFrame {
			short offsetDelta;

			public SameFrameExtended() {
				super(FrameType.SAME_FRAME_EXTENDED);
			}
		}

		static class AppendFrame extends StackMapFrame {
			short offsetDelta;
			List<VerificationTypeInfo> locals;

			public AppendFrame(final byte tag) {
				super(FrameType.APPEND_FRAME, tag);
			}
		}

		static class FullFrame extends StackMapFrame {
			short offsetDelta;
			List<VerificationTypeInfo> locals;
			List<VerificationTypeInfo> stack;

			public FullFrame() {
				super(FrameType.FULL_FRAME);
			}

		}

		static interface VerificationTypeInfo {
			byte getTag();
		}

		static enum SimpleVerificationTypeInfo implements VerificationTypeInfo {
			TOP_VARIABLE_INFO(0), INTEGER_VARIABLE_INFO(1), FLOAT_VARIABLE_INFO(
					2), LONG_VARIABLE_INFO(4), DOUBLE_VARIABLE_INFO(3), NULL_VARIABLE_INFO(
					5), UNINITIALIZEDTHIS_VARIABLE_INFO(6);

			private final byte tag;

			SimpleVerificationTypeInfo(final int tag) {
				this.tag = (byte) tag;
			}

			@Override
			public byte getTag() {
				return tag;
			}
		}

		static class ObjectVariableInfo implements VerificationTypeInfo {

			short cpoolIndex;

			@Override
			public byte getTag() {
				return 7;
			}

		}

		static class UninitializedVariableInfo implements VerificationTypeInfo {

			short offset;

			@Override
			public byte getTag() {
				return 8;
			}

		}

	}

}
