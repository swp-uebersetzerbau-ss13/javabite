package swp_compiler_ss13.javabite.compiler.additional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

public class DDinRecordTest extends AbstractCompilerTest {

	@Override
	public String getProgFile() {
		return "additional" + File.separator + "dd_in_record.prog";
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
		final List<ReportType> errors = new ArrayList<>();
		errors.add(ReportType.DOUBLE_DECLARATION);
		return errors;
	}

}
