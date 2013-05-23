package swp_compiler_ss13.javabite.codegen;

import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;

public class DeclarationNodeCG {
	
	public void convert(DeclarationNode node) throws IntermediateCodeGeneratorException {
		// get the identifier and its type, then add the identifier
		String identifier = node.getIdentifier();
		Type identifierType = node.getType();
		IntermediateCodeGeneratorJb.addIdentifier(identifier, identifierType);
	}
}
