package swp_compiler_ss13.javabite.semantic.attributes;

import swp_compiler_ss13.common.types.Type;

public enum ArithmeticAttribute implements Attribute{
	STRING,
	FLOAT,
	INTEGER,
	BOOLEAN,
	ARRAY,
	STRUCT,
	NONE;
	
	public boolean isNumeric(){
		return this==FLOAT || this==INTEGER;
	}
}
