package swp_compiler_ss13.javabite.parser.targetgrammar.semantic_check;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.parser.SymbolTable;
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
	
	// for debugging
	Logger logger=LoggerFactory.getLogger(ASTAnalyzer.class);
	
	/**
	 * creates a ASTAnalyzer for the given ast
	 * @param _ast the ast, that must be analyzed
	 */
	public ASTAnalyzer(AST _ast) {
		ast=_ast;
	}
	
	/**
	 * checks if the simple case of a division by zero exists
	 * and every used variable is declared
	 * @return if no division by zero was detected and every used variable is declared
	 */
	public boolean isValid(){
		return !containsDivisionByZeroQ()&&!nonDeclaredVariableUsedQ();
	}
	
	/**
	 * check if division by zero exists in this ast
	 * @return 	true  => div by zero exists sure as death
	 * 			false => div by zero can exist in more complex cases
	 */
	public boolean containsDivisionByZeroQ(){
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
	public boolean nonDeclaredVariableUsedQ(){
		// Idea: just traverse and check
		// we are in the luckily situation at MS1
		// that we have just one scope
		SymbolTable table=ast.getRootSymbolTable();
		
		Iterator<ASTNode> it = ast.getDFSLTRIterator();
		while (it.hasNext()){
			ASTNode node=it.next();
			// TODO implement
			// hint:
			// 1. check if it's a Node identifying a variable
			// 2. check if it's declared in the symbol table
			// 3. return true if such a constellation exist
			throw new RuntimeException("method not implemented yet");
		}
		return false;
	}
	
	/**
	 * checks if a LiteralNodeJb may be zero in this context
	 * @param candidate a node, which is sure zero
	 * @return if it's okay to be zero in the situation
	 */
	boolean isZeroRestricted(LiteralNodeJb candidate) {
		// TODO implement
		// hints
		// 1. ASTNode.getParent() returns the parent node
		// 2. what type of node may the parent be?
		// 2.1. if it's that type, to what node class should we cast?
		// 2.2. how do we assure that the "bad" zero-node is the divsor?
		throw new RuntimeException("method not implemented yet");
	}

	/**
	 * checks the node for zero
	 * @param candidate the given literal node
	 * @return if the @candidate is zero
	 */
	boolean isZero(LiteralNodeJb candidate) {
		// TODO implement
		// hints:
		// 1. what type must the candidate has?
		// 2. how to identify zero ? remember: "0" != ".0" != "0.0" != ...
		throw new RuntimeException("method not implemented yet");
	}
	
}
