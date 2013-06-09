package swp_compiler_ss13.javabite.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleConstants.CharacterConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.Utilities;
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
import swp_compiler_ss13.common.report.ReportLog;
import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.common.semanticAnalysis.SemanticAnalyser;
import swp_compiler_ss13.common.util.ModuleProvider;
import swp_compiler_ss13.javabite.ast.ASTJb;
import swp_compiler_ss13.javabite.gui.ast.ASTVisualizerJb;
import swp_compiler_ss13.javabite.gui.ast.fitted.KhaledGraphFrame;
import swp_compiler_ss13.javabite.lexer.LexerJb;
import swp_compiler_ss13.javabite.parser.ParserJb;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

import org.apache.commons.io.IOUtils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import javax.swing.JSeparator;

public class MainFrame extends JFrame implements ReportLog {
	
	private static final long serialVersionUID = 1673088367851101738L;
	
	private JPanel contentPane;
	
	// class to setup styles for our sourcecode
	StyledDocument doc = (StyledDocument) new DefaultStyledDocument();
	
	// we need to communicate with the lexer to colorize tokens
	private LexerJb lexer = new LexerJb();
	private ParserJb parser;
	private ASTJb ast;
	
	// components
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
	JTable tableReportLogs;
	DefaultTableModel modelReportLogs;
	JTabbedPane tabbedPaneLog;
	private static JTextPane editorPaneSourcode;
	
	// Files and file information
	Properties properties = new Properties();
	File openedFile = null;
	boolean fileChanged = false;
	SourecodeDocumentListener sourceCodeListener;
	FileNameExtensionFilter filter = new FileNameExtensionFilter("Sourcecode (.prog)", "prog");
	
	// undo and redo
	private Document editorPaneDocument;
	protected UndoManager undoManager = new UndoManager();
	private JButton undoButton;
	private JButton redoButton;
	private JScrollPane scrollPane;
	private JMenuItem mntmProperties;
	private JMenuItem mntmNew;
	private JScrollPane scrollPaneReportLogs;
	private JSeparator separator;
	private JSeparator separator_1;

	private boolean errorReported;
	
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
	 * Reads current editor code and writes it into given file
	 * */
	private void saveEditorContentIntoFile(File file) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(editorPaneSourcode.getText());
			bw.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Reads current file content and writes it into sourcecode editor
	 * */
	private void saveFileContentIntoEditor(File file) {
		// read out lines
		BufferedReader in = null;
		String line = null;
		try {
			in = new BufferedReader(new FileReader(file));
			line = in.readLine();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		// insert lines into source code editor
		Document doc = editorPaneSourcode.getDocument();
		try {
			doc.remove(0, doc.getLength()); // remove old content
			while (line != null) {
				doc.insertString(doc.getLength(), line + "\n", null);
				line = in.readLine();
			}
		} catch (BadLocationException | IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("Javabite Compiler - New File.prog");
		setSize(800, 600);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		menuBar = new JMenuBar();
		getContentPane().add(menuBar, BorderLayout.NORTH);
		
		menuFile = new JMenu("File");
		menuBar.add(menuFile);
		
		menuFileOpen = new JMenuItem("Open");
		menuFileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// unregister source code change listener
				editorPaneSourcode.getDocument().removeDocumentListener(sourceCodeListener);
				
				// file was not changed, thus just open new file
				if (!fileChanged) {
					
					// open file chooser
					JFileChooser chooser = new JFileChooser();
					chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
					chooser.setFileFilter(filter);
					int returnVal = chooser.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						openedFile = chooser.getSelectedFile();
						String fileName = openedFile.getName();
						setTitle("Javabite Compiler - " + fileName);
						saveFileContentIntoEditor(openedFile);
						toolBarLabel.setText("Document opened.");
					}
				} 
				// file was changed, ask what to do
				else {
					if (openedFile != null || fileChanged == true) {
						JFrame frame = new JFrame("Save");
						Object[] options = {"Cancel", "No", "Yes"};
						String fileName = (openedFile == null) ? "New File.prog" : openedFile.getName();
						int n = JOptionPane.showOptionDialog(frame,
						    "Save file \"" + fileName + "\"?\n",
						    "Save",
						    JOptionPane.YES_NO_CANCEL_OPTION,
						    JOptionPane.QUESTION_MESSAGE,
						    null,
						    options,
						    options[2]);
						// 'Yes' was selected
						if(n == 2) {
							if (openedFile == null) {
								// create and open the file chooser
								JFileChooser chooser = new JFileChooser();
								chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
								chooser.setFileFilter(filter);
								chooser.setSelectedFile(new File("New File.prog"));
								
								// save unchanged file
								int returnVal = chooser.showSaveDialog(null);
								if (returnVal == JFileChooser.APPROVE_OPTION) {
									openedFile = chooser.getSelectedFile();
									setTitle("Javabite Compiler - " + openedFile.getName());
									toolBarLabel.setText("Document saved.");
									saveEditorContentIntoFile(openedFile);
									fileChanged = false;
									
									// open file
									chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
									chooser.setFileFilter(filter);
									returnVal = chooser.showOpenDialog(null);
									if (returnVal == JFileChooser.APPROVE_OPTION) {
										openedFile = chooser.getSelectedFile();
										setTitle("Javabite Compiler - " + openedFile.getName());
										toolBarLabel.setText("Document opened.");
										saveFileContentIntoEditor(openedFile);
										fileChanged = false;
									}
								}
							} 
							else {
								// firstly save file
								saveEditorContentIntoFile(openedFile);
								setTitle("Javabite Compiler - " + openedFile.getName());
								toolBarLabel.setText("Document saved.");
								
								// now, display filechooser
								JFileChooser chooser = new JFileChooser();
								chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
								chooser.setFileFilter(filter);
								int returnVal = chooser.showOpenDialog(null);
								if (returnVal == JFileChooser.APPROVE_OPTION) {
									openedFile = chooser.getSelectedFile();
									setTitle("Javabite Compiler - " + openedFile.getName());
									saveFileContentIntoEditor(openedFile);
									toolBarLabel.setText("Document opened.");
									fileChanged = false;
								}
							}
						} 
						// 'No' was selected
						else if (n == 1) {
							// display file chooser
							JFileChooser chooser = new JFileChooser();
							chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
							chooser.setFileFilter(filter);
							int returnVal = chooser.showOpenDialog(null);
							if (returnVal == JFileChooser.APPROVE_OPTION) {
								openedFile = chooser.getSelectedFile();
								setTitle("Javabite Compiler - " + openedFile.getName());
								saveFileContentIntoEditor(openedFile);
								toolBarLabel.setText("Document opened.");
								fileChanged = false;
							}
						} 
						// 'Cancel' was selected
						else {
							return;
						}
					}
				}
				editorPaneSourcode.getDocument().addDocumentListener(sourceCodeListener);
			}
		});
		
		mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// file was changed, thus ask what to do
				if(fileChanged) {
					JFrame frame = new JFrame("Save");
					Object[] options = {"Cancel", "No", "Yes"};
					String fileName = (openedFile == null) ? "New File.prog" : openedFile.getName();
					int n = JOptionPane.showOptionDialog(frame,
					    "Save file \"" + fileName + "\"?\n",
					    "Save",
					    JOptionPane.YES_NO_CANCEL_OPTION,
					    JOptionPane.QUESTION_MESSAGE,
					    null,
					    options,
					    options[2]);
					// 'Yes' was selected
					if(n == 2) {
						if (openedFile == null) {
							// create and open the file chooser
							JFileChooser chooser = new JFileChooser();
							chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
							chooser.setFileFilter(filter);
							chooser.setSelectedFile(new File("New File.prog"));
							
							// save unchanged file
							int returnVal = chooser.showSaveDialog(null);
							if (returnVal == JFileChooser.APPROVE_OPTION) {
								openedFile = chooser.getSelectedFile();
								setTitle("Javabite Compiler - " + openedFile.getName());
								toolBarLabel.setText("Document saved.");
								saveEditorContentIntoFile(openedFile);
								fileChanged = false;
								
								// open new file
								openedFile = null;
								editorPaneSourcode.setText("");
								toolBarLabel.setText("New document opened.");
								setTitle("Javabite Compiler - New File.prog");
							}
						} 
						else {
							// firstly save file
							saveEditorContentIntoFile(openedFile);
							setTitle("Javabite Compiler - " + openedFile.getName());
							toolBarLabel.setText("Document saved.");
							
							// now, open new file
							openedFile = null;
							fileChanged = false;
							editorPaneSourcode.setText("");
							toolBarLabel.setText("New document opened.");
							setTitle("Javabite Compiler - New File.prog");
						}
					} 
					// 'No' was selected
					else if (n == 1) {
						// open new file
						openedFile = null;
						fileChanged = false;
						editorPaneSourcode.setText("");
						toolBarLabel.setText("New document opened.");
						setTitle("Javabite Compiler - New File.prog");
					} 
					// 'Cancel' was selected
					else {
						return;
					}
				}
				// file not changed, thus open new file
				else {
					openedFile = null;
					fileChanged = false;
					editorPaneSourcode.setText("");
					toolBarLabel.setText("New document opened.");
					setTitle("Javabite Compiler - New File.prog");
				}
			}
		});
		menuFile.add(mntmNew);
		menuFile.add(menuFileOpen);

		menuFileSave = new JMenuItem("Save");
		menuFileSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// file changed, thus save it
				if (fileChanged) {
					// file did not exist yet
					if (openedFile == null) {
						// open the file chooser
						JFileChooser chooser = new JFileChooser();
						chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
						chooser.setFileFilter(filter);
						chooser.setSelectedFile(new File("New File.prog"));
						
						// save unchanged file
						int returnVal = chooser.showSaveDialog(null);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							openedFile = chooser.getSelectedFile();
							setTitle("Javabite Compiler - " + openedFile.getName());
							toolBarLabel.setText("Document saved.");
							saveEditorContentIntoFile(openedFile);
							fileChanged = false;
						}
					}
					// file already exists, but was changed
					else {
						saveEditorContentIntoFile(openedFile);
						setTitle("Javabite Compiler - " + openedFile.getName());
						toolBarLabel.setText("Document saved.");
						fileChanged = false;
					}
				}
				// file was not changed
				else {
					JFrame frame = new JFrame();
					JOptionPane.showMessageDialog(frame, "There are no changes to save!");
				}
			}
		});
		menuFile.add(menuFileSave);
		
		menuFileClose = new JMenuItem("Close");
		menuFileClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// file was changed, thus ask what to do
				if(fileChanged) {
					JFrame frame = new JFrame("Save");
					Object[] options = {"Cancel", "No", "Yes"};
					String fileName = (openedFile == null) ? "New File.prog" : openedFile.getName();
					int n = JOptionPane.showOptionDialog(frame,
					    "Save file \"" + fileName + "\"?\n",
					    "Save",
					    JOptionPane.YES_NO_CANCEL_OPTION,
					    JOptionPane.QUESTION_MESSAGE,
					    null,
					    options,
					    options[2]);
					// 'Yes' was selected
					if(n == 2) {
						if (openedFile == null) {
							// create and open the file chooser
							JFileChooser chooser = new JFileChooser();
							chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
							chooser.setFileFilter(filter);
							chooser.setSelectedFile(new File("New File.prog"));
							
							// save unchanged file
							int returnVal = chooser.showSaveDialog(null);
							if (returnVal == JFileChooser.APPROVE_OPTION) {
								openedFile = chooser.getSelectedFile();
								setTitle("Javabite Compiler - " + openedFile.getName());
								toolBarLabel.setText("Document saved.");
								saveEditorContentIntoFile(openedFile);
								fileChanged = false;
								
								// close application
								System.exit(0);
							}
						} 
						else {
							// firstly save file
							saveEditorContentIntoFile(openedFile);
							setTitle("Javabite Compiler - " + openedFile.getName());
							toolBarLabel.setText("Document saved.");
							
							// close application
							System.exit(0);
						}
					} 
					// 'No' was selected
					else if (n == 1) {
						// close application
						System.exit(0);
					} 
					// 'Cancel' was selected
					else {
						return;
					}
				}
				// file not changed, thus close application
				else {
					System.exit(0);
				}
			}
		});
		
		mntmProperties = new JMenuItem("Properties");
		mntmProperties.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO: Implementation of property editor here
			}
		});
		
		separator_1 = new JSeparator();
		menuFile.add(separator_1);
		menuFile.add(mntmProperties);
		
		separator = new JSeparator();
		menuFile.add(separator);
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
				} catch (UnsupportedEncodingException ex) {
					ex.printStackTrace();
				}
				
				parser.setLexer(lexer);
				ast = parser.getParsedAST();
				ASTVisualizerJb visualizer = new ASTVisualizerJb();
				visualizer.visualizeAST(ast);
				JFrame frame = new JFrame();
				JScrollPane ast_frame=visualizer.getFrame();
				frame.setVisible(true);
				KhaledGraphFrame k= new KhaledGraphFrame();
				
				
			    frame.setSize(167*k.levelsCounter(ast), 55*k.maximumOfNodesInLevels());
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
				JFrame frame = new JFrame();
				JOptionPane.showMessageDialog(frame, "TAC visualization should be placed here! (see AST code as example!)");
				toolBarLabel.setText("Rendered TAC.");
			}
		});
		menuVisual.add(menuVisualTac);
		
		buttonRunCompile = new JButton("\u25BA");
		buttonRunCompile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compile(openedFile);
				progressBar.setValue(0);
			}
		});
		menuBar.add(buttonRunCompile);
		
		// undo button
		undoButton = new JButton("<-");
		undoButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (undoManager.canUndo()) {
					undoManager.undo();
					fileChanged = true;
				}
			}
		});
		menuBar.add(undoButton);
		
		// redo button
		redoButton = new JButton("->");
		redoButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (undoManager.canRedo()) {
					undoManager.redo();
					fileChanged = true;
				}
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
		
		// get properties for syntax highlighting
		try {
			// get the file path os-independent 
			// path is src\\main\\java\\swp_compiler_ss13\\javabite\\gui\\highlighting.properties
			// or src/main/java/swp_compiler_ss13/javabite/gui/highlighting.properties
			// respectively...   
			String sep=File.separator;
			String propPath="."+sep+"src"+sep+"main"+sep+"java"+sep+"swp_compiler_ss13"+sep+"javabite"+sep+"gui"+sep+"highlighting.properties";
			BufferedInputStream stream = new BufferedInputStream(new FileInputStream(propPath));
			properties.load(stream);
			stream.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		tabbedPaneLog = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setRightComponent(tabbedPaneLog);
		
		textPaneConsole = new JTextPane();
		textPaneConsole.setText("");
		tabbedPaneLog.addTab("Console", null, textPaneConsole, null);
		
		textPaneLogs = new JTextPane();
		textPaneLogs.setText("");
		tabbedPaneLog.addTab("Compiler Log", null, textPaneLogs, null);
		
		modelReportLogs = new DefaultTableModel();
		tableReportLogs = new JTable(modelReportLogs);
		tableReportLogs.setEnabled(false);
		tabbedPaneLog.addTab("Report Log", null, tableReportLogs, null);
		modelReportLogs.addColumn("Type");
		modelReportLogs.addColumn("Line");
		modelReportLogs.addColumn("Column");
		modelReportLogs.addColumn("Message");
		
		scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);
		
		// sourcecode with syntax highlighting
		editorPaneSourcode = new JTextPane(doc);
		scrollPane.setViewportView(editorPaneSourcode);
		editorPaneSourcode.setText("enter your sourcecode here");
		sourceCodeListener = new SourecodeDocumentListener(this);
		editorPaneSourcode.getDocument().addDocumentListener(sourceCodeListener);
		
		// tooltip
		editorPaneSourcode.addMouseMotionListener(new MouseAdapter() {
			  public void mouseMoved(MouseEvent e) {
				  Point loc = e.getPoint();
				  int pos = editorPaneSourcode.viewToModel(loc);
				  String text = editorPaneSourcode.getText();
				  
				  //dont do anything if the cursor is not hover an element
				  if (pos < text.length()) {
					  char cursor = 'x';
					  
					  //search as long we find a whitespace in our pos
					  int searchPos = 0;
					  int delimiterPos = -1;
					  while (searchPos < pos) {
						  if (text.charAt(searchPos) == ' ') {
							  delimiterPos = searchPos;
						  }
						  
						  searchPos++;
					  }
					  
					  //search next whitespace location 
					  while(searchPos < text.length()) {
						  if(text.charAt(searchPos) == ' ' || text.charAt(searchPos) == '\n' || text.charAt(searchPos) == '\0')
							  break;
						  
						  searchPos++;
					  }
					  
					  String mouseOverWord = text.substring(delimiterPos+1, searchPos);
					  
					  //get attribute of word, this is the token type
					  //we dont like to search for all tokens again
					  List<Token> tokens = getTokenList(mouseOverWord);
					  
					  //check if there is just 1 token type, if not there is no space between tokens and we have to identify what tokens is the target
					  if(tokens.size() == 1) {
						  editorPaneSourcode.setToolTipText(tokens.get(0).getTokenType().name());
					  } else {
						  int newPos = pos-delimiterPos;
						  int posCount = 0;
						  int tokenId = 0;
						  String val;
						  for(int i = 0; i < tokens.size(); i++) {
							  val = tokens.get(i).getValue();
							  posCount += val.length();
							  if(posCount >= newPos)
							  {
								  tokenId = i;
								  break;
							  }
						  }
						  editorPaneSourcode.setToolTipText(tokens.get(tokenId).getTokenType().name());
					  }
				  } else {
					  editorPaneSourcode.setToolTipText("");
				  }
			  }
		});
		
		// setup undo redo
		editorPaneSourcode.getDocument().addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				if (e.getEdit().getPresentationName().equals("L��schen") || e.getEdit().getPresentationName().equals("Hinzuf��gen")) {
					undoManager.addEdit(e.getEdit());
				}
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
	
	/**
	 * Returns a list of tokens for a given string
	 * */
	private List<Token> getTokenList(String text) {
		Token t;
		List<Token> tokens = new ArrayList<Token>();
		
		try {
			lexer.setSourceStream(new ByteArrayInputStream(text.getBytes("UTF-8")));
			do {
				t = lexer.getNextToken();
				tokens.add(t);
			} while (t.getTokenType() != TokenType.EOF);
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		
		return tokens;
	}
	
	/**
	 * Styles a special part of the sourcecode
	 */
	private void styleToken(TokenType tokenType, int start, int end) {
		//check properties file for tokentype key, if exist set defined color
		String color;
		if ((color = properties.getProperty(tokenType.toString())) != null) {
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
	 * Underlines wrongly typed tokens
	 * */
	private void underlineToken(int start, int end) {
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		attributes.addAttribute(CharacterConstants.Underline, Color.red);
		StyleConstants.setUnderline(attributes, true);
		StyledDocument doc = editorPaneSourcode.getStyledDocument();
		doc.setCharacterAttributes(start, end, attributes, true);
	}
	
	/**
	 * Styles the whole sourcode jtextpane
	 */
	private void styleEditorText() {
		String text = editorPaneSourcode.getText();
		
		//reset editor value to prevent some highlighting bugs
		int cursorPos = editorPaneSourcode.getCaretPosition();
		editorPaneSourcode.setText("");
		javax.swing.text.Style style = editorPaneSourcode.addStyle("Black", null);
		StyleConstants.setForeground(style, Color.BLACK);
		doc.setCharacterAttributes(0, 1, editorPaneSourcode.getStyle("Black"), true);
	
		editorPaneSourcode.setText(text);
		editorPaneSourcode.setCaretPosition(cursorPos);
		int index = 0;
		List<Token> tokens = getTokenList(text);
		
		Token current_token;
		int lastIndex = 0;
		for (int i = 0; i < tokens.size(); i++) {
			current_token = tokens.get(i);
			index = text.indexOf(current_token.getValue(), lastIndex);
			lastIndex = index + current_token.getValue().length();
			styleToken(current_token.getTokenType(), index , current_token.getValue().length());
			if (current_token.getTokenType() == TokenType.NOT_A_TOKEN) {
				underlineToken(index , current_token.getValue().length());
			}
		}
	}
	
	/**
	 * Main entry point for the compile process
	 * */
	private void compile(File file) {
		try {
			boolean canCompiled = false;
			if (fileChanged) {
				JFrame frame = new JFrame("Save");
				Object[] options = {"Cancel", "No", "Yes"};
				String fileName = (file == null) ? "New File.prog" : file.getName();
				int n = JOptionPane.showOptionDialog(frame,
				    "Sourcecode cannot be compiled, until it is saved.\nSave file \"" + fileName + "\"?\n",
				    "Save",
				    JOptionPane.YES_NO_CANCEL_OPTION,
				    JOptionPane.QUESTION_MESSAGE,
				    null,
				    options,
				    options[2]);
				if (n == 2) {			// 'Yes' was selected
					if (file == null) {
						// create and open the file chooser
						JFileChooser chooser = new JFileChooser();
						chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
						chooser.setFileFilter(filter);
						chooser.setSelectedFile(new File("New File.prog"));
						
						// save unchanged file
						int returnVal = chooser.showSaveDialog(null);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							openedFile = chooser.getSelectedFile();
							file = openedFile;
							setTitle("Javabite Compiler - " + file.getName());
							toolBarLabel.setText("Document saved.");
							saveEditorContentIntoFile(openedFile);
							fileChanged = false;
							
							// sourcecode can now be compiled
							canCompiled = true;
						}
					} else {			// firstly save file
						saveEditorContentIntoFile(openedFile);
						setTitle("Javabite Compiler - " + file.getName());
						toolBarLabel.setText("Document saved.");
						
						// sourcecode can now be compiled
						canCompiled = true;
					}
				} else if (n == 1) {	// 'No' was selected
					if (file == null) {
						canCompiled = false;
						frame = new JFrame();
						JOptionPane.showMessageDialog(frame, "Sourcecode not saved into a file. Cannot compile!");
					} else {
						canCompiled = true;
					}
				} else {				// 'Cancel' was selected
					canCompiled = false;
				}
			} else {
				if (file == null) {		// file not changed, but doesn't exist
					canCompiled = false;
					JFrame frame = new JFrame();
					JOptionPane.showMessageDialog(frame, "Sourcecode not saved into a file. Cannot compile!");
				} else {
					canCompiled = true;	// file not changed, but it exists
				}
			}
			
			if (canCompiled) {
				textPaneLogs.setText("Compiler started.");
				progressBar.setValue(0);
				progressBar.setEnabled(true);
				Lexer lexer = ModuleProvider.getLexerInstance();
				Parser parser = ModuleProvider.getParserInstance();
				parser.setLexer(lexer);
				parser.setReportLog(this);
				SemanticAnalyser semanticAnalyser=ModuleProvider.getSemanticAnalyserInstance();
				semanticAnalyser.setReportLog(this);
				IntermediateCodeGenerator codegen = ModuleProvider.getCodeGeneratorInstance();
				Backend backend = ModuleProvider.getBackendInstance();
				
				for (int i = 0; i < modelReportLogs.getRowCount(); i++) {
					modelReportLogs.removeRow(i);
				}
				
				parser.setReportLog(this);
				
				progressBar.setValue(10);
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
					return;
				}
				
				// get the name of file without extension
				progressBar.setValue(20);
				textPaneLogs.setText(textPaneLogs.getText() + "\nGetting file.");
				toolBarLabel.setText("Getting file content.");
				String sourceBaseName = file.getName();
				int lastDot = sourceBaseName.lastIndexOf(".");
				lastDot = lastDot > -1 ? lastDot : sourceBaseName.length();
				sourceBaseName = sourceBaseName.substring(0,lastDot);
				
				toolBarLabel.setText("Compiling sourcecode.");
				progressBar.setValue(30);
				errorReported = false;
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
				semanticAnalyser.analyse(ast);
				if (errorReported) {
					textPaneLogs.setText(textPaneLogs.getText() + "\nSourcecode could not compile.");
					toolBarLabel.setText("Sourcecode could not compile.");
					return;
				}
				
				textPaneLogs.setText(textPaneLogs.getText() + "\nCreating quadruples.");
				progressBar.setValue(60);
				List<Quadruple> quadruples = codegen.generateIntermediateCode(ast);
				progressBar.setValue(70);
				Map<String, InputStream> results = backend.generateTargetCode(sourceBaseName, quadruples);
				progressBar.setValue(80);
				textPaneLogs.setText(textPaneLogs.getText() + "\nGenerate target code finished.");
				for (Entry<String,InputStream> e:results.entrySet()) {
					textPaneLogs.setText(textPaneLogs.getText() + "\nWrite output file: " + e.getKey());
					File outFile = new File(e.getKey());
					FileOutputStream fos = new FileOutputStream(outFile);
					IOUtils.copy(e.getValue(), fos);
					fos.close();
					String line;
					textPaneLogs.setText(textPaneLogs.getText() + "\nRunning application.");
					Process p = Runtime.getRuntime().exec("java " + outFile.getAbsolutePath());
					BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
					while ((line = input.readLine()) != null) {
						textPaneConsole.setText(textPaneConsole.getText() + "\n" + line + ".");
					}
					input.close();
				}
				
				toolBarLabel.setText("File compiled.");
				progressBar.setValue(100);
				progressBar.setEnabled(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			textPaneLogs.setText(textPaneLogs.getText() + "\nCompilation failed.");
			toolBarLabel.setText("Compilation failed.");
			progressBar.setValue(0);
			progressBar.setEnabled(false);
		}
	}
	
	@Override
	public void reportWarning(ReportType type, List<Token> tokens, String message) {
		errorReported=true;
		modelReportLogs.addRow(new Object[] { type, tokens.get(0).getLine(), tokens.get(0).getColumn(), message });
	}
	
	@Override
	public void reportError(ReportType type, List<Token> tokens, String message) {
		errorReported=true;
		modelReportLogs.addRow(new Object[] { type, tokens.get(0).getLine(), tokens.get(0).getColumn(), message });
	}
	
	 public static int getRow(int pos, JTextComponent editor) {
	        int rn = (pos==0) ? 1 : 0;
	        try {
	            int offs=pos;
	            while( offs>0) {
	                offs=Utilities.getRowStart(editor, offs)-1;
	                rn++;
	            }
	        } catch (BadLocationException e) {
	            e.printStackTrace();
	        }
	        return rn;
	    }

    public static int getColumn(int pos, JTextComponent editor) {
        try {
            return pos-Utilities.getRowStart(editor, pos)+1;
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
 }