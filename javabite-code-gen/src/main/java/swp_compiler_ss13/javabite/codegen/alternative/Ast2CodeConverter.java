package swp_compiler_ss13.javabite.codegen.alternative;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;

public interface Ast2CodeConverter {
	ASTNode.ASTNodeType getNodeType();
	void setIcgJb(Ast2CodeConverterCompatibleGenerator icg);
	void convert(ASTNode node) throws IntermediateCodeGeneratorException;
}
