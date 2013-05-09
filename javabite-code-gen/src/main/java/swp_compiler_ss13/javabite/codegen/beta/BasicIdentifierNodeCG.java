package swp_compiler_ss13.javabite.codegen.beta;

import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;

public class BasicIdentifierNodeCG {

	public void convert(BasicIdentifierNode node) throws IntermediateCodeGeneratorException{
		String identifier = node.getIdentifier();
		Type identifierType = IRCodeGenerator.currentSymbolTable.peek().lookupType(identifier);
		String actualIdentifier = IRCodeGenerator.loadIdentifier(identifier);
		System.out.println("Push "+actualIdentifier);
		IRCodeGenerator.intermediateResults.push(actualIdentifier);
		IRCodeGenerator.intermediateTypes.push(identifierType);
	}
}
