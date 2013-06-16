package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.javabite.codegen.converters.DeclarationNodeConverter;

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
			ASTNode node = Mockito.mock(DeclarationNode.class);
			when(converter.icg.generateIdentifierMapping("test", new LongType())).
				thenReturn(new IdentifierData("test", new LongType()));
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
			ASTNode node = Mockito.mock(DeclarationNode.class);
			when(converter.icg.generateIdentifierMapping("test", new LongType())).
				thenReturn(new IdentifierData("test", new LongType()));
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
			ASTNode node = Mockito.mock(DeclarationNode.class);
			when(converter.icg.generateIdentifierMapping("test", new LongType())).
				thenReturn(new IdentifierData("test", new LongType()));
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
			ASTNode node = Mockito.mock(DeclarationNode.class);
			when(converter.icg.generateIdentifierMapping("test", new LongType())).
				thenReturn(new IdentifierData("test", new LongType()));
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
			ASTNode node = Mockito.mock(DeclarationNode.class);
			when(converter.icg.generateIdentifierMapping("test", new LongType())).
				thenReturn(new IdentifierData("test", new LongType()));
		}catch(IntermediateCodeGeneratorException e){
			fail();
		}
	}
}
