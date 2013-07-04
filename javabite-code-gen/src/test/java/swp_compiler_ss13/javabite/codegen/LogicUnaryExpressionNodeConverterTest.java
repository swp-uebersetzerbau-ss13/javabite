package swp_compiler_ss13.javabite.codegen;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.nodes.unary.LogicUnaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.unary.UnaryExpressionNode.UnaryOperator;
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
import swp_compiler_ss13.javabite.codegen.converters.LogicUnaryExpressionNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public class LogicUnaryExpressionNodeConverterTest {
	
	
	LogicUnaryExpressionNodeConverter converter;
	
	@Before
	public void setUp() throws Exception {
		converter = new LogicUnaryExpressionNodeConverter();

		converter.icg = Mockito
				.mock(IntermediateCodeGeneratorJb.class);
	}

	
	/**
	 * Test to put a "minus" (subtraction) in front of a Logic Unary Expression, which has the type Long.
	 */
	@Test
	public void testLogicUnaryExpressionLong(){
		try {
			LogicUnaryExpressionNode node = Mockito.mock(LogicUnaryExpressionNode.class);
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("testOld", new LongType()));
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
			.thenReturn(new IdentifierData("testNew", new BooleanType()));
			when(node.getOperator()).thenReturn(UnaryOperator.LOGICAL_NEGATE);
			converter.convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	
	
	/**
	 * Test to put a "minus" (subtraction) in front of a Logic Unary Expression, which has the type Double.
	 */
	@Test
	public void testLogicUnaryExpressionDouble(){
		try {
			LogicUnaryExpressionNode node = Mockito.mock(LogicUnaryExpressionNode.class);
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("testOld", new DoubleType()));
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
			.thenReturn(new IdentifierData("testNew", new BooleanType()));
			when(node.getOperator()).thenReturn(UnaryOperator.LOGICAL_NEGATE);
			converter.convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	/**
	 * Test to put a "minus" (subtraction) in front of a Logic Unary Expression, which has the type Double.
	 */
	@Test
	public void testLogicUnaryExpressionString(){
		try {
			LogicUnaryExpressionNode node = Mockito.mock(LogicUnaryExpressionNode.class);
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("testOld", new StringType(0l)));
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
			.thenReturn(new IdentifierData("testNew", new BooleanType()));
			when(node.getOperator()).thenReturn(UnaryOperator.LOGICAL_NEGATE);
			converter.convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	
	/**
	 * Test to put a "minus" (subtraction) in front of a Logic Unary Expression, which has the type Boolean.
	 */
	@Test
	public void testLogicUnaryExpressionBoolean(){
		try {
			LogicUnaryExpressionNode node = Mockito.mock(LogicUnaryExpressionNode.class);
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("testOld", new BooleanType()));
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
			.thenReturn(new IdentifierData("testNew", new BooleanType()));
			when(node.getOperator()).thenReturn(UnaryOperator.LOGICAL_NEGATE);
			converter.convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.NOT_BOOLEAN, "testOld",
					Quadruple.EmptyArgument, "testNew"));
			
		}catch(IntermediateCodeGeneratorException e) {
			fail();
		}
	}
	
	
	/**
	 * Test to put a "minus" (subtraction) in front of a Logic Unary Expression, which has the type Struct.
	 */
	@Test
	public void testLogicUnaryExpressionStruct(){
		try {
			LogicUnaryExpressionNode node = Mockito.mock(LogicUnaryExpressionNode.class);
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("testOld", new StructType(
					null,
					new Member[] { new Member("",
							new LongType()) })));
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
			.thenReturn(new IdentifierData("testNew", new BooleanType()));
			when(node.getOperator()).thenReturn(UnaryOperator.LOGICAL_NEGATE);
			converter.convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	
	/**
	 * Test to put a "minus" (subtraction) in front of a Logic Unary Expression, which has the type Array.
	 */
	@Test
	public void testLogicUnaryExpressionArray(){
		
		try {
			LogicUnaryExpressionNode node = Mockito.mock(LogicUnaryExpressionNode.class);
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("testOld",  new ArrayType(new LongType(), 0)));
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
			.thenReturn(new IdentifierData("testNew", new BooleanType()));
			when(node.getOperator()).thenReturn(UnaryOperator.LOGICAL_NEGATE);
			converter.convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}		
		
}
