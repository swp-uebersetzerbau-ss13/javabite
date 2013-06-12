package swp_compiler_ss13.javabite.compiler;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.report.ReportLog;
import swp_compiler_ss13.common.report.ReportType;

/**
 * main class for the JavaBite-compiler
 */
public class JavabiteCliCompiler extends AbstractJavabiteCompiler implements
		ReportLog {
	final static Logger log = LoggerFactory.getLogger(JavabiteCliCompiler.class);

	private Boolean errorReported = false;
	
	public JavabiteCliCompiler() {
		super();
		wire();
	}

	@Override
	protected boolean afterPreprocessing(String targetClassName) {
		System.out.println("Compile file: " + targetClassName + ".prog");
		System.out.println("Build ast...");
		return true;
	}

	@Override
	protected boolean afterAstGeneration(AST ast) {
		if (errorReported)
			System.out.println("Compilation failed.");
		else {
			System.out.println("AST build.");
			System.out.println("Check AST for semantical correctness...");
		}

		return !errorReported;
	}

	@Override
	protected boolean afterSemanticalAnalysis(AST ast) {
		if (errorReported)
			System.out.println("Compilation failed.");
		else {
			System.out.println("Semantical analysis of AST finished.");
			System.out.println("Generate three address code... ");
		}

		return !errorReported;
	}

	@Override
	protected boolean afterTacGeneration(List<Quadruple> quadruples) {
		if (errorReported)
			System.out.println("Compilation failed.");
		else {
			System.out.println("TAC generated.");
			System.out.println("Generate target code... ");
		}

		return !errorReported;
	}

	@Override
	protected boolean afterTargetCodeGeneration(File mainClassFile) {
		if (errorReported)
			System.out.println("Compilation failed.");
		else {
			System.out.println("Target code generated.");
			System.out.println("Main-class written to: " + mainClassFile.getAbsolutePath());
		}

		return !errorReported;
	}

	@Override
	public void reportWarning(ReportType type, List<Token> tokens,
			String message) {

		System.out.println("Warning at (" + getLine(tokens) + ","
				+ getColumn(tokens) + ") around '" + getTokenAsString(tokens)
				+ "' : " + message);
	}

	@Override
	public void reportError(ReportType type, List<Token> tokens, String message) {
		errorReported = true;
		System.out.println("Error at (" + getLine(tokens) + ","
				+ getColumn(tokens) + ") around '" + getTokenAsString(tokens)
				+ "' : " + message);
	}

	private int getLine(List<Token> tokens) {
		if (tokens == null || tokens.isEmpty())
			return 0;

		return tokens.get(0).getLine();
	}

	private int getColumn(List<Token> tokens) {
		if (tokens == null || tokens.isEmpty())
			return 0;

		return tokens.get(0).getColumn();
	}

	private String getTokenAsString(List<Token> tokens) {
		if (tokens == null || tokens.isEmpty())
			return "";
		// TODO: this is not optimal because we don't know which tokens and
		// space-characters are missing between
		StringBuilder sb = new StringBuilder();
		for (Token t : tokens) {
			sb.append(" ");
			sb.append(t.getValue());
		}

		return sb.substring(sb.length() > 0 ? 1 : 0);
	}

	@Override
	protected ReportLog getReportLog() {
		return this;
	}
}
