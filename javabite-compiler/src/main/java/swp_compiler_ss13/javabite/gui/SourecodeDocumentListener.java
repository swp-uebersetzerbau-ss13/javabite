package swp_compiler_ss13.javabite.gui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class SourecodeDocumentListener implements DocumentListener {
	
	private MainFrame mf;
	private Document oldDoc;
	
	protected SourecodeDocumentListener(MainFrame mf) {
		this.mf = mf;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		oldDoc = e.getDocument();
		remarkFileAsChanged();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		oldDoc = e.getDocument();
		remarkFileAsChanged();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// this event is also fired, when focus is changed
		// thus check, if old editor content and current content are different
		if (e.getDocument() != oldDoc) {
			remarkFileAsChanged();
		}
	}
	
	private void remarkFileAsChanged() {
		// mark file as changed
		mf.fileChanged = true;
		
		// mark filename in the title bar as changed
		String fileName = "";
		if(mf.openedFile == null) {
			fileName = "New File.prog";
		} else {
			fileName = mf.openedFile.getName();
		}
		mf.setTitle("Javabite Compiler - *" + fileName);
		mf.toolBarLabel.setText("Sourcecode changed.");
	}
	
}