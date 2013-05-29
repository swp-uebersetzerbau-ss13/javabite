package swp_compiler_ss13.javabite.codegen.alternative.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.binary.RelationExpressionNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.alternative.CastingAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.alternative.IdentifierData;
import swp_compiler_ss13.javabite.codegen.alternative.QuadrupleFactoryJb;

public class RelationNodeConverter extends CastingAst2CodeConverter {

    @Override
    public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
        if (!(node instanceof RelationNodeConverter))
            throw new IntermediateCodeGeneratorException();
        RelationExpressionNode binaryNode = (RelationExpressionNode) node;
    
        icg.processNode(binaryNode.getLeftValue());
        IdentifierData leftData = icg.popIdentifierData();
        
        icg.processNode(binaryNode.getRightValue());
        IdentifierData rightData = icg.popIdentifierData();
        
        
        IdentifierData newData = icg.generateTempIdentifier(leftData.getType());
        icg.addQuadruple(QuadrupleFactoryJb.generateRelation(binaryNode.getOperator(), leftData, rightData, newData));
        icg.pushIdentifierData(newData);
    }
    
    
    @Override
    public ASTNodeType getNodeType() {
        return ASTNodeType.RelationExpressionNode;
    }

    

}