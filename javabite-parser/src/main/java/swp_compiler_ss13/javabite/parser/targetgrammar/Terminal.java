package swp_compiler_ss13.javabite.parser.targetgrammar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.parser.grammar.Symbol;

class Terminal implements Symbol{
	TokenType t;
	Logger logger=LoggerFactory.getLogger(Terminal.class);
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Terminal other = (Terminal) obj;
		if (type != other.type)
			return false;
		return true;
	}
	TokenType type;
	Token token;
	String str;
	
	Terminal(TokenType t){
		this.type=t;
		
	}
	Terminal(Token t){
		this.token=t;
		this.type=t.getTokenType();
	}
	Terminal(String str){
		this.str=str;
	}
	
	public Token getToken(){
		return token;
	}
	
	@Override
	public String toString() {
		return type!=null?type.toString():str;
	}
	
}
