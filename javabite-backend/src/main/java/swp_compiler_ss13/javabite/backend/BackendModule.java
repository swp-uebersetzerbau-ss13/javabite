package swp_compiler_ss13.javabite.backend;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.backend.Backend;
import swp_compiler_ss13.common.backend.Quadruple;
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
public class BackendModule implements Backend {

	private static final Logger logger = LoggerFactory
			.getLogger(BackendModule.class);

	@SuppressWarnings("unused")
	private final TACOptimizer tacOptimizer;
	private final Translator translator;
	@SuppressWarnings("unused")
	private final TargetCodeOptimizer targetCodeOptimizer;

	public BackendModule() {
		this.tacOptimizer = new TACOptimizer();
		this.translator = new Translator();
		this.targetCodeOptimizer = new TargetCodeOptimizer();
	}

	@Override
	public Map<String, InputStream> generateTargetCode(final List<Quadruple> tac) {

		// TAC Optimizer
		// ### currently empty ###
		// Translator
		final Collection<IClassfile> classfiles = this.translator
				.translate(tac);
		// Target Code Optimizer
		// ### currently empty ###

		final Map<String, InputStream> targetCodeS = createTargetCodeStreams(classfiles);

		// simple visualization
		// visualizeTargetCode(targetCodeS);

		return targetCodeS;
	}

	private Map<String, InputStream> createTargetCodeStreams(
			final Collection<IClassfile> classfiles) {
		final Map<String, InputStream> targetCodeIS = new HashMap<>();

		for (final IClassfile classfile : classfiles) {
			targetCodeIS.put(classfile.getName(),
					classfile.generateInputstream());
		}

		return targetCodeIS;
	}

	/*
	 * print content of inputstreams to console
	 */
	private void visualizeTargetCode(final Map<String, InputStream> targetCodeIS) {

		for (final String classname : targetCodeIS.keySet()) {

			final StringBuilder sb = new StringBuilder();
			sb.append("Classname : " + classname + "\n");
			sb.append("Content : \n\n");

			final ByteArrayInputStream is = (ByteArrayInputStream) targetCodeIS
					.get(classname);
			final DataInputStream dis = new DataInputStream(is);

			final File testFile = new File("Program.class");
			FileOutputStream outStream;

			try {
				outStream = new FileOutputStream(testFile);
				IOUtils.copy(dis, outStream);
			} catch (final Exception e) {
				e.printStackTrace();
			}

			try {
				logger.info("Classfile written to: "
						+ testFile.getCanonicalPath());
			} catch (final IOException e1) {
				e1.printStackTrace();
			}

			int i = 0;
			byte b;
			try {
				while ((b = (byte) dis.read()) != -1) {
					final String tmp = Integer.toHexString(((b + 256) % 256));
					if (tmp.length() < 2) {
						sb.append(0).append(tmp).append(" ");
					} else {
						sb.append(tmp).append(" ");
					}

					i++;
					if (i == 16) {
						sb.append("\n");
						i = 0;
					}
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
			System.out.println(sb.toString());
		}
	}

}
