package swp_compiler_ss13.javabite.gui.config;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import swp_compiler_ss13.javabite.config.ConfigCategory;
import swp_compiler_ss13.javabite.config.ConfigKey;
import swp_compiler_ss13.javabite.config.JavabiteConfig;

public class SettingsPanel extends JFrame {
	final JavabiteConfig config = JavabiteConfig.getDefaultConfig();
	List<ConfigKeyFormInput> formInputs = new ArrayList<>();
	
	public SettingsPanel() {
		this.setMinimumSize(new Dimension(600, 400));
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		getContentPane().add(splitPane);
		
		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel buttonPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		
		JButton btnSave = new JButton("Save");
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//TODO: save the changes done on this view
				//therefore you have to do the following:
				// * iterate over the formInputs
				// * check if formInput is changed (isChanged())
				// * if changed -> insert into config
				//    -> config.setProperty(key,value)
			}
		});
		buttonPanel.add(btnSave);
		
		JButton btnClose = new JButton("Close");
		btnClose.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//TODO: close the SettingsPanel
			}
		});
		buttonPanel.add(btnClose);
		
		final JPanel formPanel = new JPanel();
		panel.add(formPanel, BorderLayout.CENTER);
				
		final JList<ConfigCategory> list = new JList<ConfigCategory>();
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				ConfigCategory cc = list.getSelectedValue();
				List<ConfigKey> configKeys = new ArrayList<>(config.getConfigKeys(cc));
				formPanel.removeAll();
				formInputs.clear();
				for(ConfigKey ck:configKeys) {
					ConfigKeyFormInput ckfi = new ConfigKeyFormInput(ck);
					formPanel.add(ckfi.getPanel());
					formInputs.add(ckfi);
				}
				formPanel.updateUI();
			}
		});
		list.setModel(new AbstractListModel<ConfigCategory>() {
			private static final long serialVersionUID = 1L;
			List<ConfigCategory> values = new ArrayList<>(config.getConfigCategories());
			public int getSize() {
				return values.size();
			}
			public ConfigCategory getElementAt(int index) {
				return values.get(index);
			}
		});
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		splitPane.setLeftComponent(list);
		
		pack();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SettingsPanel().setVisible(true);
	}

}
