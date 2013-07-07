package swp_compiler_ss13.javabite.compiler.predefined.m3;

import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractCompilerTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MatrixMultiplicationTest extends AbstractCompilerTest {

    @Override
    public String getProgFile() {
        return "predefined" + File.separator + "m3" + File.separator
                + "matrixMultiplication.prog";
    }

    @Override
    public String getOutput() {
        return "14|46\n28|92\n42|138\n56|184\n";
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
