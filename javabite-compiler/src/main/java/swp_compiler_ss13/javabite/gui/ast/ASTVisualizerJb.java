package swp_compiler_ss13.javabite.gui.ast;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
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
import swp_compiler_ss13.common.visualization.ASTVisualization;
import swp_compiler_ss13.javabite.ast.ASTSource;
import swp_compiler_ss13.javabite.gui.ast.fitted.KhaledGraphFrame;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
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
	private Set<mxCell> visitedSet = new HashSet<mxCell>();
    List<mxCell> queueSubTree = new ArrayList<mxCell>();

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
		//ToolTipManager.sharedInstance().registerComponent(frame);
		frame.getGraphControl().addMouseListener(new MouseAdapter()
		{ 
			public void mouseClicked(MouseEvent e){
				Object cell = ((mxGraphComponent) frame).getCellAt(e.getX(), e.getY());
				if (cell != null)
				{
					breadthFirstSearch((mxCell) cell);
					Object[] edges = graph.getOutgoingEdges(cell); // remove edges
					graph.removeCells(edges);
					System.out.println(queueSubTree.size());
					
				for (mxCell k: queueSubTree){
						Object[] edges1 = graph.getOutgoingEdges((mxCell) k);
						graph.removeCells(edges1);
					}
				}
			}
			
		});	
	}
	
		
	
	private void breadthFirstSearch(mxCell parent) {

        // clear marker set
        visitedSet = new HashSet<mxCell>();
        // create a queue Q
        List<mxCell> queue = new ArrayList<mxCell>();
        // enqueue v onto Q
        queue.add(parent);
        // mark v
        visit(parent);
        // while Q is not empty:
        while (!queue.isEmpty()) {
        	// t <- Q.dequeue()
            mxCell cell = queue.get(0);
            queue.remove(cell);

            // if t is what we are looking for: 
            //   return t
            // TODO: add handling code if you search something
            System.out.println("BFS visiting: " + cell.getValue());
            // for all edges e in G.incidentEdges(t) do
            Object[] edges = graph.getOutgoingEdges(cell);

            for (Object edge : edges) {

                // o <- G.opposite(t,e)
                // get node from edge
                mxCell target = (mxCell) graph.getView().getVisibleTerminal(edge, false);

                // if o is not marked:
                if (!isVisited(target)) {

                    // mark o
                    visit(target);

                    // enqueue o onto Q
                    queue.add(target);
                    queueSubTree.add(target);
                   target.removeFromParent(); //here there is problem
               }
            }
        }

    }

    private void visit(mxCell what) {
        visitedSet.add(what);
    }

    private boolean isVisited(mxCell what) {
        return visitedSet.contains(what);
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

	void treeNodes() {

	}

	void test(AST ast) {
		Queue<ASTNode> queue = new ArrayDeque<>();
		int i = 0;
		for (ASTNode k : ast.getRootNode().getChildren()) {
			queue.add(k);
		}
		for (ASTNode k : queue) {
			if (k.getNodeType().equals(ASTNodeType.ReturnNode)) {
				System.out.println(i);
			} else
				i++;
		}
	}

	/*
	 * private Object asCell(ArithmeticBinaryExpressionNode node){ return
	 * graph.insertVertex(graph.getDefaultParent(), null, node.getOperator(),
	 * 20, 40, 00, 70); }
	 */

	/**
	 * creates a cell representation of the ast
	 * 
	 * @param ast
	 *            the astNode you want to convert
	 * @return the cell-object, which correspondents to the given node
	 */
	private Object asCell(ASTNode ast) {
		Object returnVal = null;
		OperationSymbol opr=null;
		int i = 0;
		String value = null;
		String color = null;
		if (ast instanceof BasicIdentifierNode) {
			value = "Id= " + ((BasicIdentifierNode) ast).getIdentifier();
			color = "0000ff";
		} else if (ast instanceof ArithmeticBinaryExpressionNode) {
			opr =new OperationSymbol(ast);
			value = opr.getOperationSymbol();
			color = "cyan";
		} else if (ast instanceof ArithmeticUnaryExpressionNode) {
			opr =new OperationSymbol(ast);
			value = opr.getOperationSymbol();
			color = "blue";
		} else if (ast instanceof LiteralNode) {
			value = "Type= " + ((LiteralNode) ast).getLiteralType()
					+ "\nLiteral= " + ((LiteralNode) ast).getLiteral();
			color = "yellow";
		} else if (ast instanceof AssignmentNode) {
			value = "Assignment";
			color = "white";

		} else if (ast instanceof LogicBinaryExpressionNode) {
		    opr =new OperationSymbol(ast);
			value = opr.getOperationSymbol();
			color = "blue";

		}

		else if (ast instanceof ReturnNode) {
			value = "Return";
			color = "orange";
		} else if (ast instanceof DeclarationNode) {
			value = "Type= " + ((DeclarationNode) ast).getType() + "\nId= "
					+ ((DeclarationNode) ast).getIdentifier();
			color = "magenta";
		} else if (ast instanceof BlockNode) {
			value = "Statements= " + ((BlockNode) ast).getNumberOfStatements()
					+ "\nDeclarations= "
					+ ((BlockNode) ast).getNumberOfDeclarations();
			color = "pink";
		} else if (ast instanceof ArrayIdentifierNode) {
			value = "Index= " + ((ArrayIdentifierNode) ast).getIdentifierNode();
			color = "black";
		} else if (ast instanceof StructIdentifierNode) {
			value = "Index= "
					+ ((StructIdentifierNode) ast).getIdentifierNode();
			color = "red";
		}

		else if (ast instanceof LoopNode) {
			value = "Condition= " + ((LoopNode) ast).getCondition() + "\nBody"
					+ ((LoopNode) ast).getLoopBody();
			color = "violet";
		} else if (ast instanceof ReturnNode) {
			value = "" + ((ReturnNode) ast).getRightValue();
			color = "navy";
		} else if (ast instanceof BranchNode) {
			value = "Condition" + ((BranchNode) ast).getCondition();
			color = "yellow";
		} 

		else {
			value = ast.toString();
			color = "white";
		}

		returnVal = graph
				.insertVertex(
						graph.getDefaultParent(),
						null,
						value,
						20,
						40,
						100,
						35,
						"ROUNDED;strokeWidth=2.0;strokeColor=white;shadow=false;autosize=0;foldable=0;editable=0;bendable=0;movable=0;resizable=0;cloneable=0;deletable=0;rounded=true;autosize=1;separatorColor=white;gradientColor=white;fillColor="
								+ color);
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
