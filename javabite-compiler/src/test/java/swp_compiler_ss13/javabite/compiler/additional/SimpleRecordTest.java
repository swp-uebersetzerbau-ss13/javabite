package swp_compiler_ss13.javabite.compiler.additional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

public class SimpleRecordTest extends AbstractCompilerTest {

	@Override
	public String getProgFile() {
		return "additional" + File.separator + "simple_record.prog";
	}

	@Override
	public String getOutput() {
		return "2";
	}

	@Override
	public Integer getResultValue() {
		return 0;
	}

	@Override
	public List<ReportType> getErrorList() {
		final List<ReportType> errors = new ArrayList<>();
		return errors;
	}

}
