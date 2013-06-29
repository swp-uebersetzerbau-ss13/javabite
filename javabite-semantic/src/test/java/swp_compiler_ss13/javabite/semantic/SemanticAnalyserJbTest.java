package swp_compiler_ss13.javabite.semantic;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.report.ReportLog;
import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.common.types.primitive.BooleanType;
import swp_compiler_ss13.common.types.primitive.DoubleType;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.javabite.ast.ASTJb;
import swp_compiler_ss13.javabite.ast.SymbolTableJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.ArithmeticBinaryExpressionNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.AssignmentNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.LogicBinaryExpressionNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.leaf.BasicIdentifierNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.leaf.LiteralNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.marynary.BlockNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.DeclarationNodeJb;
import swp_compiler_ss13.javabite.semantic.old.SemanticAnalyserJb;


public class SemanticAnalyserJbTest {

	public SemanticAnalyserJb instance=new SemanticAnalyserJb();


	@Test
	public void testIfCompiles() {
		assertTrue("works", true);
	}
	
	/*
	@Test
	public void testNoDivisionByZero() {
		// TODO: create ast
		AST ast=null;
		ReportLog reportLog = Mockito.mock(ReportLog.class);
		return;
		
		instance.setAst(ast);
		instance.checkDivisionByZero();
		verify(reportLog, never()).reportError(any(ReportType.class),
				anyListOf(Token.class), anyString());
	}

	@Test
	public void testNoDivisionByZero2() {
		// TODO: create ast
		AST ast=null;
		ReportLog reportLog = Mockito.mock(ReportLog.class);
		return;
		
		instance.setAst(ast);
		instance.checkDivisionByZero();
		verify(reportLog, never()).reportError(any(ReportType.class),
				anyListOf(Token.class), anyString());
	}

	@Test
	public void testExistingDivisionByZero() {
		// TODO: create ast
		AST ast=null;
		ReportLog reportLog = Mockito.mock(ReportLog.class);
		return;
				
		instance.setAst(ast);
		instance.checkDivisionByZero();
		verify(reportLog, atLeastOnce()).reportError(
				ReportType.DIVISION_BY_ZERO, anyListOf(Token.class),
				"Somewhere inside the input is a division by zero.");
	}

	@Test
	public void testAllDeclared() {
		// TODO: create ast
		AST ast=null;
		ReportLog reportLog = Mockito.mock(ReportLog.class);
		return;
		
		
		instance.setAst(ast);
		instance.checkNonDeclaredVariableUsedQ();
		verify(reportLog, never()).reportError(any(ReportType.class),
				anyListOf(Token.class), anyString());
	}

	@Test
	public void testDoubleDeclared() {
		// TODO: create ast
		AST ast=null;
		ReportLog reportLog = Mockito.mock(ReportLog.class);
		return;
		
		instance.setAst(ast);
		instance.checkDoubleDeclaration();
		verify(reportLog, never())
				.reportError(ReportType.DOUBLE_DECLARATION,
						anyListOf(Token.class),
						"IDENTEFIER 'i' were multiple decleard");
	}

	@Test
	public void testNotAllDeclared() {
		// TODO: create ast
		AST ast=null;
		return;
		
		ReportLog reportLog = Mockito.mock(ReportLog.class);
		instance = new SemanticAnalyserJb();
		instance.setReportLog(reportLog);
		instance.setAst(ast);
		instance.checkNonDeclaredVariableUsedQ();
		verify(reportLog, atLeastOnce()).reportError(
				ReportType.UNDECLARED_VARIABLE_USAGE, anyListOf(Token.class),
				anyString());
	}
	*/
	
	
	@Test
	public void testCorrect(){
		instance.setReportLog(new ReportTest());
		
		BlockNode root=new BlockNodeJb();
		root.setSymbolTable(new SymbolTableJb());
		
		AST ast=new ASTJb();
		ast.setRootNode(root);
		
		
		DeclarationNode declBoolean1=new DeclarationNodeJb();
		declBoolean1.setIdentifier("declBoolean1");
		declBoolean1.setType(new BooleanType());
		
		BasicIdentifierNode identBoolean1=new BasicIdentifierNodeJb();
		identBoolean1.setIdentifier("declBoolean1");
		
		DeclarationNode declBoolean2=new DeclarationNodeJb();
		declBoolean2.setIdentifier("declBoolean2");
		declBoolean2.setType(new BooleanType());
		
		BasicIdentifierNode identBoolean2=new BasicIdentifierNodeJb();
		identBoolean2.setIdentifier("declBoolean2");
		
		DeclarationNode declDouble1=new DeclarationNodeJb();
		declDouble1.setIdentifier("declDouble1");
		declDouble1.setType(new DoubleType());
		
		BasicIdentifierNode identDouble1=new BasicIdentifierNodeJb();
		identDouble1.setIdentifier("declDouble1");
		
		DeclarationNode declDouble2=new DeclarationNodeJb();
		declDouble2.setIdentifier("declDouble2");
		declDouble2.setType(new DoubleType());
		
		BasicIdentifierNode identDouble2=new BasicIdentifierNodeJb();
		identDouble2.setIdentifier("declDouble2");
		
		
		LiteralNode one=new LiteralNodeJb();
		one.setLiteral("1.");
		one.setLiteralType(new DoubleType());
		LiteralNode two=new LiteralNodeJb();
		two.setLiteral("2");
		two.setLiteralType(new LongType());
		
		LiteralNode true_=new LiteralNodeJb();
		true_.setLiteral("true");
		true_.setLiteralType(new BooleanType());
		LiteralNode false_=new LiteralNodeJb();
		false_.setLiteral("false");
		false_.setLiteralType(new BooleanType());
		
		ArithmeticBinaryExpressionNode bin_arith=new ArithmeticBinaryExpressionNodeJb();
		
		LogicBinaryExpressionNodeJb bin_boole=new LogicBinaryExpressionNodeJb();
		
		root.addDeclaration(declDouble1);
		root.addDeclaration(declDouble2);
		root.addDeclaration(declBoolean1);
		root.addDeclaration(declBoolean2);
		
		// double declDouble1;
		// double declDouble2;
		// bool declBoolean 1;
		// bool declBoolean 2;
		// declDouble1=1;
		
		AssignmentNode an=new AssignmentNodeJb();
		an.setLeftValue(identDouble1);
		an.setRightValue(one);
		
		root.addStatement(an);
		
		ReportLog reportLog = Mockito.mock(ReportLog.class);
		instance = new SemanticAnalyserJb();
		instance.setReportLog(reportLog);
		instance.analyse(ast,false);
		
	
		
		verify(reportLog, never()).reportError(
				any(ReportType.class), anyListOf(Token.class),
				anyString());
	}
	
	@Test
	public void testAssignBoolToDouble(){
		instance.setReportLog(new ReportTest());
		
		
		BlockNode root=new BlockNodeJb();
		root.setSymbolTable(new SymbolTableJb());
		
		AST ast=new ASTJb();
		ast.setRootNode(root);
		
		
		DeclarationNodeJb declBoolean1=new DeclarationNodeJb();
		declBoolean1.setIdentifier("declBoolean1");
		declBoolean1.setType(new BooleanType());
		
		
		BasicIdentifierNode identBoolean1=new BasicIdentifierNodeJb();
		identBoolean1.setIdentifier("declBoolean1");
		
		DeclarationNode declBoolean2=new DeclarationNodeJb();
		declBoolean2.setIdentifier("declBoolean2");
		declBoolean2.setType(new BooleanType());
		
		BasicIdentifierNode identBoolean2=new BasicIdentifierNodeJb();
		identBoolean2.setIdentifier("declBoolean2");
		
		DeclarationNode declDouble1=new DeclarationNodeJb();
		declDouble1.setIdentifier("declDouble1");
		declDouble1.setType(new DoubleType());
		
		BasicIdentifierNode identDouble1=new BasicIdentifierNodeJb();
		identDouble1.setIdentifier("declDouble1");
		
		DeclarationNode declDouble2=new DeclarationNodeJb();
		declDouble2.setIdentifier("declDouble2");
		declDouble2.setType(new DoubleType());
		
		BasicIdentifierNode identDouble2=new BasicIdentifierNodeJb();
		identDouble2.setIdentifier("declDouble2");
		
		
		LiteralNode one=new LiteralNodeJb();
		one.setLiteral("1.");
		one.setLiteralType(new DoubleType());
		LiteralNode two=new LiteralNodeJb();
		two.setLiteral("2");
		two.setLiteralType(new LongType());
		LiteralNode zero=new LiteralNodeJb();
		two.setLiteral("0");
		two.setLiteralType(new LongType());
		
		LiteralNode true_=new LiteralNodeJb();
		true_.setLiteral("true");
		true_.setLiteralType(new BooleanType());
		LiteralNode false_=new LiteralNodeJb();
		false_.setLiteral("false");
		false_.setLiteralType(new BooleanType());
		
		ArithmeticBinaryExpressionNode bin_arith=new ArithmeticBinaryExpressionNodeJb();
		
		LogicBinaryExpressionNodeJb bin_boole=new LogicBinaryExpressionNodeJb();
		
		root.addDeclaration(declDouble1);
		root.addDeclaration(declDouble2);
		root.addDeclaration(declBoolean1);
		root.addDeclaration(declBoolean2);
		
		// double declDouble1;
		// double declDouble2;
		// bool declBoolean 1;
		// bool declBoolean 2;
		// declDouble1=true;
		
		AssignmentNode an=new AssignmentNodeJb();
		an.setLeftValue(identDouble1);
		an.setRightValue(false_);
		
		root.addStatement(an);
		
		
		ReportLog reportLog = Mockito.mock(ReportLog.class);
		instance = new SemanticAnalyserJb();
		instance.setReportLog(reportLog);
		instance.analyse(ast,false);
		
		verify(reportLog, only()).reportError(
				eq(ReportType.TYPE_MISMATCH), anyListOf(Token.class),
				anyString());
	}
	
	private class ReportTest implements ReportLog{

		@Override
		public void reportWarning(ReportType type, List<Token> tokens,
				String message) {
			System.out.println("warn: "+type+"\t"+tokens);
			System.out.println(message);
		}

		@Override
		public void reportError(ReportType type, List<Token> tokens,
				String message) {
			System.out.println("error: "+type+"\t"+tokens);
			System.out.println(message);
		}
		
	}
}
