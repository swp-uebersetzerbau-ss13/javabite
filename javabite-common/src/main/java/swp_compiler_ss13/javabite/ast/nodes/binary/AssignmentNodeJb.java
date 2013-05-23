package swp_compiler_ss13.javabite.ast.nodes.binary;

import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.javabite.ast.nodes.StatementNodeJb;

public class AssignmentNodeJb extends StatementNodeJb implements AssignmentNode {
	public IdentifierNode getLeftValue() {
		return leftValue;
	}

	public void setLeftValue(IdentifierNode leftValue) {
		this.leftValue = leftValue;
		addChild(leftValue, 0);
	}

	public StatementNode getRightValue() {
		return rightValue;
	}

	public void setRightValue(StatementNode rightValue) {
		this.rightValue = rightValue;
		addChild(rightValue,1);
	}

	protected IdentifierNode leftValue;
	protected StatementNode rightValue;

	public AssignmentNodeJb() {
		super(ASTNodeType.AssignmentNode);
	}

}
