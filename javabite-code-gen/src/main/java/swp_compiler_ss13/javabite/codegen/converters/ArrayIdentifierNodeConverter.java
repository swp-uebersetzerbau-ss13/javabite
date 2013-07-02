package swp_compiler_ss13.javabite.codegen.converters;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArrayIdentifierNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.common.types.derived.ArrayType;
import swp_compiler_ss13.javabite.codegen.AbstractAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.IdentifierData;

public class ArrayIdentifierNodeConverter extends AbstractAst2CodeConverter {

	@Override
	public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof ArrayIdentifierNode))
			throw new IntermediateCodeGeneratorException();
		ArrayIdentifierNode arrayIdentifierNode = (ArrayIdentifierNode) node;
		
		IdentifierNode idNode = arrayIdentifierNode.getIdentifierNode();
		
		List<Quadruple> replacementList = new ArrayList<>();
		List<Quadruple> hijackedQuadruples = icg
				.hijackQuadrupleList(replacementList);

		Deque<ArrayIdentifierNode> stack = new ArrayDeque<>();
		
		
		
		
		ASTNode parent = arrayIdentifierNode.getParentNode();
		// if node is inside an array chain it use a reference and not the
		// array-name which gets pushed up from lowest level
		// to check if the node is inside the a chain the parent has to be an
		// ArrayIdentifierNode and this node must be its IdentifierNode
		boolean insideArrayChain = parent instanceof ArrayIdentifierNode
				&& ((ArrayIdentifierNode) parent).getIdentifierNode() == arrayIdentifierNode;
		// this is the bottom of the array chain (probably a one element chain) if
		// IdentifierNode is not an ArrayIdentifierNode
		boolean isBottomOfArrayChain = !(idNode instanceof ArrayIdentifierNode);
		boolean childIsBasicIdentifier = idNode instanceof BasicIdentifierNode;
		
		
		IdentifierData data = icg.popIdentifierData();

	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.ArrayIdentifierNode;
	}

	private Type getBasicType(IdentifierData data) {
		Type type = data.getType();

		while (type instanceof ArrayType) {
			type = ((ArrayType) type).getInnerType();
		}

		return type;
	}
}
