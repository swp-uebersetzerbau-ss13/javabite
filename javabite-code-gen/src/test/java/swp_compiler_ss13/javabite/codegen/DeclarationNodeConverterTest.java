package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
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
import swp_compiler_ss13.javabite.codegen.converters.DeclarationNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public class DeclarationNodeConverterTest {
	DeclarationNodeConverter converter;
	
	@Before
	public void setUp() throws Exception {
		converter = new DeclarationNodeConverter();

		converter.icg = Mockito
				.mock(IntermediateCodeGeneratorJb.class);
	}
	
	
	/**
	 * Test for a DeclarationNode, which has the type Long.
	 */
	@Test
	public void testDeclarationNodeConverter_Long(){
		try{
			DeclarationNode node = Mockito.mock(DeclarationNode.class);
			node.setIdentifier("test");
			node.setType(new LongType());
			when(converter.icg.generateIdentifierMapping(node.getIdentifier(), node.getType())).
				thenReturn(new IdentifierData("test", new LongType()));
			
			converter.convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.DECLARE_LONG,
					Quadruple.EmptyArgument, Quadruple.EmptyArgument,node.getIdentifier()));
		}catch(IntermediateCodeGeneratorException e){
			fail();
		}
	}
	
	
	/**
	 * Test for a DeclarationNode, which has the type Double.
	 */
	@Test
	public void testDeclarationNodeConverter_Double(){
		try{
			DeclarationNode node = Mockito.mock(DeclarationNode.class);
			node.setIdentifier("test");
			node.setType(new DoubleType());
			when(converter.icg.generateIdentifierMapping(node.getIdentifier(), node.getType())).
				thenReturn(new IdentifierData("test", new DoubleType()));
			
			converter.convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.DECLARE_DOUBLE,
					Quadruple.EmptyArgument, Quadruple.EmptyArgument,node.getIdentifier()));
		}catch(IntermediateCodeGeneratorException e){
			fail();
		}
	}
	
	
	
	/**
	 * Test for a DeclarationNode, which has the type Boolean.
	 */
	@Test
	public void testDeclarationNodeConverter_Boolean(){
		try{
			DeclarationNode node = Mockito.mock(DeclarationNode.class);
			node.setIdentifier("test");
			node.setType(new BooleanType());
			when(converter.icg.generateIdentifierMapping(node.getIdentifier(), node.getType())).
				thenReturn(new IdentifierData("test", new BooleanType()));
			
			converter.convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.DECLARE_BOOLEAN,
					Quadruple.EmptyArgument, Quadruple.EmptyArgument,node.getIdentifier()));
		}catch(IntermediateCodeGeneratorException e){
			fail();
		}
	}
	
	
	/**
	 * Test for a DeclarationNode, which has the type String.
	 */
	@Test
	public void testDeclarationNodeConverter_String(){
		try{
			DeclarationNode node = Mockito.mock(DeclarationNode.class);
			node.setIdentifier("test");
			node.setType(new StringType(0l));
			when(converter.icg.generateIdentifierMapping(node.getIdentifier(), node.getType())).
				thenReturn(new IdentifierData("test", new StringType(0l)));
			
			converter.convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.DECLARE_STRING,
					Quadruple.EmptyArgument, Quadruple.EmptyArgument,node.getIdentifier()));
		}catch(IntermediateCodeGeneratorException e){
			fail();
		}
	}
	
	
	/**
	 * Test for a DeclarationNode, which has the type Array.
	 */
	@Test
	public void testDeclarationNodeConverter_Array(){
		try{
			DeclarationNode node = Mockito.mock(DeclarationNode.class);
			node.setIdentifier("test");
			node.setType(new ArrayType(new LongType(), 0));
			IdentifierData data = new IdentifierData("test", new LongType());
			when(converter.icg.generateIdentifierMapping(node.getIdentifier(), node.getType())).
				thenReturn(data);
			ArrayType arrayType = new ArrayType(new LongType(), 0);
			converter.convert(node);
			verify(converter.icg).addQuadruple(new QuadrupleJb(Operator.DECLARE_ARRAY,
					"#" + arrayType.getLength() , Quadruple.EmptyArgument,node.getIdentifier()));
			
		}catch(IntermediateCodeGeneratorException e){
			fail();
		}
	}
	
	/**
	 * Test for a DeclarationNode, which has the type Array.
	 */
	@Test
	public void testDeclarationNodeConverter_Struct(){
		try{
			DeclarationNode node = Mockito.mock(DeclarationNode.class);
			node.setIdentifier("test");
			node.setType(new StructType(null,
                    new Member[] { new Member("",
                            new DoubleType()) }));
			IdentifierData data = new IdentifierData("test", new StructType(null,
                    new Member[] { new Member("",
                            new DoubleType()) }));
			when(converter.icg.generateIdentifierMapping(node.getIdentifier(), node.getType())).
				thenReturn(data);
			converter.convert(node);
			fail();
			
		}catch(IntermediateCodeGeneratorException e){
			
		}
	}
}
