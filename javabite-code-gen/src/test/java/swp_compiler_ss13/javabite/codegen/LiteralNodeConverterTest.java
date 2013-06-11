package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.javabite.codegen.converters.LiteralNodeConverter;

public class LiteralNodeConverterTest {
	LiteralNodeConverter converter;
	
	@Before
	public void setUp() throws Exception {
		converter = new LiteralNodeConverter();

		converter.icg = Mockito
				.mock(IntermediateCodeGeneratorJb.class);
	}
	
	
	//noch überarbeiten
	
	@Test
	public void testDeclarationNodeConverter_Long(){
		ASTNode node = Mockito.mock(DeclarationNode.class);
		verify(converter.icg).pushIdentifierData(new IdentifierData("test", new LongType()));
	}
	
	
	@Test
	public void testDeclarationNodeConverter_Double(){
		ASTNode node = Mockito.mock(DeclarationNode.class);
		verify(converter.icg).pushIdentifierData(new IdentifierData("test", new LongType()));
	}
	
	
	@Test
	public void testDeclarationNodeConverter_Boolean(){
		ASTNode node = Mockito.mock(DeclarationNode.class);
		fail();
	}
	
	
	@Test
	public void testDeclarationNodeConverter_String(){
		ASTNode node = Mockito.mock(DeclarationNode.class);
		fail();
	}
	
	
	@Test
	public void testDeclarationNodeConverter_Array(){
		ASTNode node = Mockito.mock(DeclarationNode.class);
		fail();
	}
}
