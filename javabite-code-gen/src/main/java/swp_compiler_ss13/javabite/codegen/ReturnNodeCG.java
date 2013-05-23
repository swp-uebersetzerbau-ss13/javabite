package swp_compiler_ss13.javabite.codegen;

import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.ReturnNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.ast.ASTNodeJb;

public class ReturnNodeCG {
	public void convert(ReturnNode node) throws IntermediateCodeGeneratorException {
		IdentifierNode idNode = node.getRightValue();
		IntermediateCodeGeneratorJb.differentiateNode((ASTNodeJb)idNode);
		String rightValue = IntermediateCodeGeneratorJb.temporaryResultOutputs.pop();
		IntermediateCodeGeneratorJb.quadruples.add(QuadrupleFactoryJb.generateReturn(rightValue));
	}
}
