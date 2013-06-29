package swp_compiler_ss13.javabite.semantic;

import swp_compiler_ss13.common.types.Type;

enum ArithmeticAttribute implements Attribute{
	STRING,
	FLOAT,
	INTEGER,
	BOOLEAN,
	ARRAY,
	STRUCT,
	NONE;
	
	boolean isNumeric(){
		return this==FLOAT || this==INTEGER;
	}
}
