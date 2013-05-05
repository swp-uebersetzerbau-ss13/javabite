package swp_compiler_ss13.javabite.ast.nodes.unary;

import java.util.Properties;

import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArrayIdentifierNode;
import swp_compiler_ss13.javabite.ast.nodes.IdentifierNodeJb;

public class ArrayIdentifierNodeJb extends IdentifierNodeJb implements ArrayIdentifierNode {
	public ArrayIdentifierNodeJb() {
		super(ASTNodeType.ArrayIdentifierNode);
	}
	protected Integer index;
	protected IdentifierNode identifierNode;
	public Integer getIndex() {
		return index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
	public IdentifierNode getIdentifierNode() {
		return identifierNode;
	}
	public void setIdentifierNode(IdentifierNode identifierNode) {
		this.identifierNode = identifierNode;
	}
	@Override
	protected void fillNodeProperties(Properties props) {
		props.put("index", index);
	}
}
