package swp_compiler_ss13.javabite.parser.grammar;

import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar.Reduction;
import swp_compiler_ss13.javabite.token.TokenJb;

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
<<<<<<< HEAD
		List<Reduction> res= syn.derivateDFRightToLeft(sc);
		logger.info("derivation of tList {}",tList);
		logger.info("res : {}",resAsReadableString(res));
	}
	
	@Test
	public void testSimpleLR(){
		List<Token> tList=new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res= syn.derivateDFLeftToRight(sc);
		logger.info("derivation of tList {}",tList);
		logger.info("res : {}",resAsReadableString(res));
=======
		List<Reduction> res = syn.derivate(sc);
		logger.info("derivation of tList {}", tList);
		logger.info("res : {}", resAsReadableString(res));
>>>>>>> 4f8da34f9b5f068ed57a8365eabae08adca1f16c
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
<<<<<<< HEAD
		List<Reduction> res= syn.derivateDFRightToLeft(sc);
		logger.info("derivation of tList {}",tList);
		logger.info("res : {}",resAsReadableString(res));
=======
		List<Reduction> res = syn.derivate(sc);
		logger.info("derivation of tList {}", tList);
		logger.info("res : {}", resAsReadableString(res));
>>>>>>> 4f8da34f9b5f068ed57a8365eabae08adca1f16c
	}

	@Test
	public void testSimpleStatement() {
		List<Token> tList = new LinkedList<>();
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.REAL, "2.0"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
<<<<<<< HEAD
		List<Reduction> res= syn.derivateDFRightToLeft(sc);
		logger.info("derivation of tList {}",tList);
		logger.info("res : {}",resAsReadableString(res));
=======
		List<Reduction> res = syn.derivate(sc);
		logger.info("derivation of tList {}", tList);
		logger.info("res : {}", resAsReadableString(res));
>>>>>>> 4f8da34f9b5f068ed57a8365eabae08adca1f16c
	}

	@Test
	public void testSimpleProgram() {
		List<Token> tList = new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.REAL, "2.0"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
<<<<<<< HEAD
		List<Reduction> res= syn.derivateDFRightToLeft(sc);
		logger.info("derivation of tList {}",tList);
		logger.info("res : {}",resAsReadableString(res));
=======
		List<Reduction> res = syn.derivate(sc);
		logger.info("derivation of tList {}", tList);
		logger.info("res : {}", resAsReadableString(res));
>>>>>>> 4f8da34f9b5f068ed57a8365eabae08adca1f16c
	}

	@Test
	public void testLessSimpleProgram() {
		List<Token> tList = new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.REAL, "2.0"));
		tList.add(new TokenJb(TokenType.PLUS, "+"));
		tList.add(new TokenJb(TokenType.REAL, "2"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
<<<<<<< HEAD
		List<Reduction> res= syn.derivateDFRightToLeft(sc);
		logger.info("derivation of tList {}",tList);
		logger.info("res : {}",resAsReadableString(res));
=======
		List<Reduction> res = syn.derivate(sc);
		logger.info("derivation of tList {}", tList);
		logger.info("res : {}", resAsReadableString(res));
>>>>>>> 4f8da34f9b5f068ed57a8365eabae08adca1f16c
	}

	@Test
	public void testExpressionWithSelfReference() {
		List<Token> tList = new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.REAL, "2.0"));
		tList.add(new TokenJb(TokenType.PLUS, "+"));
		tList.add(new TokenJb(TokenType.ID, "2"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
<<<<<<< HEAD
		List<Reduction> res= syn.derivateDFRightToLeft(sc);
		logger.info("derivation of tList {}",tList);
		logger.info("res : {}",resAsReadableString(res));
	}
	
	@Test
	public void testLexerIntegrationComplex(){
		String toCheck="long i;long j;";
		LexerJb lex=new LexerJb();
		lex.setSourceStream(new ByteArrayInputStream(toCheck.getBytes()));
		List<Token> tList=new LinkedList<>();
		Token t;
		do{
			t=lex.getNextToken();
			tList.add(t);
		} while (t.getTokenType()!=TokenType.EOF);
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res= syn.derivateDFRightToLeft(sc);
		logger.info("Source was\n {}\n",tList);
		logger.info("res : {}",resAsReadableString(res));
=======
		List<Reduction> res = syn.derivate(sc);
		logger.info("derivation of tList {}", tList);
		logger.info("res : {}", resAsReadableString(res));
>>>>>>> 4f8da34f9b5f068ed57a8365eabae08adca1f16c
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
<<<<<<< HEAD
		List<Reduction> res= syn.derivateDFRightToLeft(sc);
		logger.info("Source was\n {}\n",tList);
		logger.info("res : {}",resAsReadableString(res));
=======
		List<Reduction> res = syn.derivate(sc);
		logger.info("Source was\n {}\n", tList);
		logger.info("res : {}", resAsReadableString(res));
>>>>>>> 4f8da34f9b5f068ed57a8365eabae08adca1f16c
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
		tList.add(new TokenJb(TokenType.NUM, "2"));
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
<<<<<<< HEAD
		List<Reduction> res= syn.derivateDFRightToLeft(sc);
		logger.info("Source was\n {}\n",tList);
		logger.info("res : {}",resAsReadableString(res));
	}
	
	
	@Test 
	public void testLexerIntegration_add() throws FileNotFoundException{
		testLexerIntegrationForFile(new File("../common/examples/m1/add.prog"));
	}
	
	@Test 
	public void testLexerIntegration_error_double_decl() throws FileNotFoundException{
		testLexerIntegrationForFile(new File("../common/examples/m1/error_double_decl.prog"));
	}
	@Test (expected=WordNotInLanguageGrammarException.class)
	public void testLexerIntegration_error_invalid_ids() throws FileNotFoundException{
		testLexerIntegrationForFile(new File("../common/examples/m1/error_invalid_ids.prog"));
	}
	@Test (expected=WordNotInLanguageGrammarException.class)
	public void testLexerIntegration_error_multiple_minus_e_notation() throws FileNotFoundException{
		testLexerIntegrationForFile(new File("../common/examples/m1/error_multiple_minus_e_notation.prog"));
	}
	@Test (expected=WordNotInLanguageGrammarException.class)
	public void testLexerIntegration_error_multiple_pluses_in_exp() throws FileNotFoundException{
		testLexerIntegrationForFile(new File("../common/examples/m1/error_multiple_pluses_in_exp.prog"));
	}
	@Test
	public void testLexerIntegration_error_undef_return() throws FileNotFoundException{
		testLexerIntegrationForFile(new File("../common/examples/m1/error_undef_return.prog"));
	}
	@Test
	public void testLexerIntegration_paratheses() throws FileNotFoundException{
		testLexerIntegrationForFile(new File("../common/examples/m1/paratheses.prog"));
	}
	@Test
	public void testLexerIntegration_simple_add() throws FileNotFoundException{
		testLexerIntegrationForFile(new File("../common/examples/m1/simple_add.prog"));
=======
		List<Reduction> res = syn.derivate(sc);
		logger.info("Source was\n {}\n", tList);
		logger.info("res : {}", resAsReadableString(res));
>>>>>>> 4f8da34f9b5f068ed57a8365eabae08adca1f16c
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
		tList.add(new TokenJb(TokenType.NUM, "2"));
		tList.add(new TokenJb(TokenType.PLUS, "+"));
		tList.add(new TokenJb(TokenType.NUM, "2"));
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
<<<<<<< HEAD
		List<Reduction> res= syn.derivateDFRightToLeft(sc);
		logger.info("Source was\n {}\n",tList);
		logger.info("res : {}",resAsReadableString(res));
=======
		List<Reduction> res = syn.derivate(sc);
		logger.info("Source was\n {}\n", tList);
		logger.info("res : {}", resAsReadableString(res));
>>>>>>> 4f8da34f9b5f068ed57a8365eabae08adca1f16c
	}

	@Test
	public void testLexerIntegrationSimple() {
		List<Token> tList = new LinkedList<>();
		// long i;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
<<<<<<< HEAD
		List<Reduction> res= syn.derivateDFRightToLeft(sc);
		logger.info("Source was\n {}\n",tList);
		logger.info("res : {}",resAsReadableString(res));
=======
		List<Reduction> res = syn.derivate(sc);
		logger.info("Source was\n {}\n", tList);
		logger.info("res : {}", resAsReadableString(res));
>>>>>>> 4f8da34f9b5f068ed57a8365eabae08adca1f16c
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
