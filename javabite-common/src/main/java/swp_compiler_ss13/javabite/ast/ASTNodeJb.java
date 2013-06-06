package swp_compiler_ss13.javabite.ast;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;

public abstract class ASTNodeJb implements ASTNode, Iterable<ASTNode>{
	final private TreeMap<Integer,ASTNode> children=new TreeMap<>();
	protected ASTNodeType astNodeType;
	private Map<TokenType,List<Token>> typeTokenMap=new HashMap<>();
	Logger logger=LoggerFactory.getLogger(this.getClass());
	
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
	
	
	/**
	 * adds a token and associates it with the given type
	 * @param token the token you want to save
	 */
	public final void associateTypeWith(Token token){
		List<Token> target=typeTokenMap.get(token.getTokenType());
		if (target==null) {
			target=new LinkedList<>();
			typeTokenMap.put(token.getTokenType(),target);
		}
		target.add(token);
	}
	
	public final void putAllTokens(List<Object> objects){
		for (Object o : objects){
			if (o instanceof Token){
				associateTypeWith((Token)o);
			}
		}
	}
	
	
	protected final List<Token> getAssociatedTokenFromType(TokenType type){
		List<Token> list=typeTokenMap.get(type);
		if (list==null){
			list=new LinkedList<>();
			typeTokenMap.put(type, list);
		}
		return list;
	}
	
	
	
	/**
	 * returns the token with one of these types... if it's defined multiple, an error should signal it:)
	 * @param type
	 * @return
	 */
	protected final List<Token> getAssociatedTokenListFromType(TokenType type, TokenType ...types){
		List<Token> res=getAssociatedTokenFromType(type);
		int i=0;
		while (res.isEmpty() && i<types.length)
			res.addAll(getAssociatedTokenFromType(types[i++]));
		return res;
	}
	/**
	 * returns the token with one of these types... if it's defined multiple, an error should signal it:)
	 * @param type
	 * @return
	 */
	protected final Token getAssociatedTokenListFromTypeUnique(TokenType type, TokenType ...types){
		List<Token> res=getAssociatedTokenFromType(type);
		int i=0;
		while (res.isEmpty() && i<types.length)
			res.addAll(getAssociatedTokenFromType(types[i++]));
		if (res.size()!=1) logger.error("{} looked for type {},{}. result was {}",this.getClass(),type,Arrays.toString(types),res);
		
		return res.get(0);
	}
	

	
}
