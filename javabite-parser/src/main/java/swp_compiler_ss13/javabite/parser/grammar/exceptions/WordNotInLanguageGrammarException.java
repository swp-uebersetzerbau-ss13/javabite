package swp_compiler_ss13.javabite.parser.grammar.exceptions;

import swp_compiler_ss13.javabite.parser.targetgrammar.Terminal;

public class WordNotInLanguageGrammarException extends GrammarException {

	public WordNotInLanguageGrammarException(Terminal tok) {
		super(tok);
	}


	
}
