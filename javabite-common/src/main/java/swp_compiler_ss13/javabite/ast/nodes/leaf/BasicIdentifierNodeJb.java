package swp_compiler_ss13.javabite.ast.nodes.leaf;

import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.javabite.ast.nodes.IdentifierNodeJb;

public class BasicIdentifierNodeJb extends IdentifierNodeJb implements BasicIdentifierNode {
	protected String identifier;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	

}
