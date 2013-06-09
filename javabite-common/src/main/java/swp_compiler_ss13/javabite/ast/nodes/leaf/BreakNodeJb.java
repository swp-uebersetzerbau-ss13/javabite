package swp_compiler_ss13.javabite.ast.nodes.leaf;

import java.util.LinkedList;
import java.util.List;

import swp_compiler_ss13.common.ast.nodes.leaf.BreakNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.ast.nodes.ExpressionNodeJb;

public class BreakNodeJb extends ExpressionNodeJb implements BreakNode {

	public BreakNodeJb() {
		super(ASTNodeType.BreakNode);
	}
	
	@Override
	public List<Token> nestedCoverage() {
		List<Token> res=new LinkedList<>();
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.BREAK));
		return res;
	}

}
