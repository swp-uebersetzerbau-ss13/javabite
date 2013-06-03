package swp_compiler_ss13.javabite.backend.translation;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import swp_compiler_ss13.common.backend.Quadruple;
import swp_compiler_ss13.common.backend.Quadruple.Operator;
import swp_compiler_ss13.javabite.backend.Program;
import swp_compiler_ss13.javabite.backend.Program.ProgramBuilder;
import swp_compiler_ss13.javabite.backend.classfile.Classfile;
import swp_compiler_ss13.javabite.backend.classfile.IClassfile;
import swp_compiler_ss13.javabite.backend.classfile.IClassfile.InfoTag;
import swp_compiler_ss13.javabite.backend.classfile.IClassfile.VariableType;
import swp_compiler_ss13.javabite.backend.external.QuadrupleImpl;

/**
 * <h1>Translator</h1>
 * <p>
 * This class and its methods are supposed to be used to translate the tac into
 * a list of classfiles containing the target bytecode.
 * </p>
 * 
 * @author Marco
 * @since 27.04.2013
 */
public class Translator {

	// necessary string constants
	public static final String SYM_CONST = "#";
	public static final String SYM_IGNORE = "!";

	public static final String CONST_FALSE = "#FALSE";
	public static final String CONST_TRUE = "#TRUE";

	public static final String DEF_LONG = "#0";
	public static final String DEF_DOUBLE = "#0.0";
	public static final String DEF_BOOLEAN = CONST_FALSE;
	public static final String DEF_STRING = "#\"\"";

	public static final String FILEENDING_CLASS = ".class";

	public static final String METHODNAME_MAIN = "main";
	public static final String METHODDESCRIPTOR_MAIN = "([Ljava/lang/String;)V";
	public static final String OBJECT_CLASSNAME_EIF = "java/lang/Object";

	/**
	 * <h1>generateNewFile</h1>
	 * <p>
	 * This method generates a new Classfile object using its parameter name and
	 * appends it to the translator's classfile list.
	 * </p>
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 * @param name
	 *            string describing the classfile's name
	 * @param thisClassNameEIF
	 *            string describing this class name of the class described in
	 *            this classfile encoded in internal form
	 * @param superClassNameEIF
	 *            string describing the superclass' class name of the class
	 *            described in this classfile encoded in internal form
	 * @return instance of a class implementing the IClassfile interface
	 */
	private IClassfile generateClassfile(final String name,
			final String thisClassNameEIF, final String superClassNameEIF,
			final Classfile.ClassfileAccessFlag... accessFlags) {
		final IClassfile file = new Classfile(name, thisClassNameEIF,
				superClassNameEIF, accessFlags);
		return file;
	}

	/**
	 * <h1>translate</h1>
	 * <p>
	 * This method translates the provided tac into target bytecode, which can
	 * be executed by a jvm. Therefore it generates appropriate classfile
	 * bytecode meeting the jvm classfile specification and translates the tac
	 * statements into byecde instructions meeting the jvm instructions
	 * specification.
	 * </p>
	 * 
	 * @author Marco
	 * @since 27.04.2013
	 * 
	 * @param tac
	 *            List of quadruple objects
	 * @param className
	 *            String filename of the (main)class, which always has to be
	 *            generated
	 * @return Collection<IClassfile> a collection of instances of the class
	 *         Classfile, which implements the interface IClassfile. Every
	 *         instance can be used to generate appropriate bytecode meeting the
	 *         jvm classfile specification.
	 */
	public Collection<IClassfile> translate(final String mainClassName,
			List<Quadruple> tac) {

		// some initialization
		String classFileName = mainClassName + FILEENDING_CLASS;
		final Collection<IClassfile> classfiles = new ArrayList<IClassfile>();
		String mainMethodName = METHODNAME_MAIN;

		// create a new (main)classfile/ classfile with main method
		final IClassfile classfile = this.generateClassfile(classFileName,
				mainClassName, OBJECT_CLASSNAME_EIF,
				Classfile.ClassfileAccessFlag.ACC_PUBLIC,
				Classfile.ClassfileAccessFlag.ACC_SUPER);

		// add main method to this (main)classfile
		classfile.addMethodToMethodArea(mainMethodName, METHODDESCRIPTOR_MAIN,
				Classfile.MethodAccessFlag.ACC_PUBLIC,
				Classfile.MethodAccessFlag.ACC_STATIC);

		// parse tac for struct declarations and create classfiles for them
		classfiles.addAll(generateClassfilesForStructsInTAC(tac));

		// translate tac/program into the main method's code
		if (tac != null) {
			tac = addVariablesToLocalVariableSpace(classfile, mainMethodName,
					tac);
			addConstantsToConstantPool(classfile, tac);
			extractInstructionsFromOperations(classfile, mainMethodName, tac);
		}

		// add final (main)classfile to the translator's classfile list
		classfiles.add(classfile);

		return classfiles;
	}

	/**
	 * TODO implementation and javadoc - search for structs in tac and build new
	 * classes use new translate method or change trnaslate method to
	 * translate(...,..., false)
	 */
	private Collection<IClassfile> generateClassfilesForStructsInTAC(
			final List<Quadruple> tac) {
		final Collection<IClassfile> classfiles = new ArrayList<>();
		return classfiles;
	}

	/**
	 * <h1>addTACConstantsToConstantPool</h1>
	 * <p>
	 * This method parses the tac and adds all constants to the provided
	 * classfile's constant pool.
	 * </p>
	 * 
	 * @author Robert, Marco
	 * @since 29.04.2013
	 * 
	 * @param classFile
	 * @param tac
	 *            List<Quadruple> three-address-code
	 */
	private static void addConstantsToConstantPool(final IClassfile classFile,
			final List<Quadruple> tac) {

		for (final Quadruple quad : tac) {

			// get the quadruple parts
			final Quadruple.Operator operator = quad.getOperator();
			final String arg1 = quad.getArgument1();
			final String arg2 = quad.getArgument2();
			final String result = quad.getResult();

			/*
			 * if the operator is an array_set-operator, it'll have to be
			 * examined separately
			 */
			if (operator.name().startsWith("ARRAY_SET")) {
				// argument 2 is a long constant
				if (arg2.startsWith(SYM_CONST)) {
					classFile.addLongConstantToConstantPool(Long.parseLong(arg2
							.substring(1)));
				}
				// result can be a cnstant of different types
				if (result.startsWith(SYM_CONST)) {
					if (operator == Operator.ARRAY_SET_LONG) {
						classFile.addLongConstantToConstantPool(Long
								.parseLong(result.substring(1)));
					} else if (operator == Operator.ARRAY_SET_DOUBLE) {
						classFile.addDoubleConstantToConstantPool(Double
								.parseDouble(result.substring(1)));
					} else if (operator == Operator.ARRAY_SET_STRING) {
						classFile.addStringConstantToConstantPool(result
								.substring(1));
					}
				}
				continue;
			}

			// get expected type of constants
			final InfoTag type = getExpectedInfoTagOfConstantsByOperator(operator);

			/*
			 * if type is equal to zero, there'll be no constants in this
			 * operation or the constants are not stored in this classfile's
			 * constant pool
			 */
			if (type == null)
				continue;

			/*
			 * check type and argument and create appropriate constant pool
			 * entry if necessary
			 */
			if (type == InfoTag.LONG && arg1.startsWith(SYM_CONST)) {
				assertNotNull(arg1);
				classFile.addLongConstantToConstantPool(Long.parseLong(arg1
						.substring(1)));
			}
			if (type == InfoTag.LONG && arg2.startsWith(SYM_CONST)) {
				assertNotNull(arg2);
				classFile.addLongConstantToConstantPool(Long.parseLong(arg2
						.substring(1)));
				continue;
			}
			if (type == InfoTag.DOUBLE && arg1.startsWith(SYM_CONST)) {
				assertNotNull(arg1);
				classFile.addDoubleConstantToConstantPool(Double
						.parseDouble(arg1.substring(1)));
			}
			if (type == InfoTag.DOUBLE && arg2.startsWith(SYM_CONST)) {
				assertNotNull(arg2);
				classFile.addDoubleConstantToConstantPool(Double
						.parseDouble(arg2.substring(1)));
				continue;
			}
			if (type == InfoTag.STRING && arg1.startsWith(SYM_CONST)) {
				assertNotNull(arg1);
				classFile.addStringConstantToConstantPool(arg1.substring(1));
			}
			if (type == InfoTag.STRING && arg2.startsWith(SYM_CONST)) {
				assertNotNull(arg2);
				classFile.addStringConstantToConstantPool(arg2.substring(1));
			}
		}
	}

	/**
	 * <h1>getExpectedInfoTagOfConstantsByOperator</h1>
	 * <p>
	 * This method maps operators to the respective data types of which the
	 * constants possibly being used in this operation are expected to be. For
	 * instance: ASSIGN_LONG maps to InfoTag.LONG, because it assigns a long and
	 * a possibly used constant is expected to be exactly of this type.
	 * </p>
	 * 
	 * TODO: Expand operator by expected constant type or "multiple"?
	 * 
	 * @author Eike
	 * @since 09.05.2013
	 * 
	 * @param operator
	 *            the operator to be examined
	 * @return InfoTag value which describes, of which type the operation
	 *         constants are expected to be
	 */
	private static InfoTag getExpectedInfoTagOfConstantsByOperator(
			final Operator operator) {
		switch (operator) {

		case ASSIGN_LONG:
		case ADD_LONG:
		case SUB_LONG:
		case MUL_LONG:
		case DIV_LONG:
		case DECLARE_ARRAY:
		case ARRAY_GET_LONG:
		case ARRAY_GET_DOUBLE:
		case ARRAY_GET_BOOLEAN:
		case ARRAY_GET_STRING:
		case ARRAY_GET_ARRAY:
		case ARRAY_GET_REFERENCE:
		case COMPARE_LONG_E:
		case COMPARE_LONG_G:
		case COMPARE_LONG_L:
		case COMPARE_LONG_GE:
		case COMPARE_LONG_LE:
		case PRINT_LONG:
		case RETURN:
			return InfoTag.LONG;

		case ASSIGN_DOUBLE:
		case ADD_DOUBLE:
		case SUB_DOUBLE:
		case MUL_DOUBLE:
		case DIV_DOUBLE:
		case COMPARE_DOUBLE_E:
		case COMPARE_DOUBLE_G:
		case COMPARE_DOUBLE_L:
		case COMPARE_DOUBLE_GE:
		case COMPARE_DOUBLE_LE:
		case PRINT_DOUBLE:
			return InfoTag.DOUBLE;

		case ASSIGN_STRING:
		case PRINT_STRING:
			return InfoTag.STRING;

		default:
			return null;
		}
	}

	/**
	 * <h1>addVariablesToLocalVariableSpace</h1>
	 * <p>
	 * This method "allocates" space in the local variable space of the provided
	 * classfile's provided method's code attribute for all variable
	 * declarations and will convert them into assignments, if they have an
	 * initial value and will delete them if not.
	 * </p>
	 * 
	 * @author Marco
	 * @since 29.04.2013
	 * @return List<Quadruple> the modified three-address-code
	 */
	private static List<Quadruple> addVariablesToLocalVariableSpace(
			final IClassfile file, final String methodName,
			final List<Quadruple> tac) {

		// is set, while array is declared
		boolean arrayFlag = false;
		for (final ListIterator<Quadruple> tacIter = tac.listIterator(); tacIter
				.hasNext();) {

			final Quadruple quad = tacIter.next();
			final Operator op;
			final String defValue;
			final String arg1 = quad.getArgument1();
			final String result = quad.getResult();
			final Quadruple q;
			final VariableType varType;

			/*
			 * While an array is declared, skip further checks until a basic
			 * type is declared/ the array declaration is finished.
			 */
			if (arrayFlag) {
				if (quad.getOperator().equals(Operator.DECLARE_STRING)
						|| quad.getOperator().equals(Operator.DECLARE_BOOLEAN)
						|| quad.getOperator().equals(Operator.DECLARE_LONG)
						|| quad.getOperator().equals(Operator.DECLARE_DOUBLE)) {
					arrayFlag = false;
					continue;
				}
				continue;
			}

			switch (quad.getOperator()) {
			case DECLARE_REFERENCE:
				file.addVariableToMethodsCode(methodName, result,
						VariableType.AREF);
				tacIter.remove();
				continue;
			case DECLARE_ARRAY:
				arrayFlag = true;
				file.addVariableToMethodsCode(methodName, result,
						VariableType.AREF);
				continue;
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

			// "allocate" local variable space
			file.addVariableToMethodsCode(methodName, result, varType);

			// modify current quadruple
			if (!SYM_IGNORE.equals(arg1)) {
				// set provided value
				q = new QuadrupleImpl(op, arg1, SYM_IGNORE, result);
			} else {
				// set default value
				q = new QuadrupleImpl(op, defValue, SYM_IGNORE, result);
			}

			// replace current quadruple by modified one
			tacIter.set(q);
		}

		return tac;
	}

	/**
	 * <h1>extractInstructionsFromOperations</h1>
	 * <p>
	 * This method generates instructions from the passed tac.
	 * </p>
	 * 
	 * TODO: EIKE - more detailed javadocs
	 * 
	 * @author Eike
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
		classfile
				.addInstructionsToMethodsCode("main", pr.toInstructionsArray());
	}

}
