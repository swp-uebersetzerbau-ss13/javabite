package swp_compiler_ss13.javabite.semantic.old;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode.BinaryOperator;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BreakNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ast.nodes.unary.ReturnNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.parser.SymbolTable;
import swp_compiler_ss13.common.report.ReportLog;
import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.common.semanticAnalysis.SemanticAnalyser;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.javabite.ast.nodes.leaf.LiteralNodeJb;

public class SemanticAnalyserJb implements SemanticAnalyser {
	Logger log = LoggerFactory.getLogger(SemanticAnalyserJb.class);
	private AST ast;
	
	private ReportLog reportLog;

	@Override
	public void setReportLog(ReportLog log) {
		reportLog = log;
	}

	@Override
	public AST analyse(AST ast) {
		return (analyse(ast,true));
	}
	
	public AST analyse(AST ast, boolean use_coverage) {
		this.ast=ast;
		checkDoubleDeclaration();
		checkDivisionByZero();
		checkNonDeclaredVariableUsedQ();
		checkBreakDeclaration();
		checkReturnDeclaration();
		ASTTypeChecker checker=new ASTTypeChecker(ast, ast.getRootNode(),reportLog);
		if (!use_coverage) checker.disableCoverageUse();
		checker.run();
		return this.ast;
	}
	
	
	
	void setAst(AST ast) {
		this.ast = ast;
	}
	
	void checkNonDeclaredVariableUsedQ(){
		// Idea: just traverse and check symboltables
		BlockNode blockNode = ast.getRootNode(); 
		checkNonDeclaredVariableUsedInBlockNode(blockNode);
	}
	
	void checkNonDeclaredVariableUsedInBlockNode(BlockNode blockNode) {
		SymbolTable table = blockNode.getSymbolTable();
		for (StatementNode stmt:blockNode.getStatementList()) {
			if (stmt.getNodeType() == ASTNodeType.BlockNode) {
				checkNonDeclaredVariableUsedInBlockNode((BlockNode)stmt);
			} else {
				Iterator<ASTNode> it = stmt.getDFSLTRNodeIterator();
				while (it.hasNext()){
					ASTNode node=it.next();
					// TODO: this check only works for BasicIdentifierNode not for arrays and structs
					if (node.getNodeType()==ASTNodeType.BasicIdentifierNode){
						BasicIdentifierNode bin=(BasicIdentifierNode) node;
						String identifier = bin.getIdentifier();
						if (!table.isDeclared(identifier))
							reportLog.reportError(ReportType.UNDECLARED_VARIABLE_USAGE, node.coverage(),"Identifier '" + identifier + "' used but never declared");
					}
				}
			}
		}
	}
		
	void checkBreakDeclaration(){
		//get all break statements
		Set<ASTNode> breakNodes=getAllOfType(ASTNodeType.BreakNode);
		// check condition for every breakNode
		for (ASTNode rawNode : breakNodes){
			// cast is valid by token-convection
			BreakNode breakNode=(BreakNode)rawNode;
			ASTNode breakNodeParent=breakNode;
			do{
				breakNodeParent=breakNodeParent.getParentNode();
			}while (breakNodeParent!=null && breakNodeParent.getNodeType()!=ASTNodeType.WhileNode && breakNodeParent.getNodeType()!=ASTNodeType.DoWhileNode);
			// breakNodeParent is null, a whileNode or a doWhileNode
			// invalid position, if it's null
			if (breakNodeParent==null){
				reportLog.reportError(ReportType.UNDEFINED, breakNode.coverage(), "BreakNode is not in valid position");
			}
		}
	}
	
	void checkReturnDeclaration(){
		//get all return statements
		Set<ASTNode> returnNodes=getAllOfType(ASTNodeType.ReturnNode);
		// check condition for every breakNode
		for (ASTNode rawNode : returnNodes){
			// cast is valid by token-convection
            ASTNode returnNodeParent= (ReturnNode)rawNode;
			boolean conditional=false;
			// TODO: implement:)
		}
	}

	
	
	void checkDoubleDeclaration() {
		Set<String> varSet = new HashSet<>();
		Iterator<ASTNode> it = ast.getDFSLTRIterator();
		// iterate through all nodes depth first
		while (it.hasNext()){
			ASTNode node=it.next();
			if (node.getNodeType()==ASTNodeType.BlockNode){
				BlockNode block = (BlockNode) node;
				varSet.clear();
				for(DeclarationNode decl:block.getDeclarationList()) {
					String identifier = decl.getIdentifier();
					if (varSet.contains(identifier))
						reportLog.reportError(ReportType.DOUBLE_DECLARATION, decl.coverage(), "Identifier '" + identifier + "' was declared multiple times");
					else
						varSet.add(identifier);
				}
			}
		}
	}
	
	
	void checkDivisionByZero() {
		//TODO: change structure so that error-reporting contains the tokens.
		if (containsDivisionByZeroQ())
			reportLog.reportError(ReportType.DIVISION_BY_ZERO, new ArrayList<Token>(), "Somewhere inside the input is a division by zero.");
	}
	
	/**
	 * check if division by zero exists in this ast
	 * @return 	true  => div by zero exists sure as death
	 * 			false => div by zero can exist in more complex cases
	 */
	boolean containsDivisionByZeroQ(){
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
	 * checks if a LiteralNodeJb may be zero in this context
	 * @param candidate a node, which is sure zero
	 * @return if it's okay to be zero in the situation
	 */
	boolean isZeroRestricted(LiteralNodeJb candidate) {
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
	boolean isZero(LiteralNodeJb candidate) {
		return ((candidate.getLiteralType().getKind()==Type.Kind.DOUBLE
				|| candidate.getLiteralType().getKind()==Type.Kind.LONG)
				&&
				Double.parseDouble(candidate.getLiteral())==0
				);
	}
	
	/**
	 * filters all the nodes to the given criteria
	 * @param t the type of the nodes you want to receive
	 * @return all nodes of the ast with the given ASTNodeType
	 */
	private Set<ASTNode> getAllOfType(ASTNode.ASTNodeType t){
		Set<ASTNode> res=new HashSet<>();
		Iterator<ASTNode> nodes =ast.getDFSLTRIterator();
		while (nodes.hasNext()){
			ASTNode candidate=nodes.next();
			if (candidate.getNodeType()==t) res.add(candidate);
		}
		return res;
	}
	
}