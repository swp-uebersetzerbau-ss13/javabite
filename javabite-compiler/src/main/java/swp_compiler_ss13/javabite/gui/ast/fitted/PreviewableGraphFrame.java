package swp_compiler_ss13.javabite.gui.ast.fitted;

import javax.swing.JFrame;
import javax.swing.JLabel;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.javabite.ast.ASTSource;
import swp_compiler_ss13.javabite.gui.ast.ASTVisualizerJb;

import com.mxgraph.view.mxGraph;

public class PreviewableGraphFrame extends GraphFrame {

	@Override
	public void initWith(AST ast, mxGraph graph) {
		// this method shows just the functionality of the jframe:)
		
		this.add(new JLabel("I am a instance of "+this.getClass()));
		this.setSize(800,100);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	public static void main(String[] args){
		AST ast=ASTSource.getSecondAST();
		ASTVisualizerJb astViz=new ASTVisualizerJb();
		astViz.visualizeAST(ast);
		
		new PreviewableGraphFrame().initWith(ast,astViz.graph);
	}
}
