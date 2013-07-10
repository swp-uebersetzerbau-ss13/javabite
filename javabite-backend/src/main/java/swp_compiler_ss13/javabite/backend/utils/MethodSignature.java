package swp_compiler_ss13.javabite.backend.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: eike Date: 7/7/13 Time: 12:53 AM To change
 * this template use File | Settings | File Templates.
 */
public class MethodSignature {

	public final ClassSignature methodClass;
	public final ClassSignature methodReturnClass;
	public final List<ClassSignature> methodArgsClasses;
	public final String methodArgs;
	public final String methodName;
	public final String methodDescriptor;

	public MethodSignature(final String methodName, final Class<?> methodClass,
			final Class<?> methodReturnClass, final Class<?>... params) {
		this.methodClass = methodClass != null ? new ClassSignature(methodClass)
				: null;
		this.methodName = methodName;
		this.methodReturnClass = new ClassSignature(methodReturnClass);
		if (params == null) {
			methodArgsClasses = null;
			methodArgs = null;
		} else {
			final StringBuilder descriptor = new StringBuilder();
			methodArgsClasses = new ArrayList<>(params.length);
			for (final Class<?> argsClass : params) {
				final ClassSignature args = new ClassSignature(argsClass);
				methodArgsClasses.add(args);
				descriptor.append(args.typeClassName);
			}
			methodArgs = descriptor.toString();
		}
		methodDescriptor = "(" + methodArgs + ")"
				+ this.methodReturnClass.typeClassName;
	}

	private MethodSignature(final String methodName,
			final ClassSignature methodClass,
			final ClassSignature methodReturnClass,
			final List<ClassSignature> methodArgsClasses) {
		this.methodName = methodName;
		this.methodClass = methodClass;
		this.methodReturnClass = methodReturnClass;
		this.methodArgsClasses = Collections
				.unmodifiableList(methodArgsClasses);
		final StringBuilder sb = new StringBuilder();
		for (final ClassSignature cs : methodArgsClasses) {
			sb.append(cs.typeClassName);
		}
		this.methodArgs = sb.toString();
		this.methodDescriptor = "(" + sb.toString() + ")"
				+ this.methodReturnClass.typeClassName;
	}

	@Override
	public String toString() {
		// example: java/lang/Integer.toString:(I)Ljava/lang/String;
		return (methodClass != null ? methodClass + "." : "") + methodName
				+ ":" + methodDescriptor;
	}

	public static class Builder {

		private final String methodName;
		private ClassSignature methodClass;
		private ClassSignature methodReturnClass;
		private List<ClassSignature> methodArgsClasses;

		public Builder(final String methodName) {
			assert methodName != null && methodName.length() > 0;
			this.methodName = methodName;
			this.methodArgsClasses = new ArrayList<>();
		}

		public Builder ofClass(final String className) {
			this.methodClass = new ClassSignature(className);
			return this;
		}

		public Builder ofClass(final String classNameStart,
				final String... classNameParts) {
			this.methodClass = new ClassSignature(classNameStart,
					classNameParts);
			return this;
		}

		public Builder ofClass(final Class<?> clazz) {
			this.methodClass = new ClassSignature(clazz);
			return this;
		}

		public Builder ofClass(final ClassSignature classSignature) {
			this.methodClass = classSignature;
			return this;
		}

		public Builder returns(final String className) {
			this.methodReturnClass = new ClassSignature(className);
			return this;
		}

		public Builder returns(final String classNameStart,
				final String... classNameParts) {
			this.methodReturnClass = new ClassSignature(classNameStart,
					classNameParts);
			return this;
		}

		public Builder returns(final Class<?> clazz) {
			this.methodReturnClass = new ClassSignature(clazz);
			return this;
		}

		public Builder returns(final ClassSignature classSignature) {
			this.methodReturnClass = classSignature;
			return this;
		}

		public Builder args(final String className) {
			this.methodArgsClasses.add(new ClassSignature(className));
			return this;
		}

		public Builder args(final Class<?> clazz) {
			this.methodArgsClasses.add(new ClassSignature(clazz));
			return this;
		}

		public Builder args(final ClassSignature classSignature) {
			this.methodArgsClasses.add(classSignature);
			return this;
		}

		public MethodSignature build() {
			if (methodReturnClass == null) {
				methodReturnClass = new ClassSignature(void.class);
			}
			return new MethodSignature(methodName, methodClass,
					methodReturnClass, methodArgsClasses);
		}

	}

}
