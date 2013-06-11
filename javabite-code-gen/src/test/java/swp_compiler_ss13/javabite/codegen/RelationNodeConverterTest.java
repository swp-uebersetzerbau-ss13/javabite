package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.binary.RelationExpressionNode;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.derived.ArrayType;
import swp_compiler_ss13.common.types.primitive.BooleanType;
import swp_compiler_ss13.common.types.primitive.DoubleType;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.common.types.primitive.StringType;
import swp_compiler_ss13.javabite.codegen.converters.RelationNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public class RelationNodeConverterTest {
	
RelationNodeConverter converter;
	
	@Before
	public void setUp() throws Exception {
		converter = new RelationNodeConverter();

		converter.icg = Mockito
				.mock(IntermediateCodeGeneratorJb.class);
	}

	
	//LONG
	@Test
	public void testRelationLong_EQUAL(){
		try {
			ASTNode node = Mockito.mock(RelationExpressionNode.class);
	
			verify(converter.icg).processNode(any(RelationExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
			
			verify(converter.icg).processNode(any(RelationExpressionNode.class));
			when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
			
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
			.thenReturn(new IdentifierData("tmp", new LongType()));
			verify(converter).convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_LONG_E,
					"tmp1", "tmp2", "tmp3"));
			verify(converter.icg).pushIdentifierData(new IdentifierData("tmp3", new LongType()));
		}catch(IntermediateCodeGeneratorException e) {
			fail();
		}
	}
	
	
		@Test
		public void testRelationLong_GREATERTHAN(){
			try {
				ASTNode node = Mockito.mock(RelationExpressionNode.class);
		
				verify(converter.icg).processNode(any(RelationExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
				
				verify(converter.icg).processNode(any(RelationExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
				
				when(converter.icg.generateTempIdentifier(any(LongType.class)))
				.thenReturn(new IdentifierData("tmp", new LongType()));
				verify(converter).convert(node);
				verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_LONG_G,
						"tmp1", "tmp2", "tmp3"));
				verify(converter.icg).pushIdentifierData(new IdentifierData("tmp3", new LongType()));
			}catch(IntermediateCodeGeneratorException e) {
				fail();
			}
		}
		
		
		
		@Test
		public void testRelationLong_LESSTHAN(){
			try {
				ASTNode node = Mockito.mock(RelationExpressionNode.class);
		
				verify(converter.icg).processNode(any(RelationExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
				
				verify(converter.icg).processNode(any(RelationExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
				
				when(converter.icg.generateTempIdentifier(any(LongType.class)))
				.thenReturn(new IdentifierData("tmp", new LongType()));
				verify(converter).convert(node);
				verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_LONG_L,
						"tmp1", "tmp2", "tmp3"));
				verify(converter.icg).pushIdentifierData(new IdentifierData("tmp3", new LongType()));
			}catch(IntermediateCodeGeneratorException e) {
				fail();
			}
		}
		
		
		@Test
		public void testRelationLong_GREATERTHANEQUAL(){
			try {
				ASTNode node = Mockito.mock(RelationExpressionNode.class);
		
				verify(converter.icg).processNode(any(RelationExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
				
				verify(converter.icg).processNode(any(RelationExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
				
				when(converter.icg.generateTempIdentifier(any(LongType.class)))
				.thenReturn(new IdentifierData("tmp", new LongType()));
				verify(converter).convert(node);
				verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_LONG_GE,
						"tmp1", "tmp2", "tmp3"));
				verify(converter.icg).pushIdentifierData(new IdentifierData("tmp3", new LongType()));
			}catch(IntermediateCodeGeneratorException e) {
				fail();
			}
		}
		
		
		@Test
		public void testRelationLong_LESSTHANEQUAL(){
			try {
				ASTNode node = Mockito.mock(RelationExpressionNode.class);
		
				verify(converter.icg).processNode(any(RelationExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
				
				verify(converter.icg).processNode(any(RelationExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
				
				when(converter.icg.generateTempIdentifier(any(LongType.class)))
				.thenReturn(new IdentifierData("tmp", new LongType()));
				verify(converter).convert(node);
				verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_LONG_LE,
						"tmp1", "tmp2", "tmp3"));
				verify(converter.icg).pushIdentifierData(new IdentifierData("tmp3", new LongType()));
			}catch(IntermediateCodeGeneratorException e) {
				fail();
			}
		}
		
		
		
		//DOUBLE
		@Test
		public void testRelationDouble_EQUAL(){
			try {
				ASTNode node = Mockito.mock(RelationExpressionNode.class);
		
				verify(converter.icg).processNode(any(RelationExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
				
				verify(converter.icg).processNode(any(RelationExpressionNode.class));
				when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
				
				when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
				.thenReturn(new IdentifierData("tmp", new LongType()));
				verify(converter).convert(node);
				verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_DOUBLE_E,
						"tmp1", "tmp2", "tmp3"));
				verify(converter.icg).pushIdentifierData(new IdentifierData("tmp3", new LongType()));
			}catch(IntermediateCodeGeneratorException e) {
				fail();
			}
		}
		
		
			@Test
			public void testRelationDouble_GREATERTHAN(){
				try {
					ASTNode node = Mockito.mock(RelationExpressionNode.class);
			
					verify(converter.icg).processNode(any(RelationExpressionNode.class));
					when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
					
					verify(converter.icg).processNode(any(RelationExpressionNode.class));
					when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
					
					when(converter.icg.generateTempIdentifier(any(LongType.class)))
					.thenReturn(new IdentifierData("tmp", new DoubleType()));
					verify(converter).convert(node);
					verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_DOUBLE_G,
							"tmp1", "tmp2", "tmp3"));
					verify(converter.icg).pushIdentifierData(new IdentifierData("tmp3", new LongType()));
				}catch(IntermediateCodeGeneratorException e) {
					fail();
				}
			}
			
			
			
			@Test
			public void testRelationDouble_LESSTHAN(){
				try {
					ASTNode node = Mockito.mock(RelationExpressionNode.class);
			
					verify(converter.icg).processNode(any(RelationExpressionNode.class));
					when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
					
					verify(converter.icg).processNode(any(RelationExpressionNode.class));
					when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
					
					when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
					.thenReturn(new IdentifierData("tmp", new LongType()));
					verify(converter).convert(node);
					verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_DOUBLE_L,
							"tmp1", "tmp2", "tmp3"));
					verify(converter.icg).pushIdentifierData(new IdentifierData("tmp3", new LongType()));
				}catch(IntermediateCodeGeneratorException e) {
					fail();
				}
			}
			
			
			@Test
			public void testRelationDouble_GREATERTHANEQUAL(){
				try {
					ASTNode node = Mockito.mock(RelationExpressionNode.class);
			
					verify(converter.icg).processNode(any(RelationExpressionNode.class));
					when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
					
					verify(converter.icg).processNode(any(RelationExpressionNode.class));
					when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
					
					when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
					.thenReturn(new IdentifierData("tmp", new LongType()));
					verify(converter).convert(node);
					verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_DOUBLE_GE,
							"tmp1", "tmp2", "tmp3"));
					verify(converter.icg).pushIdentifierData(new IdentifierData("tmp3", new LongType()));
				}catch(IntermediateCodeGeneratorException e) {
					fail();
				}
			}
			
			
			@Test
			public void testRelationDouble_LESSTHANEQUAL(){
				try {
					ASTNode node = Mockito.mock(RelationExpressionNode.class);
			
					verify(converter.icg).processNode(any(RelationExpressionNode.class));
					when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
					
					verify(converter.icg).processNode(any(RelationExpressionNode.class));
					when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
					
					when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
					.thenReturn(new IdentifierData("tmp", new LongType()));
					verify(converter).convert(node);
					verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_DOUBLE_LE,
							"tmp1", "tmp2", "tmp3"));
					verify(converter.icg).pushIdentifierData(new IdentifierData("tmp3", new LongType()));
				}catch(IntermediateCodeGeneratorException e) {
					fail();
				}
			}
	
	
	
			
			//BOOLEAN
			@Test
			public void testRelationBoolean_EQUAL(){
				try {
					ASTNode node = Mockito.mock(RelationExpressionNode.class);
			
					verify(converter.icg).processNode(any(RelationExpressionNode.class));
					when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
					
					verify(converter.icg).processNode(any(RelationExpressionNode.class));
					when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
					
					when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
					.thenReturn(new IdentifierData("tmp", new LongType()));
					verify(converter).convert(node);
					fail();
				}catch(IntermediateCodeGeneratorException e) {
					
				}
			}
			
			
				@Test
				public void testRelationBoolean_GREATERTHAN(){
					try {
						ASTNode node = Mockito.mock(RelationExpressionNode.class);
				
						verify(converter.icg).processNode(any(RelationExpressionNode.class));
						when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
						
						verify(converter.icg).processNode(any(RelationExpressionNode.class));
						when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
						
						when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
						.thenReturn(new IdentifierData("tmp", new DoubleType()));
						verify(converter).convert(node);
						fail();
					}catch(IntermediateCodeGeneratorException e) {
						
					}
				}
				
				
				
				@Test
				public void testRelationBoolean_LESSTHAN(){
					try {
						ASTNode node = Mockito.mock(RelationExpressionNode.class);
				
						verify(converter.icg).processNode(any(RelationExpressionNode.class));
						when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
						
						verify(converter.icg).processNode(any(RelationExpressionNode.class));
						when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
						
						when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
						.thenReturn(new IdentifierData("tmp", new LongType()));
						verify(converter).convert(node);
						fail();
					}catch(IntermediateCodeGeneratorException e) {
						
					}
				}
				
				
				@Test
				public void testRelationBoolean_GREATERTHANEQUAL(){
					try {
						ASTNode node = Mockito.mock(RelationExpressionNode.class);
				
						verify(converter.icg).processNode(any(RelationExpressionNode.class));
						when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
						
						verify(converter.icg).processNode(any(RelationExpressionNode.class));
						when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
						
						when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
						.thenReturn(new IdentifierData("tmp", new LongType()));
						verify(converter).convert(node);
						fail();
					}catch(IntermediateCodeGeneratorException e) {
						
					}
				}
				
				
				@Test
				public void testRelationBoolean_LESSTHANEQUAL(){
					try {
						ASTNode node = Mockito.mock(RelationExpressionNode.class);
				
						verify(converter.icg).processNode(any(RelationExpressionNode.class));
						when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
						
						verify(converter.icg).processNode(any(RelationExpressionNode.class));
						when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
						
						when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
						.thenReturn(new IdentifierData("tmp", new LongType()));
						verify(converter).convert(node);
						fail();
					}catch(IntermediateCodeGeneratorException e) {
						
					}
				}
		
		
		
				//STRING
				@Test
				public void testRelationString_EQUAL(){
					try {
						ASTNode node = Mockito.mock(RelationExpressionNode.class);
				
						verify(converter.icg).processNode(any(RelationExpressionNode.class));
						when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
						
						verify(converter.icg).processNode(any(RelationExpressionNode.class));
						when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
						
						when(converter.icg.generateTempIdentifier(any(StringType.class)))
						.thenReturn(new IdentifierData("tmp", new LongType()));
						verify(converter).convert(node);
						fail();
					}catch(IntermediateCodeGeneratorException e) {
						
					}
				}
				
				
					@Test
					public void testRelationString_GREATERTHAN(){
						try {
							ASTNode node = Mockito.mock(RelationExpressionNode.class);
					
							verify(converter.icg).processNode(any(RelationExpressionNode.class));
							when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
							
							verify(converter.icg).processNode(any(RelationExpressionNode.class));
							when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
							
							when(converter.icg.generateTempIdentifier(any(StringType.class)))
							.thenReturn(new IdentifierData("tmp", new DoubleType()));
							verify(converter).convert(node);
							fail();
						}catch(IntermediateCodeGeneratorException e) {
							
						}
					}
					
					
					
					@Test
					public void testRelationString_LESSTHAN(){
						try {
							ASTNode node = Mockito.mock(RelationExpressionNode.class);
					
							verify(converter.icg).processNode(any(RelationExpressionNode.class));
							when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
							
							verify(converter.icg).processNode(any(RelationExpressionNode.class));
							when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
							
							when(converter.icg.generateTempIdentifier(any(StringType.class)))
							.thenReturn(new IdentifierData("tmp", new LongType()));
							verify(converter).convert(node);
							fail();
						}catch(IntermediateCodeGeneratorException e) {
							
						}
					}
					
					
					@Test
					public void testRelationString_GREATERTHANEQUAL(){
						try {
							ASTNode node = Mockito.mock(RelationExpressionNode.class);
					
							verify(converter.icg).processNode(any(RelationExpressionNode.class));
							when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
							
							verify(converter.icg).processNode(any(RelationExpressionNode.class));
							when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
							
							when(converter.icg.generateTempIdentifier(any(StringType.class)))
							.thenReturn(new IdentifierData("tmp", new LongType()));
							verify(converter).convert(node);
							fail();
						}catch(IntermediateCodeGeneratorException e) {
							
						}
					}
					
					
					@Test
					public void testRelationString_LESSTHANEQUAL(){
						try {
							ASTNode node = Mockito.mock(RelationExpressionNode.class);
					
							verify(converter.icg).processNode(any(RelationExpressionNode.class));
							when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
							
							verify(converter.icg).processNode(any(RelationExpressionNode.class));
							when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
							
							when(converter.icg.generateTempIdentifier(any(StringType.class)))
							.thenReturn(new IdentifierData("tmp", new LongType()));
							verify(converter).convert(node);
							fail();
						}catch(IntermediateCodeGeneratorException e) {
							
						}
					}
			
			
					
					//ARRAY (noch zu verbessern)
					@Test
					public void testRelationArray_EQUAL(){
						try {
							ASTNode node = Mockito.mock(RelationExpressionNode.class);
					
							verify(converter.icg).processNode(any(RelationExpressionNode.class));
							when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
							
							verify(converter.icg).processNode(any(RelationExpressionNode.class));
							when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
							
							when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
							.thenReturn(new IdentifierData("tmp", new LongType()));
							verify(converter).convert(node);
							fail();
						}catch(IntermediateCodeGeneratorException e) {
							
						}
					}
					
					
						@Test
						public void testRelationArray_GREATERTHAN(){
							try {
								ASTNode node = Mockito.mock(RelationExpressionNode.class);
						
								verify(converter.icg).processNode(any(RelationExpressionNode.class));
								when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
								
								verify(converter.icg).processNode(any(RelationExpressionNode.class));
								when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
								
								when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
								.thenReturn(new IdentifierData("tmp", new DoubleType()));
								verify(converter).convert(node);
								fail();
							}catch(IntermediateCodeGeneratorException e) {
								
							}
						}
						
						
						
						@Test
						public void testRelationArray_LESSTHAN(){
							try {
								ASTNode node = Mockito.mock(RelationExpressionNode.class);
						
								verify(converter.icg).processNode(any(RelationExpressionNode.class));
								when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
								
								verify(converter.icg).processNode(any(RelationExpressionNode.class));
								when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
								
								when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
								.thenReturn(new IdentifierData("tmp", new LongType()));
								verify(converter).convert(node);
								fail();
							}catch(IntermediateCodeGeneratorException e) {
								
							}
						}
						
						
						@Test
						public void testRelationArray_GREATERTHANEQUAL(){
							try {
								ASTNode node = Mockito.mock(RelationExpressionNode.class);
						
								verify(converter.icg).processNode(any(RelationExpressionNode.class));
								when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
								
								verify(converter.icg).processNode(any(RelationExpressionNode.class));
								when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
								
								when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
								.thenReturn(new IdentifierData("tmp", new LongType()));
								verify(converter).convert(node);
								fail();
							}catch(IntermediateCodeGeneratorException e) {
								
							}
						}
						
						
						@Test
						public void testRelationArray_LESSTHANEQUAL(){
							try {
								ASTNode node = Mockito.mock(RelationExpressionNode.class);
						
								verify(converter.icg).processNode(any(RelationExpressionNode.class));
								when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test1", new LongType()));
								
								verify(converter.icg).processNode(any(RelationExpressionNode.class));
								when(converter.icg.popIdentifierData()).thenReturn(new IdentifierData("test2", new LongType()));
								
								when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
								.thenReturn(new IdentifierData("tmp", new LongType()));
								verify(converter).convert(node);
								fail();
							}catch(IntermediateCodeGeneratorException e) {
								
							}
						}
				
	

	//STRUCT?
		

}
