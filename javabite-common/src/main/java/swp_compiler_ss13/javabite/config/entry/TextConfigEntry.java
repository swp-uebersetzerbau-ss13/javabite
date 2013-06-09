package swp_compiler_ss13.javabite.config.entry;

import swp_compiler_ss13.javabite.config.ConfigEntry;
import swp_compiler_ss13.javabite.config.ConfigType;

public class TextConfigEntry implements ConfigEntry {

	@Override
	public ConfigType getConfigType() {
		return ConfigType.COLOR;
	}
}
