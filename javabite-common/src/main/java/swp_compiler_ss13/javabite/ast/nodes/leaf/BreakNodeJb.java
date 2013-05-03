package swp_compiler_ss13.javabite.ast.nodes.leaf;

import swp_compiler_ss13.common.ast.nodes.leaf.BreakNode;
import swp_compiler_ss13.javabite.ast.nodes.ExpressionNodeJb;

public class BreakNodeJb extends ExpressionNodeJb implements BreakNode {

	public BreakNodeJb() {
		super(ASTNodeType.BreakNode);
	}


}
