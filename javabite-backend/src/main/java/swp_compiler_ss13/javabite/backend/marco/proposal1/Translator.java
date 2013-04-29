package swp_compiler_ss13.javabite.backend.marco.proposal1;

import java.util.List;
import java.util.ArrayList;
import swp_compiler_ss13.javabite.backend.marco.proposal1.Quadruple.Operator;

import com.sun.org.apache.bcel.internal.util.ClassPath.ClassFile;

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
	
	/**
	 * 
	 * @param classFile
	 * @param tac
	 */
	public void extractCPContent(Classfile classFile, List<Quadruple> tac) {
		
		for(Quadruple quad : tac) {
			
			String arg1 = quad.getArgument1();
			
			switch (quad.getOperator()) {
			case DECLARE_LONG:
				if(arg1.equals("!") || arg1.length() <=2) {
					// long arg's smaller than 9 will be pushed on stack
				} else {
					classFile.addConstantToConstantPool("LONG", arg1.substring(1));
				}
				break;
			case DECLARE_DOUBLE:
				if(arg1.equals("!")) {
					classFile.addConstantToConstantPool("DOUBLE", "0.0");
				} else {
					classFile.addConstantToConstantPool("DOUBLE", arg1.substring(1));
				}
				break;
			case DECLARE_BOOL:
				if(arg1.equals("!")) {
					classFile.addConstantToConstantPool("BOOL", "false");
				}
				break;
			case DECLARE_STRING:
				if(arg1.equals("!")) {
					classFile.addConstantToConstantPool("STRING", "\"\"");
				} else {
					classFile.addConstantToConstantPool("STRING", arg1.substring(1));
				}
				break;
			case ASSIGN_LONG:
				if(!arg1.equals("!")) {
					classFile.addConstantToConstantPool("LONG", arg1.substring(1));
				}
				break;
			case ASSIGN_DOUBLE:
				if(!arg1.equals("!")) {
					classFile.addConstantToConstantPool("DOUBLE", arg1.substring(1));
				}
				break;
			case ASSIGN_BOOL:
				if(!arg1.equals("!")) {
					classFile.addConstantToConstantPool("BOOL", arg1.substring(1));
				}
				break;
			case ASSIGN_STRING:
				if(!arg1.equals("!")) {
					classFile.addConstantToConstantPool("STRING", arg1.substring(1));
				}
				break;
			default:
				break;
			}
		}
	}
}
