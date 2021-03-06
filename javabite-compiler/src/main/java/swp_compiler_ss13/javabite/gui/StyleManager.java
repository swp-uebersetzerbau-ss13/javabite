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
	private void styleToken(TokenType tokenType, int start, int length) {
		// check properties file for tokentype key, if exist set defined color
		String color;
		if ((color = config.getProperty("syntaxHighlighting."+tokenType.toString().toLowerCase())) != null) {
			Style style = mf.editorPaneSourcecode.addStyle(tokenType.toString(), null);
			StyleConstants.setForeground(style, Color.decode(color));
			mf.doc.setCharacterAttributes(start, length, style, true);
		} else {
			Style style = mf.editorPaneSourcecode.addStyle("Black", null);
			StyleConstants.setForeground(style, Color.BLACK);
			mf.doc.setCharacterAttributes(start, length, style, true);
		}
	}
	
	/**
	 * Styles a special part of the sourcecode
	 */
	private void styleToken(TokenType tokenType, TextRange range) {
		// check properties file for tokentype key, if exist set defined color
		String color;
		if ((color = config.getProperty("syntaxHighlighting."+tokenType.toString().toLowerCase())) != null) {
			Style style = mf.editorPaneSourcecode.addStyle(tokenType.toString(), null);
			StyleConstants.setForeground(style, Color.decode(color));
			mf.doc.setCharacterAttributes(range.from, range.to-range.from, style, true);
		} else {
			Style style = mf.editorPaneSourcecode.addStyle("Black", null);
			StyleConstants.setForeground(style, Color.BLACK);
			mf.doc.setCharacterAttributes(range.from, range.to-range.from, style, true);
		}
	}
	
	void styleEditorText() {
		mf.fileManager.isProcessing = true;
		// backup content
		int cursorPos = mf.editorPaneSourcecode.getCaretPosition();
		String text = mf.editorPaneSourcecode.getText();
		// reset so base style
		mf.editorPaneSourcecode.setText("a");
		Style style = mf.editorPaneSourcecode.addStyle("Black", null);
		StyleConstants.setForeground(style, Color.BLACK);
		mf.doc.setCharacterAttributes(0, 1, mf.editorPaneSourcecode.getStyle("Black"), true);
		// restore content
		mf.editorPaneSourcecode.setText(text);
		mf.editorPaneSourcecode.setCaretPosition(cursorPos);
		
		int currentPosition = 0;
		int lastPosition = 0;
		for(Token t:tokens) {
//			currentPosition = text.indexOf(t.getValue(), lastPosition);
//			lastPosition = currentPosition + t.getValue().length();
//			styleToken(t.getTokenType(), currentPosition, t.getValue().length());
			styleToken(t.getTokenType(), getTextRange(t));
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
		mf.doc.setCharacterAttributes(range.from, range.to-range.from, attributes, true);
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
	
	TextRange getTextRange(Token token){
		int sl=token.getLine();
		int sc=token.getColumn();
		int ec=sc+((token.getValue() != null)?token.getValue().length():0);
		int curr_line=1;
		int curr_pos=0;
		while (curr_line != sl) {
			try {
				curr_pos = Utilities.getRowEnd(mf.editorPaneSourcecode,
						curr_pos)+1;
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

			curr_line++;
		}
		return new TextRange(curr_pos+sc-1, curr_pos+ec-1);
	}
	
	
	TextRange getTextRangeOf(List<Token> tokens) {
		int min=Integer.MAX_VALUE;
		int max=Integer.MIN_VALUE;
		// get the smallest interval, which includes the range of each token
		for (Token t : tokens){
			TextRange range=getTextRange(t);
			min=Math.min(range.from, min);
			max=Math.max(range.to, max);
		}
		return new TextRange(min, max);
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
