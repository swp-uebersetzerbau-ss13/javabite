package swp_compiler_ss13.javabite.ast.nodes.unary;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArrayIdentifierNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.ast.nodes.IdentifierNodeJb;

public class ArrayIdentifierNodeJb extends IdentifierNodeJb implements
		ArrayIdentifierNode {
	public ArrayIdentifierNodeJb() {
		super(ASTNodeType.ArrayIdentifierNode);
	}

	protected ExpressionNode indexNode;
	protected IdentifierNode identifierNode;

	public ExpressionNode getIndexNode() {
		return indexNode;
	}

	public void setIndexNode(ExpressionNode indexNode) {
		this.indexNode = indexNode;
		addChild(indexNode, 1);
	}

	public IdentifierNode getIdentifierNode() {
		return identifierNode;
	}

	public void setIdentifierNode(IdentifierNode identifierNode) {
		this.identifierNode = identifierNode;
		addChild(identifierNode, 0);
	}

	@Override
	protected void fillNodeProperties(Properties props) {
	}

	@Override
	public List<Token> nestedCoverage() {
		List<Token> res = new LinkedList<>();
		
		res.addAll(identifierNode.coverage());
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.LEFT_BRACKET));
		res.addAll(indexNode.coverage());
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.RIGHT_BRACKET));
		
		return res;
	}
}
