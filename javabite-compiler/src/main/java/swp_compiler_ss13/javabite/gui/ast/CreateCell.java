package swp_compiler_ss13.javabite.gui.ast;

import com.mxgraph.view.mxGraph;

import swp_compiler_ss13.common.ast.ASTNode;

public class CreateCell {
	
	mxGraph graph;
	
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
		/*for(Token t:ast.coverage()){
			System.out.println(t.getValue());
		}*/
		Object returnVal;
		vertexAttributes ver = new vertexAttributes();
		ver.addAttributes(ast);
		String value=ver.value;
		String color=ver.color;
		returnVal = 
		graph.insertVertex(graph.getDefaultParent(),null,value,20,40,100,35,color);		
        return returnVal;
	}

}
