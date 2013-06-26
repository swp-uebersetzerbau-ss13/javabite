package swp_compiler_ss13.javabite.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
		// TODO: set an apropriate directory
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

	// 2 = yes, 1 = no, 0 = cancel
	public int saveOrNotDialog(File openedFile) {
		JFrame frame = new JFrame("Save");
		Object[] options = {"Cancel", "No", "Yes"};
		String fileName = (openedFile == null) ? "New File.prog" : openedFile.getName();
		int returnVal = JOptionPane.showOptionDialog (
			frame,
			"Save file \"" + fileName + "\"?\n",
			"Save",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[2]
		);
		
		return returnVal;
	}
}
