package swp_compiler_ss13.javabite.gui.ast;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.SwingConstants;

import swp_compiler_ss13.common.ast.AST;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class HideAndShowEventHandler extends MouseAdapter {
	
	// attributes set by outer usage
	AST ast;
	mxGraph graph;
	mxGraph firstGraph;
	mxGraphComponent frame;
	
	
	
	List<Object[]> temporaryEdgeslist = new ArrayList<Object[]>();
	Object[] temporaryEdges;
	
	List<mxCell> cells = new ArrayList<mxCell>();
	List<Object[]> listEdges = new ArrayList<Object[]>();
	List<mxCell> queueSubTree = new ArrayList<mxCell>();
	List<List<mxCell>> listSubTree = new ArrayList<List<mxCell>>();
	List<List<Object[]>> linLEdges = new ArrayList<List<Object[]>>(); // liste
																		// in
																		// liste
	List<Integer> listClick = new ArrayList<Integer>();
	List<Object> listObject = new ArrayList<Object>();
	int location = 0;
	int index = 0;
	int i = 0;
	int j = 0;
	
	public HideAndShowEventHandler(mxGraph graph, mxGraphComponent frame,
			AST ast) {
		this.graph = graph;
		this.frame = frame;
		this.ast = ast;
		this.firstGraph = graph;
	}
	
	public void addEventHandler() {
		frame.getGraphControl().addMouseListener(this);
	}
	
	
	public void mouseClicked(MouseEvent e) {
		mxCell cell =(mxCell) ((mxGraphComponent) frame).getCellAt(e.getX(), e.getY());
		i++;
		if (cell != null && i == 2) {
			// double click on vertex
			if (listObject.contains(cell)) {
				int cellLocation = listObject.indexOf(cell);
				listClick.set(cellLocation, 2);
			} else {
				listObject.add(location, cell);
				listClick.add(location, 2);
				location++;
			}
			enqueueAllVerticesInSubtreeAndRemoveConditionally(cell);
			listSubTree.add(j, queueSubTree);
			cells.add(j, cell); // inserted cell
			Object[] edges = graph.getOutgoingEdges(cell);
			temporaryEdgeslist.add(j, edges);
			graph.removeCells(edges);
			for (mxCell k : queueSubTree) {
				Object[] edges1 = graph.getOutgoingEdges( k);
				listEdges.add(index, edges1);
				graph.removeCells(edges1);
				index++;
			}
			linLEdges.add(j, listEdges);
			index = 0;
			i = 0;
			j++;

		} else if (cell != null && i == 1 && listObject.contains(cell)) {
			int cellLocation = listObject.indexOf(cell);
			i = 0;
			if (listClick.get(cellLocation) == 2) {
				listClick.set(cellLocation, 0);
				int indexOfCell = cells.indexOf(cell);
				temporaryEdges = temporaryEdgeslist.get(indexOfCell);
				enqueueAllVerticesInSubtreeAndInsertEdge( cell);
				listEdges = linLEdges.get(indexOfCell);
				for (mxCell k : listSubTree.get(indexOfCell)) {
					temporaryEdges = listEdges.get(index);
					enqueueAllVerticesInSubtreeAndInsertEdge((mxCell) k);
					index++;
				}
				index = 0;
				listEdges.clear();
				queueSubTree.clear();
				listClick.set(cellLocation, 0);

				mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
				layout.setOrientation(SwingConstants.WEST);
				layout.setInterRankCellSpacing(80);
				layout.setInterHierarchySpacing(20);
				layout.setIntraCellSpacing(5);
				layout.execute(graph.getDefaultParent());
			}
		}
		graph.repaint();
	}



	

	
	/**
	 * traverses through the subtree of parent.
	 * Additionally, if i==2 every node will be removed from its parent
	 * @param parent the root of the subtree
	 */
	private void enqueueAllVerticesInSubtreeAndRemoveConditionally(mxCell parent) {
		HashSet<mxCell> visitedSet= new HashSet<>();
		visitedSet.clear();
		List<mxCell> queue = new ArrayList<mxCell>();
		queue.add(parent);
		visitedSet.add(parent);
		// for each v in vertices(bfs(parent))
		while (!queue.isEmpty()) {
			mxCell cell = queue.remove(0);
			Object[] edges = graph.getOutgoingEdges(cell);
			// e_set <- (v,u) of E
			for (Object edge : edges) {
				// target <- u
				mxCell target = (mxCell) graph.getView().getVisibleTerminal(
						edge, false);
				if (!visitedSet.contains(target)) {
					// if bfs(parent) hasn't visited u yet, mark u
					visitedSet.add(target);
					// add to set of bfs(parent)
					queue.add(target);
					queueSubTree.add(target);
					if (i == 2) {
						target.removeFromParent();// here there is problem
					}
				}
			}
		}
	}

	/**
	 * traverses through the subtree of parent.
	 * additionally, each edge to every found node is inserted
	 * @param parent the root of the subtree
	 */
	private void enqueueAllVerticesInSubtreeAndInsertEdge(mxCell parent) {
		HashSet<mxCell> visitedSet= new HashSet<>();
		visitedSet = new HashSet<mxCell>();
		List<mxCell> queue = new ArrayList<mxCell>();
		queue.add(parent);
		visitedSet.add(parent);
		// for each v in vertices(bfs(parent))
		while (!queue.isEmpty()) {
			mxCell cell = queue.remove(0);
			// e_set <- (v,u) of E
			for (Object edge : temporaryEdges) {
				mxCell target = (mxCell) firstGraph.getView()
						.getVisibleTerminal(edge, false);
				if (!visitedSet.contains(target)) {
					// if bfs(parent) hasn't visited u yet, mark u
					graph.addCell(target);
					// insert edge to graph
					graph.insertEdge(parent, null, null, cell, target);
					visitedSet.add(target);
					queue.add(target);
				}
			}
		}
	}

}
