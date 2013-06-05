package swp_compiler_ss13.javabite.gui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SourecodeDocumentListener implements DocumentListener {
	
	private MainFrame mf;
	
	protected SourecodeDocumentListener(MainFrame mf) {
		this.mf = mf;
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
		remarkFileAsChanged();
	}
	
	private void remarkFileAsChanged() {
		mf.fileChanged = true;
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