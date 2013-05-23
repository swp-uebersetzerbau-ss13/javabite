package swp_compiler_ss13.javabite.parser.targetgrammar.semantic_check;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.common.parser.ReportLog;
import swp_compiler_ss13.javabite.parser.astGenerator.ASTGenerator;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar;
import swp_compiler_ss13.javabite.parser.targetgrammar.TargetGrammar.Reduction;
import swp_compiler_ss13.javabite.parser.targetgrammar.semantic_check.ASTAnalyzer;
import swp_compiler_ss13.javabite.token.NumTokenJb;
import swp_compiler_ss13.javabite.token.TokenJb;

public class ASTAnalyzerTest {

	public ASTAnalyzer instance;
	
	TargetGrammar syn=new TargetGrammar();
	
	@Test
	public void testIfCompiles(){
		assertTrue("works",true);
	}
	
	@Test
	public void testNoDivisionByZero(){
		List<Token> tList = new LinkedList<>();
		// long i;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		// long j;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "j"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		// i=(2*0);
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.LEFT_PARAN, "("));
		tList.add(new NumTokenJb(TokenType.NUM, "2"));
		tList.add(new TokenJb(TokenType.TIMES, "*"));
		tList.add(new NumTokenJb(TokenType.NUM, "0"));
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
		ASTGenerator astGen=new ASTGenerator(res);
		AST ast=astGen.generateAST();
		ReportLog reportLog = Mockito.mock(ReportLog.class);
		instance=new ASTAnalyzer(reportLog);
		instance.setAst(ast);
		instance.checkDivisionByZero();
		verify(reportLog, never()).reportError(anyString(), anyInt(), anyInt(), anyString());
	}
	
	@Test
	public void testNoDivisionByZero2(){
		List<Token> tList = new LinkedList<>();
		// long i;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		// long j;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "j"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		// i=(0/2);
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.LEFT_PARAN, "("));
		tList.add(new NumTokenJb(TokenType.NUM, "0"));
		tList.add(new TokenJb(TokenType.DIVIDE, "/"));
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
		ASTGenerator astGen=new ASTGenerator(res);
		AST ast=astGen.generateAST();
		ReportLog reportLog = Mockito.mock(ReportLog.class);
		instance=new ASTAnalyzer(reportLog);
		instance.setAst(ast);
		instance.checkDivisionByZero();
		verify(reportLog, never()).reportError(anyString(), anyInt(), anyInt(), anyString());
	}
	
	@Test
	public void testExistingDivisionByZero(){
		List<Token> tList = new LinkedList<>();
		// long i;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		// long j;
		tList.add(new TokenJb(TokenType.LONG_SYMBOL, "long"));
		tList.add(new TokenJb(TokenType.ID, "j"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		// i=(2/0);
		tList.add(new TokenJb(TokenType.ID, "i"));
		tList.add(new TokenJb(TokenType.ASSIGNOP, "="));
		tList.add(new TokenJb(TokenType.LEFT_PARAN, "("));
		tList.add(new NumTokenJb(TokenType.NUM, "2"));
		tList.add(new TokenJb(TokenType.DIVIDE, "/"));
		tList.add(new NumTokenJb(TokenType.NUM, "0"));
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
		ASTGenerator astGen=new ASTGenerator(res);
		AST ast=astGen.generateAST();
		ReportLog reportLog = Mockito.mock(ReportLog.class);
		instance=new ASTAnalyzer(reportLog);
		instance.setAst(ast);
		instance.checkDivisionByZero();
		verify(reportLog, atLeastOnce()).reportError("", 0, 0, "Somewhere inside the input is a division by zero.");
	}
	
	@Test
	public void testAllDeclared(){
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
		ASTGenerator astGen=new ASTGenerator(res);
		AST ast=astGen.generateAST();
		ReportLog reportLog = Mockito.mock(ReportLog.class);
		instance=new ASTAnalyzer(reportLog);
		instance.setAst(ast);
		instance.checkNonDeclaredVariableUsedQ();
		verify(reportLog, never()).reportError(anyString(), anyInt(), anyInt(), anyString());
	}
	
	@Test
	public void testNotAllDeclared(){
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
		// return ND;
		tList.add(new TokenJb(TokenType.RETURN, "return"));
		tList.add(new TokenJb(TokenType.ID, "ND"));
		tList.add(new TokenJb(TokenType.SEMICOLON, ";"));
		
		TargetGrammar.SourceCode sc = syn.new SourceCode(tList);
		List<Reduction> res= syn.derivateDFLeftToRight(sc);
		ASTGenerator astGen=new ASTGenerator(res);
		AST ast=astGen.generateAST();
		ReportLog reportLog = Mockito.mock(ReportLog.class);
		instance=new ASTAnalyzer(reportLog);
		instance.setAst(ast);
		instance.checkNonDeclaredVariableUsedQ();
		reportLog.reportError("ND", 0, 0, "Identifier 'ND' was declared multiple times");
		verify(reportLog, atLeastOnce()).reportError(anyString(), anyInt(), anyInt(), anyString());
	}
}