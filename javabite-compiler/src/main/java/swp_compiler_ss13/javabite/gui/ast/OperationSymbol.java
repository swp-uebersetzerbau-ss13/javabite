package swp_compiler_ss13.javabite.gui.ast;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.LogicBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.RelationExpressionNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArithmeticUnaryExpressionNode;

public class OperationSymbol {
	int i=0;
	ASTNode ast;
	
	String[] operation = { "ADDITION", "SUBSTRACTION", "MULTIPLICATION",
			"DIVISION", "LESSTHAN", "LESSTHANEQUAL", "GREATERTHAN",
			"GREATERTHANEQUAL", "EQUAL", "INEQUAL", "LOGICAL_AND", "LOGICAL_OR","MINUS" };
	String[] operationSymbole = { "+", "-", "*", "/", "<", "<=", ">", ">=", "=",
			"!=", "AND", "OR","MINUS" };
	
	OperationSymbol(ASTNode ast){
		 this.ast=ast;
	}
	
	public String getOperationSymbol(){
		if (ast instanceof ArithmeticBinaryExpressionNode){
			
			while (!(((ArithmeticBinaryExpressionNode) ast).getOperator())
					.toString().equals(operation[i])) {
				i++;
			}
			return operationSymbole[i];
		}
		else if (ast instanceof LogicBinaryExpressionNode ){
			while (!(((LogicBinaryExpressionNode) ast).getOperator())
					.toString().equals(operation[i])) {
				i++;
			}
			return operationSymbole[i];
		}
		else if(ast instanceof ArithmeticUnaryExpressionNode){
			while (!(((ArithmeticUnaryExpressionNode) ast).getOperator())
					.toString().equals(operation[i])) {
				i++;
			}
			return operationSymbole[i];
		}
		else if (ast instanceof RelationExpressionNode ){
			while (!(((RelationExpressionNode) ast).getOperator())
					.toString().equals(operation[i])) {
				i++;
			}
			return operationSymbole[i];
		}
		else return "";
		}
	
	}


