package swp_compiler_ss13.javabite.gui.ast;

import java.util.ArrayDeque;
import java.util.Queue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.binary.ArithmeticBinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.binary.BinaryExpressionNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.ast.nodes.leaf.LiteralNode;
import swp_compiler_ss13.common.visualization.ASTVisualization;
import swp_compiler_ss13.javabite.ast.ASTSource;

import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

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
		if (ast instanceof BasicIdentifierNode){
			returnVal= graph.insertVertex(graph.getDefaultParent(), null, "Id= "+ ((BasicIdentifierNode) ast).getIdentifier(), 20, 40, 200, 70);
			}
		else if (ast instanceof ArithmeticBinaryExpressionNode){
			while(!(((ArithmeticBinaryExpressionNode) ast).getOperator()).toString().equals(operation[i])){
				i++;
			}
			returnVal= graph.insertVertex(graph.getDefaultParent(), null, 
					operationS[i], 20, 40, 200, 70);
		}
		else if (ast instanceof LiteralNode){
			returnVal= graph.insertVertex(graph.getDefaultParent(), null, "Type= "+ ((LiteralNode) ast).getLiteralType() + "\nLiteral= "+((LiteralNode) ast).getLiteral()  , 20, 40, 200, 70);
			
		}
		else{
			returnVal=graph.insertVertex(graph.getDefaultParent(), null, ast, 20, 40, 200, 70);
		}
		
		return returnVal;
	}

	
	public static void main(String[] args){
		AST ast=ASTSource.getSecondAST();
		ASTVisualizerJb visualizer=new ASTVisualizerJb();
		visualizer.visualizeAST(ast);
		JFrame frame=new JFrame();
		JScrollPane ast_frame=visualizer.frame;
		frame.setVisible(true);
		frame.setSize(800, 600);
		frame.add(ast_frame);
		frame.setVisible(true);
	}
}
