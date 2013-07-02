package swp_compiler_ss13.javabite.gui;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

public class HotkeyManager implements KeyEventDispatcher {
	
	FileManager fileManager;
	JTextPane editorPaneSourcecode;
	MainFrame mainFrame;
	//helpers for copy paste cut functions
	String copyValue = "";
	
	public HotkeyManager(FileManager fileManager, JTextPane editorPaneSourcecode, MainFrame mainFrame) {
		this.fileManager = fileManager;
		this.editorPaneSourcecode = editorPaneSourcecode;
		this.mainFrame = mainFrame;
	}

	//make sure that this event just get activated on key release
	boolean keypress = false;
	@Override
	public boolean dispatchKeyEvent(KeyEvent arg0) {
		
		//save, strg+s
		if(arg0.getKeyCode() == 83 && arg0.isControlDown())
		{
			keypress = !keypress;
			if(keypress == false){
				System.out.println("Save file");
				fileManager.saveFile();
			}
		}
		
		//new File, alt+shift+n
		if(arg0.getKeyCode() == 78 && arg0.isAltDown() && arg0.isShiftDown())
		{
			keypress = !keypress;
			if(keypress == false){
				System.out.println("New file");
				fileManager.newFile();
			}
		}
		
		//open File, alt+shift+o
		if(arg0.getKeyCode() == 79 && arg0.isAltDown() && arg0.isShiftDown())
		{
			keypress = !keypress;
			if(keypress == false){
				System.out.println("Open File");
				fileManager.openFile();
			}
		}
		
		//copy, strg+c
		if(arg0.getKeyCode() == 67 && arg0.isControlDown())
		{
			keypress = !keypress;
			if(keypress == false){
				System.out.println("Copy");
				copyText();
			}
		}
		
		//cut, strg+x
		if(arg0.getKeyCode() == 88 && arg0.isControlDown())
		{
			keypress = !keypress;
			if(keypress == false){
				System.out.println("cut");
				cutText();
			}
		}
		//paste, strg+v
		if(arg0.getKeyCode() == 86 && arg0.isControlDown())
		{
			keypress = !keypress;
			if(keypress == false){
				System.out.println("paste");
				pasteText();
			}
		}
		
		//select all, strg+a
		if(arg0.getKeyCode() == 65 && arg0.isControlDown() )
		{
			keypress = !keypress;
			if(keypress == false){
				System.out.println("select all");
				selectAllText();
			}
		}
		
		//run , strg + f11
		if(arg0.getKeyCode() == 122 && arg0.isControlDown() )
		{
			keypress = !keypress;
			if(keypress == false){
				System.out.println("compile");
				mainFrame.compile();
			}
		}
		
		System.out.println(arg0.getKeyCode());
		return false;
	}
	private void selectAllText() {
		// TODO Auto-generated method stub
//		editorPaneSourcecode.selectAll();
	}
	private void pasteText() {
		// TODO Auto-generated method stub
		
	}
	private void cutText() {
//		// TODO Auto-generated method stub
//		copyValue = editorPaneSourcecode.getSelectedText();
//		try {
//			editorPaneSourcecode.getDocument().remove(editorPaneSourcecode.getSelectionStart(), editorPaneSourcecode.getSelectionEnd()-1);
//		} catch (BadLocationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	private void copyText() {
		// TODO Auto-generated method stub
//		copyValue = editorPaneSourcecode.getSelectedText();
	}
	
	private void removeTextFromTo(int start, int to) {
//		String startText = editorPaneSourcecode.getText().substring(0, start);
////		String endText = editorPaneSourcecode.getText().substring(end,  editorPaneSourcecode.getText().length());
//		try {
//			editorPaneSourcecode.getDocument().remove(start, to);
//		} catch (BadLocationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
