package swp_compiler_ss13.javabite.codegen.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.binary.LogicBinaryExpressionNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.CastingAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.IdentifierData;
import swp_compiler_ss13.javabite.codegen.QuadrupleFactoryJb;

public class LogicBinaryExpressionNodeConverter extends CastingAst2CodeConverter {

    @Override
    public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
        if (!(node instanceof LogicBinaryExpressionNode))
            throw new IntermediateCodeGeneratorException();
        LogicBinaryExpressionNode logicBinaryNode = (LogicBinaryExpressionNode) node;
        
        icg.processNode(logicBinaryNode.getLeftValue());
        IdentifierData leftData = icg.popIdentifierData();
        
        icg.processNode(logicBinaryNode.getRightValue());
        IdentifierData rightData = icg.popIdentifierData();
        
        
        IdentifierData newData = icg.generateTempIdentifier(leftData.getType());
        icg.addQuadruple(QuadrupleFactoryJb.generateLogicBinary(logicBinaryNode.getOperator(), leftData, rightData, newData));
        icg.pushIdentifierData(newData);
    }

    @Override
    public ASTNodeType getNodeType() {
        return ASTNodeType.LogicBinaryExpressionNode;
    }
}