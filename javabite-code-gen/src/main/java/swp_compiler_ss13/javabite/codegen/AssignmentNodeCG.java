package swp_compiler_ss13.javabite.codegen;

import java.util.ArrayList;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.types.Type;

public class AssignmentNodeCG {

	private IdentifierNode left;
	private StatementNode right;
	private AssignmentNode node;
	
	public AssignmentNodeCG(ASTNode node){
		this.node = (AssignmentNode) node;
//		this.left = node.getLeftValue();
//		this.right = node.getRightValue();
	}
	
	
	public ArrayList<Quadruple> convert() {
		ArrayList<Quadruple> quadrList = new ArrayList<Quadruple>();
//		Type rightType = new Type();
//		rightType.getKind().LONG;
/*		switch(this.left.getTypeOfVariable()){
		case Long:
			QuadrupleImpl quadruple = new QuadrupleImpl(Operator.ASSIGN_LONG, Quadruple.EmptyArgument, Quadruple.EmptyArgument, );
		}
		*/
		return quadrList;
	}
	
	
	public static Quadruple convert(ASTNode node) {
		// TODO Auto-generated method stub
		return null;
	}

}
