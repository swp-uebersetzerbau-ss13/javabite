package swp_compiler_ss13.javabite.ast.nodes.binary;

import java.util.LinkedList;
import java.util.List;

import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.lexer.Token;

public class ArithmeticBinaryExpressionNodeJb extends BinaryExpressionNodeJb implements
		ArithmeticBinaryExpressionNode {

	public ArithmeticBinaryExpressionNodeJb() {
		super(ASTNodeType.ArithmeticBinaryExpressionNode);
	}

}
