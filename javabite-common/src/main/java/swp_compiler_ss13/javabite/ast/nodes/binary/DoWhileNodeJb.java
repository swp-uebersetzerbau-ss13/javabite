package swp_compiler_ss13.javabite.ast.nodes.binary;

import java.util.LinkedList;
import java.util.List;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.binary.DoWhileNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;

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
	public void setLoopBody(StatementNode loopBody) {
		super.setLoopBody(loopBody);
		addChild(loopBody, 0);
	}

	@Override
	public List<Token> coverage() {
		List<Token> res = new LinkedList<>();
		
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.DO));
		res.addAll(loopBody.coverage());
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.WHILE));
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.LEFT_PARAN));
		res.addAll(condition.coverage());
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.RIGHT_PARAN));
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.SEMICOLON));

		return res;
	}
}
