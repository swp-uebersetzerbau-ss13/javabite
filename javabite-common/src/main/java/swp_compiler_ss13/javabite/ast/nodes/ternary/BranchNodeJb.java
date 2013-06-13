package swp_compiler_ss13.javabite.ast.nodes.ternary;

import java.util.LinkedList;
import java.util.List;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.ternary.BranchNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.ast.nodes.StatementNodeJb;

public class BranchNodeJb extends StatementNodeJb implements BranchNode {

	public BranchNodeJb() {
		super(ASTNodeType.BranchNode);
	}

	protected ExpressionNode condition;
	protected StatementNode statementNodeOnTrue;
	protected StatementNode statementNodeOnFalse;

	
	public ExpressionNode getCondition() {
		return condition;
	}

	public void setCondition(ExpressionNode condition) {
		this.condition = condition;
		addChild(condition, 0);
	}

	
	public StatementNode getStatementNodeOnTrue() {
		return statementNodeOnTrue;
	}
	
	public void setStatementNodeOnTrue(StatementNode statementNodeOnTrue) {
		this.statementNodeOnTrue = statementNodeOnTrue;
		addChild(this.statementNodeOnTrue, 1);
	}

	public StatementNode getStatementNodeOnFalse() {
		return statementNodeOnFalse;
	}
	
	public void setStatementNodeOnFalse(StatementNode statementNodeOnFalse) {
		this.statementNodeOnFalse = statementNodeOnFalse;
		addChild(this.statementNodeOnFalse, 2);
	}
	@Override
	public List<Token> coverage() {
		List<Token> res=new LinkedList<Token>();
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.IF));
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.LEFT_PARAN));
		res.addAll(condition.coverage());
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.RIGHT_PARAN));
		res.addAll(statementNodeOnTrue.coverage());
		if (this.statementNodeOnFalse!=null){
			// is with else
			res.add(getAssociatedTokenListFromTypeUnique(TokenType.ELSE));
			res.addAll(statementNodeOnFalse.coverage());
		}
		return res;
	}
}
