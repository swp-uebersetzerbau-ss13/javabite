package swp_compiler_ss13.javabite.codegen;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode.BinaryOperator;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.ir.IntermediateCodeGenerator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.javabite.ast.ASTJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.ArithmeticBinaryExpressionNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.AssignmentNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.leaf.BasicIdentifierNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.leaf.LiteralNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.marynary.BlockNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.DeclarationNodeJb;
import swp_compiler_ss13.javabite.codegen.tests.STTable;


/**
 * 
 * @author Alpin Sahin und Florian Mercks
 *
 */
public class CodeGenTest {

	private static ASTJb ast;

	/**
	 * for testing this command 
	 * long i;
	 * i = ((20+(8/4))-14);
	 * @throws Exception
	 */
	@BeforeClass
	public static void testCodeGeneration() throws Exception {
		//new AST
		ast = new ASTJb();
		//first BlockNode is the whole program
		BlockNode program = new BlockNodeJb();
		program.setSymbolTable(new STTable());
		ast.setRootNode(program);
		//declare i
		DeclarationNode i = new DeclarationNodeJb();
		i.setIdentifier("i");
		i.setType(new LongType());
		i.setParentNode(program);
		program.addDeclaration(i);
		
		//insert i into the SymbolTable
		program.getSymbolTable().insert("i", new LongType());
		
		//literal 20
		LiteralNode literal20 = new LiteralNodeJb();
		literal20.setLiteral("20");
		literal20.setLiteralType(new LongType());
		//literal 8
		LiteralNode literal8 = new LiteralNodeJb();
		literal8.setLiteral("8");
		literal8.setLiteralType(new LongType());
		//literal 4
		LiteralNode literal4 = new LiteralNodeJb();
		literal4.setLiteral("4");
		literal4.setLiteralType(new LongType());
		//literal 14
		LiteralNode literal14 = new LiteralNodeJb();
		literal14.setLiteral("14");
		literal14.setLiteralType(new LongType());
		//divide 8 by 4
		ArithmeticBinaryExpressionNode aben1 = new ArithmeticBinaryExpressionNodeJb();
		aben1.setLeftValue(literal8);
		aben1.setRightValue(literal4);
		aben1.setOperator(BinaryOperator.DIVISION);
		literal8.setParentNode(aben1);
		literal4.setParentNode(aben1);
		//add 20 to (8/4)
		ArithmeticBinaryExpressionNode aben2 = new ArithmeticBinaryExpressionNodeJb();
		aben2.setLeftValue(literal20);
		aben2.setRightValue(aben1);
		aben2.setOperator(BinaryOperator.ADDITION);
		literal20.setParentNode(aben2);
		aben2.setParentNode(aben2);
		
		//subtract 14 from (20 + (8/4))
		ArithmeticBinaryExpressionNode aben3 = new ArithmeticBinaryExpressionNodeJb();
		aben3.setLeftValue(aben2);
		aben3.setRightValue(literal14);
		aben3.setOperator(BinaryOperator.SUBSTRACTION);
		aben2.setParentNode(aben3);
		literal14.setParentNode(aben3);
		//call i
		BasicIdentifierNode bin = new BasicIdentifierNodeJb();
		bin.setIdentifier("i");
		//assign i to (20+(8/4))-14
		AssignmentNode an = new AssignmentNodeJb();
		an.setLeftValue(bin);
		an.setRightValue(aben3);
		aben3.setParentNode(an);
		bin.setParentNode(an);
		//add statement Assignment to program
		program.addStatement(an);
		an.setParentNode(program);
	}
	
	/**
	 * JUnit-Test for the method testCodeGneration() 
	 * @throws IntermediateCodeGeneratorException
	 */
	@Test
	public void test() throws IntermediateCodeGeneratorException {
		IntermediateCodeGenerator gen = new JavaBiteCodeGenerator();
		List<Quadruple> tac = gen.generateIntermediateCode(ast);
		for (Quadruple q : tac) {
			System.out.println(String.format("(%s|%s|%s|%s)", q.getOperator(), q.getArgument1(), q.getArgument2(),
			q.getResult()));
		}
	}
	
}
