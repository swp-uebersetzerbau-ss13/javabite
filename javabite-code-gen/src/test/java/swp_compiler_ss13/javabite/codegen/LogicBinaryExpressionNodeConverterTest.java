package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.binary.LogicBinaryExpressionNode;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.derived.ArrayType;
import swp_compiler_ss13.common.types.derived.Member;
import swp_compiler_ss13.common.types.derived.StructType;
import swp_compiler_ss13.common.types.primitive.BooleanType;
import swp_compiler_ss13.common.types.primitive.DoubleType;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.common.types.primitive.StringType;
import swp_compiler_ss13.javabite.codegen.converters.LogicBinaryExpressionNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public class LogicBinaryExpressionNodeConverterTest {
	
LogicBinaryExpressionNodeConverter converter;
	
	@Before
	public void setUp() throws Exception {
		converter = new LogicBinaryExpressionNodeConverter();

		converter.icg = Mockito
				.mock(IntermediateCodeGeneratorJb.class);
	}

	
	/**
	 * Test for two Logic Binary Expressions, which both have the type Long and the Operator AND in between.
	 */
	@Test
	public void testLogicBinaryExpressionLong_AND(){
		try {
			ASTNode node = Mockito.mock(LogicBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
			
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
			
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
			.thenReturn(new IdentifierData("tmp", new LongType()));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	
	/**
	 * Test for two Logic Binary Expressions, which both have the type Double and the Operator AND in between.
	 */
	@Test
	public void testLogicBinaryExpressionDouble_AND(){
		try {
			ASTNode node = Mockito.mock(LogicBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new DoubleType()));
			
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new DoubleType()));
			
			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
			.thenReturn(new IdentifierData("tmp", new DoubleType()));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	
	/**
	 * Test for two Logic Binary Expressions, which both have the type Boolean and the Operator AND in between.
	 */
	@Test
	public void testLogicBinaryExpressionBoolean_AND(){
		try {
			ASTNode node = Mockito.mock(LogicBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new BooleanType()));
			
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new BooleanType()));
			
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
			.thenReturn(new IdentifierData("tmp", new BooleanType()));
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.AND_BOOLEAN,
					"tmp1", "tmp2",
					"tmp3"));
		}catch(IntermediateCodeGeneratorException e) {
			fail();
		}
	}
	
	
	/**
	 * Test for two Logic Binary Expressions, which both have the type String and the Operator AND in between.
	 */
	@Test
	public void testLogicBinaryExpressionString(){
		try {
			ASTNode node = Mockito.mock(LogicBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StringType(0l)));
			
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new StringType(0l)));
			
			when(converter.icg.generateTempIdentifier(any(StringType.class)))
			.thenReturn(new IdentifierData("tmp", new StringType(0l)));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	
	/**
	 * Test for two Logic Binary Expressions, which both have the type Struct and the Operator AND in between.
	 */
	@Test
	public void testLogicBinaryExpressionStruct_AND(){
		try {
			ASTNode node = Mockito.mock(LogicBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StructType(
					null,
					new Member[] { new Member("",
							new LongType()) })));
			
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new StructType(
					null,
					new Member[] { new Member("",
							new LongType()) })));
			
			when(converter.icg.generateTempIdentifier(any(StructType.class)))
			.thenReturn(new IdentifierData("tmp", new StructType(
					null,
					new Member[] { new Member("",
							new LongType()) })));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	
	/**
	 * Test for two Logic Binary Expressions, which both have the type Array and the Operator AND in between.
	 */
	@Test
	public void testLogicBinaryExpressionArray_AND(){
		try {
			ASTNode node = Mockito.mock(LogicBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new ArrayType(new LongType(), 0)));
			
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new ArrayType(new LongType(), 0)));
			
			when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
			.thenReturn(new IdentifierData("tmp", new ArrayType(new LongType(), 0)));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	
	
	/**
	 * Test for two Logic Binary Expressions, which both have the type Long and the Operator OR in between.
	 */
	@Test
	public void testLogicBinaryExpressionLong_OR(){
		try {
			ASTNode node = Mockito.mock(LogicBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
			
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
			
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
			.thenReturn(new IdentifierData("tmp", new LongType()));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}

	
	/**
	 * Test for two Logic Binary Expressions, which both have the type Double and the Operator OR in between.
	 */
	@Test
	public void testLogicBinaryExpressionDouble_OR(){
		try {
			ASTNode node = Mockito.mock(LogicBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new DoubleType()));
			
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new DoubleType()));
			
			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
			.thenReturn(new IdentifierData("tmp", new DoubleType()));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}


	/**
	 * Test for two Logic Binary Expressions, which both have the type Boolean and the Operator OR in between.
	 */
	@Test
	public void testLogicBinaryExpressionBoolean_OR(){
		try {
			ASTNode node = Mockito.mock(LogicBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new BooleanType()));
			
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new BooleanType()));
			
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
			.thenReturn(new IdentifierData("tmp", new BooleanType()));
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.OR_BOOLEAN,
					"tmp1", "tmp2",
					"tmp3"));
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}


	
	/**
	 * Test for two Logic Binary Expressions, which both have the type String and the Operator OR in between.
	 */
	@Test
	public void testLogicBinaryExpressionString_OR(){
		try {
			ASTNode node = Mockito.mock(LogicBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StringType(0l)));
			
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new StringType(0l)));
			
			when(converter.icg.generateTempIdentifier(any(StringType.class)))
			.thenReturn(new IdentifierData("tmp", new StringType(0l)));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}


	/**
	 * Test for two Logic Binary Expressions, which both have the type Struct and the Operator OR in between.
	 */
	@Test
	public void testLogicBinaryExpressionStruct_OR(){
		try {
			ASTNode node = Mockito.mock(LogicBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StructType(
					null,
					new Member[] { new Member("",
							new LongType()) })));
			
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new StructType(
					null,
					new Member[] { new Member("",
							new LongType()) })));
			
			when(converter.icg.generateTempIdentifier(any(StructType.class)))
			.thenReturn(new IdentifierData("tmp", new StructType(
					null,
					new Member[] { new Member("",
							new LongType()) })));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}

	
	
	/**
	 * Test for two Logic Binary Expressions, which both have the type Array and the Operator OR in between.
	 */
	@Test
	public void testLogicBinaryExpressionArray_OR(){
		try {
			ASTNode node = Mockito.mock(LogicBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new ArrayType(new LongType(), 0)));
			
			verify(converter.icg).processNode(any(LogicBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new ArrayType(new LongType(), 0)));
			
			when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
			.thenReturn(new IdentifierData("tmp", new ArrayType(new LongType(), 0)));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}

}
