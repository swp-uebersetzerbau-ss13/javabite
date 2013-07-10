package swp_compiler_ss13.javabite.gui.ast.fitted;

import java.util.ArrayDeque;
import java.util.Queue;
import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;

public class KhaledGraphFrame {
	Queue<Integer> sizeQ = new ArrayDeque<>();

	public int levelsCounter(AST ast) {
		int counter = 1;
		Queue<Queue<ASTNode>> queue = new ArrayDeque<>();
		Queue<ASTNode> nextLevel = new ArrayDeque<>();
		Queue<ASTNode> level = new ArrayDeque<>();
		level.add(ast.getRootNode());
		queue.add(level);
		sizeQ.add(1);

		while (counter < ast.getNumberOfNodes()) {
			for (ASTNode node : level) {
				for (ASTNode child : node.getChildren()) {
					nextLevel.add(child);
					counter++;
				}
			}
			queue.add(nextLevel);
			sizeQ.add(nextLevel.size());
			level.clear();
			for (ASTNode node : nextLevel) {
				level.add(node);
			}
			nextLevel.clear();
		}
		return queue.size();
	}

	public int maximumOfNodesInLevels() {
		int i = 0;
		for (int k : sizeQ) {
			if (i < k)
				i = k;
		}
		return i;
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

	public int getNumberOfNodes(AST ast) {
		return ast.getNumberOfNodes();
	}
}
