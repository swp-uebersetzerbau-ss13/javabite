package swp_compiler_ss13.javabite.codegen.converters;

import java.util.ArrayDeque;
import java.util.Deque;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArrayIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.StructIdentifierNode;
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
		
		Deque<ArrayIdentifierNode> stack = new ArrayDeque<>();
		stack.push(arrayIdentifierNode);
		IdentifierNode idNode = arrayIdentifierNode.getIdentifierNode();
		while (idNode instanceof ArrayIdentifierNode) {
			ArrayIdentifierNode ain = (ArrayIdentifierNode) idNode;
			stack.push(ain);
			idNode = ain.getIdentifierNode();
		}
		
		boolean isEndNodeBasicIdentifier = idNode instanceof BasicIdentifierNode;
		boolean isEndNodeStructIdentifier = idNode instanceof StructIdentifierNode;
		
		if (!isEndNodeBasicIdentifier && !isEndNodeStructIdentifier) {
			throw new IntermediateCodeGeneratorException("Unexpected AST structure");
		}
		
		//TODO: for index nodes we must open a new InsideOfStruct-scope
		
		String baseName;
		if (icg.isInsideOfStruct()) {
			//TODO: implement
			throw new IntermediateCodeGeneratorException("Arrays inside of structs are not supported yet");
		} else if (isEndNodeBasicIdentifier) {
			baseName = ((BasicIdentifierNode)idNode).getIdentifier();
		} else {
			IdentifierNode structId = ((StructIdentifierNode)idNode).getIdentifierNode();
			
			if (!(structId instanceof BasicIdentifierNode)) {
				throw new IntermediateCodeGeneratorException("Unexpected AST structure");
			}
			
			baseName = ((BasicIdentifierNode)structId).getIdentifier();
		}
		
		if (stack.size() > 1) {
			icg.addQuadruple(null);
			//TODO
		} 
	
		
		
		
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
