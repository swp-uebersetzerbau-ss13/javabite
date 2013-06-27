package swp_compiler_ss13.javabite.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.Utilities;

import org.apache.commons.io.IOUtils;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.lexer.TokenType;
import swp_compiler_ss13.common.report.ReportLog;
import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.compiler.AbstractJavabiteCompiler;
import swp_compiler_ss13.javabite.config.Configurable;
import swp_compiler_ss13.javabite.config.JavabiteConfig;
import swp_compiler_ss13.javabite.gui.ast.ASTVisualizerJb;
import swp_compiler_ss13.javabite.gui.ast.fitted.KhaledGraphFrame;
import swp_compiler_ss13.javabite.gui.config.SettingsPanel;
import swp_compiler_ss13.javabite.gui.tac.TacVisualizerJb;
import swp_compiler_ss13.javabite.runtime.JavaClassProcess;

public class MainFrame extends JFrame implements ReportLog, Configurable {
	
	private static final long serialVersionUID = 1673088367851101738L;
	
	// class to setup styles for our sourcecode
	StyledDocument doc = (StyledDocument) new DefaultStyledDocument();
	
	private boolean astVisualizationRequested = false;
	private boolean tacVisualizationRequested = false;
	
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
	JTextPane editorPaneSourcecode;
	
	// get properties for syntax highlighting
	JavabiteConfig properties = JavabiteConfig.getDefaultConfig();
	
	// file for opened sourcecode and changes listener
	File openedFile = null;
	boolean fileChanged = false;
	SourecodeDocumentListener sourceCodeListener;
	FileManager fileManager;
	FileNameExtensionFilter filter = new FileNameExtensionFilter("Sourcecode (.prog)", "prog");
	
	// undo and redo
	final ClassLoader loader = MainFrame.class.getClassLoader();
	protected UndoCostumManager undoManager;
	private JButton undoButton;
	private JButton redoButton;
	private JScrollPane scrollPane;
	private JMenuItem mntmProperties;
	private JMenuItem mntmNew;
	private JSeparator separator;
	private JSeparator separator_1;

	private boolean errorReported;
	private GuiCompiler guiCompiler;
	
	private String lastTooltipStr;
	private JScrollPane scrollPaneReportLogs;
	
	/**
	 * Reads current editor code and writes it into given file
	 * */
	public void saveEditorContentIntoFile(File file) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			bw.write(editorPaneSourcecode.getText());
			bw.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		String fileName = "";
		if (openedFile == null) {
			fileName = "New Program.prog";
		} else {
			fileName = openedFile.getName();
		}
		setTitle("Javabite Compiler - " + fileName);
		toolBarLabel.setText("Document saved.");
	}
	
	/**
	 * Reads current file content and writes it into sourcecode editor
	 * */
	public void saveFileContentIntoEditor(File file) {
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
		Document doc = editorPaneSourcecode.getDocument();
		try {
			doc.remove(0, doc.getLength()); // remove old content
			while (line != null) {
				doc.insertString(doc.getLength(), line + "\n", null);
				line = in.readLine();
			}
			// remove wrongly inserted last newline
			if (doc.getText(0, doc.getLength()).endsWith("\n")) {
				doc.remove(doc.getLength() - 1, 1);
			}
		} catch (BadLocationException | IOException ex) {
			ex.printStackTrace();
		}
		styleEditorText();
	}
	
	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setTitle("Javabite Compiler - New File.prog");
		setSize(800, 600);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		fileManager = new FileManager(this);
		
		menuBar = new JMenuBar();
		getContentPane().add(menuBar, BorderLayout.NORTH);
		
		menuFile = new JMenu("File");
		menuBar.add(menuFile);
		
		menuFileOpen = new JMenuItem("Open");
		menuFileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editorPaneSourcecode.getDocument().removeDocumentListener(sourceCodeListener);
				if (!fileChanged) { fileManager.openFileDialog(openedFile, false); }
				else {
					if (openedFile != null || fileChanged == true) {
						int n = fileManager.saveOrNotDialog(openedFile);
						if (n == 2) {
							if (openedFile == null) {
								int returnValue = fileManager.saveFileDialog(openedFile);
								fileChanged = false;
								if(returnValue == JFileChooser.APPROVE_OPTION) {
									fileManager.openFileDialog(openedFile, false);
									fileChanged = false;
								}
							} else {
								saveEditorContentIntoFile(openedFile);
								fileManager.openFileDialog(openedFile, false);
								fileChanged = false;
							}
						} else if (n == 1) {
							fileManager.openFileDialog(openedFile, false);
							fileChanged = false;
						} else {
							return;
						}
					}
				}
				editorPaneSourcecode.getDocument().addDocumentListener(sourceCodeListener);
			}
		});
		
		mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// file was changed, thus ask what to do
				if (fileChanged) {
					int n = fileManager.saveOrNotDialog(openedFile);
					
					if (n == 2) {
						if (openedFile == null) {
							int returnValue = fileManager.saveFileDialog(openedFile);
							fileChanged = false;
							if(returnValue == JFileChooser.APPROVE_OPTION) {
								openedFile = null;
								editorPaneSourcecode.setText("");
								toolBarLabel.setText("New document opened.");
								setTitle("Javabite Compiler - New File.prog");
							}
						} else {
							saveEditorContentIntoFile(openedFile);
							fileManager.openNewFile(openedFile);
							fileChanged = false;
						}
					} else if (n == 1) {
						fileManager.openNewFile(openedFile);
						fileChanged = false;
					} else {
						return;
					}
				} else { 
					// file not changed, thus open new file
					fileManager.openNewFile(openedFile);
					openedFile = null;
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
					if (openedFile == null) {
						// file did not exist yet
						fileManager.saveFileDialog(openedFile);
						fileChanged = false;
					} else { 
						// file already exists, but was changed
						saveEditorContentIntoFile(openedFile);
						setTitle("Javabite Compiler - " + openedFile.getName());
						toolBarLabel.setText("Document saved.");
						fileChanged = false;
					}
				} else { 
					// file was not changed
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
				if (fileChanged) {
					int n = fileManager.saveOrNotDialog(openedFile);
					
					if (n == 2) { 
						// "Yes" was selected
						if (openedFile == null) {
							
							int returnValue = fileManager.saveFileDialog(openedFile);
							fileChanged = false;
							if(returnValue == JFileChooser.APPROVE_OPTION) {
								// close application
								System.exit(0);
							}
						} else {
							// firstly save file
							saveEditorContentIntoFile(openedFile);
							System.exit(0);
						}
					} else if (n == 1) { 
						// "No" was selected
						System.exit(0);
					} else { 
						// "Cancel" was selected
						return;
					}
				} else { // file not changed, thus close application
					System.exit(0);
				}
			}
		});
		
		mntmProperties = new JMenuItem("Properties");
		mntmProperties.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showSettingsPanel();
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
		menuVisualAst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				requestAstVisualization();
			}
		});
		menuVisual.add(menuVisualAst);
		
		menuVisualTac = new JMenuItem("TAC");
		menuVisualTac.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				requestTacVisualization();
			}
		});
		menuVisual.add(menuVisualTac);
		
		buttonRunCompile = new JButton("\u25BA");
		buttonRunCompile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compile();
			}
		});
		menuBar.add(buttonRunCompile);
		
		// undo button
		
		Icon icon = null;
		try {
			icon = new ImageIcon(IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream("images/undo-icon.png")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		undoButton = new JButton("", icon);
		undoButton.setMargin(new Insets(0, 0, 0, 0));
		undoButton.setBorder(null);
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
		try {
			icon = new ImageIcon(IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream("images/redo-icon.png")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		redoButton = new JButton("", icon);
		redoButton.setMargin(new Insets(0, 0, 0, 0));
		redoButton.setBorder(null);
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
		
		tabbedPaneLog = new JTabbedPane(JTabbedPane.TOP);
		splitPane.setRightComponent(tabbedPaneLog);
		
		textPaneConsole = new JTextPane();
		textPaneConsole.setText("");
		tabbedPaneLog.addTab("Console", null, textPaneConsole, null);
		
		textPaneLogs = new JTextPane();
		textPaneLogs.setText("");
		tabbedPaneLog.addTab("Compiler Log", null, textPaneLogs, null);
		
		modelReportLogs = new DefaultTableModel();
		
		scrollPaneReportLogs = new JScrollPane();
		tabbedPaneLog.addTab("Report Log", null, scrollPaneReportLogs, null);
		
		tableReportLogs = new JTable(modelReportLogs);
		tableReportLogs.setEnabled(false);
		modelReportLogs.addColumn("");
		modelReportLogs.addColumn("Type");
		modelReportLogs.addColumn("Line");
		modelReportLogs.addColumn("Column");
		modelReportLogs.addColumn("Message");
		tableReportLogs.setFillsViewportHeight(true);
		scrollPaneReportLogs.setViewportView(tableReportLogs);
		
		scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);
		
		// sourcecode with syntax highlighting
		editorPaneSourcecode = new JTextPane(doc);
		scrollPane.setViewportView(editorPaneSourcecode);
		editorPaneSourcecode.setText("enter your sourcecode here");
		sourceCodeListener = new SourecodeDocumentListener(this);
		editorPaneSourcecode.getDocument().addDocumentListener(sourceCodeListener);
		
		// undo redo manager
		undoManager = new UndoCostumManager(editorPaneSourcecode);
		
		// tooltip
		ToolTipManager.sharedInstance().setDismissDelay(5000);
		editorPaneSourcecode.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				Point loc = e.getPoint();
				int pos = editorPaneSourcecode.viewToModel(loc);
				String text = editorPaneSourcecode.getText();
				
				// dont do anything if the cursor is not hover an element
				if (pos < text.length()) {
					//search as long we find a whitespace in our pos
					int searchPos = 0;
					int delimiterPos = -1;
					while (searchPos < pos) {
						if (text.charAt(searchPos) == ' ') {
							delimiterPos = searchPos;
						}
						
						searchPos++;
					}
					
					// search next whitespace location 
					while (searchPos < text.length()) {
						if (text.charAt(searchPos) == ' ' || text.charAt(searchPos) == '\n' || text.charAt(searchPos) == '\0') {
							break;
						}
						
						searchPos++;
					}
					
					String mouseOverWord = text.substring(delimiterPos+1, searchPos);
					
					// get attribute of word, this is the token type
					// we dont like to search for all tokens again
					List<Token> tokens = guiCompiler.getTokenListFor(mouseOverWord);
					
					// check if there is just 1 token type, if not there is no space between tokens and we have to identify what tokens is the target
					if (tokens.size() == 1) {
						String mouseOverTokenStr = tokens.get(0).getTokenType().name();
						if(!mouseOverTokenStr.equals(lastTooltipStr)) {
							editorPaneSourcecode.setToolTipText(mouseOverTokenStr);
						}
					} else {
						int newPos = pos-delimiterPos;
						int posCount = 0;
						int tokenId = 0;
						String val;
						for (int i = 0; i < tokens.size(); i++) {
							val = tokens.get(i).getValue();
							posCount += val.length();
							if (posCount >= newPos) {
								tokenId = i;
								break;
							}
						}
						editorPaneSourcecode.setToolTipText(tokens.get(tokenId).getTokenType().name());
					}
				} else {
//					editorPaneSourcecode.setToolTipText("");
				}
			}
		});
		
		// add listener for sourcecode colorization 
		editorPaneSourcecode.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				styleEditorText();
			}
		});
		
		guiCompiler = new GuiCompiler(this);

		properties.getProperty("syntaxHighlighting.num", "#000000");
		properties.getProperty("syntaxHighlighting.real", "#000000");
		properties.getProperty("syntaxHighlighting.true", "#7F0055");
		properties.getProperty("syntaxHighlighting.false", "#7F0055");
		properties.getProperty("syntaxHighlighting.string", "#2A00FF");
		properties.getProperty("syntaxHighlighting.id", "#2A00FF");
		properties.getProperty("syntaxHighlighting.if", "#7F0055");
		properties.getProperty("syntaxHighlighting.else", "#7F0055");
		properties.getProperty("syntaxHighlighting.while", "#7F0055");
		properties.getProperty("syntaxHighlighting.do", "#7F0055");
		properties.getProperty("syntaxHighlighting.break", "#7F0055");
		properties.getProperty("syntaxHighlighting.return", "#7F0055");
		properties.getProperty("syntaxHighlighting.print", "#7F0055");
		properties.getProperty("syntaxHighlighting.long_symbol", "#7F0055");
		properties.getProperty("syntaxHighlighting.double_symbol", "#7F0055");
		properties.getProperty("syntaxHighlighting.bool_symbol", "#7F0055");
		properties.getProperty("syntaxHighlighting.record_symbol", "#7F0055");
		properties.getProperty("syntaxHighlighting.assignop", "#000000");
		properties.getProperty("syntaxHighlighting.and", "#000000");
		properties.getProperty("syntaxHighlighting.or", "#000000");
		properties.getProperty("syntaxHighlighting.equals", "#000000");
		properties.getProperty("syntaxHighlighting.not_equals", "#000000");
		properties.getProperty("syntaxHighlighting.less", "#000000");
		properties.getProperty("syntaxHighlighting.less_or_equal", "#000000");
		properties.getProperty("syntaxHighlighting.greater", "#000000");
		properties.getProperty("syntaxHighlighting.greater_equal", "#000000");
		properties.getProperty("syntaxHighlighting.plus", "#000000");
		properties.getProperty("syntaxHighlighting.minus", "#000000");
		properties.getProperty("syntaxHighlighting.times", "#000000");
		properties.getProperty("syntaxHighlighting.divide", "#000000");
		properties.getProperty("syntaxHighlighting.not", "#000000");
		properties.getProperty("syntaxHighlighting.left_paran", "#2A00FF");
		properties.getProperty("syntaxHighlighting.right_paran", "#2A00FF");
		properties.getProperty("syntaxHighlighting.left_bracket", "#2A00FF");
		properties.getProperty("syntaxHighlighting.right_bracket", "#2A00FF");
		properties.getProperty("syntaxHighlighting.left_brace", "#2A00FF");
		properties.getProperty("syntaxHighlighting.right_brace", "#2A00FF");
		properties.getProperty("syntaxHighlighting.dot", "#000000");
		properties.getProperty("syntaxHighlighting.semicolon", "#000000");
		properties.getProperty("syntaxHighlighting.comment", "#3F7F5F");
		properties.getProperty("syntaxHighlighting.not_a_token", "#FF0000");
		
		JavabiteConfig.registerConfigurable(this);
	}
	
	public MainFrame(File file) {
		this();
		openedFile = file;
		String fileName = openedFile.getName();
		saveFileContentIntoEditor(openedFile);
		setTitle("Javabite Compiler - " + fileName);
		fileChanged = false;
		toolBarLabel.setText("Document opened.");
	}

	@Override
	public void reportWarning(ReportType type, List<Token> tokens, String message) {
		int line = 0;
		int column = 0;
		if (tokens != null && !tokens.isEmpty()) {
			line = tokens.get(0).getLine();
			column = tokens.get(0).getColumn();
		}
		
		modelReportLogs.addRow(new Object[] { "Warning", type, line, column, message });
		underlineToken(tokens, line, column, Color.YELLOW);
	}
	
	@Override
	public void reportError(ReportType type, List<Token> tokens, String message) {
		errorReported = true;
		int line = 0;
		int column = 0;
		if (tokens != null && !tokens.isEmpty()) {
			line = tokens.get(0).getLine();
			column = tokens.get(0).getColumn();
		}
		
		modelReportLogs.addRow(new Object[] { "Error", type, line, column, message });
		underlineToken(tokens, line, column, Color.RED);
	}
	
	public static int getRow(int pos, JTextComponent editor) {
		int rn = (pos == 0) ? 1 : 0;
		try {
			int offs = pos;
			while (offs > 0) {
				offs=Utilities.getRowStart(editor, offs) - 1;
				rn++;
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		return rn;
	}
	
	public static int getColumn(int pos, JTextComponent editor) {
		try {
			return pos-Utilities.getRowStart(editor, pos) + 1;
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		return -1;
	}

	/**
	 * Styles a special part of the sourcecode
	 */
	private void styleToken(TokenType tokenType, int start, int end) {
		// check properties file for tokentype key, if exist set defined color
		String color;
		if ((color = properties.getProperty("syntaxHighlighting."+tokenType.toString().toLowerCase())) != null) {
			javax.swing.text.Style style = editorPaneSourcecode.addStyle(tokenType.toString(), null);
			StyleConstants.setForeground(style, Color.decode(color));
			doc.setCharacterAttributes(start, end, editorPaneSourcecode.getStyle(tokenType.toString()), true);
		} else {
			javax.swing.text.Style style = editorPaneSourcecode.addStyle("Black", null);
			StyleConstants.setForeground(style, Color.BLACK);
			doc.setCharacterAttributes(start, end, editorPaneSourcecode.getStyle("Black"), true);
		}
	}

	/**
	 * Styles the whole sourcode jtextpane
	 */
	private void styleEditorText() {
		String text = editorPaneSourcecode.getText();
		
		// reset editor value to prevent some highlighting bugs
		int cursorPos = editorPaneSourcecode.getCaretPosition();
		editorPaneSourcecode.setText("");
		javax.swing.text.Style style = editorPaneSourcecode.addStyle("Black", null);
		StyleConstants.setForeground(style, Color.BLACK);
		doc.setCharacterAttributes(0, 1, editorPaneSourcecode.getStyle("Black"), true);
	
		editorPaneSourcecode.setText(text);
		editorPaneSourcecode.setCaretPosition(cursorPos);
		int index = 0;
		List<Token> tokens = guiCompiler.getTokenListFor(text);
		
		Token current_token;
		int lastIndex = 0;
		for (int i = 0; i < tokens.size(); i++) {
			current_token = tokens.get(i);
			index = text.indexOf(current_token.getValue(), lastIndex);
			lastIndex = index + current_token.getValue().length();
			styleToken(current_token.getTokenType(), index , current_token.getValue().length());
		}
	}

	/**
	 * Main entry point for the compile process
	 * */
	private void compile() {
		progressBar.setValue(0);
		try {
			boolean canCompiled = false;
			if (fileChanged) {
				JFrame frame = new JFrame("Save");
				Object[] options = {"Cancel", "No", "Yes"};
				String fileName = (openedFile == null) ? "New File.prog" : openedFile.getName();
				int n = JOptionPane.showOptionDialog (
					frame,
					"Sourcecode cannot be compiled, until it is saved.\nSave file \"" + fileName + "\"?\n",
					"Save",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[2]
				);
				
				if (n == 2) { // "Yes" was selected
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
							
							// sourcecode can now be compiled
							canCompiled = true;
						}
					} else { // firstly save file
						saveEditorContentIntoFile(openedFile);
						setTitle("Javabite Compiler - " + openedFile.getName());
						toolBarLabel.setText("Document saved.");
						
						// sourcecode can now be compiled
						canCompiled = true;
					}
				} else if (n == 1) { // "No" was selected
					if (openedFile == null) {
						canCompiled = false;
						frame = new JFrame();
						JOptionPane.showMessageDialog(frame, "Sourcecode not saved into a file. Cannot compile!");
					} else {
						canCompiled = true;
					}
				} else { // "Cancel" was selected
					canCompiled = false;
				}
			} else {
				if (openedFile == null) {		// file not changed, but doesn't exist
					JFrame frame = new JFrame("Save");
					Object[] options = {"Cancel", "No", "Yes"};
					String fileName = (openedFile == null) ? "New File.prog" : openedFile.getName();
					int n = JOptionPane.showOptionDialog (
						frame,
						"Sourcecode cannot be compiled, until it is saved.\nSave file \"" + fileName + "\"?\n",
						"Save",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						options,
						options[2]
					);
					
					if (n == 2) { // "Yes" was selected
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
								
								// sourcecode can now be compiled
								canCompiled = true;
							}
						}
					}
				} else {
					canCompiled = true; // file not changed, but it exists
				}
			}
			
			if (canCompiled) {
				textPaneLogs.setText("Compiler started.");
				textPaneConsole.setText(textPaneConsole.getText() + "[Compiler] started");
				progressBar.setValue(0);
				progressBar.setEnabled(true);
				errorReported = false;
				for (int i = 0; i < modelReportLogs.getRowCount(); i++) {
					modelReportLogs.removeRow(i);
				}
				File mainFile = guiCompiler.compile(openedFile);
				if (mainFile == null) {
					progressBar.setValue(100);
					progressBar.setEnabled(false);
					return;
				}
				if (!guiCompiler.isJavaBackend()) {
					reportWarning(ReportType.UNDEFINED, null, "Class execution is only supported for Java-Backends");
					progressBar.setValue(100);
					progressBar.setEnabled(false);
					return;
				}
				
				textPaneLogs.setText(textPaneLogs.getText() + "\nExecute program...");
				textPaneConsole.setText(textPaneConsole.getText() + "\n[Compiler] execute program...");
				toolBarLabel.setText("Execute program...");
				Long startTime = System.currentTimeMillis();
				JavaClassProcess p = guiCompiler.execute(mainFile);
				Long stopTime = System.currentTimeMillis();
				textPaneConsole.setText(textPaneConsole.getText() + "\n" + p.getProcessOutput());
				textPaneConsole.setText(textPaneConsole.getText() + "\nReturn value: " + p.getReturnValue() + "\nExecution time: " + (stopTime - startTime) + "ms");
				
				toolBarLabel.setText("Execute program finished.");
				progressBar.setValue(100);
				progressBar.setEnabled(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			textPaneLogs.setText(textPaneLogs.getText() + "\nCompilation failed.");
			toolBarLabel.setText("Compilation failed.");
			Icon icon = null;
			try {
				icon = new ImageIcon(IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream("images" + System.getProperty("file.separator") + "unsuccess-icon.png")));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			toolBarLabel.setIcon(icon);
			progressBar.setValue(0);
			progressBar.setEnabled(false);
		}
	}

	/**
	 * Underlines wrongly typed tokens
	 * */
	private void underlineToken(List<Token> tokens, int line, int column, Color color) {
		String code = editorPaneSourcecode.getText();
		String[] lines = code.split(System.getProperty("line.separator"));
		
		SimpleAttributeSet attributes = new SimpleAttributeSet();
		StyleConstants.setForeground(attributes, color);
		StyleConstants.setUnderline(attributes, true);
		StyledDocument doc = editorPaneSourcecode.getStyledDocument();
		System.out.println(line-1);
		System.out.println("df " + lines[line-1]);
		doc.setCharacterAttributes(code.indexOf(lines[line-1]), tokens.get(0).getValue().length(), attributes, true);
	}
	
	private void requestTacVisualization() {
		tacVisualizationRequested = true;
		compile();
	}
	
	private void showTacVisualization(List<Quadruple> tac) {
		progressBar.setValue(0);
		progressBar.setEnabled(false);
		if (errorReported) {
			JOptionPane.showMessageDialog(null, "While generating the Three Adress Code an error occoured.", "Compilation Errors", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		tacVisualizationRequested = false;
		new TacVisualizerJb().visualizeTAC(tac);
		toolBarLabel.setText("Rendered TAC.");
	}
	
	private void requestAstVisualization() {
		astVisualizationRequested = true;
		compile();
	}
	
	private void showAstVisualization(AST ast) {
		progressBar.setValue(0);
		progressBar.setEnabled(false);
		if (ast == null) {
			JOptionPane.showMessageDialog(null, "While generating the AST an error occoured.", "Compilation Errors", JOptionPane.ERROR_MESSAGE);
			return;
		}
		else if (errorReported) {
			JOptionPane.showMessageDialog(null, "While generating the AST an error occoured. The shown AST is not correct.", "Compilation Errors", JOptionPane.ERROR_MESSAGE);
		}

		astVisualizationRequested = false;
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
	


	@Override
	public void onConfigChanges(JavabiteConfig config) {
		Integer fontSize = Integer.parseInt(properties.getProperty("font.size","18"));
		editorPaneSourcecode.setFont(new Font(Font.MONOSPACED, 0, fontSize));
	}

	private void showSettingsPanel() {
		new SettingsPanel().setVisible(true);
	}
	
	class GuiCompiler extends AbstractJavabiteCompiler {
		MainFrame mainFrame;
		
		public GuiCompiler(MainFrame mainFrame) {
			super();
			this.mainFrame = mainFrame;
			wire();
		}
		
		@Override
		public ReportLog getReportLog() {
			return mainFrame;
		}

		@Override
		protected boolean afterPreprocessing(String targetClassName) {
			progressBar.setValue(20);
			textPaneLogs.setText(textPaneLogs.getText() + "\nCompile File: " + targetClassName + ".prog");
			textPaneConsole.setText(textPaneConsole.getText() + "\n[Compiler] compile file " + targetClassName + ".prog");
			textPaneLogs.setText(textPaneLogs.getText() + "\nGenerating AST for source file...");
			textPaneConsole.setText(textPaneConsole.getText() + "\n[Compiler] generate AST for source file");
			toolBarLabel.setText("Compiling...");
			return true;
		}

		@Override
		protected boolean afterAstGeneration(AST ast) {
			if (errorReported) {
				if (astVisualizationRequested)
					showAstVisualization(ast);
				return reportFailure();
			}

			progressBar.setValue(40);
			textPaneLogs.setText(textPaneLogs.getText() + "\nExecute semantical checks on AST...");
			return true;
		}

		@Override
		protected boolean afterSemanticalAnalysis(AST ast) {
			if (errorReported) {
				if (astVisualizationRequested)
					showAstVisualization(ast);
				return reportFailure();
			}
			
			if (astVisualizationRequested) {
				showAstVisualization(ast);
				return false;
			}
			
			progressBar.setValue(60);
			textPaneLogs.setText(textPaneLogs.getText() + "\nGenerate TAC...");
			textPaneConsole.setText(textPaneConsole.getText() + "\n[Compiler] generate TAC");
			return true;
		}

		@Override
		protected boolean afterTacGeneration(List<Quadruple> tac) {
			if (errorReported) {
				if (tacVisualizationRequested)
					showTacVisualization(tac);
				return reportFailure();
			}

			if (tacVisualizationRequested) {
				showTacVisualization(tac);
				return false;
			}
			
			progressBar.setValue(80);
			textPaneLogs.setText(textPaneLogs.getText() + "\nGenerate target code...");
			textPaneConsole.setText(textPaneConsole.getText() + "\n[Compiler] generate target code");
			return true;
		}

		@Override
		protected boolean afterTargetCodeGeneration(File mainClassFile) {
			if (errorReported) {
				return reportFailure();
			}

			progressBar.setValue(90);
			textPaneLogs.setText(textPaneLogs.getText() + "\nCompilation successful. Main-file written to: " + mainClassFile.getAbsolutePath());
			toolBarLabel.setText("Compilation successful.");
			Icon icon = null;
			try {
				icon = new ImageIcon(IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream("images" + System.getProperty("file.separator") + "success-icon.png")));
			} catch (IOException e) {
				e.printStackTrace();
			}
			toolBarLabel.setIcon(icon);
			return true;
		}
		
		private boolean reportFailure() {
			textPaneLogs.setText(textPaneLogs.getText() + "\nCompilation failed. See error log!");
			toolBarLabel.setText("Compilation failed.");
			Icon icon = null;
			try {
				icon = new ImageIcon(IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream("images" + System.getProperty("file.separator") + "unsuccess-icon.png")));
			} catch (IOException e) {
				e.printStackTrace();
			}
			toolBarLabel.setIcon(icon);
			return false;
		}
		
		/**
		 * Returns a list of tokens for a given string
		 * */
		private List<Token> getTokenListFor(String text) {
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
	}
}