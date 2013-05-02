package swp_compiler_ss13.javabite.lexer;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.junit.BeforeClass;
import org.junit.Test;

import swp_compiler_ss13.common.lexer.Lexer;
import swp_compiler_ss13.common.lexer.TokenType;

public class LexerTests {
	
	static Lexer lexer = null;

	@BeforeClass
	public static void setUp() throws Exception {
		lexer = new AlternativLexer();
	}

	@Test
	public void testNonInitializedParser() {
		assertEquals(TokenType.EOF, lexer.getNextToken().getTokenType());
	}
	
	@Test
	public void testEmptyInitializedParser() {
		prepareLexer("");
		assertEquals(TokenType.EOF, lexer.getNextToken().getTokenType());
		assertEquals(TokenType.EOF, lexer.getNextToken().getTokenType());
	}
	
	@Test
	public void testSimpleTokenMatching() {
		prepareLexer("true false if while do break return print");
		assertEquals(TokenType.TRUE,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.FALSE,  lexer.getNextToken().getTokenType());
		assertEquals(TokenType.IF,     lexer.getNextToken().getTokenType());
		assertEquals(TokenType.WHILE,  lexer.getNextToken().getTokenType());
		assertEquals(TokenType.DO,     lexer.getNextToken().getTokenType());
		assertEquals(TokenType.BREAK,  lexer.getNextToken().getTokenType());
		assertEquals(TokenType.RETURN, lexer.getNextToken().getTokenType());
		assertEquals(TokenType.PRINT,  lexer.getNextToken().getTokenType());
		assertEquals(TokenType.EOF,    lexer.getNextToken().getTokenType());
		assertEquals(TokenType.EOF,    lexer.getNextToken().getTokenType());
	}
	
	@Test
	public void testOtherThings() {
		fail("Other things have to be tested");
	}

	/**
	 * set testString as input for the parser
	 */
	private void prepareLexer(String testString) {
		try {
			lexer.setSourceStream(new ByteArrayInputStream(testString.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			fail("UTF-8 is unknown ?!");
		}
	}
}