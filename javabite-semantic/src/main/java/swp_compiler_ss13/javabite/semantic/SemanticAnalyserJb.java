package swp_compiler_ss13.javabite.semantic;

import static swp_compiler_ss13.javabite.semantic.attributes.ArithmeticAttribute.BOOLEAN;
import static swp_compiler_ss13.javabite.semantic.attributes.ArithmeticAttribute.FLOAT;
import static swp_compiler_ss13.javabite.semantic.attributes.ArithmeticAttribute.INTEGER;
import static swp_compiler_ss13.javabite.semantic.attributes.ArithmeticAttribute.STRING;
import static swp_compiler_ss13.javabite.semantic.attributes.AttributingAttribute.INHERITED;
import static swp_compiler_ss13.javabite.semantic.attributes.AttributingAttribute.SYNTHESIZED;
import static swp_compiler_ss13.javabite.semantic.attributes.BreakValidnessAttribute.IN_LOOP;
import static swp_compiler_ss13.javabite.semantic.attributes.BreakValidnessAttribute.NOT_IN_LOOP;
import static swp_compiler_ss13.javabite.semantic.attributes.CodeFlowAttribute.FLOW_CONTINUE;
import static swp_compiler_ss13.javabite.semantic.attributes.CodeFlowAttribute.FLOW_INTERRUPT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ast.nodes.binary.DoWhileNode;
import swp_compiler_ss13.common.ast.nodes.binary.LogicBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.RelationExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.WhileNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BreakNode;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.ternary.BranchNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArithmeticUnaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArrayIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ast.nodes.unary.LogicUnaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.unary.PrintNode;
import swp_compiler_ss13.common.ast.nodes.unary.ReturnNode;
import swp_compiler_ss13.common.ast.nodes.unary.StructIdentifierNode;
import swp_compiler_ss13.common.parser.SymbolTable;
import swp_compiler_ss13.common.report.ReportLog;
import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.common.semanticAnalysis.SemanticAnalyser;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.common.types.Type.Kind;
import swp_compiler_ss13.common.types.derived.ArrayType;
import swp_compiler_ss13.common.types.derived.Member;
import swp_compiler_ss13.common.types.derived.StructType;
import swp_compiler_ss13.javabite.semantic.attributes.ArithmeticAttribute;
import swp_compiler_ss13.javabite.semantic.attributes.AttributingAttribute;
import swp_compiler_ss13.javabite.semantic.attributes.BreakValidnessAttribute;
import swp_compiler_ss13.javabite.semantic.attributes.CodeFlowAttribute;
import swp_compiler_ss13.javabite.semantic.attributes.ValueAttribute;

/**
 * This class is a preliminary solution to handle the semantic analysis. The
 * code will be documented in a few days. brief overview: - limited inherited
 * and synthesized attributed grammar - most used methods - get <- gets a
 * attribute from a node - set <- sets a attribute from a node - example for
 * synthesized attribute: - abstract typing ( algebra-class ) - example for
 * inherited attribute: - concrete typing ( e.g. the different levels in
 * multi-dimensional arrays)
 * 
 * @author Till
 * 
 */
public class SemanticAnalyserJb implements SemanticAnalyser {
	Logger internal_logger = LoggerFactory.getLogger(SemanticAnalyser.class);
	private ReportLog errorLog;

	/**
	 * this method has to be called, if a wrong type has been occured during the
	 * semantic check
	 * 
	 * @param n
	 *            the node related to the wrong type
	 */
	private void wrongType2(ASTNode n) {
		errorLog.reportError(ReportType.TYPE_MISMATCH, n.coverage(),
				"Wrong type!");
	}

	/**
	 * this method has the same semantic as the wrongtype(Node)-method, but
	 * offers a string, which will be reported.
	 * 
	 * @param n
	 * @param msg
	 */
	private void wrongType2(ASTNode n, String msg) {
		errorLog.reportError(ReportType.TYPE_MISMATCH, n.coverage(), msg);
	}

	/**
	 * logs an error ( type missmatch)
	 * 
	 * @param subject
	 *            the subject to the mismatch
	 * @param expected_type
	 *            the expected type
	 * @param present_type
	 *            the actual type
	 */
	private void wrongType(ASTNode subject, Object present_type,
			Object expected_type, Object... further_expected_types) {
		List<Object> al;
		if (further_expected_types != null)
			al = new ArrayList<Object>(Arrays.asList(further_expected_types));
		else
			al = new ArrayList<>();
		al.add(expected_type);
		errorLog.reportError(ReportType.TYPE_MISMATCH, subject.coverage(),
				"Present type \"" + present_type
						+ "\" is not compatible with exptected type(s) \"" + al
						+ "\"");
	}

	/**
	 * traverses the tree depth-first calls the eval-method for intermediate,
	 * inherited- and synthesized attributes in a suitable order
	 * 
	 * @param root
	 */
	private void dfVisit(ASTNode root) {
		evalIntermediateAttributes(root);
		for (ASTNode child : root.getChildren()) {
			evalInheritedAttributes(child);
			dfVisit(child);
		}
		evalSynthesizedAttributes(root);

	}

	private void evalSynthesizedAttributes(ASTNode child) {

		switch (child.getNodeType()) {
		case ArithmeticBinaryExpressionNode:
			evalSynthesizedAttributes((ArithmeticBinaryExpressionNode) child);
			break;
		case ArithmeticUnaryExpressionNode:
			evalSynthesizedAttributes((ArithmeticUnaryExpressionNode) child);
			break;
		case ArrayIdentifierNode:
			evalSynthesizedAttributes((ArrayIdentifierNode) child);
			break;
		case AssignmentNode:
			evalSynthesizedAttributes((AssignmentNode) child);
			break;
		case BasicIdentifierNode:
			evalSynthesizedAttributes((BasicIdentifierNode) child);
			break;
		case BlockNode:
			evalSynthesizedAttributes((BlockNode) child);
			break;
		case BranchNode:
			evalSynthesizedAttributes((BranchNode) child);
			break;
		case BreakNode:
			evalSynthesizedAttributes((BreakNode) child);
			break;
		case DeclarationNode:
			evalSynthesizedAttributes((DeclarationNode) child);
			break;
		case DoWhileNode:
			evalSynthesizedAttributes((DoWhileNode) child);
			break;
		case LiteralNode:
			evalSynthesizedAttributes((LiteralNode) child);
			break;
		case LogicBinaryExpressionNode:
			evalSynthesizedAttributes((LogicBinaryExpressionNode) child);
			break;
		case LogicUnaryExpressionNode:
			evalSynthesizedAttributes((LogicUnaryExpressionNode) child);
			break;
		case PrintNode:
			evalSynthesizedAttributes((PrintNode) child);
			break;
		case RelationExpressionNode:
			evalSynthesizedAttributes((RelationExpressionNode) child);
			break;
		case ReturnNode:
			evalSynthesizedAttributes((ReturnNode) child);
			break;
		case StructIdentifierNode:
			evalSynthesizedAttributes((StructIdentifierNode) child);
			break;
		case WhileNode:
			evalSynthesizedAttributes((WhileNode) child);
			break;
		default:
			errorLog.reportError(ReportType.UNDEFINED, child.coverage(),
					"undeclared nodetype used");
		}
	}

	private void evalIntermediateAttributes(ASTNode child) {
		if (child.getNodeType() != ASTNodeType.BlockNode)
			propagateSymbolTableDown(child);

		switch (child.getNodeType()) {
		case ArithmeticBinaryExpressionNode:
			evalIntermediateAttributes((ArithmeticBinaryExpressionNode) child);
			break;
		case ArithmeticUnaryExpressionNode:
			evalIntermediateAttributes((ArithmeticUnaryExpressionNode) child);
			break;
		case ArrayIdentifierNode:
			evalIntermediateAttributes((ArrayIdentifierNode) child);
			break;
		case AssignmentNode:
			evalIntermediateAttributes((AssignmentNode) child);
			break;
		case BasicIdentifierNode:
			evalIntermediateAttributes((BasicIdentifierNode) child);
			break;
		case BlockNode:
			evalIntermediateAttributes((BlockNode) child);
			break;
		case BranchNode:
			evalIntermediateAttributes((BranchNode) child);
			break;
		case BreakNode:
			evalIntermediateAttributes((BreakNode) child);
			break;
		case DeclarationNode:
			evalIntermediateAttributes((DeclarationNode) child);
			break;
		case DoWhileNode:
			evalIntermediateAttributes((DoWhileNode) child);
			break;
		case LiteralNode:
			evalIntermediateAttributes((LiteralNode) child);
			break;
		case LogicBinaryExpressionNode:
			evalIntermediateAttributes((LogicBinaryExpressionNode) child);
			break;
		case LogicUnaryExpressionNode:
			evalIntermediateAttributes((LogicUnaryExpressionNode) child);
			break;
		case PrintNode:
			evalIntermediateAttributes((PrintNode) child);
			break;
		case RelationExpressionNode:
			evalIntermediateAttributes((RelationExpressionNode) child);
			break;
		case ReturnNode:
			evalIntermediateAttributes((ReturnNode) child);
			break;
		case StructIdentifierNode:
			evalIntermediateAttributes((StructIdentifierNode) child);
			break;
		case WhileNode:
			evalIntermediateAttributes((WhileNode) child);
			break;
		default:
			errorLog.reportError(ReportType.UNDEFINED, child.coverage(),
					"undeclared nodetype used");
			break;
		}
	}

	private void evalInheritedAttributes(ASTNode child) {

		switch (child.getNodeType()) {
		case ArithmeticBinaryExpressionNode:
			evalInheritedAttributes((ArithmeticBinaryExpressionNode) child);
			break;
		case ArithmeticUnaryExpressionNode:
			evalInheritedAttributes((ArithmeticUnaryExpressionNode) child);
			break;
		case ArrayIdentifierNode:
			evalInheritedAttributes((ArrayIdentifierNode) child);
			break;
		case AssignmentNode:
			evalInheritedAttributes((AssignmentNode) child);
			break;
		case BasicIdentifierNode:
			evalInheritedAttributes((BasicIdentifierNode) child);
			break;
		case BlockNode:
			evalInheritedAttributes((BlockNode) child);
			break;
		case BranchNode:
			evalInheritedAttributes((BranchNode) child);
			break;
		case BreakNode:
			evalInheritedAttributes((BreakNode) child);
			break;
		case DeclarationNode:
			evalInheritedAttributes((DeclarationNode) child);
			break;
		case DoWhileNode:
			evalInheritedAttributes((DoWhileNode) child);
			break;
		case LiteralNode:
			evalInheritedAttributes((LiteralNode) child);
			break;
		case LogicBinaryExpressionNode:
			evalInheritedAttributes((LogicBinaryExpressionNode) child);
			break;
		case LogicUnaryExpressionNode:
			evalInheritedAttributes((LogicUnaryExpressionNode) child);
			break;
		case PrintNode:
			evalInheritedAttributes((PrintNode) child);
			break;
		case RelationExpressionNode:
			evalInheritedAttributes((RelationExpressionNode) child);
			break;
		case ReturnNode:
			evalInheritedAttributes((ReturnNode) child);
			break;
		case StructIdentifierNode:
			evalInheritedAttributes((StructIdentifierNode) child);
			break;
		case WhileNode:
			evalInheritedAttributes((WhileNode) child);
			break;
		default:
			errorLog.reportError(ReportType.UNDEFINED, child.coverage(),
					"undeclared nodetype used");
		}
	}

	private void evalIntermediateAttributes(ArithmeticBinaryExpressionNode n) {

	}

	private void evalSynthesizedAttributes(ArithmeticBinaryExpressionNode n) {
		if (applyForAtLeastOnce(n.getChildren(), STRING, SYNTHESIZED)) {
			set(n, STRING, SYNTHESIZED);
		} else if (countAttributes(n.getChildren(), FLOAT, SYNTHESIZED)
				+ countAttributes(n.getChildren(), INTEGER, SYNTHESIZED) == 2) {
			if (applyForAtLeastOnce(n.getChildren(), FLOAT, SYNTHESIZED)) {
				set(n, FLOAT, SYNTHESIZED);
			} else {
				set(n, INTEGER, SYNTHESIZED);
			}
		} else {
			if (!is(n.getLeftValue(), FLOAT, SYNTHESIZED)
					&& !is(n.getLeftValue(), INTEGER, SYNTHESIZED)) {
				wrongType(
						n.getLeftValue(),
						get(n.getLeftValue(), ArithmeticAttribute.class,
								SYNTHESIZED), INTEGER, FLOAT, STRING);
			}
			if (!is(n.getRightValue(), FLOAT, SYNTHESIZED)
					&& !is(n.getRightValue(), INTEGER, SYNTHESIZED)) {
				wrongType(
						n.getRightValue(),
						get(n.getRightValue(), ArithmeticAttribute.class,
								SYNTHESIZED), INTEGER, FLOAT, STRING);
			}
			set(n, INTEGER, SYNTHESIZED);
		}
		ValueAttribute val_right = get(n.getRightValue(), ValueAttribute.class,
				SYNTHESIZED, true);
		ValueAttribute val_left = get(n.getLeftValue(), ValueAttribute.class,
				SYNTHESIZED, true);

		if (val_left != null && val_right != null) {
			switch (n.getOperator()) {
			case ADDITION:
				set(n, val_left.add(val_right), SYNTHESIZED);
				errorLog.reportWarning(
						ReportType.UNDEFINED,
						n.coverage(),
						"expression can be simplified to "
								+ val_left.add(val_right).getNumber());
				break;
			case SUBSTRACTION:
				set(n, val_left.sub(val_right), SYNTHESIZED);
				errorLog.reportWarning(
						ReportType.UNDEFINED,
						n.coverage(),
						"expression can be simplified to "
								+ val_left.sub(val_right).getNumber());
				break;
			case MULTIPLICATION:
				set(n, val_left.mul(val_right), SYNTHESIZED);
				errorLog.reportWarning(
						ReportType.UNDEFINED,
						n.coverage(),
						"expression can be simplified to "
								+ val_left.mul(val_right).getNumber());
				break;
			case DIVISION:
				if (val_right.isZero()) {
					errorLog.reportError(ReportType.DIVISION_BY_ZERO, n
							.getRightValue().coverage(),
							"this value may not be zero");
				} else {
					set(n, val_left.div(val_right), SYNTHESIZED);
					errorLog.reportWarning(
							ReportType.UNDEFINED,
							n.coverage(),
							"expression can be simplified to "
									+ val_left.div(val_right).getNumber());
				}
				break;
			}

		}

	}

	private void evalInheritedAttributes(ArithmeticBinaryExpressionNode n) {

	}

	private void evalIntermediateAttributes(ArithmeticUnaryExpressionNode n) {

	}

	private void evalSynthesizedAttributes(ArithmeticUnaryExpressionNode n) {
		if (is(n.getRightValue(), FLOAT, SYNTHESIZED)) {
			set(n, FLOAT, SYNTHESIZED);
			ValueAttribute val = get(n.getRightValue(), ValueAttribute.class,
					SYNTHESIZED, true);
			if (val != null) {
				set(n, val.mul(new ValueAttribute(-1L)), SYNTHESIZED);
				errorLog.reportWarning(ReportType.UNDEFINED, n.coverage(),
						"expression can be simplified to " + val.getNumber());
			}
		} else if (is(n.getRightValue(), INTEGER, SYNTHESIZED)) {
			set(n, INTEGER, SYNTHESIZED);
		} else {
			wrongType(
					n.getRightValue(),
					get(n.getRightValue(), ArithmeticAttribute.class,
							SYNTHESIZED), INTEGER, FLOAT);
		}
		set(n, INTEGER, SYNTHESIZED);
	}

	private void evalInheritedAttributes(ArithmeticUnaryExpressionNode n) {

	}

	private void evalIntermediateAttributes(ArrayIdentifierNode n) {

	}

	private void evalSynthesizedAttributes(ArrayIdentifierNode n) {
		if (is(n.getIndexNode(), INTEGER, SYNTHESIZED)
				|| is(n.getIndexNode(), INTEGER, SYNTHESIZED)) {
			Type type = get(n.getIdentifierNode(), Type.class, SYNTHESIZED);
			if (type.getKind() != Kind.ARRAY) {
				wrongType(n.getIdentifierNode(),
						get(n.getIdentifierNode(), Type.class, SYNTHESIZED)
								.getKind(), Type.Kind.ARRAY);
				set(n, INTEGER, SYNTHESIZED);
			} else {
				ArrayType arrType = (ArrayType) type;
				set(n, arrType.getInnerType(), Type.class, SYNTHESIZED);
				setAccordingToType(n, arrType.getInnerType());
			}
		} else {
			wrongType(
					n.getIndexNode(),
					get(n.getIdentifierNode(), ArithmeticAttribute.class,
							SYNTHESIZED), INTEGER, FLOAT);
			set(n, INTEGER, SYNTHESIZED);
		}

	}

	private void evalInheritedAttributes(ArrayIdentifierNode n) {

	}

	private void evalIntermediateAttributes(AssignmentNode n) {
		set(n, FLOW_CONTINUE, SYNTHESIZED);
	}

	private void evalSynthesizedAttributes(AssignmentNode n) {
		ArithmeticAttribute a_expr = get(n.getRightValue(),
				ArithmeticAttribute.class, SYNTHESIZED);
		ArithmeticAttribute a_id = get(n.getLeftValue(),
				ArithmeticAttribute.class, SYNTHESIZED);
		if (a_expr.isNumeric() && a_id.isNumeric()) {
			set(n, a_id, SYNTHESIZED);
		} else if (a_expr == a_id) {
			set(n, a_id, SYNTHESIZED);
		} else if (a_id== STRING){
			set(n, STRING,SYNTHESIZED);
		}
		else {
			wrongType(
					n.getRightValue(),
					get(n.getRightValue(), ArithmeticAttribute.class,
							SYNTHESIZED),
					get(n.getLeftValue(), ArithmeticAttribute.class,
							SYNTHESIZED));
			set(n, a_id, SYNTHESIZED);
		}

	}

	private void evalInheritedAttributes(AssignmentNode n) {

	}

	private void evalIntermediateAttributes(BasicIdentifierNode n) {
		SymbolTable table = get(n, SymbolTable.class, INHERITED);
		if (!table.isDeclared(n.getIdentifier())) {
			errorLog.reportError(ReportType.UNDECLARED_VARIABLE_USAGE,
					n.coverage(), "variable \"" + n.getIdentifier()
							+ "\" has not been declared in current scope");
			set(n, INTEGER, SYNTHESIZED);
			return;
		}

		Type type = table.lookupType(n.getIdentifier());
		setAccordingToType(n, type);
	}

	private void evalSynthesizedAttributes(BasicIdentifierNode n) {

	}

	private void setAccordingToType(IdentifierNode idNode, Type type) {
		switch (type.getKind()) {
		case ARRAY:
			ArrayType at = (ArrayType) type;
			set(idNode, ArithmeticAttribute.ARRAY, SYNTHESIZED);
			set(idNode, at, Type.class, SYNTHESIZED);
			break;
		case STRUCT:
			StructType st = (StructType) type;
			set(idNode, ArithmeticAttribute.STRUCT, SYNTHESIZED);
			set(idNode, st, Type.class, SYNTHESIZED);
			break;
		case BOOLEAN:
			set(idNode, ArithmeticAttribute.BOOLEAN, SYNTHESIZED);
			set(idNode, type, Type.class, SYNTHESIZED);
			break;
		case DOUBLE:
			set(idNode, ArithmeticAttribute.FLOAT, SYNTHESIZED);
			set(idNode, type, Type.class, SYNTHESIZED);
			break;
		case LONG:
			set(idNode, ArithmeticAttribute.INTEGER, SYNTHESIZED);
			set(idNode, type, Type.class, SYNTHESIZED);
			break;
		case STRING:
			set(idNode, type, Type.class, SYNTHESIZED);
			set(idNode, ArithmeticAttribute.STRING, SYNTHESIZED);
			break;
		default:
			errorLog.reportError(ReportType.UNDEFINED, idNode.coverage(),
					"no valid kind of type");
			break;
		}
	}

	private void evalInheritedAttributes(BasicIdentifierNode n) {
		// nothing to do here
	}

	private void evalIntermediateAttributes(BlockNode n) {
		for (ASTNode child : n.getChildren()) {
			set(child, (SymbolTable) n.getSymbolTable(), SymbolTable.class,
					INHERITED);
		}

		// we have to treat different it if it is the root node
		if (n.getParentNode() == null) {
			set(n, NOT_IN_LOOP, INHERITED);
		}

		// all but the last statement is a valid return statement
		for (ASTNode child : n.getStatementList()) {
			copyAttributeFromTo(BreakValidnessAttribute.class, n, child,
					INHERITED);
		}

	}

	private void evalSynthesizedAttributes(BlockNode n) {
		// set the last to valid if the current block is a valid return
		if (!n.getStatementList().isEmpty()) {
			StatementNode last_statement = n.getStatementList().get(
					n.getStatementList().size() - 1);
			for (StatementNode statement : n.getStatementList()) {
				// statement is not last_statement -> CAN_CONTINUE
				if (statement != last_statement
						&& is(statement, FLOW_INTERRUPT, SYNTHESIZED)) {
					// not valid
					errorLog.reportError(ReportType.UNDEFINED,
							statement.coverage(), "Non-reachable code");
					set(n, FLOW_INTERRUPT, SYNTHESIZED);
					break;
				}
			}
			copyAttributeFromTo(CodeFlowAttribute.class, last_statement, n,
					SYNTHESIZED);
		} else {
			set(n, FLOW_CONTINUE, SYNTHESIZED);
		}
	}

	private void evalInheritedAttributes(BlockNode n) {

	}

	private void evalIntermediateAttributes(BranchNode n) {
		Collection<ASTNode> children = new LinkedList<>();
		children.add(n.getStatementNodeOnTrue());
		copyAttributeFromTo(BreakValidnessAttribute.class, n,
				n.getStatementNodeOnTrue(), INHERITED);
		if (n.getStatementNodeOnFalse() != null) {
			children.add(n.getStatementNodeOnFalse());
			copyAttributeFromTo(BreakValidnessAttribute.class, n,
					n.getStatementNodeOnFalse(), INHERITED);
		}

	}

	private void evalSynthesizedAttributes(BranchNode n) {
		if (!is(n.getCondition(), BOOLEAN, SYNTHESIZED)) {
			wrongType(
					n.getCondition(),
					get(n.getCondition(), ArithmeticAttribute.class,
							SYNTHESIZED), BOOLEAN);
		}

		Collection<ASTNode> children = new LinkedList<>();
		children.add(n.getStatementNodeOnTrue());
		if (n.getStatementNodeOnFalse() != null) {
			children.add(n.getStatementNodeOnFalse());
		}
		if (applyForAll(children, FLOW_INTERRUPT, SYNTHESIZED)) {
			set(n, FLOW_INTERRUPT, SYNTHESIZED);
		} else {
			set(n, FLOW_CONTINUE, SYNTHESIZED);
		}
	}

	private void evalInheritedAttributes(BranchNode n) {
		// TODO: Implement
	}

	private void evalIntermediateAttributes(BreakNode n) {

	}

	private void evalSynthesizedAttributes(BreakNode n) {
		set(n, FLOW_INTERRUPT, SYNTHESIZED);
	}

	private void evalInheritedAttributes(BreakNode n) {
		// TODO: Implement
	}

	private void evalIntermediateAttributes(DeclarationNode n) {
		SymbolTable t = get(n, SymbolTable.class, INHERITED);
		t.insert(n.getIdentifier(), n.getType());
	}

	private void evalSynthesizedAttributes(DeclarationNode n) {
		// TODO: Implement
	}

	private void evalInheritedAttributes(DeclarationNode n) {

	}

	private void evalIntermediateAttributes(DoWhileNode n) {
		set(n.getLoopBody(), IN_LOOP, INHERITED);
		set(n, FLOW_CONTINUE, SYNTHESIZED);
	}

	private void evalSynthesizedAttributes(DoWhileNode n) {
		if (!is(n.getCondition(), BOOLEAN, SYNTHESIZED)) {
			wrongType(
					n.getCondition(),
					get(n.getCondition(), ArithmeticAttribute.class,
							SYNTHESIZED), BOOLEAN);
		}
	}

	private void evalInheritedAttributes(DoWhileNode n) {
		// TODO: Implement
	}

	private void evalIntermediateAttributes(LiteralNode n) {
		Type t = n.getLiteralType();
		switch (t.getKind()) {
		case ARRAY:
			errorLog.reportError(ReportType.UNDEFINED, n.coverage(),
					"array type for literal not possible");
			break;
		case STRUCT:
			errorLog.reportError(ReportType.UNDEFINED, n.coverage(),
					"struct type for literal not possible");
			break;
		case BOOLEAN:
			set(n, BOOLEAN, SYNTHESIZED);
			break;
		case DOUBLE:
			set(n, FLOAT, SYNTHESIZED);
			set(n, new ValueAttribute(Double.parseDouble(n.getLiteral())),
					SYNTHESIZED);
			break;
		case LONG:
			set(n, INTEGER, SYNTHESIZED);
			set(n, new ValueAttribute(Long.parseLong(n.getLiteral())),
					SYNTHESIZED);
			break;
		case STRING:
			set(n, STRING, SYNTHESIZED);
			break;
		default:
			errorLog.reportError(ReportType.UNDEFINED, n.coverage(),
					"unknown kind");
			break;
		}
	}

	private void evalSynthesizedAttributes(LiteralNode n) {

	}

	private void evalInheritedAttributes(LiteralNode n) {
		// TODO: Implement
	}

	private void evalIntermediateAttributes(LogicBinaryExpressionNode n) {
		set(n, BOOLEAN, SYNTHESIZED);
	}

	private void evalSynthesizedAttributes(LogicBinaryExpressionNode n) {
		if (!applyForAll(n.getChildren(), BOOLEAN, SYNTHESIZED)) {
			if (!is(n.getLeftValue(), BOOLEAN, SYNTHESIZED)) {
				wrongType(
						n.getLeftValue(),
						get(n.getLeftValue(), ArithmeticAttribute.class,
								SYNTHESIZED), BOOLEAN);
			} else {
				wrongType(
						n.getRightValue(),
						get(n.getLeftValue(), ArithmeticAttribute.class,
								SYNTHESIZED), BOOLEAN);
			}
		}
	}

	private void evalInheritedAttributes(LogicBinaryExpressionNode n) {
		// TODO: Implement
	}

	private void evalIntermediateAttributes(LogicUnaryExpressionNode n) {
		set(n, BOOLEAN, SYNTHESIZED);
	}

	private void evalSynthesizedAttributes(LogicUnaryExpressionNode n) {
		if (!is(n.getRightValue(), BOOLEAN, SYNTHESIZED)) {
			wrongType(
					n.getRightValue(),
					get(n.getRightValue(), ArithmeticAttribute.class,
							SYNTHESIZED), BOOLEAN);
		}
	}

	private void evalInheritedAttributes(LogicUnaryExpressionNode n) {
		// TODO: Implement
	}

	private void evalIntermediateAttributes(PrintNode n) {
		set(n, FLOW_CONTINUE, SYNTHESIZED);
	}

	private void evalSynthesizedAttributes(PrintNode n) {
		// TODO: Implement
	}

	private void evalInheritedAttributes(PrintNode n) {
		// TODO: Implement
	}

	private void evalIntermediateAttributes(RelationExpressionNode n) {
		set(n, BOOLEAN, SYNTHESIZED);
	}

	private void evalSynthesizedAttributes(RelationExpressionNode n) {
		ArithmeticAttribute expr_left = get(n.getLeftValue(),
				ArithmeticAttribute.class, SYNTHESIZED);
		ArithmeticAttribute expr_right = get(n.getRightValue(),
				ArithmeticAttribute.class, SYNTHESIZED);
		if (!expr_left.isNumeric()) {
			wrongType(n.getLeftValue(), expr_left, INTEGER,FLOAT);
		} else if (!expr_right.isNumeric()) {
			wrongType(n.getRightValue(), expr_right, INTEGER,FLOAT);
		}
	}

	private void evalInheritedAttributes(ReturnNode n) {
		// TODO: Implement
	}

	private void evalIntermediateAttributes(ReturnNode n) {
		set(n, FLOW_INTERRUPT, SYNTHESIZED);
	}

	private void evalSynthesizedAttributes(ReturnNode n) {

		if (n.getRightValue() != null) {
			IdentifierNode returnVal = n.getRightValue();
			if (!is(returnVal, INTEGER, SYNTHESIZED)) {
				wrongType(n.getRightValue(), get(n.getRightValue(),ArithmeticAttribute.class,SYNTHESIZED),INTEGER);
			}
		}
	}

	private void evalInheritedAttributes(RelationExpressionNode n) {
		// TODO: Implement
	}

	private void evalIntermediateAttributes(StructIdentifierNode n) {

	}

	private void evalSynthesizedAttributes(StructIdentifierNode n) {

		Type type = get(n.getIdentifierNode(), Type.class, SYNTHESIZED);
		if (type.getKind() != Kind.STRUCT) {
			wrongType(n.getIdentifierNode(),
					get(n.getIdentifierNode(), Type.class, SYNTHESIZED)
							.getKind(), Type.Kind.STRUCT);
			set(n, INTEGER, SYNTHESIZED);
		} else {
			StructType strType = (StructType) type;
			String memberName = n.getFieldName();
			for (Member member : strType.members()) {
				if (memberName.equals(member.getName())) {
					// hit, found member
					setAccordingToType(n, member.getType());
				}
			}
			// set(n, strType.getInnerType(), SYNTHESIZED);
			// setAccordingToType(n, arrType.getInnerType());
		}

	}

	private void evalInheritedAttributes(StructIdentifierNode n) {
		// TODO: Implement
	}

	private void evalIntermediateAttributes(WhileNode n) {
		set(n.getLoopBody(), IN_LOOP, INHERITED);
		set(n, FLOW_CONTINUE, SYNTHESIZED);
	}

	private void evalSynthesizedAttributes(WhileNode n) {
		if (!is(n.getCondition(), BOOLEAN, SYNTHESIZED)) {
			wrongType(
					n.getCondition(),
					get(n.getCondition(), ArithmeticAttribute.class,
							SYNTHESIZED), BOOLEAN);
		}
	}

	private void evalInheritedAttributes(WhileNode n) {

	}

	@Override
	public void setReportLog(ReportLog log) {
		this.errorLog = log;
	}

	@Override
	public AST analyse(AST ast) {
		dfVisit(ast.getRootNode());
		return ast;
	}

	private <A> A get(ASTNode n, Class<A> at_class,
			AttributingAttribute fashion, boolean null_expectable) {
		String id = at_class.getSimpleName() + "|" + fashion;
		A res = ((A) n.getAttributeValue(id));
		if (res == null) {
			if (!null_expectable)
				internal_logger.error("res s null\nrequest for " + at_class
						+ "\nparams at node are "
						+ new HashMap<>(n.getAttributeValues())
						+ "\nclass of node: " + n.getClass().getSimpleName());
		}
		return res;
	}

	private <A> A get(ASTNode n, Class<A> at_class, AttributingAttribute fashion) {
		return get(n, at_class, fashion, false);
	}

	private <A> void set(ASTNode n, A attribute, AttributingAttribute fashion) {
		set(n, attribute, attribute.getClass(), fashion);
	}

	private <A> void set(ASTNode n, A attribute, Class clazz,
			AttributingAttribute fashion) {
		String id = clazz.getSimpleName() + "|" + fashion;
		n.setAttributeValue(id, attribute);
		internal_logger.info("" + n.getClass().getSimpleName() + "." + id
				+ " \t:=\t " + attribute);
	}

	private <A> boolean is(ASTNode n, A attribute, AttributingAttribute fashion) {

		return get(n, attribute.getClass(), fashion).equals(attribute);
	}

	private <A> boolean applyForAll(Collection<ASTNode> n, A attribute,
			AttributingAttribute fashion) {
		return countAttributes(n, attribute, fashion) == n.size();
	}

	private <A> boolean applyForAtLeastOnce(Collection<ASTNode> n, A attribute,
			AttributingAttribute fashion) {
		return countAttributes(n, attribute, fashion) > 0;
	}

	private <A> int countAttributes(Collection<ASTNode> n, A attribute,
			AttributingAttribute fashion) {
		int c = 0;
		for (ASTNode node : n)
			if (is(node, attribute, fashion))
				c++;
		return c;
	}

	private void propagateSymbolTableDown(ASTNode n) {
		for (ASTNode child : n.getChildren()) {
			set(child, get(n, SymbolTable.class, INHERITED), SymbolTable.class,
					INHERITED);
		}
	}

	private <A> void copyAttributeFromTo(Class<A> attribute_clazz,
			ASTNode from, ASTNode to, AttributingAttribute fashion) {
		set(to, get(from, attribute_clazz, fashion), fashion);
	}

	// ----------------------------------------------------------------------------------

	/*
	 * JUST FOR DIRTY TESTING public static void main(String[] args) throws
	 * FileNotFoundException { File f = new
	 * File("/Users/Till/Desktop/test.prog"); Lexer lex = new LexerJb();
	 * System.out.println("build"); lex.setSourceStream(new FileInputStream(f));
	 * ParserJb parser = new ParserJb(); parser.setLexer(lex); ReportLog
	 * reportLog = new ReportLog() {
	 * 
	 * @Override public void reportWarning(ReportType type, List<Token> tokens,
	 * String message) { System.err.println("warning");
	 * System.err.println(type); System.err.println(tokens);
	 * System.err.println(message);
	 * 
	 * }
	 * 
	 * @Override public void reportError(ReportType type, List<Token> tokens,
	 * String message) { System.err.println("error"); System.err.println(type);
	 * System.err.println(tokens); System.err.println(message);
	 * 
	 * } };
	 * 
	 * parser.setReportLog(reportLog); AST ast = parser.getParsedAST();
	 * SemanticAnalyser sa = new MonolithicSemanticAnalyzer();
	 * sa.setReportLog(reportLog); sa.analyse(ast); }
	 */
}
