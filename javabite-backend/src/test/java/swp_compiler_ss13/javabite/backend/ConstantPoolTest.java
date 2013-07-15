package swp_compiler_ss13.javabite.backend;

import junitx.util.PrivateAccessor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import swp_compiler_ss13.javabite.backend.classfile.CPInfo;
import swp_compiler_ss13.javabite.backend.classfile.ConstantPool;
import swp_compiler_ss13.javabite.backend.classfile.ConstantPoolFullException;
import swp_compiler_ss13.javabite.backend.utils.ClassfileUtils.ConstantPoolType;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.doubleToByteArray;
import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.longToByteArray;

/**
 * <h1>ConstantPoolTest</h1>
 * <p>
 * Main test-file for testing the ConstantPool functionality.
 * </p>
 * <br>
 * Notice: <br>
 * <p>
 * 1. All tests make use of the PrivateAccesssor. This is util is part of a
 * junit addon. We need this because of the package-private property of the
 * 'classfile package'. This addon encapsulates some tricky use of the Java
 * Reflection API.
 * </p>
 * <p>
 * 2. Many checks of the availability of 'item' in the constant pool HashMap
 * make use of the cpMapEntryExists Method of the ConstantPool class. This is
 * okay, because it only encapsulates the original HashMap containsKey Method.
 * </p>
 */
public class ConstantPoolTest {

	private ConstantPool cp;

	@Before
	public void setup() {
		cp = new ConstantPool();
	}

	@Test
	public void testThatInitWorks() {
		assertNotNull("The entryList of ConstantPool is null after init.",
				Whitebox.getInternalState(cp, "entryList"));
		assertNotNull("The cpEntryMap of ConstantPool is null after init.",
				Whitebox.getInternalState(cp, "cpEntryMap"));
	}

	/**
	 * <h1>testThatLongConstantAddedToCP</h1>
	 * <p>
	 * Needs two entries in the constant pool table of the classfile. If a
	 * CONSTANT_LONG info is the item in the constant pool table at index n, the
	 * next usable item in the pool is located at index n+2. The constant_pool
	 * index n+1 must be valid but is considered unusable.
	 * </p>
	 * <p>
	 * Valid index of a constant pool entry is >0 and <constant pool entries + 1
	 * </p>
	 * <p>
	 * Constant pool tagByte (CONSTANT_LONG): 5
	 * </p>
	 * 
	 * @throws Throwable
	 * 
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
	 *      #jvms-4.4.5>CONSTANT_LONG Specification</a>
	 * @see <a
	 *      href=http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
	 *      #jvms-4.4>Constant Pool Specification</a>
	 */
	@Test
	public void testThatLongConstantAddedToCP() throws Throwable {

		final short index1 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantLongInfo", new Class<?>[] { long.class },
				new Object[] { Long.MIN_VALUE });
		final short index2 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantLongInfo", new Class<?>[] { long.class },
				new Object[] { Long.MAX_VALUE });
		final short index3 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantLongInfo", new Class<?>[] { long.class },
				new Object[] { Long.MAX_VALUE });

		assertTrue("Invalid index", index1 == 1);
		assertTrue("Invalid index", index2 == 3);
		assertTrue("Invalid index", index3 == 3);

		assertTrue(
				"CONSTANT_LONG does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { String.valueOf(ConstantPoolType.LONG)
								+ Long.MIN_VALUE }));
		assertTrue(
				"CONSTANT_LONG does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { String.valueOf(ConstantPoolType.LONG)
								+ Long.MAX_VALUE }));

	}

	/**
	 * <h1>testThatConstantPoolMaxIsNotExceeded</h1>
	 * <p>
	 * The maximum of entries in the constant pool is 256 if every entry takes
	 * one slot If more slots taken by a Constant less entries are possible.
	 * </p>
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testThatConstantPoolMaxIsNotExceeded() throws Throwable {
		/**
		 * add 128 CONSTANT_LONG - maximum size (2 x 128 = 256 = 1 byte)
		 */
		for (long i = 1; i <= 128; i++) {

			final short index = (Short) PrivateAccessor.invoke(cp,
					"generateConstantLongInfo", new Class<?>[] { long.class },
					new Object[] { i });
			final short entry_amount = (short) ((ArrayList<?>) Whitebox
					.getInternalState(cp, "entryList")).size();

			assertTrue("The constant should stored normal at index " + index
					+ ". There is enough space in constant pool", index > 0
					&& index <= 255 && index < entry_amount + 1);
		}
	}

	@Test(expected = ConstantPoolFullException.class)
	public void testThatConstantPoolExceededExceptionIsThrown()
			throws Throwable {
		/**
		 * add 129 CONSTANT_LONG - 2 x 129 = 258 - more than 256 -> Exception
		 * Don't catch Exception of invoke here - it handles also
		 * ConstantPoolFullException !
		 */
		for (long l = 0; l <= 128; l++) {
			PrivateAccessor.invoke(cp, "generateConstantLongInfo",
					new Class<?>[] { long.class }, new Object[] { l });
		}
	}

	/**
	 * <h1>testThatAddingSameConstantsHaveSameIndex</h1>
	 * <p>
	 * Adding a Constant twice isn't possible. While doing it, you will get the
	 * index of the 'first' Constant.
	 * </p>
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testThatAddingSameConstantsHaveSameIndex() throws Throwable {

		assertTrue(
				"The CONSTANT_LONG is added more than once (a new index is returned).",
				((Short) PrivateAccessor.invoke(cp, "generateConstantLongInfo",
						new Class<?>[] { long.class }, new Object[] { 1000 }))
						.shortValue() == ((Short) PrivateAccessor.invoke(cp,
						"generateConstantLongInfo",
						new Class<?>[] { long.class }, new Object[] { 1000 }))
						.shortValue());
	}

	/**
	 * <h1>testThatCPLOngInfoHoldRightValues</h1>
	 * <p>
	 * Tests that the index given by the storing process holds the right value.
	 * </p>
	 */
	@Test
	public void testThatCPLOngInfoHoldRightValues() {

		final long value = 1000;

		try {
			final short index = (Short) PrivateAccessor.invoke(cp,
					"generateConstantLongInfo", new Class<?>[] { long.class },
					new Object[] { value });
			@SuppressWarnings("unchecked")
			final CPInfo constantPoolLongInfo = ((ArrayList<CPInfo>) Whitebox
					.getInternalState(cp, "entryList")).get(index - 1);

			final byte[] expected_info = longToByteArray(value);
			final byte[] stored_info = constantPoolLongInfo.getInfo();

			assertTrue("The CONSTANT_LONG info isn't stored right",
					Arrays.equals(expected_info, stored_info));

		} catch (final Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * <h1>testThatDoubleConstantAddedToCP</h1>
	 * <p>
	 * Same as store CONSTANT_LONG. CONSTANT_DOUBLE also needs two entries.
	 * </p>
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testThatDoubleConstantAddedToCP() throws Throwable {
		// 1.7976931348623157E308
		// final double d = Double.MAX_VALUE;
		// 4.9E-324
		// final double d2 = Double.MIN_VALUE;

		final short index1 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantDoubleInfo", new Class<?>[] { double.class,
						String.class }, new Object[] { Double.MIN_VALUE,
						"4.9E-324" });
		final short index2 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantDoubleInfo", new Class<?>[] { double.class,
						String.class }, new Object[] { Double.MAX_VALUE,
						"1.7976931348623157E308" });
		final short index3 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantDoubleInfo", new Class<?>[] { double.class,
						String.class }, new Object[] { Double.MAX_VALUE,
						"1.7976931348623157E308" });

		assertTrue("Invalid index", index1 == 1);
		assertTrue("Invalid index", index2 == 3);
		assertTrue("Invalid index", index3 == 3);

		assertTrue(
				"CONSTANT_DOUBLE does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { String.valueOf(ConstantPoolType.DOUBLE)
								+ Double.MIN_VALUE }));
		assertTrue(
				"CONSTANT_DOUBLE does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { String.valueOf(ConstantPoolType.DOUBLE)
								+ Double.MAX_VALUE }));
	}

	/**
	 * <h1>testThatCPDoubleInfoHoldRightValues</h1>
	 * <p>
	 * Tests that the index given by the storing process holds the right value.
	 * </p>
	 * 
	 * @throws Throwable
	 */
	// TODO: more concret tests
	@Test
	public void testThatCPDoubleInfoHoldRightValues() throws Throwable {

		final double value = 1000;

		final short index = (Short) PrivateAccessor.invoke(cp,
				"generateConstantDoubleInfo", new Class<?>[] { double.class,
						String.class }, new Object[] { value, "1000" });
		@SuppressWarnings("unchecked")
		final CPInfo constantPoolDoubleInfo = ((ArrayList<CPInfo>) Whitebox
				.getInternalState(cp, "entryList")).get(index - 1);

		final byte[] expected_info = doubleToByteArray(value);
		final byte[] stored_info = constantPoolDoubleInfo.getInfo();

		assertTrue("The CONSTANT_LONG info isn't stored right",
				Arrays.equals(expected_info, stored_info));
	}

	/**
	 * <h1>testThatUTF8ConstantAddedToCP</h1>
	 * <p>
	 * Adding a UTF8 CONSTANT needs one entry
	 * </p>
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testThatUTF8ConstantAddedToCP() throws Throwable {

		final short index1 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantUTF8Info", new Class<?>[] { String.class },
				new Object[] { "test1" });
		final short index2 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantUTF8Info", new Class<?>[] { String.class },
				new Object[] { "test2" });
		final short index3 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantUTF8Info", new Class<?>[] { String.class },
				new Object[] { "test2" });

		assertTrue("Invalid index", index1 == 1);
		assertTrue("Invalid index", index2 == 2);
		assertTrue("Invalid index", index3 == 2);

		assertTrue(
				"CONSTANT_UTF8 does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class }, new Object[] { ""
								+ "UTF8test1" }));
		assertTrue(
				"CONSTANT_UTF8 does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class }, new Object[] { ""
								+ "UTF8test2" }));
	}

	/**
	 * <h1>testThatCPUTF8InfoHoldRightValues</h1>
	 * <p>
	 * Tests that the index given by the storing process holds the right value.
	 * </p>
	 * 
	 * @throws Throwable
	 * 
	 * @see <a href=http://goo.gl/ZWi9V>CONSTANT_UTF8_INFO </a>
	 */
	@Test
	public void testThatCPUTF8InfoHoldRightValues() throws Throwable {

		final String value = "test";

		final short index = (Short) PrivateAccessor.invoke(cp,
				"generateConstantUTF8Info", new Class<?>[] { String.class },
				new Object[] { value });

		@SuppressWarnings("unchecked")
		final CPInfo constantPoolUTF8Info = ((ArrayList<CPInfo>) Whitebox
				.getInternalState(cp, "entryList")).get(index - 1);

		final byte[] expected_info = value.getBytes();
		/**
		 * be careful - the first to byte encode the length
		 */
		final byte[] stored_info = Arrays.copyOfRange(
				constantPoolUTF8Info.getInfo(), 2,
				constantPoolUTF8Info.getInfo().length);

		assertTrue("The CONSTANT_UTF8 info isn't stored right",
				Arrays.equals(expected_info, stored_info));
	}

	/**
	 * <h1>testThatNameAndTypeConstantsAddedToCP</h1>
	 * <p>
	 * Adding a NameAndType_CONSTANT needs one entry Adding a
	 * NameAndType_CONSTANT is done in three steps:
	 * </p>
	 * <p/>
	 * fst: adding an UTF8_CONSTANT for the name to constant pool
	 * </p>
	 * <p>
	 * scd: adding an UTF8_CONSTANT for the type to constant pool
	 * </p>
	 * <p>
	 * 3rd: adding an CONSTANT_NAMEANDTYPE_INFO (with indexes to name and type
	 * UTF8) to constant pool
	 * </p>
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testThatNameAndTypeConstantsAddedToCP() throws Throwable {

		final short long_index1 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantNameAndTypeInfo", new Class<?>[] {
						String.class, String.class }, new Object[] {
						"testLong1", "J" });
		final short long_index2 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantNameAndTypeInfo", new Class<?>[] {
						String.class, String.class }, new Object[] {
						"testLong2", "J" });

		final short double_index1 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantNameAndTypeInfo", new Class<?>[] {
						String.class, String.class }, new Object[] {
						"testDouble1", "D" });

		final short double_index2 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantNameAndTypeInfo", new Class<?>[] {
						String.class, String.class }, new Object[] {
						"testDouble2", "D" });

		final short object_index1 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantNameAndTypeInfo", new Class<?>[] {
						String.class, String.class }, new Object[] {
						"testObject1", "Ljava/lang/String" });

		final short object_index2 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantNameAndTypeInfo", new Class<?>[] {
						String.class, String.class }, new Object[] {
						"testObject2", "Ljava/lang/String" });

		final short object_index3 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantNameAndTypeInfo", new Class<?>[] {
						String.class, String.class }, new Object[] {
						"testObject2", "Ljava/lang/String" });

		/**
		 * Overview of constant pool entries
		 * 
		 * cp[1]: UTF8 - testLong1
		 * 
		 * cp[2]: UTF8 - J
		 * 
		 * cp[3]: NAMEANDTYPE(1,2)
		 * 
		 * cp[4]: UTF8 - testLong1
		 * 
		 * cp[5]: NAMEANDTYPE(4,2)
		 */
		assertTrue("Invalid index", long_index1 == 3);
		/**
		 * BE CAREFUL: Ijava/lang/Integer already exists in constant pool (reuse
		 * of index)
		 */
		assertTrue("Invalid index", long_index2 == 5);

		/**
		 * some index tests
		 */
		assertTrue("Invalid index", double_index1 == 8);
		assertTrue("Invalid index", double_index2 == 10);
		assertTrue("Invalid index", object_index1 == 13);
		assertTrue("Invalid index", object_index2 == 15);
		assertTrue("Invalid index", object_index3 == 15);

		/**
		 * check all CONSTANT_NAMEANDTYPE exist
		 */
		assertTrue(
				"CONSTANT_NAMEANDTYPE does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.NAMEANDTYPE
								+ "testLong1" + "J" }));

		assertTrue(
				"CONSTANT_NAMEANDTYPE does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.NAMEANDTYPE
								+ "testLong2" + "J" }));

		assertTrue(
				"CONSTANT_NAMEANDTYPE does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.NAMEANDTYPE
								+ "testDouble1" + "D" }));

		assertTrue(
				"CONSTANT_NAMEANDTYPE does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.NAMEANDTYPE
								+ "testDouble2" + "D" }));

		assertTrue(
				"CONSTANT_NAMEANDTYPE does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.NAMEANDTYPE
								+ "testObject1" + "Ljava/lang/String" }));

		assertTrue(
				"CONSTANT_NAMEANDTYPE does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.NAMEANDTYPE
								+ "testObject2" + "Ljava/lang/String" }));

		/**
		 * generateConstantStringInfo check that all CONSTANT_UTF8 exist
		 */
		assertTrue(
				"CONSTANT_NAMEANDTYPE does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.UTF8 + "testLong1" }));

		assertTrue(
				"CONSTANT_NAMEANDTYPE does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.UTF8 + "testLong2" }));

		assertTrue(
				"CONSTANT_NAMEANDTYPE does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.UTF8 + "testDouble1" }));

		assertTrue(
				"CONSTANT_NAMEANDTYPE does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.UTF8 + "testDouble2" }));

		assertTrue(
				"CONSTANT_NAMEANDTYPE does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.UTF8 + "testObject1" }));

		assertTrue(
				"CONSTANT_NAMEANDTYPE does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.UTF8 + "testObject2" }));
	}

	/**
	 * <h1>testThatStringConstantsAddedToCP</h1>
	 * <p>
	 * Adding CONSTANT_STRING_INFO to constant pool is performed in two steps.
	 * fst: add an UTF8 CONSTANT to constant pool scd: add a STRING CONSTANT
	 * (with index of UTF8) to constant pool
	 * </p>
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testThatStringConstantsAddedToCP() throws Throwable {

		final short string_index1 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantStringInfo", new Class<?>[] { String.class,
						boolean.class }, new Object[] { "test1111", false });
		final short string_index2 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantStringInfo", new Class<?>[] { String.class,
						boolean.class }, new Object[] { "test2222", false });
		final short string_index3 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantStringInfo", new Class<?>[] { String.class,
						boolean.class }, new Object[] { "test2222", false });

		/**
		 * cp[1]: UTF8
		 * 
		 * cp[2]: STRING
		 * 
		 * cp[3]: UTF8
		 * 
		 * cp[4]: STRING
		 */
		assertTrue("Invalid index", string_index1 == 2);
		assertTrue("Invalid index", string_index2 == 4);
		assertTrue("Invalid index", string_index3 == 4);

		assertTrue(
				"CONSTANT_UTF8 does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.STRING + "test1111" }));

		assertTrue(
				"CONSTANT_STRING does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.STRING + "test1111" }));

		assertTrue(
				"CONSTANT_UTF8 does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.STRING + "test2222" }));

		assertTrue(
				"CONSTANT_STRING does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.STRING + "test2222" }));
	}

	/**
	 * <h1>testThatClassConstantsAddedToCP</h1>
	 * <p>
	 * Adding CONSTANT_CLASS_INFO to constant pool is performed in two steps.
	 * fst: add an UTF8 CONSTANT to constant pool scd: add a CLASS CONSTANT
	 * (with index of UTF8) to constant pool
	 * </p>
	 * 
	 * @throws Throwable
	 */
	@Test
	public void testThatClassConstantsAddedToCP() throws Throwable {

		final short class_index1 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantClassInfo", new Class<?>[] { String.class },
				new Object[] { "java/lang/String" });
		final short class_index2 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantClassInfo", new Class<?>[] { String.class },
				new Object[] { "java/io/PrintStream" });
		final short class_index3 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantClassInfo", new Class<?>[] { String.class },
				new Object[] { "java/io/PrintStream" });

		/**
		 * cp[1]: UTF8 - java/lang/String
		 * 
		 * cp[2]: CLASS CONSTANT
		 * 
		 * cp[3]: UTF8 - java/io/PrintStream
		 * 
		 * cp[4]: CLASS
		 */
		assertTrue("Invalid index", class_index1 == 2);
		assertTrue("Invalid index", class_index2 == 4);
		assertTrue("Invalid index", class_index3 == 4);

		assertTrue(
				"CONSTANT_UTF8 does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.UTF8
								+ "java/lang/String" }));

		assertTrue(
				"CONSTANT_CLASS does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.CLASS
								+ "java/lang/String" }));

		assertTrue(
				"CONSTANT_UTF8 does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.UTF8
								+ "java/io/PrintStream" }));

		assertTrue(
				"CONSTANT_CLASS does not exists in constant pool map after adding",
				(boolean) PrivateAccessor.invoke(cp, "cpMapEntryExists",
						new Class<?>[] { String.class },
						new Object[] { ConstantPoolType.CLASS
								+ "java/io/PrintStream" }));
	}

	@Test
	public void testThatFieldrefConstantsAddedToCP() throws Throwable {

		// "out", "Ljava/io/PrintStream;", "java/lang/System");
		// "in" "Ljava/io/InputStream" "java/lang/System"
		final short class_index1 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantClassInfo", new Class<?>[] { String.class },
				new Object[] { "java/lang/String" });
		final short class_index2 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantClassInfo", new Class<?>[] { String.class },
				new Object[] { "java/lang/String" });

		final String fieldName1 = "out";
		final String eifName1 = "PrintStream";
		final short nat_index1 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantNameAndTypeInfo", new Class<?>[] {
						String.class, String.class }, new Object[] {
						fieldName1, "Ljava/io/" + eifName1 });

		final String fieldName2 = "in";
		final String eifName2 = "InputStream";
		final short nat_index2 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantNameAndTypeInfo", new Class<?>[] {
						String.class, String.class }, new Object[] {
						fieldName2, "Ljava/io/" + eifName2 });

		final short fieldref_index1 = (Short) PrivateAccessor
				.invoke(cp, "generateConstantFieldrefInfo", new Class<?>[] {
						short.class, short.class, String.class, String.class },
						new Object[] { class_index1, nat_index1, fieldName1,
								eifName1 });

		final short fieldref_index2 = (Short) PrivateAccessor
				.invoke(cp, "generateConstantFieldrefInfo", new Class<?>[] {
						short.class, short.class, String.class, String.class },
						new Object[] { class_index2, nat_index2, fieldName2,
								eifName2 });

		final short fieldref_index3 = (Short) PrivateAccessor
				.invoke(cp, "generateConstantFieldrefInfo", new Class<?>[] {
						short.class, short.class, String.class, String.class },
						new Object[] { class_index2, nat_index2, fieldName2,
								eifName2 });

		/**
		 * Overview of constant pool entries
		 * 
		 * cp[1]: UTF8 java/lang/System
		 * 
		 * cp[2]: CLASS (1)
		 * 
		 * cp[3]: UTF8 out
		 * 
		 * cp[4]: UTF8 Ljava/io/InputStream
		 * 
		 * cp[5]: NAMEANDTYPE (4,5)
		 * 
		 * cp[6]: UTF8 in
		 * 
		 * cp[7]: UTF8 Ljava/io/PrintStream
		 * 
		 * cp[8]: NAMEANDTYPE (8,9)
		 * 
		 * cp[9]: FIELDREF (2,5)
		 * 
		 * cp[10]: FIELDREF (2,10)
		 */
		assertTrue("Invalid index", fieldref_index1 == 9);
		assertTrue("Invalid index", fieldref_index2 == 10);
		assertTrue("Invalid index", fieldref_index3 == 10);
	}

	@Test
	public void testThatMethodrefConstantsAddedToCP() throws Throwable {


		final String fieldName1 = "out";
		final String eifName1 = "PrintStream";
		
		final String fieldName2 = "in";
		final String eifName2 = "InputStream";
		
		// "out", "Ljava/io/PrintStream;", "java/lang/System");
		// "in" "Ljava/io/InputStream" "java/lang/System"
		final short class_index1 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantClassInfo", new Class<?>[] { String.class },
				new Object[] { "java/io/PrintStream" });
		final short class_index2 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantClassInfo", new Class<?>[] { String.class },
				new Object[] { "java/lang/System" });

		final short nat_index1 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantNameAndTypeInfo", new Class<?>[] {
						String.class, String.class }, new Object[] { "print",
						"(Ljava/lang/String;)V" });

		final short nat_index2 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantNameAndTypeInfo", new Class<?>[] {
						String.class, String.class }, new Object[] { "exit",
						"(I)V" });

		final short methodref_index1 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantFieldrefInfo", new Class<?>[] { short.class,
						short.class, String.class, String.class },
				new Object[] { class_index1, nat_index1, fieldName1, eifName1 });

		final short methodref_index2 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantFieldrefInfo", new Class<?>[] { short.class,
						short.class, String.class, String.class},
				new Object[] { class_index2, nat_index2, fieldName2, eifName2 });

		final short methodref_index3 = (Short) PrivateAccessor.invoke(cp,
				"generateConstantFieldrefInfo", new Class<?>[] { short.class,
						short.class, String.class, String.class },
				new Object[] { class_index2, nat_index2, fieldName2, fieldName2 });

		/**
		 * Overview of constant pool entries
		 * 
		 * cp[1]: UTF8 java/io/PrintStream
		 * 
		 * cp[2]: CLASS (1)
		 * 
		 * cp[3]: UTF8 java/lang/System
		 * 
		 * cp[4]: CLASS (3)
		 * 
		 * cp[5]: UTF8 print
		 * 
		 * cp[6]: UTF8 (Ljava/lang/String;)V
		 * 
		 * cp[7]: NAMEANDTYPE (5,6)
		 * 
		 * cp[8]: UTF8 exit
		 * 
		 * cp[9]: UTF8 (I)V
		 * 
		 * cp[10]: NAMEANDTYPE (8,9)
		 * 
		 * cp[11]: METHODREF (2,7)
		 * 
		 * cp[12]: METHODREF (4,10)
		 */
		assertTrue("Invalid index", methodref_index1 == 11);
		assertTrue("Invalid index", methodref_index2 == 12);
		assertTrue("Invalid index", methodref_index3 == 13);
	}
}
