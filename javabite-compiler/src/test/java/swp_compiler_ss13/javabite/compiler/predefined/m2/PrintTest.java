package swp_compiler_ss13.javabite.compiler.predefined.m2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

public class PrintTest extends AbstractCompilerTest {

	@Override
	public String getProgFile() {
		return "predefined" + File.separator + "m2" + File.separator + "print.prog";
	}

	@Override
	public String getOutput() {
		return "true\n18121313223\n-2.323E-99\njagÄrEttString\"\n";
	}

	@Override
	public Integer getResultValue() {
		return 0;
	}

	@Override
	public List<ReportType> getErrorList() {
		List<ReportType> errors = new ArrayList<>();
		return errors;
	}

}
