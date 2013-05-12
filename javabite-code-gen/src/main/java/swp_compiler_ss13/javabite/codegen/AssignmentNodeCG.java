package swp_compiler_ss13.javabite.codegen;

import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.common.types.Type.Kind;
import swp_compiler_ss13.common.types.primitive.DoubleType;
import swp_compiler_ss13.javabite.ast.ASTNodeJb;

public class AssignmentNodeCG {


	public void convert(AssignmentNode node) throws IntermediateCodeGeneratorException {
		// get the identifier, for now BasicIdentifier
		BasicIdentifierNode identifier = (BasicIdentifierNode) node.getLeftValue();
		// for now only BasicIdentifier to find
		if(identifier.getNodeType().compareTo(ASTNodeType.BasicIdentifierNode)==0){
			StatementNode statementNode = node.getRightValue();
			
			IntermediateCodeGeneratorJb.differentiateNode((ASTNodeJb) statementNode);
			
			String rightValue = IntermediateCodeGeneratorJb.temporaryResultOutputs.pop();
			Type rightType = IntermediateCodeGeneratorJb.temporaryTypes.pop();
			// get the identifier and his type
			String currentIdentifier = IntermediateCodeGeneratorJb.getIdentifier(identifier.getIdentifier());
			Type typeOfid = IntermediateCodeGeneratorJb.symbolTable.peek().lookupType(identifier.getIdentifier());
			
			// if the left identifier long, the right value double, if not then use rightValue
			// cast the right type from double to long
			String variable;
			if (typeOfid.getKind() == Kind.LONG && rightType.getKind() == Kind.DOUBLE) {
				variable = IntermediateCodeGeneratorJb.createAndAddTemporaryIdentifier(new DoubleType());
				Quadruple quadruple = QuadrupleFactoryJb.generateCastDoubleToLong(rightValue, variable);
				IntermediateCodeGeneratorJb.quadruples.add(quadruple);
			}
			// if the left identifier double, the right value long, if not then use rightValue
			// cast the right type from long to double 
			else if (typeOfid.getKind() == Kind.DOUBLE && rightType.getKind() == Kind.LONG) {
				variable = IntermediateCodeGeneratorJb.createAndAddTemporaryIdentifier(new DoubleType());
				Quadruple quadruple = QuadrupleFactoryJb.generateCastLongToDouble(rightValue, variable);
				IntermediateCodeGeneratorJb.quadruples.add(quadruple);
			}
			else{
				variable = rightValue;
			}
			
			IntermediateCodeGeneratorJb.quadruples.add(QuadrupleFactoryJb.generateAssignment(typeOfid, variable, currentIdentifier));
		}
		else{
			throw new IntermediateCodeGeneratorException("The type: "+identifier.getNodeType()+"is not supported!");
		}
	}
}