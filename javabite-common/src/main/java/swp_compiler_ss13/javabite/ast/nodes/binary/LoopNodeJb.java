package swp_compiler_ss13.javabite.ast.nodes.binary;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.LoopNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.javabite.ast.nodes.StatementNodeJb;

public abstract class LoopNodeJb extends StatementNodeJb implements LoopNode {
	public LoopNodeJb(ASTNodeType myType) {
		super(myType);
	}
	protected ExpressionNode condition;
	protected BlockNode loopBody;
	
	public ExpressionNode getCondition() {
		return condition;
	}
	public BlockNode getLoopBody() {
		return loopBody;
	}
	
	public void setCondition(ExpressionNode condition) {
		this.condition = condition;
	}
	public void setLoopBody(BlockNode loopBody) {
		this.loopBody = loopBody;
	}
	
	

}
