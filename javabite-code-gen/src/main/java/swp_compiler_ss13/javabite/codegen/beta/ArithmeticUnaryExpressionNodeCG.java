package swp_compiler_ss13.javabite.codegen.beta;

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

		IRCodeGenerator.differentiateNode((ASTNodeJb) rightNode);

		String rightResult = IRCodeGenerator.intermediateResults.pop();
		Type rightType = IRCodeGenerator.intermediateTypes.peek();

		String temp = IRCodeGenerator.createAndSaveTemporaryIdentifier((DoubleType) rightType);
		IRCodeGenerator.irCode.add(QuadrupleFactory.unaryMinus(rightType, rightResult, temp));
		IRCodeGenerator.intermediateResults.push(temp);
}
}
