package swp_compiler_ss13.javabite.token;

import swp_compiler_ss13.common.lexer.RealToken;
import swp_compiler_ss13.common.lexer.TokenType;

public class RealTokenJb extends TokenJb implements RealToken {
	
	public RealTokenJb(TokenType type, String value) {
		super(type, value);
	}
	
	public RealTokenJb(TokenType type, String value, int line, int column) {
		super(type, value, line, column);
	}

	@Override
	public Double getDoubleValue() {
		return Double.parseDouble(getValue());
	}	
}
