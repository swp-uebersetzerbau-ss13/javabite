package swp_compiler_ss13.javabite.codegen;

import java.util.ArrayList;
import java.util.List;

import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode.BinaryOperator;
import swp_compiler_ss13.common.ast.nodes.unary.UnaryExpressionNode.UnaryOperator;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type.Kind;
import swp_compiler_ss13.common.types.derived.ArrayType;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public class QuadrupleFactoryJb {

	public static List<Quadruple> generateDeclaration(IdentifierData data)
			throws IntermediateCodeGeneratorException {
		// TODO: struct declaration has to be added
		List<Quadruple> quadruples = new ArrayList<>();
		String id = data.getIdentifier();
		id = id!=null ? id : Quadruple.EmptyArgument;
		switch (data.getType().getKind()) {
		case LONG:
			quadruples.add(new QuadrupleJb(Operator.DECLARE_LONG,
					Quadruple.EmptyArgument, Quadruple.EmptyArgument,id));
			break;
		case DOUBLE:
			quadruples.add(new QuadrupleJb(Operator.DECLARE_DOUBLE,
					Quadruple.EmptyArgument, Quadruple.EmptyArgument,id));
			break;
		case BOOLEAN:
			quadruples.add(new QuadrupleJb(Operator.DECLARE_BOOLEAN,
					Quadruple.EmptyArgument, Quadruple.EmptyArgument,id));
			break;
		case STRING:
			quadruples.add(new QuadrupleJb(Operator.DECLARE_STRING,
					Quadruple.EmptyArgument, Quadruple.EmptyArgument,id));
			break;
		case ARRAY:
			ArrayType arrayType = (ArrayType) data.getType();
			quadruples.add(new QuadrupleJb(Operator.DECLARE_ARRAY, "#" + arrayType.getLength(), Quadruple.EmptyArgument, id));
			quadruples.addAll(generateDeclaration(new IdentifierData(null, arrayType.getInnerType())));
			break;
		default:
			throw new IntermediateCodeGeneratorException("Unsupported type: "
					+ data.getType().getKind());
		}
		
		return quadruples;
	}

	public static Quadruple generateCast(IdentifierData newId,
			IdentifierData oldId) throws IntermediateCodeGeneratorException {
		if (newId.getType().getKind() == Kind.LONG) {
			switch (oldId.getType().getKind()) {
			case DOUBLE:
				return new QuadrupleJb(Operator.DOUBLE_TO_LONG,
						oldId.getIdentifier(), Quadruple.EmptyArgument,
						newId.getIdentifier());
			default:
				break;
			}
		} else if (newId.getType().getKind() == Kind.DOUBLE) {
			switch (oldId.getType().getKind()) {
			case LONG:
				return new QuadrupleJb(Operator.LONG_TO_DOUBLE,
						oldId.getIdentifier(), Quadruple.EmptyArgument,
						newId.getIdentifier());
			default:
				break;
			}
		}

		throw new IntermediateCodeGeneratorException(
				"Unsupported cast request for types: "
						+ oldId.getType().getKind() + " -> "
						+ newId.getType().getKind());
	}

	public static Quadruple generateReturn(IdentifierData data)
			throws IntermediateCodeGeneratorException {
		if (data.getType().getKind() != Kind.LONG)
			throw new IntermediateCodeGeneratorException(
					"Return works only with long variables");

		return new QuadrupleJb(Operator.RETURN, data.getIdentifier(),
				Quadruple.EmptyArgument, Quadruple.EmptyArgument);
	}

	public static Quadruple generateAssignment(IdentifierData leftData,
			IdentifierData rightData) throws IntermediateCodeGeneratorException {
		if (leftData.getType().getKind() != rightData.getType().getKind())
			throw new IntermediateCodeGeneratorException(
					"Cast before value assignment missing");

		switch (leftData.getType().getKind()) {
		case LONG:
			return new QuadrupleJb(Operator.ASSIGN_LONG,
					rightData.getIdentifier(), Quadruple.EmptyArgument,
					leftData.getIdentifier());
		case DOUBLE:
			return new QuadrupleJb(Operator.ASSIGN_DOUBLE,
					rightData.getIdentifier(), Quadruple.EmptyArgument,
					leftData.getIdentifier());
		case BOOLEAN:
			return new QuadrupleJb(Operator.ASSIGN_BOOLEAN,
					rightData.getIdentifier(), Quadruple.EmptyArgument,
					leftData.getIdentifier());
		case STRING:
			return new QuadrupleJb(Operator.ASSIGN_STRING,
					rightData.getIdentifier(), Quadruple.EmptyArgument,
					leftData.getIdentifier());
		default:
			throw new IntermediateCodeGeneratorException("Unsupported type: "
					+ leftData.getType().getKind());
		}
	}

	/**
	 * generates a Quadruple for a arithmetic unary operation
	 * 
	 * @param unaryOperator
	 * @param newData
	 * @param oldData
	 * @return
	 * @throws IntermediateCodeGeneratorException
	 */
	public static Quadruple generateArithmeticUnary(
			UnaryOperator unaryOperator, IdentifierData newData,
			IdentifierData oldData) throws IntermediateCodeGeneratorException {
		if (unaryOperator != UnaryOperator.MINUS)
			throw new IntermediateCodeGeneratorException(
					"Unsupported unary operator for arithmetics: "
							+ unaryOperator);

		switch (newData.getType().getKind()) {
		case LONG:
			return new QuadrupleJb(Operator.SUB_LONG, "#0",
					oldData.getIdentifier(), newData.getIdentifier());
		case DOUBLE:
			return new QuadrupleJb(Operator.SUB_DOUBLE, "#0.0",
					oldData.getIdentifier(), newData.getIdentifier());
		default:
			throw new IntermediateCodeGeneratorException("Unsupported type: "
					+ newData.getType().getKind());
		}
	}

	public static Quadruple generateArithmeticBinary(
			BinaryOperator binaryOperator, IdentifierData leftData,
			IdentifierData rightData, IdentifierData newData)
			throws IntermediateCodeGeneratorException {
		if (newData.getType().getKind() == Kind.LONG) {
			switch (binaryOperator) {
			case ADDITION:
				return new QuadrupleJb(Operator.ADD_LONG,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			case SUBSTRACTION:
				return new QuadrupleJb(Operator.SUB_LONG,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			case MULTIPLICATION:
				return new QuadrupleJb(Operator.MUL_LONG,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			case DIVISION:
				return new QuadrupleJb(Operator.DIV_LONG,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			default:
				break;
			}
		} else if (newData.getType().getKind() == Kind.DOUBLE) {
			switch (binaryOperator) {
			case ADDITION:
				return new QuadrupleJb(Operator.ADD_DOUBLE,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			case SUBSTRACTION:
				return new QuadrupleJb(Operator.SUB_DOUBLE,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			case MULTIPLICATION:
				return new QuadrupleJb(Operator.MUL_DOUBLE,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			case DIVISION:
				return new QuadrupleJb(Operator.DIV_DOUBLE,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			default:
				break;
			}

		}

		throw new IntermediateCodeGeneratorException(
				"Unsupported binary operation");
	}

	public static Quadruple generateLogicUnary(UnaryOperator unaryOperator,
			IdentifierData newData, IdentifierData oldData)
			throws IntermediateCodeGeneratorException {
		if (unaryOperator != UnaryOperator.LOGICAL_NEGATE)
			throw new IntermediateCodeGeneratorException(
					"Unsupported unary operator for arithmetics: "
							+ unaryOperator);

		if (oldData.getType().getKind() != Kind.BOOLEAN)
			throw new IntermediateCodeGeneratorException(
					"Logic unary operation works only on boolean types.");

		return new QuadrupleJb(Operator.NOT_BOOLEAN, oldData.getIdentifier(),
				Quadruple.EmptyArgument, newData.getIdentifier());
	}

	public static Quadruple generateLogicBinary(BinaryOperator binaryOperator,
			IdentifierData leftData, IdentifierData rightData,
			IdentifierData newData) throws IntermediateCodeGeneratorException {
		if (newData.getType().getKind() == Kind.BOOLEAN) {
			switch (binaryOperator) {
			case LOGICAL_AND:
				return new QuadrupleJb(Operator.AND_BOOLEAN,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			case LOGICAL_OR:
				return new QuadrupleJb(Operator.OR_BOOLEAN,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			default:
				break;
			}
		}

		throw new IntermediateCodeGeneratorException(
				"Unsupported binary operation");
	}

	public static Quadruple generateRelation(BinaryOperator binaryOperator,
			IdentifierData leftData, IdentifierData rightData,
			IdentifierData newData) throws IntermediateCodeGeneratorException {

		if (leftData.getType().getKind() == Kind.LONG) {
			switch (binaryOperator) {
			case EQUAL:
				return new QuadrupleJb(Operator.COMPARE_LONG_E,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			case GREATERTHAN:
				return new QuadrupleJb(Operator.COMPARE_LONG_G,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			case LESSTHAN:
				return new QuadrupleJb(Operator.COMPARE_LONG_L,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			case GREATERTHANEQUAL:
				return new QuadrupleJb(Operator.COMPARE_LONG_GE,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			case LESSTHANEQUAL:
				return new QuadrupleJb(Operator.COMPARE_LONG_LE,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			default:
				break;
			}
		} else if (leftData.getType().getKind() == Kind.DOUBLE) {
			switch (binaryOperator) {
			case EQUAL:
				return new QuadrupleJb(Operator.COMPARE_DOUBLE_E,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			case GREATERTHAN:
				return new QuadrupleJb(Operator.COMPARE_DOUBLE_G,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			case LESSTHAN:
				return new QuadrupleJb(Operator.COMPARE_DOUBLE_L,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			case GREATERTHANEQUAL:
				return new QuadrupleJb(Operator.COMPARE_DOUBLE_GE,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			case LESSTHANEQUAL:
				return new QuadrupleJb(Operator.COMPARE_DOUBLE_LE,
						leftData.getIdentifier(), rightData.getIdentifier(),
						newData.getIdentifier());
			default:
				break;
			}
		}
		throw new IntermediateCodeGeneratorException(
				"Unsupported binary operation");
	}

	public static Quadruple generatePrint(IdentifierData data)
			throws IntermediateCodeGeneratorException {
		switch (data.getType().getKind()) {
		case LONG:
			return new QuadrupleJb(Operator.PRINT_LONG, data.getIdentifier(),
					Quadruple.EmptyArgument, Quadruple.EmptyArgument);
		case DOUBLE:
			return new QuadrupleJb(Operator.PRINT_DOUBLE, data.getIdentifier(),
					Quadruple.EmptyArgument, Quadruple.EmptyArgument);
		case BOOLEAN:
			return new QuadrupleJb(Operator.PRINT_BOOLEAN,
					data.getIdentifier(), Quadruple.EmptyArgument,
					Quadruple.EmptyArgument);
		case STRING:
			return new QuadrupleJb(Operator.PRINT_STRING, data.getIdentifier(),
					Quadruple.EmptyArgument, Quadruple.EmptyArgument);
		default:
			throw new IntermediateCodeGeneratorException("Unsupported type: "
					+ data.getType().getKind());
		}
	}

	public static Quadruple generateLabel(String label)
			throws IntermediateCodeGeneratorException {
		return new QuadrupleJb(Operator.LABEL, label, Quadruple.EmptyArgument,
				Quadruple.EmptyArgument);
	}

	public static Quadruple generateBranch(String label) {
		return new QuadrupleJb(Operator.BRANCH, label, Quadruple.EmptyArgument,
				Quadruple.EmptyArgument);
	}

	public static Quadruple generateConditionalBranch(IdentifierData boolId,
			String trueLabel, String falseLabel) {
		return new QuadrupleJb(Operator.BRANCH, trueLabel, falseLabel,
				boolId.getIdentifier());
	}
}
