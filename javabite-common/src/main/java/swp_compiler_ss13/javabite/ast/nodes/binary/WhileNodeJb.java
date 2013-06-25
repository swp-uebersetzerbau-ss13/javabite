package swp_compiler_ss13.javabite.ast.nodes.binary;

import java.util.LinkedList;
import java.util.List;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.binary.WhileNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;

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
	public void setLoopBody(StatementNode loopBody) {
		super.setLoopBody(loopBody);
		addChild(loopBody, 1);
	}

	@Override
	public List<Token> coverage() {
		List<Token> res = new LinkedList<>();

		res.add(getAssociatedTokenListFromTypeUnique(TokenType.WHILE));
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.LEFT_PARAN));
		res.addAll(condition.coverage());
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.RIGHT_PARAN));
		res.addAll(loopBody.coverage());
		
		return res;
	}
}
