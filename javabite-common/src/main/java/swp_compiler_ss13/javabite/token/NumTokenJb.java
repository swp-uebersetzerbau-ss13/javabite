package swp_compiler_ss13.javabite.token;

import swp_compiler_ss13.common.lexer.NumToken;
import swp_compiler_ss13.common.lexer.TokenType;

public class NumTokenJb extends TokenJb implements NumToken {
	
	public NumTokenJb(TokenType type, String value) {
		super(type, value);
	}

	public NumTokenJb(TokenType type, String value, int line, int column) {
		super(type, value, line, column);
	}

	@Override
	public Long getLongValue() {
		return Long.parseLong(getValue());
	}	
}
