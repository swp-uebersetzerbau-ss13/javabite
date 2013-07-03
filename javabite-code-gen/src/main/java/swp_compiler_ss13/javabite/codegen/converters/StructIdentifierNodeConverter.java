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
import swp_compiler_ss13.common.types.derived.Member;
import swp_compiler_ss13.common.types.derived.StructType;
import swp_compiler_ss13.javabite.codegen.AbstractAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.IdentifierData;
import swp_compiler_ss13.javabite.codegen.QuadrupleFactoryJb;

public class StructIdentifierNodeConverter extends AbstractAst2CodeConverter {

	@Override
	public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof StructIdentifierNode))
			throw new IntermediateCodeGeneratorException();
		StructIdentifierNode structIdentifierNode = (StructIdentifierNode) node;

		icg.processNode(structIdentifierNode.getIdentifierNode());
		IdentifierData prefixId = icg.popIdentifierData();

		if (!(prefixId.getType() instanceof StructType)) {
			throw new IntermediateCodeGeneratorException(
					"StructProcessin without StructType");
		}
		StructType type = (StructType) prefixId.getType();

		Type innerType = getType(type, structIdentifierNode.getFieldName());

		String reference = null;
		// generate a reference
		if (isStructInAccessChain(structIdentifierNode)) {
			reference = icg.getNewReference();
			icg.addQuadruple(QuadrupleFactoryJb.generateGetStructReference(
					prefixId.getIdentifier(),
					structIdentifierNode.getFieldName(), reference));
			// is end of chain -> push value or reference above
		} else {
			if (isTargetOfAssignment(structIdentifierNode)) {
				reference = prefixId.getIdentifier();
				icg.pushIdentifierData(new IdentifierData(structIdentifierNode.getFieldName(), innerType));
			} else {
				if (innerType instanceof ArrayType
						|| innerType instanceof StructType)
					throw new IntermediateCodeGeneratorException(
							"Expected basic type but found Array- or StructType");
				IdentifierData tmp = icg.generateTempIdentifier(innerType);
				icg.addQuadruple(QuadrupleFactoryJb.generateGetStruct(prefixId.getIdentifier(),
						structIdentifierNode.getFieldName(), tmp));
				reference = tmp.getIdentifier();
			}
		}

		icg.pushIdentifierData(new IdentifierData(reference, innerType));
	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.StructIdentifierNode;
	}

	private Type getType(StructType structType, String fieldName)
			throws IntermediateCodeGeneratorException {
		for (Member m : structType.members()) {
			if (m.getName().equals(fieldName))
				return m.getType();
		}
		throw new IntermediateCodeGeneratorException(
				"StructType definition does not contain requested field");
	}

	private boolean isStructInAccessChain(StructIdentifierNode node)
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

	private boolean isTargetOfAssignment(StructIdentifierNode node) {
		ASTNode parent = node.getParentNode();

		if (!(parent instanceof AssignmentNode)) {
			return false;
		}

		AssignmentNode assign = (AssignmentNode) parent;
		return assign.getLeftValue() == node;
	}
}
