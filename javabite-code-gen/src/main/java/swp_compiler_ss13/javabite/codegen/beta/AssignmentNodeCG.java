package swp_compiler_ss13.javabite.codegen.beta;

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
 * @author florian
 *
 */
public class AssignmentNodeCG {


	public void convert(AssignmentNode node) throws IntermediateCodeGeneratorException {
		
		BasicIdentifierNode id = (BasicIdentifierNode) node.getLeftValue();

		switch (id.getNodeType()) {
		case BasicIdentifierNode:
			StatementNode value = node.getRightValue();
		
			IRCodeGenerator.differentiateNode((ASTNodeJb) value);
		
			String rightValue = IRCodeGenerator.intermediateResults.pop();
			Type rightType = IRCodeGenerator.intermediateTypes.pop();
			
			String idOrigName = id.getIdentifier();
			String idRenamed = IRCodeGenerator.loadIdentifier(idOrigName);
			Type typeOfid = IRCodeGenerator.currentSymbolTable.peek().lookupType(idOrigName);
			
			String casted = rightValue;
			if (typeOfid.getKind() == Kind.LONG && rightType.getKind() == Kind.DOUBLE) {
			casted = IRCodeGenerator.createAndSaveTemporaryIdentifier(new DoubleType());
			Quadruple cleft = QuadrupleFactory.castDoubleToLong(rightValue, casted);
			IRCodeGenerator.irCode.add(cleft);
			}
			if (typeOfid.getKind() == Kind.DOUBLE && rightType.getKind() == Kind.LONG) {
			casted = IRCodeGenerator.createAndSaveTemporaryIdentifier(new DoubleType());
			Quadruple cleft = QuadrupleFactory.castLongToDouble(rightValue, casted);
			IRCodeGenerator.irCode.add(cleft);
			}
			
			IRCodeGenerator.irCode.add(QuadrupleFactory.assign(typeOfid, casted, idRenamed));
			
			break;
		default:
			throw new IntermediateCodeGeneratorException("Unsupported identifer type");
		}
		
	}
}
