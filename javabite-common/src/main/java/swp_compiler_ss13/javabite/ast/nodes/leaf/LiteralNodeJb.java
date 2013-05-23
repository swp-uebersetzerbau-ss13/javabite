package swp_compiler_ss13.javabite.ast.nodes.leaf;

import java.util.Properties;

import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.javabite.ast.nodes.ExpressionNodeJb;

public class LiteralNodeJb extends ExpressionNodeJb implements LiteralNode {
	public String getLiteral() {
		return literal;
	}

	public void setLiteral(String literal) {
		this.literal = literal;
	}

	public Type getLiteralType() {
		return literalType;
	}

	public void setLiteralType(Type literalType) {
		this.literalType = literalType;
	}

	protected String literal;
	protected Type literalType;
	
	public LiteralNodeJb() {
		super(ASTNodeType.LiteralNode);
	}
	@Override
	protected void fillNodeProperties(Properties props) {
		props.put("literal",literal);
		props.put("type",literalType);
		
	}


}
