package swp_compiler_ss13.javabite.ast.nodes.unary;

import java.util.LinkedList;
import java.util.List;

import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.ReturnNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.ast.nodes.StatementNodeJb;

public class ReturnNodeJb extends StatementNodeJb implements ReturnNode {
	public ReturnNodeJb() {
		super(ASTNodeType.ReturnNode);
	}

	protected IdentifierNode rightValue;

	public IdentifierNode getRightValue() {
		return rightValue;
	}

	public void setRightValue(IdentifierNode rightValue) {
		this.rightValue = rightValue;
		if (rightValue != null)
			addChild(rightValue, 0);
	}

	@Override
	public List<Token> coverage() {
		List<Token> res = new LinkedList<>();
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.RETURN));
		if (rightValue != null)
			res.addAll(rightValue.coverage());
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.SEMICOLON));
		return res;
	}
}
