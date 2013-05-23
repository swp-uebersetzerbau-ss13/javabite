package swp_compiler_ss13.javabite.types;

import swp_compiler_ss13.common.types.Type;
/**
 * This class was just created to instantiate working test-sets.
 * Feel free to modify.
 * @author Till
 *
 */
public class TypeJb extends Type{

	public TypeJb(Kind kind) {
		super(kind);
	}

	@Override
	public String getTypeName() {
		return kind.name();
	}

	@Override
	public String toString() {
		return this.getTypeName();
	}

}
