package swp_compiler_ss13.javabite.codegen;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode.BinaryOperator;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.javabite.codegen.converters.ArithmeticBinaryExpressionNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public class ArithmeticBinaryExpressionNodeConverterTest {
	
ArithmeticBinaryExpressionNodeConverter converter;
	
	@Before
	public void setUp() throws Exception {
		converter = new ArithmeticBinaryExpressionNodeConverter();

		converter.icg = Mockito
				.mock(IntermediateCodeGeneratorJb.class);
	}

	
	/**
	 * Test an Arithmetic Binary Expression, where both sides have the type Long with the Operator Minus in between.
	 * @throws IntermediateCodeGeneratorException 
	 */
	@Test
	public void testArithmeticBinaryExpressionLong_SUB() throws IntermediateCodeGeneratorException{
			ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
		
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("test1", new LongType()) ,
					new IdentifierData("test2", new LongType()));
			IdentifierData tmp = new IdentifierData("tmp", new LongType()); 
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
			.thenReturn(tmp);
				
			when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
			
			converter.convert(node);
			
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.SUB_LONG, "test1",
					"test2", "tmp"));
			verify(converter.icg).pushIdentifierData(tmp);
	}
//	
//	
//	/**
//	 * Test an Arithmetic Binary Expression, where both sides have the type Double with the Operator Minus in between.
//	 */
//	@Test
//	public void testArithmeticBinaryExpressionDouble_SUB(){
//		try {
//			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//	
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new DoubleType()));
//			
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new DoubleType()));
//			
//			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
//			.thenReturn(new IdentifierData("tmp", new DoubleType()));
//			verify(converter).convert(node);
//			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.SUB_LONG, "#0",
//					"test", "tmp"));
//			verify(converter.icg).pushIdentifierData(new IdentifierData("tmp2", new DoubleType()));
//		}catch(IntermediateCodeGeneratorException e) {
//			fail();
//		}
//	}
//	
//	
//	/**
//	 * Test an Arithmetic Binary Expression, where both sides have the type Boolean with the Operator Minus in between.
//	 */
//	@Test
//	public void testArithmeticBinaryExpressionBoolean_SUB(){
//		try {
//			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//	
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new BooleanType()));
//			
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new BooleanType()));
//			
//			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
//			.thenReturn(new IdentifierData("tmp", new BooleanType()));
//			verify(converter).convert(node);
//			fail();
//		}catch(IntermediateCodeGeneratorException e) {
//			
//		}
//	}
//	
//	
//	/**
//	 * Test an Arithmetic Binary Expression, where both sides have the type String with the Operator Minus in between.
//	 */
//	@Test
//	public void testArithmeticBinaryExpressionString_SUB(){
//		try {
//			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//	
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StringType(0l)));
//			
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new StringType(0l)));
//			
//			when(converter.icg.generateTempIdentifier(any(StringType.class)))
//			.thenReturn(new IdentifierData("tmp", new StringType(0l)));
//			verify(converter).convert(node);
//			fail();
//		}catch(IntermediateCodeGeneratorException e) {
//			
//		}
//	}
//	
//	
//	
//	/**
//	 * Test an Arithmetic Binary Expression, where both sides have the type Struct with the Operator Minus in between.
//	 */
//	@Test
//	public void testArithmeticBinaryExpressionStruct_SUB(){
//		try {
//			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//	
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StructType(
//					null,
//					new Member[] { new Member("",
//							new LongType()) })));
//			
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new StructType(
//					null,
//					new Member[] { new Member("",
//							new LongType()) })));
//			
//			when(converter.icg.generateTempIdentifier(any(StructType.class)))
//			.thenReturn(new IdentifierData("tmp", new StructType(
//					null,
//					new Member[] { new Member("",
//							new LongType()) })));
//			verify(converter).convert(node);
//			fail();
//		}catch(IntermediateCodeGeneratorException e) {
//			
//		}
//	}
//	
//	
//	
//	/**
//	 * Test an Arithmetic Binary Expression, where both sides have the type Array with the Operator Minus in between.
//	 */
//	@Test
//	public void testArithmeticBinaryExpressionArray_SUB(){
//		try {
//			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//	
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new ArrayType(new LongType(), 0)));
//			
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new ArrayType(new LongType(), 0)));
//			
//			when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
//			.thenReturn(new IdentifierData("tmp", new ArrayType(new LongType(), 0)));
//			verify(converter).convert(node);
//			fail();
//		}catch(IntermediateCodeGeneratorException e) {
//			
//		}
//	}
//	
//	
//	
//	/**
//	 * Test an Arithmetic Binary Expression, where both sides have the type Long with the Operator Plus in between.
//	 */
//	@Test
//	public void testArithmeticBinaryExpressionLong_ADD(){
//		try {
//			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//	
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
//			
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
//			
//			when(converter.icg.generateTempIdentifier(any(LongType.class)))
//			.thenReturn(new IdentifierData("tmp", new LongType()));
//			verify(converter).convert(node);
//			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.ADD_LONG, "#0",
//					"test", "tmp"));
//			verify(converter.icg).pushIdentifierData(new IdentifierData("tmp2", new LongType()));
//		}catch(IntermediateCodeGeneratorException e) {
//			fail();
//		}
//	}
//
//	
//	
//	
//	/**
//	 * Test an Arithmetic Binary Expression, where both sides have the type Double with the Operator Plus in between.
//	 */
//	@Test
//	public void testArithmeticBinaryExpressionDouble_ADD(){
//		try {
//			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//	
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new DoubleType()));
//			
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new DoubleType()));
//			
//			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
//			.thenReturn(new IdentifierData("tmp", new DoubleType()));
//			verify(converter).convert(node);
//			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.ADD_LONG, "#0",
//					"test", "tmp"));
//			verify(converter.icg).pushIdentifierData(new IdentifierData("tmp2", new DoubleType()));
//		}catch(IntermediateCodeGeneratorException e) {
//			fail();
//		}
//	}
//
//
//	
//	/**
//	 * Test an Arithmetic Binary Expression, where both sides have the type Boolean with the Operator Plus in between.
//	 */
//	@Test
//	public void testArithmeticBinaryExpressionBoolean_ADD(){
//		try {
//			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//	
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new BooleanType()));
//			
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new BooleanType()));
//			
//			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
//			.thenReturn(new IdentifierData("tmp", new BooleanType()));
//			verify(converter).convert(node);
//			fail();
//		}catch(IntermediateCodeGeneratorException e) {
//			
//		}
//	}
//
//	
//	
//	/**
//	 * Test an Arithmetic Binary Expression, where both sides have the type String with the Operator Plus in between.
//	 */
//	@Test
//	public void testArithmeticBinaryExpressionString_ADD(){
//		try {
//			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//	
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StringType(0l)));
//			
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new StringType(0l)));
//			
//			when(converter.icg.generateTempIdentifier(any(StringType.class)))
//			.thenReturn(new IdentifierData("tmp", new StringType(0l)));
//			verify(converter).convert(node);
//			fail();
//		}catch(IntermediateCodeGeneratorException e) {
//			
//		}
//	}
//
//
//	
//	/**
//	 * Test an Arithmetic Binary Expression, where both sides have the type Struct with the Operator Plus in between.
//	 */
//	@Test
//	public void testArithmeticBinaryExpressionStruct_ADD(){
//		try {
//			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//	
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StructType(
//					null,
//					new Member[] { new Member("",
//							new LongType()) })));
//			
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new StructType(
//					null,
//					new Member[] { new Member("",
//							new LongType()) })));
//			
//			when(converter.icg.generateTempIdentifier(any(StructType.class)))
//			.thenReturn(new IdentifierData("tmp", new StructType(
//					null,
//					new Member[] { new Member("",
//							new LongType()) })));
//			verify(converter).convert(node);
//			fail();
//		}catch(IntermediateCodeGeneratorException e) {
//			
//		}
//	}
//
//	
//	/**
//	 * Test an Arithmetic Binary Expression, where both sides have the type Array with the Operator Plus in between.
//	 */
//	@Test
//	public void testArithmeticBinaryExpressionArray_ADD(){
//		try {
//			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//	
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new ArrayType(new LongType(), 0)));
//			
//			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new ArrayType(new LongType(), 0)));
//			
//			when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
//			.thenReturn(new IdentifierData("tmp", new ArrayType(new LongType(), 0)));
//			verify(converter).convert(node);
//			fail();
//		}catch(IntermediateCodeGeneratorException e) {
//			
//		}
//	}
//
//
//
//		/**
//		 * Test an Arithmetic Binary Expression, where both sides have the type Long with the Operator multiply (times) in between.
//		 */
//		@Test
//		public void testArithmeticBinaryExpressionLong_MUL(){
//			try {
//				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//		
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
//				
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
//				
//				when(converter.icg.generateTempIdentifier(any(LongType.class)))
//				.thenReturn(new IdentifierData("tmp", new LongType()));
//				verify(converter).convert(node);
//				verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.MUL_LONG, "#0",
//						"test", "tmp"));
//				verify(converter.icg).pushIdentifierData(new IdentifierData("tmp2", new LongType()));
//			}catch(IntermediateCodeGeneratorException e) {
//				fail();
//			}
//		}
//		
//		
//		/**
//		 * Test an Arithmetic Binary Expression, where both sides have the type Double with the Operator multiply (times) in between.
//		 */
//		@Test
//		public void testArithmeticBinaryExpressionDouble_MUL(){
//			try {
//				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//		
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new DoubleType()));
//				
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new DoubleType()));
//				
//				when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
//				.thenReturn(new IdentifierData("tmp", new DoubleType()));
//				verify(converter).convert(node);
//				verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.MUL_LONG, "#0",
//						"test", "tmp"));
//				verify(converter.icg).pushIdentifierData(new IdentifierData("tmp2", new DoubleType()));
//			}catch(IntermediateCodeGeneratorException e) {
//				fail();
//			}
//		}
//		
//		
//		/**
//		 * Test an Arithmetic Binary Expression, where both sides have the type Boolean with the Operator multiply (times) in between.
//		 */
//		@Test
//		public void testArithmeticBinaryExpressionBoolean_MUL(){
//			try {
//				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//		
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new BooleanType()));
//				
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new BooleanType()));
//				
//				when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
//				.thenReturn(new IdentifierData("tmp", new BooleanType()));
//				verify(converter).convert(node);
//				fail();
//			}catch(IntermediateCodeGeneratorException e) {
//				
//			}
//		}
//		
//		
//		
//		/**
//		 * Test an Arithmetic Binary Expression, where both sides have the type String with the Operator multiply (times) in between.
//		 */
//		@Test
//		public void testArithmeticBinaryExpressionString_MUL(){
//			try {
//				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//		
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StringType(0l)));
//				
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new StringType(0l)));
//				
//				when(converter.icg.generateTempIdentifier(any(StringType.class)))
//				.thenReturn(new IdentifierData("tmp", new StringType(0l)));
//				verify(converter).convert(node);
//				fail();
//			}catch(IntermediateCodeGeneratorException e) {
//				
//			}
//		}
//		
//		
//		/**
//		 * Test an Arithmetic Binary Expression, where both sides have the type Struct with the Operator multiply (times) in between.
//		 */
//		@Test
//		public void testArithmeticBinaryExpressionStruct_MUL(){
//			try {
//				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//		
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StructType(
//						null,
//						new Member[] { new Member("",
//								new LongType()) })));
//				
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new StructType(
//						null,
//						new Member[] { new Member("",
//								new LongType()) })));
//				
//				when(converter.icg.generateTempIdentifier(any(StructType.class)))
//				.thenReturn(new IdentifierData("tmp", new StructType(
//						null,
//						new Member[] { new Member("",
//								new LongType()) })));
//				verify(converter).convert(node);
//				fail();
//			}catch(IntermediateCodeGeneratorException e) {
//				
//			}
//		}
//		
//		
//		/**
//		 * Test an Arithmetic Binary Expression, where both sides have the type Array with the Operator multiply (times) in between.
//		 */
//		@Test
//		public void testArithmeticBinaryExpressionArray_MUL(){
//			try {
//				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//		
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new ArrayType(new LongType(), 0)));
//				
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new ArrayType(new LongType(), 0)));
//				
//				when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
//				.thenReturn(new IdentifierData("tmp", new ArrayType(new LongType(), 0)));
//				verify(converter).convert(node);
//				fail();
//			}catch(IntermediateCodeGeneratorException e) {
//				
//			}
//		}
//		
//		
//		
//		/**
//		 * Test an Arithmetic Binary Expression, where both sides have the type Long with the Operator divide in between.
//		 */
//		@Test
//		public void testArithmeticBinaryExpressionLong_DIV(){
//			try {
//				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//		
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
//				
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
//				
//				when(converter.icg.generateTempIdentifier(any(LongType.class)))
//				.thenReturn(new IdentifierData("tmp", new LongType()));
//				verify(converter).convert(node);
//				verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.DIV_LONG, "#0",
//						"test", "tmp"));
//				verify(converter.icg).pushIdentifierData(new IdentifierData("tmp2", new LongType()));
//			}catch(IntermediateCodeGeneratorException e) {
//				fail();
//			}
//		}
//		
//		
//		/**
//		 * Test an Arithmetic Binary Expression, where both sides have the type Double with the Operator divide in between.
//		 */
//		@Test
//		public void testArithmeticBinaryExpressionDouble_DIV(){
//			try {
//				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//		
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new DoubleType()));
//				
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new DoubleType()));
//				
//				when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
//				.thenReturn(new IdentifierData("tmp", new DoubleType()));
//				verify(converter).convert(node);
//				verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.DIV_LONG, "#0",
//						"test", "tmp"));
//				verify(converter.icg).pushIdentifierData(new IdentifierData("tmp2", new DoubleType()));
//			}catch(IntermediateCodeGeneratorException e) {
//				fail();
//			}
//		}
//		
//		
//		/**
//		 * Test an Arithmetic Binary Expression, where both sides have the type Boolean with the Operator divide in between.
//		 */
//		@Test
//		public void testArithmeticBinaryExpressionBoolean_DIV(){
//			try {
//				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//		
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new BooleanType()));
//				
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new BooleanType()));
//				
//				when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
//				.thenReturn(new IdentifierData("tmp", new BooleanType()));
//				verify(converter).convert(node);
//				fail();
//			}catch(IntermediateCodeGeneratorException e) {
//				
//			}
//		}
//		
//		
//		/**
//		 * Test an Arithmetic Binary Expression, where both sides have the type String with the Operator divide in between.
//		 */
//		@Test
//		public void testArithmeticBinaryExpressionString_DIV(){
//			try {
//				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//		
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StringType(0l)));
//				
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new StringType(0l)));
//				
//				when(converter.icg.generateTempIdentifier(any(StringType.class)))
//				.thenReturn(new IdentifierData("tmp", new StringType(0l)));
//				verify(converter).convert(node);
//				fail();
//			}catch(IntermediateCodeGeneratorException e) {
//				
//			}
//		}
//		
//		
//		/**
//		 * Test an Arithmetic Binary Expression, where both sides have the type Struct with the Operator divide in between.
//		 */
//		@Test
//		public void testArithmeticBinaryExpressionStruct_DIV(){
//			try {
//				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//		
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StructType(
//						null,
//						new Member[] { new Member("",
//								new LongType()) })));
//				
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new StructType(
//						null,
//						new Member[] { new Member("",
//								new LongType()) })));
//				
//				when(converter.icg.generateTempIdentifier(any(StructType.class)))
//				.thenReturn(new IdentifierData("tmp", new StructType(
//						null,
//						new Member[] { new Member("",
//								new LongType()) })));
//				verify(converter).convert(node);
//				verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.DIV_LONG, "#0",
//						"test", "tmp"));
//			}catch(IntermediateCodeGeneratorException e) {
//				fail();
//			}
//		}
//		
//		
//		/**
//		 * Test an Arithmetic Binary Expression, where both sides have the type Array with the Operator divide in between.
//		 */
//		@Test
//		public void testArithmeticBinaryExpressionArray_DIV(){
//			try {
//				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
//		
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new ArrayType(new LongType(), 0)));
//				
//				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
//				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new ArrayType(new LongType(), 0)));
//				
//				when(converter.icg.generateTempIdentifier(any(StructType.class)))
//				.thenReturn(new IdentifierData("tmp", new ArrayType(new LongType(), 0)));
//				verify(converter).convert(node);
//				fail();
//			}catch(IntermediateCodeGeneratorException e) {
//				
//			}
//		}
		

}