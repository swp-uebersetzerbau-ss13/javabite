package swp_compiler_ss13.javabite.codegen.beta;

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
			
		IRCodeGenerator.differentiateNode((ASTNodeJb)node.getLeftValue());
		IRCodeGenerator.differentiateNode((ASTNodeJb) node.getRightValue());
	
			
		Type rightType = IRCodeGenerator.intermediateTypes.pop();
		Type leftType = IRCodeGenerator.intermediateTypes.pop();
	
		String rightValue = IRCodeGenerator.intermediateResults.pop();
		String leftValue = IRCodeGenerator.intermediateResults.pop();
	
		if (leftType.getKind() == Kind.LONG && rightType.getKind() == Kind.LONG) {
			// Just long types
			String temp = IRCodeGenerator.createAndSaveTemporaryIdentifier(new LongType());
			Quadruple tac = QuadrupleFactory.longArithmeticBinaryOperation(node.getOperator(), leftValue, rightValue,temp);
			IRCodeGenerator.irCode.add(tac);
			IRCodeGenerator.intermediateResults.push(temp);
			IRCodeGenerator.intermediateTypes.push(new LongType());
		} else {
			// double types or mix of double and long
			String castLeft = leftValue;
			if (leftType.getKind() == Kind.LONG) {
			// cast the left value to double
			castLeft = IRCodeGenerator.createAndSaveTemporaryIdentifier(new DoubleType());
			Quadruple cleft = QuadrupleFactory.castLongToDouble(leftValue, castLeft);
			IRCodeGenerator.irCode.add(cleft);
		}
	
		String castRight = rightValue;
		if (rightType.getKind() == Kind.LONG) {
			// cast the right value to double
			castRight = IRCodeGenerator.createAndSaveTemporaryIdentifier(new DoubleType());
			Quadruple cright = QuadrupleFactory.castLongToDouble(rightValue, castRight);
			IRCodeGenerator.irCode.add(cright);
		}
			// double binary operation
			String temp = IRCodeGenerator.createAndSaveTemporaryIdentifier(new DoubleType());
			Quadruple tac = QuadrupleFactory.doubleArithmeticBinaryOperation(node.getOperator(), castLeft, castRight,temp);
			IRCodeGenerator.irCode.add(tac);
			IRCodeGenerator.intermediateResults.push(temp);
			IRCodeGenerator.intermediateTypes.push(new DoubleType());
		}
	}
}
