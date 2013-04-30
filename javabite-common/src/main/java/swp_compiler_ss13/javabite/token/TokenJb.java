package swp_compiler_ss13.javabite.token;

import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;

public class TokenJb implements Token {
	
	private String value;
	private TokenType type;
	private int line;
	private int column;
	
	@Override
	public String getValue() {
		return value;
	}

	@Override
	public TokenType getTokenType() {
		return type;
	}

	@Override
	public Integer getLine() {
		return line;
	}

	@Override
	public Integer getColumn() {
		return column;
	}
}
