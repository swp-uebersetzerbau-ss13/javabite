package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.ReturnNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
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
	public void testReturnNodeConverter(){
		try{
		
			ReturnNode returnNode = Mockito.mock(ReturnNode.class);
			when(returnNode.getRightValue()).thenReturn(any(IdentifierNode.class));
			IdentifierNode idNode = Mockito.mock(IdentifierNode.class);
			if (idNode == null) {
				verify(converter.icg).addQuadruple(
						new QuadrupleJb(Operator.RETURN, "test",
								Quadruple.EmptyArgument, Quadruple.EmptyArgument));
			}
			else{
				verify(converter.icg).processNode(any(ReturnNode.class));
				IdentifierNode data = Mockito.mock(IdentifierNode.class);
				verify(converter.icg).addQuadruple(
						new QuadrupleJb(Operator.RETURN, "test",
								Quadruple.EmptyArgument, Quadruple.EmptyArgument));
			}
			
			
		}catch(IntermediateCodeGeneratorException e){
			fail();
		}
	}
}