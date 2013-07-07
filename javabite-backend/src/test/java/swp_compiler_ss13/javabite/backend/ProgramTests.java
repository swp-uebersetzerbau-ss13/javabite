package swp_compiler_ss13.javabite.backend;

import org.junit.*;
import org.junit.rules.TestName;
import org.junit.runners.MethodSorters;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.translation.MainBuilder;
import swp_compiler_ss13.javabite.backend.translation.Program;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.ClassfileAccessFlag;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.MethodAccessFlag;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProgramTests {

	static final String methodName = "main";

	Classfile classfile;
	MainBuilder pb;

	@Rule
	public TestName name = new TestName();

	private void addBooleanVariable(final String... vars) {
		for (final String v : vars) {
			classfile.addVariableToMethodsCode(methodName, v,
					ClassfileUtils.LocalVariableType.BOOLEAN);
		}
	}

	private void addDoubleConstant(final double... vals) {
		for (final double v : vals) {
			classfile.addDoubleConstantToConstantPool(v, Double.toString(v));
		}
	}

	private void addDoubleVariable(final String... vars) {
		for (final String v : vars) {
			classfile.addVariableToMethodsCode(methodName, v,
					ClassfileUtils.LocalVariableType.DOUBLE);
		}
	}

	private void addLongConstant(final long... vals) {
		for (final long v : vals) {
			classfile.addLongConstantToConstantPool(v);
		}
	}

	private void addLongVariable(final String... vars) {
		for (final String v : vars) {
			classfile.addVariableToMethodsCode(methodName, v,
					ClassfileUtils.LocalVariableType.LONG);
		}
	}

	private void addStringConstant(final String... vals) {
		for (final String v : vals) {
			classfile.addStringConstantToConstantPool(v);
		}
	}

	private void addStringVariable(final String... vars) {
		for (final String v : vars) {
			classfile.addVariableToMethodsCode(methodName, v,
					ClassfileUtils.LocalVariableType.STRING);
		}
	}

	private void makeAssertions(final MainBuilder pb, final String sExpected) {
		final Program p = pb.build();
		Assert.assertTrue(p.toString().matches(sExpected));
	}

	@Before
	public void setUp() {
		classfile = new Classfile("main", "java/lang/Object", false,
				ClassfileAccessFlag.ACC_PUBLIC, ClassfileAccessFlag.ACC_SUPER);
		classfile.addMethodToMethodArea(methodName, "([Ljava/lang/String;])V",
				MethodAccessFlag.ACC_PUBLIC, MethodAccessFlag.ACC_STATIC);
		pb = new MainBuilder(classfile, methodName);
	}

	public static String NL = "\\\n";

	public static String HEX(final int i) {
		return "(\\s[0-9a-fA-F]{2})" + (i > 1 ? "{" + i + "}" : "");
	}

	public static String CMD(final String c) {
		return "((" + c + "_[0-9]" + ")|(" + c + " [0-9]{1,2}))";
	}

	@Test
	public void testCAndBoolean() {
		addBooleanVariable("test");
		pb.andBoolean(new QuadrupleJb(Operator.AND_BOOLEAN, "#true", "#true",
				"test"));
		final String sExpected = "(?i)ICONST_1" + NL + "ICONST_1" + NL + "IAND"
				+ NL + CMD("ISTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCAssignDouble() {
		addDoubleConstant(12.34);
		addDoubleVariable("doubleTest");
		pb.assignDouble(new QuadrupleJb(Operator.ASSIGN_DOUBLE, "#12.34", "!",
				"doubleTest"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + CMD("DSTORE")
				+ NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCAssignLong() {
		addLongConstant(1234);
		addLongVariable("longTest");
		pb.assignLong(new QuadrupleJb(Operator.ASSIGN_LONG, "#1234", "!",
				"longTest"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + CMD("LSTORE")
				+ NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCAssignString() {
		addStringConstant("\"test\"");
		addStringVariable("stringTest");
		pb.assignString(new QuadrupleJb(Operator.ASSIGN_STRING, "#\"test\"",
				"!", "stringTest"));
		final String sExpected = "(?i)LDC" + HEX(1) + NL + CMD("ASTORE") + NL
				+ "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCAddDouble() {
		addDoubleConstant(12.34, 43.21);
		addDoubleVariable("doubleTest");
		pb.addDouble(new QuadrupleJb(Operator.ADD_DOUBLE, "#12.34", "#43.21",
				"doubleTest"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "DADD" + NL + CMD("DSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCAddLong() {
		addLongConstant(1000, 234);
		addLongVariable("longTest");
		pb.addLong(new QuadrupleJb(Operator.ADD_LONG, "#1000", "#234",
				"longTest"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "LADD" + NL + CMD("LSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCDivDouble() {
		addDoubleConstant(12.34, 43.21);
		addDoubleVariable("doubleTest");
		pb.divDouble(new QuadrupleJb(Operator.DIV_DOUBLE, "#12.34", "#43.21",
				"doubleTest"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "DDIV" + NL + CMD("DSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCDivLong() {
		addLongConstant(1000, 234);
		addLongVariable("longTest");
		pb.divLong(new QuadrupleJb(Operator.DIV_LONG, "#1000", "#234",
				"longTest"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "LDIV" + NL + CMD("LSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCMulDouble() {
		addDoubleConstant(12.34, 43.21);
		addDoubleVariable("doubleTest");
		pb.mulDouble(new QuadrupleJb(Operator.MUL_DOUBLE, "#12.34", "#43.21",
				"doubleTest"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "DMUL" + NL + CMD("DSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCMulLong() {
		addLongConstant(1000, 234);
		addLongVariable("longTest");
		pb.mulLong(new QuadrupleJb(Operator.MUL_LONG, "#1000", "#234",
				"longTest"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "LMUL" + NL + CMD("LSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCompareDoubleE() {
		addDoubleConstant(1.2, 2.5);
		addBooleanVariable("test");
		pb.compareDoubleE(new QuadrupleJb(Operator.COMPARE_DOUBLE_E, "#1.2",
				"#2.5", "test"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "DCMPL" + NL + "IFNE" + HEX(2) + NL + "ICONST_1" + NL
				+ "GOTO" + HEX(2) + NL + "ICONST_0" + NL + CMD("ISTORE") + NL
				+ "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCompareDoubleG() {
		addDoubleConstant(1.2, 2.5);
		addBooleanVariable("test");
		pb.compareDoubleG(new QuadrupleJb(Operator.COMPARE_DOUBLE_G, "#1.2",
				"#2.5", "test"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "DCMPL" + NL + "IFLE" + HEX(2) + NL + "ICONST_1" + NL
				+ "GOTO" + HEX(2) + NL + "ICONST_0" + NL + CMD("ISTORE") + NL
				+ "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCompareDoubleGE() {
		addDoubleConstant(1.2, 2.5);
		addBooleanVariable("test");
		pb.compareDoubleGE(new QuadrupleJb(Operator.COMPARE_DOUBLE_GE, "#1.2",
				"#2.5", "test"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "DCMPL" + NL + "IFLT" + HEX(2) + NL + "ICONST_1" + NL
				+ "GOTO" + HEX(2) + NL + "ICONST_0" + NL + CMD("ISTORE") + NL
				+ "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCompareDoubleL() {
		addDoubleConstant(1.2, 2.5);
		addBooleanVariable("test");
		pb.compareDoubleL(new QuadrupleJb(Operator.COMPARE_DOUBLE_L, "#1.2",
				"#2.5", "test"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "DCMPG" + NL + "IFGE" + HEX(2) + NL + "ICONST_1" + NL
				+ "GOTO" + HEX(2) + NL + "ICONST_0" + NL + CMD("ISTORE") + NL
				+ "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCompareDoubleLE() {
		addDoubleConstant(1.2, 2.5);
		addBooleanVariable("test");
		pb.compareDoubleLE(new QuadrupleJb(Operator.COMPARE_DOUBLE_LE, "#1.2",
				"#2.5", "test"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "DCMPG" + NL + "IFGT" + HEX(2) + NL + "ICONST_1" + NL
				+ "GOTO" + HEX(2) + NL + "ICONST_0" + NL + CMD("ISTORE") + NL
				+ "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCompareLongE() {
		addLongConstant(1, 2);
		addBooleanVariable("test");
		pb.compareLongE(new QuadrupleJb(Operator.COMPARE_LONG_E, "#1", "#2",
				"test"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "LCMP" + NL + "IFNE" + HEX(2) + NL + "ICONST_1" + NL
				+ "GOTO" + HEX(2) + NL + "ICONST_0" + NL + CMD("ISTORE") + NL
				+ "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCompareLongG() {
		addLongConstant(1, 2);
		addBooleanVariable("test");
		pb.compareLongG(new QuadrupleJb(Operator.COMPARE_LONG_G, "#1", "#2",
				"test"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "LCMP" + NL + "IFLE" + HEX(2) + NL + "ICONST_1" + NL
				+ "GOTO" + HEX(2) + NL + "ICONST_0" + NL + CMD("ISTORE") + NL
				+ "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCompareLongGE() {
		addLongConstant(1, 2);
		addBooleanVariable("test");
		pb.compareLongGE(new QuadrupleJb(Operator.COMPARE_LONG_GE, "#1", "#2",
				"test"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "LCMP" + NL + "IFLT" + HEX(2) + NL + "ICONST_1" + NL
				+ "GOTO" + HEX(2) + NL + "ICONST_0" + NL + CMD("ISTORE") + NL
				+ "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCompareLongL() {
		addLongConstant(1, 2);
		addBooleanVariable("test");
		pb.compareLongL(new QuadrupleJb(Operator.COMPARE_LONG_L, "#1", "#2",
				"test"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "LCMP" + NL + "IFGE" + HEX(2) + NL + "ICONST_1" + NL
				+ "GOTO" + HEX(2) + NL + "ICONST_0" + NL + CMD("ISTORE") + NL
				+ "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCompareLongLE() {
		addLongConstant(1, 2);
		addBooleanVariable("test");
		pb.compareLongLE(new QuadrupleJb(Operator.COMPARE_LONG_LE, "#1", "#2",
				"test"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "LCMP" + NL + "IFGT" + HEX(2) + NL + "ICONST_1" + NL
				+ "GOTO" + HEX(2) + NL + "ICONST_0" + NL + CMD("ISTORE") + NL
				+ "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCSubDouble() {
		addDoubleConstant(12.34, 43.21);
		addDoubleVariable("doubleTest");
		pb.subDouble(new QuadrupleJb(Operator.SUB_DOUBLE, "#12.34", "#43.21",
				"doubleTest"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "DSUB" + NL + CMD("DSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCCSubLong() {
		addLongConstant(1000, 234);
		addLongVariable("longTest");
		pb.subLong(new QuadrupleJb(Operator.SUB_LONG, "#1000", "#234",
				"longTest"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "LDC2_W" + HEX(2)
				+ NL + "LSUB" + NL + CMD("LSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCDoubleToLong() {
		addDoubleConstant(12.34);
		addLongVariable("longTest");
		pb.doubleToLong(new QuadrupleJb(Quadruple.Operator.DOUBLE_TO_LONG,
				"#12.34", "!", "longTest"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "D2L" + NL
				+ CMD("LSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCLongToDouble() {
		addLongConstant(1234);
		addDoubleVariable("doubleTest");
		pb.longToDouble(new QuadrupleJb(Quadruple.Operator.LONG_TO_DOUBLE,
				"#1234", "!", "doubleTest"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "L2D" + NL
				+ CMD("DSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCNotBoolean() {
		addBooleanVariable("test");
		pb.notBoolean(new QuadrupleJb(Operator.NOT_BOOLEAN, "#true", "!",
				"test"));
		final String sExpected = "(?i)ICONST_1" + NL + "IFNE" + HEX(2) + NL
				+ "ICONST_1" + NL + "GOTO" + HEX(2) + NL + "ICONST_0" + NL
				+ CMD("ISTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCOrBoolean() {
		addBooleanVariable("test");
		pb.orBoolean(new QuadrupleJb(Operator.OR_BOOLEAN, "#true", "#false",
				"test"));
		final String sExpected = "(?i)ICONST_1" + NL + "ICONST_0" + NL + "IOR"
				+ NL + CMD("ISTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testCReturn() {
		addLongConstant(1000);
		pb.returnLong(new QuadrupleJb(Operator.RETURN, "#1000", "!", "!"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + "L2I" + NL
				+ "INVOKESTATIC" + HEX(2) + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testUncBranchBackward() {
		pb.label(new QuadrupleJb(Operator.LABEL, "testLabel", "!", "!"));
		pb.nop();
		pb.branch(new QuadrupleJb(Operator.BRANCH, "testLabel", "!", "!"));
		final String sExpected = "(?i)NOP" + NL + "GOTO" + HEX(2) + NL
				+ "RETURN" + NL + "";
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testUncBranchForward() {
		pb.branch(new QuadrupleJb(Operator.BRANCH, "testLabel", "!", "!"));
		pb.label(new QuadrupleJb(Operator.LABEL, "testLabel", "!", "!"));
		pb.nop();
		final String sExpected = "(?i)GOTO" + HEX(2) + NL + "NOP" + NL
				+ "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVAssignDouble() {
		addDoubleConstant(12.34);
		addDoubleVariable("doubleTest1", "doubleTest2");
		pb.assignDouble(new QuadrupleJb(Operator.ASSIGN_DOUBLE, "#12.34", "!",
				"doubleTest2"));
		pb.assignDouble(new QuadrupleJb(Operator.ASSIGN_DOUBLE, "doubleTest2",
				"!", "doubleTest1"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + CMD("DSTORE")
				+ NL + CMD("DLOAD") + NL + CMD("DSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVAssignLong() {
		addLongConstant(1234);
		addLongVariable("longTest1", "longTest2");
		pb.assignLong(new QuadrupleJb(Operator.ASSIGN_LONG, "#1234", "!",
				"longTest2"));
		pb.assignLong(new QuadrupleJb(Operator.ASSIGN_LONG, "longTest2", "!",
				"longTest1"));
		final String sExpected = "(?i)LDC2_W" + HEX(2) + NL + CMD("LSTORE")
				+ NL + CMD("LLOAD") + NL + CMD("LSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVCompareDoubleE() {
		addDoubleVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareDoubleE(new QuadrupleJb(Operator.COMPARE_DOUBLE_E, "lhs",
				"rhs", "test"));
		final String sExpected = CMD("DLOAD") + NL + CMD("DLOAD") + NL
				+ "DCMPL" + NL + "IFNE" + HEX(2) + NL + "ICONST_1" + NL
				+ "GOTO" + HEX(2) + NL + "ICONST_0" + NL + CMD("ISTORE") + NL
				+ "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVCompareDoubleG() {
		addDoubleVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareDoubleG(new QuadrupleJb(Operator.COMPARE_DOUBLE_G, "lhs",
				"rhs", "test"));
		final String sExpected = CMD("DLOAD") + NL + CMD("DLOAD") + NL
				+ "DCMPL" + NL + "IFLE" + HEX(2) + NL + "ICONST_1" + NL
				+ "GOTO" + HEX(2) + NL + "ICONST_0" + NL + CMD("ISTORE") + NL
				+ "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVCompareDoubleGE() {
		addDoubleVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareDoubleGE(new QuadrupleJb(Operator.COMPARE_DOUBLE_GE, "lhs",
				"rhs", "test"));
		final String sExpected = CMD("DLOAD") + NL + CMD("DLOAD") + NL
				+ "DCMPL" + NL + "IFLT" + HEX(2) + NL + "ICONST_1" + NL
				+ "GOTO" + HEX(2) + NL + "ICONST_0" + NL + CMD("ISTORE") + NL
				+ "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVCompareDoubleL() {
		addDoubleVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareDoubleL(new QuadrupleJb(Operator.COMPARE_DOUBLE_L, "lhs",
				"rhs", "test"));
		final String sExpected = CMD("DLOAD") + NL + CMD("DLOAD") + NL
				+ "DCMPG" + NL + "IFGE" + HEX(2) + NL + "ICONST_1" + NL
				+ "GOTO" + HEX(2) + NL + "ICONST_0" + NL + CMD("ISTORE") + NL
				+ "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVCompareDoubleLE() {
		addDoubleVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareDoubleLE(new QuadrupleJb(Operator.COMPARE_DOUBLE_LE, "lhs",
				"rhs", "test"));
		final String sExpected = CMD("DLOAD") + NL + CMD("DLOAD") + NL
				+ "DCMPG" + NL + "IFGT" + HEX(2) + NL + "ICONST_1" + NL
				+ "GOTO" + HEX(2) + NL + "ICONST_0" + NL + CMD("ISTORE") + NL
				+ "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVCompareLongE() {
		addLongVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareLongE(new QuadrupleJb(Operator.COMPARE_LONG_E, "lhs", "rhs",
				"test"));
		final String sExpected = CMD("LLOAD") + NL + CMD("LLOAD") + NL + "LCMP"
				+ NL + "IFNE" + HEX(2) + NL + "ICONST_1" + NL + "GOTO" + HEX(2)
				+ NL + "ICONST_0" + NL + CMD("ISTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVCompareLongG() {
		addLongVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareLongG(new QuadrupleJb(Operator.COMPARE_LONG_G, "lhs", "rhs",
				"test"));
		final String sExpected = CMD("LLOAD") + NL + CMD("LLOAD") + NL + "LCMP"
				+ NL + "IFLE" + HEX(2) + NL + "ICONST_1" + NL + "GOTO" + HEX(2)
				+ NL + "ICONST_0" + NL + CMD("ISTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVCompareLongGE() {
		addLongVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareLongGE(new QuadrupleJb(Operator.COMPARE_LONG_GE, "lhs",
				"rhs", "test"));
		final String sExpected = CMD("LLOAD") + NL + CMD("LLOAD") + NL + "LCMP"
				+ NL + "IFLT" + HEX(2) + NL + "ICONST_1" + NL + "GOTO" + HEX(2)
				+ NL + "ICONST_0" + NL + CMD("ISTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVCompareLongL() {
		addLongVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareLongL(new QuadrupleJb(Operator.COMPARE_LONG_L, "lhs", "rhs",
				"test"));
		final String sExpected = CMD("LLOAD") + NL + CMD("LLOAD") + NL + "LCMP"
				+ NL + "IFGE" + HEX(2) + NL + "ICONST_1" + NL + "GOTO" + HEX(2)
				+ NL + "ICONST_0" + NL + CMD("ISTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVCompareLongLE() {
		addLongVariable("lhs", "rhs");
		addBooleanVariable("test");
		pb.compareLongLE(new QuadrupleJb(Operator.COMPARE_LONG_LE, "lhs",
				"rhs", "test"));
		final String sExpected = CMD("LLOAD") + NL + CMD("LLOAD") + NL + "LCMP"
				+ NL + "IFGT" + HEX(2) + NL + "ICONST_1" + NL + "GOTO" + HEX(2)
				+ NL + "ICONST_0" + NL + CMD("ISTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVDoubleToLong() {
		addDoubleVariable("doubleTest");
		addLongVariable("longTest");
		pb.doubleToLong(new QuadrupleJb(Operator.DOUBLE_TO_LONG, "doubleTest",
				"!", "longTest"));
		final String sExpected = CMD("DLOAD") + NL + "D2L" + NL + CMD("LSTORE")
				+ NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVLongToDouble() {
		addLongVariable("longTest");
		addDoubleVariable("doubleTest");
		pb.longToDouble(new QuadrupleJb(Operator.LONG_TO_DOUBLE, "longTest",
				"!", "doubleTest"));
		final String sExpected = CMD("LLOAD") + NL + "L2D" + NL + CMD("DSTORE")
				+ NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVVAddDouble() {
		addDoubleVariable("doubleTest1", "doubleTest2");
		pb.addDouble(new QuadrupleJb(Operator.ADD_DOUBLE, "doubleTest1",
				"doubleTest2", "doubleTest1"));
		final String sExpected = CMD("DLOAD") + NL + CMD("DLOAD") + NL + "DADD"
				+ NL + CMD("DSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVVAddLong() {
		addLongVariable("longTest1", "longTest2");
		pb.addLong(new QuadrupleJb(Operator.ADD_LONG, "longTest1", "longTest2",
				"longTest1"));
		final String sExpected = CMD("LLOAD") + NL + CMD("LLOAD") + NL + "LADD"
				+ NL + CMD("LSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVVDivDouble() {
		addDoubleVariable("doubleTest1", "doubleTest2");
		pb.divDouble(new QuadrupleJb(Operator.DIV_DOUBLE, "doubleTest1",
				"doubleTest2", "doubleTest1"));
		final String sExpected = CMD("DLOAD") + NL + CMD("DLOAD") + NL + "DDIV"
				+ NL + CMD("DSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVVDivLong() {
		addLongVariable("longTest1", "longTest2");
		pb.divLong(new QuadrupleJb(Operator.DIV_LONG, "longTest1", "longTest2",
				"longTest1"));
		final String sExpected = CMD("LLOAD") + NL + CMD("LLOAD") + NL + "LDIV"
				+ NL + CMD("LSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVVMulDouble() {
		addDoubleVariable("doubleTest1", "doubleTest2");
		pb.mulDouble(new QuadrupleJb(Operator.MUL_DOUBLE, "doubleTest1",
				"doubleTest2", "doubleTest1"));
		final String sExpected = CMD("DLOAD") + NL + CMD("DLOAD") + NL + "DMUL"
				+ NL + CMD("DSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVVMulLong() {
		addLongVariable("longTest1", "longTest2");
		pb.mulLong(new QuadrupleJb(Operator.MUL_LONG, "longTest1", "longTest2",
				"longTest1"));
		final String sExpected = CMD("LLOAD") + NL + CMD("LLOAD") + NL + "LMUL"
				+ NL + CMD("LSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVVSubDouble() {
		addDoubleVariable("doubleTest1", "doubleTest2");
		pb.subDouble(new QuadrupleJb(Operator.SUB_DOUBLE, "doubleTest1",
				"doubleTest2", "doubleTest1"));
		final String sExpected = CMD("DLOAD") + NL + CMD("DLOAD") + NL + "DSUB"
				+ NL + CMD("DSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

	@Test
	public void testVVSubLong() {
		addLongVariable("longTest1", "longTest2");
		pb.subLong(new QuadrupleJb(Operator.SUB_LONG, "longTest1", "longTest2",
				"longTest1"));
		final String sExpected = CMD("LLOAD") + NL + CMD("LLOAD") + NL + "LSUB"
				+ NL + CMD("LSTORE") + NL + "RETURN" + NL;
		makeAssertions(pb, sExpected);
	}

}
