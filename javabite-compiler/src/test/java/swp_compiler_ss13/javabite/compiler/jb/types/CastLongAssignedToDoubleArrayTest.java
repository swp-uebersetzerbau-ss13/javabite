package swp_compiler_ss13.javabite.compiler.jb.types;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

//TODO: activate test for m3
@Ignore
public class CastLongAssignedToDoubleArrayTest extends AbstractCompilerTest {

	@Override
	public String getProgFile() {
		return "jb" + File.separator + "types" + File.separator + "cast_type_mismatch_array_decl_assign_double_long.prog";
	}

	@Override
	public String getOutput() {
		return "123.0";
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
