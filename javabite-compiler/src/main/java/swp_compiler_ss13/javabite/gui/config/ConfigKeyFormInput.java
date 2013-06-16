package swp_compiler_ss13.javabite.gui.config;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import swp_compiler_ss13.javabite.config.ConfigKey;

public class ConfigKeyFormInput {
	ConfigKey configKey;
	JTextField textField;
	
	public ConfigKeyFormInput(ConfigKey configKey) {
		this.configKey = configKey;
	}
	
	public JPanel getPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(new JLabel(configKey.toString()), BorderLayout.WEST);
		textField = new JTextField(configKey.getValue());
		textField.setColumns(20);
		panel.add(textField, BorderLayout.EAST);
		return panel;
	}
	
	public boolean isChanged() {
		return textField.getText() != configKey.getValue();
	}
	
	public String getKey() {
		return configKey.getName();
	}
	
	public String getValue() {
		return textField.getText();
	}
}
