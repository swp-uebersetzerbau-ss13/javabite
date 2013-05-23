package swp_compiler_ss13.javabite.backend;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.backend.Backend;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.javabite.backend.external.QuadrupleImpl;

public class BackendTest {

	private Backend backend;
	private Runtime rt;
	private Process cli1;

	private static final Logger logger = LoggerFactory
			.getLogger(BackendModule.class);

	final static List<Quadruple> tac1 = new ArrayList<Quadruple>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5945245333820772835L;

		{
			add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "l"));
			add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "t1"));
			add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "t2"));
			add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "t3"));
			add(new QuadrupleImpl(Operator.ADD_LONG, "#10", "#23", "t1"));
			add(new QuadrupleImpl(Operator.SUB_LONG, "t1", "#23", "t1"));
			add(new QuadrupleImpl(Operator.DIV_LONG, "#100", "#2", "t2"));
			add(new QuadrupleImpl(Operator.ADD_LONG, "t1", "t2", "t1"));
			add(new QuadrupleImpl(Operator.SUB_LONG, "t1", "#30", "t1"));
			add(new QuadrupleImpl(Operator.DIV_LONG, "#-9", "#3", "t3"));
			add(new QuadrupleImpl(Operator.ADD_LONG, "t1", "t3", "t1"));
			add(new QuadrupleImpl(Operator.ASSIGN_LONG, "t1", "!", "l"));
			add(new QuadrupleImpl(Operator.RETURN, "l", "!", "!"));
		}
	};

	final static List<Quadruple> tac2 = new ArrayList<Quadruple>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3786592292110990399L;

		{
			add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "l"));
			add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "t1"));
			add(new QuadrupleImpl(Operator.ADD_LONG, "#3", "#3", "t1"));
			add(new QuadrupleImpl(Operator.ASSIGN_LONG, "t1", "!", "l"));
			add(new QuadrupleImpl(Operator.RETURN, "l", "!", "!"));
		}
	};

	final static List<Quadruple> tac3 = new ArrayList<Quadruple>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4751308834912884423L;

		{
			add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "l"));
			add(new QuadrupleImpl(Operator.DECLARE_LONG, "!", "!", "t1"));
			add(new QuadrupleImpl(Operator.MUL_LONG, "#3", "#3", "t1"));
			add(new QuadrupleImpl(Operator.ASSIGN_LONG, "t1", "!", "l"));
			add(new QuadrupleImpl(Operator.RETURN, "l", "!", "!"));
		}
	};

	@Before
	public void setup() {
		backend = new BackendModule();
		rt = Runtime.getRuntime();
	}


	public void cleanup() {
		logger.debug("Killing command line interface process...");
		cli1.destroy();
		logger.debug("Command line interface process killed.");
	}

	@Test
	public void testTac1ReturnVal() {
		assertTrue(
				"Generated target code returns unexpected value while execution",
				27 == testToReturnValueOfTac(tac1, 1));
	}

	@Test
	public void testSimpleAddTacTranslation() {
		assertTrue(
				"Generated target code returns unexpected value while execution",
				6 == testToReturnValueOfTac(tac2, 1));
	}

	@Test
	public void testSimpleMulTacTranslation() {
		assertTrue(
				"Generated target code returns unexpected value while execution",
				9 == testToReturnValueOfTac(tac3, 1));
	}

	public long testToReturnValueOfTac(List<Quadruple> tac1, int fileamount) {
		String retVal = null;

		Map<String, InputStream> results = backend.generateTargetCode(tac1);

		assertTrue("Invalid amount of files generated",
				fileamount == results.size());

		for (Entry<String, InputStream> e : results.entrySet()) {
			File outFile = new File(e.getKey());
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(outFile);
				IOUtils.copy(e.getValue(), fos);
				fos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		try {
			logger.debug("Starting command line interface process...");
			cli1 = rt.exec(new String[] { "bash", "-c", "java Program ; $?" });
			logger.debug("Waiting for execution of \"bash -c java Program ; $?\"");
			cli1.waitFor();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					cli1.getErrorStream()));

			retVal = extractReturnValue(reader.readLine());

			logger.debug("value: {}, length: {}", retVal, retVal.length());

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		cleanup();
		long l = Long.valueOf(retVal);

		logger.debug("return value: {}", l);
		return l;
	}
	
	private static String extractReturnValue(String line) {
		String s = line.substring(line.indexOf(':') + 2);
		return s.subSequence(0, s.indexOf(':')).toString();
	}
}