package swp_compiler_ss13.javabite.codegen;

import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.common.types.Type.Kind;
import swp_compiler_ss13.common.types.primitive.DoubleType;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.javabite.ast.ASTNodeJb;

/**
 * 
 * @author Alpin Sahin und Florian Mercks
 *
 */
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
	
		
		Type rightType = JavaBiteCodeGenerator.intermediateTypes.pop();
		Type leftType = JavaBiteCodeGenerator.intermediateTypes.pop();
	
		String rightValue = JavaBiteCodeGenerator.intermediateResults.pop();
		String leftValue = JavaBiteCodeGenerator.intermediateResults.pop();
	
		if (leftType.getKind() == Kind.LONG && rightType.getKind() == Kind.LONG) {
			// Just long types
			String temp = JavaBiteCodeGenerator.createAndSaveTemporaryIdentifier(new LongType());
			Quadruple tac = QuadrupleFactory.longArithmeticBinaryOperation(node.getOperator(), leftValue, rightValue,temp);
			JavaBiteCodeGenerator.quadruples.add(tac);
			JavaBiteCodeGenerator.intermediateResults.push(temp);
			JavaBiteCodeGenerator.intermediateTypes.push(new LongType());
		} else {
			// double types or mix of double and long
			String castLeft = leftValue;
			if (leftType.getKind() == Kind.LONG) {
			// cast the left value to double
			castLeft = JavaBiteCodeGenerator.createAndSaveTemporaryIdentifier(new DoubleType());
			Quadruple cleft = QuadrupleFactory.castLongToDouble(leftValue, castLeft);
			JavaBiteCodeGenerator.quadruples.add(cleft);
		}
	
		String castRight = rightValue;
		if (rightType.getKind() == Kind.LONG) {
			// cast the right value to double
			castRight = JavaBiteCodeGenerator.createAndSaveTemporaryIdentifier(new DoubleType());
			Quadruple cright = QuadrupleFactory.castLongToDouble(rightValue, castRight);
			JavaBiteCodeGenerator.quadruples.add(cright);
		}
			// double binary operation
			String temp = JavaBiteCodeGenerator.createAndSaveTemporaryIdentifier(new DoubleType());
			Quadruple tac = QuadrupleFactory.doubleArithmeticBinaryOperation(node.getOperator(), castLeft, castRight,temp);
			JavaBiteCodeGenerator.quadruples.add(tac);
			JavaBiteCodeGenerator.intermediateResults.push(temp);
			JavaBiteCodeGenerator.intermediateTypes.push(new DoubleType());
		}
	}
}
