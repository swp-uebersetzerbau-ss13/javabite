package swp_compiler_ss13.javabite.parser.targetgrammar;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.ast.ASTJb;
import swp_compiler_ss13.javabite.parser.astGenerator.ASTGenerator;
import swp_compiler_ss13.javabite.parser.grammar.exceptions.WordNotInLanguageGrammarException;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar.Reduction;
import swp_compiler_ss13.javabite.token.NumTokenJb;
import swp_compiler_ss13.javabite.token.RealTokenJb;
import swp_compiler_ss13.javabite.token.TokenJb;

/**
 * TODO: validate results
 * @author Till
 *
 */
public class ESTGrammarTestCase {

	Logger logger = LoggerFactory.getLogger(ESTGrammarTestCase.class);

	TargetGrammar syn = new TargetGrammar();

	@Test
	public void testIfCompiles() {
		assertTrue(true);
	}

	@Test
	public void testSimpleDeclaration() {
		List<Token> tList = new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFRightToLeft(sc);
	}

	@Test
	public void testSimpleDoubleDeclaration() {
		List<Token> tList = new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "j"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFRightToLeft(sc);
	}
	
	@Test(expected=WordNotInLanguageGrammarException.class)
	public void testNotInGrammarException() {
		List<Token> tList = new LinkedList<>();
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFRightToLeft(sc);
	}

	@Test
	public void testSimpleStatement() {
		List<Token> tList = new LinkedList<>();
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new RealTokenJb(TokenType.REAL, "2.0"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFRightToLeft(sc);
	}

	@Test
	public void testSimpleProgram() {
		List<Token> tList = new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new RealTokenJb(TokenType.REAL, "2.0"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFRightToLeft(sc);
	}

	@Test
	public void testLessSimpleProgram() {
		List<Token> tList = new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new RealTokenJb(TokenType.REAL, "2.0"));
		tList.add(new TokenJb(TokenType.PLUS, "+"));
		tList.add(new NumTokenJb(TokenType.REAL, "2"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFRightToLeft(sc);
	}

	@Test
	public void testExpressionWithSelfReference() {
		List<Token> tList = new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new RealTokenJb(TokenType.REAL, "2.0"));
		tList.add(new TokenJb(TokenType.PLUS, "+"));
		tList.add(new NumTokenJb(TokenType.ID, "2"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFRightToLeft(sc);
	}

	@Test
	public void testLexerIntegrationComplex() {
		List<Token> tList = new LinkedList<>();
		// long i;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		// long j;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "j"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFRightToLeft(sc);
	}

	@Test
	public void testLexerIntegrationVeryComplex() {
		List<Token> tList = new LinkedList<>();
		// long i;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		// long j;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		// i=(j=2);
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.LEFT_PARAN, "("));
		tList.add(new TokenJb(TokenType.ID, "j"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new NumTokenJb(TokenType.NUM, "2"));
		tList.add(new TokenJb(TokenType.RIGHT_PARAN, ")"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		// j=2;
		tList.add(new TokenJb(TokenType.ID, "j"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new NumTokenJb(TokenType.NUM, "2"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		// return j;
		tList.add(new TokenJb(TokenType.RETURN, "return"));
		tList.add(new TokenJb(TokenType.ID, "j"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFRightToLeft(sc);
	}

	@Test
	public void testLexerIntegrationVeryComplexAllElements() {
		List<Token> tList = new LinkedList<>();
		// long i;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		// long j;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "j"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		// i=(2+2);
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.LEFT_PARAN, "("));
		tList.add(new NumTokenJb(TokenType.NUM, "2"));
		tList.add(new TokenJb(TokenType.PLUS, "+"));
		tList.add(new NumTokenJb(TokenType.NUM, "2"));
		tList.add(new TokenJb(TokenType.RIGHT_PARAN, ")"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		// j=2;
		tList.add(new TokenJb(TokenType.ID, "j"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.NUM, "2"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		// return j;
		tList.add(new TokenJb(TokenType.RETURN, "return"));
		tList.add(new TokenJb(TokenType.ID, "j"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFRightToLeft(sc);
	}

	@Test
	public void testLexerIntegrationSimple() {
		List<Token> tList = new LinkedList<>();
		// long i;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFRightToLeft(sc);
	}
	
	
	@Test
	public void testM2AddProgram() {
		/*# returns 10
		# prints nothing
		long a;
		long b;
		long c;

		a = 4;
		b = 3;
		c = 2;

		a = b = 4;
		c = a + b + c;

		return c;
		*/
		List<Token> tList = new LinkedList<>();
		// #returns 10
		tList.add(new TokenJb(TokenType.COMMENT, "returns 10"));
		// #prints nothing		
		tList.add(new TokenJb(TokenType.COMMENT, "prints nothing"));
		
		// long a;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "a"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// long a;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "b"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));

		// long a;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "c"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// a = 4;
		tList.add(new TokenJb(TokenType.ID, "a"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new NumTokenJb(TokenType.NUM, "4"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// b = 3;
		tList.add(new TokenJb(TokenType.ID, "b"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new NumTokenJb(TokenType.NUM, "3"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// c = 2;
		tList.add(new TokenJb(TokenType.ID, "c"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new NumTokenJb(TokenType.NUM, "2"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// a = b = 4;
		tList.add(new TokenJb(TokenType.ID, "a"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.ID,"b"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new NumTokenJb(TokenType.NUM, "4"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// 	c = a + b + c;
		tList.add(new TokenJb(TokenType.ID, "c"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.ID,"a"));
		tList.add(new TokenJb(TokenType.PLUS, "+"));
		tList.add(new TokenJb(TokenType.ID,"b"));
		tList.add(new TokenJb(TokenType.PLUS, "+"));
		tList.add(new TokenJb(TokenType.ID,"c"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// return c
		tList.add(new TokenJb(TokenType.RETURN,"return"));
		tList.add(new TokenJb(TokenType.ID,"c"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFRightToLeft(sc);
		
	}
	
	@Test
	public void testM2AddProgramSimplified() {
		/*# returns 10
		a = b = 4;
		*/
		List<Token> tList = new LinkedList<>();
				// a = b = 4;
		tList.add(new TokenJb(TokenType.ID, "a"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.ID,"b"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new NumTokenJb(TokenType.NUM, "4"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFLeftToRight(sc);
		
	}
	
	

	private String resAsReadableString(List<Reduction> res) {
		StringBuilder strb = new StringBuilder();
		boolean first = true;
		for (Reduction prod : res) {
			if (!first)
				strb.append(", ");
			else
				first = false;
			strb.append(toReadableString(prod));
		}
		return strb.toString();
	}

	private String toReadableString(TargetGrammar.Reduction reduction) {
		StringBuilder strb = new StringBuilder();
		strb.append(reduction.getLeftSide());
		strb.append("-> {");
		boolean first = true;
		for (Object o : reduction.getRightSide()) {
			if (!first)
				strb.append(", ");
			else
				first = false;
			if (o instanceof Token) {
				Token tok = (Token) o;
				strb.append(tok.getTokenType());
				strb.append("[");
				strb.append(tok.getValue());
				strb.append("]");
			} else
				strb.append(o);
		}
		strb.append(" }");
		return strb.toString();
	}
}