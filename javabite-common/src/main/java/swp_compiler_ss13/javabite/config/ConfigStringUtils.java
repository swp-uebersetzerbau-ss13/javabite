package swp_compiler_ss13.javabite.config;

public abstract class ConfigStringUtils {
	/**
	 * http://stackoverflow.com/questions/2559759/how-do-i-convert-camelcase-
	 * into-human-readable-names-in-java
	 * 
	 * @param input
	 * @return
	 */
	public static String camelCaseToReadable(String input) {
		String result =  input.replaceAll(String.format("%s|%s|%s",
				"(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
				"(?<=[A-Za-z])(?=[^A-Za-z])"), " ");
		return Character.toUpperCase(result.charAt(0)) + result.substring(1);
	}
}
