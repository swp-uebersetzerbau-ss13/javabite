package swp_compiler_ss13.javabite.backend;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class BackendTest {

	private Backend backend;
	private Runtime rt;
	private Process cli1;

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
			Operator.DECLARE_LONG, "#5", "!", "l1"), new QuadrupleImpl(
			Operator.DECLARE_LONG, "#2", "!", "l2"), new QuadrupleImpl(
			Operator.DECLARE_BOOLEAN, "!", "!", "b"), new QuadrupleImpl(
			Operator.COMPARE_LONG_E, "l1", "l2", "b"), new QuadrupleImpl(
			Operator.COMPARE_LONG_G, "l1", "l2", "b"), new QuadrupleImpl(
			Operator.COMPARE_LONG_L, "l1", "l2", "b"), new QuadrupleImpl(
			Operator.COMPARE_LONG_GE, "l1", "l2", "b"), new QuadrupleImpl(
			Operator.COMPARE_LONG_LE, "l1", "l2", "b"));

	@Before
	public void setup() {
		backend = new BackendJb();
		rt = Runtime.getRuntime();
	}

	public void cleanup() {
		System.out.println(System.getProperty("os.name"));

		logger.debug("Killing command line interface process...");
		cli1.destroy();
		logger.debug("Command line interface process killed.");
	}

	// @Test
	// public void testTac1ReturnVal() throws BackendException {
	// assertTrue(
	// "Generated target code returns unexpected value while execution",
	// 27 == testToReturnValueOfTac(tac1, 1));
	// }
	//
	// @Test
	// public void testSimpleAddTacTranslation() throws BackendException {
	// assertTrue(
	// "Generated target code returns unexpected value while execution",
	// 6 == testToReturnValueOfTac(tac2, 1));
	// }
	//
	// @Test
	// public void testSimpleMulTacTranslation() throws BackendException {
	// assertTrue(
	// "Generated target code returns unexpected value while execution",
	// 9 == testToReturnValueOfTac(tac3, 1));
	// }
	//
	// @Test
	// public void testTac4ReturnVal() throws BackendException {
	// assertTrue(
	// "Generated target code returns unexpected value while execution",
	// 0 == testToReturnValueOfTac(tac4, 1));
	// }
	//
	// @Test
	// public void testTac5ReturnVal() throws BackendException {
	// assertTrue(
	// "Generated target code returns unexpected value while execution",
	// 0 == testToReturnValueOfTac(tac5, 1));
	// }
	//
	// @Test
	// public void testTac6ReturnVal() throws BackendException {
	// assertTrue(
	// "Generated target code returns unexpected value while execution",
	// 0 == testToReturnValueOfTac(tac6, 1));
	// }
	//
	// @Test
	// public void testTac7ReturnVal() throws BackendException {
	// assertTrue(
	// "Generated target code returns unexpected value while execution",
	// 0 == testToReturnValueOfTac(tac7, 1));
	// }
	//
	@Test
	public void testTac8ReturnVal() throws BackendException {
		assertTrue(
				"Generated target code returns unexpected value while execution",
				0 == testToReturnValueOfTac(tac8, 1));
	}

	public long testToReturnValueOfTac(final List<Quadruple> tac,
			final int fileamount) throws BackendException {
		String retVal = null;

		final Map<String, InputStream> results = backend.generateTargetCode(
				"Program", tac);

		assertTrue("Invalid amount of files generated",
				fileamount == results.size());

		for (final Entry<String, InputStream> e : results.entrySet()) {
			final File outFile = new File(e.getKey());
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(outFile);
				IOUtils.copy(e.getValue(), fos);
				fos.close();
			} catch (final IOException e1) {
				e1.printStackTrace();
			}
		}

		try {
			logger.debug("Starting command line interface process...");
			cli1 = rt.exec(new String[] { "bash", "-c", "java Program ; $?" });
			logger.debug("Waiting for execution of \"bash -c java Program ; $?\"");
			cli1.waitFor();

			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(cli1.getErrorStream()));

			retVal = extractReturnValue(reader.readLine());

			logger.debug("value: {}, length: {}", retVal, retVal.length());

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		cleanup();
		final long l = Long.valueOf(retVal);

		logger.debug("return value: {}", l);
		return l;
	}

	private static String extractReturnValue(final String line) {
		final String s = line.substring(line.indexOf(':') + 2);
		return s.subSequence(0, s.indexOf(':')).toString();
	}
}