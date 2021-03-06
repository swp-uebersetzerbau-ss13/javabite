package swp_compiler_ss13.javabite.codegen.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.binary.LoopNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.AbstractAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.IdentifierData;
import swp_compiler_ss13.javabite.codegen.QuadrupleFactoryJb;

public class WhileNodeConverter extends AbstractAst2CodeConverter {

	@Override
	public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof LoopNode))
			throw new IntermediateCodeGeneratorException();
		LoopNode loopNode = (LoopNode) node;

		String startLabel = icg.getNewLabel();
		String trueLabel = icg.getNewLabel();
		String endLabel = icg.getNewLabel();
		
		icg.enterLoop(endLabel);
		
		icg.addQuadruple(QuadrupleFactoryJb.generateLabel(startLabel));
		icg.processNode(loopNode.getCondition());
		IdentifierData result = icg.popIdentifierData();
		boolean isConditionConstant = result.getIdentifier().startsWith("#");
		boolean isTrue = result.getIdentifier().equalsIgnoreCase("#true");
		if (!isConditionConstant || isTrue) {
			if (!isConditionConstant) {
				icg.addQuadruple(QuadrupleFactoryJb.generateConditionalBranch(result, trueLabel, endLabel));
				icg.addQuadruple(QuadrupleFactoryJb.generateLabel(trueLabel));
			} 
			icg.processNode(loopNode.getLoopBody());
			icg.addQuadruple(QuadrupleFactoryJb.generateBranch(startLabel));
			
			icg.addQuadruple(QuadrupleFactoryJb.generateLabel(endLabel));
		}
		icg.leaveLoop();
	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.WhileNode;
	}
}