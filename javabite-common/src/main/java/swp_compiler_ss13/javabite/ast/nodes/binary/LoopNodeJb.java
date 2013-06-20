package swp_compiler_ss13.javabite.ast.nodes.binary;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.binary.LoopNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.javabite.ast.nodes.StatementNodeJb;

public abstract class LoopNodeJb extends StatementNodeJb implements LoopNode {
	public LoopNodeJb(ASTNodeType myType) {
		super(myType);
	}
	
	protected ExpressionNode condition;
	protected StatementNode loopBody;
	
	@Deprecated
	protected BlockNode loopBody_depr;
	
	public ExpressionNode getCondition() {
		return condition;
	}
	
	@Deprecated
	/**
	 * Is going to return StatementNode in the future
	 */
	public BlockNode getLoopBody() {
		return loopBody_depr;
	}
	
	public void setCondition(ExpressionNode condition) {
		this.condition = condition;
	}
	
	public void setLoopBody_(StatementNodeJb body){
		this.loopBody=body;
	}
	
	public StatementNode getLoopBody_(){
		return loopBody;
	}
	
	@Deprecated
	/**
	 * Is going to receive a StatementNode in the future
	 */
	public void setLoopBody(BlockNode loopBody) {
		this.loopBody_depr = loopBody;
	}
	
	

}
