package swp_compiler_ss13.javabite.token;

import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;

public class TokenJb implements Token {
	
	private String value;
	private TokenType type;
	private int line;
	private int column;
	
	// TODO: line and column still needed in constructor
	public TokenJb(TokenType type, String value) {
		this.value = value;
		this.type = type;
	}
	
	public TokenJb(TokenType type, String value, int line, int column) {
		this(type, value);
		this.line = line;
		this.column = column;
	}
	
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
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "\t\t-\t" + type + "\t-\t" + value + "(" + line + "," + column + ")";
	}
}
