package swp_compiler_ss13.javabite.codegen;

import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;

public class DeclarationNodeCG {
	
	public void convert(DeclarationNode node) throws IntermediateCodeGeneratorException {
		String identifier = node.getIdentifier();
		Type identifierType = node.getType();
		JavaBiteCodeGenerator.addIdentifier(identifier, identifierType);
	}
}
