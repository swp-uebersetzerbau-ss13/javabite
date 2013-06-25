package swp_compiler_ss13.javabite.ast.nodes.binary;

import java.util.LinkedList;
import java.util.List;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.ast.nodes.ExpressionNodeJb;

public class AssignmentNodeJb extends ExpressionNodeJb implements
		AssignmentNode {
	public IdentifierNode getLeftValue() {
		return leftValue;
	}

	public void setLeftValue(IdentifierNode leftValue) {
		this.leftValue = leftValue;
		addChild(leftValue, 0);
	}

	public ExpressionNode getRightValue() {
		return rightValue;
	}

	public void setRightValue(ExpressionNode rightValue) {
		this.rightValue = rightValue;
		addChild(rightValue, 1);
	}

	protected IdentifierNode leftValue;
	protected ExpressionNode rightValue;

	public AssignmentNodeJb() {
		super(ASTNodeType.AssignmentNode);
	}

	@Override
	public List<Token> nestedCoverage() {
		List<Token> res = new LinkedList<>();
		res.addAll(leftValue.coverage());
		res.add(getAssociatedTokenListFromTypeUnique(TokenType.ASSIGNOP));
		res.addAll(rightValue.coverage());
		return res;
	}

}
