package swp_compiler_ss13.javabite.token;

import swp_compiler_ss13.common.lexer.NumToken;
import swp_compiler_ss13.common.lexer.TokenType;

public class NumTokenJb extends TokenJb implements NumToken {
	
	public NumTokenJb(TokenType type, String value) {
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
	public Long getLongValue() {
		return Long.parseLong(getValue());
	}	
}
