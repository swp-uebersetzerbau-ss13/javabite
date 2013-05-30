package swp_compiler_ss13.javabite.gui;

import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SourecodeDocumentListener implements DocumentListener {
	
	private JLabel toolBarLabel;
	private MainFrame mf;
	
	protected SourecodeDocumentListener(JLabel toolBarLabel, MainFrame mf) {
		this.toolBarLabel = toolBarLabel;
		this.mf = mf;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		mf.fileChanged = true;
		toolBarLabel.setText("Sourcecode changed.");
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		mf.fileChanged = true;
		toolBarLabel.setText("Sourcecode changed.");
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		mf.fileChanged = true;
		toolBarLabel.setText("Sourcecode changed.");
	}

}