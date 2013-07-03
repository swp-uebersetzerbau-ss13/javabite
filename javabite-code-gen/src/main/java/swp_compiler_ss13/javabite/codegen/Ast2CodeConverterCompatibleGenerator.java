package swp_compiler_ss13.javabite.codegen;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.ir.IntermediateCodeGenerator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.Type;
import swp_compiler_ss13.common.types.derived.Member;

public interface Ast2CodeConverterCompatibleGenerator extends
		IntermediateCodeGenerator {
	void addQuadruple(Quadruple quadruple);

	/**
	 * retrieve the appropriated node converter and generate the necessary
	 * quadruples
	 * 
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
	 * generate a identifier for TAC for the provided ast identifier and save
	 * the mapping
	 * 
	 * @param astIdentifier
	 * @param type
	 * @return
	 * @throws IntermediateCodeGeneratorException
	 */
	IdentifierData generateIdentifierMapping(String astIdentifier, Type type)
			throws IntermediateCodeGeneratorException;

	/**
	 * generates a identifier for a temp-value
	 * 
	 * @param type
	 * @return
	 * @throws IntermediateCodeGeneratorException
	 */
	IdentifierData generateTempIdentifier(Type type)
			throws IntermediateCodeGeneratorException;

	/**
	 * lookup a identifier data for the astIdentifier
	 * 
	 * @param astIdentifier
	 * @return
	 * @throws IntermediateCodeGeneratorException
	 */
	IdentifierData lookupIdentifierData(String astIdentifier)
			throws IntermediateCodeGeneratorException;

	/**
	 * push identifier data onto a stack for a interested callee
	 * 
	 * @param data
	 */
	void pushIdentifierData(IdentifierData data);

	/**
	 * pop the identifier data from stack which was pushed there by a requested
	 * node-processing
	 * 
	 * @return
	 */
	IdentifierData popIdentifierData();

	/**
	 * @return a unique label for this generator
	 */
	String getNewLabel();

	/**
	 * register a new break label for a loop
	 * 
	 * @param breakLabel
	 */
	void enterLoop(String breakLabel);

	/**
	 * leave a registered loop-breakLabel-scope
	 */
	void leaveLoop();

	/**
	 * @return the current break label from loop-scopes
	 */
	String getCurrentBreakLabel();
	
	
	/**
	 * @return the reference name 
	 */
	String getNewReference();
	
	void enterNewMemberAndReferenceScope();

	void leaveLastMemberAndReferenceScope();
	
	void pushMembers(Member[] members);
	
	Member[] peekMembers();
	
	Member[] popMembers();
	
	boolean isInsideOfStruct();
	
	/**
	 * pushes a reference on a special stack
	 * @param reference
	 */
	void pushReference(String reference);
	
	/**
	 * @return pop from reference stack
	 */
	String popReference();
	
	boolean isReferenceOnStack();
}
