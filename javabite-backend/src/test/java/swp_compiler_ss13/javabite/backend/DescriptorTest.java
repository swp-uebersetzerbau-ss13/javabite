package swp_compiler_ss13.javabite.backend;

import org.junit.Assert;
import org.junit.Test;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

import java.util.ArrayList;
import java.util.List;
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

		c = String[].class;
		System.out.printf(
				"getName(): %s%ngetCanonicalName(): %s%ngetSimpleName(): %s%n",
				c.getName(), c.getCanonicalName(), c.getSimpleName());

		c = void.class;
		System.out.printf(
				"getName(): %s%ngetCanonicalName(): %s%ngetSimpleName(): %s%n",
				c.getName(), c.getCanonicalName(), c.getSimpleName());
	}

	@Test
	public void testUtilsDescriptor() {
		Assert.assertEquals("Z", ClassfileUtils
				.getByQuadruples(new QuadrupleJb(
						Quadruple.Operator.DECLARE_BOOLEAN, "!", "!", "!")));

		final List<Quadruple> tac1 = new ArrayList<>();
		tac1.add(new QuadrupleJb(Quadruple.Operator.DECLARE_ARRAY, "!", "!",
				"!"));
		tac1.add(new QuadrupleJb(Quadruple.Operator.DECLARE_ARRAY, "!", "!",
				"!"));
		tac1.add(new QuadrupleJb(Quadruple.Operator.DECLARE_ARRAY, "!", "!",
				"!"));
		tac1.add(new QuadrupleJb(Quadruple.Operator.DECLARE_ARRAY, "!", "!",
				"!"));
		tac1.add(new QuadrupleJb(Quadruple.Operator.DECLARE_ARRAY, "!", "!",
				"!"));
		tac1.add(new QuadrupleJb(Quadruple.Operator.DECLARE_BOOLEAN, "!", "!",
				"!"));

		Assert.assertEquals("[[[[[Z", ClassfileUtils.getByQuadruples(tac1));

		final List<Quadruple> tac2 = new ArrayList<>();
		tac2.add(new QuadrupleJb(Quadruple.Operator.DECLARE_ARRAY, "!", "!",
				"!"));
		tac2.add(new QuadrupleJb(Quadruple.Operator.DECLARE_STRING, "!", "!",
				"!"));

		Assert.assertEquals("[java/lang/String",
				ClassfileUtils.getByQuadruples(tac2));

		final List<Quadruple> tac3 = new ArrayList<>();
		tac3.add(new QuadrupleJb(Quadruple.Operator.DECLARE_STRUCT, "!", "!",
				"someStruct"));
		tac3.add(new QuadrupleJb(Quadruple.Operator.DECLARE_ARRAY, "!", "!",
				"!"));
		tac3.add(new QuadrupleJb(Quadruple.Operator.DECLARE_ARRAY, "!", "!",
				"!"));
		tac3.add(new QuadrupleJb(Quadruple.Operator.DECLARE_BOOLEAN, "!", "!",
				"!"));
		tac3.add(new QuadrupleJb(Quadruple.Operator.DECLARE_STRING, "!", "!",
				"!"));
		tac3.add(new QuadrupleJb(Quadruple.Operator.DECLARE_STRING, "!", "!",
				"!"));
		tac3.add(new QuadrupleJb(Quadruple.Operator.DECLARE_STRING, "!", "!",
				"!"));

		Assert.assertEquals("someStruct", ClassfileUtils.getByQuadruples(tac3));
	}

}
