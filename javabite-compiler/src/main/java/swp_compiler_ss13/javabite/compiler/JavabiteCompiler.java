package swp_compiler_ss13.javabite.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.backend.Backend;
import swp_compiler_ss13.common.backend.BackendException;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.ir.IntermediateCodeGenerator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.lexer.Lexer;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.parser.Parser;
import swp_compiler_ss13.common.report.ReportLog;
import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.common.util.ModuleProvider;

/**
 * main class for the JavaBite-compiler
 */
public class JavabiteCompiler implements ReportLog {
	final static Logger log = LoggerFactory.getLogger(JavabiteCompiler.class);
	
	Lexer lexer = null;
	Parser parser = null;
	IntermediateCodeGenerator codegen = null;
	Backend backend = null;
	
	Boolean errorReported = false;
	
	public JavabiteCompiler() {
		lexer = ModuleProvider.getLexerInstance();
		parser = ModuleProvider.getParserInstance();
		parser.setLexer(lexer);
		parser.setReportLog(this);
		codegen = ModuleProvider.getCodeGeneratorInstance();
		backend = ModuleProvider.getBackendInstance();
	}
	
	/**
	 * @return check if all modules could be loaded
	 */
	public boolean checkSetup() {
		boolean setupOk = true;

		if (lexer == null) {
			setupOk = false;
			log.error("No lexer module loaded...");
		}
		if (parser == null) {
			setupOk = false;
			log.error("No parser module loaded...");
		}
		if (codegen == null) {
			setupOk = false;
			log.error("No code generation module loaded...");
		}
		if (backend == null) {
			setupOk = false;
			log.error("No backend module loaded...");
		}
		
		return setupOk;
	}
	
	public void compile(File file) throws IntermediateCodeGeneratorException, IOException, BackendException {
		// get the name of file without extension
		String sourceBaseName = file.getName();
		int lastDot = sourceBaseName.lastIndexOf(".");
		lastDot = lastDot > -1 ? lastDot : sourceBaseName.length();
		sourceBaseName = sourceBaseName.substring(0,lastDot);
		
		errorReported = false;
		System.out.println("Compile file: " + file.getName());
		lexer.setSourceStream(new FileInputStream(file));
		System.out.println("Build ast: ");
		AST ast = parser.getParsedAST();
		
		if (errorReported) {
			System.out.println("Compilation failed!");
			return;
		}

		System.out.println("Build ast finished");
		System.out.println("Generate three address code: ");
		List<Quadruple> quadruples = codegen.generateIntermediateCode(ast);
//		Print of tac
//		for (Quadruple q : quadruples) {
//			System.out.println(String.format("(%s|%s|%s|%s)", q.getOperator(),
//					q.getArgument1(), q.getArgument2(), q.getResult()));
//		}
		System.out.println("Generate three address code finished");
		System.out.println("Generate target code: ");
		Map<String, InputStream> results = backend.generateTargetCode(sourceBaseName, quadruples);
		System.out.println("Generate target code finished");
		for(Entry<String,InputStream> e:results.entrySet()) {
			System.out.println("Write output file: " + e.getKey());
			File outFile = new File(e.getKey());
//			if (outFile.exists()) {
//				throw new RuntimeException("This would override a file names " + e.getKey());
//			}
			
			FileOutputStream fos = new FileOutputStream(outFile);
			
			IOUtils.copy(e.getValue(), fos);
			fos.close();
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Javabite-Compiler Basic Console");
		JavabiteCompiler compiler = new JavabiteCompiler();
		if (compiler.checkSetup()) {
			System.out.println("Compiler is ready to start");
		} else {
			System.out.println("Compiler could not load all need modules");
		}
		
		if (args.length < 1) {
			System.out.println("Compiler need a source file as input");
			return;
		}
			
		File file = new File(args[0]);
		
		if (!file.exists()) {
			System.out.println("Compiler need a source file as input");
		}
		
		try {
			compiler.compile(file);
		} catch (IntermediateCodeGeneratorException | BackendException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reportWarning(ReportType type, List<Token> tokens,
			String message) {
		
		System.out.println("Warning at (" + getLine(tokens) + "," + getColumn(tokens) + ") around '" + getTokenAsString(tokens) + "' : " + message);
	}

	@Override
	public void reportError(ReportType type, List<Token> tokens, String message) {
		errorReported = true;
		System.out.println("Error at (" + getLine(tokens) + "," + getColumn(tokens) + ") around '" + getTokenAsString(tokens) + "' : " + message);
	}
	
	private int getLine(List<Token> tokens) {
		if (tokens.isEmpty())
			return 0;
		
		return tokens.get(0).getLine();
	}
	
	private int getColumn(List<Token> tokens) {
		if (tokens.isEmpty())
			return 0;
		
		return tokens.get(0).getColumn();
	}
	
	private String getTokenAsString(List<Token> tokens) {
		if (tokens.isEmpty())
			return "";
		//TODO: this is not optimal because we don't know which tokens and
		//      space-characters are missing between
		StringBuilder sb = new StringBuilder();
		for (Token t:tokens) {
			sb.append(" ");
			sb.append(t.getValue());
		}
		
		return sb.substring(sb.length()>0?1:0);
	}
}
