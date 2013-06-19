package swp_compiler_ss13.javabite.compiler.jb.types;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

//TODO: activate test for m3
@Ignore
public class ErrorDoubleAssignedToBooleanArrayTest extends AbstractCompilerTest {

	@Override
	public String getProgFile() {
		return "jb" + File.separator + "types" + File.separator + "error_type_mismatch_array_decl_assign_boolean_double.prog";
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
		errors.add(ReportType.TYPE_MISMATCH);
		return errors;
	}

}
