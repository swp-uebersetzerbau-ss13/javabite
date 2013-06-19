package swp_compiler_ss13.javabite.codegen.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.AbstractAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.IdentifierData;

public class LiteralNodeConverter extends AbstractAst2CodeConverter {

	@Override
	public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof LiteralNode))
			throw new IntermediateCodeGeneratorException();
		LiteralNode literalNode = (LiteralNode) node;
		
		// literals works like for TAC generation like identifiers
		icg.pushIdentifierData(new IdentifierData("#" + literalNode.getLiteral(), literalNode.getLiteralType()));
	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.LiteralNode;
	}
}
