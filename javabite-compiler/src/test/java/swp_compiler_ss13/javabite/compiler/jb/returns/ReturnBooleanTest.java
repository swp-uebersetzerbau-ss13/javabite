package swp_compiler_ss13.javabite.compiler.jb.returns;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

public class ReturnBooleanTest extends AbstractCompilerTest {

	@Override
	public String getProgFile() {
		return "jb" + File.separator + "returns" + File.separator + "error_return_boolean.prog";
	}

	@Override
	public String getOutput() {
		return null;
	}

	@Override
	public Integer getResultValue() {
		return null;
	}

	@Override
	public List<ReportType> getErrorList() {
		final ArrayList<ReportType> errors = new ArrayList<>();
		errors.add(ReportType.TYPE_MISMATCH);
		return errors;
	}

}
