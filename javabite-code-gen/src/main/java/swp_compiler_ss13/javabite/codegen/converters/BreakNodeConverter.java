package swp_compiler_ss13.javabite.codegen.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.binary.LoopNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BreakNode;
import swp_compiler_ss13.common.ast.nodes.ternary.BranchNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.AbstractAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.IdentifierData;
import swp_compiler_ss13.javabite.codegen.QuadrupleFactoryJb;

public class BreakNodeConverter extends AbstractAst2CodeConverter {

	@Override
	public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof BreakNode))
			throw new IntermediateCodeGeneratorException();
		
		icg.addQuadruple(QuadrupleFactoryJb.generateBranch(icg.getCurrentBreakLabel()));
	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.BreakNode;
	}
}