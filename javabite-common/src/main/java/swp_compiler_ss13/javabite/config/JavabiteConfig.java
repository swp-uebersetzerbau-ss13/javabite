package swp_compiler_ss13.javabite.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private final static String DEFAULT_CONFIG_FILE_NAME = "default_javabite.properties";
	public final static JavabiteConfig SINGLETON = new JavabiteConfig();

	private final List<Configurable> configurables = new ArrayList<>();

	public JavabiteConfig() {
		File configFile = new File(CONFIG_FILE_NAME);

		try {
			if (!configFile.exists()) {
				ClassLoader cl = ClassLoader.getSystemClassLoader();// getClass().getClassLoader();
				URL url = cl.getResource(DEFAULT_CONFIG_FILE_NAME);
				try {
					URI uri = new URI(url.toString());
					String filename = uri.getPath();
					configFile = new File(filename);
				} catch (URISyntaxException e) {
					configFile = null;
				}
			}

			if (configFile == null) {
				log.error("Could not load configuration file.");
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
		if (this.containsKey(key))
			this.setProperty(key, result);
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
					keys.add(new ConfigKey(key, getProperty(key)));
				}
			}
		} else {
			for (String key : this.stringPropertyNames()) {
				if (key.startsWith(categoryName)) {
					keys.add(new ConfigKey(key, getProperty(key)));
				}
			}
		}

		return keys;
	}

	private static final long serialVersionUID = 1L;
}
