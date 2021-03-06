package swp_compiler_ss13.javabite.backend;

import org.junit.Test;
import swp_compiler_ss13.javabite.backend.translation.Mnemonic;

import static org.junit.Assert.assertEquals;

public class MnemonicTests {

	static final Mnemonic NOP = Mnemonic.NOP;
	static final int NOP_CODE = 0x00;

	@Test
	public void testGetMnemonic() {
		assertEquals("NOP", NOP.getMnemonic());
	}

	@Test
	public void testGetBytecodeString() {
		assertEquals(Integer.toHexString(NOP_CODE), NOP.getBytecodeString());
	}

	@Test
	public void testGetBytecode() {
		assertEquals(NOP_CODE, NOP.getBytecode());
	}

}
