package swp_compiler_ss13.javabite.codegen;

import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;

public abstract class CastingAst2CodeConverter extends AbstractAst2CodeConverter {
	protected IdentifierData cast(Type goalType, IdentifierData oldId) throws IntermediateCodeGeneratorException {
		if (oldId.getIdentifier().startsWith("#")) {
			IdentifierData tmpId = icg.generateTempIdentifier(oldId.getType());
			icg.addQuadruple(QuadrupleFactoryJb.generateAssignment(tmpId, oldId));
			oldId = tmpId;
		}
		IdentifierData newId = icg.generateTempIdentifier(goalType);
		icg.addQuadruple(QuadrupleFactoryJb.generateCast(newId, oldId));
		return newId;
	}
}
