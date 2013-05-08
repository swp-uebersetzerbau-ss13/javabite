package swp_compiler_ss13.javabite.backend.marco.proposal1;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import swp_compiler_ss13.javabite.backend.marco.proposal1.IClassfile.VariableTypes;
import swp_compiler_ss13.javabite.backend.marco.proposal1.IQuadruple.Operator;

/**
 * Translator class.
 * 
 * @author Marco
 * @since 27.04.2013
 * 
 */
public class Translator
{
	/**
	 * generateNewFile function. This function generates a new Classfile object
	 * using its parameter name and appends it to the translator's classfile
	 * list.
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 * @param name
	 *            string describing the classfile's name
	 * @param thisClassNameEIF
	 *            string describing this classname of the class described in
	 *            this classfile encoded in internal form
	 * @param superClassNameEIF
	 *            string describing the superclass' classname of the class
	 *            described in this classfile encoded in internal form
	 * 
	 * @return instance of Classfile
	 */
	private IClassfile generateClassile(String name, String thisClassNameEIF,
			String superClassNameEIF, Classfile.ClassfileAccessFlag... accessFlags) {
		IClassfile file = new Classfile(name, thisClassNameEIF,
				superClassNameEIF, accessFlags);
		return file;
	}

	/**
	 * translate function. This function translates the tac into classfiles.
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 * @param tac
	 *            List of quadruple objects
	 * 
	 * @return Collection<IClassfile>
	 */
	public Collection<IClassfile> translate(List<IQuadruple> tac) {
		// always start with main.class
		return this.translate(tac, "main.class");
	}
	
	private Collection<IClassfile> translate(List<IQuadruple> tac, String classfileName) {
		
		Collection<IClassfile> classfiles = new ArrayList<IClassfile>();
		
		IClassfile classfile = this.generateClassile(classfileName,
				"tests/example", "java/lang/Object", 
				Classfile.ClassfileAccessFlag.ACC_PUBLIC, Classfile.ClassfileAccessFlag.ACC_SUPER);

		String methodName;
		
		if(classfileName.equals("main.class")) {
			methodName = "main";
			// always generate mainFunction in main.class file
			classfile.addMethodToMethodArea(methodName, "([Ljava/lang/String;])V", 
					Classfile.MethodAccessFlag.ACC_PUBLIC, Classfile.MethodAccessFlag.ACC_STATIC);
		} else {
			// TODO: generate public dummy function in class - needed ?
			methodName = "dummy";
			classfile.addMethodToMethodArea(methodName, "()V", 
					Classfile.MethodAccessFlag.ACC_PUBLIC);
		}
		
		classfiles.addAll(generateClassfilesForStructsInTAC(tac));
				
		// MS 1 translate everything into main.class file
		if (tac != null) {
		
			//TODO: add refs to structs to variable space ...
			
			tac = this.addVariablesToLocalVariableSpace(classfile, methodName, tac);
			this.addTACConstantsToConstantPool(classfile, tac);
		}
		
		classfiles.add(classfile);		
		
		return classfiles;
	}

	private Collection<IClassfile> generateClassfilesForStructsInTAC(List<IQuadruple> tac) {
		Collection<IClassfile> classfiles = new ArrayList<>();
		// TODO: search for structs in tac and build new classes
		// use translate(...,...)
		return classfiles;
	}
	
	
	
	/**
	 * addTACConstantsToConstantPool function. This function parses the TAC and
	 * adds all constants to the files constantPool.
	 * 
	 * @author Robert, Marco
	 * @since 29.04.2013
	 * 
	 * @param classFile
	 * @param tac
	 */
	private void addTACConstantsToConstantPool(IClassfile classFile,
			Collection<IQuadruple> tac) {

		for (IQuadruple quad : tac) {

			IQuadruple.Operator operator = quad.getOperator();
			String arg1 = quad.getArgument1();
			String arg2 = quad.getArgument2();

			switch (operator) {
			case ASSIGN_LONG:
				if (arg1.startsWith("#")) {
					classFile.addConstantToConstantPool("LONG",
							arg1.substring(1));
				}
				break;
			case ASSIGN_DOUBLE:
				if (arg1.startsWith("#")) {
					classFile.addConstantToConstantPool("DOUBLE",
							arg1.substring(1));
				}
				break;
			case ASSIGN_STRING:
				if (arg1.startsWith("#")) {
					classFile.addConstantToConstantPool("STRING",
							arg1.substring(1));
				}
				break;
			case RETURN:
				if (arg1.startsWith("#")) {
					classFile.addConstantToConstantPool("LONG",
							arg1.substring(1));
				}
				break;
			default:
				break;
			}

			if (operator == IQuadruple.Operator.ADD_LONG
					|| operator == IQuadruple.Operator.SUB_LONG
					|| operator == IQuadruple.Operator.MUL_LONG
					|| operator == IQuadruple.Operator.DIV_LONG) {

				if (arg1.startsWith("#")) {
					classFile.addConstantToConstantPool("LONG",
							arg1.substring(1));
				}
				if (arg2.startsWith("#")) {
					classFile.addConstantToConstantPool("LONG",
							arg2.substring(1));
				}
			}

			if (operator == IQuadruple.Operator.ADD_DOUBLE
					|| operator == IQuadruple.Operator.SUB_DOUBLE
					|| operator == IQuadruple.Operator.MUL_DOUBLE
					|| operator == IQuadruple.Operator.DIV_DOUBLE) {

				if (arg1.startsWith("#")) {
					classFile.addConstantToConstantPool("DOUBLE",
							arg1.substring(1));
				}
				if (arg2.startsWith("#")) {
					classFile.addConstantToConstantPool("DOUBLE",
							arg2.substring(1));
				}
			}
		}
	}

	/**
	 * addVariablesToLocalVariableSpace function. This function allocates space
	 * for all variable declarations and will convert them into assignments, if
	 * they have an initial value;
	 * 
	 * @author Marco
	 * @since 29.04.2013
	 * 
	 */
	private List<IQuadruple> addVariablesToLocalVariableSpace(IClassfile file,
			String methodName, List<IQuadruple> tac) {
		List<IQuadruple> newTac = new ArrayList<IQuadruple>();

		for (IQuadruple quad : tac) {

			IQuadruple.Operator operator = quad.getOperator();
			String arg1 = quad.getArgument1();
			String result = quad.getResult();

			switch (operator) {
			case DECLARE_STRING:
				file.addVariableToMethodsCode(methodName, result, VariableTypes.STRING);
				if (!arg1.equals("!")) {
					newTac.add(new QuadrupleImpl(Operator.ASSIGN_STRING, arg1,
							"!", result));
				} else {
					newTac.add(new QuadrupleImpl(Operator.ASSIGN_STRING,
							"#\"\"", "!", result));
				}
				break;
			case DECLARE_DOUBLE:
				file.addVariableToMethodsCode(methodName, result, VariableTypes.DOUBLE);
				if (!arg1.equals("!")) {
					newTac.add(new QuadrupleImpl(Operator.ASSIGN_DOUBLE, arg1,
							"!", result));
				} else {
					newTac.add(new QuadrupleImpl(Operator.ASSIGN_DOUBLE,
							"#0.0", "!", result));
				}
				break;
			case DECLARE_LONG:
				file.addVariableToMethodsCode(methodName, result, VariableTypes.LONG);
				if (!arg1.equals("!")) {
					newTac.add(new QuadrupleImpl(Operator.ASSIGN_LONG, arg1,
							"!", result));
				} else {
					newTac.add(new QuadrupleImpl(Operator.ASSIGN_LONG, "#0",
							"!", result));
				}
				break;
			case DECLARE_BOOL:
				file.addVariableToMethodsCode(methodName, result, VariableTypes.BOOL);
				if (!arg1.equals("!")) {
					newTac.add(new QuadrupleImpl(Operator.ASSIGN_BOOL, arg1,
							"!", result));
				} else {
					newTac.add(new QuadrupleImpl(Operator.ASSIGN_BOOL,
							"#FALSE", "!", result));
				}
				break;
			default:
				newTac.add(quad);
			}
		}

		return newTac;
	}
}
