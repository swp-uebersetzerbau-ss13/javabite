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


/**
*
* @author Alpin Sahin und Florian Mercks
*
*/
public class AssignmentNodeCG {


	public void convert(AssignmentNode node) throws IntermediateCodeGeneratorException {
		BasicIdentifierNode id = (BasicIdentifierNode) node.getLeftValue();
		
		switch (id.getNodeType()) {
		case BasicIdentifierNode:
			StatementNode value = node.getRightValue();
			
			JavaBiteCodeGenerator.differentiateNode((ASTNodeJb) value);
			
			String rightValue = JavaBiteCodeGenerator.intermediateResults.pop();
			Type rightType = JavaBiteCodeGenerator.intermediateTypes.pop();
			
			String idOrigName = id.getIdentifier();
			String idRenamed = JavaBiteCodeGenerator.loadIdentifier(idOrigName);
			Type typeOfid = JavaBiteCodeGenerator.currentSymbolTable.peek().lookupType(idOrigName);
			
			String casted = rightValue;
			if (typeOfid.getKind() == Kind.LONG && rightType.getKind() == Kind.DOUBLE) {
				casted = JavaBiteCodeGenerator.createAndSaveTemporaryIdentifier(new DoubleType());
				Quadruple cleft = QuadrupleFactory.castDoubleToLong(rightValue, casted);
				JavaBiteCodeGenerator.quadruples.add(cleft);
			}
			if (typeOfid.getKind() == Kind.DOUBLE && rightType.getKind() == Kind.LONG) {
				casted = JavaBiteCodeGenerator.createAndSaveTemporaryIdentifier(new DoubleType());
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