package swp_compiler_ss13.javabite.codegen.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArrayIdentifierNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.AbstractAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.IdentifierData;

public class ArrayIdentifierNodeConverter extends AbstractAst2CodeConverter {

	@Override
	public void  convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof ArrayIdentifierNode))
			throw new IntermediateCodeGeneratorException();
		ArrayIdentifierNode arrayIdentifierNode = (ArrayIdentifierNode) node;
		IdentifierNode idNode = arrayIdentifierNode.getIdentifierNode();
		// get identifier for node and provide for callee
		//icg.pushIdentifierData(icg.lookupIdentifierData(basicIdentifierNode.getIdentifier()));
	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.ArrayIdentifierNode;
	}
}
