package swp_compiler_ss13.javabite.gui.config;

import swp_compiler_ss13.javabite.config.ConfigKey;
import swp_compiler_ss13.javabite.config.entry.DropdownConfigEntry;
import swp_compiler_ss13.javabite.gui.config.inputs.DefaultConfigFormField;
import swp_compiler_ss13.javabite.gui.config.inputs.DropdownConfigFormField;

public class ConfigFormFieldFactory {
	private ConfigFormFieldFactory() {
	}
	
	/**
	 * Generates a ConfigFormField for a ConfigKey appropriated for the ConfigKey class
	 * @param configKey
	 * @return
	 */
	public static ConfigFormField generateField(ConfigKey configKey) {
		if (configKey.getEntryDescription() instanceof DropdownConfigEntry) {
			return new DropdownConfigFormField(configKey);
		} else {
			return new DefaultConfigFormField(configKey);
		}
	}
}
