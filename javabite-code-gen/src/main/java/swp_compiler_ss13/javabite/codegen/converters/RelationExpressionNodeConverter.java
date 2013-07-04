package swp_compiler_ss13.javabite.codegen.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode.BinaryOperator;
import swp_compiler_ss13.common.ast.nodes.binary.RelationExpressionNode;
import swp_compiler_ss13.common.ast.nodes.unary.UnaryExpressionNode.UnaryOperator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type.Kind;
import swp_compiler_ss13.common.types.primitive.BooleanType;
import swp_compiler_ss13.javabite.codegen.CastingAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.IdentifierData;
import swp_compiler_ss13.javabite.codegen.QuadrupleFactoryJb;

public class RelationExpressionNodeConverter extends CastingAst2CodeConverter {

    @Override
    public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
        if (!(node instanceof RelationExpressionNode))
            throw new IntermediateCodeGeneratorException();
        RelationExpressionNode relationNode = (RelationExpressionNode) node;
    
        icg.processNode(relationNode.getLeftValue());
        IdentifierData leftData = icg.popIdentifierData();
        
        icg.processNode(relationNode.getRightValue());
        IdentifierData rightData = icg.popIdentifierData();
        
        Kind left = leftData.getType().getKind();
        Kind right = rightData.getType().getKind();
       
        IdentifierData newData = icg.generateTempIdentifier(new BooleanType());
        if (left == Kind.BOOLEAN && right == Kind.BOOLEAN) {
        	 IdentifierData x = icg.generateTempIdentifier(new BooleanType());
        	 IdentifierData y = icg.generateTempIdentifier(new BooleanType());
        	if (relationNode.getOperator() == BinaryOperator.EQUAL) {
        		//leftData && rightData || !leftData && !rightData
        		icg.addQuadruple(QuadrupleFactoryJb.generateLogicBinary(BinaryOperator.LOGICAL_AND, leftData, rightData, newData));
        		icg.addQuadruple(QuadrupleFactoryJb.generateLogicUnary(UnaryOperator.LOGICAL_NEGATE, x, leftData));
        		icg.addQuadruple(QuadrupleFactoryJb.generateLogicUnary(UnaryOperator.LOGICAL_NEGATE, y, rightData));
        		icg.addQuadruple(QuadrupleFactoryJb.generateLogicBinary(BinaryOperator.LOGICAL_AND, x, y, y));
        		icg.addQuadruple(QuadrupleFactoryJb.generateLogicBinary(BinaryOperator.LOGICAL_OR, newData, y, newData));
        	} else if (relationNode.getOperator() == BinaryOperator.INEQUAL) {
        		//leftData && !rightData || !leftData && rightData
        		icg.addQuadruple(QuadrupleFactoryJb.generateLogicUnary(UnaryOperator.LOGICAL_NEGATE, x, leftData));
        		icg.addQuadruple(QuadrupleFactoryJb.generateLogicBinary(BinaryOperator.LOGICAL_AND, x, rightData, newData));
        		icg.addQuadruple(QuadrupleFactoryJb.generateLogicUnary(UnaryOperator.LOGICAL_NEGATE, y, rightData));
        		icg.addQuadruple(QuadrupleFactoryJb.generateLogicBinary(BinaryOperator.LOGICAL_AND, leftData, y, y));
        		icg.addQuadruple(QuadrupleFactoryJb.generateLogicBinary(BinaryOperator.LOGICAL_OR, newData, y, newData));
        	} else {
        		throw new IntermediateCodeGeneratorException("Unsupported relation");
        	}
        } else if (left == Kind.DOUBLE || left == Kind.LONG && right == Kind.DOUBLE || right == Kind.LONG) {
        	// check if types matches
     		if (left != right) {
     			// cast is needed -> relations works on arithmetic types so one has
     			// to be double and one long and we always upcast to double
     			if (left == Kind.LONG) {
     				leftData = cast(rightData.getType(), leftData);
     			} else {
     				rightData = cast(leftData.getType(), rightData);
     			}
     		}
     		
            if (relationNode.getOperator() == BinaryOperator.INEQUAL) {
            	icg.addQuadruple(QuadrupleFactoryJb.generateRelation(BinaryOperator.EQUAL, leftData, rightData, newData));
            	icg.addQuadruple(QuadrupleFactoryJb.generateLogicUnary(UnaryOperator.LOGICAL_NEGATE, newData, newData));
            } else {
            	icg.addQuadruple(QuadrupleFactoryJb.generateRelation(relationNode.getOperator(), leftData, rightData, newData));
            }
        } else {
        	throw new IntermediateCodeGeneratorException("Unsupported relation");
        }
 		
        icg.pushIdentifierData(newData);
    }
    
    
    @Override
    public ASTNodeType getNodeType() {
        return ASTNodeType.RelationExpressionNode;
    }

    

}