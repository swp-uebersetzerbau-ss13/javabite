package swp_compiler_ss13.javabite.codegen.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArrayIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.StructIdentifierNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.common.types.derived.Member;
import swp_compiler_ss13.common.types.derived.StructType;
import swp_compiler_ss13.javabite.codegen.AbstractAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.IdentifierData;

public class StructIdentifierNodeConverter extends AbstractAst2CodeConverter {

	@Override
	public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof StructIdentifierNode))
			throw new IntermediateCodeGeneratorException();
		StructIdentifierNode structIdentifierNode = (StructIdentifierNode) node;
		
		icg.processNode(structIdentifierNode.getIdentifierNode());
		IdentifierData prefixId = icg.popIdentifierData();
		
		if (!(prefixId.getType() instanceof StructType)) {
			throw new IntermediateCodeGeneratorException("StructProcessin without StructType");
		}
		StructType type = (StructType)prefixId.getType();
		
		Type innerType = getType(type, structIdentifierNode.getFieldName());
		
		String reference = null;
		// generate a reference
		if (isStructInAccessChain(structIdentifierNode)) {
			reference = icg.getNewReference();
		// is end of chain -> push value or reference above
		} else {
			
		}
		
		icg.pushIdentifierData(new IdentifierData(reference, innerType));
	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.StructIdentifierNode;
	}
	
	private Type getType(StructType structType, String fieldName) throws IntermediateCodeGeneratorException {
		for (Member m:structType.members()) {
			if (m.getName().equals(fieldName))
				return m.getType();
		}
		throw new IntermediateCodeGeneratorException("StructType definition does not contain requested field");
	}
	
	private boolean isStructInAccessChain(StructIdentifierNode node) throws IntermediateCodeGeneratorException {
		ASTNode parent = node.getParentNode();
		
		// if parent is not a IdentifierNode it does not contains to a chain
		if (!(parent instanceof IdentifierNode)) {
			return false;
		}
		
		// if parent is a StructIdentifierNode it does contains to chain
		if (parent instanceof StructIdentifierNode) {
			return true;
		// if parent is a ArrayIdentifierNode it is only in chain if it is not the index node
		} else if (parent instanceof ArrayIdentifierNode) {
			return ((ArrayIdentifierNode)parent).getIndexNode() != node;
		}
		throw new IntermediateCodeGeneratorException("Unexpected AST structure");
	}
	
	private boolean isTargetOfAssignment(StructIdentifierNode node) {
		//TODO:
		return false;
	}
}
