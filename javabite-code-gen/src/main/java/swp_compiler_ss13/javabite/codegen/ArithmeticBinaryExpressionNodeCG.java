package swp_compiler_ss13.javabite.codegen;

import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.common.types.Type.Kind;
import swp_compiler_ss13.common.types.primitive.DoubleType;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.javabite.ast.ASTNodeJb;

public class ArithmeticBinaryExpressionNodeCG {

	/**
	* Binary Arithmetic Operation
	*
	* @param node
	* @throws IntermediateCodeGeneratorException
	*/
	public void convert(ArithmeticBinaryExpressionNode node)throws IntermediateCodeGeneratorException {
			
		IntermediateCodeGeneratorJb.differentiateNode((ASTNodeJb)node.getLeftValue());
		IntermediateCodeGeneratorJb.differentiateNode((ASTNodeJb) node.getRightValue());
	
		
		Type rightType = IntermediateCodeGeneratorJb.temporaryTypes.pop();
		Type leftType = IntermediateCodeGeneratorJb.temporaryTypes.pop();
	
		String rightValue = IntermediateCodeGeneratorJb.temporaryResultOutputs.pop();
		String leftValue = IntermediateCodeGeneratorJb.temporaryResultOutputs.pop();
		// operations for long type arguments
		if (leftType.getKind() == Kind.DOUBLE || rightType.getKind() == Kind.DOUBLE) {
			// if the left argument is long, then cast it to double
			String left;
			if (leftType.getKind() == Kind.LONG) {
				// cast the left value to double
				left = IntermediateCodeGeneratorJb.createAndAddTemporaryIdentifier(new DoubleType());
				Quadruple quadruple = QuadrupleFactoryJb.generateCastLongToDouble(leftValue, left);
				IntermediateCodeGeneratorJb.quadruples.add(quadruple);
			}
			// if not long, then use its current value
			else{
				left = leftValue;
			}
			// if the right argument is long, then cast it to double
			String right;
			if (rightType.getKind() == Kind.LONG) {
				// cast the right value to double
				right = IntermediateCodeGeneratorJb.createAndAddTemporaryIdentifier(new DoubleType());
				Quadruple quadruple = QuadrupleFactoryJb.generateCastLongToDouble(rightValue, right);
				IntermediateCodeGeneratorJb.quadruples.add(quadruple);
			}
			// if not long, then use its current value
			else{
				right = rightValue;
			}
			
			// double binary operation
			String tmpId = IntermediateCodeGeneratorJb.createAndAddTemporaryIdentifier(new DoubleType());
			Quadruple quadruple = QuadrupleFactoryJb.generateDoubleAritmeticBinOp(node.getOperator(), left, right,tmpId);
			IntermediateCodeGeneratorJb.quadruples.add(quadruple);
			IntermediateCodeGeneratorJb.temporaryResultOutputs.push(tmpId);
			IntermediateCodeGeneratorJb.temporaryTypes.push(new DoubleType());
		
		} 
		// if one of the arguments or both of them are double 
		else {
			// long binary operations
			String tmpId = IntermediateCodeGeneratorJb.createAndAddTemporaryIdentifier(new LongType());
			Quadruple quadruple = QuadrupleFactoryJb.generateLongArithmeticBinOp(node.getOperator(), leftValue, rightValue,tmpId);
			IntermediateCodeGeneratorJb.quadruples.add(quadruple);
			IntermediateCodeGeneratorJb.temporaryResultOutputs.push(tmpId);
			IntermediateCodeGeneratorJb.temporaryTypes.push(new LongType());
		}
	}
}
