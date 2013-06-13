package swp_compiler_ss13.javabite.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
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
import swp_compiler_ss13.common.parser.Parser;
import swp_compiler_ss13.common.report.ReportLog;
import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.common.semanticAnalysis.SemanticAnalyser;
import swp_compiler_ss13.common.util.ModuleProvider;
import swp_compiler_ss13.javabite.config.Configurable;
import swp_compiler_ss13.javabite.config.JavabiteConfig;
import swp_compiler_ss13.javabite.runtime.JavaClassProcess;

public abstract class AbstractJavabiteCompiler implements Configurable {
	protected final static Logger log = LoggerFactory
			.getLogger(AbstractJavabiteCompiler.class);

	private final static String LEXER_KEY = "compiler.lexer";
	private final static String PARSER_KEY = "compiler.parser";
	private final static String SEMANTICAL_ANALYSER_KEY = "compiler.semantical";
	private final static String ICG_KEY = "compiler.icg";
	private final static String BACKEND_KEY = "compiler.backend";

	private final static String BUILD_DIR = "build";

	protected Lexer lexer;
	protected Parser parser;
	protected SemanticAnalyser semanticAnalyser;
	protected IntermediateCodeGenerator intermediateCodeGenerator;
	protected Backend backend;

	protected AbstractJavabiteCompiler() {
		JavabiteConfig config = JavabiteConfig.getDefaultConfig();
		Collection<? extends Lexer> lexerClasses = ModuleProvider
				.getAllLexerInstance();
		String lexerClassName = config.getProperty(LEXER_KEY,
				getClassNames(lexerClasses));
		lexer = getClassWithName(lexerClassName, lexerClasses);

		Collection<? extends Parser> parserClasses = ModuleProvider
				.getAllParserInstance();
		String parserClassName = config.getProperty(PARSER_KEY,
				getClassNames(parserClasses));
		parser = getClassWithName(parserClassName, parserClasses);

		Collection<? extends SemanticAnalyser> semanticClasses = ModuleProvider
				.getAllSemanticAnalyserInstance();
		String semanticClassName = config.getProperty(SEMANTICAL_ANALYSER_KEY,
				getClassNames(semanticClasses));
		semanticAnalyser = getClassWithName(semanticClassName, semanticClasses);

		Collection<? extends IntermediateCodeGenerator> icgClasses = ModuleProvider
				.getCodeAllGeneratorInstance();
		String icgClassName = config.getProperty(ICG_KEY,
				getClassNames(icgClasses));
		intermediateCodeGenerator = getClassWithName(icgClassName, icgClasses);

		Collection<? extends Backend> backendClasses = ModuleProvider
				.getAllBackendInstance();
		String backendClassName = config.getProperty(BACKEND_KEY,
				getClassNames(backendClasses));
		backend = getClassWithName(backendClassName, backendClasses);
		JavabiteConfig.registerConfigurable(this);
	}

	@Override
	public void onConfigChanges(JavabiteConfig config) {
		String lexerClassName = config.getProperty(LEXER_KEY);
		if (!lexer.getClass().getName().equals(lexerClassName))
			lexer = getClassWithName(lexerClassName,
					ModuleProvider.getAllLexerInstance());

		String parserClassName = config.getProperty(PARSER_KEY);
		if (!parser.getClass().getName().equals(parserClassName))
			parser = getClassWithName(parserClassName,
					ModuleProvider.getAllParserInstance());

		String semanticClassName = config.getProperty(SEMANTICAL_ANALYSER_KEY);
		if (!semanticAnalyser.getClass().getName().equals(semanticClassName))
			semanticAnalyser = getClassWithName(semanticClassName,
					ModuleProvider.getAllSemanticAnalyserInstance());

		String icgClassName = config.getProperty(ICG_KEY);
		if (!intermediateCodeGenerator.getClass().getName()
				.equals(icgClassName))
			intermediateCodeGenerator = getClassWithName(icgClassName,
					ModuleProvider.getCodeAllGeneratorInstance());

		String backendClassName = config.getProperty(BACKEND_KEY);
		if (!backend.getClass().getName().equals(backendClassName))
			backend = getClassWithName(backendClassName,
					ModuleProvider.getAllBackendInstance());
		
		wire();
	}

	protected abstract ReportLog getReportLog();

	public File compile(File input) {
		try {
			// get the name of file without extension
			String sourceBaseName = input.getName();
			int lastDot = sourceBaseName.lastIndexOf(".");
			lastDot = lastDot > -1 ? lastDot : sourceBaseName.length();
			sourceBaseName = sourceBaseName.substring(0, lastDot);

			assert lexer != null && parser != null && semanticAnalyser != null
					&& intermediateCodeGenerator != null && backend != null;

			if (!afterPreprocessing(sourceBaseName))
				return null;

			lexer.setSourceStream(new FileInputStream(input));
			AST ast = parser.getParsedAST();

			if (!afterAstGeneration(ast))
				return null;

			ast = semanticAnalyser.analyse(ast);

			if (!afterSemanticalAnalysis(ast))
				return null;

			List<Quadruple> quadruples = intermediateCodeGenerator
					.generateIntermediateCode(ast);

			if (!afterTacGeneration(quadruples))
				return null;

			File mainClassFile = null;
			Map<String, InputStream> results = backend.generateTargetCode(
					sourceBaseName, quadruples);
			for (Entry<String, InputStream> e : results.entrySet()) {
				File outFile = new File(BUILD_DIR, e.getKey());
				if (!outFile.getParentFile().exists() && !outFile.getParentFile().mkdirs()) {
					getReportLog().reportError(ReportType.UNDEFINED, null, "Could not generate output directories: " + outFile.getAbsolutePath());
					return null;
				}
					
				if (mainClassFile == null)
					mainClassFile = outFile;

				FileOutputStream fos = new FileOutputStream(outFile);
				IOUtils.copy(e.getValue(), fos);
				fos.close();
			}

			afterTargetCodeGeneration(mainClassFile);
			return mainClassFile;
		} catch (IOException | IntermediateCodeGeneratorException
				| BackendException e) {
			getReportLog().reportError(ReportType.UNDEFINED, null,
					e.getMessage());
		}
		return null;
	}
	

	public boolean isJavaBackend() {
		// flofreud: this is quite hacky but...
		return backend.getClass().getName().contains("java");
	}

	public JavaClassProcess execute(File mainClassFile) {
		return new JavaClassProcess(mainClassFile);
	}

	/**
	 * if it returns true, the compilation process will be continued, else
	 * aborted
	 */
	protected boolean afterPreprocessing(String targetClassName) {
		return true;
	}

	/**
	 * if it returns true, the compilation process will be continued, else
	 * aborted
	 */
	protected boolean afterAstGeneration(AST ast) {
		return true;
	}

	/**
	 * if it returns true, the compilation process will be continued, else
	 * aborted
	 */
	protected boolean afterSemanticalAnalysis(AST ast) {
		return true;
	}

	/**
	 * if it returns true, the compilation process will be continued, else
	 * aborted
	 */
	protected boolean afterTacGeneration(List<Quadruple> quadruples) {
		return true;
	}

	/**
	 * if it returns true, the compilation process will be continued, else
	 * aborted
	 */
	protected boolean afterTargetCodeGeneration(File mainClassFile) {
		return true;
	}

	private List<String> getClassNames(Collection<?> classInstances) {
		List<String> names = new ArrayList<>();
		for (Object c : classInstances)
			names.add(c.getClass().getName());
		return names;
	}

	private <T> T getClassWithName(String name, Collection<T> classInstances) {
		for (T t : classInstances) {
			if (t.getClass().getName().equals(name))
				return t;
		}

		log.error("Could not found class " + name + " in Serviceloader");

		return classInstances.iterator().next();
	}
	
	protected void wire() {
		parser.setLexer(lexer);
		parser.setReportLog(getReportLog());
		semanticAnalyser.setReportLog(getReportLog());
	}
}
