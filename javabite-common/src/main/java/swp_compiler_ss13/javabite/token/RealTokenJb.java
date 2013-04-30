package swp_compiler_ss13.javabite.token;

import swp_compiler_ss13.common.lexer.RealToken;
import swp_compiler_ss13.common.lexer.TokenType;

public class RealTokenJb extends TokenJb implements RealToken {
	
	public RealTokenJb(TokenType type, String value) {
		super(type, value);
	}
	
	@Override
	public String getValue() {
		return super.getValue();
	}

	@Override
	public TokenType getTokenType() {
		return super.getTokenType();
	}

	@Override
	public Integer getLine() {
		return super.getLine();
	}

	@Override
	public Integer getColumn() {
		return super.getColumn();
	}

	@Override
	public Double getDoubleValue() {
		return Double.parseDouble(getValue());
	}	
}
