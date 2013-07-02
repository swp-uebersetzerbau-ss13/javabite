
package swp_compiler_ss13.javabite.gui.bytecode;

import java.io.File;

import javax.swing.JFrame;

import org.gjt.jclasslib.browser.BrowserMDIFrame;

public class ByteCodeVisualizerJb extends JFrame {

	private static final long serialVersionUID = 1L;
	private BrowserMDIFrame structures;
	
	public ByteCodeVisualizerJb() {
		structures = new BrowserMDIFrame();
	}

	/**
	 * To visualize the bytecode we use jclasslib.
	 */
	public void visualizeByteCode(File classfile) {
		this.structures.doOpenClassFile(classfile);
		this.structures.setVisible(true);
	}
}
