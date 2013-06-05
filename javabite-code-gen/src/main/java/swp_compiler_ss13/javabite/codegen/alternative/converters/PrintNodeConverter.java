package swp_compiler_ss13.javabite.codegen.alternative.converters;



import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.unary.PrintNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.common.types.Type.Kind;
import swp_compiler_ss13.javabite.codegen.alternative.CastingAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.alternative.IdentifierData;
import swp_compiler_ss13.javabite.codegen.alternative.QuadrupleFactoryJb;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJB;

public class PrintNodeConverter extends CastingAst2CodeConverter{

    
    @Override
    public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
        if(!(node instanceof PrintNodeConverter)) 
            throw new IntermediateCodeGeneratorException();
        PrintNode printNode = (PrintNode) node;
        
        icg.processNode(printNode.getRightValue());
        IdentifierData rightData = icg.popIdentifierData();

        
 		IdentifierData newData = icg.generateTempIdentifier(rightData.getType());
        icg.addQuadruple(QuadrupleFactoryJb.generatePrint(newData));
    }
    
    
    @Override
    public ASTNodeType getNodeType() {
        return ASTNodeType.PrintNode;
    }


        
}
