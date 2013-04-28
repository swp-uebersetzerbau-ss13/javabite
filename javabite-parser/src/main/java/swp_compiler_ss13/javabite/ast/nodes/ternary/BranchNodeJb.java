package swp_compiler_ss13.javabite.ast.nodes.ternary;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.ternary.BranchNode;
import swp_compiler_ss13.javabite.ast.nodes.StatementNodeJb;

public class BranchNodeJb extends StatementNodeJb implements BranchNode {

	protected ExpressionNode condition;
	protected BlockNode BlockNodeOnTrue;
	protected BlockNode BlockNodeOnFalse;

	public ExpressionNode getCondition() {
		return condition;
	}

	public void setCondition(ExpressionNode condition) {
		this.condition = condition;
	}

	public BlockNode getBlockNodeOnTrue() {
		return BlockNodeOnTrue;
	}

	public void setBlockNodeOnTrue(BlockNode blockNodeOnTrue) {
		BlockNodeOnTrue = blockNodeOnTrue;
	}

	public BlockNode getBlockNodeOnFalse() {
		return BlockNodeOnFalse;
	}

	public void setBlockNodeOnFalse(BlockNode blockNodeOnFalse) {
		BlockNodeOnFalse = blockNodeOnFalse;
	}

}
