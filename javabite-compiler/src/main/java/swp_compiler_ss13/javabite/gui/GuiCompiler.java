package swp_compiler_ss13.javabite.gui;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.common.report.ReportLog;
import swp_compiler_ss13.javabite.compiler.AbstractJavabiteCompiler;

class GuiCompiler extends AbstractJavabiteCompiler {
	MainFrame mf;
	
	GuiCompiler(MainFrame mainFrame) {
		super();
		this.mf = mainFrame;
		wire();
	}
	
	@Override
	public ReportLog getReportLog() {
		return mf;
	}

	@Override
	protected boolean afterPreprocessing(String targetClassName) {
		mf.setProgress(20);
		mf.appendToLogs("\nCompile File: " + targetClassName + ".prog");
		mf.appendToConsole("\n[Compiler] compile file " + targetClassName + ".prog");
		mf.appendToLogs("\nGenerating AST for source file...");
		mf.appendToConsole("\n[Compiler] generate AST for source file");
		mf.setToolBar("Compiling...");
		return true;
	}

	@Override
	protected boolean afterAstGeneration(AST ast) {
		if (mf.errorReported) {
			if (mf.astVisualizationRequested)
				mf.showAstVisualization(ast);
			return reportFailure();
		}

		mf.setProgress(40);
		mf.appendToLogs("\nExecute semantical checks on AST...");
		return true;
	}

	@Override
	protected boolean afterSemanticalAnalysis(AST ast) {
		if (mf.errorReported) {
			if (mf.astVisualizationRequested)
				mf.showAstVisualization(ast);
			return reportFailure();
		}
		
		if (mf.astVisualizationRequested) {
			mf.showAstVisualization(ast);
			return false;
		}
		
		mf.setProgress(60);
		mf.appendToLogs("\nGenerate TAC...");
		mf.appendToConsole("\n[Compiler] generate TAC");
		return true;
	}

	@Override
	protected boolean afterTacGeneration(List<Quadruple> tac) {
		if (mf.errorReported) {
			if (mf.tacVisualizationRequested)
				mf.showTacVisualization(tac);
			return reportFailure();
		}

		if (mf.tacVisualizationRequested) {
			mf.showTacVisualization(tac);
			return false;
		}
		
		mf.setProgress(80);
		mf.appendToLogs("\nGenerate target code...");
		mf.appendToConsole("\n[Compiler] generate target code");
		return true;
	}

	@Override
	protected boolean afterTargetCodeGeneration(File mainClassFile) {
		if (mf.errorReported) {
			return reportFailure();
		}
		
		if (mf.byteCodeVisualizationRequested) {
			mf.showByteCodeVisualization(mainClassFile);
			return false;
		}

		mf.setProgress(90);
		mf.appendToLogs("\nCompilation successful. Main-file written to: " + mainClassFile.getAbsolutePath());
		mf.setToolBar("Compilation successful.");
		mf.setSuccessIcon();
		return true;
	}
	
	private boolean reportFailure() {
		mf.appendToLogs("\nCompilation failed. See error log!");
		mf.setToolBar("Compilation failed.");
		mf.setFailureIcon();
		return false;
	}
	
	/**
	 * Returns a list of tokens for a given string
	 * */
	List<Token> getTokenListFor(String text) {
		Token t;
		List<Token> tokens = new ArrayList<Token>();
		
		try {
			lexer.setSourceStream(new ByteArrayInputStream(text.getBytes("UTF-8")));
			while ((t = lexer.getNextToken()).getTokenType() != TokenType.EOF) {
				tokens.add(t);
			}
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		
		return tokens;
	}
}
