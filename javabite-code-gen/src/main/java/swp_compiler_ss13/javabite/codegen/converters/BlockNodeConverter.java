package swp_compiler_ss13.javabite.codegen.converters;

import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.ast.ASTNode.ASTNodeType;
import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.codegen.AbstractAst2CodeConverter;

public class BlockNodeConverter extends AbstractAst2CodeConverter {

	@Override
	public void convert(ASTNode node) throws IntermediateCodeGeneratorException {
		if (!(node instanceof BlockNode))
			throw new IntermediateCodeGeneratorException();
		BlockNode blockNode = (BlockNode) node;
		
		// register new scope
		icg.enterNewScope();
		// process the declarations
		for (DeclarationNode decl:blockNode.getDeclarationList()) {
			icg.processNode(decl);
		}
		// process the statements
		for (StatementNode stmt:blockNode.getStatementList()) {
			icg.processNode(stmt);
		}
		// drop above created scope
		icg.leaveCurrentScope();
	}

	@Override
	public ASTNodeType getNodeType() {
		return ASTNodeType.BlockNode;
	}
}
