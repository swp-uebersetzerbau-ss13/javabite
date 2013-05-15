package swp_compiler_ss13.javabite.codegen;

import java.util.HashMap;
import java.util.Iterator;

import swp_compiler_ss13.common.ast.ASTNode;
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

		// find out whether it is declaration node or an other node
		Iterator<ASTNode> astDFSLTR = node.getDFSLTRNodeIterator();
		while(astDFSLTR.hasNext()){
			ASTNode astNode = astDFSLTR.next();
			switch (astNode.getNodeType()){
			case DeclarationNode:
				DeclarationNodeCG decNCG = new DeclarationNodeCG();
				decNCG.convert((DeclarationNode) astNode);
				break;
			default:
				if((astDFSLTR.next()!=null)){
					System.out.println(astNode.getNodeType());
					StatementNode statement = (StatementNode) astNode;
					IntermediateCodeGeneratorJb.differentiateNode((ASTNodeJb) statement);
				}
				break;
			}
		}

		// pop the symbol scope and the renaming scope
		IntermediateCodeGeneratorJb.latestIdentifierNames.pop();
		IntermediateCodeGeneratorJb.symbolTable.pop();
		}

}
