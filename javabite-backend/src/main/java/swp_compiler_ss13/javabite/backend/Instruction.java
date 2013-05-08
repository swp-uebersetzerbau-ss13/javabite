package swp_compiler_ss13.javabite.backend;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static swp_compiler_ss13.javabite.backend.utils.ByteUtils.*;

public class Instruction
{
	
	Logger logger=LoggerFactory.getLogger(this.getClass());

	// offset to predecessor
	// private int offset;
	private Mnemonic mnemonic;
	private List<Byte> arguments;
	private final int size;

	public Instruction(final int size, final Mnemonic mnemonic,
			List<Byte> arguments) {
		this.mnemonic = mnemonic;
		this.arguments = arguments;
		this.size = size;
	}

	/**
	 * getBytes function. This function creates a Byte-List of all the
	 * information meeting the JVM-instruction standard.
	 * 
	 * @author Marco
	 * @since 03.05.2013
	 * 
	 */
	public ArrayList<Byte> getBytes() {
		ArrayList<Byte> instructionBytes = new ArrayList<Byte>();
		
		// get op byte
		instructionBytes.add(this.mnemonic.getBytecode());
		// get arguments' bytes
		if (this.getArguments() != null) {
			instructionBytes.addAll(this.getArguments());
		}
		
		return instructionBytes;
	}
	
	public void writeTo(DataOutputStream outputStream) {
		try {
			outputStream.writeByte(this.mnemonic.getBytecode());
			
			if(logger.isDebugEnabled()) {
				logger.info("mnemonic bcode");
				logger.info("{}", hexFromInt(this.mnemonic.getBytecode()));
			}
			
			if (this.getArguments() != null) {
				for(Byte b : this.getArguments()) {
					outputStream.writeByte(b);
				}
				
				if(logger.isDebugEnabled()) {
					logger.info("arguments");
					logger.info("{}", hexFromBytes(this.getArguments()));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
		
	/*
	 * TODO for MS2 public int getOffset() { return offset; }
	 * 
	 * public int setOffset(int offset) { this.offset = offset; return offset +
	 * size; }
	 */

	public Mnemonic getMnemonic() {
		return mnemonic;
	}

	public void setMnemonic(Mnemonic mnemonic) {
		this.mnemonic = mnemonic;
	}

	public List<Byte> getArguments() {
		return arguments;
	}

	public void setArguments(List<Byte> arguments) {
		this.arguments = arguments;
	}

	public int getSize() {
		return size;
	}



}
