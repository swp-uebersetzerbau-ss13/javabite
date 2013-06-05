package swp_compiler_ss13.javabite.semantic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ast.nodes.binary.DoWhileNode;
import swp_compiler_ss13.common.ast.nodes.binary.LogicBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.RelationExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.WhileNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.ternary.BranchNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArithmeticUnaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.unary.LogicUnaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.unary.ReturnNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.report.ReportLog;
import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.common.types.Type;


/**
 * This class is responsible to check the types.
 * It should recognize errors like a arithmetic
 * condition in a if node or division by string,
 * boolean or anything else, that's not a real.
 * @author Till
 *
 */
public class ASTTypeChecker {
	AST ast;
	BlockNode root;
	ReportLog reportLog;
	boolean use_coverage=true;
	
	public ASTTypeChecker(AST ast_, BlockNode root_, ReportLog reportLog) {
		this.ast=ast_;
		this.root=root_;
		this.reportLog=reportLog;
	}
	protected void disableCoverageUse(){
		use_coverage=false;
	}
	
	public void run() {
		Iterator<ASTNode> all_nodes=ast.getDFSLTRIterator();
		while (all_nodes.hasNext()) checkType(all_nodes.next());
	}

	// associates a Type with a Token
	private Map<ASTNode,AlgebraType> types=new HashMap<>();
	
	private void ensure(ASTNode node, AlgebraType type){
		if (type(node)==type) return;
		reportLog.reportError(ReportType.TYPE_MISMATCH,use_coverage?node.coverage():new LinkedList<Token>(), "Unexpected type, expected "+type+", given "+type(node));
	}
	
	private void checkType(ASTNode node){
		
		switch (node.getNodeType()){
		case ArithmeticBinaryExpressionNode:
			ArithmeticBinaryExpressionNode arbino=(ArithmeticBinaryExpressionNode)node;
			ensure(arbino.getLeftValue(),AlgebraType.ARITHMETIC);
			ensure(arbino.getRightValue(),AlgebraType.ARITHMETIC);
			break;
		case ArithmeticUnaryExpressionNode:
			ArithmeticUnaryExpressionNode arunno=(ArithmeticUnaryExpressionNode)node;
			ensure(arunno.getRightValue(),AlgebraType.ARITHMETIC);
			break;
		case ArrayIdentifierNode:
			break;
		case AssignmentNode:
			AssignmentNode asno=(AssignmentNode)node;
			//TODO correct just if there are no arrays and structs involved
			ensure(asno,typeOfId((BasicIdentifierNode)asno.getLeftValue()));
			break;
		case BasicIdentifierNode:
			break;
		case BlockNode:
			break;
		case BranchNode:
			BranchNode brno=(BranchNode)node;
			ensure(brno.getCondition(),AlgebraType.BOOLEAN);
			break;
		case BreakNode:
			break;
		case DeclarationNode:
			break;
		case DoWhileNode:
			DoWhileNode dwno=(DoWhileNode)node;
			ensure(dwno.getCondition(),AlgebraType.BOOLEAN);
			break;
		case LiteralNode:
			break;
		case LogicBinaryExpressionNode:
			LogicBinaryExpressionNode lbno=(LogicBinaryExpressionNode)node;
			ensure(lbno.getLeftValue(), AlgebraType.BOOLEAN);
			ensure(lbno.getRightValue(),AlgebraType.BOOLEAN);
			break;
		case LogicUnaryExpressionNode:
			LogicUnaryExpressionNode luno=(LogicUnaryExpressionNode)node;
			ensure(luno.getRightValue(),AlgebraType.BOOLEAN);
			break;
		case PrintNode:
			break;
		case RelationExpressionNode:
			RelationExpressionNode ren=(RelationExpressionNode) node;
			ensure(ren.getLeftValue(),AlgebraType.ARITHMETIC);
			ensure(ren.getRightValue(),AlgebraType.ARITHMETIC);
		case ReturnNode:
			ReturnNode rn=(ReturnNode)node;
			if (rn.getRightValue()!=null) ensure(rn.getRightValue(),AlgebraType.ARITHMETIC);
		case StructIdentifierNode:
			break;
		case WhileNode:
			WhileNode wn=(WhileNode)node;
			ensure(wn.getCondition(), AlgebraType.ARITHMETIC);
		}
	}
	
	private AlgebraType type(ASTNode node){
		if (!types.containsKey(node)){
			setType(node);
		}
		return types.get(node);
	}

	/**
	 * sets the type of the token to 
	 * @param token
	 */
	private void setType(ASTNode node) {
		AlgebraType type=null;
		
		// TODO: break
		switch (node.getNodeType()){
		case ArithmeticBinaryExpressionNode:
			type = AlgebraType.ARITHMETIC;
			break;
		case ArithmeticUnaryExpressionNode:
			type = AlgebraType.ARITHMETIC;
			break;
		case AssignmentNode:
			type = type(((AssignmentNode)node).getRightValue());
			break;
		case BasicIdentifierNode:
			type = typeOfId((BasicIdentifierNode)node);
			break;
		case ArrayIdentifierNode:
			type = AlgebraType.ARRAY;
			break;
		case BlockNode:
			type = AlgebraType.NO_TYPE;
			break;
		case BranchNode:
			type = AlgebraType.NO_TYPE;
			break;
		case BreakNode:
			type = AlgebraType.NO_TYPE;
			break;
		case DeclarationNode:
			type = AlgebraType.NO_TYPE;
			break;
		case DoWhileNode:
			type = AlgebraType.NO_TYPE;
			break;
		case LiteralNode:
			type = map(((LiteralNode)node).getLiteralType().getKind());
			break;
		case LogicBinaryExpressionNode:
			type = AlgebraType.BOOLEAN;
			break;
		case LogicUnaryExpressionNode:
			type = AlgebraType.BOOLEAN;
			break;
		case PrintNode:
			type = AlgebraType.NO_TYPE;
			break;
		case RelationExpressionNode:
			type = AlgebraType.BOOLEAN;
			break;
		case ReturnNode:
			type = AlgebraType.NO_TYPE;
			break;
		case StructIdentifierNode:
			type = AlgebraType.STRUCT;
			break;
		case WhileNode:
			type = AlgebraType.NO_TYPE;
			break;
		default:
			reportLog.reportError(ReportType.UNDEFINED, use_coverage?node.coverage():new LinkedList<Token>(), "invalid AST has been produced");
		}
		
		System.out.println("type of "+node+" = "+type);
		types.put(node, type);
	}
	
	
	
	private AlgebraType typeOfId(BasicIdentifierNode node){
		ASTNode block_candidate=node;
		while (block_candidate.getNodeType()!=ASTNodeType.BlockNode){
			block_candidate=block_candidate.getParentNode();
		}
		Type t= ((BlockNode)block_candidate).getSymbolTable().lookupType(node.getIdentifier());
		return map(t.getKind());
	}
	
	private AlgebraType map(Type.Kind kind){
		switch (kind){
		case DOUBLE:
			return AlgebraType.ARITHMETIC;
		case BOOLEAN:
			return AlgebraType.BOOLEAN;
		case LONG:
			return AlgebraType.ARITHMETIC;
		case ARRAY:
			return AlgebraType.ARRAY;
		case STRING:
			return AlgebraType.STRING;
		case STRUCT:
			return AlgebraType.STRUCT;
		default:
			// may never happen
			reportLog.reportError(ReportType.UNDEFINED,null, "Invalid AST");
			return null;
		}
	}
	
}
