package swp_compiler_ss13.javabite.ast.nodes.binary;

import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;

public class ArithmeticBinaryExpressionNodeJb extends BinaryExpressionNodeJb implements
		ArithmeticBinaryExpressionNode {

	public ArithmeticBinaryExpressionNodeJb() {
		super(ASTNodeType.ArithmeticBinaryExpressionNode);
	}
}
