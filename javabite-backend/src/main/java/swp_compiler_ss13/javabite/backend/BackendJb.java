package swp_compiler_ss13.javabite.backend;

import swp_compiler_ss13.common.backend.Backend;
import swp_compiler_ss13.common.backend.BackendException;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.translation.TACOptimizer;
import swp_compiler_ss13.javabite.backend.translation.TargetCodeOptimizer;
import swp_compiler_ss13.javabite.backend.translation.Translator;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BackendImpl class. Implementation of the interface "Backend".
 * 
 * @author Marco
 * @since 27.04.2013
 * 
 */
public class BackendJb implements Backend {

	@SuppressWarnings("unused")
	private final TACOptimizer tacOptimizer;
	private final Translator translator;
	@SuppressWarnings("unused")
	private final TargetCodeOptimizer targetCodeOptimizer;

	public BackendJb() {
		tacOptimizer = new TACOptimizer();
		translator = new Translator();
		targetCodeOptimizer = new TargetCodeOptimizer();
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

		baseFileName = rectifyJavaIdentifier(baseFileName);

		// TAC Optimizer
		tacOptimizer.optimize(tac);

		// Translator
		final Collection<Classfile> classfiles = translator.translate(
				baseFileName, tac);

		// Target Code Optimizer
		targetCodeOptimizer.optimize(classfiles);

		final Map<String, InputStream> targetCodeS = createTargetCodeStreams(classfiles);

		// simple visualization
		// visualizeTargetCode(targetCodeS);

		return targetCodeS;
	}

	/**
	 * TODO javadoc
	 * 
	 * @param classfiles
	 *            list of classfiles to create target code streams from
	 * @return map of classfile names as keys, with their target code streams as
	 *         values
	 */
	private Map<String, InputStream> createTargetCodeStreams(
			final Collection<Classfile> classfiles) {
		final Map<String, InputStream> targetCodeIS = new HashMap<>();

		for (final Classfile classfile : classfiles) {
			targetCodeIS.put(classfile.getName(),
					classfile.generateInputstream());
		}

		return targetCodeIS;
	}
}
