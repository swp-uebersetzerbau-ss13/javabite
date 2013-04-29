package swp_compiler_ss13.javabite.ast.nodes.binary;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode;
import swp_compiler_ss13.javabite.ast.nodes.ExpressionNodeJb;

public class BinaryExpressionNodeJb extends ExpressionNodeJb implements BinaryExpressionNode {
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
	}
	public void setOperator(BinaryOperator operator) {
		this.operator = operator;
	}
	public void setRightValue(ExpressionNode rightValue) {
		this.rightValue = rightValue;
	}
	
}
