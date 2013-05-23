package swp_compiler_ss13.javabite.codegen.alternative;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode.BinaryOperator;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ast.nodes.unary.ReturnNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.ir.IntermediateCodeGenerator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.parser.SymbolTable;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.javabite.ast.ASTJb;
import swp_compiler_ss13.javabite.ast.SymbolTableJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.ArithmeticBinaryExpressionNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.AssignmentNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.leaf.BasicIdentifierNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.leaf.LiteralNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.marynary.BlockNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.DeclarationNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.ReturnNodeJb;
import swp_compiler_ss13.javabite.types.TypeJb;

/**
 *  this are no tests, its only for example executio
 */
public class CodeGenTest {

	private static AST ast1;
	private static AST ast2;
	private static AST ast3;

	@BeforeClass
	public static void testCodeGeneration() throws Exception {
		setupAst1();
		setupAst2();
		setupAst3();
	}
	
	/**
	 * JUnit-Test for the method testCodeGneration()
	 * 
	 * @throws IntermediateCodeGeneratorException
	 */
	@Test
	public void ast1() throws IntermediateCodeGeneratorException {
		IntermediateCodeGenerator gen = new IntermediateCodeGeneratorJb();
		List<Quadruple> tac = gen.generateIntermediateCode(ast1);
		System.out.println("=====AST1====");
		System.out.println("long i;\ni = ((20+(8/4))-14);");
		for (Quadruple q : tac) {
			System.out.println(String.format("(%s|%s|%s|%s)", q.getOperator(),
					q.getArgument1(), q.getArgument2(), q.getResult()));
		}
	}

	/**
	 * JUnit-Test for the method testCodeGneration()
	 * 
	 * @throws IntermediateCodeGeneratorException
	 */
	@Test
	public void ast2() throws IntermediateCodeGeneratorException {
		IntermediateCodeGenerator gen = new IntermediateCodeGeneratorJb();
		List<Quadruple> tac = gen.generateIntermediateCode(ast2);
		System.out.println("=====AST2====");
		System.out.println("long i;\ni=2;\nreturn i");
		for (Quadruple q : tac) {
			System.out.println(String.format("(%s|%s|%s|%s)", q.getOperator(),
					q.getArgument1(), q.getArgument2(), q.getResult()));
		}
	}

	/**
	 * JUnit-Test for the method testCodeGneration()
	 * 
	 * @throws IntermediateCodeGeneratorException
	 */
	@Test
	public void ast3() throws IntermediateCodeGeneratorException {
		IntermediateCodeGenerator gen = new IntermediateCodeGeneratorJb();
		List<Quadruple> tac = gen.generateIntermediateCode(ast3);
		System.out.println("=====AST3====");
		System.out.println("long i;\nlong j;\ni=2;\nj=2;\ni=i*j;\nreturn i;");
		for (Quadruple q : tac) {
			System.out.println(String.format("(%s|%s|%s|%s)", q.getOperator(),
					q.getArgument1(), q.getArgument2(), q.getResult()));
		}
	}

	static void setupAst1() {
		// new AST
		ast1 = new ASTJb();
		// first BlockNode is the whole program
		BlockNode program = new BlockNodeJb();
		program.setSymbolTable(new SymbolTableJb());
		ast1.setRootNode(program);
		// declare i
		DeclarationNode i = new DeclarationNodeJb();
		i.setIdentifier("i");
		i.setType(new LongType());
		i.setParentNode(program);
		program.addDeclaration(i);

		// insert i into the SymbolTable
		program.getSymbolTable().insert("i", new LongType());

		// literal 20
		LiteralNode literal20 = new LiteralNodeJb();
		literal20.setLiteral("20");
		literal20.setLiteralType(new LongType());
		// literal 8
		LiteralNode literal8 = new LiteralNodeJb();
		literal8.setLiteral("8");
		literal8.setLiteralType(new LongType());
		// literal 4
		LiteralNode literal4 = new LiteralNodeJb();
		literal4.setLiteral("4");
		literal4.setLiteralType(new LongType());
		// literal 14
		LiteralNode literal14 = new LiteralNodeJb();
		literal14.setLiteral("14");
		literal14.setLiteralType(new LongType());
		// divide 8 by 4
		ArithmeticBinaryExpressionNode aben1 = new ArithmeticBinaryExpressionNodeJb();
		aben1.setLeftValue(literal8);
		aben1.setRightValue(literal4);
		aben1.setOperator(BinaryOperator.DIVISION);
		literal8.setParentNode(aben1);
		literal4.setParentNode(aben1);
		// add 20 to (8/4)
		ArithmeticBinaryExpressionNode aben2 = new ArithmeticBinaryExpressionNodeJb();
		aben2.setLeftValue(literal20);
		aben2.setRightValue(aben1);
		aben2.setOperator(BinaryOperator.ADDITION);
		literal20.setParentNode(aben2);
		aben2.setParentNode(aben2);

		// subtract 14 from (20 + (8/4))
		ArithmeticBinaryExpressionNode aben3 = new ArithmeticBinaryExpressionNodeJb();
		aben3.setLeftValue(aben2);
		aben3.setRightValue(literal14);
		aben3.setOperator(BinaryOperator.SUBSTRACTION);
		aben2.setParentNode(aben3);
		literal14.setParentNode(aben3);
		// call i
		BasicIdentifierNode bin = new BasicIdentifierNodeJb();
		bin.setIdentifier("i");
		// assign i to (20+(8/4))-14
		AssignmentNode an = new AssignmentNodeJb();
		an.setLeftValue(bin);
		an.setRightValue(aben3);
		aben3.setParentNode(an);
		bin.setParentNode(an);
		// add statement Assignment to program
		program.addStatement(an);
		an.setParentNode(program);
	}

	static void setupAst2() {
		SymbolTable sTable = new SymbolTableJb();
		sTable.insert("i", new TypeJb(Type.Kind.LONG));
		BasicIdentifierNode iidNode = new BasicIdentifierNodeJb();
		iidNode.setIdentifier("i");
		LiteralNode literalNode = new LiteralNodeJb();
		literalNode.setLiteral("2");
		literalNode.setLiteralType(new TypeJb(Type.Kind.LONG));
		AssignmentNode assignNode = new AssignmentNodeJb();
		assignNode.setLeftValue(iidNode);
		assignNode.setRightValue(literalNode);
		DeclarationNode declNode = new DeclarationNodeJb();
		declNode.setIdentifier("i");
		declNode.setType(new TypeJb(Type.Kind.LONG));
		BlockNode root = new BlockNodeJb();
		root.setSymbolTable(sTable);
		root.addDeclaration(declNode);
		root.addStatement(assignNode);
		ReturnNode retNode = new ReturnNodeJb();
		retNode.setRightValue(iidNode);
		root.addStatement(retNode);
		root.setSymbolTable(sTable);
		ast2 = new ASTJb();
		ast2.setRootNode(root);
	}

	static void setupAst3() {
		SymbolTable sTable = new SymbolTableJb();
		sTable.insert("i", new TypeJb(Type.Kind.LONG));
		sTable.insert("j", new TypeJb(Type.Kind.LONG));
		BasicIdentifierNode iidNodeI = new BasicIdentifierNodeJb();
		BasicIdentifierNode iidNodeJ = new BasicIdentifierNodeJb();
		iidNodeI.setIdentifier("i");
		iidNodeJ.setIdentifier("j");

		LiteralNode literalNodeI = new LiteralNodeJb();
		literalNodeI.setLiteral("2");
		literalNodeI.setLiteralType(new TypeJb(Type.Kind.LONG));
		LiteralNode literalNodeJ = new LiteralNodeJb();
		literalNodeJ.setLiteral("2");
		literalNodeJ.setLiteralType(new TypeJb(Type.Kind.LONG));

		AssignmentNode assignNodeI = new AssignmentNodeJb();
		assignNodeI.setLeftValue(iidNodeI);
		assignNodeI.setRightValue(literalNodeI);
		AssignmentNode assignNodeJ = new AssignmentNodeJb();
		assignNodeJ.setLeftValue(iidNodeJ);
		assignNodeJ.setRightValue(literalNodeJ);

		DeclarationNode declNodeI = new DeclarationNodeJb();
		declNodeI.setIdentifier("i");
		declNodeI.setType(new TypeJb(Type.Kind.LONG));

		DeclarationNode declNodeJ = new DeclarationNodeJb();
		declNodeJ.setIdentifier("j");
		declNodeJ.setType(new TypeJb(Type.Kind.LONG));

		BlockNode root = new BlockNodeJb();
		root.setSymbolTable(sTable);
		root.addDeclaration(declNodeI);
		root.addDeclaration(declNodeJ);

		root.addStatement(assignNodeI);
		root.addStatement(assignNodeJ);

		AssignmentNode mul_node = new AssignmentNodeJb();
		mul_node.setLeftValue(iidNodeI);
		BinaryExpressionNode expressionNode = new ArithmeticBinaryExpressionNodeJb();
		expressionNode.setLeftValue(iidNodeI);
		expressionNode.setRightValue(iidNodeJ);
		expressionNode.setOperator(BinaryOperator.MULTIPLICATION);
		mul_node.setRightValue(expressionNode);
		root.addStatement(mul_node);

		ReturnNode retNode = new ReturnNodeJb();
		retNode.setRightValue(iidNodeI);
		root.addStatement(retNode);
		root.setSymbolTable(sTable);
		ast3 = new ASTJb();
		ast3.setRootNode(root);
	}
}
