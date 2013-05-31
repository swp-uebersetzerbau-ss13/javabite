package swp_compiler_ss13.javabite.config;

public class ConfigKey implements Comparable<ConfigKey> {
	final String keyName;

	public ConfigKey(String name) {
		this.keyName = name;
	}

	public String getName() {
		return keyName;
	}

	@Override
	public String toString() {
		return ConfigStringUtils.camelCaseToReadable(keyName.substring(keyName
				.indexOf(".") + 1)).replace(".", " ");
	}

	@Override
	public int compareTo(ConfigKey o) {
		return keyName.compareTo(o.keyName);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfigKey other = (ConfigKey) obj;
		if (keyName == null) {
			if (other.keyName != null)
				return false;
		} else if (!keyName.equals(other.keyName))
			return false;
		return true;
	}
}
