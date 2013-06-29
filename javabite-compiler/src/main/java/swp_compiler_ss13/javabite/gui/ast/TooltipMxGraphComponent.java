package swp_compiler_ss13.javabite.gui.ast;

import java.awt.event.MouseEvent;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class TooltipMxGraphComponent extends mxGraphComponent {

	public TooltipMxGraphComponent(mxGraph graph) {
		super(graph);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public String getToolTipText(MouseEvent event) {
		return "123";
	}
	

}
