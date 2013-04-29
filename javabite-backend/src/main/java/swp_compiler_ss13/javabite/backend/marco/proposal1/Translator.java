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
	 * @param name string describing the classfile's name
	 * @param thisClassNameEIF string describing this classname of the class 
	 * described in this classfile encoded in internal form  
	 * @param superClassNameEIF string describing the superclass' classname 
	 * of the class described in this classfile encoded in internal form
	 * 
	 * @return instance of Classfile
	 */
	private Classfile generateNewFile(String name, String thisClassNameEIF, 
			String superClassNameEIF) {
		Classfile file = new Classfile(name, thisClassNameEIF, superClassNameEIF);
		this.classfileList.add(file);
		return file;
	}
	
	/**
	 * translate function. This function translates the tac into classfiles.
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 * @param tac List of quadruple objects
	 * 
	 * @return List<Classfile>
	 */
	public List<Classfile> translate(List<Quadruple> tac) {
		// always generate main.c file
		Classfile mainClassfile = this.generateNewFile
				("main.class", "Tests/Example", "java/lang/Object");
		
		// TestTMP!!!!
		mainClassfile.addConstantToConstantPool("LONG", "100000");
		mainClassfile.addConstantToConstantPool("LONG", "100000");
		
		return this.classfileList;
	}
}
