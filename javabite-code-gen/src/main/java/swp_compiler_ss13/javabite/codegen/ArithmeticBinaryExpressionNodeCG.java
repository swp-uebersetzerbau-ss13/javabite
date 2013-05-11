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
			
		JavaBiteCodeGenerator.differentiateNode((ASTNodeJb)node.getLeftValue());
		JavaBiteCodeGenerator.differentiateNode((ASTNodeJb) node.getRightValue());
	
		
		Type rightType = JavaBiteCodeGenerator.temporaryTypes.pop();
		Type leftType = JavaBiteCodeGenerator.temporaryTypes.pop();
	
		String rightValue = JavaBiteCodeGenerator.temporaryResultOutputs.pop();
		String leftValue = JavaBiteCodeGenerator.temporaryResultOutputs.pop();
		// operations for long type arguments
		if (leftType.getKind() == Kind.LONG && rightType.getKind() == Kind.LONG) {
			// long binary operations
			String temp = JavaBiteCodeGenerator.createAndAddTemporaryIdentifier(new LongType());
			Quadruple tac = QuadrupleFactory.longArithmeticBinaryOperation(node.getOperator(), leftValue, rightValue,temp);
			JavaBiteCodeGenerator.quadruples.add(tac);
			JavaBiteCodeGenerator.temporaryResultOutputs.push(temp);
			JavaBiteCodeGenerator.temporaryTypes.push(new LongType());
		} 
		// if one of the arguments or both of them are double 
		else {
			// if the left argument is long, then cast it to double
			String castLeft;
			if (leftType.getKind() == Kind.LONG) {
				// cast the left value to double
				castLeft = JavaBiteCodeGenerator.createAndAddTemporaryIdentifier(new DoubleType());
				Quadruple cleft = QuadrupleFactory.castLongToDouble(leftValue, castLeft);
				JavaBiteCodeGenerator.quadruples.add(cleft);
			}
			// if not long, then use its current value
			else{
				castLeft = leftValue;
			}
			// if the right argument is long, then cast it to double
			String castRight;
			if (rightType.getKind() == Kind.LONG) {
				// cast the right value to double
				castRight = JavaBiteCodeGenerator.createAndAddTemporaryIdentifier(new DoubleType());
				Quadruple cright = QuadrupleFactory.castLongToDouble(rightValue, castRight);
				JavaBiteCodeGenerator.quadruples.add(cright);
			}
			// if not long, then use its current value
			else{
				castRight = rightValue;
			}
			
			// double binary operation
			String temp = JavaBiteCodeGenerator.createAndAddTemporaryIdentifier(new DoubleType());
			Quadruple tac = QuadrupleFactory.doubleArithmeticBinaryOperation(node.getOperator(), castLeft, castRight,temp);
			JavaBiteCodeGenerator.quadruples.add(tac);
			JavaBiteCodeGenerator.temporaryResultOutputs.push(temp);
			JavaBiteCodeGenerator.temporaryTypes.push(new DoubleType());
		}
	}
}
