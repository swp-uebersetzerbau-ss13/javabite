package swp_compiler_ss13.javabite.backend;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import junitx.util.PrivateAccessor;
import static junitx.util.PrivateAccessor.getField;
import static org.junit.Assert.*;

import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.classfile.IClassfile.VariableType;
import swp_compiler_ss13.javabite.backend.classfile.IClassfile.MethodAccessFlag;

public class MethodAreaTest {
	// METHODAREA_TESTS:
	// addMethodToMethodArea
	// addVariableToMethodsCode
	// getIndexOfVariableInMethod
	// addInstructionToMethodsCode
	// addInstructionsToMethodsCode
	Object methodArea;
	HashMap<String, Object> methodMap;

	@SuppressWarnings("unchecked")
	@Before
	public void setup() {
		Classfile classfile = new Classfile("classname", "thisClassNameEIF",
				"superClassNameEIF", Classfile.ClassfileAccessFlag.ACC_PUBLIC,
				Classfile.ClassfileAccessFlag.ACC_SUPER);
		try {
			methodArea = getField(classfile, "methodArea");
			methodMap = (HashMap<String, Object>) PrivateAccessor.getField(
					methodArea, "methodMap");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * <h1>testThatAddMethodSetMethodPropertiesCorrectly</h1>
	 * <p>
	 * Checks if a Method is created and added to the methodMap of the
	 * MethodArea Object
	 * </p>
	 */
	@Test
	public void testThatAddMethodAddsMethodToMethodMap() {
		try {
			int size = (int) PrivateAccessor.invoke(methodMap, "size",
					new Class<?>[] {}, new Object[] {});

			PrivateAccessor.invoke(methodArea, "addMethod",
			/**
			 * Types of Parameter
			 */
			new Class<?>[] { String.class, short.class, short.class,
					short.class, String.class, MethodAccessFlag[].class, },
			/**
			 * Parameterarray
			 */
			new Object[] {
					"main",
					(short) 1,
					(short) 2,
					(short) 3,
					"([Ljava/lang/String;])V})",
					new MethodAccessFlag[] { MethodAccessFlag.ACC_PUBLIC,
							MethodAccessFlag.ACC_STATIC } });

			Object addedMethod = PrivateAccessor.invoke(methodArea,
					"getMethodByMethodName", new Class<?>[] { String.class },
					new Object[] { "main" });

			assertNotNull("Method isn't put in methodMap.", addedMethod);

			int newSize = (int) PrivateAccessor.invoke(methodMap, "size",
					new Class<?>[] {}, new Object[] {});

			assertTrue(
					"Something went wrong while putting method to methodMap - size isn't increased by one",
					(size + 1) == newSize);

			assertEquals(
					"The nameIndex of the added Method isn't set valid",
					((short) PrivateAccessor.getField(addedMethod, "nameIndex")),
					(short) 1);
			assertEquals(
					"The descriptorIndex of the added Method isn't set valid",
					((short) PrivateAccessor.getField(addedMethod,
							"descriptorIndex")), (short) 2);

			/**
			 * codeIndex is used to init a CodeAttribute and saved there. To
			 * obtain it, first get the CodeAttribute instance.
			 */
			Object codeAttribute = PrivateAccessor.getField(addedMethod,
					"codeAttribute");
			short settedCodeIndex = (short) PrivateAccessor.getField(
					codeAttribute, "codeIndex");
			assertEquals("The code of the added Method isn't set valid",
					settedCodeIndex, (short) 3);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testThatMethodReturnsMethodIfContained() {
		try {
			Object method = PrivateAccessor.invoke(methodArea,
					"getMethodByMethodName", new Class<?>[] { String.class },
					new Object[] { "testMethod1" });

			assertTrue("Method shouldn't be in methodArea", method == null);

			addDummyMethod("testMethod1");
			method = PrivateAccessor.invoke(methodArea,
					"getMethodByMethodName", new Class<?>[] { String.class },
					new Object[] { "testMethod1" });

			assertNotNull("Method should be in methodArea", method);

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testThatAddVariablesToMethodCodeWorksForAllVarTypes() {
		addDummyMethod("testMethod");
		try {
			for (VariableType type : VariableType.values()) {
				Object method = PrivateAccessor.invoke(methodArea,
						"getMethodByMethodName",
						new Class<?>[] { String.class },
						new Object[] { "testMethod" });

				Object codeAttribute = PrivateAccessor.getField(method,
						"codeAttribute");
				Object variableMap = PrivateAccessor.getField(codeAttribute,
						"variableMap");

				assertTrue(
						"Variable shouldn't be in variableMap of codeAttribute.",
						null == PrivateAccessor.invoke(variableMap, "get",
								new Class<?>[] { Object.class },
								new Object[] { "varName_" + type.name() }));

				PrivateAccessor.invoke(methodArea, "addVariableToMethodsCode",
						new Class<?>[] { String.class, String.class,
								VariableType.class }, new Object[] {
								"testMethod", "varName_" + type.name(), type });

				assertTrue(
						"Variable should be in variableMap of codeAttribute right now",
						null != PrivateAccessor.invoke(variableMap, "get",
								new Class<?>[] { Object.class },
								new Object[] { "varName_" + type.name() }));
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testThatRightIndexIsReturned() {
		addDummyMethod("testMethod");
		try {
			Object method = PrivateAccessor.invoke(methodArea,
					"getMethodByMethodName", new Class<?>[] { String.class },
					new Object[] { "testMethod" });

			Object codeAttribute = PrivateAccessor.getField(method,
					"codeAttribute");
			short maxLocals = (short) PrivateAccessor.getField(codeAttribute,
					"maxLocals");

			assertTrue("maxLocals should be initial 1.", maxLocals == 1);

			VariableType[] doubleWideTypes = new VariableType[] {
					VariableType.LONG, VariableType.DOUBLE };
			VariableType[] singleWideTypes = new VariableType[] {
					VariableType.STRING, VariableType.BOOLEAN,
					VariableType.AREF };

			short maxLocals_control = 1;
			short expectedIndex = 1;
			for (VariableType type : doubleWideTypes) {

				PrivateAccessor.invoke(methodArea, "addVariableToMethodsCode",
						new Class<?>[] { String.class, String.class,
								VariableType.class }, new Object[] {
								"testMethod", "varName_" + type.name(), type });
				
				maxLocals_control += 2;
				
				short maxLocals_fst = (short) PrivateAccessor.getField(
						codeAttribute, "maxLocals");

				assertTrue("maxLocals holds a wrong value: expected("
						+ maxLocals_control + ")/actuals(" + maxLocals_fst
						+ ").", maxLocals_fst == maxLocals_control);
				/**
				 * maxLocals equals index of variable
				 */
				byte index = (byte) PrivateAccessor.invoke(methodArea,
						"getIndexOfVariableInMethod", new Class<?>[] {
								String.class, String.class }, new Object[] {
								"testMethod", "varName_" + type.name() });

				assertTrue("The index(" + index
						+ ") of the variable isn't like the expected index("
						+ expectedIndex + ")",
						index == (byte) expectedIndex);
				
				expectedIndex+=2;
			}

			for (VariableType type : singleWideTypes) {
				PrivateAccessor.invoke(methodArea, "addVariableToMethodsCode",
						new Class<?>[] { String.class, String.class,
								VariableType.class }, new Object[] {
								"testMethod", "varName_" + type.name(), type });

				short maxLocals_fst = (short) PrivateAccessor.getField(
						codeAttribute, "maxLocals");

				maxLocals_control++;

				assertTrue("maxLocals holds a wrong value: expected("
						+ maxLocals_control + ")/actuals(" + maxLocals_fst
						+ ").", maxLocals_fst == maxLocals_control);
				
				/**
				 * maxLocals equals index of variable
				 */
				byte index = (byte) PrivateAccessor.invoke(methodArea,
						"getIndexOfVariableInMethod", new Class<?>[] {
								String.class, String.class }, new Object[] {
								"testMethod", "varName_" + type.name() });

				assertTrue("The index(" + index
						+ ") of the variable isn't like the expected index("
						+ expectedIndex + ")",
						index == (byte) expectedIndex);
				
				expectedIndex++;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a Method with the given name to the methodMap of the MethodArea.
	 */
	private void addDummyMethod(String methodName) {
		try {
			PrivateAccessor.invoke(methodArea, "addMethod",
			/**
			 * Types of Parameter
			 */
			new Class<?>[] { String.class, short.class, short.class,
					short.class, String.class, MethodAccessFlag[].class, },
			/**
			 * Parameterarray
			 */
			new Object[] {
					methodName,
					(short) 1,
					(short) 2,
					(short) 3,
					"([Ljava/lang/String;])V})",
					new MethodAccessFlag[] { MethodAccessFlag.ACC_PUBLIC,
							MethodAccessFlag.ACC_STATIC } });
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
