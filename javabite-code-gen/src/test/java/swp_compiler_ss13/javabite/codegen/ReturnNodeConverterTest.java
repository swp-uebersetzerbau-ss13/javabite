package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.nodes.unary.ReturnNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.javabite.ast.nodes.leaf.BasicIdentifierNodeJb;
import swp_compiler_ss13.javabite.codegen.converters.ReturnNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public class ReturnNodeConverterTest {
	ReturnNodeConverter converter;
	
	@Before
	public void setUp() throws Exception {
		converter = new ReturnNodeConverter();

		converter.icg = Mockito
				.mock(IntermediateCodeGeneratorJb.class);
	}
	/**
	 * Test for the ReturnNodeConverter, with given the icq object.
	 */
	@Test
	public void testReturnNodeConverterNull(){
		try{
		
			ReturnNode returnNode = Mockito.mock(ReturnNode.class);
			when(returnNode.getRightValue()).thenReturn(null);
			converter.convert(returnNode);
			verify(converter.icg).addQuadruple(
						new QuadrupleJb(Operator.RETURN, "#0",
								Quadruple.EmptyArgument, Quadruple.EmptyArgument));
			
			
		}catch(IntermediateCodeGeneratorException e){
			fail();
		}
	}
	/**
	 * Test for the ReturnNodeConverter, with given the icq object.
	 */
	@Test
	public void testReturnNodeConverterLong(){
		try{
		
			ReturnNode returnNode = Mockito.mock(ReturnNode.class);
			
			when(returnNode.getRightValue()).thenReturn(new BasicIdentifierNodeJb());
			when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test", new LongType()));
			converter.convert(returnNode);
			verify(converter.icg).addQuadruple(
						new QuadrupleJb(Operator.RETURN, "test",
								Quadruple.EmptyArgument, Quadruple.EmptyArgument));
				
			
			
		}catch(IntermediateCodeGeneratorException e){
			fail();
		}
	}
	
}