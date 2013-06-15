package swp_compiler_ss13.javabite.compiler.jb.types;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

public class DoubleAssignment extends AbstractCompilerTest {

	@Override
	public String getProgFile() {
		return "jb" + File.separator + "types" + File.separator + "double_assignment.prog";
	}

	@Override
	public String getOutput() {
		return "-2.323e-992.00.5680.01";
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
