package swp_compiler_ss13.javabite.codegen;

import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;

public class BasicIdentifierNodeCG {

	public void convert(BasicIdentifierNode node) throws IntermediateCodeGeneratorException{
		// get the identifier and its type
		String identifier = node.getIdentifier();
		Type identifierType = IntermediateCodeGeneratorJb.symbolTable.peek().lookupType(identifier);
		// get current the identifier, maybe it is overwritten
		String actualIdentifier = IntermediateCodeGeneratorJb.getIdentifier(identifier);
		IntermediateCodeGeneratorJb.temporaryResultOutputs.push(actualIdentifier);
		IntermediateCodeGeneratorJb.temporaryTypes.push(identifierType);
	}
}
