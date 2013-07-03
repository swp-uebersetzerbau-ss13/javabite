package swp_compiler_ss13.javabite.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public class QuadrupleReader {

	private final String filepath;

	public QuadrupleReader(final String filepath) {
		this.filepath = filepath;
	}

	public List<Quadruple> readQuadruples() throws IOException {
		final URL url = Thread.currentThread().getContextClassLoader()
				.getResource(filepath);
		assert url != null;
		final File file = new File(url.getPath());
		final BufferedReader in = new BufferedReader(new FileReader(file));
		final List<Quadruple> quadruples = new ArrayList<>();
		String line;
		while ((line = in.readLine()) != null) {
			if (line.length() == 0)
				continue;
			final String[] parts = line.split(" ");
			quadruples.add(new QuadrupleJb(
					Quadruple.Operator.valueOf(parts[0]), parts[1], parts[2],
					parts[3]));
		}
		return quadruples;
	}

}
