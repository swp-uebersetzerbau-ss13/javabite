package swp_compiler_ss13.javabite.gui.config.inputs;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import swp_compiler_ss13.javabite.config.ConfigKey;
import swp_compiler_ss13.javabite.gui.config.ConfigFormField;

public class DefaultConfigFormField implements ConfigFormField {
	ConfigKey configKey;
	JTextField textField;

	public DefaultConfigFormField(ConfigKey configKey) {
		this.configKey = configKey;
	}

	@Override
	public JPanel getPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(new JLabel(configKey.toString()), BorderLayout.WEST);
		textField = new JTextField(configKey.getValue());
		textField.setColumns(20);
		panel.add(textField, BorderLayout.EAST);
		return panel;
	}

	@Override
	public boolean isChanged() {
		return !textField.getText().equals(configKey.getValue());
	}

	@Override
	public String getKey() {
		return configKey.getName();
	}

	@Override
	public String getValue() {
		return textField.getText();
	}
}
