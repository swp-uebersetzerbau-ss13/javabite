package swp_compiler_ss13.javabite.codegen.alternative.converters;



import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.unary.PrintNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type.Kind;
import swp_compiler_ss13.javabite.codegen.alternative.CastingAst2CodeConverter;
import swp_compiler_ss13.javabite.codegen.alternative.IdentifierData;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJB;

public class PrintNodeConverter extends CastingAst2CodeConverter{

    
    @Override
    public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
        if(!(node instanceof PrintNodeConverter)) 
            throw new IntermediateCodeGeneratorException();
        PrintNode printNode = (PrintNode) node;
        
        icg.processNode(printNode.getRightValue());
        IdentifierData newId = icg.popIdentifierData();
        IdentifierData oldId = icg.popIdentifierData();
    }
        
            
        	// cast is needed -> its arithmetic so one has to be double and one long
            // we always upcast to double
            
            //rightData = cast(Types., rightData);
            
        	public Quadruple printCast(IdentifierData newId, IdentifierData oldId) throws IntermediateCodeGeneratorException {
                if (newId.getType().getKind() == Kind.LONG) {
                    switch (oldId.getType().getKind()) {
                    case DOUBLE:
                        return new QuadrupleJB(Operator.LONG_TO_DOUBLE, oldId.getIdentifier(), Quadruple.EmptyArgument, newId.getIdentifier());
                    default:
                        break;
                    }
                } else if (newId.getType().getKind() == Kind.DOUBLE) {
                    switch (oldId.getType().getKind()) {
                    case LONG:
                        return new QuadrupleJB(Operator.DOUBLE_TO_LONG, oldId.getIdentifier(), Quadruple.EmptyArgument, newId.getIdentifier());
                    default:
                        break;
                    }
                } 
                    
                throw new IntermediateCodeGeneratorException("Unsupported cast request for types: " + oldId.getType().getKind() + " -> " + newId.getType().getKind());
            }
        	
      //  }
   // }
    
    
    @Override
    public ASTNodeType getNodeType() {
        return ASTNodeType.PrintNode;
    }


        
}
