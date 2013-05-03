package swp_compiler_ss13.javabite.ast;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode.BinaryOperator;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ast.nodes.unary.ReturnNode;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.javabite.ast.ASTJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.AssignmentNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.binary.BinaryExpressionNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.leaf.BasicIdentifierNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.leaf.LiteralNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.marynary.BlockNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.DeclarationNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.ReturnNodeJb;
import swp_compiler_ss13.javabite.types.TypeJb;

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
		literalNode.setLiteralType(new TypeJb(Type.Kind.LONG));
		AssignmentNode assignNode=new AssignmentNodeJb();
		assignNode.setLeftValue(iidNode);
		assignNode.setRightValue(literalNode);
		DeclarationNode declNode=new DeclarationNodeJb();
		declNode.setIdentifier("i");
		declNode.setType(new TypeJb(Type.Kind.LONG));
		BlockNode root= new BlockNodeJb();
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
		literalNodeI.setLiteralType(new TypeJb(Type.Kind.LONG));
		LiteralNode literalNodeJ=new LiteralNodeJb();
		literalNodeJ.setLiteral("2");
		literalNodeJ.setLiteralType(new TypeJb(Type.Kind.LONG));
		
		
		
		AssignmentNode assignNodeI=new AssignmentNodeJb();
		assignNodeI.setLeftValue(iidNodeI);
		assignNodeI.setRightValue(literalNodeI);
		AssignmentNode assignNodeJ=new AssignmentNodeJb();
		assignNodeJ.setLeftValue(iidNodeJ);
		assignNodeJ.setRightValue(literalNodeJ);
		
		
		DeclarationNode declNodeI=new DeclarationNodeJb();
		declNodeI.setIdentifier("i");
		declNodeI.setType(new TypeJb(Type.Kind.LONG));
		
		DeclarationNode declNodeJ=new DeclarationNodeJb();
		declNodeJ.setIdentifier("j");
		declNodeJ.setType(new TypeJb(Type.Kind.LONG));
		
		
		BlockNode root= new BlockNodeJb();
		root.addDeclaration(declNodeI);
		root.addDeclaration(declNodeJ);
		
		
		root.addStatement(assignNodeI);
		root.addStatement(assignNodeJ);
		
		AssignmentNode mul_node=new AssignmentNodeJb();
		mul_node.setLeftValue(iidNodeI);
		BinaryExpressionNode expressionNode=new BinaryExpressionNodeJb();
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
	
	
}
