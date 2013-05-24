package swp_compiler_ss13.javabite.gui.ast;

import java.util.ArrayDeque;
import java.util.Queue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArithmeticUnaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.unary.ReturnNode;
import swp_compiler_ss13.common.visualization.ASTVisualization;
import swp_compiler_ss13.javabite.ast.ASTSource;
import swp_compiler_ss13.javabite.ast.nodes.binary.LoopNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.marynary.BlockNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.ternary.BranchNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.ArrayIdentifierNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.DeclarationNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.ReturnNodeJb;
import swp_compiler_ss13.javabite.ast.nodes.unary.StructIdentifierNodeJb;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

;

public class ASTVisualizerJb implements ASTVisualization {
	mxGraph graph;
	JScrollPane frame;
	Queue<Object> toVisit_celledCopy;
	
	
	String[] operation={"ADDITION","SUBSTRACTION","MULTIPLICATION",
			"DIVISION","LESSTHAN","LESSTHANEQUAL","GREATERTHAN",
			"GREATERTHANEQUAL","EQUAL","INEQUAL","LOGICAL_AND","LOGICAL_OR"};
	String[] operationS={"+","-","*","/","<","<=",">",">=","=","=!","UND","ODER"};
	
	/**
	 * visualizes the ast
	 */
	@Override
	public void visualizeAST(AST ast) {
		graph = new mxGraph();
		initTree(ast);
		mxGraphLayout layout = new mxHierarchicalLayout(graph);
		layout.execute(graph.getDefaultParent());
		frame = new mxGraphComponent(graph);
		

	}
	
	public JScrollPane getFrame() {
		return frame;
	}
	
	/**
	 * converts the ast to a tree representation of JGraphX, represented in the frame.
	 * @param ast
	 */
	private void initTree(AST ast){
		// necessary for the model, not important for us
		Object parent=graph.getDefaultParent();
		
		// double-valued BFS. We have to do so since we have two different data structures
		// this queue holds the ASTNodes we need to visit
		Queue<ASTNode> toVisit_original=new ArrayDeque<>();
		// this queue holds the Objects we get from the graph, called cell
		Queue<Object> toVisit_celled=new ArrayDeque<>();
		
		// add the root node
		toVisit_original.add(ast.getRootNode());
		toVisit_celled.add(asCell(ast.getRootNode()));

		
		
		// do while there are nodes we want to visit
		while (!toVisit_original.isEmpty()){
			
			// get the current Node
			ASTNode current_ast=toVisit_original.poll();
			Object current_cell=toVisit_celled.poll();
			// visit node
			// nothing to do here, functional part follows
			
			// add all children 
			for (ASTNode child : current_ast.getChildren()){
				// get the children, one as ASTNode, one as cell
				//System.out.println("type: "+child.getClass());
				Object child_as_cell=asCell(child);
				
				// add them to the queue to visit them
				toVisit_original.add(child);
				toVisit_celled.add(child_as_cell);
				
				// this is the line which do the necessary stuff.
				// inserts a edge to every children
				graph.insertEdge(parent, null,"",current_cell,child_as_cell);
				
			}
		}
		this.toVisit_celledCopy=toVisit_celled;
	}
	
	void treeNodes(){

	}
	
	/*private Object asCell(ArithmeticBinaryExpressionNode node){
		return graph.insertVertex(graph.getDefaultParent(), null, node.getOperator(), 20, 40, 00, 70);
	}*/
	
	
	/**
	 * creates a cell representation of the ast
	 * @param ast the astNode you want to convert
	 * @return the cell-object, which correspondents to the given node
	 */
	private Object asCell(ASTNode ast){
		
		Object returnVal=null;
		int i=0;
		String value=null;
		String color=null;
		if (ast instanceof BasicIdentifierNode){
			value="Id= "+ ((BasicIdentifierNode) ast).getIdentifier();
			color="gray";
			}
		else if (ast instanceof ArithmeticBinaryExpressionNode){
			while(!(((ArithmeticBinaryExpressionNode) ast).getOperator()).toString().equals(operation[i])){
				i++;
			}
			value=operationS[i];
			color="cyan";
		}
		else if (ast instanceof ArithmeticUnaryExpressionNode){
			while(!(((ArithmeticUnaryExpressionNode) ast).getOperator()).toString().equals(operation[i])){
				i++;
			}
			value=operationS[i];
			color="blue";
		}
		else if (ast instanceof LiteralNode){
			value="Type= "+ ((LiteralNode) ast).getLiteralType() + "\nLiteral= "+((LiteralNode) ast).getLiteral();
			color="yellow";
		}
		else if (ast instanceof AssignmentNode){
			value="Assignment";
			color="white";
			
		}
		else if (ast instanceof ReturnNode ){
			value="Return";
			color="orange";
		}
		else if (ast instanceof DeclarationNodeJb){
			value= "Type= "+ ((DeclarationNodeJb) ast).getType() + "\nId= "+((DeclarationNodeJb) ast).getIdentifier();
		    color="magenta";
		}
		else if (ast instanceof BlockNodeJb){
			value="Statements= "+ ((BlockNodeJb) ast).getNumberOfStatements() + "\nDeclarations= "+((BlockNodeJb) ast).getNumberOfDeclarations();
		    color="pink";
		}
		else if (ast instanceof ArrayIdentifierNodeJb){
			value= "Index= "+ ((ArrayIdentifierNodeJb) ast).getIdentifierNode();
		    color="black";
		}
		else if (ast instanceof StructIdentifierNodeJb){
			value= "Index= "+ ((StructIdentifierNodeJb) ast).getIdentifierNode();
		    color="red";
		}
		else if (ast instanceof LoopNodeJb){
			value= "Condition= "+ ((LoopNodeJb) ast).getCondition() + "\nBody"+ ((LoopNodeJb) ast).getLoopBody();
		    color="violet";
		}
		else if (ast instanceof ReturnNodeJb){
			value= ""+ ((ReturnNodeJb) ast).getRightValue();
		    color="navy";
		}
		else if (ast instanceof BranchNodeJb){
			value= "Condition ="+ ((BranchNodeJb) ast).getCondition();
		    color="silver";
		}
		
		else{
			value=ast.toString();
			color="white";
		}

		returnVal=graph.insertVertex(graph.getDefaultParent(), null, value, 20, 40, 100, 35,"strokeColor=black;fillColor="+color);
		return returnVal;
		
		
	}

	
	public static void main(String[] args){
		AST ast=ASTSource.getSecondAST();
		ASTVisualizerJb visualizer=new ASTVisualizerJb();
		visualizer.visualizeAST(ast);
		JFrame frame=new JFrame();
		JScrollPane ast_frame=visualizer.frame;
		frame.setVisible(true);
		frame.setSize(900, 400);
		frame.add(ast_frame);
		frame.setVisible(true);
	}
}
