package swp_compiler_ss13.javabite.parser.grammar.exceptions;

import swp_compiler_ss13.javabite.parser.targetgrammar.Terminal;

public class AmbiguityInDerivationGrammarException extends GrammarException {

	public AmbiguityInDerivationGrammarException(Terminal tok) {
		super(tok);
	}

	private static final long serialVersionUID = 1L;

}
