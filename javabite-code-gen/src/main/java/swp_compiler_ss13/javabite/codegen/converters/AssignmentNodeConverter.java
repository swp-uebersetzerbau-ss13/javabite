package swp_compiler_ss13.javabite.codegen.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArrayIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.StructIdentifierNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.CastingAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.IdentifierData;
import swp_compiler_ss13.javabite.codegen.QuadrupleFactoryJb;

public class AssignmentNodeConverter extends CastingAst2CodeConverter {

	@Override
	public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof AssignmentNode))
			throw new IntermediateCodeGeneratorException();
		AssignmentNode assignmentNode = (AssignmentNode) node;
		IdentifierNode targetNode = assignmentNode.getLeftValue();
		
		// process right node to get identifier of containing result
		icg.processNode(assignmentNode.getRightValue());
		IdentifierData rightData = icg.popIdentifierData();
		// process left node to get identifier
		icg.processNode(targetNode);
		IdentifierData leftData = icg.popIdentifierData();
		
		// cast right identifier if necessary
		if (leftData.getType().getKind() != rightData.getType().getKind()) {
			rightData = cast(leftData.getType(), rightData);
		}
		
		// add assignment operation to TAC
		if (targetNode instanceof StructIdentifierNode) {
			IdentifierData fieldName = icg.popIdentifierData();
			icg.addQuadruple(QuadrupleFactoryJb.generateSetStruct(leftData.getIdentifier(), fieldName.getIdentifier(), rightData));
		} else if (targetNode instanceof ArrayIdentifierNode) {
			IdentifierData index = icg.popIdentifierData();
			icg.addQuadruple(QuadrupleFactoryJb.generateSetStruct(leftData.getIdentifier(), index.getIdentifier(), rightData));
		} else if (targetNode instanceof BasicIdentifierNode){
			icg.addQuadruple(QuadrupleFactoryJb.generateAssignment(leftData, rightData));	
		} else {
			throw new IntermediateCodeGeneratorException("Unexpected AST structure");
		}
		
		// assignment goal could be right-side of assignment -> provide identifier for callee
		icg.pushIdentifierData(rightData);
	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.AssignmentNode;
	}
}
