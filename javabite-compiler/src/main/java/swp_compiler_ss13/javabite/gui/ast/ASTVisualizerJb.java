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
import swp_compiler_ss13.javabite.gui.ast.fitted.KhaledGraphFrame;
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
		graph = new mxGraph(){
			public String getToolTipForCell(Object cell) {
				if (cell!=null){
					String s = tooltips.get(cell);
					return s;
				}
				return null;
			}
		};
		graphStyle style=new graphStyle(graph);
		style.style();
		CreateTree createTree=new CreateTree(graph);
		createTree.initTree(ast);
		this.tooltips=createTree.tooltips;
		KhaledGraphFrame k = new KhaledGraphFrame();
		this.x = 200 * k.levelsCounter(ast);
		this.y = 70 * k.maximumOfNodesInLevels();
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
		intArray=createTree.intArray;
	}
	

	public JScrollPane getFrame() {
		return frame;
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
