package swp_compiler_ss13.javabite.token;

import swp_compiler_ss13.common.lexer.BoolToken;
import swp_compiler_ss13.common.lexer.TokenType;

public class BoolTokenJb extends TokenJb implements BoolToken {

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
	public Boolean getBooleanValue() {
		return Boolean.parseBoolean(getValue());
	}

}
