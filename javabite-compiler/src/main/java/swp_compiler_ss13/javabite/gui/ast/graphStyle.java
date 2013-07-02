package swp_compiler_ss13.javabite.gui.ast;

import java.util.Hashtable;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

public class graphStyle {
	mxGraph graph;
	
	public graphStyle(mxGraph graph){
		this.graph=graph;
	}
	
	void style(){
		graph.getStylesheet();
		mxStylesheet stylesheet = graph.getStylesheet();
		Hashtable<String, Object> style = new Hashtable<String, Object>();
		stylesheet.putCellStyle("ROUNDED", style);
		style.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
		style.put(mxConstants.STYLE_OPACITY, 50);
		style.put(mxConstants.STYLE_FONTCOLOR, "#000000");
		style.put(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD);
		stylesheet.putCellStyle("BOLD", style);
	}

}
