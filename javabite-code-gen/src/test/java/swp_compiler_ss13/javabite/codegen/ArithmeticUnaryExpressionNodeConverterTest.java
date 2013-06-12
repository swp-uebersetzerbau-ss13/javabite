package swp_compiler_ss13.javabite.codegen;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArithmeticUnaryExpressionNode;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.derived.ArrayType;
import swp_compiler_ss13.common.types.derived.Member;
import swp_compiler_ss13.common.types.derived.StructType;
import swp_compiler_ss13.common.types.primitive.BooleanType;
import swp_compiler_ss13.common.types.primitive.DoubleType;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.javabite.codegen.converters.ArithmeticUnaryExpressionNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;


public class ArithmeticUnaryExpressionNodeConverterTest {
	
	
	ArithmeticUnaryExpressionNodeConverter converter;
	
	@Before
	public void setUp() throws Exception {
		converter = new ArithmeticUnaryExpressionNodeConverter();

		converter.icg = Mockito
				.mock(IntermediateCodeGeneratorJb.class);
	}

	
	/**
	 * Test to put a "Minus"(Subtraction) in front of an Arithmetic Unary Expression of the type Long.
	 */
	@Test
	public void testArithmeticUnaryExpressionLong(){
		try {
			ASTNode node = Mockito.mock(ArithmeticUnaryExpressionNode.class);
			verify(converter.icg).processNode(any(ArithmeticUnaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test", new LongType()));
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
			.thenReturn(new IdentifierData("tmp", new LongType()));
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.SUB_LONG, "#0",
					"test", "tmp"));
		}catch(IntermediateCodeGeneratorException e) {
			fail();
		}
	}
	
	
	/**
	 * Test to put a "Minus"(Subtraction) in front of an Arithmetic Unary Expression of the type Double.
	 */
	@Test
	public void testArithmeticUnaryExpressionDouble(){
		try {
			ASTNode node = Mockito.mock(ArithmeticUnaryExpressionNode.class);
			verify(converter.icg).processNode(any(ArithmeticUnaryExpressionNode.class));
			IdentifierData oldData = new IdentifierData("test", new DoubleType());
			when(converter.icg.popIdentifierData()).thenReturn(oldData);
			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
			.thenReturn(new IdentifierData("tmp", new DoubleType()));
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.SUB_DOUBLE, "#0", "test", "tmp"));
			
		}catch(IntermediateCodeGeneratorException e) {
			fail();
		}
	}
	
	
	/**
	 * Test to put a "Minus"(Subtraction) in front of an Arithmetic Unary Expression of the type Boolean (not allowed).
	 */
	@Test
	public void testArithmeticUnaryExpressionBoolean(){
		try {
			ASTNode node = Mockito.mock(ArithmeticUnaryExpressionNode.class);
			verify(converter.icg).processNode(any(ArithmeticUnaryExpressionNode.class));
			IdentifierData oldData = new IdentifierData("test", new BooleanType());
			when(converter.icg.popIdentifierData()).thenReturn(oldData);
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
			.thenReturn(new IdentifierData("tmp", new BooleanType()));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	
	/**
	 * Test to put a "Minus"(Subtraction) in front of an Arithmetic Unary Expression of the type Struct (not allowed).
	 */
	@Test
	public void testArithmeticUnaryExpressionStruct(){
		try {
			ASTNode node = Mockito.mock(ArithmeticUnaryExpressionNode.class);
			verify(converter.icg).processNode(any(ArithmeticUnaryExpressionNode.class));
			IdentifierData oldData = new IdentifierData("test", new StructType(
					null,
					new Member[] { new Member("",
							new LongType()) }));
			when(converter.icg.popIdentifierData()).thenReturn(oldData);
			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
			.thenReturn(new IdentifierData("tmp", new DoubleType()));
			verify(converter).convert(node);
			fail();
			
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	
	/**
	 * Test to put a "Minus"(Subtraction) in front of an Arithmetic Unary Expression of the type Array (not allowed).
	 */
	@Test
	public void testArithmeticUnaryExpressionArray(){
		try {
			ASTNode node = Mockito.mock(ArithmeticUnaryExpressionNode.class);
			verify(converter.icg).processNode(any(ArithmeticUnaryExpressionNode.class));
			IdentifierData oldData = new IdentifierData("test", new ArrayType(new LongType(), 0));
			when(converter.icg.popIdentifierData()).thenReturn(oldData);
			when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
			.thenReturn(new IdentifierData("tmp", new ArrayType(new LongType(), 0)));
			verify(converter).convert(node);
			fail();
			
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}		
		
}
