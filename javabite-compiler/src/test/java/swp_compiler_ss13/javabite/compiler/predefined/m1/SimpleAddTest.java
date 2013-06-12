package swp_compiler_ss13.javabite.compiler.predefined.m1;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

public class SimpleAddTest extends AbstractCompilerTest {

	@Override
	public String getProgFile() {
		return "predefined" + File.separator + "m1" + File.separator + "simple_add.prog";
	}

	@Override
	public String getOutput() {
		return "";
	}

	@Override
	public Integer getResultValue() {
		return 6;
	}

	@Override
	public List<ReportType> getErrorList() {
		return new ArrayList<>();
	}

}
