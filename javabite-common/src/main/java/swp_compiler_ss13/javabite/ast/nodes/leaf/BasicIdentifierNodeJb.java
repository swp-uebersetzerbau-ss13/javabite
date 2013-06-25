package swp_compiler_ss13.javabite.ast.nodes.leaf;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.ast.nodes.IdentifierNodeJb;

public class BasicIdentifierNodeJb extends IdentifierNodeJb implements
		BasicIdentifierNode {
	public BasicIdentifierNodeJb() {
		super(ASTNodeType.BasicIdentifierNode);
	}

	protected String identifier;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	protected void fillNodeProperties(Properties props) {
		props.put("id", identifier);
	}

	@Override
	public List<Token> nestedCoverage() {
		List<Token> res = new LinkedList<>();
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.ID));
		return res;
	}
}
