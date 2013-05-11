package swp_compiler_ss13.javabite.codegen;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArithmeticUnaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.unary.UnaryExpressionNode.UnaryOperator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.common.types.primitive.DoubleType;
import swp_compiler_ss13.javabite.ast.ASTNodeJb;

public class ArithmeticUnaryExpressionNodeCG {

	public void convert(ArithmeticUnaryExpressionNode node)
			throws IntermediateCodeGeneratorException {
		if (node.getOperator() != UnaryOperator.MINUS) {
			throw new IntermediateCodeGeneratorException("Unsupported arithmetic unary operator");
		}
		
		ExpressionNode rightNode = node.getRightValue();

		JavaBiteCodeGenerator.differentiateNode((ASTNodeJb) rightNode);

		String rightResult = JavaBiteCodeGenerator.intermediateResults.pop();
		Type rightType = JavaBiteCodeGenerator.intermediateTypes.peek();

		String temp = JavaBiteCodeGenerator.createAndSaveTemporaryIdentifier((DoubleType) rightType);
		JavaBiteCodeGenerator.quadruples.add(QuadrupleFactory.unaryMinus(rightType, rightResult, temp));
		JavaBiteCodeGenerator.intermediateResults.push(temp);
}
}
