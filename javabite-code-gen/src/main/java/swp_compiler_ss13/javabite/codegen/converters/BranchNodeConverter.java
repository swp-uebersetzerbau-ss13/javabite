package swp_compiler_ss13.javabite.codegen.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.ternary.BranchNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.AbstractAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.IdentifierData;
import swp_compiler_ss13.javabite.codegen.QuadrupleFactoryJb;

public class BranchNodeConverter extends AbstractAst2CodeConverter {

	@Override
	public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof BranchNode))
			throw new IntermediateCodeGeneratorException();
		BranchNode branchNode = (BranchNode) node;

		boolean hasFalseBlock = branchNode.getStatementNodeOnFalse() != null;

		String trueLabel = icg.getNewLabel();
		String falseLabel = null;
		if (hasFalseBlock) {
			falseLabel = icg.getNewLabel();
		}
		String endLabel = icg.getNewLabel();

		icg.processNode(branchNode.getCondition());
		IdentifierData result = icg.popIdentifierData();
		Boolean conditionIsConstant = result.getIdentifier().startsWith("#");
		if (conditionIsConstant) {
			if (result.getIdentifier().equalsIgnoreCase("#true")) {
				icg.processNode(branchNode.getStatementNodeOnTrue());
			} else {
				if (hasFalseBlock) {
					icg.processNode(branchNode.getStatementNodeOnFalse());
				}
			}
		} else if (hasFalseBlock) {
			icg.addQuadruple(QuadrupleFactoryJb.generateConditionalBranch(
					result, trueLabel, falseLabel));
			icg.addQuadruple(QuadrupleFactoryJb.generateLabel(trueLabel));
			icg.processNode(branchNode.getStatementNodeOnTrue());
			icg.addQuadruple(QuadrupleFactoryJb.generateBranch(endLabel));
			icg.addQuadruple(QuadrupleFactoryJb.generateLabel(falseLabel));
			icg.processNode(branchNode.getStatementNodeOnFalse());
			icg.addQuadruple(QuadrupleFactoryJb.generateLabel(endLabel));
		} else {
			icg.addQuadruple(QuadrupleFactoryJb.generateConditionalBranch(
					result, trueLabel, endLabel));
			icg.addQuadruple(QuadrupleFactoryJb.generateLabel(trueLabel));
			icg.processNode(branchNode.getStatementNodeOnTrue());
			icg.addQuadruple(QuadrupleFactoryJb.generateLabel(endLabel));
		}
	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.BranchNode;
	}
}