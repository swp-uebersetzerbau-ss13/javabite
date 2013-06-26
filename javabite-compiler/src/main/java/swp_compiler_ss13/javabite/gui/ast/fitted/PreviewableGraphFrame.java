package swp_compiler_ss13.javabite.gui.ast.fitted;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sun.font.GraphicComponent;
import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.javabite.ast.ASTSource;
import swp_compiler_ss13.javabite.gui.ast.ASTVisualizerJb;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class PreviewableGraphFrame extends GraphFrame {

	@Override
	public void initWith(AST ast, mxGraph graph) {
		// this method shows just the functionality of the jframe:)

		JFrame frame = new JFrame();

		final mxGraphComponent graphComponent = new mxGraphComponent(graph);
		frame.getContentPane().add(graphComponent, BorderLayout.CENTER);

		// create toolbar (graphOutline and toolbar Buttons
		JPanel toolBar = new JPanel();
		toolBar.setLayout(new BorderLayout());

		this.setSize(800, 50);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		// toolbar Buttons
		JPanel buttonBar = new JPanel();
		buttonBar.setLayout(new FlowLayout());

		// zoom to fit
		JButton btZoomToFit = new JButton("Zoom in");
		btZoomToFit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				double newScale = 1;

				Dimension graphSize = graphComponent.getGraphControl()
						.getSize();
				Dimension viewPortSize = graphComponent.getViewport().getSize();

				int gw = (int) graphSize.getWidth();
				int gh = (int) graphSize.getHeight();

				if (gw > 0 && gh > 0) {
					int w = (int) viewPortSize.getWidth();
					int h = (int) viewPortSize.getHeight();

					newScale = Math.min((double) w / gw, (double) h / gh);
				}

				graphComponent.zoomIn();

			}
		});
		buttonBar.add(btZoomToFit);

		// center graph
		JButton btCenter = new JButton("Center Graph in ViewPort");
		btCenter.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				Dimension graphSize = graphComponent.getGraphControl()
						.getSize();
				Dimension viewPortSize = graphComponent.getViewport().getSize();

				int x = graphSize.width / 2 - viewPortSize.width / 2;
				int y = graphSize.height / 2 - viewPortSize.height / 2;
				int w = viewPortSize.width;
				int h = viewPortSize.height;

				graphComponent.getGraphControl().scrollRectToVisible(
						new Rectangle(x, y, w, h));

			}
		});
		buttonBar.add(btCenter);

		// Zoom outside
		JButton btZoomToFit1 = new JButton("Zoom Out To fit");
		btCenter.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				Dimension graphSize = graphComponent.getGraphControl()
						.getSize();
				Dimension viewPortSize = graphComponent.getViewport().getSize();

				// hier will sub the view from the windows size and the graph
				// and then will will add it

				int x = (int) viewPortSize.width;
				int y = (int) viewPortSize.height;
				int w = (int) graphSize.width;
				int v = (int) graphSize.height;

				int z = x - w;
				int l = y - v;

				int neww = (int) graphSize.height + z;
				int newv = (int) graphSize.width + l;

				graphComponent.getGraphControl().scrollRectToVisible(
						new Rectangle(z, l, neww, newv));
			}

		});
		buttonBar.add(btZoomToFit1);

		// put components on frame
		toolBar.add(buttonBar, BorderLayout.CENTER);
		frame.getContentPane().add(toolBar, BorderLayout.SOUTH);
		frame.setVisible(true);

	}

	public static void main(String[] args) {
		AST ast = ASTSource.getSecondAST();
		ASTVisualizerJb astViz = new ASTVisualizerJb();
		astViz.visualizeAST(ast);
		new PreviewableGraphFrame().initWith(ast, astViz.graph);

	}

}
