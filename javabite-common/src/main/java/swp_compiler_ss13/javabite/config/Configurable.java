package swp_compiler_ss13.javabite.config;

/**
 * All classes which should be configurable by JavabiteConfig have to implement
 * this interface. They have to register by the registerConfigurable-method.
 * 
 * @see swp_compiler_ss13.javabite.config.JavabiteConfig
 */
public interface Configurable {
	
	/**
	 * This method will be called directly after registerConfigurable was called
	 * and each time if the configuration is changed.
	 * 
	 * @param config is the JavabiteConfig object which should be used to 
	 *        retrieve the configuration.
	 */
	void onConfigChanges(JavabiteConfig config);
}
