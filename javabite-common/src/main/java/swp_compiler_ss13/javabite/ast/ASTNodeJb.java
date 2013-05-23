package swp_compiler_ss13.javabite.ast;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.TreeMap;

import swp_compiler_ss13.common.ast.ASTNode;

public abstract class ASTNodeJb implements ASTNode, Iterable<ASTNode>{
	final private TreeMap<Integer,ASTNode> children=new TreeMap<>();
	protected ASTNodeType astNodeType;
	
	
	ASTNode parent;
	
	
	public ASTNodeJb(ASTNodeType myType) {
		this.astNodeType=myType;
	}
	
	/**
	 * registers a new childnode
	 * @param child the childnode
	 * @param index the index ( child1.index < child2.index -> child1 is to the left of child2 )
	 * @return
	 */
	final protected ASTNode addChild(ASTNode child,int index){
		child.setParentNode(this);
		return children.put(index, child);
	}
	
	@Override
	final public ASTNodeType getNodeType() {
		return astNodeType;
	}

	@Override
	final public Integer getNumberOfNodes() {
		int i=0;
		for (ASTNode child : children.values()) i+=child.getNumberOfNodes();
		return i+1;
	}

	@Override
	final public Iterator<ASTNode> getDFSLTRNodeIterator() {
		return this.iterator();
	}

	@Override
	final public List<ASTNode> getChildren() {
		return new LinkedList<ASTNode>(children.values());
	}

	@Override
	final public ASTNode getParentNode() {
		return parent;
	}

	@Override
	public void setParentNode(ASTNode node) {
		this.parent=node;
	}

	@Override
	final public Iterator<ASTNode> iterator() {
		Queue<ASTNode> nodes=new ArrayDeque<>();
		for (ASTNode child : children.values()){
			Iterator<ASTNode> child_it=child.getDFSLTRNodeIterator();
			while (child_it.hasNext()){
				nodes.add(child_it.next());
			}
		}
		nodes.add(this);
		return nodes.iterator();
	}
	
	
	
	public String toString(){
		StringBuilder strb=new StringBuilder();
		strb.append(this.getClass().getSimpleName());
		strb.append(getNodePropertiesAsString());
		return strb.toString();
	}
	
	public String toLongString(){
		StringBuilder strb=new StringBuilder();
		strb.append(this.getClass().getSimpleName());
		strb.append(getNodePropertiesAsString());
		strb.append("[");
		boolean first=true;
		for (ASTNode child : children.values() ) {
			if (!first) strb.append(", ");
			else first=!first;
			strb.append(child);
		}
		strb.append("]");
		return strb.toString();
	}

	final private String getNodePropertiesAsString() {
		Properties proMap=new Properties();
		fillNodeProperties(proMap);
		return proMap.toString();
	}
	
	protected void fillNodeProperties(Properties props){
		// no properties by default
	}
}
