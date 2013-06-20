package swp_compiler_ss13.javabite.gui.config.inputs;

import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import swp_compiler_ss13.javabite.config.ConfigKey;
import swp_compiler_ss13.javabite.config.entry.DropdownConfigEntry;
import swp_compiler_ss13.javabite.gui.config.ConfigFormField;

public class DropdownConfigFormField implements ConfigFormField {
	ConfigKey configKey;
	JComboBox<String> comboBox;

	public DropdownConfigFormField(ConfigKey configKey) {
		if (!(configKey.getEntryDescription() instanceof DropdownConfigEntry))
			throw new RuntimeException("ConfigEntry has wrong type");

		this.configKey = configKey;
	}

	@Override
	public JLabel getLabel() {
		return new JLabel(configKey.toString(), JLabel.TRAILING);
	}

	@Override
	public JComponent getComponent() {
		DropdownConfigEntry ce = (DropdownConfigEntry) configKey
				.getEntryDescription();
		comboBox = new JComboBox<>(new Vector<>(ce.getPredefinedValues()));
		comboBox.setSelectedItem(configKey.getValue());
		return comboBox;
	}

	@Override
	public boolean isChanged() {
		return !comboBox.getSelectedItem().equals(configKey.getValue());
	}

	@Override
	public String getKey() {
		return configKey.getName();
	}

	@Override
	public String getValue() {
		return (String) comboBox.getSelectedItem();
	}
}
