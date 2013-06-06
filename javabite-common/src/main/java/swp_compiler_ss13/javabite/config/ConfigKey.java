package swp_compiler_ss13.javabite.config;

public class ConfigKey implements Comparable<ConfigKey> {
	final String keyName;
	String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ConfigKey(String name, String value) {
		this.keyName = name;
		this.value = value;
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
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
