package swp_compiler_ss13.javabite.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Stack;

import javax.swing.JTextPane;

public class UndoCostumManager {

	Stack<String> undoStack = new Stack<String>();
	Stack<String> redoStack = new Stack<String>();
	
	JTextPane editorPaneSourcecode;
	
	public UndoCostumManager(final JTextPane editorPaneSourcecode) {
		this.editorPaneSourcecode = editorPaneSourcecode;
		
		// add listener for sourcecode colorization 
		this.editorPaneSourcecode.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent arg0) {
				//skip layout changes
				if(undoStack.size() == 0 || !editorPaneSourcecode.getText().equals(undoStack.get(undoStack.size()-1)))
					undoStack.push(editorPaneSourcecode.getText());
				redoStack.clear();
			}
		});
	}
	
	public boolean canUndo() {
		return !undoStack.isEmpty();
	}

	public void undo() {
		redoStack.push(editorPaneSourcecode.getText());
		editorPaneSourcecode.setText(undoStack.pop());
	}

	public boolean canRedo() {
		return !redoStack.isEmpty();
	}

	public void redo() {
		
		editorPaneSourcecode.setText(redoStack.pop());
		undoStack.push(editorPaneSourcecode.getText());
		
	}
}
