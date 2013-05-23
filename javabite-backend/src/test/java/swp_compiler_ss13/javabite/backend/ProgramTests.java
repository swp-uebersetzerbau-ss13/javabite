package swp_compiler_ss13.javabite.backend;

import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.javabite.backend.Program.ProgramBuilder;
import swp_compiler_ss13.javabite.backend.external.QuadrupleImpl;

public class ProgramTests {

	static final String methodName = "main";
	static final Logger log = LoggerFactory.getLogger(ProgramTests.class);

	Classfile classfile;
	ProgramBuilder pb;

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
		pb = ProgramBuilder.newBuilder(classfile, methodName);
	}

	private void buildAndLog(final ProgramBuilder pb) {
		final Program p = pb.build();
		final String testName = name.getMethodName();
		try {
			final String resName = testName.substring(4) + ".res";
			final InputStream in = this.getClass().getClassLoader()
					.getResourceAsStream(resName);
			final StringWriter sw = new StringWriter();
			IOUtils.copy(in, sw);
			log.info("{}\n=== RESULT\n{}=== COMPARE\n{}", testName, p,
					sw.toString());
		} catch (final Exception e) {
			log.info("{}\n=== RESULT\n{}=== COMPARE\nNO .res FILE\n", testName,
					p);
		}
	}

	private void addLongVariable(final String variableName) {
		classfile.addLongVariableToMethodsCode(methodName, variableName);
	}

	private void addDoubleVariable(final String variableName) {
		classfile.addDoubleVariableToMethodsCode(methodName, variableName);
	}

	private void addStringVariable(final String variableName) {
		classfile.addStringVariableToMethodsCode(methodName, variableName);
	}

	private void addLongConstant(final long value) {
		classfile.addLongConstantToConstantPool(value);
	}

	private void addDoubleConstant(final double value) {
		classfile.addDoubleConstantToConstantPool(value);
	}

	private void addStringConstant(final String value) {
		classfile.addStringConstantToConstantPool(value);
	}

	/*
	 * convert long constant #1234 to double and store in double variable
	 * doubleTest
	 */
	@Test
	public void testCLongToDouble() {
		addLongConstant(1234);
		addDoubleVariable("doubleTest");
		pb.longToDouble(new QuadrupleImpl(Quadruple.Operator.LONG_TO_DOUBLE,
				"#1234", "!", "doubleTest"));
		buildAndLog(pb);
	}

	/*
	 * convert long variable longTest to double and store in double variable
	 * doubleTest
	 */
	@Test
	public void testVLongToDouble() {
		addLongVariable("longTest");
		addDoubleVariable("doubleTest");
		pb.longToDouble(new QuadrupleImpl(Operator.LONG_TO_DOUBLE, "longTest",
				"!", "doubleTest"));
		buildAndLog(pb);
	}

	/*
	 * convert double constant #12.34 to long and store in long variable
	 * longTest
	 */
	@Test
	public void testCDoubleToLong() {
		addDoubleConstant(12.34);
		addLongVariable("longTest");
		pb.doubleToLong(new QuadrupleImpl(Quadruple.Operator.DOUBLE_TO_LONG,
				"#12.34", "!", "longTest"));
		buildAndLog(pb);
	}

	/*
	 * convert double variable doubleTest to long and store in long variable
	 * longTest
	 */
	@Test
	public void testVDoubleToLong() {
		addDoubleVariable("doubleTest");
		addLongVariable("longTest");
		pb.doubleToLong(new QuadrupleImpl(Operator.DOUBLE_TO_LONG,
				"doubleTest", "!", "longTest"));
		buildAndLog(pb);
	}

	/*
	 * assign constant #1234 to long variable longTest
	 */
	@Test
	public void testCAssignLong() {
		addLongConstant(1234);
		addLongVariable("longTest");
		pb.assignLong(new QuadrupleImpl(Operator.ASSIGN_LONG, "#1234", "!",
				"longTest"));
		buildAndLog(pb);
	}

	/*
	 * assign #1234 to longTest2, then assign longTest2 to longTest1
	 */
	@Test
	public void testVAssignLong() {
		addLongConstant(1234);
		addLongVariable("longTest1");
		addLongVariable("longTest2");
		pb.assignLong(new QuadrupleImpl(Operator.ASSIGN_LONG, "#1234", "!",
				"longTest2"));
		pb.assignLong(new QuadrupleImpl(Operator.ASSIGN_LONG, "longTest2", "!",
				"longTest1"));
		buildAndLog(pb);
	}

	/*
	 * assign constant #12.34 to double variable doubleTest
	 */
	@Test
	public void testCAssignDouble() {
		addDoubleConstant(12.34);
		addDoubleVariable("doubleTest");
		pb.assignDouble(new QuadrupleImpl(Operator.ASSIGN_DOUBLE, "#12.34",
				"!", "doubleTest"));
		buildAndLog(pb);
	}

	/*
	 * assign #12.34 to doubleTest2, then assign doubleTest2 to doubleTest1
	 */
	@Test
	public void testVAssignDouble() {
		addDoubleConstant(12.34);
		addDoubleVariable("doubleTest1");
		addDoubleVariable("doubleTest2");
		pb.assignDouble(new QuadrupleImpl(Operator.ASSIGN_DOUBLE, "#12.34",
				"!", "doubleTest2"));
		pb.assignDouble(new QuadrupleImpl(Operator.ASSIGN_DOUBLE,
				"doubleTest2", "!", "doubleTest1"));
		buildAndLog(pb);
	}

	@Test
	public void testCAssignString() {
		addStringConstant("\"test\"");
		addStringVariable("stringTest");
		pb.assignString(new QuadrupleImpl(Operator.ASSIGN_STRING, "#\"test\"",
				"!", "stringTest"));
		buildAndLog(pb);
	}

	// testCAssignString
	// testVAssignString

	// testCAssignBoolean
	// testVAssignBoolean

	/*
	 * add long constant #1000 and long constant #234 then assign to long
	 * variable longTest
	 */
	@Test
	public void testCCAddLong() {
		addLongConstant(1000);
		addLongConstant(234);
		addLongVariable("longTest");
		pb.addLong(new QuadrupleImpl(Operator.ADD_LONG, "#1000", "#234",
				"longTest"));
		buildAndLog(pb);
	}

	@Test
	public void testVVAddLong() {
		addLongVariable("longTest1");
		addLongVariable("longTest2");
		pb.addLong(new QuadrupleImpl(Operator.ADD_LONG, "longTest1",
				"longTest2", "longTest1"));
		buildAndLog(pb);
	}

	/*
	 * add double constant #1000 and double constant #234 then assign to double
	 * variable doubleTest
	 */
	@Test
	public void testCCAddDouble() {
		addDoubleConstant(12.34);
		addDoubleConstant(43.21);
		addDoubleVariable("doubleTest");
		pb.addDouble(new QuadrupleImpl(Operator.ADD_DOUBLE, "#12.34", "#43.21",
				"doubleTest"));
		buildAndLog(pb);
	}

	@Test
	public void testVVAddDouble() {
		addDoubleVariable("doubleTest1");
		addDoubleVariable("doubleTest2");
		pb.addDouble(new QuadrupleImpl(Operator.ADD_DOUBLE, "doubleTest1",
				"doubleTest2", "doubleTest1"));
		buildAndLog(pb);
	}

	/*
	 * subtract long constant #1000 and long constant #234 then assign to long
	 * variable longTest
	 */
	@Test
	public void testCCSubLong() {
		addLongConstant(1000);
		addLongConstant(234);
		addLongVariable("longTest");
		pb.addLong(new QuadrupleImpl(Operator.SUB_LONG, "#1000", "#234",
				"longTest"));
		buildAndLog(pb);
	}

	@Test
	public void testVVSubLong() {
		addLongVariable("longTest1");
		addLongVariable("longTest2");
		pb.addLong(new QuadrupleImpl(Operator.SUB_LONG, "longTest1",
				"longTest2", "longTest1"));
		buildAndLog(pb);
	}

	/*
	 * subtract double constant #1000 and double constant #234 then assign to
	 * double variable doubleTest
	 */
	@Test
	public void testCCSubDouble() {
		addDoubleConstant(12.34);
		addDoubleConstant(43.21);
		addDoubleVariable("doubleTest");
		pb.subDouble(new QuadrupleImpl(Operator.SUB_DOUBLE, "#12.34", "#43.21",
				"doubleTest"));
		buildAndLog(pb);
	}

	@Test
	public void testVVSubDouble() {
		addDoubleVariable("doubleTest1");
		addDoubleVariable("doubleTest2");
		pb.subDouble(new QuadrupleImpl(Operator.SUB_DOUBLE, "doubleTest1",
				"doubleTest2", "doubleTest1"));
		buildAndLog(pb);
	}

	/*
	 * multiply long constant #1000 and long constant #234 then assign to long
	 * variable longTest
	 */
	@Test
	public void testCCMulLong() {
		addLongConstant(1000);
		addLongConstant(234);
		addLongVariable("longTest");
		pb.addLong(new QuadrupleImpl(Operator.MUL_LONG, "#1000", "#234",
				"longTest"));
		buildAndLog(pb);
	}

	@Test
	public void testVVMulLong() {
		addLongVariable("longTest1");
		addLongVariable("longTest2");
		pb.mulLong(new QuadrupleImpl(Operator.MUL_LONG, "longTest1",
				"longTest2", "longTest1"));
		buildAndLog(pb);
	}

	/*
	 * multiply double constant #1000 and double constant #234 then assign to
	 * double variable doubleTest
	 */
	@Test
	public void testCCMulDouble() {
		addDoubleConstant(12.34);
		addDoubleConstant(43.21);
		addDoubleVariable("doubleTest");
		pb.addDouble(new QuadrupleImpl(Operator.MUL_DOUBLE, "#12.34", "#43.21",
				"doubleTest"));
		buildAndLog(pb);
	}

	@Test
	public void testVVMulDouble() {
		addDoubleVariable("doubleTest1");
		addDoubleVariable("doubleTest2");
		pb.mulDouble(new QuadrupleImpl(Operator.MUL_DOUBLE, "doubleTest1",
				"doubleTest2", "doubleTest1"));
		buildAndLog(pb);
	}

	/*
	 * divide long constant #1000 and long constant #234 then assign to long
	 * variable longTest
	 */
	@Test
	public void testCCDivLong() {
		addLongConstant(1000);
		addLongConstant(234);
		addLongVariable("longTest");
		pb.addLong(new QuadrupleImpl(Operator.DIV_LONG, "#1000", "#234",
				"longTest"));
		buildAndLog(pb);
	}

	@Test
	public void testVVDivLong() {
		addLongVariable("longTest1");
		addLongVariable("longTest2");
		pb.divLong(new QuadrupleImpl(Operator.DIV_LONG, "longTest1",
				"longTest2", "longTest1"));
		buildAndLog(pb);
	}

	/*
	 * divide double constant #1000 and double constant #234 then assign to
	 * double variable doubleTest
	 */
	@Test
	public void testCCDivDouble() {
		addDoubleConstant(12.34);
		addDoubleConstant(43.21);
		addDoubleVariable("doubleTest");
		pb.addDouble(new QuadrupleImpl(Operator.DIV_DOUBLE, "#12.34", "#43.21",
				"doubleTest"));
		buildAndLog(pb);
	}

	@Test
	public void testVVDivDouble() {
		addDoubleVariable("doubleTest1");
		addDoubleVariable("doubleTest2");
		pb.divDouble(new QuadrupleImpl(Operator.DIV_DOUBLE, "doubleTest1",
				"doubleTest2", "doubleTest1"));
		buildAndLog(pb);
	}

	@Test
	public void testCReturn() {
		addLongConstant(1000);
		pb.returnLong(new QuadrupleImpl(Operator.RETURN, "#1000", "!", "!"));
		buildAndLog(pb);
	}

}
