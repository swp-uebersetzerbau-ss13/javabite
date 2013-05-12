package swp_compiler_ss13.javabite.codegen;

import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;

public class LiteralNodeCG{

	public void convert(LiteralNode node) throws IntermediateCodeGeneratorException {
		// get literal and its type
		String literal = node.getLiteral();
		Type type = node.getLiteralType();
		// push the type to temporaryTypes
		IntermediateCodeGeneratorJb.temporaryTypes.push(type);
		// handle the type for long, double or string
		switch (type.getKind()) {
			case LONG:
				IntermediateCodeGeneratorJb.temporaryResultOutputs.push("#" + literal);
				break;
			case DOUBLE:
				IntermediateCodeGeneratorJb.temporaryResultOutputs.push("#" + literal);
				break;
			case STRING:
				IntermediateCodeGeneratorJb.temporaryResultOutputs.push("#\"" + literal + "\"");
				break;
			default:
				throw new IntermediateCodeGeneratorException("The type " + node.getLiteralType().toString()
						+ " for literal node is not supported!");
		}
	}
}
