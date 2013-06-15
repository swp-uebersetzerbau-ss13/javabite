
package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode.BinaryOperator;
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

   
   
   
    //Long_SUB
    @Test
    public void testArithmeticBinaryExpressionLongLong_SUB() throws IntermediateCodeGeneratorException{
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
   
   
   
    //Double SUB
    @Test
    public void testArithmeticBinaryExpressionDoubleDouble_SUB() throws IntermediateCodeGeneratorException{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new DoubleType()) ,
                    new IdentifierData("test2", new DoubleType()));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.SUB_DOUBLE, "test1",
                    "test2", "tmp"));
            verify(converter.icg).pushIdentifierData(tmp);
    }
   
   
    //DoubleLong SUB
    @Test
    public void testArithmeticBinaryExpressionDoubleLong_SUB() throws IntermediateCodeGeneratorException{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new DoubleType()) ,
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
               
            when(converter.cast(new DoubleType(), new IdentifierData("test2", new LongType())))
            .thenReturn(new IdentifierData("castTemp", new DoubleType()));
           
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.SUB_DOUBLE, "test1",
                    "castTemp", "tmp"));
            verify(converter.icg).pushIdentifierData(tmp);
    }
   
   
    //LongDouble SUB
    @Test
    public void testArithmeticBinaryExpressionLongDouble_SUB() throws IntermediateCodeGeneratorException{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new LongType()) ,
                    new IdentifierData("test2", new DoubleType()));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
               
            when(converter.cast(new DoubleType(), new IdentifierData("test1", new LongType())))
            .thenReturn(new IdentifierData("castTemp", new DoubleType()));
           
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.SUB_DOUBLE, "castTemp",
                    "castTemp", "tmp"));
            verify(converter.icg).pushIdentifierData(tmp);
    }
   
   
   
   
   
    //LongLong ADD
    @Test
    public void testArithmeticBinaryExpressionLongLong_ADD() throws IntermediateCodeGeneratorException{
        ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new LongType()) ,
                new IdentifierData("test2", new LongType()));
        IdentifierData tmp = new IdentifierData("tmp", new LongType());
        when(converter.icg.generateTempIdentifier(any(LongType.class)))
        .thenReturn(tmp);
           
        when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.ADD_LONG, "test1",
                "test2", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
    }
   
    //DoubleDouble ADD
    @Test
    public void testArithmeticBinaryExpressionDoubleDouble_ADD() throws IntermediateCodeGeneratorException{
        ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new DoubleType()) ,
                new IdentifierData("test2", new DoubleType()));
        IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
        when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
        .thenReturn(tmp);
           
        when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.ADD_DOUBLE, "test1",
                "test2", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
    }
   
    //DoubleLong ADD
    @Test
    public void testArithmeticBinaryExpressionDoubleLong_ADD() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new DoubleType()) ,
                    new IdentifierData("test2", new LongType()));
           
            //((CastingAst2CodeConverter) converter.icg).cast(new DoubleType(), new IdentifierData("test2", new LongType()));
            when(converter.cast(new DoubleType(), new IdentifierData("test2", new LongType())))
            .thenReturn(new IdentifierData("castTemp", new DoubleType()));
           
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
           
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
           
            converter.convert(node);
           
            verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.ADD_DOUBLE, "test1",
                    "castTemp", "tmp"));
            verify(converter.icg).pushIdentifierData(tmp);
        }
        catch(IntermediateCodeGeneratorException e){
            fail();
        }
    }
   
    //LongDouble ADD
    @Test
    public void testArithmeticBinaryExpressionLongDouble_ADD() throws IntermediateCodeGeneratorException{
        ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new LongType()) ,
                new IdentifierData("test2", new DoubleType()));
        IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
        when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
        .thenReturn(tmp);
           
        when(converter.cast(new DoubleType(), new IdentifierData("test1", new LongType())))
        .thenReturn(new IdentifierData("castTemp", new DoubleType()));
       
        when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.ADD_DOUBLE, "castTemp",
                "castTemp", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
    }
       
   
   
   

    //LongLong MUL
    @Test
    public void testArithmeticBinaryExpressionLongLong_MUL() throws IntermediateCodeGeneratorException{
        ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new LongType()) ,
                new IdentifierData("test2", new LongType()));
        IdentifierData tmp = new IdentifierData("tmp", new LongType());
        when(converter.icg.generateTempIdentifier(any(LongType.class)))
        .thenReturn(tmp);
           
        when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.MUL_LONG, "test1",
                "test2", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
    }
               
   
    //DoubleDouble MUL
    @Test
    public void testArithmeticBinaryExpressionDoubleDouble_MUL() throws IntermediateCodeGeneratorException{
        ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new DoubleType()) ,
                new IdentifierData("test2", new DoubleType()));
        IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
        when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
        .thenReturn(tmp);
           
        when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.MUL_DOUBLE, "test1",
                "test2", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
    }
               
   
    //DoubleLong MUL
    @Test
    public void testArithmeticBinaryExpressionDoubleLong_MUL() throws IntermediateCodeGeneratorException{
        ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new DoubleType()) ,
                new IdentifierData("test2", new LongType()));
        IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
        when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
        .thenReturn(tmp);
           
        when(converter.cast(new DoubleType(), new IdentifierData("test2", new LongType())))
        .thenReturn(new IdentifierData("castTemp", new DoubleType()));
       
        when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.MUL_DOUBLE, "test1",
                "castTemp", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
    }
       
   
    //LongDouble MUL
    @Test
    public void testArithmeticBinaryExpressionLongDouble_MUL() throws IntermediateCodeGeneratorException{
        ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new LongType()) ,
                new IdentifierData("test2", new DoubleType()));
        IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
        when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
        .thenReturn(tmp);
           
        when(converter.cast(new DoubleType(), new IdentifierData("test1", new LongType())))
        .thenReturn(new IdentifierData("castTemp", new DoubleType()));
       
        when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.MUL_DOUBLE, "castTemp",
                "castTemp", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
    }
       
       
   
       
    //LongLong DIV
    @Test
    public void testArithmeticBinaryExpressionLongLong_DIV() throws IntermediateCodeGeneratorException{
        ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new LongType()) ,
                new IdentifierData("test2", new LongType()));
        IdentifierData tmp = new IdentifierData("tmp", new LongType());
        when(converter.icg.generateTempIdentifier(any(LongType.class)))
        .thenReturn(tmp);
           
        when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.DIV_LONG, "test1",
                "test2", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
    }
               
   
    //DoubleDouble DIV
    @Test
    public void testArithmeticBinaryExpressionDoubleDouble_DIV() throws IntermediateCodeGeneratorException{
        ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new DoubleType()) ,
                new IdentifierData("test2", new DoubleType()));
        IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
        when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
        .thenReturn(tmp);
           
        when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.DIV_DOUBLE, "test1",
                "test2", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
    }
   
   
    //DoubleLong DIV
    @Test
    public void testArithmeticBinaryExpressionDoubleLong_DIV() throws IntermediateCodeGeneratorException{
        ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new DoubleType()) ,
                new IdentifierData("test2", new LongType()));
        IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
        when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
        .thenReturn(tmp);
           
        when(((CastingAst2CodeConverter) converter.icg).cast(new DoubleType(), new IdentifierData("test2", new LongType())))
        .thenReturn(new IdentifierData("castTemp", new DoubleType()));
       
        when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.DIV_DOUBLE, "test1",
                "castTemp", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
    }
   
   
    //LongDouble DIV
    @Test
    public void testArithmeticBinaryExpressionLongDouble_DIV() throws IntermediateCodeGeneratorException{
        ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
   
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new LongType()) ,
                new IdentifierData("test2", new DoubleType()));
        IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
        when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
        .thenReturn(tmp);
           
        when(converter.cast(new DoubleType(), new IdentifierData("test1", new LongType())))
        .thenReturn(new IdentifierData("castTemp", new DoubleType()));
       
        when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
       
        converter.convert(node);
       
        verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.DIV_DOUBLE, "castTemp",
                "castTemp", "tmp"));
        verify(converter.icg).pushIdentifierData(tmp);
    }







   
   
   
   
   
    //LongString SUB
    @Test
    public void testArithmeticBinaryExpressionLongString_SUB() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new LongType()) ,
                    new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new LongType(), new IdentifierData("test2", new StringType(0l))))
//            .thenReturn(new IdentifierData("castTemp", new LongType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //StringLong SUB
    @Test
    public void testArithmeticBinaryExpressionStringLong_SUB() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
               
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new LongType(), new IdentifierData("test1", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new LongType()));
           
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //DoubleString SUB
    @Test
    public void testArithmeticBinaryExpressionDoubleString_SUB() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new DoubleType()) ,
                    new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new DoubleType(), new IdentifierData("test2", new StringType(0l))))
//            .thenReturn(new IdentifierData("castTemp", new DoubleType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
   
    //StringDouble SUB
    @Test
    public void testArithmeticBinaryExpressionStringDouble_SUB() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new DoubleType()));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new DoubleType(), new IdentifierData("test1", new StringType(0l))))
//            .thenReturn(new IdentifierData("castTemp", new DoubleType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //StringString SUB
    @Test
    public void testArithmeticBinaryExpressionStringString_SUB() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
            when(converter.icg.generateTempIdentifier(any(StringType.class)))
            .thenReturn(tmp);
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //StringBoolean SUB
    @Test
    public void testArithmeticBinaryExpressionStringBoolean_SUB() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
            when(converter.icg.generateTempIdentifier(any(StringType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new StringType(0l), new IdentifierData("test2", new BooleanType())))
//            .thenReturn(new IdentifierData("castTemp", new StringType(0l)));
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e){
       
        }
    }
   
   
    //BooleanString SUB
    @Test
    public void testArithmeticBinaryExpressionBooleanString_SUB() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new BooleanType(), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new BooleanType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
       
   
    //StringStruct SUB
    @Test
    public void testArithmeticBinaryExpressionStringStruct_SUB() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new StructType(null,
                            new Member[] { new Member("",
                                    new DoubleType()) })));
            IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
            when(converter.icg.generateTempIdentifier(any(StringType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new StringType(0l), new IdentifierData("test2", new StructType(null,
    //                new Member[] { new Member("",
    //                        new DoubleType()) }))))
    //        .thenReturn(new IdentifierData("castTemp", new StringType(0l)));
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
    //StructString SUB
    @Test
    public void testArithmeticBinaryExpressionStructString_SUB() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new StructType(null,
                            new Member[] { new Member("",
                                    new DoubleType()) })),
                            new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new StructType(null,
                    new Member[] { new Member("",
                            new DoubleType()) }));
            when(converter.icg.generateTempIdentifier(any(StructType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                    new Member[] { new Member("",
//                            new DoubleType()) }), new IdentifierData("test2", new StringType(0l))))
//            .thenReturn(new IdentifierData("castTemp", new StructType(null,
//                    new Member[] { new Member("",
//                            new DoubleType()) })));
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
    //StringArray SUB
    @Test
    public void testArithmeticBinaryExpressionStringArray_SUB() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new StringType(0l)),
                            new IdentifierData("test2", new ArrayType(
                                    new LongType(), 0)));
            IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
            when(converter.icg.generateTempIdentifier(any(StringType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new StringType(0l), new IdentifierData("test2", new ArrayType(
//                    new LongType(), 0))))
//            .thenReturn(new IdentifierData("castTemp", new ArrayType(
//                    new LongType(), 0)));
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
    //ArrayString SUB
    @Test
    public void testArithmeticBinaryExpressionArrayString_SUB() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new ArrayType(
                                    new LongType(), 0)),
                            new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new ArrayType(
                    new LongType(), 0));
            when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new ArrayType(
    //                new LongType(), 0), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new ArrayType(
    //                new LongType(), 0)));
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
    //LongString ADD
        @Test
    public void testArithmeticBinaryExpressionLongString_ADD() throws IntermediateCodeGeneratorException{
        try{       
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new LongType()) ,
                    new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new LongType(), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new LongType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //StringLong ADD
    @Test
    public void testArithmeticBinaryExpressionStringLong_ADD() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
               
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new LongType(), new IdentifierData("test1", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new LongType()));
           
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //DoubleString ADD
    @Test
    public void testArithmeticBinaryExpressionDoubleString_ADD() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new DoubleType()) ,
                    new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new DoubleType(), new IdentifierData("test2", new StringType(0l))))
//            .thenReturn(new IdentifierData("castTemp", new DoubleType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
   
    //StringDouble ADD
    @Test
    public void testArithmeticBinaryExpressionStringDouble_ADD() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new DoubleType()));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new DoubleType(), new IdentifierData("test1", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new DoubleType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //StringString ADD
    @Test
    public void testArithmeticBinaryExpressionStringString_ADD() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
            when(converter.icg.generateTempIdentifier(any(StringType.class)))
            .thenReturn(tmp);
               
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
           
            converter.convert(node);
            fail();
           
        }
        catch (IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //StringBoolean ADD
    @Test
    public void testArithmeticBinaryExpressionStringBoolean_ADD() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
            when(converter.icg.generateTempIdentifier(any(StringType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new StringType(0l), new IdentifierData("test2", new BooleanType())))
//            .thenReturn(new IdentifierData("castTemp", new StringType(0l)));
               
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //BooleanString ADD
    @Test
    public void testArithmeticBinaryExpressionBooleanString_ADD() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new BooleanType(), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new BooleanType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
       
   
    //StringStruct ADD
    @Test
    public void testArithmeticBinaryExpressionStringStruct_ADD() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new StructType(null,
                            new Member[] { new Member("",
                                    new DoubleType()) })));
            IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
            when(converter.icg.generateTempIdentifier(any(StringType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new StringType(0l), new IdentifierData("test2", new StructType(null,
    //                new Member[] { new Member("",
    //                        new DoubleType()) }))))
    //        .thenReturn(new IdentifierData("castTemp", new StringType(0l)));
               
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
    //StructString ADD
    @Test
    public void testArithmeticBinaryExpressionStructString_ADD() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new StructType(null,
                            new Member[] { new Member("",
                                    new DoubleType()) })),
                            new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new StructType(null,
                    new Member[] { new Member("",
                            new DoubleType()) }));
            when(converter.icg.generateTempIdentifier(any(StructType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
    //                new Member[] { new Member("",
    //                        new DoubleType()) }), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new StructType(null,
    //                new Member[] { new Member("",
    //                        new DoubleType()) })));
               
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
    //StringArray ADD
    @Test
    public void testArithmeticBinaryExpressionStringArray_ADD() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new StringType(0l)),
                            new IdentifierData("test2", new ArrayType(
                                    new LongType(), 0)));
            IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
            when(converter.icg.generateTempIdentifier(any(StringType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new StringType(0l), new IdentifierData("test2", new ArrayType(
    //                new LongType(), 0))))
    //        .thenReturn(new IdentifierData("castTemp", new ArrayType(
    //                new LongType(), 0)));
               
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
    //ArrayString ADD
    @Test
    public void testArithmeticBinaryExpressionArrayString_ADD() throws IntermediateCodeGeneratorException{
       
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new ArrayType(
                                    new LongType(), 0)),
                            new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new ArrayType(
                    new LongType(), 0));
            when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
            .thenReturn(tmp);
           
//        ((CastingAst2CodeConverter) converter.icg).cast(new ArrayType(
//                new LongType(), 0), new IdentifierData("test2", new StringType(0l)));
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new ArrayType(
    //                new LongType(), 0), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new ArrayType(
    //                new LongType(), 0)));
    //           
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
           
            converter.convert(node);
            fail();
           
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }

    //LongString MUL
        @Test
    public void testArithmeticBinaryExpressionLongString_MUL() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new LongType()) ,
                    new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new LongType(), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new LongType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //StringLong MUL
    @Test
    public void testArithmeticBinaryExpressionStringLong_MUL() throws IntermediateCodeGeneratorException{
       
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
               
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new LongType(), new IdentifierData("test1", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new LongType()));
           
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //DoubleString MUL
    @Test
    public void testArithmeticBinaryExpressionDoubleString_MUL() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new DoubleType()) ,
                    new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new DoubleType(), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new DoubleType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
        }
           
    }
   
   
   
    //StringDouble MUL
    @Test
    public void testArithmeticBinaryExpressionStringDouble_MUL() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new DoubleType()));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new DoubleType(), new IdentifierData("test1", new StringType(0l))))
//            .thenReturn(new IdentifierData("castTemp", new DoubleType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
       
        }
    }
   
   
    //StringString ADD
    @Test
    public void testArithmeticBinaryExpressionStringString_MUL() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
            when(converter.icg.generateTempIdentifier(any(StringType.class)))
            .thenReturn(tmp);
               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //StringBoolean MUL
    @Test
    public void testArithmeticBinaryExpressionStringBoolean_MUL() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
            when(converter.icg.generateTempIdentifier(any(StringType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new StringType(0l), new IdentifierData("test2", new BooleanType())))
    //        .thenReturn(new IdentifierData("castTemp", new StringType(0l)));
               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //BooleanString MUL
    @Test
    public void testArithmeticBinaryExpressionBooleanString_MUL() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new BooleanType(), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new BooleanType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
        }
    }
       
   
    //StringStruct MUL
    @Test
    public void testArithmeticBinaryExpressionStringStruct_MUL() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new StructType(null,
                            new Member[] { new Member("",
                                    new DoubleType()) })));
            IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
            when(converter.icg.generateTempIdentifier(any(StringType.class)))
            .thenReturn(tmp);
           
        //        when(((CastingAst2CodeConverter) converter.icg).cast(new StringType(0l), new IdentifierData("test2", new StructType(null,
        //                new Member[] { new Member("",
        //                        new DoubleType()) }))))
        //        .thenReturn(new IdentifierData("castTemp", new StringType(0l)));
               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
       
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
        }
    }
   
    //StructString MUL
    @Test
    public void testArithmeticBinaryExpressionStructString_MUL() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new StructType(null,
                            new Member[] { new Member("",
                                    new DoubleType()) })),
                            new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new StructType(null,
                    new Member[] { new Member("",
                            new DoubleType()) }));
            when(converter.icg.generateTempIdentifier(any(StructType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                    new Member[] { new Member("",
//                            new DoubleType()) }), new IdentifierData("test2", new StringType(0l))))
//            .thenReturn(new IdentifierData("castTemp", new StructType(null,
//                    new Member[] { new Member("",
//                            new DoubleType()) })));
               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
    //StringArray MUL
    @Test
    public void testArithmeticBinaryExpressionStringArray_MUL() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new StringType(0l)),
                            new IdentifierData("test2", new ArrayType(
                                    new LongType(), 0)));
            IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
            when(converter.icg.generateTempIdentifier(any(StringType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new StringType(0l), new IdentifierData("test2", new ArrayType(
    //                new LongType(), 0))))
    //        .thenReturn(new IdentifierData("castTemp", new ArrayType(
    //                new LongType(), 0)));
               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
    //ArrayString MUL
    @Test
    public void testArithmeticBinaryExpressionArrayString_MUL() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new ArrayType(
                                    new LongType(), 0)),
                            new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new ArrayType(
                    new LongType(), 0));
            when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new ArrayType(
    //                new LongType(), 0), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new ArrayType(
    //                new LongType(), 0)));
               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }

   
    //LongString DIV
        @Test
    public void testArithmeticBinaryExpressionLongString_DIV() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new LongType()) ,
                    new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new LongType(), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new LongType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //StringLong DIV
    @Test
    public void testArithmeticBinaryExpressionStringLong_DIV() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
               
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new LongType(), new IdentifierData("test1", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new LongType()));
           
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //DoubleString DIV
    @Test
    public void testArithmeticBinaryExpressionDoubleString_DIV() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new DoubleType()) ,
                    new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new DoubleType(), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new DoubleType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
   
    //StringDouble DIV
    @Test
    public void testArithmeticBinaryExpressionStringDouble_DIV() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new DoubleType()));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new DoubleType(), new IdentifierData("test1", new StringType(0l))))
//            .thenReturn(new IdentifierData("castTemp", new DoubleType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //StringString DIV
    @Test
    public void testArithmeticBinaryExpressionStringString_DIV() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
            when(converter.icg.generateTempIdentifier(any(StringType.class)))
            .thenReturn(tmp);
               
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //StringBoolean DIV
    @Test
    public void testArithmeticBinaryExpressionStringBoolean_DIV() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
            when(converter.icg.generateTempIdentifier(any(StringType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new StringType(0l), new IdentifierData("test2", new BooleanType())))
    //        .thenReturn(new IdentifierData("castTemp", new StringType(0l)));
               
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //BooleanString DIV
    @Test
    public void testArithmeticBinaryExpressionBooleanString_DIV() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
           
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new BooleanType(), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new BooleanType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
       
   
    //StringStruct DIV
    @Test
    public void testArithmeticBinaryExpressionStringStruct_DIV() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StringType(0l)) ,
                    new IdentifierData("test2", new StructType(null,
                            new Member[] { new Member("",
                                    new DoubleType()) })));
            IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
            when(converter.icg.generateTempIdentifier(any(StringType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new StringType(0l), new IdentifierData("test2", new StructType(null,
//                    new Member[] { new Member("",
//                            new DoubleType()) }))))
//            .thenReturn(new IdentifierData("castTemp", new StringType(0l)));
               
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
    //StructString DIV
    @Test
    public void testArithmeticBinaryExpressionStructString_DIV() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new StructType(null,
                            new Member[] { new Member("",
                                    new DoubleType()) })),
                            new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new StructType(null,
                    new Member[] { new Member("",
                            new DoubleType()) }));
            when(converter.icg.generateTempIdentifier(any(StructType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                    new Member[] { new Member("",
//                            new DoubleType()) }), new IdentifierData("test2", new StringType(0l))))
//            .thenReturn(new IdentifierData("castTemp", new StructType(null,
//                    new Member[] { new Member("",
//                            new DoubleType()) })));
               
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
    //StringArray DIV
    @Test
    public void testArithmeticBinaryExpressionStringArray_DIV() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new StringType(0l)),
                            new IdentifierData("test2", new ArrayType(
                                    new LongType(), 0)));
            IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
            when(converter.icg.generateTempIdentifier(any(StringType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new StringType(0l), new IdentifierData("test2", new ArrayType(
//                    new LongType(), 0))))
//            .thenReturn(new IdentifierData("castTemp", new ArrayType(
//                    new LongType(), 0)));
               
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
    //ArrayString DIV
    @Test
    public void testArithmeticBinaryExpressionArrayString_DIV() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new ArrayType(
                                    new LongType(), 0)),
                            new IdentifierData("test2", new StringType(0l)));
            IdentifierData tmp = new IdentifierData("tmp", new ArrayType(
                    new LongType(), 0));
            when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new ArrayType(
//                    new LongType(), 0), new IdentifierData("test2", new StringType(0l))))
//            .thenReturn(new IdentifierData("castTemp", new ArrayType(
//                    new LongType(), 0)));
               
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }

   

    //LongBool SUB
    @Test
    public void testArithmeticBinaryExpressionLongBoolean_SUB() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new LongType()) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
   
   
   
    //BoolLong SUB
   
    @Test
    public void testArithmeticBinaryExpressionBooleanLong_SUB() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e){
           
        }
    }
   
   
   
    @Test
    public void testArithmeticBinaryExpressionDoubleBoolean_SUB() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new DoubleType()) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e){
           
        }
    }
       

    //BooleanDouble SUB   
   
    @Test
    public void testArithmeticBinaryExpressionBooleanDouble_SUB() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new DoubleType()));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e){
           
        }
       
    }
   
    //BOOLBOOL SUB
   
    @Test
    public void testArithmeticBinaryExpressionBooleanBoolean_SUB() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e){
           
        }
    }
   
   
    //BooleanArray SUB
   
    @Test
    public void testArithmeticBinaryExpressionBooleanArray_SUB() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new ArrayType(new LongType(), 0)));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
            .thenReturn(tmp);
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e){
           
        }
       
    }
   
       
       
    //ArrayBoolean SUB
   
    @Test
    public void testArithmeticBinaryExpressionArrayBoolean_SUB() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new ArrayType(new LongType(), 0)) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new ArrayType(new LongType(), 0));
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
       
       
   
    //StructBoolean_SUB
            @Test
    public void testArithmeticBinaryExpressionStructBoolean_SUB() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType())})),
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new StructType(null,
                    new Member[] { new Member("",
                            new LongType())}));
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
                       
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
                   
            converter.convert(node);
                   
            fail();
        }
        catch (IntermediateCodeGeneratorException e){
           
        }
    }
       

    //BooleanStruct_SUB
    @Test
    public void testArithmeticBinaryExpressionBooleanStruct_SUB() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test2", new BooleanType())); ///TODO
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType())}));
            IdentifierData tmp = new IdentifierData("tmp", new StructType(
                    null,
                    new Member[] { new Member("",
                            new LongType())}));
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
                       
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
                   
            converter.convert(node);
                   
                fail();
        }
        catch (IntermediateCodeGeneratorException e){
           
        }
        }
           
    //LongBoolean ADD
    @Test
    public void testArithmeticBinaryExpressionLongBoolean_ADD() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new LongType()) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
                       
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
                   
            converter.convert(node);
                   
                fail();
        }
        catch (IntermediateCodeGeneratorException e){
           
        }
        }
           
           
           
           
    //BooleanLong ADD   
    @Test
    public void testArithmeticBinaryExpressionBooleanLong_ADD() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
                       
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
                   
            converter.convert(node);
                   
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
           
        }
    }
           
           
    //DoubleBool ADD
    @Test
    public void testArithmeticBinaryExpressionDoubleBoolean_ADD() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new DoubleType()) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
                       
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
                   
            converter.convert(node);
                   
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
               

    //BooleanDouble ADD   
           
    @Test
    public void testArithmeticBinaryExpressionBooleanDouble_ADD() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new DoubleType()));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
                       
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
                   
            converter.convert(node);
                   
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
           
    //BOOLBOOL ADD
           
    @Test
    public void testArithmeticBinaryExpressionBooleanBoolean_ADD() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
                       
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
                   
            converter.convert(node);
                   
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
           
           
    //BooleanArray ADD
           
    @Test
    public void testArithmeticBinaryExpressionBooleanArray_ADD() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new ArrayType(new LongType(), 0)));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
            .thenReturn(tmp);
                       
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
                   
            converter.convert(node);
                   
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
           
               
               
    //ArrayBoolean SUB
           
    @Test
    public void testArithmeticBinaryExpressionArrayBoolean_ADD() throws IntermediateCodeGeneratorException{
       
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new ArrayType(new LongType(), 0)) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new ArrayType(new LongType(), 0));
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
                       
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
                   
            converter.convert(node);
                   
            fail();
           
            }
   
            catch (IntermediateCodeGeneratorException e) {
           
       
        }
    }   
               
               
           
    //StructBoolean_ADD
    @Test
    public void testArithmeticBinaryExpressionStructBoolean_ADD() throws IntermediateCodeGeneratorException{
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
                       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                            new LongType())})),
                            new IdentifierData("test2", new BooleanType()));
                    IdentifierData tmp = new IdentifierData("tmp", new StructType(null,
                            new Member[] { new Member("",
                            new LongType())}));
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
                       
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
                   
                converter.convert(node);
                   
                fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
               

    //BooleanStruct_ADD
    @Test
    public void testArithmeticBinaryExpressionBooleanStruct_ADD() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test2", new BooleanType())); ///TODO
                    new IdentifierData("test1", new StructType(
                            null,
                                    new Member[] { new Member("",
                                            new LongType())}));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(StructType.class)))
            .thenReturn(tmp);
                               
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
                           
            converter.convert(node);
                           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
               
           
           
    //LongBool MUL
    @Test
    public void testArithmeticBinaryExpressionLongBoolean_MUL() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new LongType()) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
                               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
                           
            converter.convert(node);
                           
            fail();
            }
            catch (IntermediateCodeGeneratorException e) {
               
            }
                    }
                   
                   
                   
    //BoolLong
                   
    @Test
    public void testArithmeticBinaryExpressionBooleanLong_MUL() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
                               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
                           
            converter.convert(node);
                           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
                   
                   
    //DoubleBool MUL
    @Test
    public void testArithmeticBinaryExpressionDoubleBoolean_MUL() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new DoubleType()) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
                               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
                           
            converter.convert(node);
                           
            fail();
        }
        catch (IntermediateCodeGeneratorException e){
           
        }
    }
                       

    //BooleanDouble MUL   
                   
    @Test
    public void testArithmeticBinaryExpressionBooleanDouble_MUL() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new DoubleType()));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
                               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
                           
            converter.convert(node);
                           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
                   
    //BOOLBOOL MUL
                   
    @Test
    public void testArithmeticBinaryExpressionBooleanBoolean_MUL() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
                               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
                           
            converter.convert(node);
                           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
                   
                   
    //BooleanArray MUL
                   
    @Test
    public void testArithmeticBinaryExpressionBooleanArray_MUL() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new ArrayType(new LongType(), 0)));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
            .thenReturn(tmp);
                               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
                           
            converter.convert(node);
                           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
                   
                       
                       
    //ArrayBoolean MUL
                   
    @Test
    public void testArithmeticBinaryExpressionArrayBoolean_MUL() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new ArrayType(new LongType(), 0)) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new ArrayType(new LongType(), 0));
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
                               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
                           
            converter.convert(node);
                           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
                       
                       
                   
    //StructBoolean_MUL
    @Test
    public void testArithmeticBinaryExpressionStructBoolean_MUL() throws IntermediateCodeGeneratorException{
        try{   
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
                       
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType())})),
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new StructType(
                    null,
                    new Member[] { new Member("",
                            new LongType())}));
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
                               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
                           
            converter.convert(node);
                           
            fail();
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
                       

    //BooleanStruct_MUL
    @Test
    public void testArithmeticBinaryExpressionBooleanStruct_MUL() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test2", new BooleanType()));
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType())}));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
           
            when(converter.icg.generateTempIdentifier(any(StructType.class)))
            .thenReturn(tmp);
                                       
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
                                   
            converter.convert(node);
                                   
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
                               
           
    //LongBool DIV
    @Test
    public void testArithmeticBinaryExpressionLongBoolean_DIV() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new LongType()) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
                                       
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
                                   
            converter.convert(node);
                                   
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
                           
                           
                           
    //BoolLong DIV
                           
    @Test
    public void testArithmeticBinaryExpressionBooleanLong_DIV() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
                                       
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
                                   
            converter.convert(node);
                                   
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
                           
                           
    //DoubleBool DIV
    @Test
    public void testArithmeticBinaryExpressionDoubleBoolean_DIV() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new DoubleType()) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
                                       
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
                                   
            converter.convert(node);
                                   
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
                               

    //BooleanDouble DIV   
                           
    @Test
    public void testArithmeticBinaryExpressionBooleanDouble_DIV() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                        new IdentifierData("test1", new BooleanType()) ,
                        new IdentifierData("test2", new DoubleType()));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
                                       
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
                                   
            converter.convert(node);
                                   
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
                           
    //BOOLBOOL DIV
                           
    @Test
    public void testArithmeticBinaryExpressionBooleanBoolean_DIV() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new BooleanType()) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
                                       
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
                                   
            converter.convert(node);
                                   
            fail();
        }
        catch (IntermediateCodeGeneratorException e){
           
        }
    }
                           
                           
    //BooleanArray DIV
                           
    @Test
    public void testArithmeticBinaryExpressionBooleanArray_DIV() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new BooleanType()) ,
                new IdentifierData("test2", new ArrayType(new LongType(), 0)));
                IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
                .thenReturn(tmp);
                                   
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
                               
            converter.convert(node);
                               
        fail();
        }
        catch (IntermediateCodeGeneratorException e){
           
        }
    }
                           
                               
                               
    //ArrayBoolean DIV
                           
    @Test
    public void testArithmeticBinaryExpressionArrayBoolean_DIV() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new ArrayType(new LongType(), 0)) ,
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new ArrayType(new LongType(), 0));
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
                                       
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
                                   
            converter.convert(node);
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
                               
                               
                           
    //StructBoolean_DIV
            @Test
    public void testArithmeticBinaryExpressionStructBoolean_DIV() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType())})),
                    new IdentifierData("test2", new BooleanType()));
            IdentifierData tmp = new IdentifierData("tmp", new StructType(
                    null,
                    new Member[] { new Member("",
                            new LongType())}));
            when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
            .thenReturn(tmp);
                                       
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
                                   
            converter.convert(node);
                                   
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
                               

           
    //BooleanStruct_DIV
    @Test
    public void testArithmeticBinaryExpressionBooleanStruct_DIV() throws IntermediateCodeGeneratorException{
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test2", new BooleanType())); ///TODO
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType())}));
            IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
            when(converter.icg.generateTempIdentifier(any(StructType.class)))
            .thenReturn(tmp);
               
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
                               
           
                                   
                                   
                                   
                                   
                                   
                                   
                                   
                                   
                                   
                                   
                           
                                   
    //Struct_SUB
    @Test
    public void testArithmeticBinaryExpressionStructLong_SUB() throws IntermediateCodeGeneratorException{
       
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
           
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType()) })),
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                    new Member[] { new Member("",
//                            new DoubleType()) }), new IdentifierData("test2", new LongType())))
//            .thenReturn(new IdentifierData("castTemp", new LongType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e){
           
        }
    }

   

        @Test
        public void testArithmeticBinaryExpressionLongStruct_SUB() throws IntermediateCodeGeneratorException{
           
            try {
                ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
               
               
               
                when(converter.icg.popIdentifierData()).thenReturn(
                        new IdentifierData("test2", new LongType()),
                        new IdentifierData("test1", new StructType(
                                null,
                                new Member[] { new Member("",
                                        new LongType()) })));
                       
                IdentifierData tmp = new IdentifierData("tmp", new LongType());
                when(converter.icg.generateTempIdentifier(any(LongType.class)))
                .thenReturn(tmp);
               
//                when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                        new Member[] { new Member("",
//                                new DoubleType()) }), new IdentifierData("test2", new LongType())))
//                .thenReturn(new IdentifierData("castTemp", new LongType()));
                   
                when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
               
                converter.convert(node);
               
                fail();
            }
            catch (IntermediateCodeGeneratorException e){
               
            }
        }
   
    @Test
    public void testArithmeticBinaryExpressionStructDouble_SUB() throws IntermediateCodeGeneratorException{
       
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
           
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType()) })),
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                    new Member[] { new Member("",
//                            new DoubleType()) }), new IdentifierData("test2", new LongType())))
//            .thenReturn(new IdentifierData("castTemp", new LongType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
   
   
            @Test
            public void testArithmeticBinaryExpressionDoubleStruct_SUB() throws IntermediateCodeGeneratorException{
               
                try {
                    ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
                   
                   
                   
                    when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test2", new DoubleType()),
                            new IdentifierData("test1", new StructType(
                                    null,
                                    new Member[] { new Member("",
                                            new LongType()) })));
                           
                    IdentifierData tmp = new IdentifierData("tmp", new LongType());
                    when(converter.icg.generateTempIdentifier(any(LongType.class)))
                    .thenReturn(tmp);
                   
//                    when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                            new Member[] { new Member("",
//                                    new DoubleType()) }), new IdentifierData("test2", new LongType())))
//                    .thenReturn(new IdentifierData("castTemp", new LongType()));
                       
                    when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
                   
                    converter.convert(node);
                   
                    fail();
                }
                catch (IntermediateCodeGeneratorException e) {
                   
                }
            }
   
   
        @Test
        public void testArithmeticBinaryExpressionStructArray_SUB() throws IntermediateCodeGeneratorException{
           
        try {
    ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
           
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType()) })),
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                    new Member[] { new Member("",
//                            new DoubleType()) }), new IdentifierData("test2", new ArrayType(new LongType(), 0))))
//            .thenReturn(new IdentifierData("castTemp", new LongType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }

       
       
        @Test
        public void testArithmeticBinaryExpressionArrayStruct_SUB() throws IntermediateCodeGeneratorException{
           
            try {
                ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
               
               
               
                when(converter.icg.popIdentifierData()).thenReturn(
                        new IdentifierData("test2", new ArrayType(new LongType(), 0)),
                        new IdentifierData("test1", new StructType(
                                null,
                                new Member[] { new Member("",
                                        new LongType()) })));
                       
                IdentifierData tmp = new IdentifierData("tmp", new LongType());
                when(converter.icg.generateTempIdentifier(any(LongType.class)))
                .thenReturn(tmp);
               
//                when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                        new Member[] { new Member("",
//                                new DoubleType()) }), new IdentifierData("test2", new LongType())))
//                .thenReturn(new IdentifierData("castTemp", new LongType()));
                   
                when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
               
                converter.convert(node);
               
                fail();
            }
            catch (IntermediateCodeGeneratorException e) {
               
            }
           
        }
   
   
        @Test
        public void testArithmeticBinaryExpressionStructStruct_SUB() throws IntermediateCodeGeneratorException{
           
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
           
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType()) })),
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
           
//            when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                    new Member[] { new Member("",
//                            new DoubleType()) }), new IdentifierData("test2", new ArrayType(new LongType(), 0))))
//            .thenReturn(new IdentifierData("castTemp", new LongType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }

       
    //Struct_ADD
    @Test
    public void testArithmeticBinaryExpressionStructLong_ADD() throws IntermediateCodeGeneratorException{
       
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
           
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType()) })),
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
           
//                when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                        new Member[] { new Member("",
//                                new DoubleType()) }), new IdentifierData("test2", new LongType())))
//                .thenReturn(new IdentifierData("castTemp", new LongType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }

   

        @Test
        public void testArithmeticBinaryExpressionLongStruct_ADD() throws IntermediateCodeGeneratorException{
           
            try {
                ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
               
               
               
                when(converter.icg.popIdentifierData()).thenReturn(
                        new IdentifierData("test2", new LongType()),
                        new IdentifierData("test1", new StructType(
                                null,
                                new Member[] { new Member("",
                                        new LongType()) })));
                       
                IdentifierData tmp = new IdentifierData("tmp", new LongType());
                when(converter.icg.generateTempIdentifier(any(LongType.class)))
                .thenReturn(tmp);
               
//                    when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                            new Member[] { new Member("",
//                                    new DoubleType()) }), new IdentifierData("test2", new LongType())))
//                    .thenReturn(new IdentifierData("castTemp", new LongType()));
                   
                when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
               
                converter.convert(node);
               
                fail();
            }
            catch (IntermediateCodeGeneratorException e) {
               
            }
        }
   
    @Test
    public void testArithmeticBinaryExpressionStructDouble_ADD() throws IntermediateCodeGeneratorException{
       
    try {
        ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
       
       
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new StructType(
                        null,
                        new Member[] { new Member("",
                                new LongType()) })),
                new IdentifierData("test2", new LongType()));
        IdentifierData tmp = new IdentifierData("tmp", new LongType());
        when(converter.icg.generateTempIdentifier(any(LongType.class)))
        .thenReturn(tmp);
       
//            when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                    new Member[] { new Member("",
//                            new DoubleType()) }), new IdentifierData("test2", new LongType())))
//            .thenReturn(new IdentifierData("castTemp", new LongType()));
           
        when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
       
        converter.convert(node);
       
        fail();
    }
    catch (IntermediateCodeGeneratorException e) {
       
    }
    }
       
       
    @Test
    public void testArithmeticBinaryExpressionDoubleStruct_ADD() throws IntermediateCodeGeneratorException{
       
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
           
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test2", new DoubleType()),
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType()) })));
                   
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
           
//                        when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                                new Member[] { new Member("",
//                                        new DoubleType()) }), new IdentifierData("test2", new LongType())))
//                        .thenReturn(new IdentifierData("castTemp", new LongType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }


@Test
public void testArithmeticBinaryExpressionStructArray_ADD() throws IntermediateCodeGeneratorException{
   
try {
    ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
   
   
   
    when(converter.icg.popIdentifierData()).thenReturn(
            new IdentifierData("test1", new StructType(
                    null,
                    new Member[] { new Member("",
                            new LongType()) })),
            new IdentifierData("test2", new LongType()));
    IdentifierData tmp = new IdentifierData("tmp", new LongType());
    when(converter.icg.generateTempIdentifier(any(LongType.class)))
    .thenReturn(tmp);
   
//                when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                        new Member[] { new Member("",
//                                new DoubleType()) }), new IdentifierData("test2", new ArrayType(new LongType(), 0))))
//                .thenReturn(new IdentifierData("castTemp", new LongType()));
       
    when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
   
    converter.convert(node);
   
    fail();
}
catch (IntermediateCodeGeneratorException e) {
   
    }
}




@Test
public void testArithmeticBinaryExpressionArrayStruct_ADD() throws IntermediateCodeGeneratorException{
   
try {
    ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
   
   
   
    when(converter.icg.popIdentifierData()).thenReturn(
            new IdentifierData("test2", new ArrayType(new LongType(), 0)),
            new IdentifierData("test1", new StructType(
                    null,
                    new Member[] { new Member("",
                            new LongType()) })));
           
    IdentifierData tmp = new IdentifierData("tmp", new LongType());
    when(converter.icg.generateTempIdentifier(any(LongType.class)))
    .thenReturn(tmp);
   
//                when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                        new Member[] { new Member("",
//                                new DoubleType()) }), new IdentifierData("test2", new LongType())))
//                .thenReturn(new IdentifierData("castTemp", new LongType()));
       
    when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
   
    converter.convert(node);
   
    fail();
}
catch (IntermediateCodeGeneratorException e) {
   
    }
}


@Test
public void testArithmeticBinaryExpressionStructStruct_ADD() throws IntermediateCodeGeneratorException{
   
    try {
        ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
       
       
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new StructType(
                        null,
                        new Member[] { new Member("",
                                new LongType()) })),
                new IdentifierData("test2", new LongType()));
        IdentifierData tmp = new IdentifierData("tmp", new LongType());
        when(converter.icg.generateTempIdentifier(any(LongType.class)))
        .thenReturn(tmp);
       
//                    when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                            new Member[] { new Member("",
//                                    new DoubleType()) }), new IdentifierData("test2", new ArrayType(new LongType(), 0))))
//                    .thenReturn(new IdentifierData("castTemp", new LongType()));
           
        when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
       
        converter.convert(node);
       
        fail();
    }
    catch (IntermediateCodeGeneratorException e) {
       
    }
}



//Struct_MUL
@Test
public void testArithmeticBinaryExpressionStructLong_MUL() throws IntermediateCodeGeneratorException{
   
try {
    ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
   
   
   
    when(converter.icg.popIdentifierData()).thenReturn(
            new IdentifierData("test1", new StructType(
                    null,
                    new Member[] { new Member("",
                            new LongType()) })),
            new IdentifierData("test2", new LongType()));
    IdentifierData tmp = new IdentifierData("tmp", new LongType());
    when(converter.icg.generateTempIdentifier(any(LongType.class)))
    .thenReturn(tmp);
   
//                when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                        new Member[] { new Member("",
//                                new DoubleType()) }), new IdentifierData("test2", new LongType())))
//                .thenReturn(new IdentifierData("castTemp", new LongType()));
       
    when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
   
    converter.convert(node);
   
    fail();
}
catch (IntermediateCodeGeneratorException e) {
   
}
}

           

    @Test
    public void testArithmeticBinaryExpressionLongStruct_MUL() throws IntermediateCodeGeneratorException{
       
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
           
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test2", new LongType()),
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType()) })));
                   
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
           
//                        when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                                new Member[] { new Member("",
//                                        new DoubleType()) }), new IdentifierData("test2", new LongType())))
//                        .thenReturn(new IdentifierData("castTemp", new LongType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }

   
   
@Test
public void testArithmeticBinaryExpressionStructDouble_MUL() throws IntermediateCodeGeneratorException{
   
    try {
        ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
       
       
        when(converter.icg.popIdentifierData()).thenReturn(
                new IdentifierData("test1", new StructType(
                        null,
                        new Member[] { new Member("",
                                new LongType()) })),
                new IdentifierData("test2", new LongType()));
        IdentifierData tmp = new IdentifierData("tmp", new LongType());
        when(converter.icg.generateTempIdentifier(any(LongType.class)))
        .thenReturn(tmp);
       
//                    when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                            new Member[] { new Member("",
//                                    new DoubleType()) }), new IdentifierData("test2", new LongType())))
//                    .thenReturn(new IdentifierData("castTemp", new LongType()));
           
        when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
       
        converter.convert(node);
       
        fail();
    }
    catch (IntermediateCodeGeneratorException e) {
       
    }
}


        @Test
        public void testArithmeticBinaryExpressionDoubleStruct_MUL() throws IntermediateCodeGeneratorException{
           
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
           
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test2", new DoubleType()),
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType()) })));
                   
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
           
//                        when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                                new Member[] { new Member("",
//                                        new DoubleType()) }), new IdentifierData("test2", new LongType())))
//                        .thenReturn(new IdentifierData("castTemp", new LongType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
            }
        }


    @Test
    public void testArithmeticBinaryExpressionStructArray_MUL() throws IntermediateCodeGeneratorException{
       
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
           
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType()) })),
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
           
//                        when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                                new Member[] { new Member("",
//                                        new DoubleType()) }), new IdentifierData("test2", new ArrayType(new LongType(), 0))))
//                        .thenReturn(new IdentifierData("castTemp", new LongType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }

   
    @Test
    public void testArithmeticBinaryExpressionArrayStruct_MUL() throws IntermediateCodeGeneratorException{
       
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
           
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test2", new ArrayType(new LongType(), 0)),
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType()) })));
                   
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
           
//                        when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                                new Member[] { new Member("",
//                                        new DoubleType()) }), new IdentifierData("test2", new LongType())))
//                        .thenReturn(new IdentifierData("castTemp", new LongType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
            }
        }
       
   


    @Test
    public void testArithmeticBinaryExpressionStructStruct_MUL() throws IntermediateCodeGeneratorException{
       
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
           
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType()) })),
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
           
//                        when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                                new Member[] { new Member("",
//                                        new DoubleType()) }), new IdentifierData("test2", new ArrayType(new LongType(), 0))))
//                        .thenReturn(new IdentifierData("castTemp", new LongType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }


    //Struct_MUL
    @Test
    public void testArithmeticBinaryExpressionStructLong_DIV() throws IntermediateCodeGeneratorException{
       
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
           
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType()) })),
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
           
//                        when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                                new Member[] { new Member("",
//                                        new DoubleType()) }), new IdentifierData("test2", new LongType())))
//                        .thenReturn(new IdentifierData("castTemp", new LongType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }

   

        @Test
        public void testArithmeticBinaryExpressionLongStruct_DIV() throws IntermediateCodeGeneratorException{
           
            try {
                ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
               
               
               
                when(converter.icg.popIdentifierData()).thenReturn(
                        new IdentifierData("test2", new LongType()),
                        new IdentifierData("test1", new StructType(
                                null,
                                new Member[] { new Member("",
                                        new LongType()) })));
                       
                IdentifierData tmp = new IdentifierData("tmp", new LongType());
                when(converter.icg.generateTempIdentifier(any(LongType.class)))
                .thenReturn(tmp);
               
//                            when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                                    new Member[] { new Member("",
//                                            new DoubleType()) }), new IdentifierData("test2", new LongType())))
//                            .thenReturn(new IdentifierData("castTemp", new LongType()));
                   
                when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
               
                converter.convert(node);
               
                fail();
            }
            catch (IntermediateCodeGeneratorException e) {
               
            }
        }
   
       
    @Test
    public void testArithmeticBinaryExpressionStructDouble_DIV() throws IntermediateCodeGeneratorException{
       
        try {
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
           
           
           
            when(converter.icg.popIdentifierData()).thenReturn(
                    new IdentifierData("test1", new StructType(
                            null,
                            new Member[] { new Member("",
                                    new LongType()) })),
                    new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
           
//                        when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                                new Member[] { new Member("",
//                                        new DoubleType()) }), new IdentifierData("test2", new LongType())))
//                        .thenReturn(new IdentifierData("castTemp", new LongType()));
               
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
           
            converter.convert(node);
           
            fail();
        }
        catch (IntermediateCodeGeneratorException e) {
           
        }
    }
   
   
            @Test
            public void testArithmeticBinaryExpressionDoubleStruct_DIV() throws IntermediateCodeGeneratorException{
               
                try {
                    ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
                   
                   
                   
                    when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test2", new DoubleType()),
                            new IdentifierData("test1", new StructType(
                                    null,
                                    new Member[] { new Member("",
                                            new LongType()) })));
                           
                    IdentifierData tmp = new IdentifierData("tmp", new LongType());
                    when(converter.icg.generateTempIdentifier(any(LongType.class)))
                    .thenReturn(tmp);
                   
//                                when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                                        new Member[] { new Member("",
//                                                new DoubleType()) }), new IdentifierData("test2", new LongType())))
//                                .thenReturn(new IdentifierData("castTemp", new LongType()));
                       
                    when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
                   
                    converter.convert(node);
                   
                    fail();
                }
                catch (IntermediateCodeGeneratorException e) {
                   
                }
            }
   
   
        @Test
        public void testArithmeticBinaryExpressionStructArray_DIV() throws IntermediateCodeGeneratorException{
           
            try {
                ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
               
               
               
                when(converter.icg.popIdentifierData()).thenReturn(
                        new IdentifierData("test1", new StructType(
                                null,
                                new Member[] { new Member("",
                                        new LongType()) })),
                        new IdentifierData("test2", new LongType()));
                IdentifierData tmp = new IdentifierData("tmp", new LongType());
                when(converter.icg.generateTempIdentifier(any(LongType.class)))
                .thenReturn(tmp);
               
//                            when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                                    new Member[] { new Member("",
//                                            new DoubleType()) }), new IdentifierData("test2", new ArrayType(new LongType(), 0))))
//                            .thenReturn(new IdentifierData("castTemp", new LongType()));
                   
                when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
               
                converter.convert(node);
               
                fail();
            }
            catch (IntermediateCodeGeneratorException e) {
               
            }
        }

       
        @Test
        public void testArithmeticBinaryExpressionArrayStruct_DIV() throws IntermediateCodeGeneratorException{
           
            try {
                ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
               
               
               
                when(converter.icg.popIdentifierData()).thenReturn(
                        new IdentifierData("test2", new ArrayType(new LongType(), 0)),
                        new IdentifierData("test1", new StructType(
                                null,
                                new Member[] { new Member("",
                                        new LongType()) })));
                       
                IdentifierData tmp = new IdentifierData("tmp", new LongType());
                when(converter.icg.generateTempIdentifier(any(LongType.class)))
                .thenReturn(tmp);
               
//                            when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                                    new Member[] { new Member("",
//                                            new DoubleType()) }), new IdentifierData("test2", new LongType())))
//                            .thenReturn(new IdentifierData("castTemp", new LongType()));
                   
                when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
               
                converter.convert(node);
               
                fail();
            }
            catch (IntermediateCodeGeneratorException e) {
               
            }
        }
   
   
        @Test
        public void testArithmeticBinaryExpressionStructStruct_DIV() throws IntermediateCodeGeneratorException{
           
            try {
                ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
               
               
               
                when(converter.icg.popIdentifierData()).thenReturn(
                        new IdentifierData("test1", new StructType(
                                null,
                                new Member[] { new Member("",
                                        new LongType()) })),
                        new IdentifierData("test2", new LongType()));
                IdentifierData tmp = new IdentifierData("tmp", new LongType());
                when(converter.icg.generateTempIdentifier(any(LongType.class)))
                .thenReturn(tmp);
               
//                            when(((CastingAst2CodeConverter) converter.icg).cast(new StructType(null,
//                                    new Member[] { new Member("",
//                                            new DoubleType()) }), new IdentifierData("test2", new ArrayType(new LongType(), 0))))
//                            .thenReturn(new IdentifierData("castTemp", new LongType()));
                   
                when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
               
                converter.convert(node);
               
                fail();
            }
            catch (IntermediateCodeGeneratorException e) {
               
            }
        }   
       
    //ArrayLong ADD
    @Test
    public void testArithmeticBinaryExpressionArrayLong_ADD() throws IntermediateCodeGeneratorException{
       
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new ArrayType(
                                    new LongType(), 0)),
                            new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
                       
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new ArrayType(
    //                new LongType(), 0), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new ArrayType(
    //                new LongType(), 0)));
    //           
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
           
            converter.convert(node);
            fail();
           
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
    //ArrayDouble ADD
    @Test
    public void testArithmeticBinaryExpressionArrayDouble_ADD() throws IntermediateCodeGeneratorException{
       
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new ArrayType(
                                    new LongType(), 0)),
                            new IdentifierData("test2", new DoubleType()));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
                       
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new ArrayType(
    //                new LongType(), 0), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new ArrayType(
    //                new LongType(), 0)));
    //           
            when(node.getOperator()).thenReturn(BinaryOperator.ADDITION);
           
            converter.convert(node);
            fail();
           
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
    //ArrayLong SUB
    @Test
    public void testArithmeticBinaryExpressionArrayLong_SUB() throws IntermediateCodeGeneratorException{
       
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new ArrayType(
                                    new LongType(), 0)),
                            new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
                       
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new ArrayType(
    //                new LongType(), 0), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new ArrayType(
    //                new LongType(), 0)));
    //           
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
            fail();
           
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
    //ArrayDouble SUB
    @Test
    public void testArithmeticBinaryExpressionArrayDouble_SUB() throws IntermediateCodeGeneratorException{
       
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new ArrayType(
                                    new LongType(), 0)),
                            new IdentifierData("test2", new DoubleType()));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
                       
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new ArrayType(
    //                new LongType(), 0), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new ArrayType(
    //                new LongType(), 0)));
    //           
            when(node.getOperator()).thenReturn(BinaryOperator.SUBSTRACTION);
           
            converter.convert(node);
            fail();
           
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
    //ArrayLong MUL
    @Test
    public void testArithmeticBinaryExpressionArrayLong_MUL() throws IntermediateCodeGeneratorException{
       
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new ArrayType(
                                    new LongType(), 0)),
                            new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
                       
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new ArrayType(
    //                new LongType(), 0), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new ArrayType(
    //                new LongType(), 0)));
    //           
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
            fail();
           
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
    //ArrayDouble MUL
    @Test
    public void testArithmeticBinaryExpressionArrayDouble_MUL() throws IntermediateCodeGeneratorException{
       
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new ArrayType(
                                    new LongType(), 0)),
                            new IdentifierData("test2", new DoubleType()));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
                       
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new ArrayType(
    //                new LongType(), 0), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new ArrayType(
    //                new LongType(), 0)));
    //           
            when(node.getOperator()).thenReturn(BinaryOperator.MULTIPLICATION);
           
            converter.convert(node);
            fail();
           
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }

    //ArrayLong ADD
    @Test
    public void testArithmeticBinaryExpressionArrayLong_DIV() throws IntermediateCodeGeneratorException{
       
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new ArrayType(
                                    new LongType(), 0)),
                            new IdentifierData("test2", new LongType()));
            IdentifierData tmp = new IdentifierData("tmp", new LongType());
            when(converter.icg.generateTempIdentifier(any(LongType.class)))
            .thenReturn(tmp);
                       
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new ArrayType(
    //                new LongType(), 0), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new ArrayType(
    //                new LongType(), 0)));
    //           
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
           
            converter.convert(node);
            fail();
           
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
   
    //ArrayDouble DIV
    @Test
    public void testArithmeticBinaryExpressionArrayDouble_DIV() throws IntermediateCodeGeneratorException{
       
        try{
            ArithmeticBinaryExpressionNode node =  mock(ArithmeticBinaryExpressionNode.class);
       
            when(converter.icg.popIdentifierData()).thenReturn(
                            new IdentifierData("test1", new ArrayType(
                                    new LongType(), 0)),
                            new IdentifierData("test2", new DoubleType()));
            IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
            when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
            .thenReturn(tmp);
                       
    //        when(((CastingAst2CodeConverter) converter.icg).cast(new ArrayType(
    //                new LongType(), 0), new IdentifierData("test2", new StringType(0l))))
    //        .thenReturn(new IdentifierData("castTemp", new ArrayType(
    //                new LongType(), 0)));
    //           
            when(node.getOperator()).thenReturn(BinaryOperator.DIVISION);
           
            converter.convert(node);
            fail();
           
        }
        catch(IntermediateCodeGeneratorException e){
           
        }
    }
}








