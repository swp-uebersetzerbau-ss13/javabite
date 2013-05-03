package swp_compiler_ss13.javabite.ast.nodes;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;


public abstract class ExpressionNodeJb extends StatementNodeJb implements ExpressionNode {

	public ExpressionNodeJb(ASTNodeType myType) {
		super(myType);
	}
	
}
