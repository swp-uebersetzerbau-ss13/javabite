package swp_compiler_ss13.javabite.gui.ast.fitted;

import java.util.ArrayDeque;
import java.util.Queue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.javabite.ast.ASTSource;
import swp_compiler_ss13.javabite.gui.ast.ASTVisualizerJb;

import com.mxgraph.view.mxGraph;

public class KhaledGraphFrame extends GraphFrame {
	Queue<Integer> sizeQ= new ArrayDeque<>();
	
	@Override
	public void initWith(AST ast, mxGraph graph) {
		
		// this method shows just the functionality of the jframe:)
		this.add(new JLabel("I am a instance of "+this.getClass()));
		this.setSize(800,100);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	public int levelsCounter(AST ast){
		int counter=1;
		Queue<Queue<ASTNode>> queue= new ArrayDeque<>();
		Queue<ASTNode> nextLevel= new ArrayDeque<>();
		Queue<ASTNode> level= new ArrayDeque<>();
		level.add(ast.getRootNode());
		queue.add(level);
		sizeQ.add(1);
		
		while(counter!=ast.getNumberOfNodes()){
			for (ASTNode node:level){
				for (ASTNode child : node.getChildren()){
					nextLevel.add(child);
					counter++;
					}
				}
			queue.add(nextLevel);
			sizeQ.add(nextLevel.size());
			level.clear();
			for(ASTNode node:nextLevel ){
				level.add(node);
				}
			nextLevel.clear();
			}
		return queue.size();
		}
	
	public int maximumOfNodesInLevels(){
		int i=0;
		for (int k: sizeQ){
			if (i<k) i=k;
			}
		return i;
	}
	
	public int getNumberOfNodes(AST ast){
		return ast.getNumberOfNodes();
	}
	
	public static void main(String[] args){
		AST ast=ASTSource.getSecondAST();
		ASTVisualizerJb astViz=new ASTVisualizerJb();
		astViz.visualizeAST(ast);
		
		new KhaledGraphFrame().initWith(ast,astViz.graph);
	}
}
