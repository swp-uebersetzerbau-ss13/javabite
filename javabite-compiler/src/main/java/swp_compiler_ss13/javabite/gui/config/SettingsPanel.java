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
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import swp_compiler_ss13.javabite.config.ConfigCategory;
import swp_compiler_ss13.javabite.config.ConfigKey;
import swp_compiler_ss13.javabite.config.JavabiteConfig;

public class SettingsPanel extends JFrame {
	private static final long serialVersionUID = 1L;
	final JavabiteConfig config = JavabiteConfig.getDefaultConfig();
	List<ConfigFormField> formInputs = new ArrayList<>();
	
	public SettingsPanel() {
		this.setMinimumSize(new Dimension(800, 600));
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
				for (ConfigFormField input:formInputs) {
					if (input.isChanged())
						config.setProperty(input.getKey(), input.getValue());
				}
			}
		});
		buttonPanel.add(btnSave);

		JButton btnClose = new JButton("Close");
		btnClose.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				closeWindow();
			}
		});
		buttonPanel.add(btnClose);
		final JPanel formPanel = new JPanel();
		panel.add(new JScrollPane(formPanel,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
		final JList<ConfigCategory> list = new JList<ConfigCategory>();
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				ConfigCategory cc = list.getSelectedValue();
				List<ConfigKey> configKeys = new ArrayList<>(config
						.getConfigKeys(cc));
				formPanel.removeAll();
				formInputs.clear();
				formPanel.setLayout(new SpringLayout());
				for (ConfigKey ck : configKeys) {
					ConfigFormField ckfi = ConfigFormFieldFactory.generateField(ck);
					JLabel l = ckfi.getLabel();
					formPanel.add(l);
					JComponent p = ckfi.getComponent();
					l.setLabelFor(p);
					formPanel.add(p);
					formInputs.add(ckfi);
				}
				
				for (int i = configKeys.size(); i < 18; i++) {
					JLabel dummyLabel = new JLabel(" ");
					formPanel.add(dummyLabel);
					JLabel labeledLabel = new JLabel(" ");
					dummyLabel.setLabelFor(labeledLabel);
					formPanel.add(labeledLabel);
				}
				SpringUtilities.makeCompactGrid(formPanel, (configKeys.size() > 18)?configKeys.size():18, 2, 6, 6, 6, 6);
				formPanel.updateUI();
			}
		});
		list.setModel(new AbstractListModel<ConfigCategory>() {
			private static final long serialVersionUID = 1L;
			List<ConfigCategory> values = new ArrayList<>(config
					.getConfigCategories());

			public int getSize() {
				return values.size();
			}
			
			public ConfigCategory getElementAt(int index) {
				return values.get(index);
			}
		});
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setSelectedIndex(0);
		splitPane.setLeftComponent(list);

		pack();
	}

	public void closeWindow() {
		this.dispose();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JavabiteConfig config = JavabiteConfig.getDefaultConfig();
		config.getProperty("catFirst.value1", "default1");
		config.getProperty("catFirst.value2", "default2");
		config.getProperty("catFirst.value3", "default3");
		config.getProperty("value1", "default1");
		config.getProperty("comp.key1", "default1");
		config.getProperty("comp.key2", "default1");
		config.getProperty("comp.key3", "default1");
		new SettingsPanel().setVisible(true);
	}

}
