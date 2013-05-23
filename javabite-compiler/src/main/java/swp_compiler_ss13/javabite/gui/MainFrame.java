package swp_compiler_ss13.javabite.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
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
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import swp_compiler_ss13.common.backend.BackendException;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.javabite.ast.ASTJb;
import swp_compiler_ss13.javabite.compiler.JavabiteCompiler;
import swp_compiler_ss13.javabite.lexer.LexerJb;
import swp_compiler_ss13.javabite.parser.ParserJb;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	
	// class to setup styles for our sourcecode
	StyledDocument doc = (StyledDocument) new DefaultStyledDocument();
	
	//we need to communicate with the lexer to colorize tokens
	private LexerJb lexer;
	private ParserJb parser;
	private ASTJb ast;
	
	// Components
	JMenuBar menuBar;
	JMenu menuFile;
	JMenuItem menuFileOpen;
	JMenuItem menuFileSave;
	JMenuItem menuFileClose;
	JMenu menuVisual;
	JMenuItem menuVisualAst;
	JMenuItem menuVisualTac;
	JButton buttonRunCompile;
	JToolBar toolBar;
	JPanel panelLabel;
	JPanel panelProgressBar;
	JLabel toolBarLabel;
	JProgressBar progressBar;
	JSplitPane splitPane;
	JTextPane textPaneConsole;
	JTextPane textPaneLogs;
	JTabbedPane tabbedPaneLog;
	JTabbedPane tabbedPaneEditor;
	private static JTextPane editorPaneSourcode;
	
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
		
		menuBar = new JMenuBar();
		getContentPane().add(menuBar, BorderLayout.NORTH);
		
		menuFile = new JMenu("File");
		menuBar.add(menuFile);
		
		menuFileOpen = new JMenuItem("Open");
		menuFileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: open document
				//toolBarLabel.setText("Document opened.");
				//JFileChooser fc = new JFileChooser();
				//fc.showOpenDialog( null );
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(null);
				File file = null;
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = chooser.getSelectedFile(); // editorPaneSourcode
				}
				BufferedReader in = null;
				try {
					in = new BufferedReader(new FileReader(file));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String line = null;
				try {
					line = in.readLine();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Document doc = editorPaneSourcode.getDocument();
				try {
					doc.remove(0, doc.getLength()); // remove old content
				} catch (BadLocationException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				while(line != null){
				  try {
					  doc.insertString(doc.getLength(), line + "\n", null);
					  line = in.readLine();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				}
			}
		});
		menuFile.add(menuFileOpen);
		
		menuFileSave = new JMenuItem("Save");
		menuFileSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: save document
				JFileChooser jfc = new JFileChooser("./");  
				int returnVal = jfc.showSaveDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION)
		        {
		            File file = jfc.getSelectedFile();
		            // save the file.
		            BufferedWriter bw;
		            try {
		                bw = new BufferedWriter(new FileWriter(file));
		                bw.write(editorPaneSourcode.getText());
		                bw.flush();
		            }               
		            catch (IOException e1)
		            {
		                e1.printStackTrace();
		            }
		            //version++;

		        }
		        else
		        {
		            System.out.println("Save command cancelled by user. ");
		        }
				toolBarLabel.setText("Document saved.");
			}
		});
		menuFile.add(menuFileSave);
		
		menuFileClose = new JMenuItem("Close");
		menuFileClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: save documents
				System.exit(0);
			}
		});
		menuFile.add(menuFileClose);
		
		menuVisual = new JMenu("Visual");
		menuBar.add(menuVisual);
		
		menuVisualAst = new JMenuItem("AST");
		parser = new ParserJb();
		parser.setLexer(lexer);
		menuVisualAst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: show AST
				toolBarLabel.setText("Rendered AST.");
				ast = parser.getParsedAST();
			}
		});
		menuVisual.add(menuVisualAst);
		
		menuVisualTac = new JMenuItem("TAC");
		menuVisualTac.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: show TAC
				toolBarLabel.setText("Rendered TAC.");
			}
		});
		menuVisual.add(menuVisualTac);
		
		buttonRunCompile = new JButton("\u25BA");
		buttonRunCompile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: save sourcecode
				// TODO: compile sourcecode
				JavabiteCompiler compiler = new JavabiteCompiler();
				try {
					compiler.compile(null);
				} catch (IntermediateCodeGeneratorException | IOException | BackendException ex) {
					ex.printStackTrace();
				}
				toolBarLabel.setText("Sourcode compiled.");
			}
		});
		menuBar.add(buttonRunCompile);
		
		toolBar = new JToolBar();
		getContentPane().add(toolBar, BorderLayout.SOUTH);
		
		panelLabel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panelLabel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		toolBar.add(panelLabel);
		
		toolBarLabel = new JLabel("Compiler started");
		panelLabel.add(toolBarLabel);
		
		panelProgressBar = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panelProgressBar.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		toolBar.add(panelProgressBar);
		
		progressBar = new JProgressBar();
		panelProgressBar.add(progressBar);
		
		splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(250);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		tabbedPaneEditor = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setLeftComponent(tabbedPaneEditor);
		
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
		
		tabbedPaneEditor.addTab("Unknown", null, editorPaneSourcode, null);
		
		tabbedPaneLog = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setRightComponent(tabbedPaneLog);
		
		textPaneConsole = new JTextPane();
		textPaneConsole.setText("Console logs");
		tabbedPaneLog.addTab("Console", null, textPaneConsole, null);
		
		textPaneLogs = new JTextPane();
		textPaneLogs.setText("Other logs");
		tabbedPaneLog.addTab("Log", null, textPaneLogs, null);
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