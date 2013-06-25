package swp_compiler_ss13.javabite.ast.nodes.binary;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.binary.LoopNode;
import swp_compiler_ss13.javabite.ast.nodes.StatementNodeJb;

public abstract class LoopNodeJb extends StatementNodeJb implements LoopNode {
	public LoopNodeJb(ASTNodeType myType) {
		super(myType);
	}

	protected ExpressionNode condition;
	protected StatementNode loopBody;

	public ExpressionNode getCondition() {
		return condition;
	}

	/**
	 * Is going to return StatementNode in the future
	 */
	public StatementNode getLoopBody() {
		return loopBody;
	}

	public void setCondition(ExpressionNode condition) {
		this.condition = condition;
	}

	public void setLoopBody(StatementNode loopBody) {
		this.loopBody = loopBody;
	}

}
