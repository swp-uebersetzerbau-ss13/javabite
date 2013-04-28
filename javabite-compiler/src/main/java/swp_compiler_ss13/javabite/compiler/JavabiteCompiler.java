package swp_compiler_ss13.javabite.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.backend.Backend;
import swp_compiler_ss13.common.ir.IntermediateCodeGenerator;
import swp_compiler_ss13.common.lexer.Lexer;
import swp_compiler_ss13.common.parser.Parser;
import swp_compiler_ss13.javabite.compiler.util.ModuleProvider;

/**
 * main class for the JavaBite-compiler
 * 
 * @author flofreud
 *
 */
public class JavabiteCompiler {
	final static Logger log = LoggerFactory.getLogger(JavabiteCompiler.class);
	
	Lexer lexer = null;
	Parser parser = null;
	IntermediateCodeGenerator codegen = null;
	Backend backend = null;
	
	public JavabiteCompiler() {
		lexer = ModuleProvider.getLexerInstance();
		parser = ModuleProvider.getParserInstance();
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
	
	
	public static void main(String[] args) {
		System.out.println("Javabite-Compiler Basic Console");
		JavabiteCompiler compiler = new JavabiteCompiler();
		if (compiler.checkSetup()) {
			System.out.println("Compiler is ready to start");
		} else {
			System.out.println("Compiler could not load all need modules");
		}
		
	}
}
