package swp_compiler_ss13.javabite.ast.nodes.unary;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.StructIdentifierNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.ast.nodes.IdentifierNodeJb;

public class StructIdentifierNodeJb extends IdentifierNodeJb implements
		StructIdentifierNode {
	public StructIdentifierNodeJb() {
		super(ASTNodeType.StructIdentifierNode);
	}

	protected String fieldName;
	protected IdentifierNode identifierNode;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
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
		props.put("fieldname", fieldName);
	}

	@Override
	public List<Token> nestedCoverage() {
		List<Token> res = new LinkedList<>();
		
		res.addAll(identifierNode.coverage());
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.DOT));
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.ID));
		
		return res;
	}
}
