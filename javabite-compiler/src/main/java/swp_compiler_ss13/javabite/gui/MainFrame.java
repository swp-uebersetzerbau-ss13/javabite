package swp_compiler_ss13.javabite.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.backend.Backend;
import swp_compiler_ss13.common.backend.BackendException;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.ir.IntermediateCodeGenerator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.lexer.Lexer;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.common.parser.Parser;
import swp_compiler_ss13.common.parser.ReportLog;
import swp_compiler_ss13.common.util.ModuleProvider;
import swp_compiler_ss13.javabite.ast.ASTJb;
import swp_compiler_ss13.javabite.compiler.JavabiteCompiler;
import swp_compiler_ss13.javabite.gui.ast.ASTVisualizerJb;
import swp_compiler_ss13.javabite.lexer.LexerJb;
import swp_compiler_ss13.javabite.parser.ParserJb;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

import org.apache.commons.io.IOUtils;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame implements ReportLog {

	private JPanel contentPane;
	
	// class to setup styles for our sourcecode
	StyledDocument doc = (StyledDocument) new DefaultStyledDocument();
	
	//we need to communicate with the lexer to colorize tokens
	private LexerJb lexer = new LexerJb();
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
	private static JTextPane editorPaneSourcode;
	
	// Files
	Properties properties = new Properties();
	File openedFile;
	
	// undo and redo
	private Document editorPaneDocument;
	protected UndoManager undoManager = new UndoManager();
	private JButton undoButton;
	private JButton redoButton;
	private JScrollPane scrollPane;
	
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
		setTitle("Javabite Compiler - Unknown");
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
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					openedFile = chooser.getSelectedFile(); // editorPaneSourcode
				}
				String fileName = openedFile.getName();
				setTitle("Javabite Compiler - " + fileName);
				BufferedReader in = null;
				try {
					in = new BufferedReader(new FileReader(openedFile));
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
		menuVisualAst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = editorPaneSourcode.getText();
				try {
					lexer.setSourceStream(new ByteArrayInputStream(text.getBytes("UTF-8")));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				parser.setLexer(lexer);
				ast = parser.getParsedAST();
				ASTVisualizerJb visualizer = new ASTVisualizerJb();
				visualizer.visualizeAST(ast);
				JFrame frame=new JFrame();
				JScrollPane ast_frame=visualizer.getFrame();
				frame.setVisible(true);
				frame.setSize(800, 600);
				frame.getContentPane().add(ast_frame);
				frame.setVisible(true);
				toolBarLabel.setText("Rendered AST.");
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
				try {
					compile(openedFile);
				} catch (IntermediateCodeGeneratorException | IOException | BackendException ex) {
					ex.printStackTrace();
				}
			}
		});
		menuBar.add(buttonRunCompile);
		
		//Undo Button
		undoButton = new JButton("<-");
		undoButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(undoManager.canUndo())
					undoManager.undo();
			}
		});
		menuBar.add(undoButton);
		
		//Redo Button
		redoButton = new JButton("->");
		redoButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(undoManager.canRedo())
					undoManager.redo();
				styleEditorText();
			}
		});
		menuBar.add(redoButton);
		
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
		
		lexer = new LexerJb();
		
		//get properties for syntax highlighting
		//read properties
		
		try {
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream("src\\main\\java\\swp_compiler_ss13\\javabite\\gui\\highlighting.properties"));
			properties.load(stream);
			stream.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		tabbedPaneLog = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setRightComponent(tabbedPaneLog);
		
		textPaneConsole = new JTextPane();
		textPaneConsole.setText("");
		tabbedPaneLog.addTab("Console", null, textPaneConsole, null);
		
		textPaneLogs = new JTextPane();
		textPaneLogs.setText("");
		tabbedPaneLog.addTab("Log", null, textPaneLogs, null);
		
		scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);
		
		
		//sourcecode with syntax highlighting
		editorPaneSourcode = new JTextPane(doc);
		scrollPane.setViewportView(editorPaneSourcode);
		editorPaneSourcode.setText("enter your sourcecode here");
		
		//setup undo redo
		editorPaneSourcode.getDocument().addUndoableEditListener(
				new UndoableEditListener() {
					public void undoableEditHappened(UndoableEditEvent e) {
						if(e.getEdit().getPresentationName().equals("L�schen") || e.getEdit().getPresentationName().equals("Hinzuf�gen")) 
							undoManager.addEdit(e.getEdit());
					}
				});
		
				
				// add listener for sourcecode colorization 
				editorPaneSourcode.addKeyListener(new KeyAdapter() {
					@Override
					public void keyReleased(KeyEvent arg0) {
						styleEditorText();
					} 
					
				});
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

	/**
	 * Styles a special part of the sourccode
	 */
	private void styleToken(TokenType tokenType, int start, int end) {
		
		//check properties file for tokentype key, if exist set defined color
		String color;
		if((color = properties.getProperty(tokenType.toString())) != null) {
			javax.swing.text.Style style = editorPaneSourcode.addStyle(tokenType.toString(), null);
			StyleConstants.setForeground(style, new Color(Integer.parseInt(color, 16)+0xFF000000));
			doc.setCharacterAttributes(start, end, editorPaneSourcode.getStyle(tokenType.toString()), true);
		
			
		} else {
			javax.swing.text.Style style = editorPaneSourcode.addStyle("Black", null);
			StyleConstants.setForeground(style, Color.BLACK);
			doc.setCharacterAttributes(start, end, editorPaneSourcode.getStyle("Black"), true);

		}
	}
	
	/**
	 * Styles the whole sourcode jtextpane
	 */
	private void styleEditorText() {
		String text = editorPaneSourcode.getText();
		
		int index = 0;
		List<Token> tokens = getTokenList(text);
		
		Token current_token;
		int lastIndex = 0;
		for (int i = 0; i < tokens.size(); i++) {
			current_token = tokens.get(i);
			index = text.indexOf(current_token.getValue(), lastIndex);
			lastIndex = index + current_token.getValue().length();
			styleToken(current_token.getTokenType(), index , current_token.getValue().length());
		}
	}
	
	private void compile(File file) throws IntermediateCodeGeneratorException, IOException, BackendException {
		textPaneLogs.setText("Compiler started.");
		Lexer lexer = ModuleProvider.getLexerInstance();
		Parser parser = ModuleProvider.getParserInstance();
		parser.setLexer(lexer);
		parser.setReportLog(this);
		IntermediateCodeGenerator codegen = ModuleProvider.getCodeGeneratorInstance();
		Backend backend = ModuleProvider.getBackendInstance();
		
		textPaneLogs.setText(textPaneLogs.getText() + "\nChecking compiler setup.");
		boolean setupOk = true;
		if (lexer == null) {
			setupOk = false;
		}
		if (parser == null) {
			setupOk = false;
		}
		if (codegen == null) {
			setupOk = false;
		}
		if (backend == null) {
			setupOk = false;
		}

		if (setupOk) {
			System.out.println("Compiler is ready to start");
		} else {
			System.out.println("Compiler could not load all need modules");
		}
		
		// get the name of file without extension
		textPaneLogs.setText(textPaneLogs.getText() + "\nGetting file.");
		toolBarLabel.setText("Getting file content.");
		String sourceBaseName = file.getName();
		int lastDot = sourceBaseName.lastIndexOf(".");
		lastDot = lastDot > -1 ? lastDot : sourceBaseName.length();
		sourceBaseName = sourceBaseName.substring(0,lastDot);

		toolBarLabel.setText("Compiling sourcecode.");
		boolean errorReported = false;
		lexer.setSourceStream(new FileInputStream(file));
		toolBarLabel.setText("Building AST.");
		textPaneLogs.setText(textPaneLogs.getText() + "\nStarting Lexer.");
		textPaneLogs.setText(textPaneLogs.getText() + "\nStarting Parser.");
		textPaneLogs.setText(textPaneLogs.getText() + "\nCreating AST.");
		AST ast = parser.getParsedAST();
		
		if (errorReported) {
			textPaneLogs.setText(textPaneLogs.getText() + "\nSourcecode could not compile.");
			toolBarLabel.setText("Sourcecode could not compile.");
			return;
		}
		
		textPaneLogs.setText(textPaneLogs.getText() + "\nCreating quadruples.");
		List<Quadruple> quadruples = codegen.generateIntermediateCode(ast);
		Map<String, InputStream> results = backend.generateTargetCode(sourceBaseName, quadruples);
		textPaneLogs.setText(textPaneLogs.getText() + "\nGenerate target code finished.");
		for(Entry<String,InputStream> e:results.entrySet()) {
			textPaneLogs.setText(textPaneLogs.getText() + "\nWrite output file: " + e.getKey());
			File outFile = new File(e.getKey());
			FileOutputStream fos = new FileOutputStream(outFile);
			IOUtils.copy(e.getValue(), fos);
			fos.close();
		}
	}
	@Override
	public void reportError(String text, Integer line, Integer column, String message) {
		// TODO Auto-generated method stub
	}

	
}