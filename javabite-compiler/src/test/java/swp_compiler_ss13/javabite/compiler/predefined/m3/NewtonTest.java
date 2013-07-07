package swp_compiler_ss13.javabite.compiler.predefined.m3;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class NewtonTest extends AbstractCompilerTest {

    @Override
    public String getProgFile() {
        return "predefined" + File.separator + "m3" + File.separator
                + "newton.prog";
    }

    @Override
    public String getOutput() {
        return "i hate floating point numbers1.4142156862745097";
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
