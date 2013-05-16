package swp_compiler_ss13.javabite.codegen.alternative.converters;


import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type.Kind;
import swp_compiler_ss13.javabite.codegen.alternative.CastingAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.alternative.IdentifierData;
import swp_compiler_ss13.javabite.codegen.alternative.QuadrupleFactoryJb;

public class ArithmeticBinaryExpressionNodeConverter extends CastingAst2CodeConverter {

	@Override
	public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof ArithmeticBinaryExpressionNode))
			throw new IntermediateCodeGeneratorException();
		ArithmeticBinaryExpressionNode binaryNode = (ArithmeticBinaryExpressionNode) node;
		
		// process content of left value node and get result identifier
		icg.processNode(binaryNode.getLeftValue());
		IdentifierData leftData = icg.popIdentifierData();
		
		// process content of right value node and get result identifier
		icg.processNode(binaryNode.getRightValue());
		IdentifierData rightData = icg.popIdentifierData();
		
		// check if types matches
		if (leftData.getType().getKind() != rightData.getType().getKind()) {
			// cast is needed -> its arithmetic so one has to be double and one long
			// we always upcast to double
			if (leftData.getType().getKind() == Kind.LONG) {
				leftData = cast(rightData.getType(), leftData);
			} else {
				rightData = cast(leftData.getType(), rightData);
			}
		}
		
		// generate a new identifier for result, add operation to TAC and push result identifier onto stack for callee
		IdentifierData newData = icg.generateTempIdentifier(leftData.getType());
		icg.addQuadruple(QuadrupleFactoryJb.generateArithmeticBinary(binaryNode.getOperator(), leftData, rightData, newData));
		icg.pushIdentifierData(newData);
	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.ArithmeticBinaryExpressionNode;
	}
}
