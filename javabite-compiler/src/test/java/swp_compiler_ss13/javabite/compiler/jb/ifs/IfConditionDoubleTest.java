package swp_compiler_ss13.javabite.compiler.jb.ifs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

public class IfConditionDoubleTest extends AbstractCompilerTest {

	@Override
	public String getProgFile() {
		return "jb" + File.separator + "ifs" + File.separator + "error_if_condition_double.prog";
	}

	@Override
	public String getOutput() {
		return "hello";
	}

	@Override
	public Integer getResultValue() {
		return 0;
	}

	@Override
	public List<ReportType> getErrorList() {
		final List<ReportType> errors = new ArrayList<>();
		errors.add(ReportType.TYPE_MISMATCH);
		return errors;
	}

}
