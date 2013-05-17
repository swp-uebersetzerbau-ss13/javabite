package swp_compiler_ss13.javabite.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
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
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.Panel;
import javax.swing.JTree;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingConstants;

public class MainFrame extends JFrame {
	
	private JPanel contentPane;
	
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
		
		JEditorPane dtrpnSourcecode = new JEditorPane();
		dtrpnSourcecode.setText("Sourcecode");
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
}
