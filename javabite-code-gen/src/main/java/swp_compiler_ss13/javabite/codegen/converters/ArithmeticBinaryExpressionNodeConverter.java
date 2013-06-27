package swp_compiler_ss13.javabite.codegen.converters;


import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type.Kind;
import swp_compiler_ss13.common.types.primitive.StringType;
import swp_compiler_ss13.javabite.codegen.CastingAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.IdentifierData;
import swp_compiler_ss13.javabite.codegen.QuadrupleFactoryJb;

public class ArithmeticBinaryExpressionNodeConverter extends CastingAst2CodeConverter {

	//TODO: tests:
	// check
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
		
		// first check if any of the types is a string and cast the other to string
		// if non is a string it has to be LONG or DOUBLE if the arn't both of same
		// type we have to cast to double
		if (leftData.getType().getKind() == Kind.STRING || rightData.getType().getKind() == Kind.STRING) {
			if (leftData.getType().getKind() != Kind.STRING)
				leftData = cast(new StringType(0L), leftData);

			if (rightData.getType().getKind() != Kind.STRING)
				rightData = cast(new StringType(0L), rightData);
		} else if (leftData.getType().getKind() != rightData.getType().getKind()) {
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
