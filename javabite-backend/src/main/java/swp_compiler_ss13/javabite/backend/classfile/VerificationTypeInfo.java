package swp_compiler_ss13.javabite.backend.classfile;

interface VerificationTypeInfo {

	static enum VerificationType {
		TOP_VARIABLE_INFO(0), INTEGER_VARIABLE_INFO(1), FLOAT_VARIABLE_INFO(2), LONG_VARIABLE_INFO(
				4), DOUBLE_VARIABLE_INFO(3), NULL_VARIABLE_INFO(5), UNINITIALIZED_THIS_VARIABLE_INFO(
				6), OBJECT_VARIABLE_INFO(7), UNINITIALIZED_VARIABLE_INFO(8);

		private final byte tag;

		VerificationType(final int tag) {
			this.tag = (byte) tag;
		}

		public byte getTag() {
			return tag;
		}
	}

	static class TopVariableInfo implements VerificationTypeInfo {

		@Override
		public VerificationType getVerificationType() {
			return VerificationType.TOP_VARIABLE_INFO;
		}

	}

	static class IntegerVariableInfo implements VerificationTypeInfo {

		@Override
		public VerificationType getVerificationType() {
			return VerificationType.INTEGER_VARIABLE_INFO;
		}

	}

	static class FloatVariableInfo implements VerificationTypeInfo {

		@Override
		public VerificationType getVerificationType() {
			return VerificationType.FLOAT_VARIABLE_INFO;
		}

	}

	static class LongVariableInfo implements VerificationTypeInfo {

		@Override
		public VerificationType getVerificationType() {
			return VerificationType.LONG_VARIABLE_INFO;
		}

	}

	static class DoubleVariableInfo implements VerificationTypeInfo {

		@Override
		public VerificationType getVerificationType() {
			return VerificationType.DOUBLE_VARIABLE_INFO;
		}

	}

	static class NullVariableInfo implements VerificationTypeInfo {

		@Override
		public VerificationType getVerificationType() {
			return VerificationType.NULL_VARIABLE_INFO;
		}

	}

	static class UninitializedThisVariableInfo implements VerificationTypeInfo {

		@Override
		public VerificationType getVerificationType() {
			return VerificationType.UNINITIALIZED_THIS_VARIABLE_INFO;
		}

	}

	static class ObjectVariableInfo implements VerificationTypeInfo {

		@Override
		public VerificationType getVerificationType() {
			return VerificationType.OBJECT_VARIABLE_INFO;
		}

	}

	static class UninitializedVariableInfo implements VerificationTypeInfo {

		@Override
		public VerificationType getVerificationType() {
			return VerificationType.UNINITIALIZED_VARIABLE_INFO;
		}

	}

	VerificationType getVerificationType();

}
