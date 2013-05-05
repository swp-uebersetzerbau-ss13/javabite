package swp_compiler_ss13.javabite.ast.nodes.ternary;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.ternary.BranchNode;
import swp_compiler_ss13.javabite.ast.nodes.StatementNodeJb;

public class BranchNodeJb extends StatementNodeJb implements BranchNode {

	public BranchNodeJb() {
		super(ASTNodeType.BranchNode);
	}

	protected ExpressionNode condition;
	protected BlockNode BlockNodeOnTrue;
	protected BlockNode BlockNodeOnFalse;

	public ExpressionNode getCondition() {
		return condition;
	}

	public void setCondition(ExpressionNode condition) {
		this.condition = condition;
		addChild(condition, 0);
	}

	public BlockNode getBlockNodeOnTrue() {
		return BlockNodeOnTrue;
	}

	public void setBlockNodeOnTrue(BlockNode blockNodeOnTrue) {
		BlockNodeOnTrue = blockNodeOnTrue;
		addChild(blockNodeOnTrue, 1);
	}

	public BlockNode getBlockNodeOnFalse() {
		return BlockNodeOnFalse;
	}

	public void setBlockNodeOnFalse(BlockNode blockNodeOnFalse) {
		BlockNodeOnFalse = blockNodeOnFalse;
		addChild(blockNodeOnFalse, 2);
	}

}
