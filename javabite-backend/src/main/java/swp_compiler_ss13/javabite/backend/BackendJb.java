package swp_compiler_ss13.javabite.backend;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import swp_compiler_ss13.common.backend.Backend;
import swp_compiler_ss13.common.backend.BackendException;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.classfile.IClassfile;
import swp_compiler_ss13.javabite.backend.translation.TACOptimizer;
import swp_compiler_ss13.javabite.backend.translation.TargetCodeOptimizer;
import swp_compiler_ss13.javabite.backend.translation.Translator;

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
	 * TODO javadoc
	 * 
	 * @param s
	 * @return
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
	 * TODO javadoc
	 */
	@Override
	public Map<String, InputStream> generateTargetCode(String baseFileName,
			final List<Quadruple> tac) throws BackendException {

		baseFileName = rectifyJavaIdentifier(baseFileName);

		// TAC Optimizer
		// ### currently empty ###
		// Translator
		final Collection<IClassfile> classfiles = translator.translate(
				baseFileName, tac);
		// Target Code Optimizer
		// ### currently empty ###

		final Map<String, InputStream> targetCodeS = createTargetCodeStreams(classfiles);

		// simple visualization
		// visualizeTargetCode(targetCodeS);

		return targetCodeS;
	}

	/**
	 * TODO javadoc
	 * 
	 * @param classfiles
	 * @return
	 */
	private Map<String, InputStream> createTargetCodeStreams(
			final Collection<IClassfile> classfiles) {
		final Map<String, InputStream> targetCodeIS = new HashMap<>();

		for (final IClassfile classfile : classfiles) {
			targetCodeIS.put(classfile.getName(),
					classfile.generateInputstream());
		}

		return targetCodeIS;
	}
}
