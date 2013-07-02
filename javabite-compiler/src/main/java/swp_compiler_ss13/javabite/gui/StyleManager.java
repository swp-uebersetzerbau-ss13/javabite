package swp_compiler_ss13.javabite.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Point;
import java.io.File;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.Utilities;

import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.config.JavabiteConfig;

public class StyleManager extends DocumentFilter {
	private MainFrame mf;
	private JavabiteConfig config;
	public String filename;
	public File currentFile;
	public boolean hasUnsavedChanges = false;
	public List<Token> tokens;
	
	public StyleManager(MainFrame mf) {
		config = JavabiteConfig.getDefaultConfig();
		this.mf = mf;
	}
	
	/**
	 * Styles a special part of the sourcecode
	 */
	private void styleToken(TokenType tokenType, int start, int end) {
		// check properties file for tokentype key, if exist set defined color
		String color;
		if ((color = config.getProperty("syntaxHighlighting."+tokenType.toString().toLowerCase())) != null) {
			Style style = mf.editorPaneSourcecode.addStyle(tokenType.toString(), null);
			StyleConstants.setForeground(style, Color.decode(color));
			mf.doc.setCharacterAttributes(start, end, mf.editorPaneSourcecode.getStyle(tokenType.toString()), true);
		} else {
			Style style = mf.editorPaneSourcecode.addStyle("Black", null);
			StyleConstants.setForeground(style, Color.BLACK);
			mf.doc.setCharacterAttributes(start, end, mf.editorPaneSourcecode.getStyle("Black"), true);
		}
	}
	
	void styleEditorText() {
		mf.fileManager.isProcessing = true;
		// backup content
		int cursorPos = mf.editorPaneSourcecode.getCaretPosition();
		String text = mf.editorPaneSourcecode.getText();
		// reset so base style
		mf.editorPaneSourcecode.setText("");
		Style style = mf.editorPaneSourcecode.addStyle("Black", null);
		StyleConstants.setForeground(style, Color.BLACK);
		mf.doc.setCharacterAttributes(0, 1, mf.editorPaneSourcecode.getStyle("Black"), true);
		// restore content
		mf.editorPaneSourcecode.setText(text);
		mf.editorPaneSourcecode.setCaretPosition(cursorPos);
		
		int currentPosition = 0;
		int lastPosition = 0;
		for(Token t:tokens) {
			currentPosition = text.indexOf(t.getValue(), lastPosition);
			lastPosition = currentPosition + t.getValue().length();
			styleToken(t.getTokenType(), currentPosition, lastPosition);
		}
		mf.fileManager.isProcessing = false;
	}
	
	/**
	 * Underlines wrongly typed tokens
	 * */
	void underlineToken(List<Token> tokens, Color color) {
		mf.fileManager.isProcessing = true;
//		String code = mf.editorPaneSourcecode.getText();
//		String[] lines = code.split(System.getProperty("line.separator"));
//		
//		for(Token t:tokens) {
//			SimpleAttributeSet attributes = new SimpleAttributeSet();
//			StyleConstants.setForeground(attributes, color);
//			StyleConstants.setUnderline(attributes, true);
//			mf.doc.setCharacterAttributes(code.indexOf(lines[t.getLine()-1]), t.getValue().length(), attributes, true);
//		}

		SimpleAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setForeground(attributes, color);
		StyleConstants.setUnderline(attributes, true);
		TextRange range = getTextRangeOf(tokens);
		mf.doc.setCharacterAttributes(range.from, range.to, attributes, true);
		mf.fileManager.isProcessing = false;
	}
	
	int getLine(int pos) {
		int rn = (pos == 0) ? 1 : 0;
		try {
			int offs = pos;
			while (offs > 0) {
				offs=Utilities.getRowStart(mf.editorPaneSourcecode, offs) - 1;
				rn++;
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		return rn;
	}
	
	int getColumn(int pos) {
		try {
			return pos-Utilities.getRowStart(mf.editorPaneSourcecode, pos) + 1;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	TextRange getTextRangeOf(List<Token> tokens) {
		int from = 0;
		int to = 0;
		if (tokens != null && !tokens.isEmpty()) {
			int currentPos = 0;
			int line = 1;
			int startLine = tokens.get(0).getLine();
			int startColumn = tokens.get(0).getColumn();
			int endLine = tokens.get(tokens.size()-1).getLine();
			int endColumn = tokens.get(tokens.size()-1).getColumn() + tokens.get(tokens.size()-1).getValue().length();
			
			try {
				while (startLine != line) {
					currentPos = Utilities.getRowEnd(mf.editorPaneSourcecode,
							currentPos)+1;
					line++;
				}
				from = currentPos + startColumn - 1;
	
				if (startLine != endLine) {
					while (endLine != line) {
						currentPos = Utilities.getRowEnd(mf.editorPaneSourcecode,
								currentPos);
						line++;
					}
	
					to = currentPos + endColumn - 1;
				} else {
					to = currentPos + endColumn - startColumn;
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		
		return new TextRange(from, to);
	}
	
	void showToolTip(Point loc) {
		int pos = mf.editorPaneSourcecode.viewToModel(loc);
		int line = getLine(pos);
		int column = getColumn(pos);
		
		if (tokens == null)
			return;
		
		for (Token t:tokens) {
			if (t.getLine() < line) {
				continue;
			}
			
			if (t.getColumn()+t.getValue().length() <= column) {
				continue;
			}
			
			mf.editorPaneSourcecode.setToolTipText(t.getTokenType().name());
			return;
		}
	}
	
	void setTokens(List<Token> tokens) {
		this.tokens = tokens;
		styleEditorText();
	}

	@Override
	public void remove(FilterBypass fb, int offset, int length)
			throws BadLocationException {
		super.remove(fb, offset, length);
		onChanged();
	}

	@Override
	public void insertString(FilterBypass fb, int offset, String string,
			AttributeSet attr) throws BadLocationException {
		super.insertString(fb, offset, string, attr);
		onChanged();
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text,
			AttributeSet attrs) throws BadLocationException {
		super.replace(fb, offset, length, text, attrs);
		onChanged();
	}
	
	private void onChanged() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mf.restyle();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	class TextRange {
		int from;
		int to;
		
		TextRange(int from, int to) {
			this.from = from;
			this.to = to;
		}
	}
}
