package swp_compiler_ss13.javabite.ast.nodes.unary;

import swp_compiler_ss13.common.ast.nodes.unary.ArithmeticUnaryExpressionNode;

public class ArithmeticUnaryExpressionNodeJb extends UnaryExpressionNodeJb implements
		ArithmeticUnaryExpressionNode {

	public ArithmeticUnaryExpressionNodeJb() {
		super(ASTNodeType.ArithmeticUnaryExpressionNode);
	}

}
