package swp_compiler_ss13.javabite.codegen.alternative.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.unary.ReturnNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.alternative.AbstractAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.alternative.IdentifierData;
import swp_compiler_ss13.javabite.codegen.alternative.QuadrupleFactoryJb;

public class ReturnNodeConverter extends AbstractAst2CodeConverter {

	@Override
	public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof ReturnNode))
			throw new IntermediateCodeGeneratorException();
		ReturnNode returnNode = (ReturnNode) node;

		// process the right node and get the result containing identifier
		icg.processNode(returnNode.getRightValue());
		IdentifierData data = icg.popIdentifierData();
		// add return TAC for this identifier
		icg.addQuadruple(QuadrupleFactoryJb.generateReturn(data));
	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.ReturnNode;
	}
}
