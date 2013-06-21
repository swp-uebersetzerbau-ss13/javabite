package swp_compiler_ss13.javabite.backend;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.ClassfileAccessFlag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * <h1>ClassfileTest</h1>
 * <p>
 * Main test-file for testing the Classfile functionality.
 * </p>
 * <p>
 * NOTICE: Most of the methods of the Classfile forward the control to the
 * ConstantPool or MethodArea class. For theese 'forwarding methods' the tests
 * are part of ConstantPoolTest or MethodAreaTest.
 * 
 * 
 * @see ConstantPoolTest
 * @see MethodAreaTest
 */
public class ClassfileTest {

	Classfile classfile;

	@Before
	public void setup() {
		classfile = new Classfile("classname", "thisClassNameEIF",
				"superClassNameEIF", ClassfileAccessFlag.ACC_PUBLIC,
				ClassfileAccessFlag.ACC_SUPER);
	}

	@After
	public void teardown() {

	}

	@Test
	public void testThatInitializeClassfileWorksCorrectly() {
		assertEquals("The classname is set incorrect while initiation",
				classfile.getName(),
				Whitebox.getInternalState(classfile, "name"));
		assertEquals(
				"The thisClassNameEIF field is set incorrect while initiation",
				"thisClassNameEIF",
				Whitebox.getInternalState(classfile, "thisClassNameEIF"));
		assertEquals(
				"The superClassNameEIF field is set incorrect while initiation",
				"superClassNameEIF",
				Whitebox.getInternalState(classfile, "superClassNameEIF"));

		assertNotNull("No constantPool set",
				Whitebox.getInternalState(classfile, "constantPool"));
		assertNotNull("No methodArea set",
				Whitebox.getInternalState(classfile, "methodArea"));

	}

	// TODO:
	@Test
	public void testWriteTo() {

	}

	// TODO:
	// addLongVariableToMethodsCode
	// addDoubleVariableToMethodsCode
	// addStringVariableToMethodsCode
	// addBooleanVariableToMethodsCode
	//
}
