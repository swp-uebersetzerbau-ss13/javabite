package swp_compiler_ss13.javabite.ast.nodes;

import swp_compiler_ss13.common.ast.nodes.IdentifierNode;

public abstract class IdentifierNodeJb extends ExpressionNodeJb implements
		IdentifierNode {

	public IdentifierNodeJb(ASTNodeType myType) {
		super(myType);
	}

}
