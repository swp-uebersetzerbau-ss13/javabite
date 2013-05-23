package swp_compiler_ss13.javabite.codegen.alternative.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.alternative.AbstractAst2CodeConverter;

public class BasicIdentifierNodeConverter extends AbstractAst2CodeConverter {

	@Override
	public void  convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof BasicIdentifierNode))
			throw new IntermediateCodeGeneratorException();
		BasicIdentifierNode basicIdentifierNode = (BasicIdentifierNode) node;
		
		// get identifier for node and provide for callee
		icg.pushIdentifierData(icg.lookupIdentifierData(basicIdentifierNode.getIdentifier()));
	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.BasicIdentifierNode;
	}
}
