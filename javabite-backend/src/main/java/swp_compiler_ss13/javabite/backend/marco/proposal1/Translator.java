package swp_compiler_ss13.javabite.backend.marco.proposal1;

import java.util.List;
import java.util.ArrayList;

/**
 * Translator class.
 * 
 * @author Marco
 * @since 27.04.2013
 * 
 */
public class Translator {
	List<Classfile> classfileList;
	
	public Translator() {
		this.classfileList = new ArrayList<Classfile>();
	}
	
	/**
	 * generateNewFile function. This function generates a new Classfile
	 * object using its parameter name and appends it to the translator's
	 * classfile list.
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 */
	private Classfile generateNewFile(String name) {
		Classfile file = new Classfile(name);
		this.classfileList.add(file);
		return file;
	}
	
	public List<Classfile> translate(List<Quadruple> tac) {
		// always generate main.c file
		Classfile mainClassfile = this.generateNewFile("main.c");
		
		// TestTMP!!!!
		mainClassfile.addConstantToConstantPool("LONG", "100000");
		mainClassfile.addConstantToConstantPool("LONG", "100000");
		
		return this.classfileList;
	}
	
}
