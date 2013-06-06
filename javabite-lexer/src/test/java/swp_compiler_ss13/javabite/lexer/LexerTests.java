package swp_compiler_ss13.javabite.lexer;

import swp_compiler_ss13.common.lexer.LexerContractTest;

public class LexerTests extends LexerContractTest<LexerJb> {
	
	@Override
	protected LexerJb getLexerInstance() {
		return new LexerJb();
	}
}