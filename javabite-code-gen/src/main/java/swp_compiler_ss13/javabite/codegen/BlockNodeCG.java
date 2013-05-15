package swp_compiler_ss13.javabite.codegen;

import java.util.HashMap;
import java.util.Iterator;

import swp_compiler_ss13.common.ast.nodes.StatementNode;
import swp_compiler_ss13.common.ast.nodes.marynary.BlockNode;
import swp_compiler_ss13.common.ast.nodes.unary.DeclarationNode;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.ast.ASTNodeJb;

/**
 * 
 * @author Alpin Sahin und Florian Mercks
 *
 */
public class BlockNodeCG {
	

	public void convert(BlockNode node) throws IntermediateCodeGeneratorException {
		// push a new identifier name list to latestIdentifierNames stack
		IntermediateCodeGeneratorJb.latestIdentifierNames.push(new HashMap<String, String>());
		// push the symbol table to symbolTable stack
		IntermediateCodeGeneratorJb.symbolTable.push(node.getSymbolTable());

		Iterator<DeclarationNode> declarationIter = node.getDeclarationIterator();
		while(declarationIter.hasNext()){
			DeclarationNode dNode = declarationIter.next();
			new DeclarationNodeCG().convert(dNode);
		}
		
		Iterator<StatementNode> statementIter = node.getStatementIterator();
		while(statementIter.hasNext()){
			StatementNode sNode = statementIter.next();
			IntermediateCodeGeneratorJb.differentiateNode((ASTNodeJb)sNode);
		}
		

		// pop the symbol scope and the renaming scope
		IntermediateCodeGeneratorJb.latestIdentifierNames.pop();
		IntermediateCodeGeneratorJb.symbolTable.pop();
		}

}
