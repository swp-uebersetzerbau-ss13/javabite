package swp_compiler_ss13.javabite.backend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.backend.Backend;
import swp_compiler_ss13.common.backend.BackendException;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.javabite.backend.external.QuadrupleImpl;
import swp_compiler_ss13.javabite.runtime.JavaClassProcess;

public class BackendTest {

	private Backend backend;

	private static final Logger logger = LoggerFactory
			.getLogger(BackendJb.class);

	private static List<Quadruple> asList(final Quadruple... quadruples) {
		return new ArrayList<>(Arrays.asList(quadruples));
	}

	static final List<Quadruple> tac1 = asList(new QuadrupleImpl(
			Operator.DECLARE_LONG, "!", "!", "l"), new QuadrupleImpl(
			Operator.DECLARE_LONG, "!", "!", "t1"), new QuadrupleImpl(
			Operator.DECLARE_LONG, "!", "!", "t2"), new QuadrupleImpl(
			Operator.DECLARE_LONG, "!", "!", "t3"), new QuadrupleImpl(
			Operator.ADD_LONG, "#10", "#23", "t1"), new QuadrupleImpl(
			Operator.SUB_LONG, "t1", "#23", "t1"), new QuadrupleImpl(
			Operator.DIV_LONG, "#100", "#2", "t2"), new QuadrupleImpl(
			Operator.ADD_LONG, "t1", "t2", "t1"), new QuadrupleImpl(
			Operator.SUB_LONG, "t1", "#30", "t1"), new QuadrupleImpl(
			Operator.DIV_LONG, "#-9", "#3", "t3"), new QuadrupleImpl(
			Operator.ADD_LONG, "t1", "t3", "t1"), new QuadrupleImpl(
			Operator.ASSIGN_LONG, "t1", "!", "l"), new QuadrupleImpl(
			Operator.RETURN, "l", "!", "!"));

	static final List<Quadruple> tac2 = asList(new QuadrupleImpl(
			Operator.DECLARE_LONG, "!", "!", "l"), new QuadrupleImpl(
			Operator.DECLARE_LONG, "!", "!", "t1"), new QuadrupleImpl(
			Operator.ADD_LONG, "#3", "#3", "t1"), new QuadrupleImpl(
			Operator.ASSIGN_LONG, "t1", "!", "l"), new QuadrupleImpl(
			Operator.RETURN, "l", "!", "!"));

	static final List<Quadruple> tac3 = asList(new QuadrupleImpl(
			Operator.DECLARE_LONG, "!", "!", "l"), new QuadrupleImpl(
			Operator.DECLARE_LONG, "!", "!", "t1"), new QuadrupleImpl(
			Operator.MUL_LONG, "#3", "#3", "t1"), new QuadrupleImpl(
			Operator.ASSIGN_LONG, "t1", "!", "l"), new QuadrupleImpl(
			Operator.RETURN, "l", "!", "!"));

	static final List<Quadruple> tac4 = asList(new QuadrupleImpl(
			Operator.DECLARE_BOOLEAN, "#false", "!", "b"), new QuadrupleImpl(
			Operator.PRINT_BOOLEAN, "b", "!", "!"), new QuadrupleImpl(
			Operator.DECLARE_STRING, "#\"hello world\"", "!", "s"),
			new QuadrupleImpl(Operator.PRINT_STRING, "s", "!", "!"),
			new QuadrupleImpl(Operator.DECLARE_LONG, "#123456", "!", "l"),
			new QuadrupleImpl(Operator.PRINT_LONG, "l", "!", "!"),
			new QuadrupleImpl(Operator.DECLARE_DOUBLE, "#123.456", "!", "d"),
			new QuadrupleImpl(Operator.PRINT_DOUBLE, "d", "!", "!"));

	static final List<Quadruple> tac5 = asList(new QuadrupleImpl(
			Operator.DECLARE_ARRAY, "#3", "!", "anArray"), new QuadrupleImpl(
			Operator.DECLARE_LONG, "!", "!", "!"), new QuadrupleImpl(
			Operator.ARRAY_SET_LONG, "anArray", "#0", "#1234"),
			new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "l"),
			new QuadrupleImpl(Operator.ARRAY_GET_LONG, "anArray", "#0", "l"),
			new QuadrupleImpl(Operator.PRINT_LONG, "l", "!", "!"));

	static final List<Quadruple> tac6 = asList(new QuadrupleImpl(
			Operator.DECLARE_ARRAY, "#3", "!", "anArray"), new QuadrupleImpl(
			Operator.DECLARE_ARRAY, "#2", "!", "!"), new QuadrupleImpl(
			Operator.DECLARE_LONG, "!", "!", "!"), new QuadrupleImpl(
			Operator.DECLARE_REFERENCE, "!", "!", "r"), new QuadrupleImpl(
			Operator.ARRAY_GET_REFERENCE, "anArray", "#0", "r"),
			new QuadrupleImpl(Operator.ARRAY_SET_LONG, "r", "#0", "#1234"),
			new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "l"),
			new QuadrupleImpl(Operator.ARRAY_GET_LONG, "r", "#0", "l"),
			new QuadrupleImpl(Operator.PRINT_LONG, "l", "!", "!"));

	static final List<Quadruple> tac7 = asList(new QuadrupleImpl(
			Operator.DECLARE_BOOLEAN, "!", "!", "b"), new QuadrupleImpl(
			Operator.NOT_BOOLEAN, "#FALSE", "!", "b"), new QuadrupleImpl(
			Operator.AND_BOOLEAN, "#TRUE", "#TRUE", "b"), new QuadrupleImpl(
			Operator.OR_BOOLEAN, "#FALSE", "#TRUE", "b"));

	static final List<Quadruple> tac8 = asList(new QuadrupleImpl(
			Operator.DECLARE_BOOLEAN, "!", "!", "b"), new QuadrupleImpl(
			Operator.COMPARE_LONG_E, "#1", "#2", "b"), new QuadrupleImpl(
			Operator.PRINT_BOOLEAN, "b", "!", "!"));

	static final List<Quadruple> tac9 = asList(new QuadrupleImpl(
			Operator.BRANCH, "lbl", "!", "!"), new QuadrupleImpl(
			Operator.PRINT_STRING, "#\"error\"", "!", "!"), new QuadrupleImpl(
			Operator.LABEL, "lbl", "!", "!"), new QuadrupleImpl(
			Operator.PRINT_STRING, "#\"success\"", "!", "!"));

	static final List<Quadruple> tac10 = asList(new QuadrupleImpl(
			Operator.PRINT_STRING, "#\"hello, world!\na new line\"", "!", "!"));

	static final List<Quadruple> tac11 = asList(new QuadrupleImpl(
			Operator.DECLARE_LONG, "!", "!", "a"), new QuadrupleImpl(
			Operator.DECLARE_LONG, "!", "!", "b"), new QuadrupleImpl(
			Operator.DECLARE_LONG, "!", "!", "c"), new QuadrupleImpl(
			Operator.ASSIGN_LONG, "#4", "!", "a"), new QuadrupleImpl(
			Operator.ASSIGN_LONG, "#3", "!", "b"), new QuadrupleImpl(
			Operator.ASSIGN_LONG, "#2", "!", "c"), new QuadrupleImpl(
			Operator.ASSIGN_LONG, "#4", "!", "b"), new QuadrupleImpl(
			Operator.ASSIGN_LONG, "b", "!", "a"), new QuadrupleImpl(
			Operator.DECLARE_LONG, "a", "!", "t1"), new QuadrupleImpl(
			Operator.ADD_LONG, "b", "t1", "t1"), new QuadrupleImpl(
			Operator.ADD_LONG, "c", "t1", "c"), new QuadrupleImpl(
			Operator.RETURN, "c", "!", "!"));

	static final List<Quadruple> tac12 = asList(new QuadrupleImpl(
			Operator.DECLARE_BOOLEAN, "!", "!", "b"), new QuadrupleImpl(
			Operator.DECLARE_BOOLEAN, "!", "!", "c"), new QuadrupleImpl(
			Operator.DECLARE_LONG, "!", "!", "l"), new QuadrupleImpl(
			Operator.DECLARE_STRING, "!", "!", "bla"), new QuadrupleImpl(
			Operator.ASSIGN_STRING, "#\"bla\"", "!", "bla"), new QuadrupleImpl(
			Operator.ASSIGN_BOOLEAN, "#TRUE", "!", "b"), new QuadrupleImpl(
			Operator.ASSIGN_BOOLEAN, "#FALSE", "!", "c"), new QuadrupleImpl(
			Operator.ASSIGN_LONG, "#4", "!", "l"), new QuadrupleImpl(
			Operator.BRANCH, "ltrue1", "lfalse1", "b"), new QuadrupleImpl(
			Operator.LABEL, "ltrue1", "!", "!"), new QuadrupleImpl(
			Operator.DECLARE_BOOLEAN, "!", "!", "t1"), new QuadrupleImpl(
			Operator.NOT_BOOLEAN, "b", "!", "t1"), new QuadrupleImpl(
			Operator.OR_BOOLEAN, "c", "t1", "t1"), new QuadrupleImpl(
			Operator.BRANCH, "ltrue2", "lfalse2", "t1"), new QuadrupleImpl(
			Operator.LABEL, "ltrue2", "!", "!"), new QuadrupleImpl(
			Operator.PRINT_STRING, "bla", "!", "!"), new QuadrupleImpl(
			Operator.BRANCH, "lend", "!", "!"), new QuadrupleImpl(
			Operator.LABEL, "lfalse2", "!", "!"), new QuadrupleImpl(
			Operator.ASSIGN_LONG, "#5", "!", "l"), new QuadrupleImpl(
			Operator.LABEL, "lfalse1", "!", "!"), new QuadrupleImpl(
			Operator.LABEL, "lend", "!", "!"), new QuadrupleImpl(
			Operator.RETURN, "l", "!", "!"));

	static final List<Quadruple> tac13 = asList(
			new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "l"),
			new QuadrupleImpl(Operator.DECLARE_DOUBLE, "!", "!", "d"),
			new QuadrupleImpl(Operator.DECLARE_STRING, "!", "!", "s"),
			new QuadrupleImpl(Operator.DECLARE_BOOLEAN, "!", "!", "b"),
			new QuadrupleImpl(Operator.DECLARE_STRING, "!", "!", "linebreak"),
			new QuadrupleImpl(Operator.ASSIGN_STRING, "#\"\n\"", "!",
					"linebreak"),
			new QuadrupleImpl(Operator.ASSIGN_BOOLEAN, "#TRUE", "!", "b"),
			new QuadrupleImpl(Operator.ASSIGN_LONG, "#18121313223", "!", "l"),
			new QuadrupleImpl(Operator.ASSIGN_DOUBLE, "#-23.23e-100", "!", "d"),
			new QuadrupleImpl(Operator.ASSIGN_STRING,
					"#\"jagÄrEttString\"\n\"", "!", "s"), new QuadrupleImpl(
					Operator.PRINT_BOOLEAN, "b", "!", "!"), new QuadrupleImpl(
					Operator.PRINT_STRING, "linebreak", "!", "!"),
			new QuadrupleImpl(Operator.PRINT_LONG, "l", "!", "!"),
			new QuadrupleImpl(Operator.PRINT_STRING, "linebreak", "!", "!"),
			new QuadrupleImpl(Operator.PRINT_DOUBLE, "d", "!", "!"),
			new QuadrupleImpl(Operator.PRINT_STRING, "linebreak", "!", "!"),
			new QuadrupleImpl(Operator.PRINT_STRING, "s", "!", "!"),
			new QuadrupleImpl(Operator.RETURN, "#0", "!", "!"));
	
	static final List<Quadruple> tac14 = asList(
			new QuadrupleImpl(Operator.DECLARE_STRING, "#\"#\"", "!", "s"),
			new QuadrupleImpl(Operator.PRINT_STRING, "s", "!", "!")
	);

	@Before
	public void setup() {
		backend = new BackendJb();
	}

//	@Test
//	public void testTac1ReturnVal() throws BackendException {
//		assertEquals(
//				"Generated target code returns unexpected value while execution",
//				27, testToReturnValueOfTac(tac1, 1));
//	}
//
//	@Test
//	public void testSimpleAddTacTranslation() throws BackendException {
//		assertEquals(
//				"Generated target code returns unexpected value while execution",
//				6, testToReturnValueOfTac(tac2, 1));
//	}
//
//	@Test
//	public void testSimpleMulTacTranslation() throws BackendException {
//		assertEquals(
//				"Generated target code returns unexpected value while execution",
//				9, testToReturnValueOfTac(tac3, 1));
//	}
//
//	@Test
//	public void testTac4ReturnVal() throws BackendException {
//		assertEquals(
//				"Generated target code returns unexpected value while execution",
//				0, testToReturnValueOfTac(tac4, 1));
//	}
//
//	@Test
//	public void testTac5ReturnVal() throws BackendException {
//		assertEquals(
//				"Generated target code returns unexpected value while execution",
//				0, testToReturnValueOfTac(tac5, 1));
//	}
//
//	@Test
//	public void testTac6ReturnVal() throws BackendException {
//		assertEquals(
//				"Generated target code returns unexpected value while execution",
//				0, testToReturnValueOfTac(tac6, 1));
//	}
//
//	@Test
//	public void testTac7ReturnVal() throws BackendException {
//		assertEquals(
//				"Generated target code returns unexpected value while execution",
//				0, testToReturnValueOfTac(tac7, 1));
//	}
//
//	@Test
//	public void testTac8ReturnVal() throws BackendException {
//		assertEquals(
//				"Generated target code returns unexpected value while execution",
//				0, testToReturnValueOfTac(tac8, 1));
//	}
//
//	@Test
//	public void testTac9ReturnVal() throws BackendException {
//		assertEquals(
//				"Generated target code returns unexpected value while execution",
//				0, testToReturnValueOfTac(tac9, 1));
//	}
//
//	@Test
//	public void testTac10ReturnVal() throws BackendException {
//		assertEquals(
//				"Generated target code returns unexpected value while execution",
//				0, testToReturnValueOfTac(tac10, 1));
//	}
//
//	@Test
//	public void testTac11ReturnVal() throws BackendException {
//		assertEquals(
//				"Generated target code returns unexpected value while execution",
//				10, testToReturnValueOfTac(tac11, 1));
//	}
//
//	@Test
//	public void testTac12ReturnVal() throws BackendException {
//		assertEquals(
//				"Generated target code returns unexpected value while execution",
//				5, testToReturnValueOfTac(tac12, 1));
//	}
//
//	@Test
//	public void testTac13ReturnVal() throws BackendException {
//		assertEquals(
//				"Generated target code returns unexpected value while execution",
//				0, testToReturnValueOfTac(tac13, 1));
//	}

	@Test
	public void testTac14ReturnVal() throws BackendException {
		assertEquals(
				"Generated target code returns unexpected value while execution",
				0, testToReturnValueOfTac(tac14, 1));
	}

	public long testToReturnValueOfTac(final List<Quadruple> tac,
			final int fileamount) throws BackendException {
		Integer retVal = null;

		final Map<String, InputStream> results = backend.generateTargetCode(
				"Program", tac);

		assertTrue("Invalid amount of files generated",
				fileamount == results.size());

		File mainFile = null;
		for (final Entry<String, InputStream> e : results.entrySet()) {
			final File outFile = new File("build" + File.separator + e.getKey());
			outFile.getParentFile().mkdirs();

			if (mainFile == null) {
				mainFile = outFile;
			}

			FileOutputStream fos;
			try {
				fos = new FileOutputStream(outFile);
				IOUtils.copy(e.getValue(), fos);
				fos.close();
			} catch (final IOException e1) {
				e1.printStackTrace();
				fail();
			}
		}

		retVal = new JavaClassProcess(mainFile).getReturnValue();

		logger.debug("return value: {}", retVal);
		return retVal;
	}
}