package swp_compiler_ss13.javabite.backend;

/**
 * BytecodeAppender interface. The BytecodeAppender interface is the contract
 * between the Mnemonic enum and the CommandBuilder and is used to exchange
 * multiple bytecode assemblers for different tasks.
 * 
 * @author eike
 * @since 21.04.2013 01:03:05
 */
public interface BytecodeAppender {

	/**
	 * Append a mnemonic.
	 * 
	 * @param mnemonic
	 *          The mnemonic to append
	 * @return this BytecodeAppender instance
	 */
	BytecodeAppender append(Mnemonic mnemonic);

	/**
	 * Append a mnemonic.
	 * 
	 * @param mnemonic
	 *          The mnemonic to append
	 * @param argument1
	 *          The first argument
	 * @return this BytecodeAppender instance
	 */
	BytecodeAppender append(Mnemonic mnemonic, int argument1);

	/**
	 * Append a mnemonic.
	 * 
	 * @param mnemonic
	 *          The mnemonic to append
	 * @param argument1
	 *          The first argument
	 * @param argument2
	 *          The second argument
	 * @return this BytecodeAppender instance
	 */
	BytecodeAppender append(Mnemonic mnemonic, int argument1, int argument2);

}
