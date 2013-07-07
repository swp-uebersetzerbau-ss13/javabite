package swp_compiler_ss13.javabite.backend;

import org.junit.Assert;
import org.junit.Test;
import swp_compiler_ss13.javabite.backend.utils.ClassSignature;
import swp_compiler_ss13.javabite.backend.utils.FieldSignature;
import swp_compiler_ss13.javabite.backend.utils.MethodSignature;

import java.io.PrintStream;
import java.util.regex.Pattern;

public class DescriptorTest {

	static void describe(final Class<?> clazz, final String pad,
			final String leadin) {
		if (clazz == null)
			return;
		final String type = clazz.isInterface() ? "interface"
				: clazz.isArray() ? "array" : clazz.isPrimitive() ? "primitive"
						: clazz.isEnum() ? "enum" : "class";
		System.out.printf("%s%s%s %s ( %s )%n", pad, leadin, type,
				clazz.getSimpleName(), clazz.getName());
		for (final Class<?> interfaze : clazz.getInterfaces()) {
			describe(interfaze, pad + "   ", "implements ");
		}
		describe(clazz.getComponentType(), pad + "   ", "elements are ");
		describe(clazz.getSuperclass(), pad + "   ", "extends ");
	}

	static void describe(final Class<?> clazz) {
		describe(clazz, "", "");
		System.out.println();
	}

	@Test
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

	@Test
	public void testClassNames() {
		Class<?> c = boolean[].class;
		System.out.printf(
				"getName(): %s%ngetCanonicalName(): %s%ngetSimpleName(): %s%n",
				c.getName(), c.getCanonicalName(), c.getSimpleName());

		c = String[][][].class;
		System.out.printf(
				"getName(): %s%ngetCanonicalName(): %s%ngetSimpleName(): %s%n",
				c.getName(), c.getCanonicalName(), c.getSimpleName());

		c = void.class;
		System.out.printf(
				"getName(): %s%ngetCanonicalName(): %s%ngetSimpleName(): %s%n",
				c.getName(), c.getCanonicalName(), c.getSimpleName());
	}

	static class SimpleClass {
		private long[] l;

		public String m(final int a, final boolean[] b, final String x) {
			return null;
		}
	}

	@Test
	public void testMethodSignature() {
		final MethodSignature m = new MethodSignature("m", SimpleClass.class,
				String.class, int.class, boolean[].class, String.class);
		Assert.assertEquals(
				"swp_compiler_ss13/javabite/backend/DescriptorTest$SimpleClass.m:(I[ZLjava/lang/String;)Ljava/lang/String;",
				m.toString());
	}

	@Test
	public void testFieldSignature() {
		final FieldSignature f = new FieldSignature("l", SimpleClass.class,
				long[].class);
		Assert.assertEquals(
				"swp_compiler_ss13/javabite/backend/DescriptorTest$SimpleClass.l:[J",
				f.toString());

		final FieldSignature out = new FieldSignature("out", System.class,
				PrintStream.class);
		Assert.assertEquals("java/lang/System.out:Ljava/io/PrintStream;",
				out.toString());
	}

	@Test
	public void testClassSignature() {
		final Class<?>[] classes1 = { int.class, String.class, String[].class,
				String[][].class, int[][].class };

		final String[] classes2 = { "java/lang/String", "Ljava/lang/String;",
				"[java/lang/String", "[Ljava/lang/String;" };

		for (final Class<?> clazz : classes1) {
			final ClassSignature sig = new ClassSignature(clazz);
			System.out.println(clazz + " as container: " + sig.className);
			System.out.println(clazz + " as type: " + sig.typeClassName);
		}

		for (final String clazz : classes2) {
			final ClassSignature sig = new ClassSignature(clazz);
			System.out.println(clazz + " as container: " + sig.className);
			System.out.println(clazz + " as type: " + sig.typeClassName);
		}
	}

}
