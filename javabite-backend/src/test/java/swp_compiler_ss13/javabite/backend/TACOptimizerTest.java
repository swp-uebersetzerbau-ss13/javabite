package swp_compiler_ss13.javabite.backend;

import org.junit.Before;
import org.junit.Test;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.javabite.backend.translation.TACOptimizer;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TACOptimizerTest {

	private TACOptimizer optimizer;

	@Before
	public void setUp() {
		optimizer = new TACOptimizer();
	}

	@Test
	public void testOptimizeBranch() {
		final List<Quadruple> tac = new ArrayList<>();

		// constant true
		// expected: convert to unconditional jump to first target
		tac.add(new QuadrupleJb(Operator.BRANCH, "ttrue", "tfalse", "#TRUE"));

		// constant false
		// expected: convert to unconditional jump to second target
		tac.add(new QuadrupleJb(Operator.BRANCH, "ttrue", "tfalse", "#FALSE"));

		// jump to next on true
		// expected: remove first target
		tac.add(new QuadrupleJb(Operator.BRANCH, "ttrue", "tfalse", "x"));
		tac.add(new QuadrupleJb(Operator.LABEL, "ttrue", "!", "!"));

		// jump to next on false
		// expected: remove second target
		tac.add(new QuadrupleJb(Operator.BRANCH, "ttrue", "tfalse", "x"));
		tac.add(new QuadrupleJb(Operator.LABEL, "tfalse", "!", "!"));

		// both jumps in same direction
		// expected: convert to unconditional jump
		tac.add(new QuadrupleJb(Operator.BRANCH, "tsame", "tsame", "x"));

		// unused label
		// expected: label is removed
		tac.add(new QuadrupleJb(Operator.LABEL, "tunused", "!", "!"));

		optimizer.optimize(tac);
	}

	@Test
	public void testOptimizeConcatString() throws IOException {
		final List<Quadruple> tac = new QuadrupleReader("opt_concat")
				.readQuadruples();
		optimizer.optimize(tac);
	}

}
