package swp_compiler_ss13.javabite.compiler.jb.misc;

import java.io.File;
import java.util.List;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

//TODO: SEMANTICAL
public class MultiSemicolonTest extends AbstractCompilerTest {

	@Override
	public String getProgFile() {
		return "jb" + File.separator + "misc" + File.separator + "multi_semicolon.prog";
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
		return null;
	}

}
