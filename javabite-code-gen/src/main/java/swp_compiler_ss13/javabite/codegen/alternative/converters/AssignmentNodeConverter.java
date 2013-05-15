package swp_compiler_ss13.javabite.codegen.alternative.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.alternative.CastingAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.alternative.IdentifierData;
import swp_compiler_ss13.javabite.codegen.alternative.QuadrupleFactoryJb;

public class AssignmentNodeConverter extends CastingAst2CodeConverter {

	@Override
	public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof AssignmentNode))
			throw new IntermediateCodeGeneratorException();
		AssignmentNode assignmentNode = (AssignmentNode) node;
		
		icg.processNode(assignmentNode.getRightValue());
		IdentifierData rightData = icg.popIdentifierData();
		icg.processNode(assignmentNode.getLeftValue());
		IdentifierData leftData = icg.popIdentifierData();
		
		if (leftData.getType().getKind() != rightData.getType().getKind()) {
			rightData = cast(leftData.getType(), rightData);
		}
		
		icg.addQuadruple(QuadrupleFactoryJb.generateAssignment(leftData, rightData));
		
		// assignment goal could be right-side of assignment
		icg.pushIdentifierData(leftData);
	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.AssignmentNode;
	}
}
