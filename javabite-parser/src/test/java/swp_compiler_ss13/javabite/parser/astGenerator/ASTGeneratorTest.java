package swp_compiler_ss13.javabite.parser.astGenerator;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.common.parser.SymbolTable;
import swp_compiler_ss13.common.types.Type.Kind;
import swp_compiler_ss13.javabite.ast.ASTJb;
import swp_compiler_ss13.javabite.parser.astGenerator.ASTGenerator;
import swp_compiler_ss13.javabite.parser.grammar.exceptions.AmbiguityInDerivationGrammarException;
import swp_compiler_ss13.javabite.parser.grammar.exceptions.WordNotInLanguageGrammarException;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar.Reduction;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar.SourceCode;
import swp_compiler_ss13.javabite.token.NumTokenJb;
import swp_compiler_ss13.javabite.token.RealTokenJb;
import swp_compiler_ss13.javabite.token.TokenJb;
import static org.junit.Assert.*;
import static swp_compiler_ss13.common.lexer.TokenType.*;

/**
 * TODO: validate results
 * @author Till
 *
 */
public class ASTGeneratorTest {
	ASTGenerator instance;
	Logger logger=LoggerFactory.getLogger(this.getClass());
	TargetGrammar syn=new TargetGrammar();
	
	
	@Test
	public void testSimpleDeclaration(){
		List<Token> tList=new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res= syn.derivateDFLeftToRight(sc);
		instance=new ASTGenerator(res);
		AST ast=instance.generateAST();
		
	}
	
	
	@Test
	public void testSimpleDoubleDeclaration(){
		List<Token> tList=new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "j"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res= syn.derivateDFLeftToRight(sc);
		instance=new ASTGenerator(res);
		AST ast=instance.generateAST();
	}
	
	@Test
	public void testSimpleDoubleInvalidDeclaration(){
		List<Token> tList=new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res= syn.derivateDFLeftToRight(sc);
		instance=new ASTGenerator(res);
		AST ast=instance.generateAST();
	}
	
	
	@Test
	public void testSimpleStatement(){
		List<Token> tList=new LinkedList<>();
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new RealTokenJb(TokenType.REAL,"2.0"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res= syn.derivateDFLeftToRight(sc);
		instance=new ASTGenerator(res);
		AST ast=instance.generateAST();
	}
	
	
	@Test
	public void testSimpleProgram(){
		List<Token> tList=new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new RealTokenJb(TokenType.REAL,"2.0"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res= syn.derivateDFLeftToRight(sc);
		instance=new ASTGenerator(res);
		AST ast=instance.generateAST();
	}
	
	@Test
	public void testLessSimpleProgram(){
		List<Token> tList=new LinkedList<>();
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new RealTokenJb(TokenType.REAL,"2.0"));
		tList.add(new TokenJb(TokenType.DIVIDE,"/"));
		tList.add(new NumTokenJb(TokenType.NUM,"2"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res= syn.derivateDFLeftToRight(sc);
		instance=new ASTGenerator(res);
		AST ast=instance.generateAST();
	}
	
	@Test
	public void testComplexProgram(){
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
		tList.add(new NumTokenJb(TokenType.NUM, "2"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		// return j;
		tList.add(new TokenJb(TokenType.RETURN, "return"));
		tList.add(new TokenJb(TokenType.ID, "j"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res= syn.derivateDFLeftToRight(sc);
		instance=new ASTGenerator(res);
		AST ast=instance.generateAST();
	}
	
	@Test
	public void testComplexProgramSymbolTable(){
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
		tList.add(new NumTokenJb(TokenType.NUM, "2"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		// return j;
		tList.add(new TokenJb(TokenType.RETURN, "return"));
		tList.add(new TokenJb(TokenType.ID, "j"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res= syn.derivateDFLeftToRight(sc);
		instance=new ASTGenerator(res);
		AST ast=instance.generateAST();
		SymbolTable table=ast.getRootNode().getSymbolTable();
		assertTrue("should be declared",table.isDeclared("i"));
		assertTrue("should be declared",table.isDeclared("j"));
		assertFalse("should be not declared",table.isDeclared("ij"));
		assertEquals("should be the same",Kind.LONG,table.lookupType("i").getKind());
		assertEquals("should be the same",Kind.LONG,table.lookupType("j").getKind());
		assertNull("should not exist",table.lookupType("ij"));
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
		
		// return c;
		tList.add(new TokenJb(TokenType.RETURN,"return"));
		tList.add(new TokenJb(TokenType.ID,"c"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFLeftToRight(sc);
		ASTGenerator astgen=new ASTGenerator(res);
		ASTJb ast=astgen.generateAST();
		
	}
	
	@Test(expected=WordNotInLanguageGrammarException.class)
	public void testM2CondProgram() {
		/*# return 5
		# prints nothing
		
		bool b;
		bool c;
		long l;
		
		b = true;
		c = false;
		
		l = 4;
		
		# dangling-else should be resolved as given by indentation
		
		if ( b )
		  if ( c || ! b )
		    print "bla";
		  else
		    l = 5;
		
		return l;
		*/
		List<Token> tList = new LinkedList<>();
		// #returns 5
		tList.add(new TokenJb(TokenType.COMMENT, "returns 5"));
		// #prints nothing		
		tList.add(new TokenJb(TokenType.COMMENT, "prints nothing"));
		
		// bool b;
		tList.add(new TokenJb(TokenType.BOOL_SYMBOL, "bool"));
		tList.add(new TokenJb(TokenType.ID, "b"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// bool c;
		tList.add(new TokenJb(TokenType.BOOL_SYMBOL, "bool"));
		tList.add(new TokenJb(TokenType.ID, "c"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));

		// long l;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "l"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// b = true;
		tList.add(new TokenJb(TokenType.ID, "b"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.TRUE, "true"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// c = false;
		tList.add(new TokenJb(TokenType.ID, "c"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.FALSE, "false"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// l = 4;
		tList.add(new TokenJb(TokenType.ID, "c"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new NumTokenJb(TokenType.NUM, "4"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// if ( b )
		tList.add(new TokenJb(IF,"if"));
		tList.add(new TokenJb(LEFT_PARAN,"("));
		tList.add(new TokenJb(ID,"b"));
		tList.add(new TokenJb(RIGHT_PARAN,")"));
		
			// if ( c || ! b )
			tList.add(new TokenJb(IF,"if"));
			tList.add(new TokenJb(LEFT_PARAN,"("));
			tList.add(new TokenJb(ID,"c"));
			tList.add(new TokenJb(OR,"||"));
			tList.add(new TokenJb(NOT,"!"));
			tList.add(new TokenJb(ID,"b"));
			tList.add(new TokenJb(RIGHT_PARAN,")"));
			
				// print "bla" -- may not compile! word not in language
				tList.add(new TokenJb(PRINT,"print"));
				tList.add(new TokenJb(STRING,"\"bla\""));
				tList.add(new TokenJb(SEMICOLON,";"));
				
			// else
			tList.add(new TokenJb(ELSE,"else"));
				// l = 5;
				tList.add(new TokenJb(ID	,"l"));
				tList.add(new TokenJb(ASSIGNOP,"5"));
				tList.add(new NumTokenJb(NUM,"5"));
			
				
		
		// return l;
		tList.add(new TokenJb(TokenType.RETURN,"return"));
		tList.add(new TokenJb(TokenType.ID,"l"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFLeftToRight(sc);
		ASTGenerator astgen=new ASTGenerator(res);
		ASTJb ast=astgen.generateAST();
		
	}


	@Test
	public void testM2CondProgramModified() {
		/*# return 5
		# prints nothing
		
		bool b;
		bool c;
		long l;
		string bla;
		
		b = true;
		c = false;
		
		l = 4;
		bla="bla"
		
		# dangling-else should be resolved as given by indentation
		
		if ( b )
		  if ( c || ! b )
		    print bla;
		  else
		    l = 5;
		
		return l;
		*/
		List<Token> tList = new LinkedList<>();
		// #returns 5
		tList.add(new TokenJb(TokenType.COMMENT, "returns 5"));
		// #prints nothing		
		tList.add(new TokenJb(TokenType.COMMENT, "prints nothing"));
		
		// bool b;
		tList.add(new TokenJb(TokenType.BOOL_SYMBOL, "bool"));
		tList.add(new TokenJb(TokenType.ID, "b"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// bool c;
		tList.add(new TokenJb(TokenType.BOOL_SYMBOL, "bool"));
		tList.add(new TokenJb(TokenType.ID, "c"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));

		// long l;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "l"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// string str;
		tList.add(new TokenJb(TokenType.STRING_SYMBOL, "string"));
		tList.add(new TokenJb(TokenType.ID, "str"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// b = true;
		tList.add(new TokenJb(TokenType.ID, "a"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.TRUE, "true"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// c = false;
		tList.add(new TokenJb(TokenType.ID, "b"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new NumTokenJb(TokenType.FALSE, "false"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// l = 4;
		tList.add(new TokenJb(TokenType.ID, "c"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new NumTokenJb(TokenType.NUM, "4"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// str=bla;
		tList.add(new TokenJb(TokenType.ID, "str"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.STRING, "bla"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		
		
		// if ( b )
		tList.add(new TokenJb(IF,"if"));
		tList.add(new TokenJb(LEFT_PARAN,"("));
		tList.add(new TokenJb(ID,"b"));
		tList.add(new TokenJb(RIGHT_PARAN,")"));
		
			// if ( c || ! b )
			tList.add(new TokenJb(IF,"if"));
			tList.add(new TokenJb(LEFT_PARAN,"("));
			tList.add(new TokenJb(ID,"c"));
			tList.add(new TokenJb(OR,"||"));
			tList.add(new TokenJb(NOT,"!"));
			tList.add(new TokenJb(ID,"b"));
			tList.add(new TokenJb(RIGHT_PARAN,")"));
			
				// print "bla" -- may not compile! word not in language
				tList.add(new TokenJb(PRINT,"print"));
				tList.add(new TokenJb(ID,"bla"));
				tList.add(new TokenJb(SEMICOLON,";"));
				
			// else
			tList.add(new TokenJb(ELSE,"else"));
				// l = 5;
				tList.add(new TokenJb(ID	,"l"));
				tList.add(new TokenJb(ASSIGNOP,"="));
				tList.add(new NumTokenJb(NUM,"5"));
				tList.add(new TokenJb(SEMICOLON,";"));
				
				
		
		// return l;
		tList.add(new TokenJb(TokenType.RETURN,"return"));
		tList.add(new TokenJb(TokenType.ID,"l"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFLeftToRight(sc);
		ASTGenerator astgen=new ASTGenerator(res);
		ASTJb ast=astgen.generateAST();
		
	}

	@Test(expected=WordNotInLanguageGrammarException.class)
	public void testM2Print() {
		/*# return 0
		# prints:
		# true
		# 18121313223
		# -2.323e-99
		# jagARrEttString"
		
		long l;
		double d;
		string s;
		bool b;
		
		b = true;
		l = 18121313223;
		d = -23.23e-100;
		s = "jagAErEttString\"\n";  # c-like escaping in strings
		
		print b; print "\n";
		print l; print "\n";       # print one digit left of the radix point
		print d; print "\n";
		print s;
		
		return;                    # equivalent to return EXIT_SUCCESS
		*/
		List<Token> tList = new LinkedList<>();
		// ## return 0
		tList.add(new TokenJb(TokenType.COMMENT, "return 0"));
		// #prints something
		tList.add(new TokenJb(TokenType.COMMENT, "prints :"));
		tList.add(new TokenJb(TokenType.COMMENT, "true"));
		tList.add(new TokenJb(TokenType.COMMENT, "18121313223"));
		tList.add(new TokenJb(TokenType.COMMENT, "2.323e-99"));
		tList.add(new TokenJb(TokenType.COMMENT, "jagAErEttString\""));
		
		// long l;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "l"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// double d;
		tList.add(new TokenJb(TokenType.DOUBLE_SYMBOL, "double"));
		tList.add(new TokenJb(TokenType.ID, "d"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));

		// string s;
		tList.add(new TokenJb(TokenType.STRING_SYMBOL, "string"));
		tList.add(new TokenJb(TokenType.ID, "s"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// bool b;
		tList.add(new TokenJb(TokenType.BOOL_SYMBOL, "bool"));
		tList.add(new TokenJb(TokenType.ID, "b"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// b = true;
		tList.add(new TokenJb(TokenType.ID, "a"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.TRUE, "true"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// l = 18121313223;
		tList.add(new TokenJb(TokenType.ID, "l"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new NumTokenJb(TokenType.NUM, "18121313223"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// d = -23.23e-100;
		tList.add(new TokenJb(TokenType.ID, "d"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new RealTokenJb(TokenType.REAL, "-23.23e-100"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// s = "jagAErEttString\"\n";
		tList.add(new TokenJb(TokenType.ID, "s"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.STRING, "jagAErEttString\"\n"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// print b; print "\n";
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(ID,"b"));
		tList.add(new TokenJb(SEMICOLON,";"));
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(STRING,"\"\n\""));
		tList.add(new TokenJb(SEMICOLON,";"));
		// print l; print "\n";
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(ID,"l"));
		tList.add(new TokenJb(SEMICOLON,";"));
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(STRING,"\"\n\""));
		tList.add(new TokenJb(SEMICOLON,";"));
		// print d; print "\n";
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(ID,"b"));
		tList.add(new TokenJb(SEMICOLON,";"));
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(STRING,"\"\n\""));
		tList.add(new TokenJb(SEMICOLON,";"));
		// print b; print "\n";
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(ID,"d"));
		tList.add(new TokenJb(SEMICOLON,";"));
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(STRING,"\"\n\""));
		tList.add(new TokenJb(SEMICOLON,";"));
		// print s;
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(ID,"d"));
		tList.add(new TokenJb(SEMICOLON,";"));
		
		// return ;
		tList.add(new TokenJb(TokenType.RETURN,"return"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFLeftToRight(sc);
		ASTGenerator astgen=new ASTGenerator(res);
		ASTJb ast=astgen.generateAST();
		
	}
	
	@Test
	public void testM2PrintModified() {
		/*# return 0
		# prints:
		# true
		# 18121313223
		# -2.323e-99
		# jagARrEttString"
		
		string linebreak;
		long l;
		double d;
		string s;
		bool b;
		
		linebreak="\n"
		b = true;
		l = 18121313223;
		d = -23.23e-100;
		s = "jagAErEttString\"\n";  # c-like escaping in strings
		
		print b; print linebreak;
		print l; print linebreak;       # print one digit left of the radix point
		print d; print linebreak;
		print s;
		
		return;                    # equivalent to return EXIT_SUCCESS
		*/
		List<Token> tList = new LinkedList<>();
		// ## return 0
		tList.add(new TokenJb(TokenType.COMMENT, "return 0"));
		// #prints something
		tList.add(new TokenJb(TokenType.COMMENT, "prints :"));
		tList.add(new TokenJb(TokenType.COMMENT, "true"));
		tList.add(new TokenJb(TokenType.COMMENT, "18121313223"));
		tList.add(new TokenJb(TokenType.COMMENT, "2.323e-99"));
		tList.add(new TokenJb(TokenType.COMMENT, "jagAErEttString\""));
		
		// long l;
		tList.add(new TokenJb(TokenType.STRING_SYMBOL, "string"));
		tList.add(new TokenJb(TokenType.ID, "linebreak"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
				
		
		// long l;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "l"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// double d;
		tList.add(new TokenJb(TokenType.DOUBLE_SYMBOL, "double"));
		tList.add(new TokenJb(TokenType.ID, "d"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));

		// string s;
		tList.add(new TokenJb(TokenType.STRING_SYMBOL, "string"));
		tList.add(new TokenJb(TokenType.ID, "s"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// bool b;
		tList.add(new TokenJb(TokenType.BOOL_SYMBOL, "bool"));
		tList.add(new TokenJb(TokenType.ID, "b"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// linebreak = "\n";
		tList.add(new TokenJb(TokenType.ID, "linebreak"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.TRUE, "\"\n\""));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
				
				
		// b = true;
		tList.add(new TokenJb(TokenType.ID, "a"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.TRUE, "true"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// l = 18121313223;
		tList.add(new TokenJb(TokenType.ID, "l"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new NumTokenJb(TokenType.NUM, "18121313223"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// d = -23.23e-100;
		tList.add(new TokenJb(TokenType.ID, "d"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new RealTokenJb(TokenType.REAL, "-23.23e-100"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// s = "jagAErEttString\"\n";
		tList.add(new TokenJb(TokenType.ID, "s"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.STRING, "jagAErEttString\"\n"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// print b; print "\n";
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(ID,"b"));
		tList.add(new TokenJb(SEMICOLON,";"));
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(ID,"linebreak"));
		tList.add(new TokenJb(SEMICOLON,";"));
		// print l; print "\n";
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(ID,"l"));
		tList.add(new TokenJb(SEMICOLON,";"));
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(ID,"linebreak"));
		tList.add(new TokenJb(SEMICOLON,";"));
		// print d; print "\n";
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(ID,"b"));
		tList.add(new TokenJb(SEMICOLON,";"));
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(ID,"linebreak"));
		tList.add(new TokenJb(SEMICOLON,";"));
		// print b; print "\n";
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(ID,"d"));
		tList.add(new TokenJb(SEMICOLON,";"));
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(ID,"linebreak"));
		tList.add(new TokenJb(SEMICOLON,";"));
		// print s;
		tList.add(new TokenJb(PRINT,"print"));
		tList.add(new TokenJb(ID,"d"));
		tList.add(new TokenJb(SEMICOLON,";"));
		
		// return ;
		tList.add(new TokenJb(TokenType.RETURN,"return"));
		tList.add(new TokenJb(TokenType.SEMICOLON,";"));
		
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res = syn.derivateDFLeftToRight(sc);
		ASTGenerator astgen=new ASTGenerator(res);
		ASTJb ast=astgen.generateAST();
		
	}

	
	
	private String toReadableString(TargetGrammar.Reduction reduction){
		StringBuilder strb=new StringBuilder();
		strb.append(reduction.getLeftSide());
		strb.append("-> {");
		boolean first=true;
		for (Object o : reduction.getRightSide()){
			if (!first) strb.append(", ");
			else first=false;
			if (o instanceof Token){
				Token tok=(Token)o;
				strb.append(tok.getTokenType());
				strb.append("[");
				strb.append(tok.getValue());
				strb.append("]");
			}
			else strb.append(o);
		}
		strb.append(" }");
		return strb.toString();
	}

	
	private String resAsReadableString(List<Reduction> res){
		StringBuilder strb=new StringBuilder();
		boolean first=true;
		for (Reduction prod : res) {
			if (!first) strb.append(", ");
			else first=false;
			strb.append(toReadableString(prod));
		}
		return strb.toString();
	}
}
