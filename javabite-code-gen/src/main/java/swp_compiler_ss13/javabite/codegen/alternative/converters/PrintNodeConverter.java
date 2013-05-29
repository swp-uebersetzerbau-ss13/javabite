package swp_compiler_ss13.javabite.codegen.alternative.converters;

import javax.print.DocFlavor.STRING;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.unary.PrintNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type.Kind;
import swp_compiler_ss13.javabite.codegen.alternative.CastingAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.alternative.IdentifierData;

public class PrintNodeConverter extends CastingAst2CodeConverter{

    
    @Override
    public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
        // TODO Auto-generated method stub
        if(!(node instanceof PrintNodeConverter)) 
            throw new IntermediateCodeGeneratorException();
        PrintNode printNode = (PrintNode) node;
        
        icg.processNode(printNode.getRightValue());
        IdentifierData rightData = icg.popIdentifierData();
        
        if (rightData.getType().getKind() != Kind.STRING) {
            // cast is needed -> its arithmetic so one has to be double and one long
            // we always upcast to double
            
            //rightData = cast(Types., rightData);
            
        }
    }
    
    
    @Override
    public ASTNodeType getNodeType() {
        return ASTNodeType.PrintNode;
    }


        
}
