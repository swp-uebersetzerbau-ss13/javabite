package swp_compiler_ss13.javabite.gui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
	public int openFileDialog(File openedFile, boolean save) {
		JFileChooser chooser = new JFileChooser();
		// TODO: set an apropriate directory
		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(null);
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			openedFile = chooser.getSelectedFile();
			String fileName = openedFile.getName();
			mf.setTitle("Javabite Compiler - " + fileName);
			if(save) {
				saveEditorContentIntoFile(openedFile);
				mf.toolBarLabel.setText("Document saved.");
			}
			else {
				mf.saveFileContentIntoEditor(openedFile);
				mf.toolBarLabel.setText("Document opened.");
			}
		}
		
		return returnVal;
	}
	
	public int saveFileDialog(File openedFile) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		chooser.setFileFilter(filter);
		chooser.setSelectedFile(new File("New File.prog"));
		
		int returnVal = chooser.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) { 
			openedFile = chooser.getSelectedFile();
			mf.setTitle("Javabite Compiler - " + openedFile.getName());
			mf.toolBarLabel.setText("Document saved.");
			saveEditorContentIntoFile(openedFile);
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
	
	public void openNewFile(File openedFile) {
		openedFile = null;
		mf.editorPaneSourcecode.setText("");
		mf.toolBarLabel.setText("New document opened.");
		mf.setTitle("Javabite Compiler - New File.prog");
	}
	
	/**
	 * Reads current editor code and writes it into given file
	 * */
	public void saveEditorContentIntoFile(File openedFile) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(openedFile));
			bw.write(mf.editorPaneSourcecode.getText());
			bw.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		String fileName = "";
		if (openedFile == null) {
			fileName = "New File.prog";
		} else {
			fileName = openedFile.getName();
		}
		mf.setTitle("Javabite Compiler - " + fileName);
		mf.toolBarLabel.setText("Document saved.");
	}
}