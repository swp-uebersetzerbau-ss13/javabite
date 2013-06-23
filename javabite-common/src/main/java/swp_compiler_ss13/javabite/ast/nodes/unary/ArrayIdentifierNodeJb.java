package swp_compiler_ss13.javabite.ast.nodes.unary;

import java.util.List;
import java.util.Properties;

import swp_compiler_ss13.common.ast.nodes.ExpressionNode;
import swp_compiler_ss13.common.ast.nodes.IdentifierNode;
import swp_compiler_ss13.common.ast.nodes.unary.ArrayIdentifierNode;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.javabite.ast.nodes.IdentifierNodeJb;

public class ArrayIdentifierNodeJb extends IdentifierNodeJb implements ArrayIdentifierNode {
	public ArrayIdentifierNodeJb() {
		super(ASTNodeType.ArrayIdentifierNode);
	}
	@Deprecated
	protected Integer index_depr;

	protected ExpressionNode indexNode;
	protected IdentifierNode identifierNode;
	
	public ExpressionNode getIndexNode(){
		return indexNode;
	}
	
	public void setIndexNode(ExpressionNode indexNode){
		this.indexNode=indexNode;
	}
	
	@Deprecated
	public Integer getIndex() {
		return index_depr;
	}
	
	@Deprecated
	public void setIndex(Integer index) {
		this.index_depr = index;
	}
	
	public IdentifierNode getIdentifierNode() {
		return identifierNode;
	}
	public void setIdentifierNode(IdentifierNode identifierNode) {
		this.identifierNode = identifierNode;
	}
	
	
	@Override
	protected void fillNodeProperties(Properties props) {
	}
	@Override
	public List<Token> nestedCoverage() {
		//TODO implement
		throw new RuntimeException("not requested for MS2");
	}
}
