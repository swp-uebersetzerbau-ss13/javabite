package swp_compiler_ss13.javabite.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JToolBar;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JEditorPane;

public class MainFrame extends JFrame {
	
	private JPanel contentPane;
	
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
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
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
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JEditorPane editorPane = new JEditorPane();
		tabbedPane.addTab("Unbenannt", null, editorPane, null);
		
	
	}
}
