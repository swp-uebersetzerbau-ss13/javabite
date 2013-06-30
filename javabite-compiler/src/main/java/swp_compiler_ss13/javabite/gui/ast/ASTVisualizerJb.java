package swp_compiler_ss13.javabite.gui.ast;

import java.util.ArrayDeque;
import java.util.Hashtable;
import java.util.Queue;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.visualization.ASTVisualization;
import swp_compiler_ss13.javabite.ast.ASTSource;
import swp_compiler_ss13.javabite.gui.ast.fitted.KhaledGraphFrame;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

public class ASTVisualizerJb implements ASTVisualization {
	public mxGraph graph;
	mxGraphComponent frame;
	Queue<Object> toVisit_celledCopy;
	int x, y;
	int i=1;

	/**
	 * visualizes the ast
	 */
	@Override
	public void visualizeAST(AST ast) {
		graph = new mxGraph();
		graph.getStylesheet();
		mxStylesheet stylesheet = graph.getStylesheet();
		Hashtable<String, Object> style = new Hashtable<String, Object>();
		stylesheet.putCellStyle("ROUNDED", style);
		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		style.put(mxConstants.STYLE_OPACITY, 50);
		style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
		stylesheet.putCellStyle("BOLD", style);
		initTree(ast);
		KhaledGraphFrame k = new KhaledGraphFrame();
		this.x = 167 * k.levelsCounter(ast);
		this.y = 47 * k.maximumOfNodesInLevels();
		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
		layout.setOrientation(SwingConstants.WEST);
		layout.setInterRankCellSpacing(80);
		layout.setInterHierarchySpacing(20);
		layout.setIntraCellSpacing(5);
		layout.execute(graph.getDefaultParent());
		frame = new mxGraphComponent(graph);
		frame.setToolTips(true);
		HidingSubTree h = new HidingSubTree(graph,frame,ast);
		h.hiddenSubTree();
		//ToolTipManager.sharedInstance().registerComponent(frame);
	}
		

	public JScrollPane getFrame() {
		return frame;
	}

	/**
	 * converts the ast to a tree representation of JGraphX, represented in the
	 * frame.
	 * 
	 * @param ast
	 */

	private void initTree(AST ast) {
		// necessary for the model, not important for us
		Object parent = graph.getDefaultParent();

		// double-valued BFS. We have to do so since we have two different data
		// structures
		// this queue holds the ASTNodes we need to visit
		Queue<ASTNode> toVisit_original = new ArrayDeque<>();

		// this queue holds the Objects we get from the graph, called cell
		Queue<Object> toVisit_celled = new ArrayDeque<>();

		// add the root node
		toVisit_original.add(ast.getRootNode());

		toVisit_celled.add(asCell(ast.getRootNode()));

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
				Object child_as_cell = asCell(child);

				// add them to the queue to visit them
				toVisit_original.add(child);
				toVisit_celled.add(child_as_cell);

				// this is the line which do the necessary stuff.
				// inserts a edge to every children
				graph.insertEdge(parent, null, "", current_cell, child_as_cell);
				

			}

		}
		this.toVisit_celledCopy = toVisit_celled;
	}
	
	/**
	 * creates a cell representation of the ast
	 * 
	 * @param ast
	 *            the astNode you want to convert
	 * @return the cell-object, which correspondents to the given node
	 */
	private Object asCell(ASTNode ast) {
		Object returnVal;
		vertexAttributes ver = new vertexAttributes();
		ver.addAttributes(ast);
		String value=ver.value;
		String color=ver.color;
		returnVal = 
		graph.insertVertex(graph.getDefaultParent(),null,value,20,40,100,35,color);
        return returnVal;
	}

	public static void main(String[] args) {
		AST ast = ASTSource.getSecondAST();
		ASTVisualizerJb visualizer = new ASTVisualizerJb();
		visualizer.visualizeAST(ast);
		JFrame frame = new JFrame();
		JScrollPane ast_frame = visualizer.frame;
		frame.setVisible(true);
		frame.setSize(visualizer.x, visualizer.y);
		frame.add(ast_frame);
		frame.setVisible(true);

	}
}
