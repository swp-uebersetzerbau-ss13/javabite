package swp_compiler_ss13.javabite.backend.marco.proposal1;

import java.util.List;
import java.util.ArrayList;

import swp_compiler_ss13.javabite.backend.marco.proposal1.Quadruple.Operator;

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
		// always generate main.class file
		Classfile mainClassfile = this.generateNewFile
				("main.class", "Tests/Example", "java/lang/Object");
		// always generate mainFunction in main.cass file
		mainClassfile.addMethodToMethodArea("main", "([Ljava/lang/String;])V");
		
		// MS 1 translate everything into main.class file
		tac = this.addVariablesToLocalVariableSpace(mainClassfile, "main", tac);
		
		
		// TestTMP!!!!
		mainClassfile.addConstantToConstantPool("LONG", "100000");
		mainClassfile.addConstantToConstantPool("LONG", "100000");
		
		return this.classfileList;
	}
	
	/**
	 * addVariablesToLocalVariableSpace function. This function allocates space for all variable
	 * declarations and will convert them into assignments, if they have an inital value;
	 * 
	 * @author Marco
	 * @since 29.04.2013
	 * 
	 */
	public List<Quadruple> addVariablesToLocalVariableSpace(Classfile file, String methodName, List<Quadruple> tac) {
		List<Quadruple> newTac = new ArrayList<Quadruple>();
		
		for (Quadruple tacElement : tac) {
			switch(tacElement.getOperator()) {
			case DECLARE_STRING:
				file.addVariableToMethodsCode(methodName, tacElement.getResult(), "STRING");
				if (!tacElement.getArgument1().equals("!")) {
					newTac.add(new QuadrupleImpl(Operator.ASSIGN_STRING, tacElement.getArgument1(), "!", tacElement.getResult()));
				} else {
					newTac.add(new QuadrupleImpl(Operator.ASSIGN_STRING, "#", "!", tacElement.getResult()));
				}
				break;
			case DECLARE_DOUBLE:
				file.addVariableToMethodsCode(methodName, tacElement.getResult(), "DOUBLE");
				if (!tacElement.getArgument1().equals("!")) {
					newTac.add(new QuadrupleImpl(Operator.ASSIGN_DOUBLE, tacElement.getArgument1(), "!", tacElement.getResult()));
				} else {
					newTac.add(new QuadrupleImpl(Operator.ASSIGN_DOUBLE, "#0.0", "!", tacElement.getResult()));
				}
				break;
			case DECLARE_LONG:
				file.addVariableToMethodsCode(methodName, tacElement.getResult(), "LONG");
				if (!tacElement.getArgument1().equals("!")) {
					newTac.add(new QuadrupleImpl(Operator.ASSIGN_LONG, tacElement.getArgument1(), "!", tacElement.getResult()));
				} else {
					newTac.add(new QuadrupleImpl(Operator.ASSIGN_LONG, "#0", "!", tacElement.getResult()));
				}
				break;
			case DECLARE_BOOL:
				file.addVariableToMethodsCode(methodName, tacElement.getResult(), "BOOL");
				if (!tacElement.getArgument1().equals("!")) {
					newTac.add(new QuadrupleImpl(Operator.ASSIGN_BOOL, tacElement.getArgument1(), "!", tacElement.getResult()));
				} else {
					newTac.add(new QuadrupleImpl(Operator.ASSIGN_BOOL, "#FALSE", "!", tacElement.getResult()));
				}
				break;
			default:
				newTac.add(tacElement);
			}
		}
		
		return newTac;
	}
}
