//package swp_compiler_ss13.javabite.backend;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.TestName;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import swp_compiler_ss13.common.backend.Quadruple;
//import swp_compiler_ss13.common.backend.Quadruple.Operator;
//import swp_compiler_ss13.javabite.backend.IClassfile.ConstantType;
//import swp_compiler_ss13.javabite.backend.Program.ProgramBuilder;
//import swp_compiler_ss13.javabite.backend.external.QuadrupleImpl;
//
//public class ProgramTestsCurrent {
//	static final String methodName = "main";
//	static final Logger log = LoggerFactory.getLogger(ProgramTests.class);
//
//	Classfile classfile;
//	ProgramBuilder pb;
////	
//	IClassfile classfileMOCK;
//
//	@Rule
//	public TestName name = new TestName();
//
//	@Before
//	public void setUp() {
//		classfile = new Classfile("main.class", "tests/example", "java/lang/Object", Classfile.ClassfileAccessFlag.ACC_PUBLIC,
//				Classfile.ClassfileAccessFlag.ACC_SUPER);
//		classfile.addMethodToMethodArea(methodName, "([Ljava/lang/String;])V", Classfile.MethodAccessFlag.ACC_PUBLIC, Classfile.MethodAccessFlag.ACC_STATIC);
//		pb = ProgramBuilder.newBuilder(classfile, methodName);
//		
//		
//		classfileMOCK = mock(Classfile.class);
//	}
//	
//	@Test
//	public void testAssignLongValue() {
//		// needed information for mocking getIndexOfConstantInConstantPool
//		final String variableName =  "!";
//		final String methodName = "main";
//		// set expect return of getIndex... (1)
//		when(classfileMOCK.getIndexOfVariableInMethod(methodName, variableName)).thenReturn((short) 1);
//	
////		
//		// set needed Parameter for invokation of assingValue(...)
//		final Quadruple quadruple = new QuadrupleImpl(Operator.ASSIGN_LONG, "!", "!", "t");
//		final ConstantType type = ConstantType.LONG;
//		final String loadOp = "LDC2_W";
//		final Mnemonic mnemonic = Mnemonic.LDC2_W;
//		final String storeOp = "";
//		
//		//invoke private methode of Programm class
//		try {
//			ProgramBuilder o = org.powermock.reflect.Whitebox.<ProgramBuilder>invokeMethod(pb, "assignValue", new Object[]{quadruple, type, loadOp, mnemonic, storeOp});
//			System.out.println("");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
// }
