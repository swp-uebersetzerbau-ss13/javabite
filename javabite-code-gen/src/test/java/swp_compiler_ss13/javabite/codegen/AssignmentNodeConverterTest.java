package swp_compiler_ss13.javabite.codegen;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import swp_compiler_ss13.common.ast.nodes.binary.AssignmentNode;
import swp_compiler_ss13.common.ast.nodes.leaf.BasicIdentifierNode;
import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.common.ir.IntermediateCodeGeneratorException;
import swp_compiler_ss13.common.types.derived.ArrayType;
import swp_compiler_ss13.common.types.derived.Member;
import swp_compiler_ss13.common.types.derived.StructType;
import swp_compiler_ss13.common.types.primitive.BooleanType;
import swp_compiler_ss13.common.types.primitive.DoubleType;
import swp_compiler_ss13.common.types.primitive.LongType;
import swp_compiler_ss13.common.types.primitive.StringType;
import swp_compiler_ss13.javabite.ast.nodes.leaf.BasicIdentifierNodeJb;
import swp_compiler_ss13.javabite.codegen.converters.AssignmentNodeConverter;
import swp_compiler_ss13.javabite.quadtruple.QuadrupleJb;

public class AssignmentNodeConverterTest {
	AssignmentNodeConverter converter;

	@Before
	public void setUp() throws Exception {
		converter = new AssignmentNodeConverter();

		converter.icg = Mockito.mock(IntermediateCodeGeneratorJb.class);
	}

	// ////////////////LONG/////////////////////////////

	/**
	 * Test for an AssignmentNode, which has the type Long.
	 */
	@Test
	public void testAssignmentNodeConverter_LongLong() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new LongType());
			IdentifierData rightData = new IdentifierData("testRight",
					new LongType());
			when(converter.icg.popIdentifierData()).thenReturn(rightData,
					leftData);
			when(node.getLeftValue()).thenReturn(new BasicIdentifierNodeJb());
			converter.convert(node);
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.ASSIGN_LONG, "testRight",
							Quadruple.EmptyArgument, "testLeft"));

			verify(converter.icg).pushIdentifierData(rightData);
		} catch (IntermediateCodeGeneratorException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test for an AssignmentNode, which has the left type Long and right type
	 * Double.
	 */
	@Test
	public void testAssignmentNodeConverter_LongDouble() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);
			when(node.getLeftValue()).thenReturn(new BasicIdentifierNodeJb());
			IdentifierData leftData = new IdentifierData("testLeft",
					new LongType());
			when(converter.icg.popIdentifierData())
					.thenReturn(
							new IdentifierData("testRight", new DoubleType()),
							leftData);
			IdentifierData tmp = new IdentifierData("tmp", new LongType());
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.ASSIGN_LONG, "tmp",
							Quadruple.EmptyArgument, "testLeft"));

			verify(converter.icg).pushIdentifierData(tmp);
		} catch (IntermediateCodeGeneratorException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test for an AssignmentNode, which has the left type Long and right type
	 * Boolean.
	 */
	@Test
	public void testAssignmentNodeConverter_LongBoolean() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new LongType());
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new BooleanType()),
					leftData);
			IdentifierData tmp = new IdentifierData("tmp", new LongType());
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which has the left type Long and right type
	 * String.
	 */
	@Test
	public void testAssignmentNodeConverter_LongString() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new LongType());
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new StringType(0l)),
					leftData);
			IdentifierData tmp = new IdentifierData("tmp", new LongType());
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which has the left type Long and right type
	 * Array.
	 */
	@Test
	public void testAssignmentNodeConverter_LongArray() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new LongType());
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new ArrayType(
							new LongType(), 0)), leftData);
			IdentifierData tmp = new IdentifierData("tmp", new LongType());
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which has the left type Long and right type
	 * Struct.
	 */
	@Test
	public void testAssignmentNodeConverter_LongStruct() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new LongType());
			when(converter.icg.popIdentifierData())
					.thenReturn(
							new IdentifierData("testRight", new StructType(
									new Member[] { new Member("",
											new DoubleType()) })), leftData);
			IdentifierData tmp = new IdentifierData("tmp", new LongType());
			when(converter.icg.generateTempIdentifier(any(LongType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	// ////////////////////DOUBLE/////////////////////////

	/**
	 * Test for an AssignmentNode, which has the type Double.
	 */
	@Test
	public void testAssignmentNodeConverter_DoubleDouble() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new DoubleType());
			IdentifierData rightData = new IdentifierData("testRight",
					new DoubleType());
			when(node.getLeftValue()).thenReturn(new BasicIdentifierNodeJb());
			when(converter.icg.popIdentifierData()).thenReturn(rightData,
					leftData);
			converter.convert(node);
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.ASSIGN_DOUBLE, "testRight",
							Quadruple.EmptyArgument, "testLeft"));

			verify(converter.icg).pushIdentifierData(rightData);
		} catch (IntermediateCodeGeneratorException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test for an AssignmentNode, which has the left type Double and right type
	 * Long.
	 */
	@Test
	public void testAssignmentNodeConverter_DoubleLong() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new DoubleType());
			IdentifierData rightData = new IdentifierData("testRight",
					new DoubleType());
			when(converter.icg.popIdentifierData()).thenReturn(rightData,
					leftData);
			when(node.getLeftValue()).thenReturn(new BasicIdentifierNodeJb());
			IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.ASSIGN_DOUBLE, "testRight",
							Quadruple.EmptyArgument, "testLeft"));

			verify(converter.icg).pushIdentifierData(rightData);
		} catch (IntermediateCodeGeneratorException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test for an AssignmentNode, which has the left type Double and right type
	 * Boolean.
	 */
	@Test
	public void testAssignmentNodeConverter_DoubleBoolean() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new DoubleType());
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new BooleanType()),
					leftData);
			IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which has the left type Double and right type
	 * String.
	 */
	@Test
	public void testAssignmentNodeConverter_DoubleString() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new DoubleType());
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new StringType(0l)),
					leftData);
			IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which has the left type Double and right type
	 * Array.
	 */
	@Test
	public void testAssignmentNodeConverter_DoubleArray() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new DoubleType());
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new ArrayType(
							new LongType(), 0)), leftData);
			IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which has the left type Double and right type
	 * Struct.
	 */
	@Test
	public void testAssignmentNodeConverter_DoubleStruct() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new DoubleType());
			when(converter.icg.popIdentifierData())
					.thenReturn(
							new IdentifierData("testRight", new StructType(
									new Member[] { new Member("",
											new DoubleType()) })), leftData);
			IdentifierData tmp = new IdentifierData("tmp", new DoubleType());
			when(converter.icg.generateTempIdentifier(any(DoubleType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	// /////////////////BOOLEAN//////////////////////////

	/**
	 * Test for an AssignmentNode, which has the type Boolean.
	 */
	@Test
	public void testAssignmentNodeConverter_BooleanBoolean() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new BooleanType());
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new BooleanType()),
					leftData);
			converter.convert(node);
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.ASSIGN_BOOLEAN, "testRight",
							Quadruple.EmptyArgument, "testLeft"));

			verify(converter.icg).pushIdentifierData(leftData);
		} catch (IntermediateCodeGeneratorException e) {
		}
	}

	/**
	 * Test for an AssignmentNode, which left type Boolean and right type Long.
	 */
	@Test
	public void testAssignmentNodeConverter_BooleanLong() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new BooleanType());
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new LongType()), leftData);
			IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type Boolean and right type
	 * Double.
	 */
	@Test
	public void testAssignmentNodeConverter_BooleanDouble() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new BooleanType());
			when(converter.icg.popIdentifierData())
					.thenReturn(
							new IdentifierData("testRight", new DoubleType()),
							leftData);
			IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type Boolean and right type
	 * String.
	 */
	@Test
	public void testAssignmentNodeConverter_BooleanString() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new BooleanType());
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new StringType(0l)),
					leftData);
			IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type Boolean and right type Array.
	 */
	@Test
	public void testAssignmentNodeConverter_BooleanArray() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new BooleanType());
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new ArrayType(
							new LongType(), 0)), leftData);
			IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type Boolean and right type
	 * Struct.
	 */
	@Test
	public void testAssignmentNodeConverter_BooleanStruct() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new BooleanType());
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new ArrayType(
							new StructType(new Member[] { new Member("",
									new DoubleType()) }), 0)), leftData);
			IdentifierData tmp = new IdentifierData("tmp", new BooleanType());
			when(converter.icg.generateTempIdentifier(any(BooleanType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	// ///////STRING////////////////

	/**
	 * Test for an AssignmentNode, which has the type String.
	 */
	@Test
	public void testAssignmentNodeConverter_StringString() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new StringType(0l));
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new StringType(0l)),
					leftData);
			converter.convert(node);
			verify(converter.icg).addQuadruple(
					new QuadrupleJb(Operator.ASSIGN_STRING, "testRight",
							Quadruple.EmptyArgument, "testLeft"));

			verify(converter.icg).pushIdentifierData(leftData);
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type String and right type Long.
	 */
	@Test
	public void testAssignmentNodeConverter_StringLong() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new StringType(0l));
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new LongType()), leftData);
			IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
			when(converter.icg.generateTempIdentifier(any(StringType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type String and right type Double.
	 */
	@Test
	public void testAssignmentNodeConverter_StringDouble() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new StringType(0l));
			when(converter.icg.popIdentifierData())
					.thenReturn(
							new IdentifierData("testRight", new DoubleType()),
							leftData);
			IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
			when(converter.icg.generateTempIdentifier(any(StringType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type String and right type
	 * Boolean.
	 */
	@Test
	public void testAssignmentNodeConverter_StringBoolean() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new StringType(0l));
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new BooleanType()),
					leftData);
			IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
			when(converter.icg.generateTempIdentifier(any(StringType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type String and right type Array.
	 */
	@Test
	public void testAssignmentNodeConverter_StringArray() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new StringType(0l));
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new ArrayType(
							new LongType(), 0)), leftData);
			IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
			when(converter.icg.generateTempIdentifier(any(StringType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type String and right type Struct.
	 */
	@Test
	public void testAssignmentNodeConverter_StringStruct() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new StringType(0l));
			when(converter.icg.popIdentifierData())
					.thenReturn(
							new IdentifierData("testRight", new StructType(
									new Member[] { new Member("",
											new DoubleType()) })), leftData);
			IdentifierData tmp = new IdentifierData("tmp", new StringType(0l));
			when(converter.icg.generateTempIdentifier(any(StringType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	// ///////////////ARRAY/////////////////////

	/**
	 * Test for an AssignmentNode, which has the type Array.
	 */
	@Test
	public void testAssignmentNodeConverter_ArrayArray() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new ArrayType(new LongType(), 0));
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new ArrayType(
							new LongType(), 0)), leftData);
			converter.convert(node);
			fail();

		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type Array and right type Long.
	 */
	@Test
	public void testAssignmentNodeConverter_ArrayLong() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new ArrayType(new LongType(), 0));
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new LongType()), leftData);
			IdentifierData tmp = new IdentifierData("tmp", new ArrayType(
					new LongType(), 0));
			when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type Array and right type Double.
	 */
	@Test
	public void testAssignmentNodeConverter_ArrayDouble() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new ArrayType(new LongType(), 0));
			when(converter.icg.popIdentifierData())
					.thenReturn(
							new IdentifierData("testRight", new DoubleType()),
							leftData);
			IdentifierData tmp = new IdentifierData("tmp", new ArrayType(
					new LongType(), 0));
			when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type Array and right type Boolean.
	 */
	@Test
	public void testAssignmentNodeConverter_ArrayBoolean() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new ArrayType(new LongType(), 0));
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new BooleanType()),
					leftData);
			IdentifierData tmp = new IdentifierData("tmp", new ArrayType(
					new LongType(), 0));
			when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type Array and right type Struct.
	 */
	@Test
	public void testAssignmentNodeConverter_ArrayStruct() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new ArrayType(new LongType(), 0));
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new ArrayType(
							new StructType(new Member[] { new Member("",
									new DoubleType()) }), 0)));
			IdentifierData tmp = new IdentifierData("tmp", new ArrayType(
					new LongType(), 0));
			when(converter.icg.generateTempIdentifier(any(ArrayType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	// //////////////STRUCT///////////////////////////

	/**
	 * Test for an AssignmentNode, which has the type Struct.
	 */
	@Test
	public void testAssignmentNodeConverter_StructStruct() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new StructType(new Member[] { new Member("",
							new DoubleType()) }));
			when(converter.icg.popIdentifierData())
					.thenReturn(
							new IdentifierData("testRight", new StructType(
									new Member[] { new Member("",
											new DoubleType()) })), leftData);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type Struct and right type Long.
	 */
	@Test
	public void testAssignmentNodeConverter_StructLong() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new StructType(new Member[] { new Member("",
							new DoubleType()) }));
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new LongType()), leftData);
			IdentifierData tmp = new IdentifierData("tmp", new StructType(
					new Member[] { new Member("", new DoubleType()) }));
			when(converter.icg.generateTempIdentifier(any(StructType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type Struct and right type Double.
	 */
	@Test
	public void testAssignmentNodeConverter_StructDouble() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new StructType(new Member[] { new Member("",
							new DoubleType()) }));
			when(converter.icg.popIdentifierData())
					.thenReturn(
							new IdentifierData("testRight", new DoubleType()),
							leftData);
			IdentifierData tmp = new IdentifierData("tmp", new StructType(
					new Member[] { new Member("", new DoubleType()) }));
			when(converter.icg.generateTempIdentifier(any(StructType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type Struct and right type
	 * Boolean.
	 */
	@Test
	public void testAssignmentNodeConverter_StructBoolean() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new StructType(new Member[] { new Member("",
							new DoubleType()) }));
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new BooleanType()),
					leftData);
			IdentifierData tmp = new IdentifierData("tmp", new StructType(
					new Member[] { new Member("", new DoubleType()) }));
			when(converter.icg.generateTempIdentifier(any(StructType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type Struct and right type String.
	 */
	@Test
	public void testAssignmentNodeConverter_StructString() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new StructType(new Member[] { new Member("",
							new DoubleType()) }));
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new StringType(0l)),
					leftData);
			IdentifierData tmp = new IdentifierData("tmp", new StructType(
					new Member[] { new Member("", new DoubleType()) }));
			when(converter.icg.generateTempIdentifier(any(StructType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

	/**
	 * Test for an AssignmentNode, which left type Struct and right type Array.
	 */
	@Test
	public void testAssignmentNodeConverter_StructArray() {
		try {
			AssignmentNode node = Mockito.mock(AssignmentNode.class);

			IdentifierData leftData = new IdentifierData("testLeft",
					new StructType(new Member[] { new Member("",
							new DoubleType()) }));
			when(converter.icg.popIdentifierData()).thenReturn(
					new IdentifierData("testRight", new ArrayType(
							new LongType(), 0)), leftData);
			IdentifierData tmp = new IdentifierData("tmp", new StructType(
					new Member[] { new Member("", new DoubleType()) }));
			when(converter.icg.generateTempIdentifier(any(StructType.class)))
					.thenReturn(tmp);
			converter.convert(node);
			fail();
		} catch (IntermediateCodeGeneratorException e) {

		}
	}

}
