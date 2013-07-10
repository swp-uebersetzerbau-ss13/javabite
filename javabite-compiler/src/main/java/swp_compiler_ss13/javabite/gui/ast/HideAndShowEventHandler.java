package swp_compiler_ss13.javabite.gui.ast;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingConstants;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraph.mxICellVisitor;

/**
 * This class provides user-triggered online graph manipulations.
 * 
 * @author Khaled, Mahmoud, Ahmet, Till
 *
 */
public class HideAndShowEventHandler extends MouseAdapter {

	// attributes set by outer usage
	mxGraph graph;
	mxGraphComponent frame;

	// nodes, that have been hidden by invoking the hiding on the root ( key)
	Map<mxCell, Set<mxCell>> rootToHiddenVertices = new HashMap<>();

	/**
	 * Adds the event handler to the graph in the frame.
	 * @param graph "-"
	 * @param frame "-"
	 */
	public HideAndShowEventHandler(mxGraph graph, mxGraphComponent frame) {
		this.graph = graph;
		this.frame = frame;
		frame.getGraphControl().addMouseListener(this);
		
	}

	/**
	 * reacts to the mouse-clicked event. 
	 * is NOOP, if no cell has been selected or it wasn't a double-click
	 */
	public void mouseClicked(MouseEvent e) {
		// get cell corresponding to the click
		final mxCell root =(mxCell) ((mxGraphComponent) frame).getCellAt(e.getX(), e.getY());
		
		// ignore everything but a double-click on a vertex
		if (e.getClickCount()!=2 || root==null)
			return;
		
		// restore or hide subtree. Choice depends on "backuped" subtreenodes
		if (rootToHiddenVertices.containsKey(root)){
			restoreSubTree(root);
		}
		else{
			hideSubTree(root);
		}
		
		// layout in each case, even if the graph components "jump around"
		layout();
	}

	/**
	 * looks for the hidden subtree and its nodes in the corresponding map "rootToHiddenVertices" and
	 * makes them al visible again
	 * @param root the mxCell, which has been clicked on as the subtree has been hidden.
	 */
	private void restoreSubTree(mxCell root) {
		Set<mxCell> hiddenNodes = rootToHiddenVertices.get(root);
		rootToHiddenVertices.remove(root);
		for (mxCell cell : hiddenNodes){
			graph.addCell(cell);
		}
	}

	/**
	 * removes/hides every vertex/edge in the subtree of the given mxCell.
	 * The hidden nodes are united and this set is going to be the value
	 * ( the key is the mxCell root) in the map "rootToHiddenVertices".
	 * @param root
	 */
	private void hideSubTree(final mxCell root) {

		// add an empty set, which is going to be filled with hidden nodes
		final Set<mxCell> hiddenNodes = new HashSet<>();
		rootToHiddenVertices.put(root, hiddenNodes);
		
		// traverse through the tree, add the nodes to be removed to the hiddenNodes set.
		// no manipulation here at the graph
		graph.traverse(root, true, new mxICellVisitor() {
			@Override
			public boolean visit(Object vertex, Object edge) {
				mxCell cell = (mxCell) vertex;
				mxCell cell_edge= (mxCell) edge;
				if (cell != root) {
					hiddenNodes.add(cell);
					hiddenNodes.add(cell_edge);
				}
				return true;
			}
		});
		
		// remove the cells using the synchronized update of the model
		graph.getModel().beginUpdate();
		for (mxCell cell : hiddenNodes)
			graph.getModel().remove(cell);
		graph.getModel().endUpdate();
	}

	
	/**
	 * layouts the complete graph again
	 */
	private void layout() {
		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
		layout.setOrientation(SwingConstants.WEST);
		layout.setInterRankCellSpacing(80);
		layout.setInterHierarchySpacing(20);
		layout.setIntraCellSpacing(5);
		layout.execute(graph.getDefaultParent());
	}
}
