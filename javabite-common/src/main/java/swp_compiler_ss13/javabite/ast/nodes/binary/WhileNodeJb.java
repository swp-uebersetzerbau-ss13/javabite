package swp_compiler_ss13.javabite.ast.nodes.binary;

import java.util.List;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.WhileNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.lexer.Token;

public class WhileNodeJb extends LoopNodeJb implements WhileNode {

	public WhileNodeJb() {
		super(ASTNodeType.WhileNode);
	}
	@Override
	public void setCondition(ExpressionNode condition) {
		super.setCondition(condition);
		addChild(condition, 0);
	}
	
	@Override
	public void setLoopBody(BlockNode loopBody) {
		super.setLoopBody(loopBody);
		addChild(loopBody,1);
	}
	@Override
	public List<Token> coverage() {
		//TODO implement
		throw new RuntimeException("not requested for MS2");
	}
}
