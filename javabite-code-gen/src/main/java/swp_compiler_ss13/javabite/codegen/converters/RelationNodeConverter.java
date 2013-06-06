package swp_compiler_ss13.javabite.codegen.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.binary.RelationExpressionNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type.Kind;
import swp_compiler_ss13.javabite.codegen.CastingAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.IdentifierData;
import swp_compiler_ss13.javabite.codegen.QuadrupleFactoryJb;

public class RelationNodeConverter extends CastingAst2CodeConverter {

    @Override
    public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
        if (!(node instanceof RelationNodeConverter))
            throw new IntermediateCodeGeneratorException();
        RelationExpressionNode relationNode = (RelationExpressionNode) node;
    
        icg.processNode(relationNode.getLeftValue());
        IdentifierData leftData = icg.popIdentifierData();
        
        icg.processNode(relationNode.getRightValue());
        IdentifierData rightData = icg.popIdentifierData();
        
        // check if types matches
 		if (leftData.getType().getKind() != rightData.getType().getKind()) {
 			// cast is needed -> relations works on arithmetic types so one has
 			// to be double and one long and we always upcast to double
 			if (leftData.getType().getKind() == Kind.LONG) {
 				leftData = cast(rightData.getType(), leftData);
 			} else {
 				rightData = cast(leftData.getType(), rightData);
 			}
 		}
        
        IdentifierData newData = icg.generateTempIdentifier(leftData.getType());
        icg.addQuadruple(QuadrupleFactoryJb.generateRelation(relationNode.getOperator(), leftData, rightData, newData));
        icg.pushIdentifierData(newData);
    }
    
    
    @Override
    public ASTNodeType getNodeType() {
        return ASTNodeType.RelationExpressionNode;
    }

    

}