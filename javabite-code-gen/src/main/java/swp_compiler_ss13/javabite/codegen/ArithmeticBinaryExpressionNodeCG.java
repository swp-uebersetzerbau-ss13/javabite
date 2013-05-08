package swp_compiler_ss13.javabite.codegen;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.backend.Quadruple;

public class ArithmeticBinaryExpressionNodeCG {

	private ArithmeticBinaryExpressionNode aben;
	
	public ArithmeticBinaryExpressionNodeCG(ASTNode node){
		ArithmeticBinaryExpressionNode astNode = (ArithmeticBinaryExpressionNode) node; 
		this.aben = astNode;
	}
	
	public static Quadruple convert(ASTNode node) {
		// TODO Auto-generated method stub
		return null;
	}

}
