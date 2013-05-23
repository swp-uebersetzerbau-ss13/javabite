package swp_compiler_ss13.javabite.codegen.alternative;


import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode.BinaryOperator;
import swp_compiler_ss13.common.ast.nodes.unary.UnaryExpressionNode.UnaryOperator;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type.Kind;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJB;

public class QuadrupleFactoryJb {
	
	public static Quadruple generateDeclaration(IdentifierData data) throws IntermediateCodeGeneratorException {
		//TODO: boolean, string, struct and array declaration has to be added
		switch (data.getType().getKind()) {
		case LONG:
			return new QuadrupleJB(Operator.DECLARE_LONG, Quadruple.EmptyArgument, Quadruple.EmptyArgument, data.getIdentifier());
		case DOUBLE:
			return new QuadrupleJB(Operator.DECLARE_DOUBLE, Quadruple.EmptyArgument, Quadruple.EmptyArgument, data.getIdentifier());
		default:
			throw new IntermediateCodeGeneratorException("Unsupported type: " + data.getType().getKind());
		}
	}
	
	public static Quadruple generatCast(IdentifierData newId,
			IdentifierData oldId) throws IntermediateCodeGeneratorException {
		if (newId.getType().getKind() == Kind.LONG) {
			switch (oldId.getType().getKind()) {
			case DOUBLE:
				return new QuadrupleJB(Operator.LONG_TO_DOUBLE, oldId.getIdentifier(), Quadruple.EmptyArgument, newId.getIdentifier());
			default:
				break;
			}
		} else if (newId.getType().getKind() == Kind.DOUBLE) {
			switch (oldId.getType().getKind()) {
			case LONG:
				return new QuadrupleJB(Operator.DOUBLE_TO_LONG, oldId.getIdentifier(), Quadruple.EmptyArgument, newId.getIdentifier());
			default:
				break;
			}
		} 
			
		throw new IntermediateCodeGeneratorException("Unsupported cast request for types: " + oldId.getType().getKind() + " -> " + newId.getType().getKind());
	}

	public static Quadruple generateReturn(IdentifierData data) throws IntermediateCodeGeneratorException {
		if (data.getType().getKind() != Kind.LONG)
			throw new IntermediateCodeGeneratorException("Return works only with long variables");
		
		return new QuadrupleJB(Operator.RETURN, data.getIdentifier(), Quadruple.EmptyArgument, Quadruple.EmptyArgument);
	}

	public static Quadruple generateAssignment(IdentifierData leftData, IdentifierData rightData) throws IntermediateCodeGeneratorException {
		if (leftData.getType().getKind() != rightData.getType().getKind())
			throw new IntermediateCodeGeneratorException("Cast before value assignment missing");
		
		switch (leftData.getType().getKind()) {
		case LONG:
			return new QuadrupleJB(Operator.ASSIGN_LONG, rightData.getIdentifier(), Quadruple.EmptyArgument, leftData.getIdentifier());
		case DOUBLE:
			return new QuadrupleJB(Operator.ASSIGN_DOUBLE, rightData.getIdentifier(), Quadruple.EmptyArgument, leftData.getIdentifier());
		default:
			throw new IntermediateCodeGeneratorException("Unsupported type: " + leftData.getType().getKind());
		}
	}

	/**
	 * generates a Quadruple for a arithmetic unary operation
	 * @param unaryOperator 
	 * @param newData
	 * @param oldData
	 * @return
	 * @throws IntermediateCodeGeneratorException
	 */
	public static Quadruple generateArithmeticUnary(UnaryOperator unaryOperator, IdentifierData newData, IdentifierData oldData) throws IntermediateCodeGeneratorException {
		if (unaryOperator != UnaryOperator.MINUS)
			throw new IntermediateCodeGeneratorException("Unsupported unary operator for arithmetics: " + unaryOperator);
		
		switch (newData.getType().getKind()) {
		case LONG:
			return new QuadrupleJB(Operator.SUB_LONG, "#0", oldData.getIdentifier(), newData.getIdentifier());
		case DOUBLE:
			return new QuadrupleJB(Operator.SUB_DOUBLE, "#0.0", oldData.getIdentifier(), newData.getIdentifier());
		default:
			throw new IntermediateCodeGeneratorException("Unsupported type: " + newData.getType().getKind());
		}
	}

	public static Quadruple generateArithmeticBinary(BinaryOperator binaryOperator, IdentifierData leftData,
			IdentifierData rightData, IdentifierData newData) throws IntermediateCodeGeneratorException {
		if (newData.getType().getKind() == Kind.LONG) {
			switch (binaryOperator) {
			case ADDITION:
				return new QuadrupleJB(Operator.ADD_LONG, leftData.getIdentifier(), rightData.getIdentifier(), newData.getIdentifier());
			case SUBSTRACTION:
				return new QuadrupleJB(Operator.SUB_LONG, leftData.getIdentifier(), rightData.getIdentifier(), newData.getIdentifier());
			case MULTIPLICATION:
				return new QuadrupleJB(Operator.MUL_LONG, leftData.getIdentifier(), rightData.getIdentifier(), newData.getIdentifier());
			case DIVISION:
				return new QuadrupleJB(Operator.DIV_LONG, leftData.getIdentifier(), rightData.getIdentifier(), newData.getIdentifier());
			default:
				break;
			}
		} else if (newData.getType().getKind() == Kind.DOUBLE) {
			switch (binaryOperator) {
			case ADDITION:
				return new QuadrupleJB(Operator.ADD_DOUBLE, leftData.getIdentifier(), rightData.getIdentifier(), newData.getIdentifier());
			case SUBSTRACTION:
				return new QuadrupleJB(Operator.SUB_DOUBLE, leftData.getIdentifier(), rightData.getIdentifier(), newData.getIdentifier());
			case MULTIPLICATION:
				return new QuadrupleJB(Operator.MUL_DOUBLE, leftData.getIdentifier(), rightData.getIdentifier(), newData.getIdentifier());
			case DIVISION:
				return new QuadrupleJB(Operator.DIV_DOUBLE, leftData.getIdentifier(), rightData.getIdentifier(), newData.getIdentifier());
			default:
				break;
			}
			
		}
		
		throw new IntermediateCodeGeneratorException("Unsupported binary operation");
	}
}
