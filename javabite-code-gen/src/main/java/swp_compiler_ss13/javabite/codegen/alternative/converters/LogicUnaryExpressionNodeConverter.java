package swp_compiler_ss13.javabite.codegen.alternative.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.unary.LogicUnaryExpressionNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.alternative.CastingAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.alternative.IdentifierData;
import swp_compiler_ss13.javabite.codegen.alternative.QuadrupleFactoryJb;

public class LogicUnaryExpressionNodeConverter extends CastingAst2CodeConverter {

    @Override
    public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
        if (!(node instanceof LogicUnaryExpressionNode))
            throw new IntermediateCodeGeneratorException();
        LogicUnaryExpressionNode logicunaryNode = (LogicUnaryExpressionNode) node;
        
        icg.processNode(logicunaryNode.getRightValue());
        IdentifierData oldData = icg.popIdentifierData();
        IdentifierData newData = icg.generateTempIdentifier(oldData.getType());
        icg.addQuadruple(QuadrupleFactoryJb.generateLogicUnary(logicunaryNode.getOperator(), newData, oldData));
        icg.pushIdentifierData(newData);
    }

    @Override
    public ASTNodeType getNodeType() {
        return ASTNodeType.LogicUnaryExpressionNode;
    }
}