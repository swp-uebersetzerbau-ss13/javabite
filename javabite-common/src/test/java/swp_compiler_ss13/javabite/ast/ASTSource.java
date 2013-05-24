package swp_compiler_ss13.javabite.ast;

import java.util.Iterator;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode.BinaryOperator;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ast.nodes.unary.ReturnNode;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.javabite.ast.nodes.binary.ArithmeticBinaryExpressionNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.AssignmentNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.leaf.BasicIdentifierNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.leaf.LiteralNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.marynary.BlockNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.DeclarationNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.ReturnNodeJb;

public class ASTSource {
	/**
	 * creates a text AST for the source:
	 * long i
	 * i=2
	 * return i
	 * @return
	 */
	static public AST getFirstAST(){
		BasicIdentifierNode iidNode=new BasicIdentifierNodeJb();
		iidNode.setIdentifier("i");
		LiteralNode literalNode=new LiteralNodeJb();
		literalNode.setLiteral("2");
		literalNode.setLiteralType(new LongType());
		AssignmentNode assignNode=new AssignmentNodeJb();
		assignNode.setLeftValue(iidNode);
		assignNode.setRightValue(literalNode);
		DeclarationNode declNode=new DeclarationNodeJb();
		declNode.setIdentifier("i");
		declNode.setType(new LongType());
		BlockNode root= new BlockNodeJb();
		root.setSymbolTable(new SymbolTableJb());
		root.addDeclaration(declNode);
		root.addStatement(assignNode);
		ReturnNode retNode=new ReturnNodeJb();
		retNode.setRightValue(iidNode);
		root.addStatement(retNode);
		AST ast=new ASTJb();
		ast.setRootNode(root);
		return ast;
	}
	/**
	 * creates a text AST for the source:
	 * long i
	 * long j
	 * i=2
	 * j=2
	 * i=i*j
	 * return i
	 * @return
	 */
	static public AST getSecondAST(){
		BasicIdentifierNode iidNodeI=new BasicIdentifierNodeJb();
		BasicIdentifierNode iidNodeJ=new BasicIdentifierNodeJb();
		iidNodeI.setIdentifier("i");
		iidNodeJ.setIdentifier("j");
		
		LiteralNode literalNodeI=new LiteralNodeJb();
		literalNodeI.setLiteral("2");
		literalNodeI.setLiteralType(new LongType());
		LiteralNode literalNodeJ=new LiteralNodeJb();
		literalNodeJ.setLiteral("2");
		literalNodeJ.setLiteralType(new LongType());
		
		
		
		AssignmentNode assignNodeI=new AssignmentNodeJb();
		assignNodeI.setLeftValue(iidNodeI);
		assignNodeI.setRightValue(literalNodeI);
		AssignmentNode assignNodeJ=new AssignmentNodeJb();
		assignNodeJ.setLeftValue(iidNodeJ);
		assignNodeJ.setRightValue(literalNodeJ);
		
		
		DeclarationNode declNodeI=new DeclarationNodeJb();
		declNodeI.setIdentifier("i");
		declNodeI.setType(new LongType());
		
		DeclarationNode declNodeJ=new DeclarationNodeJb();
		declNodeJ.setIdentifier("j");
		declNodeJ.setType(new LongType());
		
		
		BlockNode root= new BlockNodeJb();
		root.setSymbolTable(new SymbolTableJb());
		root.addDeclaration(declNodeI);
		root.addDeclaration(declNodeJ);
		
		
		root.addStatement(assignNodeI);
		root.addStatement(assignNodeJ);
		
		AssignmentNode mul_node=new AssignmentNodeJb();
		mul_node.setLeftValue(iidNodeI);
		BinaryExpressionNode expressionNode=new ArithmeticBinaryExpressionNodeJb();
		expressionNode.setLeftValue(iidNodeI);
		expressionNode.setRightValue(iidNodeJ);
		expressionNode.setOperator(BinaryOperator.MULTIPLICATION);
		mul_node.setRightValue(expressionNode);
		root.addStatement(mul_node);
		
		ReturnNode retNode=new ReturnNodeJb();
		retNode.setRightValue(iidNodeI);
		root.addStatement(retNode);
		AST ast=new ASTJb();
		ast.setRootNode(root);
		return ast;
	}
	
	public static void main(String[] args){
		AST first=getFirstAST();
		AST second=getSecondAST();
		Iterator<ASTNode> firstIt=first.getDFSLTRIterator();
		while (firstIt.hasNext()) System.out.println(firstIt.next());

		Iterator<ASTNode> secondIt=second.getDFSLTRIterator();
		while (secondIt.hasNext()) System.out.println(secondIt.next());
	}
	
		
}
