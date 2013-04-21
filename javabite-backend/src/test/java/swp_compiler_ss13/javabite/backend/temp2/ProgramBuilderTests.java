package swp_compiler_ss13.javabite.backend.temp2;

import org.junit.Before;
import org.junit.Test;

import swp_compiler_ss13.javabite.backend.temp2.PresetTemplates;
import swp_compiler_ss13.javabite.backend.temp2.ProgramBuilder;

public class ProgramBuilderTests {

	ProgramBuilder builder;

	@Before
	public void setUp() {
		builder = ProgramBuilder.newBuilder();
	}

	@Test
	public void testUnaryMinusNumOperation() {
		builder.add(PresetTemplates.UNARY_MINUS_NUM, 0);
	}

}
