package swp_compiler_ss13.javabite.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.Component;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JEditorPane;
import javax.swing.JDesktopPane;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import java.awt.FlowLayout;
import javax.swing.JTextPane;
import javax.swing.JScrollBar;
import javax.swing.JInternalFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.Panel;
import javax.swing.JTree;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;

import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.lexer.LexerJb;

public class MainFrame extends JFrame {
	
	private JPanel contentPane;
	
	//Class to setup styles for our sourcecode
	StyledDocument doc = (StyledDocument) new DefaultStyledDocument();
	
	private static JTextPane dtrpnSourcecode;
	
	//we need to communicate with the lexer to colorize tokens
	private LexerJb lexer;
	
	// Components
	JProgressBar progressBar;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("Javabite Compiler Pro 2013");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JMenuBar menuBar = new JMenuBar();
		getContentPane().add(menuBar, BorderLayout.NORTH);
		
		JMenu mnNewMenu = new JMenu("Datei");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Datei \u00F6ffnen");
		mnNewMenu.add(mntmNewMenuItem);
		
		JMenuItem mntmDateiSpeichern = new JMenuItem("Datei speichern");
		mnNewMenu.add(mntmDateiSpeichern);
		
		JMenuItem mntmSchlieen = new JMenuItem("Schlie\u00DFen");
		mnNewMenu.add(mntmSchlieen);
		
		JMenu mnVisualisierung = new JMenu("Visualisierung");
		menuBar.add(mnVisualisierung);
		
		JMenuItem mntmAst = new JMenuItem("AST");
		mnVisualisierung.add(mntmAst);
		
		JMenuItem mntmTac = new JMenuItem("TAC");
		mnVisualisierung.add(mntmTac);
		
		JButton btnNewButton = new JButton("Ausf\u00FChren");
		menuBar.add(btnNewButton);
		
		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		menuBar.add(progressBar);
		
		JToolBar toolBar = new JToolBar();
		getContentPane().add(toolBar, BorderLayout.SOUTH);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(250);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		JTabbedPane editorTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setLeftComponent(editorTabbedPane);
		
		lexer = new LexerJb();
		dtrpnSourcecode = new JTextPane(doc);
		dtrpnSourcecode.setText("enter your sourcecode here");
		//add listner for sourcecode colorization 
		dtrpnSourcecode.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				
				int index = 0;
				String text = dtrpnSourcecode.getText();
				List<Token> tokens = getTokenList(text);
				
				//text without breaklines and other special chars that jpane not counts
				String cuttedText = text.replace("\n", "");
				
				System.out.println(tokens.toString());
				
				Token current_token;
				int lastIndex = 0;
				for (int i = 0; i < tokens.size(); i++) {
					current_token = tokens.get(i);
					index = text.indexOf(current_token.getValue(), lastIndex);
					lastIndex = index + current_token.getValue().length();
					styleToken(current_token.getTokenType(), index , index + current_token.getValue().length());
					
				}

			}
		});
		
		editorTabbedPane.addTab("Unbekannt", null, dtrpnSourcecode, null);
		
		JTabbedPane logTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setRightComponent(logTabbedPane);
		
		JTextPane txtpnLogs = new JTextPane();
		txtpnLogs.setText("Konsole");
		logTabbedPane.addTab("Konsole", null, txtpnLogs, null);
		
		JTextPane textPane = new JTextPane();
		textPane.setText("Logs");
		logTabbedPane.addTab("Logs", null, textPane, null);
		
		// Events
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				progressBar.setVisible(true);
			}
		});
		
		
	}
	
	private List<Token> getTokenList(String text) {
		Token t;
		List<Token> tokens = new ArrayList<Token>();
		try {
			lexer.setSourceStream(new ByteArrayInputStream(text
					.getBytes("UTF-8")));

			do {
				t = lexer.getNextToken();
				tokens.add(t);
				
			} while (t.getTokenType() != TokenType.EOF);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tokens;

	}

	private void styleToken(TokenType tokenType, int start, int end) {
		System.out.println(start + " bis " + end + ": " + tokenType);
		if (tokenType == TokenType.STRING ) {

			javax.swing.text.Style style = dtrpnSourcecode.addStyle("Blue", null);
			StyleConstants.setForeground(style, Color.BLUE);
			doc.setCharacterAttributes(start, end, dtrpnSourcecode.getStyle("Blue"),
					true);
			
		} else if(tokenType == TokenType.IF || tokenType == TokenType.WHILE || tokenType == TokenType.DO || tokenType == TokenType.BREAK || tokenType == TokenType.RETURN || tokenType == TokenType.PRINT ) {
			javax.swing.text.Style style = dtrpnSourcecode.addStyle("Pink", null);
			StyleConstants.setForeground(style, new Color(145, 0, 85));
			doc.setCharacterAttributes(start, end, dtrpnSourcecode.getStyle("Pink"),
					true);
			
		} else if(tokenType == TokenType.COMMENT) {
			javax.swing.text.Style style = dtrpnSourcecode.addStyle("Gray", null);
			StyleConstants.setForeground(style, Color.GRAY);
			doc.setCharacterAttributes(start, end, dtrpnSourcecode.getStyle("Gray"),
					true);
		} else {
			javax.swing.text.Style style = dtrpnSourcecode.addStyle("Black", null);
			StyleConstants.setForeground(style, Color.BLACK);
			doc.setCharacterAttributes(start, end, dtrpnSourcecode.getStyle("Black"),
					true);
		}

	}
}
