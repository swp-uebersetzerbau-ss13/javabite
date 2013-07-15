package swp_compiler_ss13.javabite.backend;

import swp_compiler_ss13.common.backend.Backend;
import swp_compiler_ss13.common.backend.BackendException;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.translation.Translator;
import swp_compiler_ss13.javabite.backend.utils.ClassSignature;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * BackendJb class. Implementation of the interface "Backend".
 * 
 * @since 27.04.2013
 */
public class BackendJb implements Backend {

	/**
	 * <h1>generateTargetCode</h1>
	 * 
	 * <p>
	 * This method generates the target code for the input list of three address
	 * code quadruples using the main components - a three-address-code
	 * optimizer, an actual translater and a target code optimizer.
	 * </p>
	 * 
	 * @since 27.04.2013
	 * 
	 * @param baseFileName
	 *            String filename, which is to be used for the main classfile
	 *            generation and as prefix for all struct classfile names.
	 * @param tac
	 *            list of quadruples, which represents the program, that is
	 *            supposed to be translated into java bytecode.
	 * @See {@link Translator#translate(String, List)}
	 */
	@Override
	public Map<String, InputStream> generateTargetCode(String baseFileName,
			final List<Quadruple> tac) throws BackendException {

		// final TACOptimizer tacOptimizer = new TACOptimizer();
		// final TargetCodeOptimizer targetCodeOptimizer = new
		// TargetCodeOptimizer();
		final Translator translator = new Translator();

		baseFileName = ClassSignature.rectifyJavaIdentifier(baseFileName);

		// TAC Optimizer
		// tacOptimizer.optimize(tac);

		// Translator
		final Collection<Classfile> classfiles = translator.translate(
				baseFileName, tac);

		// Target Code Optimizer
		// targetCodeOptimizer.optimize(classfiles);

		final Map<String, InputStream> targetCodeIS = new TreeMap<>();

		for (final Classfile classfile : classfiles) {
			targetCodeIS.put(classfile.getFilename(),
					classfile.generateInputstream());
		}

		return targetCodeIS;
	}

}
