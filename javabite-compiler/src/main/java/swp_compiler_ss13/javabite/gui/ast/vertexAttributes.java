package swp_compiler_ss13.javabite.gui.ast;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ast.nodes.binary.LogicBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.LoopNode;
import swp_compiler_ss13.common.ast.nodes.binary.RelationExpressionNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.ternary.BranchNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArithmeticUnaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArrayIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ast.nodes.unary.PrintNode;
import swp_compiler_ss13.common.ast.nodes.unary.ReturnNode;
import swp_compiler_ss13.common.ast.nodes.unary.StructIdentifierNode;
import swp_compiler_ss13.common.lexer.Token;

public class vertexAttributes {
	final String colorAttributes= "ROUNDED;strokeWidth=2.0;" +
			"strokeColor=white;shadow=false;" +
			"autosize=0;foldable=0;" +
			"editable=0;bendable=0;movable=0;resizable=0;" +
			"cloneable=0;deletable=0;rounded=true;autosize=1" +
			";separatorColor=white;gradientColor=white;fillColor=";
	Object returnVal = null;
	OperationSymbol opr=null;
	String value = null;
	String color = null;
	ASTNode ast=null;
	String strA= "";
	
	public void addAttributes(ASTNode ast) {
		if (ast instanceof BasicIdentifierNode) {
			value = "BasicIdentifierNode"+"\nId= " + ((BasicIdentifierNode) ast).getIdentifier();
			color = colorAttributes+"0000ff";
			getToken(ast);
		} else if (ast instanceof ArithmeticBinaryExpressionNode) {
			opr =new OperationSymbol(ast);
			value = "ArithmeticBinary\nExpressionNode\n"+opr.getOperationSymbol();
			color = colorAttributes+"cyan";
			getToken(ast);
		} else if (ast instanceof ArithmeticUnaryExpressionNode) {
			opr =new OperationSymbol(ast);
			value = "ArithmeticUnary\nExpressionNode\n"+opr.getOperationSymbol();
			color = colorAttributes+"blue";
			getToken(ast);
		}else if (ast instanceof PrintNode) {
			value ="PrintNode";
			color = colorAttributes+"0000ff";
			getToken(ast);
		} 
		
		else if (ast instanceof LiteralNode) {
			value = "LiteralNode"+"\nType= " + ((LiteralNode) ast).getLiteralType()
					+ "\nLiteral= " + ((LiteralNode) ast).getLiteral();
			color = colorAttributes+"yellow";
			getToken(ast);
		} else if (ast instanceof AssignmentNode) {
			value = "AssignmentNode";
			color = colorAttributes+"red";
			getToken(ast);

		} else if (ast instanceof LogicBinaryExpressionNode) {
			opr =new OperationSymbol(ast);
			value = "LogicBinary\nExpressionNode\n"+opr.getOperationSymbol();
			color = colorAttributes+"blue";
			getToken(ast);

		} else if (ast instanceof ReturnNode) {
			value = "ReturnNode";
			color = colorAttributes+"orange";
			getToken(ast);
		} else if (ast instanceof DeclarationNode) {
			value = "DeclarationNode\n" + ((DeclarationNode) ast).getIdentifier() +":" 
					+((DeclarationNode) ast).getType()+"\n";
			color = colorAttributes+"magenta";			
			getToken(ast);
		} else if (ast instanceof BlockNode) {
			value ="BlockNode"+"\nStatements= " + ((BlockNode) ast).getNumberOfStatements()
					+ "\nDeclarations= "
					+ ((BlockNode) ast).getNumberOfDeclarations();
			color = colorAttributes+"pink";
			getToken(ast);	
		} else if (ast instanceof ArrayIdentifierNode) {
			value ="ArrayIdentifierNode";
			color = colorAttributes+"black";
			getToken(ast);
		} else if (ast instanceof StructIdentifierNode) {
			value = "StructIdentifierNode";
			color = colorAttributes+"red";
			getToken(ast);
		} else if (ast instanceof LoopNode) {
			value = "LoopNode";
			color = colorAttributes+"violet";
			getToken(ast);
		} else if (ast instanceof ReturnNode) {
			value = "ReturnNode\n" + ((ReturnNode) ast).getRightValue();
			color = colorAttributes+"navy";
			getToken(ast);
		} else if (ast instanceof BranchNode) {
			value = "BranchNode";
			color = colorAttributes+"yellow";
			getToken(ast);
		} 
		else if (ast instanceof BranchNode) {
			value = "BranchNode";
			color = colorAttributes+"pink";
			getToken(ast);
		}
		else if (ast instanceof RelationExpressionNode) {
			opr =new OperationSymbol(ast);
			value = "Relation\nExpressionNode\n"+opr.getOperationSymbol();
			color = colorAttributes+"pink";
			getToken(ast);
		}
		
		else {
			value = ast.getNodeType().toString();
			color = colorAttributes+"white";
			getToken(ast);
		}

	}
	
	void getToken(ASTNode ast){
		
		for(Token t:ast.coverage()){
			strA=strA+" "+t.getValue();
		}
	}

}
