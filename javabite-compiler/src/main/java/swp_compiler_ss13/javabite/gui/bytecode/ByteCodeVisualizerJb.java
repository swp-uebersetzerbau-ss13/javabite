
package swp_compiler_ss13.javabite.gui.bytecode;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;

import org.gjt.jclasslib.browser.BrowserInternalFrame;
import org.gjt.jclasslib.browser.BrowserMDIFrame;
import org.gjt.jclasslib.browser.BrowserServices;

public class ByteCodeVisualizerJb extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextPane structures;
//	private BrowserMDIFrame structures;
	private JTextPane hexdump;
	JTabbedPane tabbedPane;
	
	public ByteCodeVisualizerJb(File classfile) {
		tabbedPane = new JTabbedPane();

		JScrollPane scrollPane_structures = new JScrollPane();
		JScrollPane scrollPane_hexdump = new JScrollPane();
		
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		
//		structures = new BrowserMDIFrame();		
//		tabbedPane.addTab("structure", null, structures,
//                "displays the strucutes of the classfile");		

		/*
		 * dummy
		 */
		structures = new JTextPane();
		scrollPane_structures.setViewportView(structures);
		
		hexdump = new JTextPane();
		scrollPane_hexdump.setViewportView(hexdump);
		
		tabbedPane.addTab("structure", null, structures,
                "displays the strucutes of the classfile");

		tabbedPane.addTab("hexdump", null, hexdump,
                "shows the hexdump of the classfile");

	}

	/**
	 * To visualize the bytecode we use jclasslib.
	 */
	public void visualizeByteCode(String hexdump) {
		this.hexdump.setText(hexdump);
		this.structures.setText("dummy - remove it");
		
//		this.structures.doOpenClassFile(classfile);
//		this.structures.setVisible(true);
		
		this.setSize(600, 400);
		this.setVisible(true);
		tabbedPane.setVisible(true);
	}
}
