package swp_compiler_ss13.javabite.gui.ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mxgraph.view.mxGraph;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.lexer.Token;

public class CellBuilder {
	
	mxGraph graph;
	List<Token> token= new ArrayList<Token>();
	Map<Object, String> tooltips = new HashMap<Object, String>();
	String str= "";
	int counter=0;
	List<Integer> intArray= new ArrayList<Integer>();
	
	public CellBuilder(mxGraph graph){
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
		VertexAttributes ver = new VertexAttributes();
		ver.addAttributes(ast);
		List<String> strList=ver.strList;
		intArray.add(strList.size());
		str=ver.strA;
		String value=ver.value;
		String color=ver.color;
		if (ast instanceof DeclarationNode){
				if (strList.size()>1){
					int y=6*5*strList.size();
					returnVal = 
							graph.insertVertex(graph.getDefaultParent(),null,value,20,40,150,y,color);
				}
				else{
					returnVal = 
							graph.insertVertex(graph.getDefaultParent(),null,value,20,40,150,55,color);	
				}
		}
		else{
			returnVal = 
					graph.insertVertex(graph.getDefaultParent(),null,value,20,40,150,55,color);	
		}
		tooltips.put(returnVal, str);
		str="";
        return returnVal;
	}

}
