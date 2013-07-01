package swp_compiler_ss13.javabite.gui.ast;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import swp_compiler_ss13.common.ast.AST;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class HidingSubTree {

	private Set<mxCell> visitedSet = new HashSet<mxCell>();
	AST ast;
	mxGraph graph;
	mxGraph firstGraph;
	List<Object[]> temporaryEdgeslist= new ArrayList<Object[]>();
	Object[] temporaryEdges;
	mxGraphComponent frame;
	List<mxCell> cells = new ArrayList<mxCell>();
	List<Object[]> listEdges = new ArrayList<Object[]>();
	List<mxCell> queueSubTree = new ArrayList<mxCell>();
	List<List<mxCell>> listSubTree = new ArrayList<List<mxCell>>();
	List<List<Object[]>> linLEdges = new ArrayList<List<Object[]>>(); //liste in liste
	List<Integer> listClick = new ArrayList<Integer>();
	List<Object> listObject = new ArrayList<Object>();
	int location = 0;
	int index = 0;
	int i = 0;
	int j=0;

	public HidingSubTree(mxGraph graph, mxGraphComponent frame, AST ast) {
		this.graph = graph;
		this.frame = frame;
		this.ast = ast;
		this.firstGraph=graph;
	}

	public void hiddenSubTree() {
		frame.getGraphControl().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Object cell = ((mxGraphComponent) frame).getCellAt(e.getX(),
						e.getY());
				i++;
				if (cell != null && i == 2) {
					if (listObject.contains(cell)) { // if list contains cell
						int cellLocation = listObject.indexOf(cell);
						listClick.set(cellLocation, 2);
					} else {
						listObject.add(location, cell);
						listClick.add(location, 2);
						location++;
					}
					breadthFirstSearch((mxCell) cell);
					listSubTree.add(j, (List<mxCell>) queueSubTree); 
					cells.add(j,(mxCell) cell); //inserted cell 
					Object[] edges = graph.getOutgoingEdges(cell);
					temporaryEdgeslist.add(j,edges);
					graph.removeCells(edges);
					for (mxCell k : queueSubTree) {
						Object[] edges1 = graph.getOutgoingEdges((mxCell) k);
						listEdges.add(index, edges1);
						graph.removeCells(edges1);
						index++;
					}
					linLEdges.add(j, listEdges);
					index = 0;
					i = 0;
					j++;
				} 
				else if (cell != null && i == 1 && listObject.contains(cell)) {
					int cellLocation = listObject.indexOf(cell);
					i = 0;
					if (listClick.get(cellLocation) == 2) {
						listClick.set(cellLocation, 0);
						int indexOfCell=cells.indexOf(cell);
						temporaryEdges=temporaryEdgeslist.get(indexOfCell);
						bfsProduceSubTree((mxCell) cell);
						listEdges=linLEdges.get(indexOfCell);
						for (mxCell k : listSubTree.get(indexOfCell)) {
							temporaryEdges = listEdges.get(index);
							bfsProduceSubTree((mxCell) k);
							index++;
						}
						index = 0;
						listEdges.clear();
						queueSubTree.clear();
						listClick.set(cellLocation, 0);
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
			// return t
			// TODO: add handling code if you search something
			// for all edges e in G.incidentEdges(t) do
			Object[] edges = graph.getOutgoingEdges(cell);
			for (Object edge : edges) {
				// o <- G.opposite(t,e)
				// get node from edge
				mxCell target = (mxCell) graph.getView().getVisibleTerminal(
						edge, false);
				// if o is not marked:
				if (!isVisited(target)) {
					// mark o
					visit(target);
					// enqueue o onto Q
					queue.add(target);
					queueSubTree.add(target);
					if (i == 2) {
						target.removeFromParent();// here there is problem
					}
				}
			}
		}
	}

	private void bfsProduceSubTree(mxCell parent) {
		visitedSet = new HashSet<mxCell>();
		List<mxCell> queue = new ArrayList<mxCell>();
		queue.add(parent);
		visit(parent);
		while (!queue.isEmpty()) {
			mxCell cell = queue.get(0);
			queue.remove(cell);
			for (Object edge : temporaryEdges) {
				mxCell target = (mxCell) firstGraph.getView()
						.getVisibleTerminal(edge, false);
				if (!isVisited(target)) {
					graph.addCell(target);
					graph.insertEdge(parent, null, null, cell, target);
					visit(target);
					queue.add(target);
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

}
