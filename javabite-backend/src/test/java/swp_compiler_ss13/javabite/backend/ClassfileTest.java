package swp_compiler_ss13.javabite.backend;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

public class ClassfileTest {

	IClassfile classfile;

	@Before
	public void setup() {
		classfile = new Classfile("classname", "thisClassNameEIF",
				"superClassNameEIF", Classfile.ClassfileAccessFlag.ACC_PUBLIC,
				Classfile.ClassfileAccessFlag.ACC_SUPER);
	}

	@After
	public void teardown() {

	}

	@Test
	public void initializeClassfile() {
		assertEquals("The classname is set incorrect while initiation",
				classfile.getName(),
				(String) Whitebox.getInternalState(classfile, "name"));
		assertEquals(
				"The thisClassNameEIF field is set incorrect while initiation",
				"thisClassNameEIF", (String) Whitebox.getInternalState(
						classfile, "thisClassNameEIF"));
		assertEquals(
				"The superClassNameEIF field is set incorrect while initiation",
				"superClassNameEIF", (String) Whitebox.getInternalState(
						classfile, "superClassNameEIF"));

		assertNotNull("No constantPool set",
				Whitebox.getInternalState(classfile, "constantPool"));
		assertNotNull("No methodArea set",
				Whitebox.getInternalState(classfile, "methodArea"));

	}

	@Test
	public void testWriteTo() {

	}

	@Test
	public void testTranslation() {

	}
}
