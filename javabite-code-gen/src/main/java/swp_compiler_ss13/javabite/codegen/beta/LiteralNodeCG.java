package swp_compiler_ss13.javabite.codegen.beta;

import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;

public class LiteralNodeCG{

	public void convert(LiteralNode node) throws IntermediateCodeGeneratorException {
		String literal = node.getLiteral();
		Type type = node.getLiteralType();
		IRCodeGenerator.intermediateTypes.push(type);
		switch (type.getKind()) {
			case DOUBLE:
			case LONG:
				IRCodeGenerator.intermediateResults.push("#" + literal);
				break;
			case STRING:
				IRCodeGenerator.intermediateResults.push("#\"" + literal + "\"");
				break;
			default:
				throw new IntermediateCodeGeneratorException("Literal node of type " + node.getLiteralType().toString()
						+ " is not supported");
		}
	}
}
