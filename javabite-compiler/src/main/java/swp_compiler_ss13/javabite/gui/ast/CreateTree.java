package swp_compiler_ss13.javabite.gui.ast;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.mxgraph.view.mxGraph;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.ASTNode;

public class CreateTree {
	
	mxGraph graph;
	Map<Object, String> tooltips = new HashMap<Object, String>();
	Queue<Object> toVisit_celledCopy;
	List<String> strList= new ArrayList<String>();
	List<Integer> intArray= new ArrayList<Integer>();
	CreateTree(mxGraph graph){
		this.graph=graph;	
	}
	
	/**
	 * converts the ast to a tree representation of JGraphX, represented in the
	 * frame.
	 * 
	 * @param ast
	 */
	
	void initTree(AST ast) {
		// necessary for the model, not important for us
		Object parent = graph.getDefaultParent();
		CreateCell createCell = new CreateCell(graph);

		// double-valued BFS. We have to do so since we have two different data
		// structures
		// this queue holds the ASTNodes we need to visit
		Queue<ASTNode> toVisit_original = new ArrayDeque<>();

		// this queue holds the Objects we get from the graph, called cell
		Queue<Object> toVisit_celled = new ArrayDeque<>();

		// add the root node
		toVisit_original.add(ast.getRootNode());

		toVisit_celled.add(createCell.asCell(ast.getRootNode()));

		// do while there are nodes we want to visit
		while (!toVisit_original.isEmpty()) {

			// get the current Node
			ASTNode current_ast = toVisit_original.poll();
			Object current_cell = toVisit_celled.poll();

			// visit node
			// nothing to do here, functional part follows

			// add all children
			for (ASTNode child : current_ast.getChildren()) {
				// get the children, one as ASTNode, one as cell
				// System.out.println("type: "+child.getClass());
				Object child_as_cell = createCell.asCell(child);

				// add them to the queue to visit them
				toVisit_original.add(child);
				toVisit_celled.add(child_as_cell);

				// this is the line which do the necessary stuff.
				// inserts a edge to every children
				graph.insertEdge(parent, null, "", current_cell, child_as_cell);
				

			}

		}
		this.toVisit_celledCopy = toVisit_celled;
		this.tooltips=createCell.tooltips;
		intArray=createCell.intArray;
	}

}
