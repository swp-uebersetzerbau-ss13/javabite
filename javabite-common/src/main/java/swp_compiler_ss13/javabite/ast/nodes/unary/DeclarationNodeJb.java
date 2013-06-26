package swp_compiler_ss13.javabite.ast.nodes.unary;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.javabite.ast.ASTNodeJb;

public class DeclarationNodeJb extends ASTNodeJb implements DeclarationNode {
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	protected String identifier;
	protected Type type;

	public DeclarationNodeJb() {
		super(ASTNodeType.DeclarationNode);
	}

	@Override
	protected void fillNodeProperties(Properties props) {
		props.put("id", identifier);
		props.put("type", type);
	}

	@Override
	public List<Token> coverage() {
		List<Token> res = new LinkedList<>();
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.BOOL_SYMBOL,
				TokenType.DOUBLE_SYMBOL, TokenType.LONG_SYMBOL,
				TokenType.STRING_SYMBOL));
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.ID));
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.SEMICOLON));
		return res;
	}
}
