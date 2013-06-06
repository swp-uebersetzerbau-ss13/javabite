package swp_compiler_ss13.javabite.codegen;

import swp_compiler_ss13.common.types.Type;

public class IdentifierData {
	private String identifier;
	private Type type;

	public IdentifierData(String identifier, Type type) {
		this.identifier = identifier;
		this.type = type;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}