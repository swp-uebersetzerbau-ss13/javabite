package swp_compiler_ss13.javabite.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultStyledDocument;

import org.apache.commons.io.HexDump;
import org.apache.commons.io.IOUtils;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.lexer.Token;
import swp_compiler_ss13.common.report.ReportLog;
import swp_compiler_ss13.common.report.ReportType;
import swp_compiler_ss13.javabite.config.Configurable;
import swp_compiler_ss13.javabite.config.JavabiteConfig;

import swp_compiler_ss13.javabite.gui.ast.ASTVisualizerJb;
import swp_compiler_ss13.javabite.gui.ast.fitted.KhaledGraphFrame;
import swp_compiler_ss13.javabite.gui.bytecode.ByteCodeVisualizerJb;
import swp_compiler_ss13.javabite.gui.config.SettingsPanel;
import swp_compiler_ss13.javabite.gui.tac.TacVisualizerJb;
import swp_compiler_ss13.javabite.runtime.JavaClassProcess;

public class MainFrame extends JFrame implements ReportLog, Configurable {
	
	private static final long serialVersionUID = 1673088367851101738L;
	
	private static final String BASE_TITLE = "Javabite Compiler - ";
	
	// class to setup styles for our sourcecode
	DefaultStyledDocument doc = new DefaultStyledDocument();
	
	boolean astVisualizationRequested = false;
	boolean tacVisualizationRequested = false;
	boolean byteCodeVisualizationRequested = false;
	
	boolean errorReported;
	private GuiCompiler guiCompiler;

	// file for opened sourcecode and changes listener
	FileManager fileManager;
	StyleManager styleManager;
	UndoCostumManager undoManager;
	
	// get properties for syntax highlighting
	JavabiteConfig config = JavabiteConfig.getDefaultConfig();
	
	// components
	JMenuBar menuBar;
	JMenu menuFile;
	JMenuItem menuFileOpen;
	JMenuItem menuFileSave;
	JMenuItem menuFileClose;
	JMenu menuVisual;
	JMenuItem menuVisualAst;
	JMenuItem menuVisualTac;
	JMenuItem menuVisualByteCode;
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
	
	// undo and redo
	private JButton undoButton;
	private JButton redoButton;
	private JScrollPane scrollPane;
	private JMenuItem mntmProperties;
	private JMenuItem mntmNew;
	private JSeparator separator;
	private JSeparator separator_1;

	private JScrollPane scrollPaneReportLogs;
	
	/**
	 * Create the frame.
	 */
	public MainFrame() {
		this(true);
	}
	
	public MainFrame(File file) {
		this(false);
		fileManager.openFile(file);
	}
	
	private MainFrame(Boolean withNewFile) {
		guiCompiler = new GuiCompiler(this);
		initializeConfig();
		fileManager = new FileManager(this);
		doc.addDocumentListener(fileManager);
		styleManager = new StyleManager(this);
		//doc.setDocumentFilter(styleManager);
		
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
				fileManager.openFile();
			}
		});
		
		mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileManager.newFile();
			}
		});
		menuFile.add(mntmNew);
		menuFile.add(menuFileOpen);
		
		menuFileSave = new JMenuItem("Save");
		menuFileSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileManager.saveFile();
			}
		});
		menuFile.add(menuFileSave);
		
		menuFileClose = new JMenuItem("Close");
		menuFileClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileManager.saveFileIfChanged();
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
		
		menuVisualByteCode = new JMenuItem("ByteCode");
		menuVisualByteCode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				requestByteCodeVisualization();
			}
		});
		menuVisual.add(menuVisualByteCode);
		
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
				}
				restyle();
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
				}
				restyle();
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
		
		// add listener for sourcecode colorization 
		editorPaneSourcecode.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				restyle();
			}
		});
		
		// undo redo manager
		undoManager = new UndoCostumManager(editorPaneSourcecode);
		
		// tooltip
		ToolTipManager.sharedInstance().setDismissDelay(5000);
		editorPaneSourcecode.addMouseMotionListener(new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				styleManager.showToolTip(e.getPoint());
			}
		});

		JavabiteConfig.registerConfigurable(this);
		
		if (withNewFile)
			fileManager.newFile();
	}


	@Override
	public void setTitle(String title) {
		super.setTitle(BASE_TITLE + title);
	}
	
	public void setToolBar(String label) {
		toolBarLabel.setText(label);
	}
	
	public void appendToLogs(String message) {
		textPaneLogs.setText(textPaneLogs.getText() + message);
	}
	
	public void appendToConsole(String message) {
		textPaneConsole.setText(textPaneConsole.getText() + message);
	}
	
	public void setSuccessIcon() {
		Icon icon = null;
		try {
			icon = new ImageIcon(IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream("images" + System.getProperty("file.separator") + "success-icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		toolBarLabel.setIcon(icon);
	}
	
	public void setFailureIcon() {
		Icon icon = null;
		try {
			icon = new ImageIcon(IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream("images" + System.getProperty("file.separator") + "unsuccess-icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		toolBarLabel.setIcon(icon);
	}
	
	public void setProgress(int progress) {
		if (progress >= 100) {
			progressBar.setEnabled(false);
		} else {
			progressBar.setEnabled(true);
			progressBar.setValue(progress);
		}
	}
	
	public void clearSourcePane() {
		editorPaneSourcecode.setText("");
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
		styleManager.underlineToken(tokens, Color.YELLOW);
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
		styleManager.underlineToken(tokens, Color.RED);
	}

	private String lastText = null;
	public void restyle() {
		if (editorPaneSourcecode.getText().equals(lastText))
			return;
		lastText = editorPaneSourcecode.getText();
		styleManager.setTokens(guiCompiler.getTokenListFor(lastText));
	}

	private void showSettingsPanel() {
		new SettingsPanel().setVisible(true);
	}

	private void initializeConfig() {
		config.getProperty("syntaxHighlighting.num", "#000000");
		config.getProperty("syntaxHighlighting.real", "#000000");
		config.getProperty("syntaxHighlighting.true", "#7F0055");
		config.getProperty("syntaxHighlighting.false", "#7F0055");
		config.getProperty("syntaxHighlighting.string", "#2A00FF");
		config.getProperty("syntaxHighlighting.id", "#2A00FF");
		config.getProperty("syntaxHighlighting.if", "#7F0055");
		config.getProperty("syntaxHighlighting.else", "#7F0055");
		config.getProperty("syntaxHighlighting.while", "#7F0055");
		config.getProperty("syntaxHighlighting.do", "#7F0055");
		config.getProperty("syntaxHighlighting.break", "#7F0055");
		config.getProperty("syntaxHighlighting.return", "#7F0055");
		config.getProperty("syntaxHighlighting.print", "#7F0055");
		config.getProperty("syntaxHighlighting.long_symbol", "#7F0055");
		config.getProperty("syntaxHighlighting.double_symbol", "#7F0055");
		config.getProperty("syntaxHighlighting.bool_symbol", "#7F0055");
		config.getProperty("syntaxHighlighting.record_symbol", "#7F0055");
		config.getProperty("syntaxHighlighting.assignop", "#000000");
		config.getProperty("syntaxHighlighting.and", "#000000");
		config.getProperty("syntaxHighlighting.or", "#000000");
		config.getProperty("syntaxHighlighting.equals", "#000000");
		config.getProperty("syntaxHighlighting.not_equals", "#000000");
		config.getProperty("syntaxHighlighting.less", "#000000");
		config.getProperty("syntaxHighlighting.less_or_equal", "#000000");
		config.getProperty("syntaxHighlighting.greater", "#000000");
		config.getProperty("syntaxHighlighting.greater_equal", "#000000");
		config.getProperty("syntaxHighlighting.plus", "#000000");
		config.getProperty("syntaxHighlighting.minus", "#000000");
		config.getProperty("syntaxHighlighting.times", "#000000");
		config.getProperty("syntaxHighlighting.divide", "#000000");
		config.getProperty("syntaxHighlighting.not", "#000000");
		config.getProperty("syntaxHighlighting.left_paran", "#2A00FF");
		config.getProperty("syntaxHighlighting.right_paran", "#2A00FF");
		config.getProperty("syntaxHighlighting.left_bracket", "#2A00FF");
		config.getProperty("syntaxHighlighting.right_bracket", "#2A00FF");
		config.getProperty("syntaxHighlighting.left_brace", "#2A00FF");
		config.getProperty("syntaxHighlighting.right_brace", "#2A00FF");
		config.getProperty("syntaxHighlighting.dot", "#000000");
		config.getProperty("syntaxHighlighting.semicolon", "#000000");
		config.getProperty("syntaxHighlighting.comment", "#3F7F5F");
		config.getProperty("syntaxHighlighting.not_a_token", "#FF0000");
	}

	@Override
	public void onConfigChanges(JavabiteConfig config) {
		Integer fontSize = Integer.parseInt(config.getProperty("font.size","18"));
		editorPaneSourcecode.setFont(new Font(Font.MONOSPACED, 0, fontSize));
	}

	/**
	 * Main entry point for the compile process
	 * */
	private void compile() {
		progressBar.setValue(0);
		try {
			if (!fileManager.saveFileIfChanged())
				return;
			
			textPaneLogs.setText("Compiler started.");
			textPaneConsole.setText(textPaneConsole.getText() + "[Compiler] started");
			progressBar.setValue(0);
			progressBar.setEnabled(true);
			errorReported = false;
			for (int i = 0; i < modelReportLogs.getRowCount(); i++) {
				modelReportLogs.removeRow(i);
			}
			File mainFile = guiCompiler.compile(fileManager.currentFile);
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
	
	private String dump(File classfile) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		byte[] bytes;
		try {
			bytes = IOUtils.toByteArray(new FileInputStream(classfile));
			HexDump.dump(bytes, 0, baos, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos.toString();
	}
	


	private void requestTacVisualization() {
		tacVisualizationRequested = true;
		compile();
	}
	
	void showTacVisualization(List<Quadruple> tac) {
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
	
	private void requestByteCodeVisualization() {
		byteCodeVisualizationRequested = true;
		compile();
	}
	
	void showByteCodeVisualization(File classfile) {
		progressBar.setValue(0);
		progressBar.setEnabled(false);
		if (errorReported) {
			JOptionPane.showMessageDialog(null, "While generating the Three Adress Code an error occoured.", "Compilation Errors", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		byteCodeVisualizationRequested = false;
		new ByteCodeVisualizerJb(classfile).visualizeByteCode(dump(classfile));
		toolBarLabel.setText("Rendered ByteCode.");
	}
	
	private void requestAstVisualization() {
		astVisualizationRequested = true;
		compile();
	}
	
	void showAstVisualization(AST ast) {
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
}
