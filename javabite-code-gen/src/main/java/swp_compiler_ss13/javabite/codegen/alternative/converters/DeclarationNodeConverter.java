package swp_compiler_ss13.javabite.codegen.alternative.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.alternative.AbstractAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.alternative.IdentifierData;
import swp_compiler_ss13.javabite.codegen.alternative.QuadrupleFactoryJb;

public class DeclarationNodeConverter extends AbstractAst2CodeConverter {

	@Override
	public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof DeclarationNode))
			throw new IntermediateCodeGeneratorException();
		DeclarationNode declarationNode = (DeclarationNode) node;

		// generate identifier mapping for the declaration node
		IdentifierData data = icg.generateIdentifierMapping(
				declarationNode.getIdentifier(), declarationNode.getType());
		// add declaration to TAC
		icg.addQuadruple(QuadrupleFactoryJb.generateDeclaration(data));
	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.DeclarationNode;
	}
}
