package swp_compiler_ss13.javabite.backend;

import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import swp_compiler_ss13.javabite.backend.utils.ByteUtils;

public class ByteUtilsTests {

	static final Logger log = LoggerFactory.getLogger(ByteUtilsTests.class);

	private static final Pattern HEX_BYTES = Pattern.compile("(.{2})");
	
	private static String splitHexBytes(final String hex) {
		return HEX_BYTES.matcher(hex).replaceAll("$1 ");
	}
	
	private static String toHexString(final int i) {
		return splitHexBytes(String.format("%08x", Integer.valueOf(i)));
	}
	
	private static String toHexString(final short i) {
		return splitHexBytes(String.format("%08x", Short.valueOf(i)));
	}
	
	private static String toHexString(final byte i) {
		return splitHexBytes(String.format("%08x", Byte.valueOf(i)));
	}
	
	@Rule
	public TestName name = new TestName();
	
	@Before
	public void setUp() {
		log.info(name.getMethodName());
	}
	
	@After
	public void tearDown() {
		log.info("===");
	}

	@Test
	public void testHexFromInt() {
		final int posInt = Integer.MAX_VALUE;
		final int negInt = Integer.MIN_VALUE;
		log.info("java pos {}", Integer.toHexString(posInt));
		log.info("mine pos {}", toHexString(posInt));
		log.info("ours pos {}", ByteUtils.hexFromInt(posInt));
		log.info("java neg {}", Integer.toHexString(negInt));
		log.info("mine neg {}", toHexString(negInt));
		log.info("ours neg {}", ByteUtils.hexFromInt(negInt));
	}
	
	@Test
	public void testHexFromShort() {
		final short posShort = Short.MAX_VALUE;
		final short negShort = Short.MIN_VALUE;
		log.info("java pos {}", Integer.toHexString(posShort));
		log.info("mine pos {}", toHexString(posShort));
		log.info("ours pos {}", ByteUtils.hexFromShort(posShort));
		log.info("java neg {}", Integer.toHexString(negShort));
		log.info("mine neg {}", toHexString(negShort));
		log.info("ours neg {}", ByteUtils.hexFromShort(negShort));
	}
	
	@Test
	public void testHexFromByte() {
		final byte posByte = Byte.MAX_VALUE;
		final byte negByte = Byte.MIN_VALUE;
		log.info("java pos {}", Integer.toHexString(posByte));
		log.info("mine pos {}", toHexString(posByte));
		log.info("ours pos {}", ByteUtils.hexFromByte(posByte));
		log.info("java neg {}", Integer.toHexString(negByte));
		log.info("mine neg {}", toHexString(negByte));
		log.info("ours neg {}", ByteUtils.hexFromByte(negByte));
	}

}
