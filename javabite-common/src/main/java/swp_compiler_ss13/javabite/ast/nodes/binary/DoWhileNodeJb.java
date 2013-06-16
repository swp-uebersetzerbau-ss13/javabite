package swp_compiler_ss13.javabite.ast.nodes.binary;

import java.util.List;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.DoWhileNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.lexer.Token;

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

	@Override
	public List<Token> coverage() {
		//TODO implement
		throw new RuntimeException("not requested for MS2");
	}
}
