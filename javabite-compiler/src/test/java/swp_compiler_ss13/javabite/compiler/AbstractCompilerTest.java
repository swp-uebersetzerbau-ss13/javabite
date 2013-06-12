package swp_compiler_ss13.javabite.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
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
		List<ReportType> errors = getErrorList();
		if (errors != null && !errors.isEmpty()) {
			assertEquals(errors, testCompiler.errorList);
		} else {
			if (main == null || !testCompiler.errorList.isEmpty()) {
				System.out.println(testCompiler.errorList);
				fail("Unexpected compilation error for " + getProgFile());	
			}
			JavaClassProcess process = testCompiler.execute(main);
			assertEquals(getResultValue(), process.getReturnValue());
			assertEquals(getOutput(), process.getProcessOutput());
		}
	}

	public abstract String getProgFile();
	public abstract String getOutput();
	public abstract Integer getResultValue();
	public abstract List<ReportType> getErrorList();
}
