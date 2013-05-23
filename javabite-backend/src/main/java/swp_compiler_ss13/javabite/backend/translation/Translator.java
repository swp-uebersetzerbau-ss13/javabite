package swp_compiler_ss13.javabite.backend.translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.javabite.backend.Classfile;
import swp_compiler_ss13.javabite.backend.IClassfile;
import swp_compiler_ss13.javabite.backend.IClassfile.ConstantType;
import swp_compiler_ss13.javabite.backend.IClassfile.VariableType;
import swp_compiler_ss13.javabite.backend.Program;
import swp_compiler_ss13.javabite.backend.Program.ProgramBuilder;
import swp_compiler_ss13.javabite.backend.external.QuadrupleImpl;

/**
 * Translator class.
 * 
 * @author Marco
 * @since 27.04.2013
 * 
 */
public class Translator {

	public static final String SYM_CONST = "#";
	public static final String SYM_IGNORE = "!";

	public static final String CONST_FALSE = "#FALSE";
	public static final String CONST_TRUE = "#TRUE";

	public static final String DEF_LONG = "#0";
	public static final String DEF_DOUBLE = "#0.0";
	public static final String DEF_BOOLEAN = CONST_FALSE;
	public static final String DEF_STRING = "#\"\"";

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
	private IClassfile generateClassfile(final String name,
			final String thisClassNameEIF, final String superClassNameEIF,
			final Classfile.ClassfileAccessFlag... accessFlags) {
		final IClassfile file = new Classfile(name, thisClassNameEIF,
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
	 * @param className
	 *            Name of the class to be generated
	 * 
	 * @return Collection<IClassfile>
	 */
	public Collection<IClassfile> translate(final String className, List<Quadruple> tac) {
		return translate(className, tac, true);
	}
	
	private Collection<IClassfile> translate(final String className, List<Quadruple> tac, Boolean mainClass) {
		String classFileName = className + ".class";
		
		final Collection<IClassfile> classfiles = new ArrayList<IClassfile>();

		final IClassfile classfile = this.generateClassfile(classFileName,
				className, "java/lang/Object",
				Classfile.ClassfileAccessFlag.ACC_PUBLIC,
				Classfile.ClassfileAccessFlag.ACC_SUPER);

		String methodName;

		if (mainClass) {
			methodName = "main";
			// always generate mainFunction in main.class file
			classfile.addMethodToMethodArea(methodName,
					"([Ljava/lang/String;)V",
					Classfile.MethodAccessFlag.ACC_PUBLIC,
					Classfile.MethodAccessFlag.ACC_STATIC);
		} else {
			// TODO: generate public dummy function in class - needed ?
			methodName = "dummy";
			classfile.addMethodToMethodArea(methodName, "()V",
					Classfile.MethodAccessFlag.ACC_PUBLIC);
		}

		classfiles.addAll(generateClassfilesForStructsInTAC(tac));

		// MS 1 translate everything into main.class file
		if (tac != null) {
			// TODO: add refs to structs to variable space ...
			tac = addVariablesToLocalVariableSpace(classfile, methodName, tac);
			addConstantsToConstantPool(classfile, tac);
			extractInstructionsFromOperations(classfile, methodName, tac);
		}

		classfiles.add(classfile);

		return classfiles;
	}

	private Collection<IClassfile> generateClassfilesForStructsInTAC(
			final List<Quadruple> tac) {
		final Collection<IClassfile> classfiles = new ArrayList<>();
		// TODO: search for structs in tac and build new classes
		// use translate(...,..., false)
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
	private static void addConstantsToConstantPool(final IClassfile classFile,
			final List<Quadruple> tac) {

		for (final Quadruple quad : tac) {

			final Quadruple.Operator operator = quad.getOperator();
			final String arg1 = quad.getArgument1();
			final String arg2 = quad.getArgument2();

			if (operator == Quadruple.Operator.ADD_LONG
					|| operator == Quadruple.Operator.SUB_LONG
					|| operator == Quadruple.Operator.MUL_LONG
					|| operator == Quadruple.Operator.DIV_LONG) {

				if (arg1.startsWith(SYM_CONST)) {
					classFile.addConstantToConstantPool(ConstantType.LONG,
							arg1.substring(1));
				}
				if (arg2.startsWith(SYM_CONST)) {
					classFile.addConstantToConstantPool(ConstantType.LONG,
							arg2.substring(1));
				}
			}

			if (operator == Quadruple.Operator.ADD_DOUBLE
					|| operator == Quadruple.Operator.SUB_DOUBLE
					|| operator == Quadruple.Operator.MUL_DOUBLE
					|| operator == Quadruple.Operator.DIV_DOUBLE) {

				if (arg1.startsWith(SYM_CONST)) {
					classFile.addConstantToConstantPool(ConstantType.DOUBLE,
							arg1.substring(1));
				}
				if (arg2.startsWith(SYM_CONST)) {
					classFile.addConstantToConstantPool(ConstantType.DOUBLE,
							arg2.substring(1));
				}
			}

			final ConstantType type = getDataTypeOfOperator(operator);

			if (type != null && arg1.startsWith(SYM_CONST)) {
				classFile.addConstantToConstantPool(type, arg1.substring(1));
			}
		}
	}

	/**
	 * This method maps operators to their respective data types, eg.
	 * ASSIGN_LONG maps to ConstantType.LONG, because it assigns a long!
	 * 
	 * @author eike
	 * @since 09.05.2013
	 * 
	 * @param operator
	 * @return
	 */
	private static ConstantType getDataTypeOfOperator(final Operator operator) {
		switch (operator) {
		case ASSIGN_LONG:
			return ConstantType.LONG;
		case ASSIGN_DOUBLE:
			return ConstantType.DOUBLE;
		case ASSIGN_STRING:
			return ConstantType.STRING;
		default:
			return null;
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
	private static List<Quadruple> addVariablesToLocalVariableSpace(
			final IClassfile file, final String methodName,
			final List<Quadruple> tac) {

		for (final ListIterator<Quadruple> tacIter = tac.listIterator(); tacIter
				.hasNext();) {

			final Quadruple quad = tacIter.next();
			final Operator op;
			final String defValue;
			final String arg1 = quad.getArgument1();
			final String result = quad.getResult();
			final Quadruple q;
			final VariableType varType;

			switch (quad.getOperator()) {
			case DECLARE_STRING:
				op = Operator.ASSIGN_STRING;
				defValue = DEF_STRING;
				varType = VariableType.STRING;
				break;
			case DECLARE_LONG:
				op = Operator.ASSIGN_LONG;
				defValue = DEF_LONG;
				varType = VariableType.LONG;
				break;
			case DECLARE_DOUBLE:
				op = Operator.ASSIGN_DOUBLE;
				defValue = DEF_DOUBLE;
				varType = VariableType.DOUBLE;
				break;
			case DECLARE_BOOLEAN:
				op = Operator.ASSIGN_BOOLEAN;
				defValue = DEF_BOOLEAN;
				varType = VariableType.BOOLEAN;
				break;
			default:
				continue;
			}

			file.addVariableToMethodsCode(methodName, result, varType);

			if (!SYM_IGNORE.equals(arg1)) {
				q = new QuadrupleImpl(op, arg1, SYM_IGNORE, result);
			} else {
				q = new QuadrupleImpl(op, defValue, SYM_IGNORE, result);
			}

			tacIter.set(q);

		}

		return tac;
	}

	/**
	 * This method generates instructions from the passed tac.
	 * 
	 * @author eike
	 * @since 09.05.2013
	 * 
	 * @param classfile
	 * @param tac
	 */
	private static void extractInstructionsFromOperations(
			final IClassfile classfile, final String methodName,
			final List<Quadruple> tac) {
		final ProgramBuilder pb = ProgramBuilder.newBuilder(classfile,
				methodName);

		boolean stopTranslation = false;
		for (final Quadruple quad : tac) {

			if (stopTranslation)
				break;

			switch (quad.getOperator()) {
			case ADD_DOUBLE:
				pb.addDouble(quad);
				break;
			case ADD_LONG:
				pb.addLong(quad);
				break;
			case ASSIGN_BOOLEAN:
				pb.assignBoolean(quad);
				break;
			case ASSIGN_DOUBLE:
				pb.assignDouble(quad);
				break;
			case ASSIGN_LONG:
				pb.assignLong(quad);
				break;
			case ASSIGN_STRING:
				pb.assignString(quad);
				break;
			case DECLARE_BOOLEAN:
				pb.declareBoolean(quad);
				break;
			case DECLARE_DOUBLE:
				pb.declareDouble(quad);
				break;
			case DECLARE_LONG:
				pb.declareLong(quad);
				break;
			case DECLARE_STRING:
				pb.declareString(quad);
				break;
			case DIV_DOUBLE:
				pb.divDouble(quad);
				break;
			case DIV_LONG:
				pb.divLong(quad);
				break;
			case DOUBLE_TO_LONG:
				pb.doubleToLong(quad);
				break;
			case LONG_TO_DOUBLE:
				pb.longToDouble(quad);
				break;
			case MUL_DOUBLE:
				pb.mulDouble(quad);
				break;
			case MUL_LONG:
				pb.mulLong(quad);
				break;
			case RETURN:
				pb.returnLong(quad);
				stopTranslation = true;
				break;
			case SUB_DOUBLE:
				pb.subDouble(quad);
				break;
			case SUB_LONG:
				pb.subLong(quad);
				break;
			default:
				break;

			}
		}

		final Program pr = pb.build();
		classfile.addInstructionsToMethodsCode("main", pr.toInstructionsList());
	}

}
