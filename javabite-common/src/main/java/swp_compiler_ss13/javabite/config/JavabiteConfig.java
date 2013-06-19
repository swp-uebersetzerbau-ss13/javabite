package swp_compiler_ss13.javabite.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.javabite.config.entry.ColorConfigEntry;
import swp_compiler_ss13.javabite.config.entry.DropdownConfigEntry;
import swp_compiler_ss13.javabite.config.entry.TextConfigEntry;

/**
 * The JavabiteConfig extends java.util.Properties and load/write the config
 * from local startup path. It also is a registry for configurable classes which
 * gets informed about configuration changes.
 * 
 * If a key starts with a prefix (config.test.key would have the prefix config)
 * it belongs to the category of the prefix.
 */
public class JavabiteConfig extends Properties {
	private final static Logger log = LoggerFactory
			.getLogger(JavabiteConfig.class);

	private final static String CONFIG_FILE_NAME = "javabite.properties";
//	private final static String DEFAULT_CONFIG_FILE_NAME = "default_javabite.properties";
	public final static JavabiteConfig SINGLETON = new JavabiteConfig();

	private final List<Configurable> configurables = new ArrayList<>();
	private final HashMap<String, ConfigEntry> configEntryMap = new HashMap<>();

	JavabiteConfig() {
		this(CONFIG_FILE_NAME);
	}

	JavabiteConfig(String path) {
		File configFile = new File(path);
		try {
			if (!configFile.exists()) {
//				ClassLoader cl = ClassLoader.getSystemClassLoader();// getClass().getClassLoader();
//				this.load(cl.getResourceAsStream(DEFAULT_CONFIG_FILE_NAME));
				return;
			}
			
			this.load(new FileReader(configFile));
		} catch (IOException e) {
			e.printStackTrace();
			log.error("Could not load configuration file.");
		}
	}

	public static JavabiteConfig getDefaultConfig() {
		return SINGLETON;
	}

	public void configurationChanges() {
		log.debug("All registered configurables are informed about the configuration changes.");
		for (Configurable c : configurables)
			c.onConfigChanges(this);
	}

	public static void registerConfigurable(Configurable config) {
		getDefaultConfig().configurables.add(config);
		config.onConfigChanges(getDefaultConfig());
	}

	@Override
	public synchronized Object setProperty(String key, String value) {
		Object old = super.setProperty(key, value);
		configurationChanges();
		writeConfig();
		return old;
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		String result = super.getProperty(key, defaultValue);
		if (!this.containsKey(key))
			this.setProperty(key, result);
		return result;
	}
	
	/**
	 * properties accessed like this will be interpreted as colors
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getProperty(String key, Integer color) {
		configEntryMap.put(key, new ColorConfigEntry());
		String hex = Integer.toHexString(color);
		hex = hex.substring(2,hex.length());
		String result = super.getProperty(key, hex);
		if (!this.containsKey(key))
			this.setProperty(key, hex);
		return result;
	}
	
	/**
	 * properties accessed like this will be interpreted as limited
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getProperty(String key, List<String> validValues) {
		configEntryMap.put(key, new DropdownConfigEntry(validValues));
		String result = super.getProperty(key, validValues.get(0));
		if (!this.containsKey(key))
			this.setProperty(key, validValues.get(0));
		return result;
	}

	public void writeConfig() {
		log.debug("Write the configuration file to runtime path.");
		File configFile = new File(CONFIG_FILE_NAME);
		try {
			this.store(new FileWriter(configFile),
					"basic Javabite-IDE configuration");
		} catch (IOException e) {
			log.error("Could not write configuration file.");
		}
	}

	/**
	 * @return a set of categories defined like in the object description
	 */
	public Set<ConfigCategory> getConfigCategories() {
		Set<ConfigCategory> categories = new HashSet<>();

		for (String key : this.stringPropertyNames()) {
			if (key.contains(".")) {
				categories.add(new ConfigCategory(key.substring(0,
						key.indexOf("."))));
			} else {
				categories.add(new ConfigCategory(""));
			}
		}

		return categories;
	}

	public Set<ConfigKey> getConfigKeys(ConfigCategory category) {
		return getConfigKeys(category.getName());
	}

	/**
	 * @param categoryName
	 * @return a set of key belonging to the defined category
	 */
	public Set<ConfigKey> getConfigKeys(String categoryName) {
		Set<ConfigKey> keys = new HashSet<>();

		if (categoryName.isEmpty()) {
			for (String key : this.stringPropertyNames()) {
				if (!key.contains(".")) {
					ConfigEntry ce = configEntryMap.get(key);
					ce = ce != null ? ce : new TextConfigEntry();
					keys.add(new ConfigKey(key, getProperty(key), ce));
				}
			}
		} else {
			for (String key : this.stringPropertyNames()) {
				if (key.startsWith(categoryName)) {
					ConfigEntry ce = configEntryMap.get(key);
					ce = ce != null ? ce : new TextConfigEntry();
					keys.add(new ConfigKey(key, getProperty(key), ce));
				}
			}
		}

		return keys;
	}

	private static final long serialVersionUID = 1L;
}