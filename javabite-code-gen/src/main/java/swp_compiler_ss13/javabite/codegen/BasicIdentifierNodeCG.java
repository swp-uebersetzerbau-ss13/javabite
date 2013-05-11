package swp_compiler_ss13.javabite.codegen;

import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;

public class BasicIdentifierNodeCG {

	public void convert(BasicIdentifierNode node) throws IntermediateCodeGeneratorException{
		String identifier = node.getIdentifier();
		Type identifierType = JavaBiteCodeGenerator.latestSymbolTable.peek().lookupType(identifier);
		// get current the identifier, maybe it is overwritten
		String actualIdentifier = JavaBiteCodeGenerator.getIdentifier(identifier);
		JavaBiteCodeGenerator.temporaryResultOutputs.push(actualIdentifier);
		JavaBiteCodeGenerator.temporaryTypes.push(identifierType);
	}
}
