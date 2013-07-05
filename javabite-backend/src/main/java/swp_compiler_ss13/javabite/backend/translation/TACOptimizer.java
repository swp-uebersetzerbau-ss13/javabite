package swp_compiler_ss13.javabite.backend.translation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.javabite.backend.utils.ConstantUtils;
import swp_compiler_ss13.javabite.backend.utils.QuadrupleUtils;

/**
 * TACOptimizer class.
 * 
 * @author Marco
 * @since 27.04.2013
 * 
 */
public class TACOptimizer {

	private Set<String> jumpTargets;
	private TacIterator iter;

	public void optimize(final List<Quadruple> tac) {

		jumpTargets = new HashSet<>();
		iter = new TacIterator(tac);

		// loop 1
		// perform primary analysis
		for (final Quadruple quad : iter) {
			switch (quad.getOperator()) {
			case BRANCH:
				checkBranch(quad);
			default:
				break;
			}
		}

		// loop 2
		// perform secondary analysis (requires loop 1 to gather information)
		for (final Quadruple quad : iter) {
			switch (quad.getOperator()) {
			case LABEL:
				if (!jumpTargets.contains(quad.getArgument1())) {
					iter.remove();
				}
				break;
			default:
				break;
			}
		}

	}

	private void checkBranch(final Quadruple quad) {
		final Quadruple next = iter.peek();
		String arg1 = null;
		String arg2 = null;
		String result = null;
		boolean replace = false;
		if (next != null && next.getOperator() == Operator.LABEL) {
			final String labelName = next.getArgument1();
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
		if (arg1 != null && arg2 != null) {
			// if both targets point to the next operation, remove jump
			iter.remove();
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
				iter.remove();
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
				iter.remove();
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
			iter.set(QuadrupleUtils.copyQuadruple(quad, null, arg1, arg2,
					result));
		}
	}

	private static class TacIterator implements ListIterator<Quadruple>,
			Iterable<Quadruple> {

		private ListIterator<Quadruple> iter;
		private final List<Quadruple> tac;
		private int index;

		public TacIterator(final List<Quadruple> tac) {
			iter = tac.listIterator();
			this.tac = tac;
			index = 0;
		}

		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public Quadruple next() {
			index++;
			return iter.next();
		}

		@Override
		public boolean hasPrevious() {
			return iter.hasPrevious();
		}

		@Override
		public Quadruple previous() {
			index--;
			return iter.previous();
		}

		@Override
		public int nextIndex() {
			return iter.nextIndex();
		}

		@Override
		public int previousIndex() {
			return iter.previousIndex();
		}

		@Override
		public void remove() {
			iter.remove();
		}

		@Override
		public void set(final Quadruple quadruple) {
			iter.set(quadruple);
		}

		@Override
		public void add(final Quadruple quadruple) {
			iter.add(quadruple);
		}

		public Quadruple peek() {
			if (hasNext()) {
				return tac.get(nextIndex());
			}
			return null;
		}

		public Quadruple current() {
			return tac.get(index);
		}

		@Override
		public Iterator<Quadruple> iterator() {
			index = 0;
			iter = tac.listIterator();
			return this;
		}

	}

}
