package swp_compiler_ss13.javabite.backend.temp1;

import swp_compiler_ss13.javabite.backend.Mnemonic;

/**
 * BytecodeBuilder class. This class is used to assemble bytecode sequences. The
 * mnemonics provided by the enum Mnemonic can be used together with 0-2
 * arguments to form a bytecode instruction, which is then appended to the
 * existing instructions.
 * 
 * @author eike
 * @since 21.04.2013 00:57:13
 */
public class BytecodeBuilder implements BytecodeAppender {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fub.ss13com.whilst.backend.BytecodeAppender#append(de.fub.ss13com.whilst
	 * .backend.Mnemonic)
	 */
	@Override
	public BytecodeAppender append(Mnemonic mnemonic) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fub.ss13com.whilst.backend.BytecodeAppender#append(de.fub.ss13com.whilst
	 * .backend.Mnemonic, int)
	 */
	@Override
	public BytecodeAppender append(Mnemonic mnemonic, int argument1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.fub.ss13com.whilst.backend.BytecodeAppender#append(de.fub.ss13com.whilst
	 * .backend.Mnemonic, int, int)
	 */
	@Override
	public BytecodeAppender append(Mnemonic mnemonic, int argument1, int argument2) {
		// TODO Auto-generated method stub
		return null;
	}

}
