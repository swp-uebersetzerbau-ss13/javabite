package swp_compiler_ss13.javabite.backend;

import java.util.regex.Pattern;

import org.junit.Test;

public class DescriptorTest {

	static void describe(Class<?> clazz, String pad, String leadin) {
		if (clazz == null)
			return;
		String type = clazz.isInterface() ? "interface"
				: clazz.isArray() ? "array" : clazz.isPrimitive() ? "primitive"
						: clazz.isEnum() ? "enum" : "class";
		System.out.printf("%s%s%s %s ( %s )%n", pad, leadin, type,
				clazz.getSimpleName(), clazz.getName());
		for (Class<?> interfaze : clazz.getInterfaces()) {
			describe(interfaze, pad + "   ", "implements ");
		}
		describe(clazz.getComponentType(), pad + "   ", "elements are ");
		describe(clazz.getSuperclass(), pad + "   ", "extends ");
	}

	static void describe(Class<?> clazz) {
		describe(clazz, "", "");
		System.out.println();
	}

	// @Test
	public void testDescribe() {
		describe(boolean[][].class);
		describe(java.math.RoundingMode.class);
		describe(java.util.ArrayList.class);
		describe(void.class);
	}

	public static final Pattern P_PACKAGE_DOTS = Pattern.compile("\\.");

	public String dotsToSlashes(final String s) {
		return P_PACKAGE_DOTS.matcher(s).replaceAll("/");
	}

	public String getMethodDescriptor(final String name,
			final Class<?> container, final Class<?> returnType1,
			final String returnType2, final Class<?>... params) {
		final StringBuilder sb = new StringBuilder();
		if (!name.equals("main")) {
			sb.append(dotsToSlashes(container.getName())).append(".\"")
					.append(name).append("\":");
		}
		sb.append("(");
		if (params != null) {
			for (final Class<?> param : params) {
				final String paramName = dotsToSlashes(param.getName());
				sb.append(paramName);
				if (!paramName.endsWith(";")) {
					sb.append(";");
				}
			}
		}
		sb.append(")");
		if (returnType1 != null) {
			sb.append(dotsToSlashes(returnType1.getName()));
		} else if (name.startsWith("<init>") || returnType2 == null) {
			sb.append("V");
		} else {
			sb.append(returnType2);
		}
		return sb.toString();
	}

	@Test
	public void testMethodDescriptor() {
		System.out.println(getMethodDescriptor("<init>", Object.class, null,
				"V"));
	}

	// @Test
	public void testClassNames() {
		Class<?> c = boolean[][].class;
		System.out.printf(
				"getName(): %s%ngetCanonicalName(): %s%ngetSimpleName(): %s%n",
				c.getName(), c.getCanonicalName(), c.getSimpleName());

		c = String[].class;
		System.out.printf(
				"getName(): %s%ngetCanonicalName(): %s%ngetSimpleName(): %s%n",
				c.getName(), c.getCanonicalName(), c.getSimpleName());

		c = void.class;
		System.out.printf(
				"getName(): %s%ngetCanonicalName(): %s%ngetSimpleName(): %s%n",
				c.getName(), c.getCanonicalName(), c.getSimpleName());
	}

}
