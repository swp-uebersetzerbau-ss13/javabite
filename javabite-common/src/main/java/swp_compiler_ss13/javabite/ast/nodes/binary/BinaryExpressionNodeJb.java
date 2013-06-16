package swp_compiler_ss13.javabite.ast.nodes.binary;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.ast.nodes.ExpressionNodeJb;

public abstract class BinaryExpressionNodeJb extends ExpressionNodeJb implements BinaryExpressionNode {
	public BinaryExpressionNodeJb(ASTNodeType myType) {
		super(myType);
	}
	protected ExpressionNode leftValue;
	protected BinaryOperator operator;
	protected ExpressionNode rightValue;
	public ExpressionNode getLeftValue() {
		return leftValue;
	}
	public BinaryOperator getOperator() {
		return operator;
	}
	public ExpressionNode getRightValue() {
		return rightValue;
	}
	public void setLeftValue(ExpressionNode leftValue) {
		this.leftValue = leftValue;
		addChild(leftValue,0);
	}
	public void setOperator(BinaryOperator operator) {
		this.operator = operator;
	}
	public void setRightValue(ExpressionNode rightValue) {
		this.rightValue = rightValue;
		addChild(rightValue,1);
	}
	@Override
	protected void fillNodeProperties(Properties props) {
		props.put("Op", operator);
	}
	@Override
	public List<Token> nestedCoverage() {
		List<Token> res=new LinkedList<>();
		res.addAll(leftValue.coverage());
		res.add(getAssociatedTokenListFromTypeUnique(
				TokenType.AND,
				TokenType.DIVIDE,
				TokenType.EQUALS,
				TokenType.LESS,
				TokenType.LESS_OR_EQUAL,
				TokenType.GREATER,
				TokenType.GREATER_EQUAL,
				TokenType.MINUS,
				TokenType.PLUS,
				TokenType.TIMES,
				TokenType.OR,
				TokenType.NOT_EQUALS));
		res.addAll(rightValue.coverage());
		return res;
	}
	
}
