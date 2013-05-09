package swp_compiler_ss13.javabite.codegen.beta;

import swp_compiler_ss13.common.ast.AST;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.javabite.ast.ASTSource;

public class CodeGenTester {

	public static void main(String[] args){
		
		AST ast = ASTSource.getFirstAST();
		IRCodeGenerator codegen = new IRCodeGenerator();
		try {
			codegen.generateIntermediateCode(ast);
			System.out.println(codegen.irCode);
		} catch (IntermediateCodeGeneratorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
