package swp_compiler_ss13.javabite.codegen.beta;

import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.javabite.ast.nodes.unary.DeclarationNodeJb;

public class DeclarationNodeCG {
	
	public void convert(DeclarationNode node) throws IntermediateCodeGeneratorException {
		String identifierName = node.getIdentifier();
		Type identifierType = node.getType();
		String id = IRCodeGenerator.saveIdentifier(identifierName, identifierType);
}
}
