package swp_compiler_ss13.javabite.gui.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mxgraph.view.mxGraph;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.lexer.Token;

public class CreateCell {
	
	mxGraph graph;
	List<Token> token= new ArrayList<Token>();
	Map<Object, String> tooltips = new HashMap<Object, String>();
	String str= "";
	
	public CreateCell(mxGraph graph){
		this.graph=graph;
	}
	
	/**
	 * creates a cell representation of the ast
	 * 
	 * @param ast
	 *            the astNode you want to convert
	 * @return the cell-object, which correspondents to the given node
	 */

	Object asCell(ASTNode ast) {
		Object returnVal;
		vertexAttributes ver = new vertexAttributes();
		ver.addAttributes(ast);
		str=ver.strA;
		String value=ver.value;
		String color=ver.color;
		returnVal = 
		graph.insertVertex(graph.getDefaultParent(),null,value,20,40,140,55,color);	
		tooltips.put(returnVal, str);
		str="";
        return returnVal;
	}

}
