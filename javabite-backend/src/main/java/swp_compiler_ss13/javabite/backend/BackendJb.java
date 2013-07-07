package swp_compiler_ss13.javabite.backend;

import swp_compiler_ss13.common.backend.Backend;
import swp_compiler_ss13.common.backend.BackendException;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.translation.Translator;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * BackendImpl class. Implementation of the interface "Backend".
 * 
 * @author Marco
 * @since 27.04.2013
 * 
 */
public class BackendJb implements Backend {

	public BackendJb() {
	}

	/**
	 * Filters out every non legal character for java identifiers.
	 * 
	 * @param s
	 *            string to check for invalid characters
	 * @return valid java identifier
	 */
	public static String rectifyJavaIdentifier(final String s) {
		final StringBuilder sb = new StringBuilder();

		final char[] c = s.toCharArray();
		if (Character.isJavaIdentifierStart(c[0])) {
			sb.append(Character.toUpperCase(c[0]));
		}

		for (int i = 1; i < c.length; i++) {
			if (Character.isJavaIdentifierPart(c[i])) {
				sb.append(c[i]);
			}
		}

		return sb.toString();
	}

	/**
	 * generates the target code for the input list of three address code
	 * quadruples.
	 */
	@Override
	public Map<String, InputStream> generateTargetCode(String baseFileName,
			final List<Quadruple> tac) throws BackendException {

        //final TACOptimizer tacOptimizer = new TACOptimizer();
        //final TargetCodeOptimizer targetCodeOptimizer = new TargetCodeOptimizer();
        final Translator translator = new Translator();

		baseFileName = rectifyJavaIdentifier(baseFileName);

		// TAC Optimizer
		// tacOptimizer.optimize(tac);

		// Translator
		final Collection<Classfile> classfiles = translator.translate(
				baseFileName, tac);

		// Target Code Optimizer
		// targetCodeOptimizer.optimize(classfiles);

		return createTargetCodeStreams(classfiles);
	}

	/**
	 * TODO javadoc
	 * 
	 * @param classfiles
	 *            list of classfiles to create target code streams from
	 * @return map of classfile names as keys, with their target code streams as
	 *         values
	 */
	private static Map<String, InputStream> createTargetCodeStreams(
			final Collection<Classfile> classfiles) {
		final Map<String, InputStream> targetCodeIS = new TreeMap<>();

		for (final Classfile classfile : classfiles) {
			targetCodeIS.put(classfile.getFilename(),
					classfile.generateInputstream());
		}

		return targetCodeIS;
	}
}
