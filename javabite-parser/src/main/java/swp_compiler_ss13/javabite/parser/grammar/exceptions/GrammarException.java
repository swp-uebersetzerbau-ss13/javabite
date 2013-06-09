package swp_compiler_ss13.javabite.parser.grammar.exceptions;

import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.javabite.parser.targetgrammar.Terminal;

public class GrammarException extends RuntimeException{
	Token tok;
	public GrammarException(Terminal tok) {
		this.tok=tok.getToken();
	}
	public Token getRelatedToken(){
		return tok;
	}
}
