package swp_compiler_ss13.javabite.gui.config.inputs;

import javax.swing.JComponent;
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
	public JLabel getLabel() {
		return new JLabel(configKey.toString(), JLabel.TRAILING);
	}

	@Override
	public JComponent getComponent() {
		textField = new JTextField(configKey.getValue());
		return textField;
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
