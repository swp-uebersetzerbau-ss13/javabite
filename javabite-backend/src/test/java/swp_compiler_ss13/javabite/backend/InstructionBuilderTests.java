package swp_compiler_ss13.javabite.backend;

import org.junit.Before;
import org.junit.Test;

import swp_compiler_ss13.javabite.backend.InstructionBuilder;

public class InstructionBuilderTests {

	InstructionBuilder builder;

	@Before
	public void setUp() {
		this.builder = InstructionBuilder.createWithBytecodeVisualizer();
	}

	@Test
	public void testUnaryMinusNumOperation() {
		builder.UNARY_MINUS_NUM(0);
	}

}
