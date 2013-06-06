package swp_compiler_ss13.javabite.ast.nodes;

import java.util.LinkedList;
import java.util.List;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;


public abstract class ExpressionNodeJb extends StatementNodeJb implements ExpressionNode {

	public ExpressionNodeJb(ASTNodeType myType) {
		super(myType);
	}
	
	@Override
	public final List<Token> coverage() {
		List<Token> res=new LinkedList<Token>();
		List<Token> open=getAssociatedTokenFromType(TokenType.LEFT_PARAN);
		List<Token> close=getAssociatedTokenFromType(TokenType.RIGHT_PARAN);
		List<Token> semicolon=getAssociatedTokenFromType(TokenType.SEMICOLON);
		res.addAll(open);
		res.addAll(nestedCoverage());
		res.addAll(close);
		res.addAll(semicolon);
		return res;
	}
	
	/**
	 * this is the alternative for the coverage method
	 * we need this to handle paranthesis  
	 * @return
	 */
	public abstract List<Token> nestedCoverage();
	
}
