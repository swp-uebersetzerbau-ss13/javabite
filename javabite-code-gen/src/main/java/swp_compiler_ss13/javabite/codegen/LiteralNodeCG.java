package swp_compiler_ss13.javabite.codegen;

import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;

public class LiteralNodeCG{

	public void convert(LiteralNode node) throws IntermediateCodeGeneratorException {
		String literal = node.getLiteral();
		Type type = node.getLiteralType();
		JavaBiteCodeGenerator.temporaryTypes.push(type);
		switch (type.getKind()) {
			case LONG:
				JavaBiteCodeGenerator.temporaryResultOutputs.push("#" + literal);
				break;
			case DOUBLE:
				JavaBiteCodeGenerator.temporaryResultOutputs.push("#" + literal);
				break;
			case STRING:
				JavaBiteCodeGenerator.temporaryResultOutputs.push("#\"" + literal + "\"");
				break;
			default:
				throw new IntermediateCodeGeneratorException("The type " + node.getLiteralType().toString()
						+ " for literal node is not supported!");
		}
	}
}
