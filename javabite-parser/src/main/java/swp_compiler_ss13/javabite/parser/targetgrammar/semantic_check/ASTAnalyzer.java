package swp_compiler_ss13.javabite.parser.targetgrammar.semantic_check;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode.BinaryOperator;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.parser.ReportLog;
import swp_compiler_ss13.common.parser.SymbolTable;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.javabite.ast.nodes.leaf.LiteralNodeJb;

/**
 * this class is responsible to assume some conditions
 * like no div by zero or no use of unitialized variables.
 * @author Till
 *
 */
public class ASTAnalyzer {
	
	// the subject of the investigation
	AST ast;
	ReportLog reportLog;
	
	// for debugging
	Logger logger=LoggerFactory.getLogger(ASTAnalyzer.class);
	
	/**
	 * creates a ASTAnalyzer
	 */
	public ASTAnalyzer(ReportLog _reportLog) {
		reportLog = _reportLog;
	}
	
	/**
	 * analyse the given ast
	 * @param _ast
	 */
	public void analyse(AST _ast) {
		ast=_ast;
		checkDoubleDeclaration();
		checkDivisionByZero();
		checkUninitializedIdentifierUsage();
	}
	
	private void checkUninitializedIdentifierUsage() {
		//TODO: implement analysis
	}

	private void checkDoubleDeclaration() {
		Set<String> varSet = new HashSet<>();
		Iterator<ASTNode> it = ast.getDFSLTRIterator();
		// iterate through all nodes. depth first, left to right 
		// order is not quite important in this case
		while (it.hasNext()){
			ASTNode node=it.next();
			if (node.getNodeType()==ASTNodeType.BlockNode){
				BlockNode block = (BlockNode) node;
				varSet.clear();
				for(DeclarationNode decl:block.getDeclarationList()) {
					String identifier = decl.getIdentifier();
					if (varSet.contains(identifier))
						reportLog.reportError(identifier, 0, 0, "Identifier '" + identifier + "' was declared multiple times");
					else
						varSet.add(identifier);
				}
			}
		}
	}
	
	private void checkDivisionByZero() {
		if (containsDivisionByZeroQ())
			reportLog.reportError("", 0, 0, "Somewhere inside the input is a division by zero.");
	}
	
	/**
	 * check if division by zero exists in this ast
	 * @return 	true  => div by zero exists sure as death
	 * 			false => div by zero can exist in more complex cases
	 */
	private boolean containsDivisionByZeroQ(){
		// Idea: a division by zero can happen, if a 
		// zero exists somewhere;)
		
		Iterator<ASTNode> it = ast.getDFSLTRIterator();
		// iterate through all nodes. depth first, left to right 
		// order is not quite important in this case
		while (it.hasNext()){
			ASTNode node=it.next();
			if (node.getNodeType()==ASTNodeType.LiteralNode){
				// cast is safe since it has to be that kind of node if
				// it has the given type
				LiteralNodeJb candidate=(LiteralNodeJb)node;
				if (isZero(candidate)){
					if (isZeroRestricted(candidate)) return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * checks if somewhere in a statement a undeclared
	 * variable exists
	 * @return if such a undeclared variable exists
	 */
	private boolean nonDeclaredVariableUsedQ(){
		// TODO: do it
		// Idea: just traverse and check
		// we are in the luckily situation at MS1
		// that we have just one scope
		SymbolTable table=ast.getRootSymbolTable();
		
		Iterator<ASTNode> it = ast.getDFSLTRIterator();
		while (it.hasNext()){
			ASTNode node=it.next();
			if (node.getNodeType()==ASTNodeType.BasicIdentifierNode){
				BasicIdentifierNode bin=(BasicIdentifierNode) node;
				if (!table.isDeclared(bin.getIdentifier()))
					return true;
			}
		}
		return false;
	}
	
	/**
	 * checks if a LiteralNodeJb may be zero in this context
	 * @param candidate a node, which is sure zero
	 * @return if it's okay to be zero in the situation
	 */
	private boolean isZeroRestricted(LiteralNodeJb candidate) {
		return (candidate.getParentNode().getNodeType()==ASTNodeType.ArithmeticBinaryExpressionNode&&
				((ArithmeticBinaryExpressionNode)candidate.getParentNode()).getOperator()==BinaryOperator.DIVISION
				&&((ArithmeticBinaryExpressionNode)candidate.getParentNode()).getRightValue()==candidate);
	}

	/**
	 * checks the node for zero
	 * @param candidate the given literal node
	 * @return if the @candidate is zero
	 * 
	 */
	private boolean isZero(LiteralNodeJb candidate) {
		return ((candidate.getLiteralType().getKind()==Type.Kind.DOUBLE
				|| candidate.getLiteralType().getKind()==Type.Kind.LONG)
				&&
				Double.parseDouble(candidate.getLiteral())==0
				);
	}
}
