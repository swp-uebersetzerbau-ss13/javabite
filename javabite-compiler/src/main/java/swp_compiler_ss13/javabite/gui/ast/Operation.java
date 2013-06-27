package swp_compiler_ss13.javabite.gui.ast;

import swp_compiler_ss13.common.ast.AST;

public class Operation {
	String[] operation = { "ADDITION", "SUBSTRACTION", "MULTIPLICATION",
			"DIVISION", "LESSTHAN", "LESSTHANEQUAL", "GREATERTHAN",
			"GREATERTHANEQUAL", "EQUAL", "INEQUAL", "LOGICAL_AND", "LOGICAL_OR" };
	String[] operationS = { "+", "-", "*", "/", "<", "<=", ">", ">=", "=",
			"=!", "UND", "ODER" };
	
	boolean compareTwoOperations(AST ast){
		return false;
	}

}
