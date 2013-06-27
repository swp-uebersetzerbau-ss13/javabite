package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
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
		try {
			LiteralNode node = Mockito.mock(LiteralNode.class);
			node.setLiteral("test");
			node.setLiteralType(new LongType());
			converter.convert(node);
			verify(converter.icg).pushIdentifierData(new IdentifierData("#"+node.getLiteral(), node.getLiteralType()));
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
		
	}
	
	
	/**
	 * Test for a LiteralNode, which has the type Double.
	 */
	@Test
	public void testLiteralNodeConverter_Double(){
		try {
			LiteralNode node = Mockito.mock(LiteralNode.class);
			node.setLiteral("test");
			node.setLiteralType(new DoubleType());
			converter.convert(node);
			verify(converter.icg).pushIdentifierData(new IdentifierData("#"+node.getLiteral(), node.getLiteralType()));
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
	}
	
	
	/**
	 * Test for a LiteralNode, which has the type Boolean.
	 * It fails, because the type is not supported.
	 */
	@Test
	public void testLiteralNodeConverter_Boolean(){
		try {
			LiteralNode node = Mockito.mock(LiteralNode.class);
			node.setLiteral("test");
			node.setLiteralType(new BooleanType());
			converter.convert(node);
			verify(converter.icg).pushIdentifierData(new IdentifierData("#"+node.getLiteral(), node.getLiteralType()));
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
	}
	
	
	
	/**
	 * Test for a LiteralNode, which has the type String.
	 * It fails, because the type is not supported.
	 */
	@Test
	public void testLiteralNodeConverter_String(){
		try {
			LiteralNode node = Mockito.mock(LiteralNode.class);
			node.setLiteral("test");
			node.setLiteralType(new StringType(0l));
			converter.convert(node);
			verify(converter.icg).pushIdentifierData(new IdentifierData("#"+node.getLiteral(), node.getLiteralType()));
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
	}
	
	
	
	/**
	 * Test for a LiteralNode, which has the type Struct.
	 * It fails, because the type is not supported.
	 */
	@Test
	public void testLiteralNodeConverter_Struct(){
		try {
			LiteralNode node = Mockito.mock(LiteralNode.class);
			node.setLiteral("test");
			node.setLiteralType(new StructType(
					null,
					new Member[] { new Member("",
							new LongType()) }));
			converter.convert(node);
			verify(converter.icg).pushIdentifierData(new IdentifierData("#"+node.getLiteral(), node.getLiteralType()));
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
		
	}
	
	
	
	/**
	 * Test for a LiteralNode, which has the type Array.
	 * It fails, because the type is not supported.
	 */
	@Test
	public void testLiteralNodeConverter_Array(){
		try {
			LiteralNode node = Mockito.mock(LiteralNode.class);
			node.setLiteral("test");
			node.setLiteralType(new ArrayType(new LongType(), 0));
			converter.convert(node);
			verify(converter.icg).pushIdentifierData(new IdentifierData("#"+node.getLiteral(), node.getLiteralType()));
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
		
	}
}
