package swp_compiler_ss13.javabite.compiler.jb.misc;

import java.io.File;
import java.util.List;

import org.junit.Ignore;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

//TODO: activate test for m3
@Ignore
public class FactorialTest extends AbstractCompilerTest {

	@Override
	public String getProgFile() {
		return "jb" + File.separator + "misc" + File.separator + "factorial.prog";
	}

	@Override
	public String getOutput() {
		return "6";
	}

	@Override
	public Integer getResultValue() {
		return 6;
	}

	@Override
	public List<ReportType> getErrorList() {
		return null;
	}

}
