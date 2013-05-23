package swp_compiler_ss13.javabite.ast.nodes.binary;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.DoWhileNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;

public class DoWhileNodeJb extends LoopNodeJb implements DoWhileNode {

	public DoWhileNodeJb() {
		super(ASTNodeType.DoWhileNode);
	}
	
	@Override
	public void setCondition(ExpressionNode condition) {
		super.setCondition(condition);
		addChild(condition, 1);
	}
	
	@Override
	public void setLoopBody(BlockNode loopBody) {
		super.setLoopBody(loopBody);
		addChild(loopBody,0);
	}
}
