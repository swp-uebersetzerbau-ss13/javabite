package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
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
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public class CastingAst2CodeConverterTest {
	CastingAst2CodeConverter converter;

	@Before
	public void setUp() throws Exception {
		converter = new CastingAst2CodeConverter() {

			@Override
			public ASTNodeType getNodeType() {
				return null;
			}

			@Override
			public void convert(ASTNode node)
					throws IntermediateCodeGeneratorException {
			}
		};

		converter.icg = Mockito
				.mock(Ast2CodeConverterCompatibleGenerator.class);
	}

	@Test
	public void testCastLongToDouble() {
		try {
			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
					.thenReturn(new IdentifierData("tmp", new DoubleType()));
			converter.cast(new DoubleType(), new IdentifierData("test",
					new LongType()));
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.LONG_TO_DOUBLE, "test",
							Quadruple.EmptyArgument, "tmp"));
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
	}

	@Test
	public void testCastDoubleToLong() {
		try {
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
					.thenReturn(new IdentifierData("tmp", new LongType()));
			converter.cast(new LongType(), new IdentifierData("test",
					new DoubleType()));
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.DOUBLE_TO_LONG, "test",
							Quadruple.EmptyArgument, "tmp"));
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
	}

	@Test
	public void testCastLongToString() {
		try {
			when(converter.icg.generateTempIdentifier(any(StringType.class)))
					.thenReturn(new IdentifierData("tmp", new StringType(0l)));
			converter.cast(new StringType(0l), new IdentifierData("test",
					new LongType()));
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.LONG_TO_STRING, "test",
							Quadruple.EmptyArgument, "tmp"));
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
	}

	@Test
	public void testCastLongToBooleanFails() {
		try {
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
					.thenReturn(new IdentifierData("tmp", new BooleanType()));
			converter.cast(new BooleanType(), new IdentifierData("test",
					new LongType()));
			fail();
		} catch (IntermediateCodeGeneratorException e) {
		}
	}

	@Test
	public void testCastLongToArrayFails() {
		try {
			when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
					.thenReturn(
							new IdentifierData("tmp", new ArrayType(
									new LongType(), 0)));
			converter.cast(new ArrayType(new LongType(), 0),
					new IdentifierData("test", new LongType()));
			fail();
		} catch (IntermediateCodeGeneratorException e) {
		}
	}

	@Test
	public void testCastLongToStructFails() {
		try {
			when(converter.icg.generateTempIdentifier(any(StructType.class)))
					.thenReturn(
							new IdentifierData("tmp", new StructType(null,
									new Member[] { new Member("",
											new LongType()) })));
			converter.cast(new StructType(null, new Member[] { new Member("",
					new LongType()) }), new IdentifierData("test",
					new LongType()));
			fail();
		} catch (IntermediateCodeGeneratorException e) {
		}
	}

	@Test
	public void testCastDoubleToString() {
		try {
			when(converter.icg.generateTempIdentifier(any(StringType.class)))
					.thenReturn(new IdentifierData("tmp", new StringType(0l)));
			converter.cast(new StringType(0l), new IdentifierData("test",
					new DoubleType()));
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.DOUBLE_TO_STRING, "test",
							Quadruple.EmptyArgument, "tmp"));
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
	}

	@Test
	public void testCastDoubleToBooleanFails() {
		try {
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
					.thenReturn(new IdentifierData("tmp", new BooleanType()));
			converter.cast(new BooleanType(), new IdentifierData("test",
					new DoubleType()));
			fail();
		} catch (IntermediateCodeGeneratorException e) {
		}
	}

	@Test
	public void testCastDoubleToArrayFails() {
		try {
			when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
					.thenReturn(
							new IdentifierData("tmp", new ArrayType(
									new LongType(), 0)));
			converter.cast(new ArrayType(new LongType(), 0),
					new IdentifierData("test", new DoubleType()));
			fail();
		} catch (IntermediateCodeGeneratorException e) {
		}
	}

	@Test
	public void testCastDoubleToStructFails() {
		try {
			when(converter.icg.generateTempIdentifier(any(StructType.class)))
					.thenReturn(
							new IdentifierData("tmp", new StructType(null,
									new Member[] { new Member("",
											new DoubleType()) })));
			converter.cast(new StructType(null, new Member[] { new Member("",
					new LongType()) }), new IdentifierData("test",
					new DoubleType()));
			fail();
		} catch (IntermediateCodeGeneratorException e) {
		}
	}

	@Test
	public void testCastBooleanToLongFails() {
		try {
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
					.thenReturn(new IdentifierData("tmp", new LongType()));
			converter.cast(new LongType(), new IdentifierData("test",
					new BooleanType()));
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	@Test
	public void testCastBooleanToDoubleFails() {
		try {
			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
					.thenReturn(new IdentifierData("tmp", new DoubleType()));
			converter.cast(new DoubleType(), new IdentifierData("test",
					new BooleanType()));
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	@Test
	public void testCastBooleanToString() {
		try {
			when(converter.icg.generateTempIdentifier(any(StringType.class)))
					.thenReturn(new IdentifierData("tmp", new StringType(0l)));
			converter.cast(new StringType(0l), new IdentifierData("test",
					new BooleanType()));
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.BOOLEAN_TO_STRING, "test",
							Quadruple.EmptyArgument, "tmp"));
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
	}

	@Test
	public void testCastBooleanToStructFails() {
		try {
			when(converter.icg.generateTempIdentifier(any(StructType.class)))
					.thenReturn(
							new IdentifierData("tmp", new StructType(null,
									new Member[] { new Member("",
											new BooleanType()) })));
			converter.cast(new StructType(null, new Member[] { new Member("",
					new LongType()) }), new IdentifierData("test",
					new BooleanType()));
			fail();
		} catch (IntermediateCodeGeneratorException e) {
		}
	}

	@Test
	public void testCastBooleanToArrayFails() {
		try {
			when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
					.thenReturn(
							new IdentifierData("tmp", new ArrayType(
									new LongType(), 0)));
			converter.cast(new ArrayType(new LongType(), 0),
					new IdentifierData("test", new BooleanType()));
			fail();
		} catch (IntermediateCodeGeneratorException e) {
		}
	}

	@Test
	public void testCastStringToLongFails(){
		try{
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
					.thenReturn(new IdentifierData("tmp", new LongType()));
			converter.cast(new LongType(), new IdentifierData("test", new StringType(0l)));
		}catch (IntermediateCodeGeneratorException e){
			
		}
	}

	@Test
	 public void testCastStringToDoubleFails(){
	  try {
	   when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
	     .thenReturn(
	       new IdentifierData("tmp", new DoubleType()));
	   converter.cast(new DoubleType(), new IdentifierData("test",
	     new StringType(0l)));
	   fail();
	  } catch (IntermediateCodeGeneratorException e) {
	   
	  }
	 }
	
	 @Test
	 public void testCastStringToBooleanFails() {
	  try {
	   when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
	     .thenReturn(new IdentifierData("tmp", new BooleanType()));
	   converter.cast(new BooleanType(), new IdentifierData("test",
	     new StringType(0l)));
	   fail();
	  } catch (IntermediateCodeGeneratorException e) {
	  }
	 }

	@Test
	 public void testCastStringToStructFails(){
	  try {
	   when(converter.icg.generateTempIdentifier(any(StructType.class)))
	     .thenReturn(
	       new IdentifierData("tmp", new StructType(null,
	         new Member[] { new Member("",
	           new StringType(0l)) })));
	   converter.cast(new StructType(null, new Member[] { new Member("",
	     new LongType()) }), new IdentifierData("test",
	     new StringType(0l)));
	   fail();
	  } catch (IntermediateCodeGeneratorException e) {
	  }
	 }

	@Test
	 public void testCastStringToArrayFails(){
	  try {
	   when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
	     .thenReturn(
	       new IdentifierData("tmp", new ArrayType(
	         new LongType(), 0)));
	   converter.cast(new ArrayType(new LongType(), 0),
	     new IdentifierData("test", new StringType(0l)));
	   fail();
	  } catch (IntermediateCodeGeneratorException e) {
	  }
	 }

	@Test
	public void testCastStructToLongFails(){
		try{
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
					.thenReturn(new IdentifierData("tmp", new LongType()));
			converter.cast(new LongType(), new IdentifierData("test", 
					new StructType(null, new Member[] { new Member("",
					new LongType()) })));
		}catch (IntermediateCodeGeneratorException e){
			
		}
	}
	
	@Test
	public void testCastStructToDoubleFails(){
		try{
			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
					.thenReturn(new IdentifierData("tmp", new DoubleType()));
			converter.cast(new LongType(), new IdentifierData("test", 
					new StructType(null, new Member[] { new Member("",
					new LongType()) })));
		}catch (IntermediateCodeGeneratorException e){
			
		}
	}
	
	@Test
	public void testCastStructToStringFails(){
		try {
			when(converter.icg.generateTempIdentifier(any(StringType.class)))
					.thenReturn(new IdentifierData("tmp", new StringType(0l)));
			converter.cast(new StringType(0l), new IdentifierData("test",
					new StructType(null, new Member[] { new Member("",
							new LongType()) })));
			fail();
		} catch (IntermediateCodeGeneratorException e) {
		}
	}
	
	@Test
	public void testCastCastStructToBooleanFails(){
		try {
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
					.thenReturn(new IdentifierData("tmp", new BooleanType()));
			converter.cast(new StringType(0l), new IdentifierData("test",
					new StructType(null, new Member[] { new Member("",
							new LongType()) })));
			fail();
		} catch (IntermediateCodeGeneratorException e) {
		}
	}
	
	@Test
	public void testCastCastStructToArrayFails(){
		try {
			when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
					.thenReturn(new IdentifierData("tmp",
							new ArrayType(new LongType(), 0)));
			converter.cast(new StringType(0l), new IdentifierData("test",
					new StructType(null, new Member[] { new Member("",
							new LongType()) })));
			fail();
		} catch (IntermediateCodeGeneratorException e) {
		}
	}
	
	@Test
	 public void testCastArrayToLongFails(){
	  try{
		   when(converter.icg.generateTempIdentifier(any(LongType.class)))
		     .thenReturn(new IdentifierData("tmp", new LongType()));
		   converter.cast(new LongType(), new IdentifierData("test", 
				   new ArrayType(new LongType(), 0)));
		   fail();
	  }catch (IntermediateCodeGeneratorException e){
	   
	  }
	 }
	 
	 
	 @Test
	 public void testCastArrayToDoubleFails(){
	 try {
		   when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
		     .thenReturn(
		       new IdentifierData("tmp", new DoubleType()));
		   converter.cast(new DoubleType(), new IdentifierData("test",
		     new ArrayType(new LongType(), 0)));
		   fail();
	  } catch (IntermediateCodeGeneratorException e) {
	   
	  }
	 }
	 
	 @Test
	public void testCastArrayToBoolean(){
	try {
	   when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
	     .thenReturn(new IdentifierData("tmp", new BooleanType()));
	   converter.cast(new BooleanType(), new IdentifierData("test",
	     new ArrayType(new LongType(), 0)));
	   fail();
	  } catch (IntermediateCodeGeneratorException e) {
	  }
	 }
	 
	 
	 @Test
	 public void testCastArrayToString(){
	   try {
	   when(converter.icg.generateTempIdentifier(any(StringType.class)))
	     .thenReturn(new IdentifierData("tmp", new StringType(0l)));
	   converter.cast(new StringType(0l), new IdentifierData("test",
	     new ArrayType(new LongType(), 0)));
	   fail();
	  } catch (IntermediateCodeGeneratorException e) {
	  }
	 }
	 
	 @Test
	 public void testCastArrayToStruct(){
	   try {
		   when(converter.icg.generateTempIdentifier(any(StructType.class)))
		     .thenReturn(
		       new IdentifierData("tmp", new StructType(null,
		         new Member[] { new Member("",
		           new BooleanType()) })));
		   converter.cast(new StructType(null, new Member[] { new Member("",
		     new LongType()) }), new IdentifierData("test",
		     new ArrayType(new LongType(), 0)));
		   fail();
	  } catch (IntermediateCodeGeneratorException e) {
	  }
	}
}
