package swp_compiler_ss13.javabite.codegen;


import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode.BinaryOperator;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJB;



/**
* This factory for generating quadruples for the given instructions
*/
public class QuadrupleFactoryJb {
	
	/**
	 * generate a quadruple for declaration
	 * @param identifier
	 * @param type
	 * @return
	 * @throws IntermediateCodeGeneratorException
	 */
	public static Quadruple declaration(String identifier, Type type) throws IntermediateCodeGeneratorException {
		switch (type.getKind()) {
		case DOUBLE:
			return new QuadrupleJB(Operator.DECLARE_DOUBLE, Quadruple.EmptyArgument, Quadruple.EmptyArgument, identifier);
		case LONG:
			return new QuadrupleJB(Operator.DECLARE_LONG, Quadruple.EmptyArgument, Quadruple.EmptyArgument, identifier);
		default:
			throw new IntermediateCodeGeneratorException("Type: "+type.toString()+" is not supported!");
		}
	}

	/**
	 * Generates a quadruple for binary operations as long type 
	 * @param operator
	 * @param arg1
	 * @param arg2
	 * @param res
	 * @return
	 * @throws IntermediateCodeGeneratorException
	 */
	public static Quadruple generateLongArithmeticBinOp(BinaryOperator operator, String arg1, String arg2, String res)
	throws IntermediateCodeGeneratorException {
		switch (operator) {
		case ADDITION:
			return new QuadrupleJB(Operator.ADD_LONG, arg1, arg2, res);
		case DIVISION:
			return new QuadrupleJB(Operator.DIV_LONG, arg1, arg2, res);
		case MULTIPLICATION:
			return new QuadrupleJB(Operator.MUL_LONG, arg1, arg2, res);
		case SUBSTRACTION:
			return new QuadrupleJB(Operator.SUB_LONG, arg1, arg2, res);
		default:
			throw new IntermediateCodeGeneratorException("Operator: "+operator.toString()+ " is not supported!");
		}
	}

	/**
	 * Generates a quadruple for binary operations as double type
	 * @param operator
	 * @param arg1
	 * @param arg2
	 * @param res
	 * @return
	 * @throws IntermediateCodeGeneratorException
	 */
	public static Quadruple generateDoubleAritmeticBinOp(BinaryOperator operator, String arg1, String arg2,
	String res) throws IntermediateCodeGeneratorException {
		switch (operator) {
		case ADDITION:
			return new QuadrupleJB(Operator.ADD_DOUBLE, arg1, arg2, res);
		case DIVISION:
			return new QuadrupleJB(Operator.DIV_DOUBLE, arg1, arg2, res);
		case MULTIPLICATION:
			return new QuadrupleJB(Operator.MUL_DOUBLE, arg1, arg2, res);
		case SUBSTRACTION:
			return new QuadrupleJB(Operator.SUB_DOUBLE, arg1, arg2, res);
		default:
			throw new IntermediateCodeGeneratorException("Operator: "+operator.toString()+ " is not supported!");
		}
	}

	/**
	 * Generates a quadruple for casting from long to double
	 * @param from
	 * @param to
	 * @return
	 */
	public static Quadruple generateCastLongToDouble(String from, String to) {
		return new QuadrupleJB(Operator.LONG_TO_DOUBLE, from, Quadruple.EmptyArgument, to);
	}

	/**
	 * Generates a quadruple for casting from double to long
	 * @param from
	 * @param to
	 * @return
	 */
	public static Quadruple generateCastDoubleToLong(String from, String to) {
		return new QuadrupleJB(Operator.DOUBLE_TO_LONG, from, Quadruple.EmptyArgument, to);
	}

	/**
	 * Generates a quadruple for assignment
	 * @param type
	 * @param leftSide
	 * @param rightSide
	 * @return
	 * @throws IntermediateCodeGeneratorException
	 */
	public static Quadruple generateAssignment(Type type, String leftSide, String rightSide) throws IntermediateCodeGeneratorException {
		switch (type.getKind()) {
		case DOUBLE:
			return new QuadrupleJB(Operator.ASSIGN_DOUBLE, leftSide, Quadruple.EmptyArgument, rightSide);
		case LONG:
			return new QuadrupleJB(Operator.ASSIGN_LONG, leftSide, Quadruple.EmptyArgument, rightSide);
		default:
			throw new IntermediateCodeGeneratorException("The assignment: "+type+" "+leftSide+" "+" "+rightSide+" is not supported!");
		}
	}

	/**
	 * Generates a quadruple for unary minus
	 * @param type
	 * @param value
	 * @param result
	 * @return
	 * @throws IntermediateCodeGeneratorException
	 */
	public static Quadruple genaerateUnaryMinus(Type type, String value, String result) throws IntermediateCodeGeneratorException {
		switch (type.getKind()) {
		case DOUBLE:
			return new QuadrupleJB(Operator.SUB_DOUBLE, "#0.0", value, result);
		case LONG:
			return new QuadrupleJB(Operator.SUB_LONG, "#0", value, result);
		default:
			throw new IntermediateCodeGeneratorException("Unsupport assignment type");
		}
	}

	/**
	 * Generates a quadruple for Return-Statement
	 * @param identifier
	 * @return
	 */
	public static Quadruple generateReturn(String identifier) {
		return new QuadrupleJB(Quadruple.Operator.RETURN, identifier, Quadruple.EmptyArgument,
				Quadruple.EmptyArgument);
	}
}