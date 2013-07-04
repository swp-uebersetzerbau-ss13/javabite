package swp_compiler_ss13.javabite.gui.ast;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;

public class OperationSymbol {
	int i=0;
	ASTNode ast;
	
	String[] operation = { "ADDITION", "SUBSTRACTION", "MULTIPLICATION",
			"DIVISION", "LESSTHAN", "LESSTHANEQUAL", "GREATERTHAN",
			"GREATERTHANEQUAL", "EQUAL", "INEQUAL", "LOGICAL_AND", "LOGICAL_OR" };
	String[] operationSymbole = { "+", "-", "*", "/", "<", "<=", ">", ">=", "=",
			"!=", "UND", "ODER" };
	
	OperationSymbol(ASTNode ast){
		 this.ast=ast;
	}
	
	public String getOperationSymbol(){
		while (!(((ArithmeticBinaryExpressionNode) ast).getOperator())
				.toString().equals(operation[i])) {
			i++;
		}
		return operationSymbole[i];
	}

}
