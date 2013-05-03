package swp_compiler_ss13.javabite.lexer;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import swp_compiler_ss13.common.lexer.Lexer;
import swp_compiler_ss13.common.lexer.Token;
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
	/**
	 * Test all kinds of tokens
	 */
public void basicTokenMatching() {
		
		//Basic types
		prepareLexer("12312");
		assertEquals(TokenType.NUM,   lexer.getNextToken().getTokenType());
		prepareLexer("183.13");
		assertEquals(TokenType.REAL,   lexer.getNextToken().getTokenType());
		prepareLexer("\"Hello World\"");
		assertEquals(TokenType.STRING,   lexer.getNextToken().getTokenType());
		
		//BinOps
		prepareLexer("-");
		assertEquals(TokenType.MINUS,   lexer.getNextToken().getTokenType());
		prepareLexer("+");
		assertEquals(TokenType.PLUS,   lexer.getNextToken().getTokenType());
		prepareLexer("*");
		assertEquals(TokenType.TIMES,   lexer.getNextToken().getTokenType());
		prepareLexer("/");
		assertEquals(TokenType.DIVIDE,   lexer.getNextToken().getTokenType());
		
		//bool ops
		prepareLexer("==");
		assertEquals(TokenType.EQUALS,   lexer.getNextToken().getTokenType());
		prepareLexer(">");
		assertEquals(TokenType.GREATER,   lexer.getNextToken().getTokenType());
		prepareLexer(">=");
		assertEquals(TokenType.GREATER_EQUAL,   lexer.getNextToken().getTokenType());
		prepareLexer("<");
		assertEquals(TokenType.LESS,   lexer.getNextToken().getTokenType());
		prepareLexer("<=");
		assertEquals(TokenType.LESS_OR_EQUAL,   lexer.getNextToken().getTokenType());
		prepareLexer("!");
		assertEquals(TokenType.NOT,   lexer.getNextToken().getTokenType());
		prepareLexer("!=");
		assertEquals(TokenType.NOT_EQUALS,   lexer.getNextToken().getTokenType());
		prepareLexer("||");
		assertEquals(TokenType.OR,   lexer.getNextToken().getTokenType());
		prepareLexer("&&");
		assertEquals(TokenType.AND,   lexer.getNextToken().getTokenType());
	
		
		//boolean values
		prepareLexer("true");
		assertEquals(TokenType.TRUE,   lexer.getNextToken().getTokenType());
		prepareLexer("false");
		assertEquals(TokenType.FALSE,   lexer.getNextToken().getTokenType());
		
		//others ops
		prepareLexer("=");
		assertEquals(TokenType.ASSIGNOP,   lexer.getNextToken().getTokenType());
		
		//other
		prepareLexer("break");
		assertEquals(TokenType.BREAK,   lexer.getNextToken().getTokenType());
		prepareLexer("do");
		assertEquals(TokenType.DO,   lexer.getNextToken().getTokenType());
		prepareLexer("");
		assertEquals(TokenType.EOF,   lexer.getNextToken().getTokenType());
		prepareLexer("dsad66s");
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		prepareLexer("if");
		assertEquals(TokenType.IF,   lexer.getNextToken().getTokenType());
		prepareLexer("[");
		assertEquals(TokenType.LEFT_BRACE,   lexer.getNextToken().getTokenType());
		prepareLexer("]");
		assertEquals(TokenType.RIGHT_BRACE,   lexer.getNextToken().getTokenType());
		prepareLexer("{");
		assertEquals(TokenType.LEFT_BRACKET,   lexer.getNextToken().getTokenType());
		prepareLexer("}");
		assertEquals(TokenType.RIGHT_BRACKET,   lexer.getNextToken().getTokenType());
		prepareLexer("(");
		assertEquals(TokenType.LEFT_PARAN,   lexer.getNextToken().getTokenType());
		prepareLexer(")");
		assertEquals(TokenType.RIGHT_PARAN,   lexer.getNextToken().getTokenType());
		prepareLexer("print");
		assertEquals(TokenType.PRINT,   lexer.getNextToken().getTokenType());
		prepareLexer("return");
		assertEquals(TokenType.RETURN,   lexer.getNextToken().getTokenType());
		prepareLexer(";");
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		prepareLexer("while");
		assertEquals(TokenType.WHILE,   lexer.getNextToken().getTokenType());
		
	}
	
	@Test
	/**
	 * Do advanced tests, some real szenarios
	 */
	public void advancedTokenMatching() {
		
		//Simple Match Calc
		prepareLexer("distance=a*b;");
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ASSIGNOP,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.TIMES,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.EOF,   lexer.getNextToken().getTokenType());
		
		//String assignment
		prepareLexer("str=\"Hello World\";");
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ASSIGNOP,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.STRING,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.EOF,   lexer.getNextToken().getTokenType());
		
		//if with bool op
		prepareLexer("if(i<10){\n}");
		assertEquals(TokenType.IF,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.LEFT_PARAN,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.LESS,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.NUM,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.RIGHT_PARAN,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.LEFT_BRACKET,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.RIGHT_BRACKET,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.EOF,   lexer.getNextToken().getTokenType());
		
		//while example
		prepareLexer("while(i >= 10");
		assertEquals(TokenType.WHILE,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.LEFT_PARAN,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.GREATER_EQUAL,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.NUM,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.EOF,   lexer.getNextToken().getTokenType());

		//Comment Example
		prepareLexer("#My First Comment\nmyid=5;");
		assertEquals(TokenType.COMMENT,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ASSIGNOP,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.NUM,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.EOF,   lexer.getNextToken().getTokenType());

		
		//Array Access
		prepareLexer("gate[9] = 2.67;");
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.LEFT_BRACE,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.NUM,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.RIGHT_BRACE,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ASSIGNOP,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.REAL,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.EOF,   lexer.getNextToken().getTokenType());
		
		//NOT test case
		prepareLexer("if(!available)");
		assertEquals(TokenType.IF,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.LEFT_PARAN,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.NOT,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.RIGHT_PARAN,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.EOF,   lexer.getNextToken().getTokenType());
		
		//Match Calculations with NUM
		prepareLexer("a=10/2;b=a*2;c=b-2;d=c+2;");
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ASSIGNOP,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.NUM,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.DIVIDE,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.NUM,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ASSIGNOP,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.TIMES,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.NUM,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ASSIGNOP,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.MINUS,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.NUM,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ASSIGNOP,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.PLUS,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.NUM,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		
		//Match Calculations with REAL
		prepareLexer("a=10.0/2.1;b=a*2.3;c=b-2.8;d=c+2.0;");
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ASSIGNOP,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.REAL,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.DIVIDE,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.REAL,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ASSIGNOP,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.TIMES,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.REAL,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ASSIGNOP,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.MINUS,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.REAL,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ASSIGNOP,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.PLUS,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.REAL,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		
		//bool ops
		prepareLexer("a<b;a>b;a<=b;a>=b;a==b;a!=b;");
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.LESS,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.GREATER,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.LESS_OR_EQUAL,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.GREATER_EQUAL,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.EQUALS,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.NOT_EQUALS,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		
		
		//&& || tests
		prepareLexer("if( (a && b) || (a || b)) ");
		assertEquals(TokenType.IF,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.LEFT_PARAN,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.LEFT_PARAN,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.AND,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.RIGHT_PARAN,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.OR,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.LEFT_PARAN,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.OR,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.RIGHT_PARAN,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.RIGHT_PARAN,   lexer.getNextToken().getTokenType());

		//do while loop
		prepareLexer("do {\n a++; } while ( a < 10 );");
		assertEquals(TokenType.DO,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.LEFT_BRACKET,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.PLUS,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.PLUS,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.RIGHT_BRACKET,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.WHILE,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.LEFT_PARAN,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.LESS,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.NUM,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.RIGHT_PARAN,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		
	}
	
	@Test
	/**
	 * Some stress tests
	 */
	public void stresstests() {
		String teststring = "";
		
		for(int i = 0 ; i < 10000; i++) {
			teststring += "a < b; \n #comment \n 6.56;";
		}
		
		prepareLexer(teststring);
		for(int i = 0 ; i < 10000; i++) {
			assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
			assertEquals(TokenType.LESS,   lexer.getNextToken().getTokenType());
			assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
			assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
			assertEquals(TokenType.COMMENT,   lexer.getNextToken().getTokenType());
			assertEquals(TokenType.REAL,   lexer.getNextToken().getTokenType());
			assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
			
		}		
		assertEquals(TokenType.EOF,   lexer.getNextToken().getTokenType());
		
	}
	
	@Test
	/**
	 * LOC COC Tests
	 */
	public void locCocTests() {
		String teststring = "";
		
		//in line 501 is b
		for(int i = 0 ; i < 500; i++) {
			teststring += "a++;\n";
		}
		teststring += "b = 100;\n";
		
		//in line 502 column 201 is c
		for(int i = 0 ; i < 50; i++) {
			teststring += "a++;";
		}
		teststring += "c = 100;";
		
		prepareLexer(teststring);
		
		for(int i = 0 ; i < 500; i++) {
			assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
			assertEquals(TokenType.PLUS,   lexer.getNextToken().getTokenType());
			assertEquals(TokenType.PLUS,   lexer.getNextToken().getTokenType());
			assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		}
		// test b values
		Token b_token = lexer.getNextToken();
		Assert.assertTrue(501 == b_token.getLine());
		Assert.assertTrue(0 == b_token.getColumn());
		assertEquals(TokenType.ID,   b_token.getTokenType());
		assertEquals(TokenType.ASSIGNOP,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.NUM,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		
		
		for(int i = 0 ; i < 50; i++) {
			assertEquals(TokenType.ID,   lexer.getNextToken().getTokenType());
			assertEquals(TokenType.PLUS,   lexer.getNextToken().getTokenType());
			assertEquals(TokenType.PLUS,   lexer.getNextToken().getTokenType());
			assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
	
		}
		
		// test c values
		Token c_token = lexer.getNextToken();
		Assert.assertTrue(502==c_token.getLine());
		Assert.assertTrue(200==c_token.getColumn());
		assertEquals(TokenType.ID,   c_token.getTokenType());
		assertEquals(TokenType.ASSIGNOP,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.NUM,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.SEMICOLON,   lexer.getNextToken().getTokenType());
		assertEquals(TokenType.EOF,   lexer.getNextToken().getTokenType());
				
		
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