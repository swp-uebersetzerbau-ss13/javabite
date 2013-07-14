package swp_compiler_ss13.javabite.backend.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <h1>MethodSignature</h1>
 * <p>
 * This class provides a signature container for methods. Because the rules to
 * assemble such a signature are complicated in some cases, this class was
 * created as a helper class.
 * </p>
 */
public class MethodSignature {

	public final ClassSignature methodClass;
	public final ClassSignature methodReturnClass;
	public final List<ClassSignature> methodArgsClasses;
	public final String methodArgs;
	public final String methodName;
	public final String methodDescriptor;

	/**
	 * Create a new MethodSignature class instance
	 * 
	 * @param methodName
	 *            name of method
	 * @param methodClass
	 *            class in which method is contained
	 * @param methodReturnClass
	 *            class which is returned by method
	 * @param params
	 *            classes of method parameters
	 */
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

	/**
	 * Create a new MethodSignature class instance, private constructor.
	 * 
	 * @param methodName
	 *            name of method
	 * @param methodClass
	 *            class in which method is contained
	 * @param methodReturnClass
	 *            class which is returned by method
	 * @param methodArgsClasses
	 *            classes of method parameters
	 */
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

	/**
	 * <h1>Builder</h1>
	 * <p>
	 * This class helps creating instances of the class MethodSignature.
	 * </p>
	 */
	public static class Builder {

		private final String methodName;
		private ClassSignature methodClass;
		private ClassSignature methodReturnClass;
		private List<ClassSignature> methodArgsClasses;

		/**
		 * Create a new builder instance
		 * 
		 * @param methodName
		 *            name of method to build
		 */
		public Builder(final String methodName) {
			assert methodName != null && methodName.length() > 0;
			this.methodName = methodName;
			this.methodArgsClasses = new ArrayList<>();
		}

		/**
		 * Adds container class information to this builder
		 * 
		 * @param className
		 *            name of class containing the method
		 * @return this builders instance
		 */
		public Builder ofClass(final String className) {
			this.methodClass = new ClassSignature(className);
			return this;
		}

		/**
		 * Adds container class information to this builder. The parts of the
		 * classname are joined together with a delimiter character.
		 * 
		 * @param classNameStart
		 *            first part of the class name
		 * @param classNameParts
		 *            last parts of the class name
		 * @return this builders instance
		 */
		public Builder ofClass(final String classNameStart,
				final String... classNameParts) {
			this.methodClass = new ClassSignature(classNameStart,
					classNameParts);
			return this;
		}

		/**
		 * Adds container class information to this builder.
		 * 
		 * @param clazz
		 *            class which contains the method
		 * @return this builders instance
		 */
		public Builder ofClass(final Class<?> clazz) {
			this.methodClass = new ClassSignature(clazz);
			return this;
		}

		/**
		 * Adds container class information to this builder.
		 * 
		 * @param classSignature
		 *            class which contains the method
		 * @return this builders instance
		 */
		public Builder ofClass(final ClassSignature classSignature) {
			this.methodClass = classSignature;
			return this;
		}

		/**
		 * Adds return class information to this builder
		 * 
		 * @param className
		 *            class which is returned by the method
		 * @return this builders instance
		 */
		public Builder returns(final String className) {
			this.methodReturnClass = new ClassSignature(className);
			return this;
		}

		/**
		 * Adds return class information to this builder. The parts of the
		 * classname are joined together with a delimiter character.
		 * 
		 * @param classNameStart
		 *            first part of the class name
		 * @param classNameParts
		 *            last parts of the class name
		 * @return this builders instance
		 */
		public Builder returns(final String classNameStart,
				final String... classNameParts) {
			this.methodReturnClass = new ClassSignature(classNameStart,
					classNameParts);
			return this;
		}

		/**
		 * Adds return class information to this builder.
		 * 
		 * @param clazz
		 *            class which is returned by the method
		 * @return this builders instance
		 */
		public Builder returns(final Class<?> clazz) {
			this.methodReturnClass = new ClassSignature(clazz);
			return this;
		}

		/**
		 * Adds return class information to this builder.
		 * 
		 * @param classSignature
		 *            class which is returned by the method
		 * @return this builders instance
		 */
		public Builder returns(final ClassSignature classSignature) {
			this.methodReturnClass = classSignature;
			return this;
		}

		/**
		 * Adds argument information to this builder.
		 * 
		 * @param className
		 *            class of argument to add to the method
		 * @return this builders instance
		 */
		public Builder args(final String className) {
			this.methodArgsClasses.add(new ClassSignature(className));
			return this;
		}

		/**
		 * Adds argument information to this builder.
		 * 
		 * @param clazz
		 *            class of argument to add to the method
		 * @return this builders instance
		 */
		public Builder args(final Class<?> clazz) {
			this.methodArgsClasses.add(new ClassSignature(clazz));
			return this;
		}

		/**
		 * Adds argument information to this builder.
		 * 
		 * @param classSignature
		 *            class of argument to add to the method
		 * @return this builders instance
		 */
		public Builder args(final ClassSignature classSignature) {
			this.methodArgsClasses.add(classSignature);
			return this;
		}

		/**
		 * Returns a new instance of the class MethodSignature with the
		 * inforation passed to the builder beforehand.
		 * 
		 * @return new MethodSignature instance
		 */
		public MethodSignature build() {
			if (methodReturnClass == null) {
				methodReturnClass = new ClassSignature(void.class);
			}
			return new MethodSignature(methodName, methodClass,
					methodReturnClass, methodArgsClasses);
		}

	}

}
