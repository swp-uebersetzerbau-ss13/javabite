package swp_compiler_ss13.javabite.gui;

import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SourecodeDocumentListener implements DocumentListener {
	
	private JLabel toolBarLabel;
	
	protected SourecodeDocumentListener(JLabel toolBarLabel) {
		this.toolBarLabel = toolBarLabel;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		toolBarLabel.setText("Sourcecode inserted.");
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		toolBarLabel.setText("Sourcecode removed.");
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		toolBarLabel.setText("Sourcecode changed.");
	}

}