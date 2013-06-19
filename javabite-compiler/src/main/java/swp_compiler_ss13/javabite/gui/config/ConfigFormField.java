package swp_compiler_ss13.javabite.gui.config;

import javax.swing.JPanel;

/**
 * ConfigFormField provide the input fields for a configuration form
 * 
 * The ConfigFormFieldFactory generates the appropriated form for a 
 * ConfigKey
 */
public interface ConfigFormField {
	/**
	 * generates a JPanel containing a labeled form input for the key
	 */
	public JPanel getPanel();
	
	/**
	 * @return true, if the form field value changed since generation
	 */
	public boolean isChanged();
	
	/**
	 * @return the key identifying the config entry
	 */
	public String getKey();
	
	/**
	 * @return the value of the form field in config format
	 */
	public String getValue();
}
