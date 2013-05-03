package swp_compiler_ss13.javabite.codegen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ast.ASTNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.ir.IntermediateCodeGenerator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;

public class JavaBiteCodeGenerator implements IntermediateCodeGenerator {
	
	List<Quadruple> quadruples;
	
	JavaBiteCodeGenerator() {
		quadruples = new ArrayList<Quadruple>();
	}
	
	@Override
	public List<Quadruple> generateIntermediateCode(AST ast) throws IntermediateCodeGeneratorException {
		
		//TODO: iterate over AST and add quadruples to quadruple list
		Iterator<ASTNode> it = ast.getDFSLTRIterator();
		while (it.hasNext()) {
			ASTNode node = it.next();
			switch (node.getNodeType()){
				// TODO: convert classes
				default: break;
			}
		}
		
		return quadruples;
	}
}
