package swp_compiler_ss13.javabite.gui.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.visualization.ASTVisualization;
import swp_compiler_ss13.javabite.ast.ASTSource;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;


public class ASTVisualizerJb extends JComponent implements ASTVisualization {
	
	private static final long serialVersionUID = 1L;
	Map<Object, String> tooltips = new HashMap<Object, String>();
	public mxGraph graph;
	public List<Integer> intArray= new ArrayList<Integer>();
	mxGraph graph1;
	mxGraphComponent frame;
	Queue<Object> toVisit_celledCopy;
	int x, y;
	int i=1;

	@Override
	public void visualizeAST(AST ast) { //visualizes the ast
		
		// init with anonymous class, which implements the correct tooltip-behavior
		graph = new mxGraph(){
			public String getToolTipForCell(Object cell) {
				if (cell!=null){
					String s = tooltips.get(cell);
					return s;
				}
				return "NOT_RESOLVABLE";
			}
		};
		
		// init style & necessary TreeBuilder
		GraphStyle style=new GraphStyle(graph);
		style.style();
		TreeBuilder treeBuilder=new TreeBuilder(graph);
		treeBuilder.initTree(ast);
		this.tooltips=treeBuilder.tooltips;
		
		// set dimensions of frame
		GraphPropertyCalculator k = new GraphPropertyCalculator();
		this.x = 200 * k.levelsCounter(ast);
		this.y = 70 * k.maximumOfNodesInLevels();
		
		// configure layout
		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
		layout.setOrientation(SwingConstants.WEST);
		layout.setInterRankCellSpacing(80);
		layout.setInterHierarchySpacing(20);
		layout.setIntraCellSpacing(5);
		layout.execute(graph.getDefaultParent());
		
		// final rendering & frame mixing & event delegation
		frame = new mxGraphComponent(graph);
		frame.setToolTips(true);
		HideAndShowEventHandler h = new HideAndShowEventHandler(graph,frame,ast);
		h.addEventHandler();
		intArray=treeBuilder.intArray;
	}
	

	public JScrollPane getFrame() {
		return frame;
	}

}
