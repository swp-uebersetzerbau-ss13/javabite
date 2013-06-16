package swp_compiler_ss13.javabite.codegen.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.unary.PrintNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.CastingAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.IdentifierData;
import swp_compiler_ss13.javabite.codegen.QuadrupleFactoryJb;

public class PrintNodeConverter extends CastingAst2CodeConverter {

	@Override
	public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof PrintNode))
			throw new IntermediateCodeGeneratorException();
		PrintNode printNode = (PrintNode) node;

		icg.processNode(printNode.getRightValue());
		IdentifierData rightData = icg.popIdentifierData();
		icg.addQuadruple(QuadrupleFactoryJb.generatePrint(rightData));
	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.PrintNode;
	}

}

