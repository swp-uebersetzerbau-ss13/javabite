package swp_compiler_ss13.javabite.backend;

import org.junit.Test;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils;

import java.io.PrintStream;

public class ClassfileUtilsTest {

	@Test
	public void testClassSignature1() {
		final ClassfileUtils.ClassSignature c = new ClassfileUtils.ClassSignature(
				String.class);
		System.out
				.println("class as container: " + c.getClassNameAsContainer());
		System.out.println("class as type: " + c.getClassNameAsType());
        System.out.println("is primitive: " + c.isPrimitive);
	}

	@Test
	public void testClassSignature2() {
		final ClassfileUtils.ClassSignature c = new ClassfileUtils.ClassSignature(
				"Program_something");
		System.out
				.println("class as container: " + c.getClassNameAsContainer());
		System.out.println("class as type: " + c.getClassNameAsType());
        System.out.println("is primitive: " + c.isPrimitive);
	}

	@Test
	public void testClassSignature3() {
		final ClassfileUtils.ClassSignature c = new ClassfileUtils.ClassSignature(
				int.class);
		System.out
				.println("class as container: " + c.getClassNameAsContainer());
		System.out.println("class as type: " + c.getClassNameAsType());
        System.out.println("is primitive: " + c.isPrimitive);
	}

	@Test
	public void testClassSignature4() {
		final ClassfileUtils.ClassSignature c = new ClassfileUtils.ClassSignature(
				"J");
		System.out
				.println("class as container: " + c.getClassNameAsContainer());
		System.out.println("class as type: " + c.getClassNameAsType());
        System.out.println("is primitive: " + c.isPrimitive);
	}

	@Test
	public void testMethodSignature1() {
		final ClassfileUtils.MethodSignature m = new ClassfileUtils.MethodSignature(
				"<init>", "Program_something", void.class, String.class);
		System.out.println("method toString:  " + m.toString());
	}

	@Test
	public void testFieldSignature1() {
		final ClassfileUtils.FieldSignature f = new ClassfileUtils.FieldSignature(
				"out", System.class, PrintStream.class);
		System.out.println("field toString: " + f.toString());
	}

}
