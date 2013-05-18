package swp_compiler_ss13.javabite.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.compiler.JavabiteCompiler;
import swp_compiler_ss13.javabite.lexer.LexerJb;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	
	//Class to setup styles for our sourcecode
	StyledDocument doc = (StyledDocument) new DefaultStyledDocument();
	
	private static JTextPane editorPaneSourcode;
	
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
		setTitle("Javabite Compiler");
		setSize(700, 500);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JMenuBar menuBar = new JMenuBar();
		getContentPane().add(menuBar, BorderLayout.NORTH);
		
		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);
		
		JMenuItem menuFileOpen = new JMenuItem("Open");
		menuFileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: open document
			}
		});
		menuFile.add(menuFileOpen);
		
		JMenuItem menuFileSave = new JMenuItem("Save");
		menuFileSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: save document
			}
		});
		menuFile.add(menuFileSave);
		
		JMenuItem menuFileClose = new JMenuItem("Close");
		menuFileClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: save documents
				System.exit(0);
			}
		});
		menuFile.add(menuFileClose);
		
		JMenu menuVisual = new JMenu("Visual");
		menuBar.add(menuVisual);
		
		JMenuItem menuVisualAst = new JMenuItem("AST");
		menuVisualAst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: show AST
			}
		});
		menuVisual.add(menuVisualAst);
		
		JMenuItem menuVisualTac = new JMenuItem("TAC");
		menuVisualTac.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: show TAC
			}
		});
		menuVisual.add(menuVisualTac);
		
		JButton buttonRunCompile = new JButton("\u25BA");
		buttonRunCompile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				progressBar.setVisible(true);
				// TODO: save sourcecode
				// TODO: compile sourcecode
				JavabiteCompiler compiler = new JavabiteCompiler();
				try {
					compiler.compile(null);
				} catch (IntermediateCodeGeneratorException | IOException ex) {
					ex.printStackTrace();
				}
				progressBar.setVisible(false);
			}
		});
		menuBar.add(buttonRunCompile);
		
		JToolBar toolBar = new JToolBar();
		getContentPane().add(toolBar, BorderLayout.SOUTH);
		
		JPanel panelLabel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panelLabel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		toolBar.add(panelLabel);
		
		JLabel toolBarLabel = new JLabel("Compiler started");
		panelLabel.add(toolBarLabel);
		
		JPanel panelProgressBar = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelProgressBar.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		toolBar.add(panelProgressBar);
		
		progressBar = new JProgressBar();
		panelProgressBar.add(progressBar);
		progressBar.setVisible(false);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(250);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		JTabbedPane editorTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setLeftComponent(editorTabbedPane);
		
		lexer = new LexerJb();
		editorPaneSourcode = new JTextPane(doc);
		editorPaneSourcode.setText("enter your sourcecode here");
		// add listener for sourcecode colorization 
		editorPaneSourcode.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				int index = 0;
				String text = editorPaneSourcode.getText();
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
		
		editorTabbedPane.addTab("Unknown", null, editorPaneSourcode, null);
		
		JTabbedPane logTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setRightComponent(logTabbedPane);
		
		JTextPane textPaneConsole = new JTextPane();
		textPaneConsole.setText("Console logs");
		logTabbedPane.addTab("Console", null, textPaneConsole, null);
		
		JTextPane textPaneLogs = new JTextPane();
		textPaneLogs.setText("Other logs");
		logTabbedPane.addTab("Log", null, textPaneLogs, null);
	}
	
	private List<Token> getTokenList(String text) {
		Token t;
		List<Token> tokens = new ArrayList<Token>();
		
		try {
			lexer.setSourceStream(new ByteArrayInputStream(text.getBytes("UTF-8")));
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
			javax.swing.text.Style style = editorPaneSourcode.addStyle("Blue", null);
			StyleConstants.setForeground(style, Color.BLUE);
			doc.setCharacterAttributes(start, end, editorPaneSourcode.getStyle("Blue"), true);
		} else if(tokenType == TokenType.IF || tokenType == TokenType.WHILE || tokenType == TokenType.DO || tokenType == TokenType.BREAK || tokenType == TokenType.RETURN || tokenType == TokenType.PRINT ) {
			javax.swing.text.Style style = editorPaneSourcode.addStyle("Pink", null);
			StyleConstants.setForeground(style, new Color(145, 0, 85));
			doc.setCharacterAttributes(start, end, editorPaneSourcode.getStyle("Pink"), true);
		} else if(tokenType == TokenType.COMMENT) {
			javax.swing.text.Style style = editorPaneSourcode.addStyle("Gray", null);
			StyleConstants.setForeground(style, Color.GRAY);
			doc.setCharacterAttributes(start, end, editorPaneSourcode.getStyle("Gray"), true);
		} else {
			javax.swing.text.Style style = editorPaneSourcode.addStyle("Black", null);
			StyleConstants.setForeground(style, Color.BLACK);
			doc.setCharacterAttributes(start, end, editorPaneSourcode.getStyle("Black"), true);
		}
	}
}