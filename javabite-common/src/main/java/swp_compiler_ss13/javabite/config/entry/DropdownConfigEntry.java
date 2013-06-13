package swp_compiler_ss13.javabite.config.entry;

import java.util.List;

import swp_compiler_ss13.javabite.config.ConfigEntry;
import swp_compiler_ss13.javabite.config.ConfigType;

public class DropdownConfigEntry implements ConfigEntry {
	private List<String> predefinedValues;
	
	public DropdownConfigEntry(List<String> predefinedValues) {
		this.predefinedValues = predefinedValues;
	}
	
	@Override
	public ConfigType getConfigType() {
		return ConfigType.DROPDOWN;
	}
	
	public List<String> getPredefinedValues() {
		return predefinedValues;
	}
}
