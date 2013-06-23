package swp_compiler_ss13.javabite.backend.translation;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.javabite.backend.utils.ConstantUtils;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

import java.util.*;

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

		this.jumpTargets = new HashSet<>();
		this.iter = new TacIterator(tac);

		// loop 1
		// perform primary analysis
		for (final Quadruple quad : iter) {
			switch (quad.getOperator()) {
			case BRANCH:
				checkBranch(quad);
				break;
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
			iter.set(copyQuadruple(quad, null, arg1, arg2, result));
		}
	}

	private static Quadruple copyQuadruple(final Quadruple q,
			final Operator op, final String arg1, final String arg2,
			final String res) {
		return new QuadrupleJb(op != null ? op : q.getOperator(),
				arg1 != null ? arg1 : q.getArgument1(), arg2 != null ? arg2
						: q.getArgument2(), res != null ? res : q.getResult());
	}

	private static class TacIterator implements ListIterator<Quadruple>,
			Iterable<Quadruple> {

		private ListIterator<Quadruple> iter;
		private List<Quadruple> tac;
		private int index;

		public TacIterator(final List<Quadruple> tac) {
			this.iter = tac.listIterator();
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
		public void set(Quadruple quadruple) {
			iter.set(quadruple);
		}

		@Override
		public void add(Quadruple quadruple) {
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
			this.index = 0;
			this.iter = tac.listIterator();
			return this;
		}

	}

}
