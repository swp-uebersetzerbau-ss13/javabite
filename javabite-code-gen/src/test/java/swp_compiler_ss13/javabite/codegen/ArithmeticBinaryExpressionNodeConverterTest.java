package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.derived.ArrayType;
import swp_compiler_ss13.common.types.derived.Member;
import swp_compiler_ss13.common.types.derived.StructType;
import swp_compiler_ss13.common.types.primitive.BooleanType;
import swp_compiler_ss13.common.types.primitive.DoubleType;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.common.types.primitive.StringType;
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

	
	//SUB
	@Test
	public void testArithmeticBinaryExpressionLong_SUB(){
		try {
			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
			
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
			.thenReturn(new IdentifierData("tmp", new LongType()));
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.SUB_LONG, "#0",
					"test", "tmp"));
			verify(converter.icg).pushIdentifierData(new IdentifierData("tmp2", new LongType()));
		}catch(IntermediateCodeGeneratorException e) {
			fail();
		}
	}
	
	
	@Test
	public void testArithmeticBinaryExpressionDouble_SUB(){
		try {
			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new DoubleType()));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new DoubleType()));
			
			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
			.thenReturn(new IdentifierData("tmp", new DoubleType()));
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.SUB_LONG, "#0",
					"test", "tmp"));
			verify(converter.icg).pushIdentifierData(new IdentifierData("tmp2", new DoubleType()));
		}catch(IntermediateCodeGeneratorException e) {
			fail();
		}
	}
	
	
	
	@Test
	public void testArithmeticBinaryExpressionBoolean_SUB(){
		try {
			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new BooleanType()));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new BooleanType()));
			
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
			.thenReturn(new IdentifierData("tmp", new BooleanType()));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	
	@Test
	public void testArithmeticBinaryExpressionString(){
		try {
			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StringType(0l)));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new StringType(0l)));
			
			when(converter.icg.generateTempIdentifier(any(StringType.class)))
			.thenReturn(new IdentifierData("tmp", new StringType(0l)));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	
	@Test
	public void testArithmeticBinaryExpressionStruct_SUB(){
		try {
			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StructType(
					null,
					new Member[] { new Member("",
							new LongType()) })));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
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
	
	@Test
	public void testArithmeticBinaryExpressionArray_SUB(){
		try {
			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new ArrayType(new LongType(), 0)));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new ArrayType(new LongType(), 0)));
			
			when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
			.thenReturn(new IdentifierData("tmp", new ArrayType(new LongType(), 0)));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}
	
	
	
	//ADD
	@Test
	public void testArithmeticBinaryExpressionLong_ADD(){
		try {
			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
			
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
			.thenReturn(new IdentifierData("tmp", new LongType()));
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.ADD_LONG, "#0",
					"test", "tmp"));
			verify(converter.icg).pushIdentifierData(new IdentifierData("tmp2", new LongType()));
		}catch(IntermediateCodeGeneratorException e) {
			fail();
		}
	}


	@Test
	public void testArithmeticBinaryExpressionDouble_ADD(){
		try {
			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new DoubleType()));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new DoubleType()));
			
			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
			.thenReturn(new IdentifierData("tmp", new DoubleType()));
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.ADD_LONG, "#0",
					"test", "tmp"));
			verify(converter.icg).pushIdentifierData(new IdentifierData("tmp2", new DoubleType()));
		}catch(IntermediateCodeGeneratorException e) {
			fail();
		}
	}



	@Test
	public void testArithmeticBinaryExpressionBoolean_ADD(){
		try {
			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new BooleanType()));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new BooleanType()));
			
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
			.thenReturn(new IdentifierData("tmp", new BooleanType()));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}


	@Test
	public void testArithmeticBinaryExpressionString_ADD(){
		try {
			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StringType(0l)));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new StringType(0l)));
			
			when(converter.icg.generateTempIdentifier(any(StringType.class)))
			.thenReturn(new IdentifierData("tmp", new StringType(0l)));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}


	@Test
	public void testArithmeticBinaryExpressionStruct_ADD(){
		try {
			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StructType(
					null,
					new Member[] { new Member("",
							new LongType()) })));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
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

	@Test
	public void testArithmeticBinaryExpressionArray_ADD(){
		try {
			ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
	
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new ArrayType(new LongType(), 0)));
			
			verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new ArrayType(new LongType(), 0)));
			
			when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
			.thenReturn(new IdentifierData("tmp", new ArrayType(new LongType(), 0)));
			verify(converter).convert(node);
			fail();
		}catch(IntermediateCodeGeneratorException e) {
			
		}
	}



//MUL
		@Test
		public void testArithmeticBinaryExpressionLong_MUL(){
			try {
				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
		
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
				
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
				
				when(converter.icg.generateTempIdentifier(any(LongType.class)))
				.thenReturn(new IdentifierData("tmp", new LongType()));
				verify(converter).convert(node);
				verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.MUL_LONG, "#0",
						"test", "tmp"));
				verify(converter.icg).pushIdentifierData(new IdentifierData("tmp2", new LongType()));
			}catch(IntermediateCodeGeneratorException e) {
				fail();
			}
		}
		
		
		@Test
		public void testArithmeticBinaryExpressionDouble_MUL(){
			try {
				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
		
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new DoubleType()));
				
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new DoubleType()));
				
				when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
				.thenReturn(new IdentifierData("tmp", new DoubleType()));
				verify(converter).convert(node);
				verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.MUL_LONG, "#0",
						"test", "tmp"));
				verify(converter.icg).pushIdentifierData(new IdentifierData("tmp2", new DoubleType()));
			}catch(IntermediateCodeGeneratorException e) {
				fail();
			}
		}
		
		
		
		@Test
		public void testArithmeticBinaryExpressionBoolean_MUL(){
			try {
				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
		
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new BooleanType()));
				
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new BooleanType()));
				
				when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
				.thenReturn(new IdentifierData("tmp", new BooleanType()));
				verify(converter).convert(node);
				fail();
			}catch(IntermediateCodeGeneratorException e) {
				
			}
		}
		
		
		@Test
		public void testArithmeticBinaryExpressionString_MUL(){
			try {
				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
		
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StringType(0l)));
				
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new StringType(0l)));
				
				when(converter.icg.generateTempIdentifier(any(StringType.class)))
				.thenReturn(new IdentifierData("tmp", new StringType(0l)));
				verify(converter).convert(node);
				fail();
			}catch(IntermediateCodeGeneratorException e) {
				
			}
		}
		
		
		@Test
		public void testArithmeticBinaryExpressionStruct_MUL(){
			try {
				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
		
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StructType(
						null,
						new Member[] { new Member("",
								new LongType()) })));
				
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
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
		
		@Test
		public void testArithmeticBinaryExpressionArray_MUL(){
			try {
				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
		
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new ArrayType(new LongType(), 0)));
				
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new ArrayType(new LongType(), 0)));
				
				when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
				.thenReturn(new IdentifierData("tmp", new ArrayType(new LongType(), 0)));
				verify(converter).convert(node);
				fail();
			}catch(IntermediateCodeGeneratorException e) {
				
			}
		}
		
		
		//DIV
		@Test
		public void testArithmeticBinaryExpressionLong_DIV(){
			try {
				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
		
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
				
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
				
				when(converter.icg.generateTempIdentifier(any(LongType.class)))
				.thenReturn(new IdentifierData("tmp", new LongType()));
				verify(converter).convert(node);
				verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.DIV_LONG, "#0",
						"test", "tmp"));
				verify(converter.icg).pushIdentifierData(new IdentifierData("tmp2", new LongType()));
			}catch(IntermediateCodeGeneratorException e) {
				fail();
			}
		}
		
		
		@Test
		public void testArithmeticBinaryExpressionDouble_DIV(){
			try {
				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
		
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new DoubleType()));
				
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new DoubleType()));
				
				when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
				.thenReturn(new IdentifierData("tmp", new DoubleType()));
				verify(converter).convert(node);
				verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.DIV_LONG, "#0",
						"test", "tmp"));
				verify(converter.icg).pushIdentifierData(new IdentifierData("tmp2", new DoubleType()));
			}catch(IntermediateCodeGeneratorException e) {
				fail();
			}
		}
		
		
		
		@Test
		public void testArithmeticBinaryExpressionBoolean_DIV(){
			try {
				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
		
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new BooleanType()));
				
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new BooleanType()));
				
				when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
				.thenReturn(new IdentifierData("tmp", new BooleanType()));
				verify(converter).convert(node);
				fail();
			}catch(IntermediateCodeGeneratorException e) {
				
			}
		}
		
		
		@Test
		public void testArithmeticBinaryExpressionString_DIV(){
			try {
				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
		
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StringType(0l)));
				
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new StringType(0l)));
				
				when(converter.icg.generateTempIdentifier(any(StringType.class)))
				.thenReturn(new IdentifierData("tmp", new StringType(0l)));
				verify(converter).convert(node);
				fail();
			}catch(IntermediateCodeGeneratorException e) {
				
			}
		}
		
		
		@Test
		public void testArithmeticBinaryExpressionStruct_DIV(){
			try {
				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
		
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new StructType(
						null,
						new Member[] { new Member("",
								new LongType()) })));
				
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
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
				verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.DIV_LONG, "#0",
						"test", "tmp"));
			}catch(IntermediateCodeGeneratorException e) {
				fail();
			}
		}
		
		@Test
		public void testArithmeticBinaryExpressionArray_DIV(){
			try {
				ASTNode node = Mockito.mock(ArithmeticBinaryExpressionNode.class);
		
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new ArrayType(new LongType(), 0)));
				
				verify(converter.icg).processNode(any(ArithmeticBinaryExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new ArrayType(new LongType(), 0)));
				
				when(converter.icg.generateTempIdentifier(any(StructType.class)))
				.thenReturn(new IdentifierData("tmp", new ArrayType(new LongType(), 0)));
				verify(converter).convert(node);
				fail();
			}catch(IntermediateCodeGeneratorException e) {
				
			}
		}
		

}
