package swp_compiler_ss13.javabite.ast.nodes.unary;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.unary.UnaryExpressionNode;
import swp_compiler_ss13.javabite.ast.nodes.ExpressionNodeJb;

public class UnaryExpressionNodeJb extends ExpressionNodeJb implements UnaryExpressionNode {
	protected ExpressionNode rightValue;
	protected UnaryOperator operator;
	public ExpressionNode getRightValue() {
		return rightValue;
	}
	public void setRightValue(ExpressionNode rightValue) {
		this.rightValue = rightValue;
	}
	public UnaryOperator getOperator() {
		return operator;
	}
	public void setOperator(UnaryOperator operator) {
		this.operator = operator;
	}
	
	
}
