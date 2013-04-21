package swp_compiler_ss13.common.backend;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.backend.InstructionBuilder;

public class InstructionBuilderTests {

	static final Logger log = LoggerFactory
	    .getLogger(InstructionBuilderTests.class);

	InstructionBuilder builder;

	@Before
	public void setUp() {
		this.builder = InstructionBuilder.createWithBytecodeVisualizer();
	}

	@Test
	public void testUnaryMinusNumOperation() {
		builder.UNARY_MINUS_NUM(0);
		log.info(builder.toString());
	}

}
