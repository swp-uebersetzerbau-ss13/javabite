package swp_compiler_ss13.javabite.backend.temp3;

import org.junit.Test;

import swp_compiler_ss13.javabite.backend.temp3.Program.ProgramBuilder;

public class ProgramTest {

	@Test
	public void testProgram() {
		
		ProgramBuilder pb = ProgramBuilder.newBuilder(0);
		pb.add(Operations.UNARY_MINUS_NUM(0));
		
	}
	
}
