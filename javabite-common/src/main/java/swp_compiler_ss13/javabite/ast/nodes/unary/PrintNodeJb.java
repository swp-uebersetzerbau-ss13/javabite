package swp_compiler_ss13.javabite.ast.nodes.unary;

import java.util.LinkedList;
import java.util.List;

import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.PrintNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.ast.nodes.StatementNodeJb;

public class PrintNodeJb extends StatementNodeJb implements PrintNode {
	public PrintNodeJb() {
		super(ASTNodeType.PrintNode);
	}

	protected IdentifierNode rightValue;

	public IdentifierNode getRightValue() {
		return rightValue;
	}

	public void setRightValue(IdentifierNode rightValue) {
		this.rightValue = rightValue;
		addChild(rightValue, 0);
	}

	@Override
	public List<Token> coverage() {
		List<Token> res=new LinkedList<>();
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.PRINT));
		res.addAll(rightValue.coverage());
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.SEMICOLON));
		return res;
	}
	
}
