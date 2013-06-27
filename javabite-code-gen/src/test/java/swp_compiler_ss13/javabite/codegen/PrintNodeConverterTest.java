package swp_compiler_ss13.javabite.codegen;

import static org.mockito.Matchers.any;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode.BinaryOperator;
import swp_compiler_ss13.common.ast.nodes.unary.PrintNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.derived.ArrayType;
import swp_compiler_ss13.common.types.derived.Member;
import swp_compiler_ss13.common.types.derived.StructType;
import swp_compiler_ss13.common.types.primitive.BooleanType;
import swp_compiler_ss13.common.types.primitive.DoubleType;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.common.types.primitive.StringType;
import swp_compiler_ss13.javabite.ast.nodes.unary.PrintNodeJb;
import swp_compiler_ss13.javabite.codegen.converters.PrintNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public class PrintNodeConverterTest {
	PrintNodeConverter converter;
	
	@Before
	public void setUp() throws Exception {
		converter = new PrintNodeConverter();

		converter.icg = Mockito
				.mock(IntermediateCodeGeneratorJb.class);
	}
	
	
	/**
	 * Test for any PrintNode, which has the type String.
	 * Pop the IdentifierData, verify the converter and add the Quadruple.
	 */
	@Test
	public void testPrintStringConverter() throws IntermediateCodeGeneratorException{
		try {
			PrintNode node = Mockito.mock(PrintNode.class);
			IdentifierData tmp = new IdentifierData("test", new StringType(0L));
			when(converter.icg.popIdentifierData()).thenReturn(tmp);
			
			converter.convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.PRINT_STRING, tmp.getIdentifier(),
					Quadruple.EmptyArgument, Quadruple.EmptyArgument));
					
			
		}catch(IntermediateCodeGeneratorException e) {
			fail();
		}
		
	}
	
	
	
	/**
	 * Test for any PrintNode, which has the type Struct.
	 * Pop the IdentifierData, verify the converter and fail,
	 * because the type Struct is not supported for this Test.
	 */
	@Test
	public void testPrintStructConverter() throws IntermediateCodeGeneratorException{
		
			PrintNode node = Mockito.mock(PrintNode.class);
			IdentifierData tmp = new IdentifierData("test", new StructType(null,
                    new Member[] { new Member("",
                            new DoubleType()) }));
			when(converter.icg.popIdentifierData()).thenReturn(tmp);
			
			
					
			
		
		
	}


	
	/**
	 * Test for any PrintNode, which has the type Array.
	 * Pop the IdentifierData, verify the converter and fail,
	 * because the type Array is not supported for this Test.
	 */
	@Test
	public void testPrintArrayConverter() throws IntermediateCodeGeneratorException{
		
	
	
			
			PrintNode node = Mockito.mock(PrintNode.class);
			IdentifierData tmp = new IdentifierData("test", new ArrayType(new LongType(),0));
			when(converter.icg.popIdentifierData()).thenReturn(tmp);	
					
	}
			
}	
	
	


