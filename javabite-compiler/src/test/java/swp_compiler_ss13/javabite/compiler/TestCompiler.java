package swp_compiler_ss13.javabite.compiler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.report.ReportLog;
import swp_compiler_ss13.common.report.ReportType;

public class TestCompiler extends AbstractJavabiteCompiler implements ReportLog {
	List<ReportType> errorList = new ArrayList<>();
	List<ReportType> warningList = new ArrayList<>();
	
	public TestCompiler() {
		super();
		wire();
	}
	
	@Override
	public void reportWarning(ReportType type, List<Token> tokens,
			String message) {
		warningList.add(type);
	}

	@Override
	public void reportError(ReportType type, List<Token> tokens, String message) {
		errorList.add(type);
	}

	@Override
	protected ReportLog getReportLog() {
		return this;
	}

	@Override
	protected boolean afterPreprocessing(String targetClassName) {
		return errorList.isEmpty();
	}

	@Override
	protected boolean afterAstGeneration(AST ast) {
		return errorList.isEmpty();
	}

	@Override
	protected boolean afterSemanticalAnalysis(AST ast) {
		return errorList.isEmpty();
	}

	@Override
	protected boolean afterTacGeneration(List<Quadruple> quadruples) {
		return errorList.isEmpty();
	}

	@Override
	protected boolean afterTargetCodeGeneration(File mainClassFile) {
		return errorList.isEmpty();
	}
}
