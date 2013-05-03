package swp_compiler_ss13.javabite.ast.nodes.unary;

import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.ReturnNode;
import swp_compiler_ss13.javabite.ast.nodes.StatementNodeJb;

public class ReturnNodeJb extends StatementNodeJb implements ReturnNode {
	public ReturnNodeJb() {
		super(ASTNodeType.ReturnNode);
	}

	protected IdentifierNode rightValue;

	public IdentifierNode getRightValue() {
		return rightValue;
	}

	public void setRightValue(IdentifierNode rightValue) {
		this.rightValue = rightValue;
		addChild(rightValue,0);
	}

}
