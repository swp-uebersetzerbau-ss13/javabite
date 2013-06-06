package swp_compiler_ss13.javabite.gui.ast.fitted;

import javax.swing.JFrame;
import javax.swing.JLabel;

import swp_compiler_ss13.common.ast.AST;

import com.mxgraph.view.mxGraph;

public abstract class GraphFrame extends JFrame {
	public GraphFrame() {
		this.add(new JLabel("Please change me"));
		this.setSize(50,50);
	}
	
	/**
	 * Initializes the frame
	 * @param ast the ast
	 * @param graph the corresponding graph, which already has been initialized
	 */
	public abstract void initWith(AST ast, mxGraph graph);
}
