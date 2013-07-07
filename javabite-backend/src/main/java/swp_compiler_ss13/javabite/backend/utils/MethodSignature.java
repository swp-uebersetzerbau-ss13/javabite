package swp_compiler_ss13.javabite.backend.utils;

import java.util.ArrayList;
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

	public MethodSignature(final String methodName, final String methodClass,
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

	public MethodSignature(final String methodName, final Class<?> methodClass,
			final Class<?> methodReturnClass, final Class<?>... params) {
		this(methodName, ClassSignature.getClassName(methodClass),
				methodReturnClass, params);
	}

	@Override
	public String toString() {
		// example: java/lang/Integer.toString:(I)Ljava/lang/String;
		return (methodClass != null ? methodClass + "." : "") + methodName
				+ ":" + methodDescriptor;
	}

}
