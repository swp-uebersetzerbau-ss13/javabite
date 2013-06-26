package swp_compiler_ss13.javabite.ast.nodes;

import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.javabite.ast.ASTNodeJb;

public abstract class StatementNodeJb extends ASTNodeJb implements
		StatementNode {

	public StatementNodeJb(ASTNodeType myType) {
		super(myType);
	}

}
