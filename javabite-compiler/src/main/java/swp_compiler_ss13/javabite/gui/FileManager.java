package swp_compiler_ss13.javabite.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class FileManager implements DocumentListener {
	private final static String DOCUMENT_OPENED = "Document opened.";
	private final static String DOCUMENT_SAVED = "Document saved.";
	private final static String DOCUMENT_ASK = "Document has unsaved changes! Save file: ";
	
	private final FileNameExtensionFilter filter = new FileNameExtensionFilter("Sourcecode (.prog)", "prog");
	private MainFrame mf;
	public String filename;
	public File currentFile;
	public boolean hasUnsavedChanges = false;
	
	boolean isProcessing = false;
	
	public FileManager(MainFrame mf) {
		this.mf = mf;
	}
	
	/**
	 * open a new file
	 * 
	 * handles the saving of unsave changes
	 * 
	 * @return false if aborted else true
	 */
	public boolean newFile() {
		if (!saveFileIfChanged())
			return false;
		
		isProcessing = true;
		filename = "new.prog";
		currentFile = null;
		mf.clearSourcePane();
		updateUi();
		isProcessing = false;
		
		return true;
	}
	
	/**
	 * open a via user workflow
	 * 
	 * handles the saving of unsave changes
	 * 
	 * @return false if aborted else true
	 */
	public boolean openFile() {
		if (!saveFileIfChanged()) {
			return false;
		}
		
		JFileChooser chooser = getFileChooser();
		
		if (chooser.showOpenDialog(mf) != JFileChooser.APPROVE_OPTION) {
			return false;
		}
		
		return openFile(chooser.getSelectedFile());
	}
	
	/**
	 * open a the file
	 * 
	 * DOES NOT handles the saving of unsave changes
	 * 
	 * @param file which should be opened
	 * @return false if aborted else true
	 */
	public boolean openFile(File file) {
		isProcessing = true;
		setCurrentFile(file);
		loadFileContentIntoEditor(file);
		updateUi();
		mf.restyle();
		isProcessing = false;
		mf.setToolBar(DOCUMENT_OPENED);
		return true;
	}
	
	/**
	 * check if there are unsave changes and starts save workflow
	 * @return false if user abort workflow else true
	 */
	public boolean saveFileIfChanged() {
		if (!hasUnsavedChanges) {
			return true;
		}
		
		int result = JOptionPane.showConfirmDialog(mf, DOCUMENT_ASK + filename,
				"Save", JOptionPane.YES_NO_CANCEL_OPTION);
		
		if (result == JOptionPane.CANCEL_OPTION) {
			return false;
		}
		
		if (result == JOptionPane.YES_OPTION) {
			saveFile();
		}
		
		hasUnsavedChanges = false;
		return true;
	}
	
	public boolean saveFile() {
		if (currentFile == null) {
			return saveFileIn();
		}
		
		saveEditorContentIntoFile(currentFile);
		hasUnsavedChanges = false;
		mf.setToolBar(DOCUMENT_SAVED);
		updateUi();
		return true;
	}
	
	public boolean saveFileIn() {
		JFileChooser chooser = getFileChooser();
		chooser.setSelectedFile(currentFile != null?currentFile:new File(filename));
		if (chooser.showSaveDialog(mf) != JFileChooser.APPROVE_OPTION)
			return false;
		
		setCurrentFile(chooser.getSelectedFile());
		
		return saveFile();
	}
	
	public void updateUi() {
		mf.setTitle(filename + (hasUnsavedChanges?"*":""));
	}
	
	/**
	 * Reads current editor code and writes it into given file
	 */
	public void saveEditorContentIntoFile(File file) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(mf.editorPaneSourcecode.getText());
			bw.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Reads current file content and writes it into sourcecode editor
	 */
	public void loadFileContentIntoEditor(File file) {
		// read out lines
		BufferedReader in = null;
		String line = null;
		try {
			in = new BufferedReader(new FileReader(file));
			line = in.readLine();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
//		// insert lines into source code editor
		Document doc = mf.doc;
		try {
			doc.remove(0, doc.getLength()); // remove old content
			while (line != null) {
				doc.insertString(doc.getLength(), line + "\n", null);
				line = in.readLine();
			}
			// remove wrongly inserted last newline
			if (doc.getText(0, doc.getLength()).endsWith("\n")) {
				doc.remove(doc.getLength() - 1, 1);
			}
		} catch (BadLocationException | IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void setCurrentFile(File file) {
		currentFile = file;
		filename = currentFile.getName();
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		remarkFileAsChanged();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		remarkFileAsChanged();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// styling changes are of no value
	}
	
	private void remarkFileAsChanged() {
		if (isProcessing)
			return;
		
		hasUnsavedChanges = true;
		updateUi();
	}
	
	private JFileChooser getFileChooser() {
		JFileChooser chooser = new JFileChooser(currentFile != null ? currentFile.getParentFile() : getUserHome());
		chooser.setFileFilter(filter);
		return chooser;
	}
	
	private File getUserHome() {
		return new File(System.getProperty("user.home"));
	}
}
