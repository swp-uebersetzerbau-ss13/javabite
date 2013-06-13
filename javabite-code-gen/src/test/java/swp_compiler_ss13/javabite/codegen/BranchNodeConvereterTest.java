package swp_compiler_ss13.javabite.codegen;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.ternary.BranchNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.converters.BlockNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public class BranchNodeConvereterTest {

	BlockNodeConverter converter;
	
	@Before
	public void setUp() throws Exception {
		converter = new BlockNodeConverter();

		converter.icg = Mockito
				.mock(IntermediateCodeGeneratorJb.class);
	}
	
	/**
	 * Testing the BranchNodeConverter with given test icg object 
	 */
	@Test
	public void testBranchNodeConverter(){
		try{
			BranchNode branchNode = Mockito.mock(BranchNode.class);
			when(branchNode.getStatementNodeOnFalse()).thenReturn(any(StatementNode.class));
			boolean hasFalseBlock = branchNode.getStatementNodeOnFalse() != null;
			when(converter.icg.getNewLabel()).thenReturn(any(String.class));
			String trueLabel = converter.icg.getNewLabel();
			String falseLabel = null;
			
			if (hasFalseBlock) {
				when(converter.icg.getNewLabel()).thenReturn(any(String.class));
				falseLabel = converter.icg.getNewLabel();
			}
			
			when(converter.icg.getNewLabel()).thenReturn(any(String.class));
			String endLabel = converter.icg.getNewLabel();
			
			when(branchNode.getCondition()).thenReturn((ExpressionNode) any(ExpressionNode.class));
			verify(converter.icg).processNode(branchNode.getCondition());

			if (hasFalseBlock) {
				verify(converter.icg).addQuadruple(
						new QuadrupleJb(Operator.BRANCH, trueLabel, falseLabel,"test"));
				verify(converter.icg).addQuadruple(
						new QuadrupleJb(Operator.LABEL, trueLabel, Quadruple.EmptyArgument,
								Quadruple.EmptyArgument));
				
				when(branchNode.getStatementNodeOnTrue()).thenReturn(any(StatementNode.class));
				verify(converter.icg).processNode(branchNode.getStatementNodeOnTrue());
				
				verify(converter.icg).addQuadruple(
						new QuadrupleJb(Operator.BRANCH, endLabel, Quadruple.EmptyArgument,
								Quadruple.EmptyArgument));
				
				verify(converter.icg).addQuadruple(
						new QuadrupleJb(Operator.LABEL, falseLabel, Quadruple.EmptyArgument,
								Quadruple.EmptyArgument));
				
				when(branchNode.getStatementNodeOnTrue()).thenReturn(any(StatementNode.class));
				verify(converter.icg).processNode(branchNode.getStatementNodeOnFalse());
				
				verify(converter.icg).addQuadruple(
						new QuadrupleJb(Operator.LABEL, endLabel, Quadruple.EmptyArgument,
								Quadruple.EmptyArgument));
			} else {
				verify(converter.icg).addQuadruple(
						new QuadrupleJb(Operator.BRANCH, trueLabel, endLabel,"test"));
				
				verify(converter.icg).addQuadruple(
						new QuadrupleJb(Operator.LABEL, trueLabel, Quadruple.EmptyArgument,
								Quadruple.EmptyArgument));
				
				when(branchNode.getStatementNodeOnTrue()).thenReturn(any(StatementNode.class));
				verify(converter.icg).processNode(branchNode.getStatementNodeOnFalse());
				
				verify(converter.icg).addQuadruple(
						new QuadrupleJb(Operator.LABEL, endLabel, Quadruple.EmptyArgument,
								Quadruple.EmptyArgument));		
				}
			
		}
		catch(IntermediateCodeGeneratorException e){
			fail();
		}
	}
		
}
