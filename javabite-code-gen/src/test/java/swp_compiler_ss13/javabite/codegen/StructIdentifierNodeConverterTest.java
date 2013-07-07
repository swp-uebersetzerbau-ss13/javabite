package swp_compiler_ss13.javabite.codegen;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ast.nodes.unary.StructIdentifierNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.derived.Member;
import swp_compiler_ss13.common.types.derived.StructType;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.javabite.codegen.converters.StructIdentifierNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;
public class StructIdentifierNodeConverterTest {
        
        StructIdentifierNodeConverter converter;
           
    @Before
    public void setUp() throws Exception {
        converter = new StructIdentifierNodeConverter();
        converter.icg = Mockito
                .mock(IntermediateCodeGeneratorJb.class);
    }
    
    @Test
    public void testStructIdentifierNodeConverterStructAccesChainTrue(){
    	try {
	    	StructIdentifierNode node = Mockito.mock(StructIdentifierNode.class);
	    	when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("testStruct", new StructType(
					new Member[] { new Member("",
							new LongType()) })));
	    	when(node.getFieldName()).thenReturn("structField");
			when(node.getParentNode()).thenReturn(any(StructIdentifierNode.class));
			when(converter.icg.getNewReference()).thenReturn("reference");
			converter.convert(node);
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.DECLARE_REFERENCE,
							Quadruple.EmptyArgument, Quadruple.EmptyArgument, "reference"));
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.STRUCT_GET_REFERENCE, "testStruct", "structField",
							"reference"));
		} catch (IntermediateCodeGeneratorException e) {
			
		}
    }
    
    @Test
    public void testStructIdentifierNodeConverterStructAccesChainFalse_isTargetOfAssignemntTrue(){
    	try {
	    	StructIdentifierNode node = Mockito.mock(StructIdentifierNode.class);
	    	when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("testStruct", new StructType(
					new Member[] { new Member("",
							new LongType()) })));
	    	when(node.getFieldName()).thenReturn("structField");
			when(node.getParentNode()).thenReturn(any(AssignmentNode.class));
			when(converter.icg.getNewReference()).thenReturn("reference");
			converter.convert(node);
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.DECLARE_REFERENCE,
							Quadruple.EmptyArgument, Quadruple.EmptyArgument, "reference"));
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.STRUCT_GET_REFERENCE, "testStruct", "structField",
							"reference"));
		} catch (IntermediateCodeGeneratorException e) {
			
		}
    }
    
    @Test
    public void testStructIdentifierNodeConverterStructAccesChainFalse_isTargetOfAssignemntFalse(){
    	try {
	    	StructIdentifierNode node = Mockito.mock(StructIdentifierNode.class);
	    	when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("testStruct", new StructType(
					new Member[] { new Member("",
							new LongType()) })));
	    	when(node.getFieldName()).thenReturn("structField");
			when(node.getParentNode()).thenReturn(any(LiteralNode.class));
			when(converter.icg.getNewReference()).thenReturn("reference");
			converter.convert(node);
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.DECLARE_REFERENCE,
							Quadruple.EmptyArgument, Quadruple.EmptyArgument, "reference"));
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.STRUCT_GET_REFERENCE, "testStruct", "structField",
							"reference"));
		} catch (IntermediateCodeGeneratorException e) {
			
		}
    }
}