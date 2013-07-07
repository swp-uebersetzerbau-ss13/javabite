package swp_compiler_ss13.javabite.compiler.predefined.m3;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FibTest extends AbstractCompilerTest {

	@Override
	public String getProgFile() {
		return "predefined" + File.separator + "m3" + File.separator
				+ "fib.prog";
	}

	@Override
	public String getOutput() {
		return "6765";
	}

	@Override
	public Integer getResultValue() {
		return 610;
	}

	@Override
	public List<ReportType> getErrorList() {
		List<ReportType> errors = new ArrayList<>();
		return errors;
	}

}
