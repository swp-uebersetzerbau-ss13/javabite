package swp_compiler_ss13.javabite.codegen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BreakNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.ternary.BranchNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.ir.IntermediateCodeGenerator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;

/**
 * 
 * @author AMS
 *
 */
public class JavaBiteCodeGenerator implements IntermediateCodeGenerator {
	
	List<Quadruple> quadruples;
	
	JavaBiteCodeGenerator() {
		quadruples = new ArrayList<Quadruple>();
	}
	
	@Override
	public List<Quadruple> generateIntermediateCode(AST ast) throws IntermediateCodeGeneratorException {
		
		//TODO: iterate over AST and add quadruples to quadruple list
		BlockNode program = (BlockNode) ast;
		quadruples.add(BlockNodeCG.convert(program));
		
		return quadruples;
	}
	
	
	
	
	/**
	 * Differentiate the next node.
	 * @param node
	 * @throws IntermediateCodeGeneratorException
	 */
	private void differentiateNode (ASTNode node) throws IntermediateCodeGeneratorException {
		switch (node.getNodeType()){
		case ArithmeticBinaryExpressionNode:
			quadruples.add(ArithmeticBinaryExpressionNodeCG.convert(node));
			break;
		case ArithmeticUnaryExpressionNode:
			quadruples.add(ArithmeticUnaryExpressionNodeCG.convert(node));
			break;
		case ArrayIdentifierNode:
			quadruples.add(ArrayIdentifierNodeCG.convert(node));
			break;
		case AssignmentNode:
			quadruples.add(AssignmentNodeCG.convert(node));
			break;
		case BasicIdentifierNode:
			quadruples.add(BasicIdentifierNodeCG.convert(node));
			break;
		case BlockNode:
			quadruples.add(BlockNodeCG.convert(node));
			break;
		case BranchNode:
			quadruples.add(BranchNodeCG.convert(node));
			break;
		case BreakNode:
			quadruples.add(BreakNodeCG.convert(node));
			quadruples.add(BreakNodeCG.convert(node));
			break;
		case DeclarationNode:
			quadruples.add(DeclarationNodeNodeCG.convert(node));
			break;
		case DoWhileNode:
			quadruples.add(DoWhileNodeCG.convert(node));
			break;
		case LiteralNode:
			quadruples.add(LiteralNodeCG.convert(node));
			break;
		case LogicBinaryExpressionNode:
			quadruples.add(LogicBinaryNodeCG.convert(node));
			break;
		case LogicUnaryExpressionNode:
			quadruples.add(LogicUnaryEcpressionNodeCG.convert(node));
			break;
		case PrintNode:
			quadruples.add(PrintNodeCG.convert(node));
			break;
		case RelationExpressionNode:
			quadruples.add(RelationExpressionNodeCG.convert(node));
			break;
		case ReturnNode:
			quadruples.add(ReturnNodeNodeCG.convert(node));
			break;
		case StructIdentifierNode:
			quadruples.add(StructIdentifierNodeCG.convert(node));
			break;
		case WhileNode:
			quadruples.add(WhileNodeCG.convert(node));
			break;
		default:
			break;
		}
	}
	
	
	
}
