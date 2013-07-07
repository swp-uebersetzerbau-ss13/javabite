package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode.BinaryOperator;
import swp_compiler_ss13.common.ast.nodes.binary.RelationExpressionNode;
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
import swp_compiler_ss13.javabite.codegen.converters.RelationExpressionNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public class RelationExpressionNodeConverterTest {

	RelationExpressionNodeConverter converter;

	@Before
	public void setUp() throws Exception {
		converter = new RelationExpressionNodeConverter();

		converter.icg = Mockito.mock(IntermediateCodeGeneratorJb.class);
	}

	
	
	/**
	 * Test for two RelationExpressions, which have both the type Long and the Operator EQUAL in between.
	 */
	@Test
    public void testRelationLong_EQUAL() throws IntermediateCodeGeneratorException{
            RelationExpressionNode node =  mock(RelationExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new LongType()) ,
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
               
            when(node.getOperator()).thenReturn(BinaryOperator.EQUAL);
           
            converter.convert(node);
           
            verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_LONG_E, "test1",
                    "test2", "tmp"));
            verify(converter.icg).pushIdentifierData(tmp);
    }
	
	/**
	 * Test for two RelationExpressions, which have both the type Long and the Operator GREATERTHAN in between.
	 */
	@Test
	 public void testRelationLong_GREATERTHAN() throws IntermediateCodeGeneratorException{
        RelationExpressionNode node =  mock(RelationExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new LongType()) ,
                new IdentifierData("test2", new LongType()));
        IdentifierData tmp = new IdentifierData("tmp", new LongType());
        when(converter.icg.generateTempIdentifier(any(LongType.class)))
        .thenReturn(tmp);
           
        when(node.getOperator()).thenReturn(BinaryOperator.GREATERTHAN);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_LONG_G, "test1",
                "test2", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
}

	
	/**
	 * Test for two RelationExpressions, which have both the type Long and the Operator LESSTHAN in between.
	 */
	@Test
	 public void testRelationLong_LESSTHAN() throws IntermediateCodeGeneratorException{
        RelationExpressionNode node =  mock(RelationExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new LongType()) ,
                new IdentifierData("test2", new LongType()));
        IdentifierData tmp = new IdentifierData("tmp", new LongType());
        when(converter.icg.generateTempIdentifier(any(LongType.class)))
        .thenReturn(tmp);
           
        when(node.getOperator()).thenReturn(BinaryOperator.LESSTHAN);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_LONG_L, "test1",
                "test2", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
}

	
	/**
	 * Test for two RelationExpressions, which have both the type Long and the Operator GREATERTHANEQUAL in between.
	 */
	@Test
	 public void testRelationLong_GREATERTHANEQUAL() throws IntermediateCodeGeneratorException{
        RelationExpressionNode node =  mock(RelationExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new LongType()) ,
                new IdentifierData("test2", new LongType()));
        IdentifierData tmp = new IdentifierData("tmp", new LongType());
        when(converter.icg.generateTempIdentifier(any(LongType.class)))
        .thenReturn(tmp);
           
        when(node.getOperator()).thenReturn(BinaryOperator.GREATERTHANEQUAL);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_LONG_GE, "test1",
                "test2", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
}

	
	/**
	 * Test for two RelationExpressions, which have both the type Long and the Operator LESSTHANEQUAL in between.
	 */
	@Test
	 public void testRelationLong_LESSTHANEQUAL() throws IntermediateCodeGeneratorException{
        RelationExpressionNode node =  mock(RelationExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new LongType()) ,
                new IdentifierData("test2", new LongType()));
        IdentifierData tmp = new IdentifierData("tmp", new LongType());
        when(converter.icg.generateTempIdentifier(any(LongType.class)))
        .thenReturn(tmp);
           
        when(node.getOperator()).thenReturn(BinaryOperator.LESSTHANEQUAL);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_LONG_LE, "test1",
                "test2", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
}

	
	
	/**
	 * Test for two RelationExpressions, which have both the type Double and the Operator EQUAL in between.
	 */
	@Test
	 public void testRelationDouble_EQUAL() throws IntermediateCodeGeneratorException{
        RelationExpressionNode node =  mock(RelationExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new DoubleType()) ,
                new IdentifierData("test2", new DoubleType()));
        IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
        when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
        .thenReturn(tmp);
           
        when(node.getOperator()).thenReturn(BinaryOperator.EQUAL);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_DOUBLE_E, "test1",
                "test2", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
}

	
	/**
	 * Test for two RelationExpressions, which have both the type Double and the Operator GREATERTHAN in between.
	 */
	@Test
	 public void testRelationDouble_GREATERTHAN() throws IntermediateCodeGeneratorException{
        RelationExpressionNode node =  mock(RelationExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new DoubleType()) ,
                new IdentifierData("test2", new DoubleType()));
        IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
        when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
        .thenReturn(tmp);
           
        when(node.getOperator()).thenReturn(BinaryOperator.GREATERTHAN);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_DOUBLE_G, "test1",
                "test2", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
}

	
	/**
	 * Test for two RelationExpressions, which have both the type Double and the Operator LESSTHAN in between.
	 */
	@Test
	 public void testRelationDouble_LESSTHAN() throws IntermediateCodeGeneratorException{
        RelationExpressionNode node =  mock(RelationExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new DoubleType()) ,
                new IdentifierData("test2", new DoubleType()));
        IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
        when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
        .thenReturn(tmp);
           
        when(node.getOperator()).thenReturn(BinaryOperator.LESSTHAN);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_DOUBLE_L, "test1",
                "test2", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
}

	
	/**
	 * Test for two RelationExpressions, which have both the type Double and the Operator GREATERTHANEQUAL in between.
	 */
	@Test
	 public void testRelationDouble_GREATERTHANEQUAL() throws IntermediateCodeGeneratorException{
        RelationExpressionNode node =  mock(RelationExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new DoubleType()) ,
                new IdentifierData("test2", new DoubleType()));
        IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
        when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
        .thenReturn(tmp);
           
        when(node.getOperator()).thenReturn(BinaryOperator.GREATERTHANEQUAL);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_DOUBLE_GE, "test1",
                "test2", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
}

	
	/**
	 * Test for two RelationExpressions, which have both the type Double and the Operator LESSTHANEQUAL in between.
	 */
	@Test
	 public void testRelationDouble_LESSTHANEQUAL() throws IntermediateCodeGeneratorException{
        RelationExpressionNode node =  mock(RelationExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new DoubleType()) ,
                new IdentifierData("test2", new DoubleType()));
        IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
        when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
        .thenReturn(tmp);
           
        when(node.getOperator()).thenReturn(BinaryOperator.LESSTHANEQUAL);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.COMPARE_DOUBLE_LE, "test1",
                "test2", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
}
	
	
	/**
	 * Test for two RelationExpressions, which have both the type Boolean and the Operator EQUAL in between.
	 */
	@Test
	public void testRelationBoolean_EQUAL() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new BooleanType()) ,
	                new IdentifierData("test2", new BooleanType()));
	        IdentifierData tmp1 = new IdentifierData("tmp1", new BooleanType());
	        IdentifierData tmp2 = new IdentifierData("tmp2", new BooleanType());
	        IdentifierData tmp3 = new IdentifierData("tmp3", new BooleanType());
	        when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
	        .thenReturn(tmp1,tmp2,tmp3);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.EQUAL);
	       
	        converter.convert(node);
	        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.AND_BOOLEAN, "test1", "test2", "tmp1"));
	        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.NOT_BOOLEAN, "test1", Quadruple.EmptyArgument, "tmp2"));
	        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.NOT_BOOLEAN, "test2", Quadruple.EmptyArgument, "tmp3"));
	        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.AND_BOOLEAN, "tmp2", "tmp3", "tmp3"));
	        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.OR_BOOLEAN, "tmp1", "tmp3", "tmp1"));
	        verify(converter.icg).pushIdentifierData(tmp1);
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	
	/**
	 * Test for two RelationExpressions, which have both the type Boolean and the Operator GREATERTHAN in between.
	 */
	@Test
	public void testRelationBoolean_GREATERTHAN() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new BooleanType()) ,
	                new IdentifierData("test2", new BooleanType()));
	        IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
	        when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.GREATERTHAN);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	
	/**
	 * Test for two RelationExpressions, which have both the type Boolean and the Operator LESSTHAN in between.
	 */
	@Test
	public void testRelationBoolean_LESSTHAN() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new BooleanType()) ,
	                new IdentifierData("test2", new BooleanType()));
	        IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
	        when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.LESSTHAN);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	
	/**
	 * Test for two RelationExpressions, which have both the type Boolean and the Operator GREATERTHANEQUAL in between.
	 */
	@Test
	public void testRelationBoolean_GREATERTHANEQUAL() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new BooleanType()) ,
	                new IdentifierData("test2", new BooleanType()));
	        IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
	        when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.GREATERTHANEQUAL);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	
	/**
	 * Test for two RelationExpressions, which have both the type Boolean and the Operator LESSTHANEQUAL in between.
	 */
	@Test
	public void testRelationBoolean_LESSTHANEQUAL() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new BooleanType()) ,
	                new IdentifierData("test2", new BooleanType()));
	        IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
	        when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.LESSTHANEQUAL);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	
	
	/**
	 * Test for two RelationExpressions, which have both the type String and the Operator EQUAL in between.
	 */
	@Test
	public void testRelationString_EQUAL() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new StringType(0L)) ,
	                new IdentifierData("test2", new StringType(0L)));
	        IdentifierData tmp = new IdentifierData("tmp", new StringType(0L));
	        when(converter.icg.generateTempIdentifier(any(StringType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.EQUAL);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	
	/**
	 * Test for two RelationExpressions, which have both the type String and the Operator GREATERTHAN in between.
	 */
	@Test
	public void testRelationString_GREATERTHAN() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new StringType(0L)) ,
	                new IdentifierData("test2", new StringType(0L)));
	        IdentifierData tmp = new IdentifierData("tmp", new StringType(0L));
	        when(converter.icg.generateTempIdentifier(any(StringType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.GREATERTHAN);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	
	/**
	 * Test for two RelationExpressions, which have both the type String and the Operator LESSTHAN in between.
	 */
	@Test
	public void testRelationString_LESSTHAN() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new StringType(0L)) ,
	                new IdentifierData("test2", new StringType(0L)));
	        IdentifierData tmp = new IdentifierData("tmp", new StringType(0L));
	        when(converter.icg.generateTempIdentifier(any(StringType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.LESSTHAN);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	
	/**
	 * Test for two RelationExpressions, which have both the type String and the Operator GREATERTHANEQUAL in between.
	 */
	@Test
	public void testRelationString_GREATERTHANEQUAL() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new StringType(0L)) ,
	                new IdentifierData("test2", new StringType(0L)));
	        IdentifierData tmp = new IdentifierData("tmp", new StringType(0L));
	        when(converter.icg.generateTempIdentifier(any(StringType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.GREATERTHANEQUAL);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	
	/**
	 * Test for two RelationExpressions, which have both the type String and the Operator LESSTHANEQUAL in between.
	 */
	@Test
	public void testRelationString_LESSTHANEQUAL() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new StringType(0L)) ,
	                new IdentifierData("test2", new StringType(0L)));
	        IdentifierData tmp = new IdentifierData("tmp", new StringType(0L));
	        when(converter.icg.generateTempIdentifier(any(StringType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.LESSTHANEQUAL);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	
	
	/**
	 * Test for two RelationExpressions, which have both the type Array and the Operator EQUAL in between.
	 */
	@Test
	public void testRelationArray_EQUAL() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new ArrayType(new LongType(),0)) ,
	                new IdentifierData("test2", new ArrayType(new LongType(),0)));
	        IdentifierData tmp = new IdentifierData("tmp", new ArrayType(new LongType(),0));
	        when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.EQUAL);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	
	/**
	 * Test for two RelationExpressions, which have both the type Array and the Operator GREATERTHAN in between.
	 */
	@Test
	public void testRelationArray_GREATERTHAN() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new ArrayType(new LongType(),0)) ,
	                new IdentifierData("test2", new ArrayType(new LongType(),0)));
	        IdentifierData tmp = new IdentifierData("tmp", new ArrayType(new LongType(),0));
	        when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.GREATERTHAN);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	
	/**
	 * Test for two RelationExpressions, which have both the type Array and the Operator LESSTHAN in between.
	 */
	@Test
	public void testRelationArray_LESSTHAN() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new ArrayType(new LongType(),0)) ,
	                new IdentifierData("test2", new ArrayType(new LongType(),0)));
	        IdentifierData tmp = new IdentifierData("tmp", new ArrayType(new LongType(),0));
	        when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.LESSTHAN);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	
	/**
	 * Test for two RelationExpressions, which have both the type Array and the Operator GREATERTHANEQUAL in between.
	 */
	@Test
	public void testRelationArray_GREATERTHANEQUAL() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new ArrayType(new LongType(),0)) ,
	                new IdentifierData("test2", new ArrayType(new LongType(),0)));
	        IdentifierData tmp = new IdentifierData("tmp", new ArrayType(new LongType(),0));
	        when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.GREATERTHANEQUAL);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	
	
	/**
	 * Test for two RelationExpressions, which have both the type Array and the Operator LESSTHANEQUAL in between.
	 */
	@Test
	public void testRelationArray_LESSTHANEQUAL() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new ArrayType(new LongType(),0)) ,
	                new IdentifierData("test2", new ArrayType(new LongType(),0)));
	        IdentifierData tmp = new IdentifierData("tmp", new ArrayType(new LongType(),0));
	        when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.LESSTHANEQUAL);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	
	
	/**
	 * Test for two RelationExpressions, which have both the type Struct and the Operator EQUAL in between.
	 */
	@Test
	public void testRelationStruct_EQUAL() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new StructType(
                            new Member[] { new Member("",
                                    new DoubleType()) })) ,
	                new IdentifierData("test2", new StructType(
                            new Member[] { new Member("",
                                    new DoubleType()) })));
	        IdentifierData tmp = new IdentifierData("tmp", new StructType(
                    new Member[] { new Member("",
                            new DoubleType()) }));
	        when(converter.icg.generateTempIdentifier(any(StructType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.EQUAL);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}
	
	
	/**
	 * Test for two RelationExpressions, which have both the type Struct and the Operator EQUAL in between.
	 */
	@Test
	public void testRelationStruct_GREATERTHAN() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new StructType(
                            new Member[] { new Member("",
                                    new DoubleType()) })) ,
	                new IdentifierData("test2", new StructType(
                            new Member[] { new Member("",
                                    new DoubleType()) })));
	        IdentifierData tmp = new IdentifierData("tmp", new StructType(
                    new Member[] { new Member("",
                            new DoubleType()) }));
	        when(converter.icg.generateTempIdentifier(any(StructType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.GREATERTHAN);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	
	/**
	 * Test for two RelationExpressions, which have both the type Struct and the Operator EQUAL in between.
	 */
	@Test
	public void testRelationStruct_LESSTHAN() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new StructType(
                            new Member[] { new Member("",
                                    new DoubleType()) })) ,
	                new IdentifierData("test2", new StructType(
                            new Member[] { new Member("",
                                    new DoubleType()) })));
	        IdentifierData tmp = new IdentifierData("tmp", new StructType(
                    new Member[] { new Member("",
                            new DoubleType()) }));
	        when(converter.icg.generateTempIdentifier(any(StructType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.LESSTHAN);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}
	
	/**
	 * Test for two RelationExpressions, which have both the type Struct and the Operator EQUAL in between.
	 */
	@Test
	public void testRelationStruct_GREATERTHANEQUAL() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new StructType(
                            new Member[] { new Member("",
                                    new DoubleType()) })) ,
	                new IdentifierData("test2", new StructType(
                            new Member[] { new Member("",
                                    new DoubleType()) })));
	        IdentifierData tmp = new IdentifierData("tmp", new StructType(
                    new Member[] { new Member("",
                            new DoubleType()) }));
	        when(converter.icg.generateTempIdentifier(any(StructType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.GREATERTHANEQUAL);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}
	
	
	/**
	 * Test for two RelationExpressions, which have both the type Struct and the Operator EQUAL in between.
	 */
	@Test
	public void testRelationStruct_LESSTHANEQUAL() {
		try {
			RelationExpressionNode node =  mock(RelationExpressionNode.class);
			   
	        when(converter.icg.popIdentifierData()).thenReturn(
	                new IdentifierData("test1", new StructType(
                            new Member[] { new Member("",
                                    new DoubleType()) })) ,
	                new IdentifierData("test2", new StructType(
                            new Member[] { new Member("",
                                    new DoubleType()) })));
	        IdentifierData tmp = new IdentifierData("tmp", new StructType(
                    new Member[] { new Member("",
                            new DoubleType()) }));
	        when(converter.icg.generateTempIdentifier(any(StructType.class)))
	        .thenReturn(tmp);
	           
	        when(node.getOperator()).thenReturn(BinaryOperator.LESSTHANEQUAL);
	       
	        converter.convert(node);
	       
	        verify(converter.icg).pushIdentifierData(tmp);
	        fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}
	
}
