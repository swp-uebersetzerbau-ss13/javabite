package swp_compiler_ss13.javabite.ast.nodes.unary;

import java.util.Properties;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.unary.UnaryExpressionNode;
import swp_compiler_ss13.javabite.ast.nodes.ExpressionNodeJb;

public class UnaryExpressionNodeJb extends ExpressionNodeJb implements UnaryExpressionNode {
	public UnaryExpressionNodeJb(ASTNodeType myType) {
		super(myType);
	}
	protected ExpressionNode rightValue;
	protected UnaryOperator operator;
	public ExpressionNode getRightValue() {
		return rightValue;
	}
	public void setRightValue(ExpressionNode rightValue) {
		this.rightValue = rightValue;
		addChild(rightValue, 0);
	}
	public UnaryOperator getOperator() {
		return operator;
	}
	public void setOperator(UnaryOperator operator) {
		this.operator = operator;
	}
	@Override
	protected void fillNodeProperties(Properties props) {
		props.put("op", operator);
	}
	
}
