package swp_compiler_ss13.javabite.backend;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.javabite.backend.Program.ProgramBuilder;
import swp_compiler_ss13.javabite.backend.external.QuadrupleImpl;

public class ProgramTests
{

	static final String methodName = "main";
	static final Logger log = LoggerFactory.getLogger(ProgramTests.class);

	Classfile classfile;
	ProgramBuilder pb;

	@Before
	public void setUp() {
		classfile = new Classfile("main.class", "tests/example",
				"java/lang/Object", Classfile.ClassfileAccessFlag.ACC_PUBLIC,
				Classfile.ClassfileAccessFlag.ACC_SUPER);
		classfile.addMethodToMethodArea(methodName, "([Ljava/lang/String;])V",
				Classfile.MethodAccessFlag.ACC_PUBLIC,
				Classfile.MethodAccessFlag.ACC_STATIC);
		pb = ProgramBuilder.newBuilder(classfile, methodName);
	}

	@Test
	public void testCLongToDouble() {
		classfile.addConstantToConstantPool("LONG", "1234");
		classfile.addVariableToMethodsCode(methodName, "longTest",
				IClassfile.VariableType.LONG);
		final Quadruple q = new QuadrupleImpl(
				Quadruple.Operator.LONG_TO_DOUBLE, "#1234", "!", "longTest");
		pb.longToDouble(q);
		final Program p = pb.build();
		log.info("testCLongToDouble\n{}", p);
	}

}
