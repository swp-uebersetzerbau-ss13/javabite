package swp_compiler_ss13.javabite.backend;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import swp_compiler_ss13.common.backend.Backend;
import swp_compiler_ss13.common.backend.BackendException;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.runtime.JavaClassProcess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.junit.Assert.*;

public class BackendTest {

	private Backend backend;

	private static final Logger logger = LoggerFactory
			.getLogger(BackendJb.class);

	@Before
	public void setup() {
		backend = new BackendJb();
	}

	@Test
	public void testTac1ReturnVal() throws BackendException, IOException {
		assertEquals(27, testToReturnValueOfTac("tac01", 1));
	}

	@Test
	public void testTac2ReturnVal() throws BackendException, IOException {
		assertEquals(6, testToReturnValueOfTac("tac02", 1));
	}

	@Test
	public void testTac3ReturnVal() throws BackendException, IOException {
		assertEquals(6, testToReturnValueOfTac("tac03", 1));
	}

	@Test
	public void testTac4ReturnVal() throws BackendException, IOException {
		assertEquals(9, testToReturnValueOfTac("tac04", 1));
	}

	@Test
	public void testTac5ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac05", 1));
	}

	@Test
	public void testTac6ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac06", 1));
	}

	@Test
	public void testTac7ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac07", 1));
	}

	@Test
	public void testTac8ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac08", 1));
	}

	@Test
	public void testTac9ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac09", 1));
	}

	@Test
	public void testTac10ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac10", 1));
	}

	@Test
	public void testTac11ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac11", 1));
	}

	@Test
	public void testTac12ReturnVal() throws BackendException, IOException {
		assertEquals(10, testToReturnValueOfTac("tac12", 1));
	}

	@Test
	public void testTac13ReturnVal() throws BackendException, IOException {
		assertEquals(5, testToReturnValueOfTac("tac13", 1));
	}

	@Test
	public void testTac14ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac14", 1));
	}

	@Test
	public void testTac15ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac15", 2));
	}

	@Test
	public void testTac16ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac16", 3));
	}

	@Test
	public void testTac17ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac17", 3));
	}

	@Test
	public void testTac18ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac18", 1));
	}

	@Test
	public void testTac19ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac19", 2));
	}

	@Test
	public void testTac20ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac20", 2));
	}

	@Test
	public void testTac21ReturnVal() throws BackendException, IOException {
		assertEquals(20, testToReturnValueOfTac("tac21", 2));
	}

	@Test
	public void testTac22ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac22", 2));
	}

	@Test
	public void testTac23ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac23", 2));
	}

	@Test
	public void testTac24ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac24", 1));
	}

	@Test
	public void testTac25ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac25", 1));
	}

	@Test
	public void testTac26ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac26", 1));
	}

	@Test
	public void testTac27ReturnVal() throws BackendException, IOException {
		assertEquals(0, testToReturnValueOfTac("tac27", 1));
	}

    @Test
    public void testTac28ReturnVal() throws BackendException, IOException {
        assertEquals(0, testToReturnValueOfTac("tac28", 3));
    }

    @Test
    public void testTac29ReturnVal() throws BackendException, IOException {
        assertEquals(0, testToReturnValueOfTac("tac29", 4));
    }

	public long testToReturnValueOfTac(final String tacFile,
			final int fileamount) throws BackendException, IOException {
		Integer retVal;

		final List<Quadruple> tac = new QuadrupleReader(tacFile)
				.readQuadruples();

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
