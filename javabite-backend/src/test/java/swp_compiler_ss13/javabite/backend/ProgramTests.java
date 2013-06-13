package swp_compiler_ss13.javabite.backend;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.external.QuadrupleImpl;

public class ProgramTests {

	static final String methodName = "main";
	static final Logger log = LoggerFactory.getLogger(ProgramTests.class);

	Classfile classfile;
	Program.Builder pb;

	@Rule
	public TestName name = new TestName();

	@Before
	public void setUp() {
		classfile = new Classfile("main.class", "tests/example",
				"java/lang/Object", Classfile.ClassfileAccessFlag.ACC_PUBLIC,
				Classfile.ClassfileAccessFlag.ACC_SUPER);
		classfile.addMethodToMethodArea(methodName, "([Ljava/lang/String;])V",
				Classfile.MethodAccessFlag.ACC_PUBLIC,
				Classfile.MethodAccessFlag.ACC_STATIC);
		pb = new Program.Builder(classfile, methodName);
	}

	@SuppressWarnings("unused")
	private void buildAndLog(final Program.Builder pb) {
		final Program p = pb.build();
		System.out.println(name.getMethodName());
		final String bex = p.toHexString();
		final String bex2 = bex != null ? bex.trim().replaceAll(" ",
				", (byte)0x") : bex;
		System.out.println("final byte[] bExpected = new byte[] { (byte)0x"
				+ bex2 + " };\nfinal String sExpected = \""
				+ p.toString().replaceAll("\n", "\\\\n")
				+ "\";\nmakeAssertions(pb, bExpected, sExpected);");
	}

	private void makeAssertions(final Program.Builder pb,
			final byte[] bExpected, final String sExpected) {
		final Program p = pb.build();
		Assert.assertArrayEquals("byte array compare", bExpected,
				p.toByteArray());
		Assert.assertEquals("string compare", sExpected, p.toString());
	}

	private void addLongVariable(final String... vars) {
		for (final String v : vars) {
			classfile.addLongVariableToMethodsCode(methodName, v);
		}
	}

	private void addDoubleVariable(final String... vars) {
		for (final String v : vars) {
			classfile.addDoubleVariableToMethodsCode(methodName, v);
		}
	}

	private void addStringVariable(final String... vars) {
		for (final String v : vars) {
			classfile.addStringVariableToMethodsCode(methodName, v);
		}
	}

	private void addBooleanVariable(final String... vars) {
		for (final String v : vars) {
			classfile.addBooleanVariableToMethodsCode(methodName, v);
		}
	}

	private void addLongConstant(final long... vals) {
		for (final long v : vals) {
			classfile.addLongConstantToConstantPool(v);
		}
	}

	private void addDoubleConstant(final double... vals) {
		for (final double v : vals) {
			classfile.addDoubleConstantToConstantPool(v, Double.toString(v));
		}
	}

	private void addStringConstant(final String... vals) {
		for (final String v : vals) {
			classfile.addStringConstantToConstantPool(v);
		}
	}

	@Test
	public void testCLongToDouble() {
		addLongConstant(1234);
		addDoubleVariable("doubleTest");
		pb.longToDouble(new QuadrupleImpl(Quadruple.Operator.LONG_TO_DOUBLE,
				"#1234", "!", "doubleTest"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x8a, (byte) 0x48, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nL2D\nDSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVLongToDouble() {
		addLongVariable("longTest");
		addDoubleVariable("doubleTest");
		pb.longToDouble(new QuadrupleImpl(Operator.LONG_TO_DOUBLE, "longTest",
				"!", "doubleTest"));
		final byte[] bExpected = new byte[] { (byte) 0x1f, (byte) 0x8a,
				(byte) 0x4a, (byte) 0xb1 };
		final String sExpected = "LLOAD_1\nL2D\nDSTORE_3\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCDoubleToLong() {
		addDoubleConstant(12.34);
		addLongVariable("longTest");
		pb.doubleToLong(new QuadrupleImpl(Quadruple.Operator.DOUBLE_TO_LONG,
				"#12.34", "!", "longTest"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x8f, (byte) 0x40, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nD2L\nLSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVDoubleToLong() {
		addDoubleVariable("doubleTest");
		addLongVariable("longTest");
		pb.doubleToLong(new QuadrupleImpl(Operator.DOUBLE_TO_LONG,
				"doubleTest", "!", "longTest"));
		final byte[] bExpected = new byte[] { (byte) 0x27, (byte) 0x8f,
				(byte) 0x42, (byte) 0xb1 };
		final String sExpected = "DLOAD_1\nD2L\nLSTORE_3\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCAssignLong() {
		addLongConstant(1234);
		addLongVariable("longTest");
		pb.assignLong(new QuadrupleImpl(Operator.ASSIGN_LONG, "#1234", "!",
				"longTest"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x40, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVAssignLong() {
		addLongConstant(1234);
		addLongVariable("longTest1", "longTest2");
		pb.assignLong(new QuadrupleImpl(Operator.ASSIGN_LONG, "#1234", "!",
				"longTest2"));
		pb.assignLong(new QuadrupleImpl(Operator.ASSIGN_LONG, "longTest2", "!",
				"longTest1"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x42, (byte) 0x21, (byte) 0x40, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLSTORE_3\nLLOAD_3\nLSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCAssignDouble() {
		addDoubleConstant(12.34);
		addDoubleVariable("doubleTest");
		pb.assignDouble(new QuadrupleImpl(Operator.ASSIGN_DOUBLE, "#12.34",
				"!", "doubleTest"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x48, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nDSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVAssignDouble() {
		addDoubleConstant(12.34);
		addDoubleVariable("doubleTest1", "doubleTest2");
		pb.assignDouble(new QuadrupleImpl(Operator.ASSIGN_DOUBLE, "#12.34",
				"!", "doubleTest2"));
		pb.assignDouble(new QuadrupleImpl(Operator.ASSIGN_DOUBLE,
				"doubleTest2", "!", "doubleTest1"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x4a, (byte) 0x29, (byte) 0x48, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nDSTORE_3\nDLOAD_3\nDSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCAssignString() {
		addStringConstant("\"test\"");
		addStringVariable("stringTest");
		pb.assignString(new QuadrupleImpl(Operator.ASSIGN_STRING, "#\"test\"",
				"!", "stringTest"));
		final byte[] bExpected = new byte[] { (byte) 0x12, (byte) 0x0d,
				(byte) 0x4c, (byte) 0xb1 };
		final String sExpected = "LDC 0D\nASTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCAddLong() {
		addLongConstant(1000, 234);
		addLongVariable("longTest");
		pb.addLong(new QuadrupleImpl(Operator.ADD_LONG, "#1000", "#234",
				"longTest"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x61, (byte) 0x40, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nLADD\nLSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVVAddLong() {
		addLongVariable("longTest1", "longTest2");
		pb.addLong(new QuadrupleImpl(Operator.ADD_LONG, "longTest1",
				"longTest2", "longTest1"));
		final byte[] bExpected = new byte[] { (byte) 0x1f, (byte) 0x21,
				(byte) 0x61, (byte) 0x40, (byte) 0xb1 };
		final String sExpected = "LLOAD_1\nLLOAD_3\nLADD\nLSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCAddDouble() {
		addDoubleConstant(12.34, 43.21);
		addDoubleVariable("doubleTest");
		pb.addDouble(new QuadrupleImpl(Operator.ADD_DOUBLE, "#12.34", "#43.21",
				"doubleTest"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x63, (byte) 0x48, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nDADD\nDSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVVAddDouble() {
		addDoubleVariable("doubleTest1", "doubleTest2");
		pb.addDouble(new QuadrupleImpl(Operator.ADD_DOUBLE, "doubleTest1",
				"doubleTest2", "doubleTest1"));
		final byte[] bExpected = new byte[] { (byte) 0x27, (byte) 0x29,
				(byte) 0x63, (byte) 0x48, (byte) 0xb1 };
		final String sExpected = "DLOAD_1\nDLOAD_3\nDADD\nDSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCSubLong() {
		addLongConstant(1000, 234);
		addLongVariable("longTest");
		pb.subLong(new QuadrupleImpl(Operator.SUB_LONG, "#1000", "#234",
				"longTest"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x65, (byte) 0x40, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nLSUB\nLSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVVSubLong() {
		addLongVariable("longTest1", "longTest2");
		pb.subLong(new QuadrupleImpl(Operator.SUB_LONG, "longTest1",
				"longTest2", "longTest1"));
		final byte[] bExpected = new byte[] { (byte) 0x1f, (byte) 0x21,
				(byte) 0x65, (byte) 0x40, (byte) 0xb1 };
		final String sExpected = "LLOAD_1\nLLOAD_3\nLSUB\nLSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCSubDouble() {
		addDoubleConstant(12.34, 43.21);
		addDoubleVariable("doubleTest");
		pb.subDouble(new QuadrupleImpl(Operator.SUB_DOUBLE, "#12.34", "#43.21",
				"doubleTest"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x67, (byte) 0x48, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nDSUB\nDSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVVSubDouble() {
		addDoubleVariable("doubleTest1", "doubleTest2");
		pb.subDouble(new QuadrupleImpl(Operator.SUB_DOUBLE, "doubleTest1",
				"doubleTest2", "doubleTest1"));
		final byte[] bExpected = new byte[] { (byte) 0x27, (byte) 0x29,
				(byte) 0x67, (byte) 0x48, (byte) 0xb1 };
		final String sExpected = "DLOAD_1\nDLOAD_3\nDSUB\nDSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCMulLong() {
		addLongConstant(1000, 234);
		addLongVariable("longTest");
		pb.mulLong(new QuadrupleImpl(Operator.MUL_LONG, "#1000", "#234",
				"longTest"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x69, (byte) 0x40, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nLMUL\nLSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVVMulLong() {
		addLongVariable("longTest1", "longTest2");
		pb.mulLong(new QuadrupleImpl(Operator.MUL_LONG, "longTest1",
				"longTest2", "longTest1"));
		final byte[] bExpected = new byte[] { (byte) 0x1f, (byte) 0x21,
				(byte) 0x69, (byte) 0x40, (byte) 0xb1 };
		final String sExpected = "LLOAD_1\nLLOAD_3\nLMUL\nLSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCMulDouble() {
		addDoubleConstant(12.34, 43.21);
		addDoubleVariable("doubleTest");
		pb.mulDouble(new QuadrupleImpl(Operator.MUL_DOUBLE, "#12.34", "#43.21",
				"doubleTest"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x6b, (byte) 0x48, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nDMUL\nDSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVVMulDouble() {
		addDoubleVariable("doubleTest1", "doubleTest2");
		pb.mulDouble(new QuadrupleImpl(Operator.MUL_DOUBLE, "doubleTest1",
				"doubleTest2", "doubleTest1"));
		final byte[] bExpected = new byte[] { (byte) 0x27, (byte) 0x29,
				(byte) 0x6b, (byte) 0x48, (byte) 0xb1 };
		final String sExpected = "DLOAD_1\nDLOAD_3\nDMUL\nDSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCDivLong() {
		addLongConstant(1000, 234);
		addLongVariable("longTest");
		pb.divLong(new QuadrupleImpl(Operator.DIV_LONG, "#1000", "#234",
				"longTest"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x6d, (byte) 0x40, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nLDIV\nLSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVVDivLong() {
		addLongVariable("longTest1", "longTest2");
		pb.divLong(new QuadrupleImpl(Operator.DIV_LONG, "longTest1",
				"longTest2", "longTest1"));
		final byte[] bExpected = new byte[] { (byte) 0x1f, (byte) 0x21,
				(byte) 0x6d, (byte) 0x40, (byte) 0xb1 };
		final String sExpected = "LLOAD_1\nLLOAD_3\nLDIV\nLSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCDivDouble() {
		addDoubleConstant(12.34, 43.21);
		addDoubleVariable("doubleTest");
		pb.divDouble(new QuadrupleImpl(Operator.DIV_DOUBLE, "#12.34", "#43.21",
				"doubleTest"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x6f, (byte) 0x48, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nDDIV\nDSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVVDivDouble() {
		addDoubleVariable("doubleTest1", "doubleTest2");
		pb.divDouble(new QuadrupleImpl(Operator.DIV_DOUBLE, "doubleTest1",
				"doubleTest2", "doubleTest1"));
		final byte[] bExpected = new byte[] { 0x27, 0x29, 0x6f, 0x48,
				(byte) 0xb1 };
		final String sExpected = "DLOAD_1\nDLOAD_3\nDDIV\nDSTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCReturn() {
		addLongConstant(1000);
		pb.returnLong(new QuadrupleImpl(Operator.RETURN, "#1000", "!", "!"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x88, (byte) 0xb8, (byte) 0x00,
				(byte) 0x13, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nL2I\nINVOKESTATIC 00 13\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCNotBoolean() {
		addBooleanVariable("test");
		pb.notBoolean(new QuadrupleImpl(Operator.NOT_BOOLEAN, "#true", "!",
				"test"));
		final byte[] bExpected = new byte[] { (byte) 0x04, (byte) 0x9a,
				(byte) 0x00, (byte) 0x07, (byte) 0x04, (byte) 0xa7,
				(byte) 0x00, (byte) 0x04, (byte) 0x03, (byte) 0x3c, (byte) 0xb1 };
		final String sExpected = "ICONST_1\nIFNE 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCAndBoolean() {
		addBooleanVariable("test");
		pb.andBoolean(new QuadrupleImpl(Operator.AND_BOOLEAN, "#true", "#true",
				"test"));
		final byte[] bExpected = new byte[] { (byte) 0x04, (byte) 0x04,
				(byte) 0x7e, (byte) 0x3c, (byte) 0xb1 };
		final String sExpected = "ICONST_1\nICONST_1\nIAND\nISTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCOrBoolean() {
		addBooleanVariable("test");
		pb.orBoolean(new QuadrupleImpl(Operator.OR_BOOLEAN, "#true", "#false",
				"test"));
		final byte[] bExpected = new byte[] { (byte) 0x04, (byte) 0x03,
				(byte) 0x80, (byte) 0x3c, (byte) 0xb1 };
		final String sExpected = "ICONST_1\nICONST_0\nIOR\nISTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCompareLongE() {
		addLongConstant(1, 2);
		addBooleanVariable("test");
		pb.compareLongE(new QuadrupleImpl(Operator.COMPARE_LONG_E, "#1", "#2",
				"test"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x94, (byte) 0x9a, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x3c, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nLCMP\nIFNE 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCompareLongG() {
		addLongConstant(1, 2);
		addBooleanVariable("test");
		pb.compareLongG(new QuadrupleImpl(Operator.COMPARE_LONG_G, "#1", "#2",
				"test"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x94, (byte) 0x9e, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x3c, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nLCMP\nIFLE 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCompareLongL() {
		addLongConstant(1, 2);
		addBooleanVariable("test");
		pb.compareLongL(new QuadrupleImpl(Operator.COMPARE_LONG_L, "#1", "#2",
				"test"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x94, (byte) 0x9c, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x3c, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nLCMP\nIFGE 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCompareLongGE() {
		addLongConstant(1, 2);
		addBooleanVariable("test");
		pb.compareLongGE(new QuadrupleImpl(Operator.COMPARE_LONG_GE, "#1",
				"#2", "test"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x94, (byte) 0x9b, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x3c, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nLCMP\nIFLT 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCompareLongLE() {
		addLongConstant(1, 2);
		addBooleanVariable("test");
		pb.compareLongLE(new QuadrupleImpl(Operator.COMPARE_LONG_LE, "#1",
				"#2", "test"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x94, (byte) 0x9d, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x3c, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nLCMP\nIFGT 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVCompareLongE() {
		addLongVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareLongE(new QuadrupleImpl(Operator.COMPARE_LONG_E, "lhs",
				"rhs", "test"));
		final byte[] bExpected = new byte[] { (byte) 0x1f, (byte) 0x21,
				(byte) 0x94, (byte) 0x9a, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x36, (byte) 0x05, (byte) 0xb1 };
		final String sExpected = "LLOAD_1\nLLOAD_3\nLCMP\nIFNE 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE 05\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVCompareLongG() {
		addLongVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareLongG(new QuadrupleImpl(Operator.COMPARE_LONG_G, "lhs",
				"rhs", "test"));
		final byte[] bExpected = new byte[] { (byte) 0x1f, (byte) 0x21,
				(byte) 0x94, (byte) 0x9e, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x36, (byte) 0x05, (byte) 0xb1 };
		final String sExpected = "LLOAD_1\nLLOAD_3\nLCMP\nIFLE 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE 05\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVCompareLongL() {
		addLongVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareLongL(new QuadrupleImpl(Operator.COMPARE_LONG_L, "lhs",
				"rhs", "test"));
		final byte[] bExpected = new byte[] { (byte) 0x1f, (byte) 0x21,
				(byte) 0x94, (byte) 0x9c, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x36, (byte) 0x05, (byte) 0xb1 };
		final String sExpected = "LLOAD_1\nLLOAD_3\nLCMP\nIFGE 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE 05\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVCompareLongGE() {
		addLongVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareLongGE(new QuadrupleImpl(Operator.COMPARE_LONG_GE, "lhs",
				"rhs", "test"));
		final byte[] bExpected = new byte[] { (byte) 0x1f, (byte) 0x21,
				(byte) 0x94, (byte) 0x9b, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x36, (byte) 0x05, (byte) 0xb1 };
		final String sExpected = "LLOAD_1\nLLOAD_3\nLCMP\nIFLT 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE 05\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVCompareLongLE() {
		addLongVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareLongLE(new QuadrupleImpl(Operator.COMPARE_LONG_LE, "lhs",
				"rhs", "test"));
		final byte[] bExpected = new byte[] { (byte) 0x1f, (byte) 0x21,
				(byte) 0x94, (byte) 0x9d, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x36, (byte) 0x05, (byte) 0xb1 };
		final String sExpected = "LLOAD_1\nLLOAD_3\nLCMP\nIFGT 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE 05\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCompareDoubleE() {
		addDoubleConstant(1.2, 2.5);
		addBooleanVariable("test");
		pb.compareDoubleE(new QuadrupleImpl(Operator.COMPARE_DOUBLE_E, "#1.2",
				"#2.5", "test"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x97, (byte) 0x9a, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x3c, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nDCMPL\nIFNE 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCompareDoubleG() {
		addDoubleConstant(1.2, 2.5);
		addBooleanVariable("test");
		pb.compareDoubleG(new QuadrupleImpl(Operator.COMPARE_DOUBLE_G, "#1.2",
				"#2.5", "test"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x97, (byte) 0x9e, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x3c, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nDCMPL\nIFLE 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCompareDoubleL() {
		addDoubleConstant(1.2, 2.5);
		addBooleanVariable("test");
		pb.compareDoubleL(new QuadrupleImpl(Operator.COMPARE_DOUBLE_L, "#1.2",
				"#2.5", "test"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x98, (byte) 0x9c, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x3c, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nDCMPG\nIFGE 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCompareDoubleGE() {
		addDoubleConstant(1.2, 2.5);
		addBooleanVariable("test");
		pb.compareDoubleGE(new QuadrupleImpl(Operator.COMPARE_DOUBLE_GE,
				"#1.2", "#2.5", "test"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x97, (byte) 0x9b, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x3c, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nDCMPL\nIFLT 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testCCompareDoubleLE() {
		addDoubleConstant(1.2, 2.5);
		addBooleanVariable("test");
		pb.compareDoubleLE(new QuadrupleImpl(Operator.COMPARE_DOUBLE_LE,
				"#1.2", "#2.5", "test"));
		final byte[] bExpected = new byte[] { (byte) 0x14, (byte) 0x00,
				(byte) 0x0c, (byte) 0x14, (byte) 0x00, (byte) 0x0e,
				(byte) 0x98, (byte) 0x9d, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x3c, (byte) 0xb1 };
		final String sExpected = "LDC2_W 00 0C\nLDC2_W 00 0E\nDCMPG\nIFGT 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE_1\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVCompareDoubleE() {
		addDoubleVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareDoubleE(new QuadrupleImpl(Operator.COMPARE_DOUBLE_E, "lhs",
				"rhs", "test"));
		final byte[] bExpected = new byte[] { (byte) 0x27, (byte) 0x29,
				(byte) 0x97, (byte) 0x9a, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x36, (byte) 0x05, (byte) 0xb1 };
		final String sExpected = "DLOAD_1\nDLOAD_3\nDCMPL\nIFNE 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE 05\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVCompareDoubleG() {
		addDoubleVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareDoubleG(new QuadrupleImpl(Operator.COMPARE_DOUBLE_G, "lhs",
				"rhs", "test"));
		final byte[] bExpected = new byte[] { (byte) 0x27, (byte) 0x29,
				(byte) 0x97, (byte) 0x9e, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x36, (byte) 0x05, (byte) 0xb1 };
		final String sExpected = "DLOAD_1\nDLOAD_3\nDCMPL\nIFLE 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE 05\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVCompareDoubleL() {
		addDoubleVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareDoubleL(new QuadrupleImpl(Operator.COMPARE_DOUBLE_L, "lhs",
				"rhs", "test"));
		final byte[] bExpected = new byte[] { (byte) 0x27, (byte) 0x29,
				(byte) 0x98, (byte) 0x9c, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x36, (byte) 0x05, (byte) 0xb1 };
		final String sExpected = "DLOAD_1\nDLOAD_3\nDCMPG\nIFGE 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE 05\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVCompareDoubleGE() {
		addDoubleVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareDoubleGE(new QuadrupleImpl(Operator.COMPARE_DOUBLE_GE, "lhs",
				"rhs", "test"));
		final byte[] bExpected = new byte[] { (byte) 0x27, (byte) 0x29,
				(byte) 0x97, (byte) 0x9b, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x36, (byte) 0x05, (byte) 0xb1 };
		final String sExpected = "DLOAD_1\nDLOAD_3\nDCMPL\nIFLT 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE 05\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testVCompareDoubleLE() {
		addDoubleVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareDoubleLE(new QuadrupleImpl(Operator.COMPARE_DOUBLE_LE, "lhs",
				"rhs", "test"));
		final byte[] bExpected = new byte[] { (byte) 0x27, (byte) 0x29,
				(byte) 0x98, (byte) 0x9d, (byte) 0x00, (byte) 0x07,
				(byte) 0x04, (byte) 0xa7, (byte) 0x00, (byte) 0x04,
				(byte) 0x03, (byte) 0x36, (byte) 0x05, (byte) 0xb1 };
		final String sExpected = "DLOAD_1\nDLOAD_3\nDCMPG\nIFGT 00 07\nICONST_1\nGOTO 00 04\nICONST_0\nISTORE 05\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testUncBranchForward() {
		pb.branch(new QuadrupleImpl(Operator.BRANCH, "testLabel", "!", "!"));
		pb.label(new QuadrupleImpl(Operator.LABEL, "testLabel", "!", "!"));
		pb.nop();
		final byte[] bExpected = new byte[] { (byte) 0xa7, (byte) 0x00,
				(byte) 0x03, (byte) 0x00, (byte) 0xb1 };
		final String sExpected = "GOTO 00 03\nNOP\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

	@Test
	public void testUncBranchBackward() {
		pb.label(new QuadrupleImpl(Operator.LABEL, "testLabel", "!", "!"));
		pb.nop();
		pb.branch(new QuadrupleImpl(Operator.BRANCH, "testLabel", "!", "!"));
		final byte[] bExpected = new byte[] { (byte) 0x00, (byte) 0xa7,
				(byte) 0xff, (byte) 0xff, (byte) 0xb1 };
		final String sExpected = "NOP\nGOTO FF FF\nRETURN\n";
		makeAssertions(pb, bExpected, sExpected);
	}

}
