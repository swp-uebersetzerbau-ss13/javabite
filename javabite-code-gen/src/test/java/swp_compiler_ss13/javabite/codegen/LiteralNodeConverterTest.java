package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.types.derived.ArrayType;
import swp_compiler_ss13.common.types.derived.Member;
import swp_compiler_ss13.common.types.derived.StructType;
import swp_compiler_ss13.common.types.primitive.BooleanType;
import swp_compiler_ss13.common.types.primitive.DoubleType;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.common.types.primitive.StringType;
import swp_compiler_ss13.javabite.codegen.converters.LiteralNodeConverter;

public class LiteralNodeConverterTest {
	LiteralNodeConverter converter;
	
	@Before
	public void setUp() throws Exception {
		converter = new LiteralNodeConverter();

		converter.icg = Mockito
				.mock(IntermediateCodeGeneratorJb.class);
	}
	
	
	/**
	 * Test for a LiteralNode, which has the type Long.
	 */
	@Test
	public void testLiteralNodeConverter_Long(){
		ASTNode node = Mockito.mock(LiteralNode.class);
		verify(converter.icg).pushIdentifierData(new IdentifierData("test", new LongType()));
	}
	
	
	/**
	 * Test for a LiteralNode, which has the type Double.
	 */
	@Test
	public void testLiteralNodeConverter_Double(){
		ASTNode node = Mockito.mock(LiteralNode.class);
		verify(converter.icg).pushIdentifierData(new IdentifierData("test", new DoubleType()));
	}
	
	
	/**
	 * Test for a LiteralNode, which has the type Boolean.
	 * It fails, because the type is not supported.
	 */
	@Test
	public void testLiteralNodeConverter_Boolean(){
		ASTNode node = Mockito.mock(LiteralNode.class);
		verify(converter.icg).pushIdentifierData(new IdentifierData("test", new BooleanType()));
		fail();
	}
	
	
	
	/**
	 * Test for a LiteralNode, which has the type String.
	 * It fails, because the type is not supported.
	 */
	@Test
	public void testLiteralNodeConverter_String(){
		ASTNode node = Mockito.mock(LiteralNode.class);
		verify(converter.icg).pushIdentifierData(new IdentifierData("test", new StringType(0l)));
		fail();
	}
	
	
	
	/**
	 * Test for a LiteralNode, which has the type Struct.
	 * It fails, because the type is not supported.
	 */
	@Test
	public void testLiteralNodeConverter_Struct(){
		ASTNode node = Mockito.mock(LiteralNode.class);
		verify(converter.icg).pushIdentifierData(new IdentifierData("test", new StructType(
				null,
				new Member[] { new Member("",
						new LongType()) })));
		fail();
	}
	
	
	
	/**
	 * Test for a LiteralNode, which has the type Array.
	 * It fails, because the type is not supported.
	 */
	@Test
	public void testLiteralNodeConverter_Array(){
		ASTNode node = Mockito.mock(LiteralNode.class);
		verify(converter.icg).pushIdentifierData(new IdentifierData("test", new ArrayType(new LongType(), 0)));
		fail();
	}
}
