package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.ternary.BranchNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.primitive.BooleanType;
import swp_compiler_ss13.javabite.codegen.converters.BranchNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public class BranchNodeConvereterTest {

	BranchNodeConverter converter;
	Ast2CodeConverterCompatibleGenerator icg;

	@Before
	public void setUp() throws Exception {
		converter = new BranchNodeConverter();
		icg = Mockito.mock(IntermediateCodeGeneratorJb.class);
		converter.icg = icg;
	}

	@Test
	public void testBranchNodeConverterWithTrueAndFalse() {
		try {
			StatementNode trueNode = Mockito.mock(StatementNode.class);
			StatementNode falseNode = Mockito.mock(StatementNode.class);
			BranchNode branchNode = Mockito.mock(BranchNode.class);
			when(branchNode.getStatementNodeOnFalse()).thenReturn(falseNode);
			when(icg.getNewLabel()).thenReturn("trueLabel", "falseLabel",
					"endLabel");
			ExpressionNode condition = Mockito.mock(ExpressionNode.class);
			when(branchNode.getCondition()).thenReturn(condition);
			when(icg.popIdentifierData()).thenReturn(
					new IdentifierData("conditionResult", new BooleanType()));
			when(branchNode.getStatementNodeOnTrue()).thenReturn(trueNode);
			when(branchNode.getStatementNodeOnFalse()).thenReturn(falseNode);

			converter.convert(branchNode);

			verify(icg).processNode(condition);
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.BRANCH, "trueLabel", "falseLabel",
							"conditionResult"));
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.LABEL, "trueLabel",
							Quadruple.EmptyArgument, Quadruple.EmptyArgument));
			verify(converter.icg).processNode(trueNode);
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.BRANCH, "endLabel",
							Quadruple.EmptyArgument, Quadruple.EmptyArgument));

			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.LABEL, "falseLabel",
							Quadruple.EmptyArgument, Quadruple.EmptyArgument));
			verify(converter.icg).processNode(falseNode);

			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.LABEL, "endLabel",
							Quadruple.EmptyArgument, Quadruple.EmptyArgument));
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
	}

	@Test
	public void testBranchNodeConverterOnlyTrue() {
		try {
			StatementNode trueNode = Mockito.mock(StatementNode.class);
			BranchNode branchNode = Mockito.mock(BranchNode.class);
			when(icg.getNewLabel()).thenReturn("trueLabel", "endLabel");
			ExpressionNode condition = Mockito.mock(ExpressionNode.class);
			when(branchNode.getCondition()).thenReturn(condition);
			when(icg.popIdentifierData()).thenReturn(
					new IdentifierData("conditionResult", new BooleanType()));
			when(branchNode.getStatementNodeOnTrue()).thenReturn(trueNode);

			converter.convert(branchNode);

			verify(icg).processNode(condition);
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.BRANCH, "trueLabel", "endLabel",
							"conditionResult"));
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.LABEL, "trueLabel",
							Quadruple.EmptyArgument, Quadruple.EmptyArgument));
			verify(converter.icg).processNode(trueNode);
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.LABEL, "endLabel",
							Quadruple.EmptyArgument, Quadruple.EmptyArgument));
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
	}

	@Test
	public void testBranchNodeConverterConstantTrue() {
		try {
			StatementNode trueNode = Mockito.mock(StatementNode.class);
			StatementNode falseNode = Mockito.mock(StatementNode.class);
			BranchNode branchNode = Mockito.mock(BranchNode.class);
			when(branchNode.getStatementNodeOnFalse()).thenReturn(falseNode);
			when(icg.getNewLabel()).thenReturn("trueLabel", "falseLabel",
					"endLabel");
			ExpressionNode condition = Mockito.mock(ExpressionNode.class);
			when(branchNode.getCondition()).thenReturn(condition);
			when(icg.popIdentifierData()).thenReturn(
					new IdentifierData("#true", new BooleanType()));
			when(branchNode.getStatementNodeOnTrue()).thenReturn(trueNode);
			when(branchNode.getStatementNodeOnFalse()).thenReturn(falseNode);

			converter.convert(branchNode);

			verify(icg).processNode(condition);
			verify(converter.icg).processNode(trueNode);
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
	}

	@Test
	public void testBranchNodeConverterOnlyTrueConstantFalse() {
		try {
			StatementNode trueNode = Mockito.mock(StatementNode.class);
			StatementNode falseNode = Mockito.mock(StatementNode.class);
			BranchNode branchNode = Mockito.mock(BranchNode.class);
			when(branchNode.getStatementNodeOnFalse()).thenReturn(falseNode);
			when(icg.getNewLabel()).thenReturn("trueLabel", "falseLabel",
					"endLabel");
			ExpressionNode condition = Mockito.mock(ExpressionNode.class);
			when(branchNode.getCondition()).thenReturn(condition);
			when(icg.popIdentifierData()).thenReturn(
					new IdentifierData("#false", new BooleanType()));
			when(branchNode.getStatementNodeOnTrue()).thenReturn(trueNode);
			when(branchNode.getStatementNodeOnFalse()).thenReturn(falseNode);

			converter.convert(branchNode);

			verify(icg).processNode(condition);
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
	}

	@Test
	public void testBranchNodeConverterWithTrueAndFalseConstantFalse() {
		try {
			StatementNode trueNode = Mockito.mock(StatementNode.class);
			StatementNode falseNode = Mockito.mock(StatementNode.class);
			BranchNode branchNode = Mockito.mock(BranchNode.class);
			when(branchNode.getStatementNodeOnFalse()).thenReturn(falseNode);
			when(icg.getNewLabel()).thenReturn("trueLabel", "falseLabel",
					"endLabel");
			ExpressionNode condition = Mockito.mock(ExpressionNode.class);
			when(branchNode.getCondition()).thenReturn(condition);
			when(icg.popIdentifierData()).thenReturn(
					new IdentifierData("#false", new BooleanType()));
			when(branchNode.getStatementNodeOnTrue()).thenReturn(trueNode);
			when(branchNode.getStatementNodeOnFalse()).thenReturn(falseNode);

			converter.convert(branchNode);

			verify(icg).processNode(condition);
			verify(converter.icg).processNode(falseNode);
		} catch (IntermediateCodeGeneratorException e) {
			fail();
		}
	}
}