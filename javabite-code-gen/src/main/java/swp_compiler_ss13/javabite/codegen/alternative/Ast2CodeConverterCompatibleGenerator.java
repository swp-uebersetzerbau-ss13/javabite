package swp_compiler_ss13.javabite.codegen.alternative;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.ir.IntermediateCodeGenerator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;

public interface Ast2CodeConverterCompatibleGenerator extends IntermediateCodeGenerator {
	void addQuadruple(Quadruple quadruple);
	
	/**
	 * retrieve the appropriated node converter and generate the necessary quadruples
	 * @param node
	 * @throws IntermediateCodeGeneratorException
	 */
	void processNode(ASTNode node) throws IntermediateCodeGeneratorException;
	
	/**
	 * prepare a new scope area
	 */
	void enterNewScope();

	/**
	 * returns to the old scope area
	 */
	void leaveCurrentScope();

	/**
	 * generate a identifier for TAC for the provided ast identifier and
	 * save the mapping
	 * @param astIdentifier
	 * @param type
	 * @return
	 * @throws IntermediateCodeGeneratorException
	 */
	IdentifierData generateIdentifierMapping(String astIdentifier, Type type) throws IntermediateCodeGeneratorException;
	
	IdentifierData lookupIdentifierData(String astIdentifier) throws IntermediateCodeGeneratorException;
	
	/**
	 * lookup a ICG identifier for the astIdentifier
	 * @param astIdentifier
	 * @return
	 * @throws IntermediateCodeGeneratorException
	 */
	String lookupIcgIdentifier(String astIdentifier) throws IntermediateCodeGeneratorException;
	
	/**
	 * lookup the type of the astIdentifier
	 * @param astIdentifier
	 * @return
	 * @throws IntermediateCodeGeneratorException
	 */
	Type lookupType(String astIdentifier) throws IntermediateCodeGeneratorException;
	
	void pushIdentifierData(IdentifierData data);
	
	IdentifierData popIdentifierData();
	
	/**
	 * generates a identifier for a temp-value
	 * @param type
	 * @return
	 * @throws IntermediateCodeGeneratorException
	 */
	IdentifierData generateTempIdentifier(Type type) throws IntermediateCodeGeneratorException;
}
