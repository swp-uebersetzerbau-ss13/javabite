package swp_compiler_ss13.javabite.ast.nodes.unary;

import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.PrintNode;
import swp_compiler_ss13.javabite.ast.nodes.StatementNodeJb;

public class PrintNodeJb extends StatementNodeJb implements PrintNode {
	public PrintNodeJb() {
		super(ASTNodeType.PrintNode);
	}

	protected IdentifierNode rightValue;

	public IdentifierNode getRightValue() {
		return rightValue;
	}

	public void setRightValue(IdentifierNode rightValue) {
		this.rightValue = rightValue;
		addChild(rightValue, 0);
	}
}
