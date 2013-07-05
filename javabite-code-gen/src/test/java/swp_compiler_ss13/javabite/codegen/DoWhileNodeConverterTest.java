package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.binary.DoWhileNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.common.types.primitive.BooleanType;
import swp_compiler_ss13.common.types.primitive.DoubleType;
import swp_compiler_ss13.javabite.codegen.converters.DoWhileNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;
public class DoWhileNodeConverterTest {
        
        DoWhileNodeConverter converter;
           
    @Before
    public void setUp() throws Exception {
        converter = new DoWhileNodeConverter();
        converter.icg = Mockito
                .mock(IntermediateCodeGeneratorJb.class);  
    }
    
    @Test
    public void testDoWhileNodeConverter(){
    	try {
	    	DoWhileNode node = Mockito.mock(DoWhileNode.class);
	    	when(converter.icg.getNewLabel()).thenReturn("startLabel","endLabel");
	    	when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("condition", new BooleanType()));
	    	StatementNode statementNode = Mockito.mock(StatementNode.class);
	    	when(node.getLoopBody()).thenReturn(statementNode);
	    	ExpressionNode expressionNode = Mockito.mock(ExpressionNode.class);
	    	when(node.getCondition()).thenReturn(expressionNode);
			converter.convert(node);
			verify(converter.icg).enterLoop("endLabel");
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.LABEL, "startLabel", Quadruple.EmptyArgument,
							Quadruple.EmptyArgument));
			
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.BRANCH, "startLabel", "endLabel",
							"condition"));
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.LABEL, "endLabel", Quadruple.EmptyArgument,
							Quadruple.EmptyArgument));
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
    	
    }
    
}