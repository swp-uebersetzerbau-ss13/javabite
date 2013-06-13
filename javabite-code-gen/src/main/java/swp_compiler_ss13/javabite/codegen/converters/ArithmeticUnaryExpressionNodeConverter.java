package swp_compiler_ss13.javabite.codegen.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.unary.ArithmeticUnaryExpressionNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.CastingAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.IdentifierData;
import swp_compiler_ss13.javabite.codegen.QuadrupleFactoryJb;

public class ArithmeticUnaryExpressionNodeConverter extends CastingAst2CodeConverter {

	@Override
	public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof ArithmeticUnaryExpressionNode))
			throw new IntermediateCodeGeneratorException();
		ArithmeticUnaryExpressionNode unaryNode = (ArithmeticUnaryExpressionNode) node;
		
		// process right node and get identifier containing result
		icg.processNode(unaryNode.getRightValue());
		IdentifierData oldData = icg.popIdentifierData();
		
		// generate new identifier for result, add TAC and push new identifier for callee
		IdentifierData newData = icg.generateTempIdentifier(oldData.getType());
		icg.addQuadruple(QuadrupleFactoryJb.generateArithmeticUnary(unaryNode.getOperator(), newData, oldData));
		icg.pushIdentifierData(newData);
	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.ArithmeticUnaryExpressionNode;
	}
}
