package swp_compiler_ss13.javabite.compiler.cross.predefined.m2;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.cross.AbstractCompilerTest;

public class AssignmentTest extends AbstractCompilerTest {

	@Override
	public String getProgFile() {
		return "predefined" + File.separator + "m2" + File.separator + "assignment.prog";
	}

	@Override
	public String getOutput() {
		return "";
	}

	@Override
	public Integer getResultValue() {
		return 10;
	}

	@Override
	public List<ReportType> getErrorList() {
		List<ReportType> errors = new ArrayList<>();
		return errors;
	}

}
