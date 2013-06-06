package swp_compiler_ss13.javabite.codegen;

import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;

public abstract class CastingAst2CodeConverter extends AbstractAst2CodeConverter {
	protected IdentifierData cast(Type goalType, IdentifierData oldId) throws IntermediateCodeGeneratorException {
		IdentifierData newId = icg.generateTempIdentifier(goalType);
		icg.addQuadruple(QuadrupleFactoryJb.generatCast(newId, oldId));
		return newId;
	}
}
