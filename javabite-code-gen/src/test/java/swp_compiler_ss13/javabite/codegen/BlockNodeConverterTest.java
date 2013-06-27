package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.converters.BlockNodeConverter;

public class BlockNodeConverterTest {
	BlockNodeConverter converter;
	
	@Before
	public void setUp() throws Exception {
		converter = new BlockNodeConverter();

		converter.icg = Mockito
				.mock(IntermediateCodeGeneratorJb.class);
	}
	
	/**
	 * Testing the BlockNodeConverter for entering a new scope 
	 * with given test object icg. If it is executed, it will 
	 * be created a new BlockNode test object.
	 */
	@Test
	public void testBlockNodeConverter(){ 
		try{
			BlockNode node = Mockito.mock(BlockNode.class);
			
			when(node.getDeclarationList()).thenReturn(new ArrayList<DeclarationNode>());
			when(node.getStatementList()).thenReturn(new ArrayList<StatementNode>());
			converter.convert(node);
			verify(converter.icg).enterNewScope();
			verify(converter.icg).leaveCurrentScope();
			
		}
		catch(IntermediateCodeGeneratorException e){
			fail();
		}
		
		
	}
}
