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
		
		// if the operator is not MINUS, then here is not the right place
		if (node.getOperator() != UnaryOperator.MINUS) {
			throw new IntermediateCodeGeneratorException("This arithmetic unary operator is not supported!");
		}
		
		ExpressionNode rightValue = node.getRightValue();

		JavaBiteCodeGenerator.differentiateNode((ASTNodeJb) rightValue);

		String rightResult = JavaBiteCodeGenerator.temporaryResultOutputs.pop();
		Type rightType = JavaBiteCodeGenerator.temporaryTypes.peek();

		String temp = JavaBiteCodeGenerator.createAndAddTemporaryIdentifier((DoubleType) rightType);
		JavaBiteCodeGenerator.quadruples.add(QuadrupleFactory.unaryMinus(rightType, rightResult, temp));
		JavaBiteCodeGenerator.temporaryResultOutputs.push(temp);
	}
}
