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
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.ir.IntermediateCodeGenerator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.lexer.Lexer;
import swp_compiler_ss13.common.parser.Parser;
import swp_compiler_ss13.common.parser.ReportLog;
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
	
	public void compile(File file) throws IntermediateCodeGeneratorException, IOException {
		lexer.setSourceStream(new FileInputStream(file));
		
		AST ast = parser.getParsedAST();
		
		List<Quadruple> quadruples = codegen.generateIntermediateCode(ast);
		for (Quadruple q : quadruples) {
			System.out.println(String.format("(%s|%s|%s|%s)", q.getOperator(),
					q.getArgument1(), q.getArgument2(), q.getResult()));
		}
		Map<String, InputStream> results = backend.generateTargetCode(quadruples);
		
		for(Entry<String,InputStream> e:results.entrySet()) {
			File outFile = new File(e.getKey());
			if (outFile.exists()) {
				throw new RuntimeException("This would override a file names " + e.getKey());
			}
			
			FileOutputStream fos = new FileOutputStream(outFile);
			
			IOUtils.copy(e.getValue(), fos);
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
		} catch (IntermediateCodeGeneratorException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reportError(String text, Integer line, Integer column,
			String message) {
		System.out.println("Error at (" + line + "," + column + ") around " + text + " : " + message);
	}
}
