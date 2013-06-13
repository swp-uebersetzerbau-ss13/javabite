package swp_compiler_ss13.javabite.parser.astGenerator;

import static org.junit.Assert.*;
import static swp_compiler_ss13.common.lexer.TokenType.ASSIGNOP;
import static swp_compiler_ss13.common.lexer.TokenType.ELSE;
import static swp_compiler_ss13.common.lexer.TokenType.ID;
import static swp_compiler_ss13.common.lexer.TokenType.IF;
import static swp_compiler_ss13.common.lexer.TokenType.LEFT_PARAN;
import static swp_compiler_ss13.common.lexer.TokenType.NOT;
import static swp_compiler_ss13.common.lexer.TokenType.NUM;
import static swp_compiler_ss13.common.lexer.TokenType.OR;
import static swp_compiler_ss13.common.lexer.TokenType.PRINT;
import static swp_compiler_ss13.common.lexer.TokenType.RIGHT_PARAN;
import static swp_compiler_ss13.common.lexer.TokenType.SEMICOLON;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.ast.ASTJb;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar.Reduction;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar.SourceCode;
import swp_compiler_ss13.javabite.token.NumTokenJb;
import swp_compiler_ss13.javabite.token.RealTokenJb;
import swp_compiler_ss13.javabite.token.TokenJb;

public class ASTGeneratorMS2Test{
	ASTGenerator instance;
	TargetGrammar grammar=new TargetGrammar();
	
	@Test
	public void testAssignmentProg(){
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
		
		TargetGrammar.SourceCode sc = grammar.new SourceCode(tList);
		List<Reduction> res = grammar.derivateDFLeftToRight(sc);
		ASTGenerator astgen=new ASTGenerator(res);
		ASTJb ast=astgen.generateAST();
		BlockNode bn=ast.getRootNode();
		//38 tokens
		assertEquals(38, bn.coverage().size());
		// return statement
		assertEquals(3, bn.getStatementList().get(bn.getStatementList().size()-1).coverage().size());
		// c= a + b + c;
		assertEquals(8, bn.getStatementList().get(bn.getStatementList().size()-2).coverage().size());
		
	}
	
	@Test
	public void testCondProg(){
		/*# return 5
		# prints nothing
		
		bool b;
		bool c;
		long l;
		
		string bla;
		bla="bla"
		
		b = true;
		c = false;
		
		l = 4;
		
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
		

		// str="bla";
		tList.add(new TokenJb(TokenType.ID, "str"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.STRING, "bla"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		
		// b = true;
		tList.add(new TokenJb(TokenType.ID, "b"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.TRUE, "true"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// c = false;
		tList.add(new TokenJb(TokenType.ID, "c"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new NumTokenJb(TokenType.FALSE, "false"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		// l = 4;
		tList.add(new TokenJb(TokenType.ID, "l"));
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
		
		TargetGrammar.SourceCode sc = grammar.new SourceCode(tList);
		List<Reduction> res = grammar.derivateDFLeftToRight(sc);
		ASTGenerator astgen=new ASTGenerator(res);
		ASTJb ast=astgen.generateAST();
		BlockNode root=ast.getRootNode();
		assertEquals(50, root.coverage().size());
		
	}
	
	
	@Test
	public void testPrintProg(){
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
		
		string linebreak;
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
		
		// string linebreak;
		tList.add(new TokenJb(TokenType.STRING_SYMBOL, "string"));
		tList.add(new TokenJb(TokenType.ID, "linebreak"));
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
		
		TargetGrammar.SourceCode sc = grammar.new SourceCode(tList);
		List<Reduction> res = grammar.derivateDFLeftToRight(sc);
		ASTGenerator astgen=new ASTGenerator(res);
		ASTJb ast=astgen.generateAST();
		
		BlockNode root=ast.getRootNode();
		assertEquals(64, root.coverage().size());
		
	}
}
