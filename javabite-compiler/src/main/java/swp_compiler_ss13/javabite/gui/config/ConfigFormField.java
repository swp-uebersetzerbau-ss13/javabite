package swp_compiler_ss13.javabite.gui.config;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * ConfigFormField provide the input fields for a configuration form
 * 
 * The ConfigFormFieldFactory generates the appropriated form for a 
 * ConfigKey
 */
public interface ConfigFormField {
	
	/**
	 * generates a JPanel containing a label for form input 
	 * @return
	 */
	public JLabel getLabel();
	
	/**
	 * generates a JComponent containing a form input for the key
	 */
	public JComponent getComponent();
	
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
