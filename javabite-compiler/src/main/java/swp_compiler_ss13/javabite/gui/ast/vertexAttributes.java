package swp_compiler_ss13.javabite.gui.ast;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ast.nodes.binary.LogicBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.LoopNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.ternary.BranchNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArithmeticUnaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArrayIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ast.nodes.unary.ReturnNode;
import swp_compiler_ss13.common.ast.nodes.unary.StructIdentifierNode;

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
	
	public void addAttributes(ASTNode ast) {
		if (ast instanceof BasicIdentifierNode) {
			value = "Id= " + ((BasicIdentifierNode) ast).getIdentifier();
			color = colorAttributes+"0000ff";
		} else if (ast instanceof ArithmeticBinaryExpressionNode) {
			opr =new OperationSymbol(ast);
			value = opr.getOperationSymbol();
			color = colorAttributes+"cyan";
		} else if (ast instanceof ArithmeticUnaryExpressionNode) {
			opr =new OperationSymbol(ast);
			value = opr.getOperationSymbol();
			color = colorAttributes+"blue";
		} else if (ast instanceof LiteralNode) {
			value = "Type= " + ((LiteralNode) ast).getLiteralType()
					+ "\nLiteral= " + ((LiteralNode) ast).getLiteral();
			color = colorAttributes+"yellow";
		} else if (ast instanceof AssignmentNode) {
			value = "Assignment";
			color = colorAttributes+"white";

		} else if (ast instanceof LogicBinaryExpressionNode) {
		    opr =new OperationSymbol(ast);
			value = opr.getOperationSymbol();
			color = colorAttributes+"blue";

		} else if (ast instanceof ReturnNode) {
			value = "Return";
			color = colorAttributes+"orange";
		} else if (ast instanceof DeclarationNode) {
			value = "Type= " + ((DeclarationNode) ast).getType() + "\nId= "
					+ ((DeclarationNode) ast).getIdentifier();
			color = colorAttributes+"magenta";
		} else if (ast instanceof BlockNode) {
			value = "Statements= " + ((BlockNode) ast).getNumberOfStatements()
					+ "\nDeclarations= "
					+ ((BlockNode) ast).getNumberOfDeclarations();
			color = colorAttributes+"pink";
		} else if (ast instanceof ArrayIdentifierNode) {
			value = "Index= " + ((ArrayIdentifierNode) ast).getIdentifierNode();
			color = colorAttributes+"black";
		} else if (ast instanceof StructIdentifierNode) {
			value = "Index= "
					+ ((StructIdentifierNode) ast).getIdentifierNode();
			color = colorAttributes+"red";
		} else if (ast instanceof LoopNode) {
			value = "Condition= " + ((LoopNode) ast).getCondition() + "\nBody"
					+ ((LoopNode) ast).getLoopBody();
			color = colorAttributes+"violet";
		} else if (ast instanceof ReturnNode) {
			value = "" + ((ReturnNode) ast).getRightValue();
			color = colorAttributes+"navy";
		} else if (ast instanceof BranchNode) {
			value = "Condition" + ((BranchNode) ast).getCondition();
			color = colorAttributes+"yellow";
		} 
		else {
			value = ast.toString();
			color = colorAttributes+"white";
		}

	}

}
