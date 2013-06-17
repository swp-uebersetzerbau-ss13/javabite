package swp_compiler_ss13.javabite.codegen;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.converters.BlockNodeConverter;

@Ignore
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
	@SuppressWarnings("unchecked")
	@Test
	public void testBlockNodeConverter(){ 
		try{
			BlockNode node = Mockito.mock(BlockNode.class);
			verify(converter.icg).enterNewScope();
			
			List<DeclarationNode> decList = Mockito.mock(List.class);
			when(node.getDeclarationList()).thenReturn(any(List.class));
			for(DeclarationNode decNode : decList){
				verify(converter.icg).processNode(decNode);
			}
			
			List<StatementNode> stateList = Mockito.mock(List.class);
			when(node.getStatementList()).thenReturn(any(List.class));
			for(StatementNode stateNode : stateList){
				verify(converter.icg).processNode(stateNode);
			}
			
			verify(converter.icg).leaveCurrentScope();
		}
		catch(IntermediateCodeGeneratorException e){
			fail();
		}
		
		
	}
}
