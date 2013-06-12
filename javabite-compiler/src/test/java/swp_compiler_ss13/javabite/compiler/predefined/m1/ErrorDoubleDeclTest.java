package swp_compiler_ss13.javabite.compiler.predefined.m1;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

public class ErrorDoubleDeclTest extends AbstractCompilerTest {

	@Override
	public String getProgFile() {
		return "predefined" + File.separator + "m1" + File.separator + "error_double_decl.prog";
	}

	@Override
	public String getOutput() {
		return "";
	}

	@Override
	public Integer getResultValue() {
		return null;
	}

	@Override
	public List<ReportType> getErrorList() {
		List<ReportType> errors = new ArrayList<>();
		errors.add(ReportType.DOUBLE_DECLARATION);
		return errors;
	}

}
