package swp_compiler_ss13.javabite.ast;

import java.util.Iterator;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.parser.SymbolTable;

public class ASTJb implements AST {


	protected BlockNode rootNode;

	@Override
	public Iterator<ASTNode> getDFSLTRIterator() {
		return rootNode.getDFSLTRNodeIterator();
	}

	@Override
	public Integer getNumberOfNodes() {
		int i=0;
		Iterator<ASTNode> it = rootNode.getDFSLTRNodeIterator();
		while (it.hasNext()) {
			i++;
			it.next();
	}
		return i;
	}

	public BlockNode getRootNode() {
		return rootNode;
	}

	@Override
	public SymbolTable getRootSymbolTable() {
		return rootNode.getSymbolTable();
	}

	public void setRootNode(BlockNode rootNode) {
		this.rootNode = rootNode;
	}

}
