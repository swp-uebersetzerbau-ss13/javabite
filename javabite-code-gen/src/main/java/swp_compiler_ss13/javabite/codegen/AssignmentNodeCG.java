package swp_compiler_ss13.javabite.codegen;

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
		switch (identifier.getNodeType()) {
		case BasicIdentifierNode:
			StatementNode value = node.getRightValue();
			
			JavaBiteCodeGenerator.differentiateNode((ASTNodeJb) value);
			
			String rightValue = JavaBiteCodeGenerator.temporaryResultOutputs.pop();
			Type rightType = JavaBiteCodeGenerator.temporaryTypes.pop();
			// get the identifier and his type
			String idOrigName = identifier.getIdentifier();
			String idRenamed = JavaBiteCodeGenerator.getIdentifier(idOrigName);
			Type typeOfid = JavaBiteCodeGenerator.latestSymbolTable.peek().lookupType(idOrigName);
			
			// if the left identifier long, the right value double, if not then use rightValue
			// cast the right type from double to long
			String casted = rightValue;
			if (typeOfid.getKind() == Kind.LONG && rightType.getKind() == Kind.DOUBLE) {
				casted = JavaBiteCodeGenerator.createAndAddTemporaryIdentifier(new DoubleType());
				Quadruple cleft = QuadrupleFactory.castDoubleToLong(rightValue, casted);
				JavaBiteCodeGenerator.quadruples.add(cleft);
			}
			// if the left identifier double, the right value long, if not then use rightValue
			// cast the right type from long to double 
			if (typeOfid.getKind() == Kind.DOUBLE && rightType.getKind() == Kind.LONG) {
				casted = JavaBiteCodeGenerator.createAndAddTemporaryIdentifier(new DoubleType());
				Quadruple cleft = QuadrupleFactory.castLongToDouble(rightValue, casted);
				JavaBiteCodeGenerator.quadruples.add(cleft);
			}
			
			JavaBiteCodeGenerator.quadruples.add(QuadrupleFactory.assign(typeOfid, casted, idRenamed));
			break;
		default:
			throw new IntermediateCodeGeneratorException("Unsupported identifer type");
		}
	
	}
}