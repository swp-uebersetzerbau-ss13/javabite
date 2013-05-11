package swp_compiler_ss13.javabite.codegen;


import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode.BinaryOperator;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;



/**
* This factory generates quadruples for the given instructions
*
* @author "Frank Zechert"
* @version 1
*/
public class QuadrupleFactory {

	
	
	
	/**
	* create a quadruple to represent the variable declaration
	*
	* @param id
	* The name of the declared variable
	* @param type
	* The type of the declared variable
	* @return The quadruple representing the declaration
	* @throws IntermediateCodeGeneratorException
	* Thrown if an unknown type is declared
	*/
	public static Quadruple declaration(String id, Type type) throws IntermediateCodeGeneratorException {
		switch (type.getKind()) {
		case DOUBLE:
			return new QuadrupleImpl(Operator.DECLARE_DOUBLE, Quadruple.EmptyArgument, Quadruple.EmptyArgument, id);
		case LONG:
			return new QuadrupleImpl(Operator.DECLARE_LONG, Quadruple.EmptyArgument, Quadruple.EmptyArgument, id);
		default:
			throw new IntermediateCodeGeneratorException("Unsupported type " + type.toString());
		}
	}

	/**
	* Create a new binaryOperator Quadruple for Arguments of Type long
	*
	* @param operator
	* The binary operator
	* @param arg1
	* argument 1
	* @param arg2
	* argument 2
	* @param res
	* result
	* @return the quadruple
	* @throws IntermediateCodeGeneratorException
	* unsupported operator
	*/
	public static Quadruple longArithmeticBinaryOperation(BinaryOperator operator, String arg1, String arg2, String res)
	throws IntermediateCodeGeneratorException {
		switch (operator) {
		case ADDITION:
			return new QuadrupleImpl(Operator.ADD_LONG, arg1, arg2, res);
		case DIVISION:
			return new QuadrupleImpl(Operator.DIV_LONG, arg1, arg2, res);
		case MULTIPLICATION:
			return new QuadrupleImpl(Operator.MUL_LONG, arg1, arg2, res);
		case SUBSTRACTION:
			return new QuadrupleImpl(Operator.SUB_LONG, arg1, arg2, res);
		default:
			throw new IntermediateCodeGeneratorException("Unsupported binary operator " + operator.toString());
		}
	}

	/**
	* Create a new binaryOperator Quadruple for Arguments of Type double
	*
	* @param operator
	* The binary operator
	* @param arg1
	* argument 1
	* @param arg2
	* argument 2
	* @param res
	* result
	* @return the quadruple
	* @throws IntermediateCodeGeneratorException
	* unsupported operator
	*/
	public static Quadruple doubleArithmeticBinaryOperation(BinaryOperator operator, String arg1, String arg2,
	String res) throws IntermediateCodeGeneratorException {
		switch (operator) {
		case ADDITION:
			return new QuadrupleImpl(Operator.ADD_DOUBLE, arg1, arg2, res);
		case DIVISION:
			return new QuadrupleImpl(Operator.DIV_DOUBLE, arg1, arg2, res);
		case MULTIPLICATION:
			return new QuadrupleImpl(Operator.MUL_DOUBLE, arg1, arg2, res);
		case SUBSTRACTION:
			return new QuadrupleImpl(Operator.SUB_DOUBLE, arg1, arg2, res);
		default:
			throw new IntermediateCodeGeneratorException("Unsupported binary operator " + operator.toString());
		}
	}

	/**
	* Create a new cast from long to double
	*
	* @param from
	* The long to cast from
	* @param to
	* The double to cast to
	* @return The tac quadruple
	*/
	public static Quadruple castLongToDouble(String from, String to) {
		return new QuadrupleImpl(Operator.LONG_TO_DOUBLE, from, Quadruple.EmptyArgument, to);
	}

	/**
	* Create a new cast from double to long
	*
	* @param from
	* The double to cast from
	* @param to
	* The long to cast to
	* @return The tac quadurple
	*/
	public static Quadruple castDoubleToLong(String from, String to) {
		return new QuadrupleImpl(Operator.DOUBLE_TO_LONG, from, Quadruple.EmptyArgument, to);
	}

	/**
	* Create a new assignment in TAC
	*
	* @param typeOfid
	* The type of the variable to assign the value to
	* @param from
	* The value to assign to
	* @param to
	* The variable to hold the assigned value
	* @return The tac quadruple
	* @throws IntermediateCodeGeneratorException
	* something went wrong.
	*/
	public static Quadruple assign(Type typeOfid, String from, String to) throws IntermediateCodeGeneratorException {
		switch (typeOfid.getKind()) {
		case DOUBLE:
			return new QuadrupleImpl(Operator.ASSIGN_DOUBLE, from, Quadruple.EmptyArgument, to);
		case LONG:
			return new QuadrupleImpl(Operator.ASSIGN_LONG, from, Quadruple.EmptyArgument, to);
		default:
			throw new IntermediateCodeGeneratorException("Unsupport assignment type");
		}
	}

	/**
	* Generate unary minus quadruples for long or double
	*
	* @param typeOfId
	* The type of the identifier (long or double)
	* @param from
	* The variable to calculate minus from
	* @param to
	* The result variable
	* @return The quadruple for unary minus
	* @throws IntermediateCodeGeneratorException
	* something went wrong
	*/
	public static Quadruple unaryMinus(Type typeOfId, String from, String to) throws IntermediateCodeGeneratorException {
		switch (typeOfId.getKind()) {
		case DOUBLE:
			return new QuadrupleImpl(Operator.SUB_DOUBLE, "#0.0", from, to);
		case LONG:
			return new QuadrupleImpl(Operator.SUB_LONG, "#0", from, to);
		default:
			throw new IntermediateCodeGeneratorException("Unsupport assignment type");
		}
	}
}