package swp_compiler_ss13.javabite.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileManager {
	
	private final FileNameExtensionFilter filter = new FileNameExtensionFilter("Sourcecode (.prog)", "prog");
	private MainFrame mf;
	
	public FileManager(MainFrame mf) {
		this.mf = mf;
	}
	
	// if it returns 0, file was opened. if it returns 1, it was cancelled
	public int openFileDialog(File openedFile) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(null);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			openedFile = chooser.getSelectedFile();
			String fileName = openedFile.getName();
			mf.setTitle("Javabite Compiler - " + fileName);
			mf.saveFileContentIntoEditor(openedFile);
			mf.toolBarLabel.setText("Document opened.");
		}
		
		return returnVal;
	}

}
