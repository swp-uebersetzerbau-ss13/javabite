package swp_compiler_ss13.javabite.codegen.beta;

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
	

	
	public static void convert(BlockNode node) throws IntermediateCodeGeneratorException {
		// push new renaming scope
		IRCodeGenerator.currentScopeRenames.push(new HashMap<String, String>());
		// push current symbol table
		IRCodeGenerator.currentSymbolTable.push(node.getSymbolTable());

		Iterator<ASTNode> astDFSLTR = node.getDFSLTRNodeIterator();
		//int counter=0;
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
					IRCodeGenerator.differentiateNode((ASTNodeJb) statement);
				}
				else{}
				break;
			}
		}
		// get declarations
//		Iterator<DeclarationNode> declIterator = node.getDeclarationIterator();
//		while (declIterator.hasNext()) {
//			DeclarationNodeCG decNCG = new DeclarationNodeCG();
//			decNCG.convert(declIterator.next());
//		}
//
//		//Iterator<StatementNode> statementIterator = node.getStatementIterator();
//		 
//		
//		while (statementIterator.hasNext()) {
//			StatementNode statement = statementIterator.next();
//			IRCodeGenerator.differentiateNode((ASTNodeJb) statement);
//		}

		// pop the symbol scope and the renaming scope
		IRCodeGenerator.currentScopeRenames.pop();
		IRCodeGenerator.currentSymbolTable.pop();
		}

}
