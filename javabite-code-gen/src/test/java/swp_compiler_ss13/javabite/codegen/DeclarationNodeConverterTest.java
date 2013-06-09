package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.binary.LogicBinaryExpressionNode;
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
}
