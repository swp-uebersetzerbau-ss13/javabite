package swp_compiler_ss13.javabite.compiler.jb.while_;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

//TODO: activate test for m3
@Ignore
public class MultipleAssignmentInWhileHeadTest extends AbstractCompilerTest{
	
	@Override
	public String getProgFile() {
		return "jb" + File.separator + "while_" + File.separator + "multiple_assignment_in_while.prog";
	}

	@Override
	public String getOutput() {
		return "";
	}

	@Override
	public Integer getResultValue() {
		return 1;
	}

	@Override
	public List<ReportType> getErrorList() {
		List<ReportType> errors = new ArrayList<>();
		return errors;
	}
}
