package swp_compiler_ss13.javabite.lexer;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import swp_compiler_ss13.common.lexer.LexerContractTest;

public class LexerTests extends LexerContractTest<LexerJb> {
	
	@Override
	protected LexerJb getLexerInstance() {
		return new LexerJb();
	}
}