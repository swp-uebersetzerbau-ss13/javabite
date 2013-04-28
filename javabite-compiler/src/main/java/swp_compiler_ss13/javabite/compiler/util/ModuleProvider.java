package swp_compiler_ss13.javabite.compiler.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import swp_compiler_ss13.common.backend.Backend;
import swp_compiler_ss13.common.ir.IntermediateCodeGenerator;
import swp_compiler_ss13.common.lexer.Lexer;
import swp_compiler_ss13.common.parser.Parser;

/**
 * The module provider gives access to the module implementation which is
 * configured by the ServiceLoader-API.
 * 
 * If the configuration of the ServiceLoader-API is not correct, a 
 * {@link ServiceConfigurationError} may be thrown.
 * 
 * @author flofreud
 * 
 */
public class ModuleProvider {

	/**
	 * @return a available Lexer implementation instance
	 * @throws ServiceConfigurationError
	 */
	public static Lexer getLexerInstance() {
		return getModuleInstance(Lexer.class);
	}

	/**
	 * @return a collection of all available Lexer implementation instance
	 * @throws ServiceConfigurationError
	 */
	public static Collection<? extends Lexer> getAllLexerInstance() {
		return getAllModuleInstances(Lexer.class);
	}

	/**
	 * @return a available Parser implementation instance
	 * @throws ServiceConfigurationError
	 */
	public static Parser getParserInstance() {
		return getModuleInstance(Parser.class);
	}

	/**
	 * @return a collection of all available Parser implementation instance
	 * @throws ServiceConfigurationError
	 */
	public static Collection<? extends Parser> getAllParserInstance() {
		return getAllModuleInstances(Parser.class);
	}

	/**
	 * @return a available IntermediateCodeGenerator implementation instance
	 * @throws ServiceConfigurationError
	 */
	public static IntermediateCodeGenerator getCodeGeneratorInstance() {
		return getModuleInstance(IntermediateCodeGenerator.class);
	}

	/**
	 * @return a collection of all available IntermediateCodeGenerator implementation instance
	 * @throws ServiceConfigurationError
	 */
	public static Collection<? extends IntermediateCodeGenerator> getCodeAllGeneratorInstance() {
		return getAllModuleInstances(IntermediateCodeGenerator.class);
	}

	/**
	 * @return a available Backend implementation instance
	 * @throws ServiceConfigurationError
	 */
	public static Backend getBackendInstance() {
		return getModuleInstance(Backend.class);
	}

	/**
	 * @return a collection of all available Backend implementation instance
	 * @throws ServiceConfigurationError
	 */
	public static Collection<? extends Backend> getAllBackendInstance() {
		return getAllModuleInstances(Backend.class);
	}

	private static <T> T getModuleInstance(Class<T> clazz) {
		Iterator<T> iterator = ServiceLoader.load(clazz).iterator();
		return iterator.hasNext() ? iterator.next() : null;
	}

	private static <T> Collection<? extends T> getAllModuleInstances(
			Class<T> clazz) {
		Collection<T> result = new ArrayList<T>();
		for (T e : ServiceLoader.load(clazz))
			result.add(e);
		return result;
	}
}
