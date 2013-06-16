package swp_compiler_ss13.javabite.compiler.jb.while_;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

//TODO: activate test for m3
@Ignore
public class WhileHeadWithRelationEquals2  extends AbstractCompilerTest{
	
	@Override
	public String getProgFile() {
		return "jb" + File.separator + "while_" + File.separator + "while_head_with_relation_equals_2.prog";
	}

	@Override
	public String getOutput() {
		return "";
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