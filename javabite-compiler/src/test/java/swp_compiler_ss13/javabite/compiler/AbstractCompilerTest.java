package swp_compiler_ss13.javabite.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.runtime.JavaClassProcess;

public abstract class AbstractCompilerTest {
	TestCompiler testCompiler;

	@Before
	public void setUp() throws Exception {
		testCompiler = new TestCompiler();
	}

	@Test
	public void test() throws URISyntaxException {
		File main = testCompiler.compile(new File(ClassLoader.getSystemClassLoader().getResource(getProgFile()).toURI().getPath()));
		List<ReportType> errors = getErrorList()!=null ? getErrorList(): new LinkedList<ReportType>();
		if (errors != null && !errors.isEmpty()) {
			assertEquals("Expected errors do not match found errors.", errors, testCompiler.errorList);
		} else {
			assertEquals("Unexpected errors found", errors,testCompiler.errorList);
			assertNotNull("No target code", main);
			JavaClassProcess process = testCompiler.execute(main);
			assertEquals("Output of programm execution does not match", getOutput(), process.getProcessOutput());
			assertEquals("Return value does not match", getResultValue(), process.getReturnValue());
		}
	}

	public abstract String getProgFile();
	public abstract String getOutput();
	public abstract Integer getResultValue();
	/**
	 * 
	 * @return the list of errors or null, which means no errors
	 */
	public abstract List<ReportType> getErrorList();
}
