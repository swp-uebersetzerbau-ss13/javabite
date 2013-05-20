package swp_compiler_ss13.javabite.lexer;

import swp_compiler_ss13.common.lexer.TokenType;

public enum JavabiteTokenType {
	TRUE(TokenType.TRUE, "true(?!\\w)"),
	FALSE(TokenType.FALSE, "false(?!\\w)"),
	IF(TokenType.IF, "if(?!\\w)"),
	ELSE(TokenType.ELSE, "else(?!\\w)"),
	WHILE(TokenType.WHILE, "while(?!\\w)"),
	DO(TokenType.DO, "do(?!\\w)"),
	BREAK(TokenType.BREAK, "break(?!\\w)"),
	RETURN(TokenType.RETURN, "return(?!\\w)"),
	PRINT(TokenType.PRINT, "print(?!\\w)"),
	LONGSYMBOL(TokenType.LONG_SYMBOL, "long(?!\\w)"),
	DOUBLESYMBOL(TokenType.DOUBLE_SYMBOL, "double(?!\\w)"),
	BOOLSYMBOL(TokenType.BOOL_SYMBOL, "bool(?!\\w)"),
	STRINGSYMBOL(TokenType.STRING_SYMBOL, "string(?!\\w)"),
	AND(TokenType.AND, "\\&\\&"),
	OR(TokenType.OR, "\\|\\|"),
	EQUALS(TokenType.EQUALS, "\\=\\="),
	NOTEQUALS(TokenType.NOT_EQUALS, "\\!\\="),
	ASSIGNOP(TokenType.ASSIGNOP, "\\="),
	LESSOREQUAL(TokenType.LESS_OR_EQUAL, "\\<\\="),
	GREATEREQUAL(TokenType.GREATER_EQUAL, "\\>\\="),
	LESS(TokenType.LESS, "\\<"),
	GREATER(TokenType.GREATER, "\\>"),
	PLUS(TokenType.PLUS, "\\+"),
	MINUS(TokenType.MINUS, "\\-"),
	TIMES(TokenType.TIMES, "\\*"),
	DIVIDE(TokenType.DIVIDE, "\\/"),
	NOT(TokenType.NOT, "\\!"),
	LEFTPARAN(TokenType.LEFT_PARAN, "\\("),
	RIGHTPARAN(TokenType.RIGHT_PARAN, "\\)"),
	LEFTBRACKET(TokenType.LEFT_BRACKET, "\\["),
	RIGHTBRACKET(TokenType.RIGHT_BRACKET, "\\]"),
	LEFTBRACE(TokenType.LEFT_BRACE, "\\{"),
	RIGHTBRACE(TokenType.RIGHT_BRACE, "\\}"),
	SEMICOLON(TokenType.SEMICOLON, ";"),
	REALWITHERROR(TokenType.NOT_A_TOKEN, "[0-9]+\\.[0-9]+(E|e)-*[0-9]+\\.[0-9]+"),
	REALWITHERROR2(TokenType.NOT_A_TOKEN, "[0-9]+\\.[0-9]+(E|e)-{2,}[0-9]+"),
	REAL(TokenType.REAL, "[0-9]+\\.[0-9]+((E|e)-?[0-9]+)?"),
	NUMWITHERROR(TokenType.NOT_A_TOKEN, "[0-9]+(E|e)-*[0-9]+\\.[0-9]+"),
	NUMWITHERROR2(TokenType.NOT_A_TOKEN, "[0-9]+(E|e)-{2,}[0-9]+"),
	NUM(TokenType.NUM, "[0-9]+((E|e)-?[0-9]+)?"),
	STRING(TokenType.STRING, "\\\"(?:[^\\\"\\\\]+|\\\\.)*\\\""),
	COMMENT(TokenType.COMMENT, "#[^\n]*"),
	ID(TokenType.ID, "[a-zA-Z]\\w*"),
	WHITESPACE(null,"[ \t\f\r]+"),
	LINEBREAK(null,"[\n]+"),
	EOF(TokenType.EOF, null),
	NOTATOKEN(TokenType.NOT_A_TOKEN, ".");
	
	JavabiteTokenType(TokenType tokenType, String regex) {
		this.tokenType = tokenType;
		this.regex = regex;
	}
	
	private TokenType tokenType;
	private String regex;
	
	public TokenType getTokenType() {
		return tokenType;
	}
	
	public String getRegex() {
		return regex;
	}
}
