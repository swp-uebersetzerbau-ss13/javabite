package swp_compiler_ss13.javabite.compiler.jb.misc;

import java.io.File;
import java.util.List;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

public class FibonacciTest extends AbstractCompilerTest {

	@Override
	public String getProgFile() {
		return "jb" + File.separator + "misc" + File.separator + "fibonacci.prog";
	}

	@Override
	public String getOutput() {
		return "5";
	}

	@Override
	public Integer getResultValue() {
		return 5;
	}

	@Override
	public List<ReportType> getErrorList() {
		return null;
	}

}
