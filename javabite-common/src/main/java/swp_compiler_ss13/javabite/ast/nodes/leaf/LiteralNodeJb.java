package swp_compiler_ss13.javabite.ast.nodes.leaf;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.javabite.ast.nodes.ExpressionNodeJb;

public class LiteralNodeJb extends ExpressionNodeJb implements LiteralNode {
	public String getLiteral() {
		return literal;
	}

	public void setLiteral(String literal) {
		this.literal = literal;
	}

	public Type getLiteralType() {
		return literalType;
	}

	public void setLiteralType(Type literalType) {
		this.literalType = literalType;
	}

	protected String literal;
	protected Type literalType;

	public LiteralNodeJb() {
		super(ASTNodeType.LiteralNode);
	}

	@Override
	protected void fillNodeProperties(Properties props) {
		props.put("literal", literal);
		props.put("type", literalType);

	}

	@Override
	public List<Token> nestedCoverage() {
		List<Token> res = new LinkedList<>();
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.STRING,
				TokenType.NUM, TokenType.REAL, TokenType.FALSE, TokenType.TRUE));
		return res;
	}

}
