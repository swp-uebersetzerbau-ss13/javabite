package swp_compiler_ss13.javabite.config;

public class ConfigCategory implements Comparable<ConfigCategory> {
	final String categoryName;

	public ConfigCategory(String name) {
		this.categoryName = name;
	}

	public String getName() {
		return categoryName;
	}

	@Override
	public String toString() {
		if (categoryName.isEmpty()) {
			return "Misc";
		}
		
		return ConfigStringUtils.camelCaseToReadable(categoryName);
	}

	@Override
	public int compareTo(ConfigCategory o) {
		return categoryName.compareTo(o.categoryName);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((categoryName == null) ? 0 : categoryName.hashCode());
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
		ConfigCategory other = (ConfigCategory) obj;
		if (categoryName == null) {
			if (other.categoryName != null)
				return false;
		} else if (!categoryName.equals(other.categoryName))
			return false;
		return true;
	}
}
