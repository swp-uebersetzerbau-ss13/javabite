package swp_compiler_ss13.javabite.backend.translation;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.javabite.backend.utils.ConstantUtils;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TACOptimizer class.
 * 
 * @author Marco
 * @since 27.04.2013
 * 
 */
public class TACOptimizer {

	private Set<String> jumpTargets;
	private List<Quadruple> tac;
	private int index;

    private int removed = 0;

	public void optimize(final List<Quadruple> tac) {

		this.jumpTargets = new HashSet<>();
		this.tac = tac;

		// loop 1
		for (index = 0; index < tac.size(); index++) {
			final Quadruple quad = tac.get(index);
			switch (quad.getOperator()) {
			case BRANCH:
				checkBranch(quad);
				break;
			default:
				break;
			}
		}

		// loop 2
		for (index = 0; index < tac.size(); index++) {
			final Quadruple quad = tac.get(index);
            if(quad == null)
                continue;
			if (quad.getOperator() == Operator.LABEL) {
				if (!jumpTargets.contains(quad.getArgument1())) {
					remove();
				}
			}
		}

		// loop 3
		for (index = tac.size() - 1; index >= 0; index--) {
			final Quadruple quad = tac.get(index);
			if (quad == null) {
				tac.remove(index);
                removed++;
			}
		}

	}

	private void checkBranch(final Quadruple quad) {
		final Quadruple next = peek();
		String arg1 = null;
		String arg2 = null;
		String result = null;
		boolean replace = false;
		if (next != null && next.getOperator() == Operator.LABEL) {
			String labelName = next.getArgument1();
			// check if next label is true target
			if (quad.getArgument1().equals(labelName)) {
				arg1 = ConstantUtils.SYMBOL_IGNORE_PARAM;
				replace = true;
			} else {
				jumpTargets.add(quad.getArgument1());
			}
			// check if next label is false target
			if (quad.getArgument2().equals(labelName)) {
				arg2 = ConstantUtils.SYMBOL_IGNORE_PARAM;
				replace = true;
			} else {
				jumpTargets.add(quad.getArgument2());
			}
		}
		if ((arg1 != null && arg2 != null)) {
			// if both targets point to the next operation, remove jump
			remove();
			return;
        } else if (quad.getArgument1().equals(quad.getArgument2())) {
            // if both targets are the same, remove second target
            arg2 = ConstantUtils.SYMBOL_IGNORE_PARAM;
            result = ConstantUtils.SYMBOL_IGNORE_PARAM;
            replace = true;
		} else if (quad.getResult().equals(ConstantUtils.CONSTANT_VALUE_TRUE)) {
			// condition is constantly true
			if (arg1 != null) {
				// jump is always to next operation, remove jump
				remove();
				return;
			} else {
				// convert to unconditional jump
				arg2 = ConstantUtils.SYMBOL_IGNORE_PARAM;
				result = ConstantUtils.SYMBOL_IGNORE_PARAM;
				replace = true;
			}
		} else if (quad.getResult().equals(ConstantUtils.CONSTANT_VALUE_FALSE)) {
			// condition is constantly false
			if (arg2 != null) {
				// jump is always to next operation, remove jump
				remove();
				return;
			} else {
				// convert to unconditional jump
				arg1 = quad.getArgument2();
				arg2 = ConstantUtils.SYMBOL_IGNORE_PARAM;
				result = ConstantUtils.SYMBOL_IGNORE_PARAM;
				replace = true;
			}
		}
		if (replace) {
			replace(copyQuadruple(quad, null, arg1, arg2, result));
		}
	}

	private void remove() {
		replace(null);
	}

	private void replace(final Quadruple quad) {
		tac.set(index, quad);
	}

	private Quadruple peek() {
		if (index + 1 < tac.size()) {
			return tac.get(index + 1);
		}
		return null;
	}

	private static Quadruple copyQuadruple(final Quadruple q,
			final Operator op, final String arg1, final String arg2,
			final String res) {
		return new QuadrupleJb(op != null ? op : q.getOperator(),
				arg1 != null ? arg1 : q.getArgument1(), arg2 != null ? arg2
						: q.getArgument2(), res != null ? res : q.getResult());
	}

}
