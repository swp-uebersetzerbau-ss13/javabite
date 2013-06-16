package swp_compiler_ss13.javabite.compiler.jb.types;

import java.io.File;
import java.util.List;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

public class StringTest extends AbstractCompilerTest {

	@Override
	public String getProgFile() {
		return "jb" + File.separator + "types" + File.separator + "string.prog";
	}

	@Override
	public String getOutput() {
		return "abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVXYZ1234567890!";
	}

	@Override
	public Integer getResultValue() {
		return 0;
	}

	@Override
	public List<ReportType> getErrorList() {
		// TODO Auto-generated method stub
		return null;
	}

}
