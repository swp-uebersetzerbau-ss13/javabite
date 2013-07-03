package swp_compiler_ss13.javabite.codegen.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArrayIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.StructIdentifierNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.common.types.derived.ArrayType;
import swp_compiler_ss13.common.types.derived.StructType;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.javabite.codegen.AbstractAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.IdentifierData;
import swp_compiler_ss13.javabite.codegen.QuadrupleFactoryJb;

public class ArrayIdentifierNodeConverter extends AbstractAst2CodeConverter {

	@Override
	public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof ArrayIdentifierNode))
			throw new IntermediateCodeGeneratorException();
		ArrayIdentifierNode arrayIdentifierNode = (ArrayIdentifierNode) node;
		
		icg.processNode(arrayIdentifierNode.getIdentifierNode());
		IdentifierData prefixId = icg.popIdentifierData();

		if (!(prefixId.getType() instanceof ArrayType)) {
			throw new IntermediateCodeGeneratorException(
					"StructProcessin without ArrayType");
		}
		ArrayType type = (ArrayType) prefixId.getType();

		Type innerType = type.getInnerType();

		icg.processNode(arrayIdentifierNode.getIndexNode());
		IdentifierData indexId = icg.popIdentifierData();
		
		if (!(indexId.getType() instanceof LongType)) {
			throw new IntermediateCodeGeneratorException("Index expression is not of type Long");
		}
		
		String reference = null;
		// generate a reference
		if (isArrayInAccessChain(arrayIdentifierNode)) {
			reference = icg.getNewReference();
			icg.addQuadruple(QuadrupleFactoryJb.generateGetArrayReference(
					prefixId.getIdentifier(),
					indexId.getIdentifier(), reference));
			// is end of chain -> push value or reference above
		} else {
			if (isTargetOfAssignment(arrayIdentifierNode)) {
				reference = prefixId.getIdentifier();
				icg.pushIdentifierData(new IdentifierData(indexId.getIdentifier(), innerType));
			} else {
				if (innerType instanceof ArrayType
						|| innerType instanceof StructType)
					throw new IntermediateCodeGeneratorException(
							"Expected basic type but found Array- or StructType");
				IdentifierData tmp = icg.generateTempIdentifier(innerType);
				icg.addQuadruple(QuadrupleFactoryJb.generateGetArray(prefixId.getIdentifier(),
						indexId.getIdentifier(), tmp));
				reference = tmp.getIdentifier();
			}
		}

		icg.pushIdentifierData(new IdentifierData(reference, innerType));
	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.ArrayIdentifierNode;
	}

	private boolean isArrayInAccessChain(ArrayIdentifierNode node)
			throws IntermediateCodeGeneratorException {
		ASTNode parent = node.getParentNode();

		// if parent is not a IdentifierNode it is not contained in a chain
		if (!(parent instanceof IdentifierNode)) {
			return false;
		}

		// if parent is a StructIdentifierNode it is contained in chain
		if (parent instanceof StructIdentifierNode) {
			return true;
			// if parent is a ArrayIdentifierNode it is only in chain if it is
			// not the index node
		} else if (parent instanceof ArrayIdentifierNode) {
			return ((ArrayIdentifierNode) parent).getIndexNode() != node;
		}
		throw new IntermediateCodeGeneratorException("Unexpected AST structure");
	}

	private boolean isTargetOfAssignment(ArrayIdentifierNode node) {
		ASTNode parent = node.getParentNode();

		if (!(parent instanceof AssignmentNode)) {
			return false;
		}

		AssignmentNode assign = (AssignmentNode) parent;
		return assign.getLeftValue() == node;
	}
}
