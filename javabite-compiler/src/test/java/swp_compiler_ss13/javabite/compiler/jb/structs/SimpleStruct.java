package swp_compiler_ss13.javabite.compiler.jb.structs;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SimpleStruct extends AbstractCompilerTest {

    @Override
    public String getProgFile() {
        return "jb" + File.separator + "structs" + File.separator + "simple_struct.prog";
    }

    @Override
    public String getOutput() {
        return "5";
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
