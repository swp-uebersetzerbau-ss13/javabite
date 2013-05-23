package swp_compiler_ss13.javabite.token;

import swp_compiler_ss13.common.lexer.BoolToken;
import swp_compiler_ss13.common.lexer.TokenType;

public class BoolTokenJb extends TokenJb implements BoolToken {
	
	public BoolTokenJb(TokenType type, String value) {
		super(type, value);
	}
	
	public BoolTokenJb(TokenType type, String value, int line, int column) {
		super(type, value, line, column);
	}

	@Override
	public Boolean getBooleanValue() {
		return Boolean.parseBoolean(getValue());
	}

}
